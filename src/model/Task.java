package model;

import java.time.LocalDateTime;

public class Task {
    private Integer id;
    private String name;
    private Status status;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Task(String name, Status status, String description) {
        this.name = name;
        this.status = status;
        this.description = description;
        this.startTime = LocalDateTime.now();
    }

    public Task() {
    }

    public Task(String name, String description) {
        this(name, Status.NEW, description);
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Status getStatus() { return status; }

    public void setStatus(Status status) { this.status = status; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;

        if (id != task.id) return false;
        if (!name.equals(task.name)) return false;
        if (status != task.status) return false;
        return description.equals(task.description);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + name.hashCode();
        result = 31 * result + status.hashCode();
        result = 31 * result + description.hashCode();
        result = 31 * result + startTime.hashCode();
        result = 31 * result + (endTime != null ? endTime.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        String task = "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", description='" + description + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
        return task.replaceAll("\r\n", "\n");
    }
}

