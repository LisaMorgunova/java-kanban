package service;

import model.Task;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryHistoryManager implements HistoryManager {
    private final Set<Task> history = new TreeSet(new Comparator<Task>() {
        @Override
        public int compare(Task s1, Task s2) {
            return s1.getStartTime().isBefore(s2.getStartTime()) ? 1 : -1;
        }
    });

    public List<Task> getPrioritizedTasks () {
        return history.stream().collect(Collectors.toList());
    }

    @Override
    public void add(Task task) {
        if (intersection(task)) {
            return;
        }
        history.add(task);
    }

    private boolean intersection(Task task) {
        long count = history.stream().filter(o -> {
            if (o.getStartTime().isBefore(task.getStartTime()) && o.getEndTime().isAfter(task.getEndTime())) {
                return false;
            } else if (o.getStartTime().isBefore(task.getStartTime()) && o.getEndTime().isBefore(task.getEndTime())) {
                return false;
            } else if (o.getStartTime().isAfter(task.getStartTime()) && o.getEndTime().isAfter(task.getEndTime())) {
                return false;
            } else {
                return true;
            }
        }).count();
        return count != history.size();
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
