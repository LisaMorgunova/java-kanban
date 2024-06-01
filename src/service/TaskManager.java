package service;

import model.Epic;
import model.SubTask;
import model.Task;
import model.Status;
import java.util.List;

public interface TaskManager {
    Task createTask(Task task);
    SubTask createSubTask(SubTask subTask);
    Epic createEpic(Epic epic);
    Task getTaskById(int id);
    SubTask getSubTaskById(int id);
    Epic getEpicById(int id);
    void updateTask(Task task);
    void updateSubTask(SubTask subTask);
    void updateEpic(Epic epic);
    void updateTaskStatus(int id, Status newStatus);
    void deleteTask(int id);
    void deleteSubTask(int id);
    void deleteEpic(int id);
    void deleteAllTasks();
    List<Task> getAllTasks();
    List<Epic> getAllEpics();
    List<SubTask> getAllSubTasks();
    List<SubTask> getSubTasksOfEpic(int epicId);
    List<Task> getHistory();
    List<Task> getPrioritizedTasks();
}
