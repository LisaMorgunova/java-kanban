package server;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {
    private HttpTaskServer httpTaskServer;

    @BeforeEach
    void setUp() {
        TaskManager manager = Managers.getInMemoryTaskManager();
        this.httpTaskServer = new HttpTaskServer(manager);
        httpTaskServer.start();
    }

    @AfterEach
    void tearDown() {
        this.httpTaskServer.stop();
    }

    @Test
    void main() {
        TaskManager taskManager = Managers.getDefaultTaskManager();

        Task task1 = new Task("Задача 1", Status.NEW, "Описание задачи 1");
        Task savedTask1 = taskManager.createTask(task1);
        System.out.println("Создана задача: " + savedTask1);

        Epic epic1 = new Epic("Эпик 1", Status.NEW, "Описание эпика 1");
        Epic savedEpic1 = taskManager.createEpic(epic1);
        System.out.println("Создан эпик: " + savedEpic1);

        SubTask subTask1 = new SubTask("Подзадача 1", Status.NEW, "Описание подзадачи 1", savedEpic1.getId());
        SubTask savedSubTask1 = taskManager.createSubTask(subTask1);
        System.out.println("Создана подзадача: " + savedSubTask1);

        taskManager.getTaskById(savedTask1.getId());
        taskManager.getEpicById(savedEpic1.getId());
        taskManager.getSubTaskById(savedSubTask1.getId());

        System.out.println("История просмотров:");
        taskManager.getHistory().forEach(System.out::println);

        taskManager.updateTaskStatus(savedSubTask1.getId(), Status.DONE);
        System.out.println("После обновления статуса подзадачи, эпик имеет статус: " + taskManager.getEpicById(savedEpic1.getId()).getStatus());

        taskManager.deleteTask(savedTask1.getId());
        taskManager.deleteSubTask(savedSubTask1.getId());
        System.out.println("История просмотров после удаления задач:");
        taskManager.getHistory().forEach(System.out::println);
    }
}