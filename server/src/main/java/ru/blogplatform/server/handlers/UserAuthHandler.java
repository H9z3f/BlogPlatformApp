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
import ru.blogplatform.server.entities.User;
import ru.blogplatform.server.responses.UserResponseBody;
import ru.blogplatform.server.utilities.JWTUtility;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

public class UserAuthHandler implements HttpHandler {
    private SessionFactory sessionFactory;
    private ObjectMapper objectMapper;
    private PasswordEncoder passwordEncoder;

    public UserAuthHandler() {
        Configuration configuration = new Configuration().configure();
        sessionFactory = configuration.buildSessionFactory();
        objectMapper = new ObjectMapper();
        passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        if (!httpExchange.getRequestMethod().equalsIgnoreCase("POST")) {
            sendResponse(httpExchange, HttpURLConnection.HTTP_BAD_METHOD, new UserResponseBody(false, "Invalid request method"));
            return;
        }

        Map<String, Object> data = receiveData(httpExchange);
        String email = (String) data.get("email");
        String password = (String) data.get("password");

        Session session = sessionFactory.openSession();
        session.beginTransaction();

        try {
            if (email.equals("") || password.equals("")) {
                throw new Exception();
            }

            User user = session.createQuery("FROM User WHERE email = :email", User.class)
                    .setParameter("email", email)
                    .uniqueResult();

            if (user == null) {
                sendResponse(httpExchange, HttpURLConnection.HTTP_NOT_FOUND, new UserResponseBody(false, "User with this email does not exist"));
                return;
            }

            if (!passwordEncoder.matches(password, user.getPassword())) {
                sendResponse(httpExchange, HttpURLConnection.HTTP_CONFLICT, new UserResponseBody(false, "Invalid password"));
                return;
            }

            List<Blog> yourBlogs = session.createQuery("FROM Blog WHERE user = :user", Blog.class)
                    .setParameter("user", user)
                    .list();
            List<Blog> blogs = session.createQuery("FROM Blog WHERE user != :user", Blog.class)
                    .setParameter("user", user)
                    .list();

            String JWT = JWTUtility.generateJWT(user.getId());

            sendResponse(httpExchange, HttpURLConnection.HTTP_OK, new UserResponseBody(true, user, JWT, yourBlogs, blogs));
        } catch (Exception e) {
            session.getTransaction().rollback();
            sendResponse(httpExchange, HttpURLConnection.HTTP_CONFLICT, new UserResponseBody(false, "Fields cannot be empty"));
        } finally {
            session.close();
        }
    }

    private Map<String, Object> receiveData(HttpExchange httpExchange) throws IOException {
        InputStream inputStream = httpExchange.getRequestBody();
        Map<String, Object> data = objectMapper.readValue(inputStream, new TypeReference<Map<String, Object>>() {
        });
        inputStream.close();

        return data;
    }

    private void sendResponse(HttpExchange httpExchange, int code, UserResponseBody userResponseBody) throws IOException {
        httpExchange.sendResponseHeaders(code, 0);
        OutputStream outputStream = httpExchange.getResponseBody();
        objectMapper.writeValue(outputStream, userResponseBody);
        outputStream.close();
    }
}
