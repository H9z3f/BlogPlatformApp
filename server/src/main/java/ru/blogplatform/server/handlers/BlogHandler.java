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
import ru.blogplatform.server.entities.User;
import ru.blogplatform.server.responses.BlogResponseBody;
import ru.blogplatform.server.utilities.JWTUtility;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.List;
import java.util.Map;

public class BlogHandler implements HttpHandler {
    private SessionFactory sessionFactory;
    private ObjectMapper objectMapper;
    private PasswordEncoder passwordEncoder;

    public BlogHandler() {
        Configuration configuration = new Configuration().configure();
        sessionFactory = configuration.buildSessionFactory();
        objectMapper = new ObjectMapper();
        passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String JWT = extractJWT(httpExchange);
        if (JWT == null) {
            sendResponse(httpExchange, HttpURLConnection.HTTP_UNAUTHORIZED, new BlogResponseBody(false, "Access key not found"));
            return;
        }

        Long ID = JWTUtility.parseJWT(JWT);
        if (ID == null) {
            sendResponse(httpExchange, HttpURLConnection.HTTP_UNAUTHORIZED, new BlogResponseBody(false, "Invalid access key"));
            return;
        }

        switch (httpExchange.getRequestMethod()) {
            case "GET" -> {
                getBlog(httpExchange);
            }
            case "POST" -> {
                createBlog(httpExchange, ID);
            }
            case "PUT" -> {
                updateBlog(httpExchange, ID);
            }
            case "DELETE" -> {
                deleteBlog(httpExchange, ID);
            }
            default -> {
                sendResponse(httpExchange, HttpURLConnection.HTTP_BAD_METHOD, new BlogResponseBody(false, "Invalid request method"));
            }
        }
    }

    private void getBlog(HttpExchange httpExchange) throws IOException {
        URI uri = httpExchange.getRequestURI();
        String uriPath = uri.getPath();
        String[] parts = uriPath.split("/");
        if (parts.length != 3 || !parts[parts.length - 1].matches("[+-]?\\d*(\\.\\d+)?([eE][+-]?\\d+)?")) {
            sendResponse(httpExchange, HttpURLConnection.HTTP_BAD_REQUEST, new BlogResponseBody(false, "Invalid request"));
            return;
        }

        long blogID = Long.parseLong(parts[parts.length - 1]);

        Session session = sessionFactory.openSession();
        session.beginTransaction();

        try {
            Blog blog = session.get(Blog.class, blogID);

            if (blog == null) {
                sendResponse(httpExchange, HttpURLConnection.HTTP_BAD_REQUEST, new BlogResponseBody(false, "Blog does not exist"));
                return;
            }

            List<Post> posts = session.createQuery("FROM Post WHERE blog = :blog", Post.class)
                    .setParameter("blog", blog)
                    .list();

            sendResponse(httpExchange, HttpURLConnection.HTTP_CREATED, new BlogResponseBody(true, blog, posts));
        } catch (Exception e) {
            session.getTransaction().rollback();
            sendResponse(httpExchange, HttpURLConnection.HTTP_CONFLICT, new BlogResponseBody(false, "Something went wrong"));
        } finally {
            session.close();
        }
    }

    private void createBlog(HttpExchange httpExchange, long userID) throws IOException {
        Map<String, Object> data = receiveData(httpExchange);
        String blogName = (String) data.get("blog_name");
        String blogDescription = (String) data.get("blog_description");
        String blogTheme = (String) data.get("blog_theme");

        Session session = sessionFactory.openSession();
        session.beginTransaction();

        try {
            if (blogName.equals("") || blogDescription.equals("") || blogTheme.equals("")) {
                throw new Exception();
            }

            User user = session.get(User.class, userID);

            Blog blog = new Blog();
            blog.setUser(user);
            blog.setBlogName(blogName);
            blog.setBlogDescription(blogDescription);
            blog.setBlogTheme(blogTheme);

            session.save(blog);
            session.getTransaction().commit();

            sendResponse(httpExchange, HttpURLConnection.HTTP_CREATED, new BlogResponseBody(true, blog, null));
        } catch (Exception e) {
            session.getTransaction().rollback();
            sendResponse(httpExchange, HttpURLConnection.HTTP_CONFLICT, new BlogResponseBody(false, "Fields cannot be empty"));
        } finally {
            session.close();
        }
    }

