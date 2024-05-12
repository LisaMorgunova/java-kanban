package service;

import model.Status;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;
    private Task task1, task2, task11;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
        task1 = new Task("Task 1", Status.NEW, "Description 1");
        task2 = new Task("Task 2", Status.NEW, "Description 2");
        task1.setId(1);
        task2.setId(2);
        task11 = new Task("Task 11", Status.NEW, "Description 11");
        task11.setId(11);
    }

    @Test
    void addAndRetrieveHistory() {
        historyManager.add(task1);
        historyManager.add(task2);
        List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не должна быть пустой.");
        assertEquals(2, history.size(), "История должна содержать две задачи.");
        assertTrue(history.contains(task1), "История должна содержать первую задачу.");
        assertTrue(history.contains(task2), "История должна содержать вторую задачу.");
    }

    @Test
    void historySizeLimit() {
        for (int i = 1; i <= 10; i++) {
            Task task = new Task("Task " + i, Status.NEW, "Description " + i);
            task.setId(i);
            historyManager.add(task);
        }
        historyManager.add(task11);
        List<Task> history = historyManager.getHistory();
        assertEquals(10, history.size(), "История не должна превышать 10 задач.");
        Task t = new Task("Task 1", Status.NEW, "Description 1");
        t.setId(1);
        historyManager.add(t);
        assertFalse(history.stream().anyMatch(task -> "Task 11".equals(task.getName())), "Одиннадцатая задача должна быть удалена из истории.");
        assertTrue(history.contains(task1), "Первая задача должна быть в истории.");
    }

    @Test
    void removeTaskFromHistory() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.remove(task1.getId());
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "История должна содержать одну задачу после удаления.");
        assertFalse(history.contains(task1), "Первая задача не должна быть в истории.");
    }

    @Test
    void clearHistory() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.clear();
        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty(), "История должна быть очищена.");
    }

    @Test
    void testEmptyHistory() {
        assertTrue(historyManager.getHistory().isEmpty(), "История должна быть пустой после инициализации");
    }

    @Test
    void testHistoryDuplication() {
        historyManager.add(task1);
        historyManager.add(task1);
        assertEquals(1, historyManager.getHistory().size(), "История не должна содержать дубликаты");
    }

    @Test
    void testRemoveFromHistory() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.remove(task1.getId());
        assertFalse(historyManager.getHistory().contains(task1), "Задача должна быть удалена из истории");
    }

    @Test
    void removeTaskFromStartOfHistory() {
        for (int i = 1; i <= 5; i++) {
            Task task = new Task("Task " + i, Status.NEW, "Description " + i);
            task.setId(i);
            historyManager.add(task);
        }
        historyManager.remove(1);
        assertFalse(historyManager.getHistory().contains(new Task("Task 1", Status.NEW, "Description 1")), "Первая задача должна быть удалена из истории.");
    }

    @Test
    void removeTaskFromMiddleOfHistory() {
        for (int i = 1; i <= 5; i++) {
            Task task = new Task("Task " + i, Status.NEW, "Description " + i);
            task.setId(i);
            historyManager.add(task);
        }
        historyManager.remove(3);
        assertFalse(historyManager.getHistory().contains(new Task("Task 3", Status.NEW, "Description 3")), "Задача из середины должна быть удалена из истории.");
    }

    @Test
    void removeTaskFromEndOfHistory() {
        for (int i = 1; i <= 5; i++) {
            Task task = new Task("Task " + i, Status.NEW, "Description " + i);
            task.setId(i);
            historyManager.add(task);
        }
        historyManager.remove(5);
        assertFalse(historyManager.getHistory().contains(new Task("Task 5", Status.NEW, "Description 5")), "Последняя задача должна быть удалена из истории.");
    }

    @Test
    void testRemovingFromEmptyHistory() {
        historyManager.remove(1);
        assertTrue(historyManager.getHistory().isEmpty(), "История должна оставаться пустой после попытки удаления.");
    }

    @Test
    void testClearOnEmptyHistory() {
        historyManager.clear();
        assertTrue(historyManager.getHistory().isEmpty(), "История должна оставаться пустой после очистки.");
    }

    @Test
    void testIntersectionFalse() {
        Task subTask1 = new Task(1, "Task1", Status.NEW, "desc1", Duration.ofHours(1), LocalDateTime.now());
        Task subTask2 = new Task(2, "Task2", Status.NEW, "desc2", Duration.ofHours(1), LocalDateTime.now());
        historyManager.add(subTask1);
        historyManager.add(subTask2);
        assertEquals(historyManager.getHistory().size(), 1);
    }

    @Test
    void testIntersectionTrue() {
        Task subTask1 = new Task(1, "Task1", Status.NEW, "desc1", Duration.ofHours(1), LocalDateTime.now().plusHours(2));
        Task subTask2 = new Task(2, "Task2", Status.NEW, "desc2", Duration.ofHours(1), LocalDateTime.now());
        historyManager.add(subTask1);
        historyManager.add(subTask2);
        assertEquals(historyManager.getHistory().size(), 2);
    }
}
