package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileBackedTaskManagerTest {
    File file;
    HistoryManager historyManager;
    String string1 = "id,type,name,status,description,epic\n" +
            "1,TASK,Task1,NEW,Description task1,\n" +
            "2,EPIC,Epic2,DONE,Description epic2,\n" +
            "3,SUBTASK,Sub Task2,DONE,Description sub task3,2";

    @BeforeEach
    void tempFile() throws IOException {
        file = File.createTempFile("aaa", "b");
        FileWriter FW = new FileWriter(file);
        FW.write("id,type,name,status,description,epic\n" +
                "1,TASK,Task1,NEW,Description task1,\n" +
                "2,EPIC,Epic2,DONE,Description epic2,\n" +
                "3,SUBTASK,Sub Task2,DONE,Description sub task3,2");
        historyManager = new InMemoryHistoryManager();
        Task task = new Task("Task1", Status.NEW, "Description task1");
        Epic epic = new Epic("Epic2", Status.DONE, "Description epic2");
        SubTask subtask = new SubTask("Sub Task2", Status.DONE, "Description sub task3", 2);
        subtask.setId(3);
        task.setId(1);
        epic.setId(2);
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);
    }

    @Test
    void save() {
        FileBackedTaskManager taskManager = new FileBackedTaskManager(historyManager, file);
        taskManager.save();
        String savedContent = "";
        try {
            savedContent = Files.readString(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals(string1, savedContent);
    }

    @Test
    void historyToString() {
        String s = FileBackedTaskManager.historyToString(historyManager);
        assertEquals(s, string1);

    }

    @Test
    void historyFromString() {
        List<Task> list = FileBackedTaskManager.historyFromString(string1);
        List<Task> list1 = historyManager.getHistory();
        assertTrue(list1.containsAll(list));
        assertEquals(list.size(), list1.size());
    }

    @Test
    void testToString() {
        Task task = new Task("Task1", Status.NEW, "Description task1");
        task.setId(1);
        String expected = "1,TASK,Task1,NEW,Description task1";
        String result = FileBackedTaskManager.toString(task);
        assertEquals(expected, result);
    }

    @Test
    void loadFromFile() {
        FileBackedTaskManager first = new FileBackedTaskManager(historyManager, file);
        FileBackedTaskManager second = FileBackedTaskManager.loadFromFile(file);
        assertTrue(historyManager.getHistory().containsAll(second.getHistory()));
    }

    @Test
    void loadFromEmptyFile() throws IOException {
        File tmp = File.createTempFile("bbb", "a");
        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(tmp);
        assertTrue(fileBackedTaskManager.getHistory().isEmpty());
    }
}

