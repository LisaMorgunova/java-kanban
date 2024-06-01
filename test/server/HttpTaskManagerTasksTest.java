package server;

import com.google.gson.JsonObject;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.HistoryManager;
import service.InMemoryHistoryManager;
import service.InMemoryTaskManager;
import service.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskManagerTasksTest {

    private HistoryManager historyManager;
    private TaskManager manager;
    private HttpTaskServer taskServer;


    @BeforeEach
    public void setUp() {
        historyManager = new InMemoryHistoryManager();
        manager = new InMemoryTaskManager(historyManager);
        manager.deleteAllTasks();
        taskServer = new HttpTaskServer(manager);
        taskServer.start();
    }


    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testAddTask() throws IOException {
        Task task = new Task("Test Task", "Test Description");
        task.setId(1);
        JsonObject innerObject = new JsonObject();
        innerObject.addProperty("name", "Test Task");
        innerObject.addProperty("description", "Test Description");
        String taskJson = innerObject.toString();


        HttpClient client = HttpClient.newHttpClient();
        URI urlAdd = URI.create("http://localhost:8080/tasks");
        HttpRequest requestAdd = HttpRequest.newBuilder()
                .uri(urlAdd)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        try {
            HttpResponse<String> responseAdd = client.send(requestAdd, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, responseAdd.statusCode());

            assertNotNull(manager.getTaskById(1));
            assertEquals(manager.getTaskById(1), task);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        URI urlGet = URI.create("http://localhost:8080/tasks/1");
        HttpRequest requestGet = HttpRequest.newBuilder()
                .uri(urlGet)
                .GET()
                .build();

        try {
            HttpResponse<String> responseGet = client.send(requestGet, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, responseGet.statusCode());

            Task task1 = HttpTaskServer.gson.fromJson(responseGet.body(), Task.class);

            assertEquals(task, task1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    @Test
    public void testNotExistTask() throws IOException {
        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest requestGet = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        try {
            HttpResponse<String> responseGet = client.send(requestGet, HttpResponse.BodyHandlers.ofString());

            assertEquals(404, responseGet.statusCode());

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


}
