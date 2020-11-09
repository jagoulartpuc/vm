package pucrs.domain;

public class PedidoConsoleIO {
    public String processID;
    public IOType ioOperation;
    public int endereco;
    public String value;

    public PedidoConsoleIO(String processID, IOType ioOperation, int endereco) {
        this.processID = processID;
        this.ioOperation = ioOperation;
        this.endereco = endereco;
    }

    public String getProcessID() {
        return this.processID;
    }

    public void setProcessID(String processID) {
        this.processID = processID;
    }
    
    public IOType getIOOperation() {
        return this.ioOperation;
    }

    public void setIOOperation(IOType ioOperation) {
        this.ioOperation = ioOperation;
    }

    public int getEndereco() {
        return this.endereco;
    }

    public void setEndereco(int endereco) {
        this.endereco = endereco;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public enum IOType {
        READ,
        WRITE
    }
}