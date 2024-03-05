package service;

import model.Task;
import model.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        assertFalse(history.stream().anyMatch(task -> "Task 1".equals(task.getName())), "Первая задача должна быть удалена из истории.");
        assertTrue(history.contains(task11), "11-я задача должна быть в истории.");
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
    void addTest() {
        Task task = new Task("Task 1", Status.NEW, "New description 1");
        task.setId(1);
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        assertFalse(history.contains(task1), "Task 1 должен быть удален из истории.");
    }
}
