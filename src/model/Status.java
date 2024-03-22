package model;

public enum Status {
    NEW, IN_PROGRESS, DONE;

    static Status getType(String s) {
        switch (s) {
            case "NEW":
                return Status.NEW;
            case "IN_PROGRESS":
                return Status.IN_PROGRESS;
            case "DONE":
                return Status.DONE;
        }
        return null;
    }
}