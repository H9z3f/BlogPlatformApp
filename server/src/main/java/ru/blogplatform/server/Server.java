package ru.blogplatform.server;

import com.sun.net.httpserver.HttpServer;
import ru.blogplatform.server.handlers.*;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Server {
    private final int PORT = 8080;
    private final int THREADS = 0;
    private HttpServer httpServer;

    public Server() {
        try {
            httpServer = HttpServer.create(new InetSocketAddress(PORT), THREADS);
            httpServer.createContext("/user/register", new UserRegisterHandler());
            httpServer.createContext("/user/auth", new UserAuthHandler());
            httpServer.createContext("/blog", new BlogHandler());
            httpServer.createContext("/post", new PostHandler());
            httpServer.createContext("/rating", new RatingHandler());
            httpServer.setExecutor(null);
            httpServer.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
