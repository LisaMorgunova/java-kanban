package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import model.Task;
import service.TaskManager;

import java.io.IOException;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public PrioritizedHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            getPrioritizedTasks(exchange);
        } else {
            sendNotFound(exchange);
        }
    }

    private void getPrioritizedTasks(HttpExchange exchange) throws IOException {
        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        String jsonResponse = convertTasksToJson(prioritizedTasks);
        sendResponse(exchange, 200, jsonResponse);
    }

    private String convertTasksToJson(List<Task> tasks) {
        Gson gson = new Gson();
        return gson.toJson(tasks);
    }
}