    private void updateBlog(HttpExchange httpExchange, long userID) throws IOException {
        URI uri = httpExchange.getRequestURI();
        String uriPath = uri.getPath();
        String[] parts = uriPath.split("/");
        if (parts.length != 3 || !parts[parts.length - 1].matches("[+-]?\\d*(\\.\\d+)?([eE][+-]?\\d+)?")) {
            sendResponse(httpExchange, HttpURLConnection.HTTP_BAD_REQUEST, new BlogResponseBody(false, "Invalid request"));
            return;
        }

        long blogID = Long.parseLong(parts[parts.length - 1]);

        Map<String, Object> data = receiveData(httpExchange);
        String blogName = (String) data.get("blog_name");
        String blogDescription = (String) data.get("blog_description");
        String blogTheme = (String) data.get("blog_theme");

        Session session = sessionFactory.openSession();
        session.beginTransaction();

        try {
            if (blogName.equals("") || blogDescription.equals("") || blogTheme.equals("")) {
                throw new Exception();
            }

            Blog blog = session.get(Blog.class, blogID);

            if (blog == null) {
                sendResponse(httpExchange, HttpURLConnection.HTTP_BAD_REQUEST, new BlogResponseBody(false, "Blog does not exist"));
                return;
            }

            if (blog.getUser().getId() != userID) {
                sendResponse(httpExchange, HttpURLConnection.HTTP_BAD_REQUEST, new BlogResponseBody(false, "No owner rights"));
                return;
            }

            blog.setBlogName(blogName);
            blog.setBlogDescription(blogDescription);
            blog.setBlogTheme(blogTheme);

            session.update(blog);
            session.getTransaction().commit();

            sendResponse(httpExchange, HttpURLConnection.HTTP_OK, new BlogResponseBody(true, "Blog successfully updated"));
        } catch (Exception e) {
            session.getTransaction().rollback();
            sendResponse(httpExchange, HttpURLConnection.HTTP_CONFLICT, new BlogResponseBody(false, "Fields cannot be empty"));
        } finally {
            session.close();
        }
    }

    private void deleteBlog(HttpExchange httpExchange, long userID) throws IOException {
        URI uri = httpExchange.getRequestURI();
        String uriPath = uri.getPath();
        String[] parts = uriPath.split("/");
        if (parts.length != 3 || !parts[parts.length - 1].matches("[+-]?\\d*(\\.\\d+)?([eE][+-]?\\d+)?")) {
            sendResponse(httpExchange, HttpURLConnection.HTTP_BAD_REQUEST, new BlogResponseBody(false, "Invalid request"));
            return;
        }

        long blogID = Long.parseLong(parts[parts.length - 1]);

        Session session = sessionFactory.openSession();
        session.beginTransaction();

        try {
            Blog blog = session.get(Blog.class, blogID);

            if (blog == null) {
                sendResponse(httpExchange, HttpURLConnection.HTTP_BAD_REQUEST, new BlogResponseBody(false, "Blog does not exist"));
                return;
            }

            if (blog.getUser().getId() != userID) {
                sendResponse(httpExchange, HttpURLConnection.HTTP_BAD_REQUEST, new BlogResponseBody(false, "No owner rights"));
                return;
            }

            session.delete(blog);
            session.getTransaction().commit();

            sendResponse(httpExchange, HttpURLConnection.HTTP_OK, new BlogResponseBody(true, "Blog successfully deleted"));
        } catch (Exception e) {
            session.getTransaction().rollback();
            sendResponse(httpExchange, HttpURLConnection.HTTP_CONFLICT, new BlogResponseBody(false, "Something went wrong"));
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

    private void sendResponse(HttpExchange httpExchange, int code, BlogResponseBody blogResponseBody) throws IOException {
        httpExchange.sendResponseHeaders(code, 0);
        OutputStream outputStream = httpExchange.getResponseBody();
        objectMapper.writeValue(outputStream, blogResponseBody);
        outputStream.close();
    }
}
