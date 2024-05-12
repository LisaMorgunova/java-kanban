package service;

import model.Task;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryHistoryManager implements HistoryManager {
    private final Set<Task> history = new TreeSet(new Comparator<Task>() {
        @Override
        public int compare(Task s1, Task s2) {
            int res1 = s1.getStartTime().isBefore(s2.getStartTime()) ? -1 : 1;
            int res2 = s2.getStartTime().isBefore(s1.getStartTime()) ? 1 : -1;
            return res1 + res2;
        }
    });

    public List<Task> getPrioritizedTasks () {
        return history.stream().collect(Collectors.toList());
    }

    @Override
    public void add(Task task) {
        if (intersection(task) || history.size() == 10) {
            return;
        }
        history.add(task);
    }

    private boolean intersection(Task task) {
        for (Task existingTask : history) {
            if (existingTask.getEndTime().isAfter(task.getStartTime()) && existingTask.getStartTime().isBefore(task.getEndTime())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Task> getHistory() {
        return new LinkedList<>(history);
    }

    @Override
    public void remove(int id) {
        Iterator<Task> iterator = history.iterator();
        while (iterator.hasNext()) {
            Task task = iterator.next();
            if (task.getId() == id) {
                iterator.remove();
                break;
            }
        }
    }

    @Override
    public void clear() {
        history.clear();
    }
}
