package server;

import model.Epic;
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

public class HttpTaskManagerEpicHandlerTest {

    //private HistoryManager historyManager = ;
    private HistoryManager InMemoryHistoryManager;
    // Создаем экземпляр InMemoryTaskManager
   // HistoryManager historyManager = new InMemoryTaskManager();
    TaskManager manager = new InMemoryTaskManager(InMemoryHistoryManager);
    //TaskManager manager = new InMemoryTaskManager(historyManager);
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
    public void testGetAllEpics() throws IOException, InterruptedException {
        // Создаем несколько эпиков для проверки
        manager.createEpic(new Epic("Epic 1", "Description 1"));
        manager.createEpic(new Epic("Epic 2", "Description 2"));

        // Отправляем GET-запрос для получения всех эпиков
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        // Получаем ответ от сервера
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем статус код ответа
        assertEquals(200, response.statusCode());

        // Проверяем, что ответ содержит список эпиков
        assertNotNull(response.body());
    }
}
