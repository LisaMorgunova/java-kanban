package model;

import java.util.Date;

public class Task {
    private Integer id;
    private String name;
    private Status status;
    private String description;
    private Date startTime;
    private Date endTime;

    public Task(String name, Status status, String description) {
        this.name = name;
        this.status = status;
        this.description = description;
        this.startTime = new Date();
    }

    public Task() {
        this.id = id;
        this.name = name;
        this.status = status;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Task(String s, String string) {
    }

    public int getId() {
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

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;

        if (id != task.id) return false;
        if (!name.equals(task.name)) return false;
        if (status != task.status) return false;
        if (!description.equals(task.description)) return false;
        if (!startTime.equals(task.startTime)) return false;
        return endTime != null ? endTime.equals(task.endTime) : task.endTime == null;
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
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", description='" + description + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}

