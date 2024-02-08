package model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subTaskIds = new ArrayList<>();

    public Epic(String name, String description, Status status) { // Исправлен порядок и типы аргументов
        super(name, status, description);
    }

    public List<Integer> getSubTaskIds() {
        return new ArrayList<>(subTaskIds);
    }

    public void addSubTaskId(int id) {
        subTaskIds.add(id);
    }

    public void removeSubTaskId(Integer id) {
        subTaskIds.remove(id);
    }
}
