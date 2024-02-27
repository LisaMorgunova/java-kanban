package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryTaskManagerTest {

    private InMemoryTaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
    }

    @Test
    void whenCreatingTask_thenTaskIsStored() {
        Task task = new Task("Task 1", Status.NEW, "Task Description");
        task = taskManager.createTask(task);
        Task taskExpected = taskManager.getTaskById(task.getId());
        assertNotNull(taskExpected, "Created task should not be null");
        assertEquals(task, taskExpected, "Tasks should match");
    }

    @Test
    void whenCreatingSubTask_thenSubTaskAndEpicAreUpdated() {
        Epic epic = new Epic("Epic 1", "Epic Description", Status.NEW);
        Epic createdEpic = taskManager.createEpic(epic);
        SubTask subTask = new SubTask("SubTask 1", "SubTask Description", Status.NEW, createdEpic.getId());
        SubTask createdSubTask = taskManager.createSubTask(subTask);

        assertNotNull(createdSubTask, "Created subtask should not be null");
        assertEquals(subTask.getName(), createdSubTask.getName(), "SubTask names should match");

        Epic updatedEpic = taskManager.getEpicById(createdEpic.getId());
        assertFalse(updatedEpic.getSubTaskIds().isEmpty(), "Epic should have subtasks");
    }

    @Test
    void whenUpdatingTaskStatus_thenStatusIsUpdated() {
        Task task = new Task("Task for status update", Status.NEW, "Task Description");
        Task createdTask = taskManager.createTask(task);
        taskManager.updateTaskStatus(createdTask.getId(), Status.DONE);
        Task updatedTask = taskManager.getTaskById(createdTask.getId());
        assertEquals(Status.DONE, updatedTask.getStatus(), "Task status should be updated to DONE");
    }

    @Test
    void whenDeletingTask_thenTaskIsRemoved() {
        Task task = new Task("Task for deletion", Status.NEW, "Task Description");
        Task createdTask = taskManager.createTask(task);
        taskManager.deleteTask(createdTask.getId());
        assertNull(taskManager.getTaskById(createdTask.getId()), "Task should be deleted and not found");
    }

    @Test
    void whenGettingAllTasks_thenCorrectListReturned() {
        taskManager.createTask(new Task("Task 1", Status.NEW, "Task Description"));
        Epic epic = taskManager.createEpic(new Epic("Epic 1", "Epic Description", Status.NEW));
        taskManager.createSubTask(new SubTask("SubTask 1", "SubTask Description", Status.NEW, epic.getId()));

        assertFalse(taskManager.getAllTasks().isEmpty(), "List of all tasks should not be empty");
        assertEquals(1, taskManager.getAllTasks().size(), "There should be task (1 task)");
        assertEquals(1, taskManager.getAllEpics().size(), "There should be epic (1 epic)");
        assertEquals(1, taskManager.getAllSubTasks().size(), "There should be subTask (1 subTask)");
    }

    @Test
    void whenViewingTasks_thenHistoryIsUpdated() {
        Task task = taskManager.createTask(new Task("Task 1", Status.NEW, "Task Description"));
        taskManager.getTaskById(task.getId()); // View the task
        assertFalse(taskManager.getHistory().isEmpty(), "History should not be empty after viewing a task");
        assertEquals(1, taskManager.getHistory().size(), "History should contain one entry after viewing a task");
    }

    @Test
    void whenDeletingSubTask_thenEpicStatusIsUpdated() {
        Epic epic = taskManager.createEpic(new Epic("Epic 1", "Epic Description", Status.NEW));
        SubTask subTask1 = taskManager.createSubTask(new SubTask("SubTask 1", "SubTask Description", Status.NEW, epic.getId()));
        SubTask subTask2 = taskManager.createSubTask(new SubTask("SubTask 2", "SubTask Description", Status.NEW, epic.getId()));
        assertEquals(Status.NEW, epic.getStatus(), "Epic status should be NEW if all subtasks are NEW or if there's at least one subtask left.");
        taskManager.updateTaskStatus(subTask1.getId(), Status.IN_PROGRESS);
        Epic updatedEpic = taskManager.getEpicById(epic.getId());

        assertEquals(Status.IN_PROGRESS, updatedEpic.getStatus(), "Epic status should be NEW if all subtasks are NEW or if there's at least one subtask left.");
        taskManager.deleteSubTask(subTask1.getId());

        taskManager.updateTaskStatus(subTask2.getId(), Status.DONE);
        updatedEpic = taskManager.getEpicById(epic.getId());
        assertEquals(Status.DONE, updatedEpic.getStatus(), "Epic status should be DONE if all subtasks are DONE.");
    }

}
