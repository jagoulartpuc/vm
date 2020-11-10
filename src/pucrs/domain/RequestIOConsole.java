package pucrs.domain;

public class RequestIOConsole {
    private String processID;
    private IOType ioOperation;
    private int adress;
    private String value;

    public RequestIOConsole(String processID, IOType ioOperation, int adress) {
        this.processID = processID;
        this.ioOperation = ioOperation;
        this.adress = adress;
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

    public int getAdress() {
        return adress;
    }

    public void setAdress(int adress) {
        this.adress = adress;
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