package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private LocalDateTime endTime;

    private final List<Integer> subTaskIds = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, status, description);
        startTime = null;
        duration = null;
        endTime = null;
    }

    public List<Integer> getSubTaskIds() {
        return subTaskIds;
    }

    public void addSubTaskId(SubTask subTask) {
        subTaskIds.add(subTask.getId());
        if (startTime == null || startTime.isAfter(subTask.startTime)) {
            startTime = subTask.startTime;
        }
        if (endTime == null || endTime.isBefore(subTask.getEndTime())) {
            endTime = subTask.getEndTime();
        }
        duration = Duration.between(startTime, endTime);
    }

    public void removeSubTaskId(Integer id) {
        subTaskIds.remove(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Epic epic = (Epic) o;

        return subTaskIds.equals(epic.subTaskIds);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + subTaskIds.hashCode();
        return result;
    }
}
