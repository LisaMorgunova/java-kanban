package Model;

public class SubTask extends Task {
    private int epicId;

    public SubTask(String name, int epicId) {
        super(name);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }
    @Override
    public void calculateStatus() {
    }
}
