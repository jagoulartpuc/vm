package pucrs.domain;

public class Partition {
    public Status status;

    public Partition() {
        status = Status.DEALLOCATED;
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
    
    public enum Status {
        ALLOCATED,
        DEALLOCATED
    }
}
