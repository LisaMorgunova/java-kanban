package service;

import exception.ManagerSaveException;
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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    File file;

    String string2 = "id,type,name,status,description,startTime,duration,epic,\n" +
            "1,TASK,Task1,NEW,Description task1,2024-04-24T04:20,3,\n" +
            "2,EPIC,Epic2,DONE,Description epic2,2024-04-24T05:20,5,\n" +
            "3,SUBTASK,Sub Task2,DONE,Description sub task3,2024-04-24T06:20,2,2,\n";
    HistoryManager historyManager;

    @BeforeEach
    void tempFile() throws IOException {
        file = File.createTempFile("aaa", "b");
        FileWriter FW = new FileWriter(file);
        FW.write(string2);
        historyManager = new InMemoryHistoryManager();
        Task task = new Task(1, "Task1", Status.NEW, "Description task1", Duration.ofMinutes(3), LocalDateTime.of(LocalDate.of(2024, 4, 24), LocalTime.of(4, 20)));
        Epic epic = new Epic(2, "Epic2", Status.DONE, "Description epic2", Duration.ofMinutes(5), LocalDateTime.of(LocalDate.of(2024, 4, 24), LocalTime.of(5, 20)));
        SubTask subtask = new SubTask(3, "Sub Task2", Status.DONE, "Description sub task3", Duration.ofMinutes(2), LocalDateTime.of(LocalDate.of(2024, 4, 24), LocalTime.of(6, 20)), 2);
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
        assertEquals(string2, savedContent);
    }

    @Test
    void historyToString() {
        String s = FileBackedTaskManager.historyToString(historyManager);
        assertEquals(string2, s);
    }

    @Test
    void historyFromString() {
        List<Task> list = FileBackedTaskManager.historyFromString(string2);
        List<Task> list1 = historyManager.getHistory();
        assertTrue(list1.containsAll(list));
        assertEquals(list.size(), list1.size());
    }

    @Test
    void testToString() {
        Task task = new Task(1, "Task1", Status.NEW, "Description task1", Duration.ofMinutes(3), LocalDateTime.of(LocalDate.of(2024, 4, 24), LocalTime.of(4, 20)));
        String expected = "1,TASK,Task1,NEW,Description task1,2024-04-24T04:20,3,";
        String result = FileBackedTaskManager.getTaskAsString(task);
        assertEquals(expected, result);
    }

    @Test
    void loadFromFile() {
        FileBackedTaskManager first = new FileBackedTaskManager(historyManager, file);
        first.createTask(new Task(1, "Task1", Status.NEW, "Description task1", Duration.ofMinutes(3), LocalDateTime.of(LocalDate.of(2024, 4, 24), LocalTime.of(4, 20))));
        FileBackedTaskManager second = FileBackedTaskManager.loadFromFile(file);
        assertTrue(historyManager.getHistory().containsAll(second.getHistory()));
    }

    @Test
    void loadFromEmptyFile() throws IOException {
        File tmp = File.createTempFile("bbb", "a");
        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(tmp);
        assertTrue(fileBackedTaskManager.getHistory().isEmpty());
    }

    @Test
    void testFileOperationException() {
        Path tmp = Paths.get("C:\\users\\username\\desktop\\favouriteFile.txt");
        assertThrows(ManagerSaveException.class, () -> FileBackedTaskManager.loadFromFile(tmp.toFile()), "Должно быть выброшено исключение при попытке загрузки из несуществующего файла");
    }
}