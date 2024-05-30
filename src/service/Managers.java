package service;

public class Managers {
    public static TaskManager getDefaultTaskManager() {
        return new InMemoryTaskManager(getDefaultHistoryManager());
    }

    public static HistoryManager getDefaultHistoryManager() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getInMemoryTaskManager() {
        HistoryManager historyManager = getDefaultHistoryManager();
        return new InMemoryTaskManager(historyManager);
    }
}