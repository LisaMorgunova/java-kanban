import Model.Epic;
import Model.SubTask;
import Model.Task;
import Model.Status;
import service.TaskManager;

import java.util.List;

public class Main { // Уже сложно успеть, но я попробую успеть :)

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        Epic epic1 = taskManager.createEpic(new Epic("Эпик 1"));
        Epic epic2 = taskManager.createEpic(new Epic("Эпик 2"));

        SubTask subTask1 = taskManager.createSubTask(new SubTask("Подзадача 1 для эпика 1", epic1.getId()), epic1.getId());
        SubTask subTask2 = taskManager.createSubTask(new SubTask("Подзадача 1 для эпика 2", epic2.getId()), epic2.getId());
        SubTask subTask3 = taskManager.createSubTask(new SubTask("Подзадача 2 для эпика 2", epic2.getId()), epic2.getId());

        taskManager.updateTaskStatus(subTask1.getId(), Status.DONE);
        taskManager.updateTaskStatus(subTask2.getId(), Status.IN_PROGRESS);
        taskManager.updateTaskStatus(subTask3.getId(), Status.DONE);

        System.out.println("Список всех задач:");
        printTasks(taskManager.getAllTasks());

        System.out.println("\nСписок всех эпиков:");
        printEpics(taskManager.getAllEpics());

        System.out.println("\nСписок всех подзадач для эпика 2:");
        printSubTasks(taskManager.getSubTasksOfEpic(epic2.getId()));

        taskManager.deleteTask(subTask1.getId());

        System.out.println("\nПосле удаления подзадачи:");
        printTasks(taskManager.getAllTasks());
    }

    private static void printTasks(List<Task> tasks) {
        for (Task task : tasks) {
            System.out.println(task);
        }
    }

    private static void printEpics(List<Epic> epics) {
        for (Epic epic : epics) {
            System.out.println(epic + " Статус: " + epic.getStatus());
        }
    }

    private static void printSubTasks(List<SubTask> subTasks) {
        for (SubTask subTask : subTasks) {
            System.out.println(subTask + " Принадлежит эпику с ID: " + subTask.getEpicId() + ", Статус: " + subTask.getStatus());
        }
    }
}
