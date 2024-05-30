package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import service.*;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {

    private static final int PORT = 8080;
    public static final Gson gson = new Gson();
    private TaskManager taskManager;
    private static HttpServer server = null;


    public HttpTaskServer(TaskManager manager) {
        this.taskManager = manager;
    }

    public static void main(String[] args) {
        TaskManager manager = Managers.getInMemoryTaskManager();
        HttpTaskServer httpTaskServer = new HttpTaskServer(manager);
        httpTaskServer.start();
    }

    public void start() {
        try {
            HttpTaskServer.server = HttpServer.create(new InetSocketAddress(PORT), 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert server != null;
        server.createContext("/tasks", (HttpHandler) new TaskHandler(taskManager));
        server.createContext("/subtasks", (HttpHandler) new SubTaskHandler(taskManager));
        server.createContext("/epics", (HttpHandler) new EpicHandler(taskManager));
        server.createContext("/history", (HttpHandler) new HistoryHandler(Managers.getDefaultHistoryManager()));
        server.createContext("/prioritized", (HttpHandler) new PrioritizedHandler(taskManager));
        server.setExecutor(null);
        server.start();
        System.out.println("Server started on port: " + PORT);

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

    public void stop() {
        HttpTaskServer.server.stop(0);
        System.out.println("Server stopped");
    }
}