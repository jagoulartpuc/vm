package pucrs.domain;

public class ParticaoMemoria {
    Status status;

    public ParticaoMemoria() {
        status = Status.DESALOCADO;
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
    
    public enum Status {
        ALOCADO, 
        DESALOCADO
    }
}
