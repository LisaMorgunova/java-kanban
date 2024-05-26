package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {

    private static final int PORT = 8080;
    public static final Gson gson = new Gson();
    private static final TaskManager taskManager = Managers.getInMemoryTaskManager();

    public HttpTaskServer(TaskManager manager) {
    }

    public static void main(String[] args) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
            server.createContext("/tasks", (HttpHandler) new TaskHandler(taskManager));
            server.createContext("/subtasks", (HttpHandler) new SubTaskHandler(taskManager));
            server.createContext("/epics", (HttpHandler) new EpicHandler(taskManager));
            server.createContext("/history", (HttpHandler) new HistoryHandler(taskManager));
            server.createContext("/prioritized", (HttpHandler) new PrioritizedHandler(taskManager));
            server.setExecutor(null);
            server.start();
            System.out.println("Server started on port: " + PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }

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

    public void start() {
    }

    public void stop() {
    }
}
