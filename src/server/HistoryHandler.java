package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import model.Task;
import service.HistoryManager;
import service.TaskManager;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler {
    private final HistoryManager historyManager;

    public HistoryHandler(TaskManager historyManager) {
        this.historyManager = (HistoryManager) historyManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            getHistory(exchange);
        } else {
            sendNotFound(exchange);
        }
    }

    private void getHistory(HttpExchange exchange) throws IOException {
        List<Task> history = historyManager.getHistory();
        String jsonResponse = convertHistoryToJson(history);
        sendResponse(exchange, 200, jsonResponse);
    }

    private String convertHistoryToJson(List<Task> history) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(history);
    }
}
