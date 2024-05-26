package server;

import model.Task;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import service.HistoryManager;
import service.InMemoryTaskManager;
import service.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskManagerPrioritizedTasksTest {

    private HistoryManager historyManager;
    // Создаем экземпляр InMemoryTaskManager
    TaskManager manager = new InMemoryTaskManager(historyManager);
    // Передаем его в качестве аргумента в конструктор HttpTaskServer
    HttpTaskServer taskServer = new HttpTaskServer(manager);

    @BeforeEach
    public void setUp() {
        manager.deleteAllTasks(); // Очищаем задачи перед каждым тестом
        taskServer.start(); // Запускаем HTTP-сервер перед каждым тестом
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop(); // Останавливаем HTTP-сервер после каждого теста
    }

    @Test
    public void testGetPrioritizedTasks() throws IOException, InterruptedException {
        // Создаем несколько задач для проверки приоритета
        manager.createTask(new Task("Task 1", "Description 1"));
        manager.createTask(new Task("Task 2", "Description 2"));
        manager.createTask(new Task("Task 3", "Description 3"));

        // Отправляем GET-запрос для получения списка задач в порядке приоритета
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        // Получаем ответ от сервера
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем статус код ответа
        assertEquals(200, response.statusCode());

        // Проверяем, что ответ содержит список задач
        assertNotNull(response.body());
    }
}
