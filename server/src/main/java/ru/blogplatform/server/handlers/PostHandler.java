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
import ru.blogplatform.server.entities.Blog;
import ru.blogplatform.server.entities.Post;
import ru.blogplatform.server.responses.PostResponseBody;
import ru.blogplatform.server.utilities.JWTUtility;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.ArrayList;
import java.util.Map;

public class PostHandler implements HttpHandler {
    private SessionFactory sessionFactory;
    private ObjectMapper objectMapper;
    private PasswordEncoder passwordEncoder;

    public PostHandler() {
        Configuration configuration = new Configuration().configure();
        sessionFactory = configuration.buildSessionFactory();
        objectMapper = new ObjectMapper();
        passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String JWT = extractJWT(httpExchange);
        if (JWT == null) {
            sendResponse(httpExchange, HttpURLConnection.HTTP_UNAUTHORIZED, new PostResponseBody(false, "Access key not found"));
            return;
        }

        Long ID = JWTUtility.parseJWT(JWT);
        if (ID == null) {
            sendResponse(httpExchange, HttpURLConnection.HTTP_UNAUTHORIZED, new PostResponseBody(false, "Invalid access key"));
            return;
        }

        switch (httpExchange.getRequestMethod()) {
            case "POST" -> {
                createPost(httpExchange);
            }
            case "PUT" -> {
                updatePost(httpExchange, ID);
            }
            case "DELETE" -> {
                deletePost(httpExchange, ID);
            }
            default -> {
                sendResponse(httpExchange, HttpURLConnection.HTTP_BAD_METHOD, new PostResponseBody(false, "Invalid request method"));
            }
        }
    }

    private void createPost(HttpExchange httpExchange) throws IOException {
        Map<String, Object> data = receiveData(httpExchange);
        String blogID = (String) data.get("blog_id");
        String postTitle = (String) data.get("post_title");
        String postText = (String) data.get("post_text");

        Session session = sessionFactory.openSession();
        session.beginTransaction();

        try {
            if (blogID.equals("") || postTitle.equals("") || postText.equals("")) {
                throw new Exception();
            }

            Blog blog = session.get(Blog.class, Long.parseLong(blogID));

            if (blog == null) {
                sendResponse(httpExchange, HttpURLConnection.HTTP_BAD_REQUEST, new PostResponseBody(false, "Blog does not exist"));
                return;
            }

            Post post = new Post();
            post.setBlog(blog);
            post.setPostTitle(postTitle);
            post.setPostText(postText);

            session.save(post);
            session.getTransaction().commit();

            post.setPostRatings(new ArrayList<>());

            sendResponse(httpExchange, HttpURLConnection.HTTP_CREATED, new PostResponseBody(true, post));
        } catch (Exception e) {
            session.getTransaction().rollback();
            sendResponse(httpExchange, HttpURLConnection.HTTP_CONFLICT, new PostResponseBody(false, "Fields cannot be empty"));
        } finally {
            session.close();
        }
    }

    private void updatePost(HttpExchange httpExchange, long userID) throws IOException {
        URI uri = httpExchange.getRequestURI();
        String uriPath = uri.getPath();
        String[] parts = uriPath.split("/");
        if (parts.length != 3 || !parts[parts.length - 1].matches("[+-]?\\d*(\\.\\d+)?([eE][+-]?\\d+)?")) {
            sendResponse(httpExchange, HttpURLConnection.HTTP_BAD_REQUEST, new PostResponseBody(false, "Invalid request"));
            return;
        }

        long postID = Long.parseLong(parts[parts.length - 1]);

        Map<String, Object> data = receiveData(httpExchange);
        String postTitle = (String) data.get("post_title");
        String postText = (String) data.get("post_text");

        Session session = sessionFactory.openSession();
        session.beginTransaction();

        try {
            if (postTitle.equals("") || postText.equals("")) {
                throw new Exception();
            }

            Post post = session.get(Post.class, postID);

            if (post == null) {
                sendResponse(httpExchange, HttpURLConnection.HTTP_BAD_REQUEST, new PostResponseBody(false, "Post does not exist"));
                return;
            }

            if (post.getBlog().getUser().getId() != userID) {
                sendResponse(httpExchange, HttpURLConnection.HTTP_BAD_REQUEST, new PostResponseBody(false, "No owner rights"));
                return;
            }

            post.setPostTitle(postTitle);
            post.setPostText(postText);

            session.update(post);
            session.getTransaction().commit();

            sendResponse(httpExchange, HttpURLConnection.HTTP_OK, new PostResponseBody(true, "Post successfully updated"));
        } catch (Exception e) {
            session.getTransaction().rollback();
            sendResponse(httpExchange, HttpURLConnection.HTTP_CONFLICT, new PostResponseBody(false, "Fields cannot be empty"));
        } finally {
            session.close();
        }
    }

    private void deletePost(HttpExchange httpExchange, long userID) throws IOException {
        URI uri = httpExchange.getRequestURI();
        String uriPath = uri.getPath();
        String[] parts = uriPath.split("/");
        if (parts.length != 3 || !parts[parts.length - 1].matches("[+-]?\\d*(\\.\\d+)?([eE][+-]?\\d+)?")) {
            sendResponse(httpExchange, HttpURLConnection.HTTP_BAD_REQUEST, new PostResponseBody(false, "Invalid request"));
            return;
        }

        long postID = Long.parseLong(parts[parts.length - 1]);

        Session session = sessionFactory.openSession();
        session.beginTransaction();

        try {
            Post post = session.get(Post.class, postID);

            if (post == null) {
                sendResponse(httpExchange, HttpURLConnection.HTTP_BAD_REQUEST, new PostResponseBody(false, "Post does not exist"));
                return;
            }

            if (post.getBlog().getUser().getId() != userID) {
                sendResponse(httpExchange, HttpURLConnection.HTTP_BAD_REQUEST, new PostResponseBody(false, "No owner rights"));
                return;
            }

            session.delete(post);
            session.getTransaction().commit();

            sendResponse(httpExchange, HttpURLConnection.HTTP_OK, new PostResponseBody(true, "Post successfully deleted"));
        } catch (Exception e) {
            session.getTransaction().rollback();
            sendResponse(httpExchange, HttpURLConnection.HTTP_CONFLICT, new PostResponseBody(false, "Something went wrong"));
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

    private void sendResponse(HttpExchange httpExchange, int code, PostResponseBody postResponseBody) throws IOException {
        httpExchange.sendResponseHeaders(code, 0);
        OutputStream outputStream = httpExchange.getResponseBody();
        objectMapper.writeValue(outputStream, postResponseBody);
        outputStream.close();
    }
}
