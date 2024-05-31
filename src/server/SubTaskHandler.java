package server;

import com.sun.net.httpserver.HttpExchange;
import model.SubTask;
import service.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static server.HttpTaskServer.gson;

public class SubTaskHandler extends BaseHttpHandler {

    private final TaskManager taskManager;

    public SubTaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            try {
                String path = exchange.getRequestURI().getPath();
                String[] pathParts = path.split("/");
                if (pathParts.length == 2) {
                    // Получаем подзадачи
                    List<SubTask> subTasks = taskManager.getAllSubTasks();
                    String jsonResponse = gson.toJson(subTasks);
                    sendText(exchange, 200, jsonResponse);
                } else if (pathParts.length == 3) {
                    // Получаем подзадачу по id
                    int id = Integer.parseInt(pathParts[2]);
                    SubTask subTask = taskManager.getSubTaskById(id);
                    if (subTask != null) {
                        String jsonResponse = gson.toJson(subTask);
                        sendText(exchange, 200, jsonResponse);
                    } else {
                        sendText(exchange, 404, "SubTask not found");
                    }
                } else {
                    sendText(exchange, 400, "Invalid URL");
                }
            } catch (NumberFormatException e) {
                sendText(exchange, 400, "Invalid ID");
            }
        } else if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            try {
                String body = getRequestBody(exchange);
                SubTask subTask = gson.fromJson(body, SubTask.class);
                if (subTask.getId() != 0) {
                    // Если id указан, обновляем существующую подзадачу
                    taskManager.updateSubTask(subTask);
                    sendText(exchange, 201, "SubTask updated successfully");
                } else {
                    // Если id не указан, создаем новую подзадачу
                    SubTask createdSubTask = taskManager.createSubTask(subTask);
                    String jsonResponse = gson.toJson(createdSubTask);
                    sendText(exchange, 201, jsonResponse);
                }
            } catch (IOException e) {
                sendText(exchange, 500, "Error reading request body");
            } catch (RuntimeException e) {
                sendText(exchange, 406, "Error creating/updating SubTask: " + e.getMessage());
            }
        } else if (exchange.getRequestMethod().equalsIgnoreCase("DELETE")) {
            try {
                String path = exchange.getRequestURI().getPath();
                String[] pathParts = path.split("/");
                if (pathParts.length == 3) {
                    // Удаляем подзадачу по id
                    int id = Integer.parseInt(pathParts[2]);
                    taskManager.deleteSubTask(id);
                    sendText(exchange, 200, "SubTask deleted successfully");
                } else {
                    sendText(exchange, 400, "Invalid URL");
                }
            } catch (NumberFormatException e) {
                sendText(exchange, 400, "Invalid ID");
            }
        } else {
            sendText(exchange, 405, "Method not allowed");
        }
    }

    private String getRequestBody(HttpExchange exchange) {
        try {
            return new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8).replaceAll("\r\n", "\n");
        } catch (IOException e) {
            return "";
        }
    }
}
