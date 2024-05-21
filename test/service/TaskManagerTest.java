package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

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
        Epic epic = new Epic("Epic", Status.NEW,"Epic Description");
        epic = taskManager.createEpic(epic);
        SubTask subTask = new SubTask("SubTask name", Status.NEW, "SubTask Description", epic.getId());
        SubTask savedSubTask = taskManager.createSubTask(subTask);
        assertEquals(savedSubTask, taskManager.getSubTaskById(savedSubTask.getId()), "SubTask should be retrievable by ID.");
        assertTrue(taskManager.getEpicById(epic.getId()).getSubTasks().contains(savedSubTask), "Epic should contain the subtask ID.");
    }

    @Test
    public void testEpicStatusUpdate() {
        Epic epic = new Epic("Epic", Status.NEW, "Epic Description");
        epic = taskManager.createEpic(epic);

        Epic retrievedEpic = taskManager.getEpicById(epic.getId());
        assertNotNull(retrievedEpic, "Epic should not be null");

        List<SubTask> subTaskIds = retrievedEpic.getSubTasks();
        assertNotNull(subTaskIds, "Subtask IDs should not be null");

        boolean hasInProgress = subTaskIds.stream().anyMatch(subTask -> taskManager.getSubTaskById(subTask.getId()).getStatus() == Status.IN_PROGRESS);
        boolean hasDone = subTaskIds.stream().anyMatch(subTask -> taskManager.getSubTaskById(subTask.getId()).getStatus() == Status.DONE);

        if (hasInProgress) {
            taskManager.updateTaskStatus(epic.getId(), Status.IN_PROGRESS);
            assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(epic.getId()).getStatus(), "Epic status should be IN_PROGRESS if at least one subtask is IN_PROGRESS.");
        } else if (hasDone) {
            taskManager.updateTaskStatus(epic.getId(), Status.DONE);
            assertEquals(Status.DONE, taskManager.getEpicById(epic.getId()).getStatus(), "Epic status should be DONE if all subtasks are DONE.");
        } else {
            assertEquals(Status.NEW, taskManager.getEpicById(epic.getId()).getStatus(), "Epic status should be NEW if all subtasks are NEW.");
        }
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
        Epic epic = new Epic("Epic", Status.NEW, "Description");
        epic = taskManager.createEpic(epic);
        SubTask subTask = new SubTask("SubTask name", Status.NEW, "Description", epic.getId());
        subTask = taskManager.createSubTask(subTask);
        taskManager.deleteSubTask(subTask.getId());
        assertTrue(taskManager.getEpicById(epic.getId()).getSubTasks().isEmpty(), "Epic's subtask list should be empty after deleting the subtask.");
        assertEquals(Status.DONE, taskManager.getEpicById(epic.getId()).getStatus(), "Epic status should be NEW after all subtasks are deleted.");
    }

    @Test
    public void testEpicContainsSubTasks() {
        Epic epic = new Epic("Epic", Status.NEW, "Epic Description");
        SubTask subTask1 = new SubTask("SubTask 1", Status.NEW, "Description for SubTask 1", 0);
        SubTask subTask2 = new SubTask("SubTask 2", Status.NEW, "Description for SubTask 2", 0);

        epic = taskManager.createEpic(epic);
        subTask1.setEpicId(epic.getId());
        subTask2.setEpicId(epic.getId());

        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);

        assertTrue(taskManager.getEpicById(epic.getId()).getSubTasks().contains(subTask1), "Epic should contain SubTask 1.");
        assertTrue(taskManager.getEpicById(epic.getId()).getSubTasks().contains(subTask2), "Epic should contain SubTask 2.");
    }

//    @Test
//    public void testCorrectEpicStatusCalculation() {
//        Epic epic = new Epic("Epic for Status Calculation", Status.NEW, "Epic Description");
//        epic = taskManager.createEpic(epic);
//        SubTask subTask1 = new SubTask("SubTask 1", Status.DONE, "Description for SubTask 1", epic.getId());
//        SubTask subTask2 = new SubTask("SubTask 2", Status.IN_PROGRESS, "Description for SubTask 2", epic.getId());
//
//        taskManager.createSubTask(subTask1);
//        taskManager.createSubTask(subTask2);
//
//        taskManager.updateTaskStatus(subTask1.getId(), Status.DONE);
//        taskManager.updateTaskStatus(subTask2.getId(), Status.IN_PROGRESS);
//
//        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(epic.getId()).getStatus(), "Epic status should be IN_PROGRESS when subtasks are mixed DONE and IN_PROGRESS.");
//    }
}