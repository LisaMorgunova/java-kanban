package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import service.TaskManager;
import model.Epic;
import model.SubTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EpicHandler extends BaseHttpHandler {

    private final TaskManager taskManager;
    private final Gson gson = new Gson();
    private final Pattern idPattern = Pattern.compile("/epics/(\\d+)");
    private final Pattern subtaskPattern = Pattern.compile("/epics/(\\d+)/subtasks");

    public EpicHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        if (method.equals("GET")) {
            handleGetRequest(exchange);
        } else if (method.equals("POST")) {
            handlePostRequest(exchange);
        } else if (method.equals("DELETE")) {
            handleDeleteRequest(exchange);
        } else {
            sendNotFound(exchange);
        }
    }

    private void handleGetRequest(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        Matcher idMatcher = idPattern.matcher(path);
        if (path.equals("/epics")) {
            sendEpics(exchange);
        } else if (idMatcher.matches()) {
            int id = Integer.parseInt(idMatcher.group(1));
            sendEpicById(exchange, id);
        } else {
            Matcher subtaskMatcher = subtaskPattern.matcher(path);
            if (subtaskMatcher.matches()) {
                int epicId = Integer.parseInt(subtaskMatcher.group(1));
                sendEpicSubtasks(exchange, epicId);
            } else {
                sendNotFound(exchange);
            }
        }
    }

    private void handlePostRequest(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if (path.equals("/epics")) {
            createEpic(exchange);
        } else {
            sendNotFound(exchange);
        }
    }

    private void handleDeleteRequest(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        Matcher idMatcher = idPattern.matcher(path);
        if (idMatcher.matches()) {
            int id = Integer.parseInt(idMatcher.group(1));
            deleteEpic(exchange, id);
        } else {
            sendNotFound(exchange);
        }
    }

    private void sendEpics(HttpExchange exchange) throws IOException {
        List<Epic> epics = taskManager.getAllEpics();
        String response = convertEpicsToJson(epics);
        sendResponse(exchange, 200, response);
    }

    private void sendEpicById(HttpExchange exchange, int id) throws IOException {
        Epic epic = taskManager.getEpicById(id);
        if (epic != null) {
            String response = convertEpicToJson(epic);
            sendResponse(exchange, 200, response);
        } else {
            sendNotFound(exchange);
        }
    }

    private void sendEpicSubtasks(HttpExchange exchange, int epicId) throws IOException {
        List<SubTask> subtasks = taskManager.getSubTasksOfEpic(epicId);
        if (!subtasks.isEmpty()) {
            String response = convertSubTasksToJson(subtasks);
            sendResponse(exchange, 200, response);
        } else {
            sendNotFound(exchange);
        }
    }

    private void createEpic(HttpExchange exchange) throws IOException {
        try {
            InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder requestBody = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                requestBody.append(line);
            }
            br.close();
            isr.close();

            Epic epic = gson.fromJson(requestBody.toString(), Epic.class);
            Epic createdEpic = taskManager.createEpic(epic);
            String response = convertEpicToJson(createdEpic);
            sendResponse(exchange, 201, response);
        } catch (Exception e) {
            sendHasInteractions(exchange);
        }
    }

    private void deleteEpic(HttpExchange exchange, int id) throws IOException {
        Epic epic = taskManager.getEpicById(id);
        if (epic != null) {
            taskManager.deleteEpic(id);
            sendResponse(exchange, 200, "Epic deleted successfully");
        } else {
            sendNotFound(exchange);
        }
    }

    private String convertEpicsToJson(List<Epic> epics) {
        return gson.toJson(epics);
    }

    private String convertEpicToJson(Epic epic) {
        return gson.toJson(epic);
    }

    private String convertSubTasksToJson(List<SubTask> subtasks) {
        return gson.toJson(subtasks);
    }
}

