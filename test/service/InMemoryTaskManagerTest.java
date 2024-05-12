package service;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager(new InMemoryHistoryManager());
    }
}
