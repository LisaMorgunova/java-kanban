package service;

import exception.ManagerSaveException;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    protected FileBackedTaskManager createTaskManager() {
        return new FileBackedTaskManager(new InMemoryHistoryManager(), new File("path_to_your_file"));
    }

    @Test
    void testFileOperationException() {
        Path tmp = Paths.get("C:\\users\\username\\desktop\\favouriteFile.txt");
        assertThrows(ManagerSaveException.class, () -> FileBackedTaskManager.loadFromFile(tmp.toFile()), "Должно быть выброшено исключение при попытке загрузки из несуществующего файла");
    }

    @Test
    public void testSubTaskAndEpicInteraction() {
        Epic epic = new Epic("Epic", Status.NEW, "Description");
        epic = taskManager.createEpic(epic);
        SubTask subTask = new SubTask("SubTask name", Status.NEW, "Description", epic.getId());
        subTask = taskManager.createSubTask(subTask);
        taskManager.deleteSubTask(subTask.getId());
        assertTrue(taskManager.getEpicById(epic.getId()).getSubTasks().isEmpty(), "Epic's subtask list should be empty after deleting the subtask.");
        assertEquals(Status.NEW, taskManager.getEpicById(epic.getId()).getStatus(), "Epic status should be NEW after all subtasks are deleted.");
    }
}