import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;

    @BeforeEach
    public void setup() {
        taskManager = createTaskManager();
    }

    protected abstract T createTaskManager();

    @Test
    public void testCreateAndRetrieveTasks() {
        Task task = new Task("Task", Status.NEW, "Description");
        Task savedTask = taskManager.createTask(task);
        assertEquals(savedTask, taskManager.getTaskById(savedTask.getId()), "Task should be retrievable by ID.");
    }

    @Test
    public void testCreateAndRetrieveSubTasks() {
        Epic epic = new Epic("Epic", "Epic Description");
        epic = taskManager.createEpic(epic);
        SubTask subTask = new SubTask(1, "SubTask name", Status.NEW, "SubTask Description", Duration.ofHours(1), LocalDateTime.now(), epic.getId());
        SubTask savedSubTask = taskManager.createSubTask(subTask);
        assertEquals(savedSubTask, taskManager.getSubTaskById(savedSubTask.getId()), "SubTask should be retrievable by ID.");
        assertTrue(taskManager.getEpicById(epic.getId()).getSubTasks().contains(savedSubTask.getId()), "Epic should contain the subtask ID.");
    }

    @Test
    public void testEpicStatusUpdate() {
        Epic epic = new Epic("Epic", "Epic Description", Status.NEW);
        epic = taskManager.createEpic(epic);
        SubTask subTask1 = new SubTask(1, "SubTask name", Status.NEW, "Description", Duration.ofHours(1), LocalDateTime.now(), epic.getId());
        SubTask subTask2 = new SubTask(2, "SubTask name", Status.NEW, "Description", Duration.ofHours(1), LocalDateTime.now().plusHours(2), epic.getId());
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.updateTaskStatus(subTask1.getId(), Status.DONE);
        taskManager.updateTaskStatus(subTask2.getId(), Status.DONE);
        assertEquals(Status.DONE, taskManager.getEpicById(epic.getId()).getStatus(), "Epic status should be DONE if all subtasks are DONE.");
    }

    @Test
    public void testTaskDeletion() {
        Task task = new Task("Task", Status.NEW, "Description");
        task = taskManager.createTask(task);
        taskManager.deleteTask(task.getId());
        assertNull(taskManager.getTaskById(task.getId()), "Task should be deleted.");
    }

    @Test
    public void testSubTaskAndEpicInteraction() {
        Epic epic = new Epic("Epic", "Description", Status.NEW);
        epic = taskManager.createEpic(epic);
        SubTask subTask = new SubTask(0, "SubTask name", Status.NEW, "Description", Duration.ofHours(1), LocalDateTime.now(), epic.getId());
        subTask = taskManager.createSubTask(subTask);
        taskManager.deleteSubTask(subTask.getId());
        assertTrue(taskManager.getEpicById(epic.getId()).getSubTasks().isEmpty(), "Epic's subtask list should be empty after deleting the subtask.");
        assertEquals(Status.NEW, taskManager.getEpicById(epic.getId()).getStatus(), "Epic status should be NEW after all subtasks are deleted.");
    }
}

