package pucrs.domain;

import java.util.HashMap;
import java.util.Map;

public class ProcessControlBlock {

    private Map<String, Integer> registers;
    private String processID;
    private State state;
    private int pc = 0;
    private int offSet;
    private int limitAdress;
    private int actualPartition;

    public ProcessControlBlock(String processID, int actualPartition) {
        registers = new HashMap<>();

        registers.put("r0", 0);
        registers.put("r1", 0);
        registers.put("r2", 0);
        registers.put("r3", 0);
        registers.put("r4", 0);
        registers.put("r5", 0);
        registers.put("r6", 0);
        registers.put("r7", 0);

        this.processID = processID;
        this.state = State.READY;
        this.actualPartition = actualPartition;
    }

    public enum State {
        READY,
        RUNNING,
        WAITING,
        FINISHED,
        BLOCKED_BY_IO_OPERATION,
        ABORTED_DUE_TO_EXCEPTION
    }

    public Map<String, Integer> getRegisters() {
        return registers;
    }

    public void setRegisters(Map<String, Integer> registers) {
        this.registers = registers;
    }

    public String getProcessID() {
        return processID;
    }

    public void setProcessID(String processID) {
        this.processID = processID;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public int getPc() {
        return pc;
    }

    public void incrementPc() {
        pc++;
    }

    public void setPc(int pc) {
        this.pc = pc;
    }

    public int getOffSet() {
        return offSet;
    }

    public void setOffSet(int offSet) {
        this.offSet = offSet;
    }

    public int getLimitAdress() {
        return limitAdress;
    }

    public void setLimitAdress(int limitAdress) {
        this.limitAdress = limitAdress;
    }

    public int getActualPartition() {
        return actualPartition;
    }

}