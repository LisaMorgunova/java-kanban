package service;

import model.Task;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;
import java.util.stream.Collectors;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> history = new ArrayList<>();

    private Node first;
    private Node last;

    private static class Node {
            Task item;
            Node next;
            Node prev;

            Node(Node prev, Task element, Node next) {
                this.item = element;
                this.next = next;
                this.prev = prev;
            }
        }

        private void linkLast(Task task) {
            final Node l = last;
            final Node newNode = new Node(l, task, null);
            last = newNode;
            if (l == null) {
                first = newNode;
            } else {
                l.next = newNode;
            }
        }

    public List<Task> getPrioritizedTasks() {
        return history.stream().collect(Collectors.toList());
    }
    @Override
    public void add(Task task) {
        if (history.size() >= 10) {
            history.remove(0);
        }
        history.add(task);
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
            last = null;
            first = null;
        }
    }
