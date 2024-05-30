package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import service.TaskManager;
import model.Task;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class TaskHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        switch (method) {
            case "GET":
                if (path.equals("/tasks")) {
                    handleGetTasks(exchange);
                } else if (path.matches("/tasks/\\d+")) {
                    handleGetTaskById(exchange);
                } else {
                    sendResponse(exchange, 404, "Not Found");
                }
                break;
            case "POST":
                if (path.equals("/tasks")) {
                    handleCreateOrUpdateTask(exchange);
                } else {
                    sendResponse(exchange, 404, "Not Found");
                }
                break;
            case "DELETE":
                if (path.matches("/tasks/\\d+")) {
                    handleDeleteTask(exchange);
                } else {
                    sendResponse(exchange, 404, "Not Found");
                }
                break;
            default:
                sendResponse(exchange, 405, "Method Not Allowed");
                break;
        }
    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        List<Task> tasks = taskManager.getAllTasks();
        String response = tasks.toString();
        sendResponse(exchange, 200, response);
    }

    private void handleGetTaskById(HttpExchange exchange) throws IOException {
        int id = Integer.parseInt(exchange.getRequestURI().getPath().split("/")[2]);
        Task task = taskManager.getTaskById(id);
        if (task != null) {
            sendResponse(exchange, 200, task.toString());
        } else {
            sendResponse(exchange, 404, "Not Found");
        }
    }

    private void handleCreateOrUpdateTask(HttpExchange exchange) throws IOException {
        Task task = new Task("Task 1", "Description 1");
        Task createdTask = taskManager.createTask(task);
        sendResponse(exchange, 201, createdTask.toString());
    }

    private void handleDeleteTask(HttpExchange exchange) throws IOException {
        int id = Integer.parseInt(exchange.getRequestURI().getPath().split("/")[2]);
        taskManager.deleteTask(id);
        sendResponse(exchange, 200, "Task deleted successfully");
    }

    public void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
