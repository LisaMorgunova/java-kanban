import Model.Epic;
import Model.SubTask;
import Model.Task;
import Model.Status;
import service.TaskManager;

import java.util.List;

public class Main { // постаралась все учесть :)

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        Epic epic1 = taskManager.createEpic(new Epic("Эпик 1", "Описание эпика 1"));
        Epic epic2 = taskManager.createEpic(new Epic("Эпик 2", "Описание эпика 2"));

        SubTask subTask1 = taskManager.createSubTask(new SubTask("Подзадача 1 для эпика 1", "Описание подзадачи 1", epic1.getId()));
        SubTask subTask2 = taskManager.createSubTask(new SubTask("Подзадача 1 для эпика 2", "Описание подзадачи 2", epic2.getId()));
        SubTask subTask3 = taskManager.createSubTask(new SubTask("Подзадача 2 для эпика 2", "Описание подзадачи 3", epic2.getId()));

        taskManager.updateTaskStatus(subTask1.getId(), Status.DONE);
        taskManager.updateTaskStatus(subTask2.getId(), Status.IN_PROGRESS);
        taskManager.updateTaskStatus(subTask3.getId(), Status.DONE);


        System.out.println("\nПосле обновления статусов:");
        printEpics(taskManager.getAllEpics());

        taskManager.deleteSubTask(subTask2.getId());

        System.out.println("\nПосле удаления подзадачи:");
        printEpics(taskManager.getAllEpics());
    }

    private static void printTasks(List<Task> tasks) {
        tasks.forEach(task -> System.out.println(task));
    }

    private static void printEpics(List<Epic> epics) {
        epics.forEach(epic -> {
            System.out.println(epic + ", Статус: " + epic.getStatus());
            System.out.println("Подзадачи эпика:");
            epic.getSubTasksIds().forEach(subTaskId -> System.out.println("ID подзадачи: " + subTaskId));
        });
    }

    private static void printSubTasks(List<SubTask> subTasks) {
        subTasks.forEach(subTask -> System.out.println(subTask + ", Принадлежит эпику с ID: " + subTask.getEpicId() + ", Статус: " + subTask.getStatus()));
    }
}