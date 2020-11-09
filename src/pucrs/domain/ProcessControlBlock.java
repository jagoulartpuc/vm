package pucrs.domain;

import java.util.Dictionary;
import java.util.Enumeration;

// é um bloco de informações de controle a respeito de um processo,
// ele basicamente representa um processo;
public class ProcessControlBlock {
    public Dictionary<String, Integer> registradores;
    public String processID;
    public State state;
    public int pc;
    public int offSet;
    public int enderecoLimite;
    public int particaoAtual;

    public ProcessControlBlock(String processID, int particaoAtual) {
        registradores = new Dictionary<String, Integer>() {
            @Override
            public int size() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public Enumeration<String> keys() {
                return null;
            }

            @Override
            public Enumeration<Integer> elements() {
                return null;
            }

            @Override
            public Integer get(Object key) {
                return null;
            }

            @Override
            public Integer put(String key, Integer value) {
                return null;
            }

            @Override
            public Integer remove(Object key) {
                return null;
            }
        };
        
        registradores.put("r0", 0);
        registradores.put("r1", 0);
        registradores.put("r2", 0);
        registradores.put("r3", 0);
        registradores.put("r4", 0);
        registradores.put("r5", 0);
        registradores.put("r6", 0);
        registradores.put("r7", 0);

        this.processID = processID;
        this.state = State.READY;
        this.particaoAtual = particaoAtual;
    }

    //COMO FAZER GET E SET PRO DICIONARIO???

    public String getProcessID() {
        return this.processID;
    }

    public void setProcessID(String processID) {
        this.processID = processID;
    }

    public State getState() {
        return this.state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public int getPc() {
        return this.pc;
    }

    public void setPc(int pc) {
        this.pc = pc;
    }

    public int getOffSet() {
        return this.offSet;
    }

    public void setOffSet(int offSet) {
        this.offSet = offSet;
    }

    public int getEnderecoLimite() {
        return this.enderecoLimite;
    }

    public void setEnderecoLimite(int enderecoLimite) {
        this.enderecoLimite = enderecoLimite;
    }

    public int getParticaoAtual() {
        return this.particaoAtual;
    }

    public void setParticaoAtual(int particaoAtual) {
        this.particaoAtual = particaoAtual;
    }

    public enum State
    {
        READY,
        RUNNING,
        WAITING,
        FINISHED,
        BLOCKED_BY_IO_OPERATION,
        ABORTED_DUE_TO_EXCEPTION
    }
}