package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;

public class HttpTaskServer {

    private final int PORT;
    public static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();
    private HttpServer server;

    public HttpTaskServer(TaskManager taskManager) {
        this(taskManager, 8080);
    }

    public HttpTaskServer(TaskManager manager, int port) {
        this.PORT = port;
        try {
            this.server = HttpServer.create(new InetSocketAddress(PORT), 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert server != null;
        server.createContext("/tasks", new TaskHandler(manager));
        server.createContext("/subtasks", new SubTaskHandler(manager));
        server.createContext("/epics", new EpicHandler(manager));
        server.createContext("/history", new HistoryHandler(Managers.getDefaultHistoryManager()));
        server.createContext("/prioritized", new PrioritizedHandler(manager));
        server.setExecutor(null);
    }

    public static void main(String[] args) {
        TaskManager manager = Managers.getInMemoryTaskManager();
        HttpTaskServer httpTaskServer = new HttpTaskServer(manager);
        httpTaskServer.start();
    }

    public void start() {
        server.start();
        System.out.println("Server started on port: " + PORT);
    }

    public void stop() {
        this.server.stop(0);
        System.out.println("Server stopped");
    }
}
