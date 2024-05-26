package server;

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

public class HttpTaskManagerTasksTest {

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
    public void testAddTask() throws IOException, InterruptedException {
        // Создаем JSON-строку для новой задачи
        String taskJson = "{\"name\":\"Test Task\",\"description\":\"Test Description\"}";

        // Создаем HTTP-клиент и отправляем POST-запрос для создания новой задачи
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        // Получаем ответ от сервера
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем статус код ответа
        assertEquals(200, response.statusCode());

        // Проверяем, что задача была успешно добавлена
        assertNotNull(manager.getTaskById(1));
    }
}
