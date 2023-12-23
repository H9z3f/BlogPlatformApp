package ru.blogplatform.server.handlers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.blogplatform.server.entities.Post;
import ru.blogplatform.server.entities.PostRating;
import ru.blogplatform.server.entities.User;
import ru.blogplatform.server.responses.RatingResponseBody;
import ru.blogplatform.server.utilities.JWTUtility;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Map;

public class RatingHandler implements HttpHandler {
    private SessionFactory sessionFactory;
    private ObjectMapper objectMapper;
    private PasswordEncoder passwordEncoder;

    public RatingHandler() {
        Configuration configuration = new Configuration().configure();
        sessionFactory = configuration.buildSessionFactory();
        objectMapper = new ObjectMapper();
        passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String JWT = extractJWT(httpExchange);
        if (JWT == null) {
            sendResponse(httpExchange, HttpURLConnection.HTTP_UNAUTHORIZED, new RatingResponseBody(false, "Access key not found"));
            return;
        }

        Long ID = JWTUtility.parseJWT(JWT);
        if (ID == null) {
            sendResponse(httpExchange, HttpURLConnection.HTTP_UNAUTHORIZED, new RatingResponseBody(false, "Invalid access key"));
            return;
        }

        switch (httpExchange.getRequestMethod()) {
            case "POST" -> {
                createBlog(httpExchange, ID);
            }
            case "DELETE" -> {
                deleteBlog(httpExchange, ID);
            }
            default -> {
                sendResponse(httpExchange, HttpURLConnection.HTTP_BAD_METHOD, new RatingResponseBody(false, "Invalid request method"));
            }
        }
    }

    private void createBlog(HttpExchange httpExchange, long userID) throws IOException {
        Map<String, Object> data = receiveData(httpExchange);
        String postID = (String) data.get("post_id");

        Session session = sessionFactory.openSession();
        session.beginTransaction();

        try {
            if (postID.equals("")) {
                throw new Exception();
            }

            User user = session.get(User.class, userID);
            Post post = session.get(Post.class, Long.parseLong(postID));

            PostRating postRating = new PostRating();
            postRating.setUser(user);
            postRating.setPost(post);

            session.save(postRating);
            session.getTransaction().commit();

            sendResponse(httpExchange, HttpURLConnection.HTTP_CREATED, new RatingResponseBody(true, postRating));
        } catch (Exception e) {
            session.getTransaction().rollback();
            sendResponse(httpExchange, HttpURLConnection.HTTP_CONFLICT, new RatingResponseBody(false, "Fields cannot be empty"));
        } finally {
            session.close();
        }
    }

    private void deleteBlog(HttpExchange httpExchange, long userID) throws IOException {
        URI uri = httpExchange.getRequestURI();
        String uriPath = uri.getPath();
        String[] parts = uriPath.split("/");
        if (parts.length != 3 || !parts[parts.length - 1].matches("[+-]?\\d*(\\.\\d+)?([eE][+-]?\\d+)?")) {
            sendResponse(httpExchange, HttpURLConnection.HTTP_BAD_REQUEST, new RatingResponseBody(false, "Invalid request"));
            return;
        }

        long ratingID = Long.parseLong(parts[parts.length - 1]);

        Session session = sessionFactory.openSession();
        session.beginTransaction();

        try {
            PostRating postRating = session.get(PostRating.class, ratingID);

            if (postRating == null) {
                sendResponse(httpExchange, HttpURLConnection.HTTP_BAD_REQUEST, new RatingResponseBody(false, "Rating does not exist"));
                return;
            }

            if (postRating.getUser().getId() != userID) {
                sendResponse(httpExchange, HttpURLConnection.HTTP_BAD_REQUEST, new RatingResponseBody(false, "No owner rights"));
                return;
            }

            session.delete(postRating);
            session.getTransaction().commit();

            sendResponse(httpExchange, HttpURLConnection.HTTP_OK, new RatingResponseBody(true, "Rating successfully deleted"));
        } catch (Exception e) {
            session.getTransaction().rollback();
            sendResponse(httpExchange, HttpURLConnection.HTTP_CONFLICT, new RatingResponseBody(false, "Something went wrong"));
        } finally {
            session.close();
        }
    }

    private String extractJWT(HttpExchange httpExchange) {
        try {
            String header = httpExchange.getRequestHeaders().get("Authorization").get(0);
            if (header != null && header.startsWith("Bearer ")) {
                return header.substring(7);
            }
        } catch (Exception e) {
            return null;
        }

        return null;
    }

    private Map<String, Object> receiveData(HttpExchange httpExchange) throws IOException {
        InputStream inputStream = httpExchange.getRequestBody();
        Map<String, Object> data = objectMapper.readValue(inputStream, new TypeReference<Map<String, Object>>() {
        });
        inputStream.close();

        return data;
    }

    private void sendResponse(HttpExchange httpExchange, int code, RatingResponseBody ratingResponseBody) throws IOException {
        httpExchange.sendResponseHeaders(code, 0);
        OutputStream outputStream = httpExchange.getResponseBody();
        objectMapper.writeValue(outputStream, ratingResponseBody);
        outputStream.close();
    }
}
