package service;

import model.Task;
import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;


public class InMemoryHistoryManager implements HistoryManager {
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


    HashMap<Integer, Node> history = new HashMap<>();
    Node first;
    Node last;

    @Override
    public void add(Task task) {
        Node node = history.get(task.getId());
        if (node != null) removeNode(node);
        linkLast(task);
    }

    @Override
    public List<Task> getHistory() {
        List <Task> linkedList = new LinkedList<>();
        Node node = first;
        while (node != null) {
            linkedList.add(node.item);
            node = node.next;
        }
        return linkedList;
    }

    @Override
    public void remove(int id) {
        Node node = history.get(id);
        removeNode(node);
    }

    @Override
    public void clear() {
    last = null;
    first = null;
    history.clear();
    }

    void linkLast(Task task) {
        final Node l = last;
        final Node newNode = new Node(l, task, null);
        last = newNode;
        if (l == null) {
            first = newNode;
        } else {
            l.next = newNode;
        }
    }

    private void removeNode(Node node) {
        Node left = node.prev;
        Node right = node.next;
        if (left != null) {
            left.next = right;
        }
        if (right != null) {
            right.prev = left;
        }
        history.remove(node.item.getId());
    }
}