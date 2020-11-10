package pucrs.domain;

public class MemoryPos {
    public String opCode;
    public String reg1;
    public String reg2;
    public int parameter;

    public String getOpcode() {
        return this.opCode;
    }

    public void setOpcode(String opCode) {
        this.opCode = opCode;
    }

    public String getReg1() {
        return this.reg1;
    }

    public void setReg1(String reg1) {
        this.reg1 = reg1;
    } 

    public String getReg2() {
        return this.reg2;
    }

    public void setReg2(String reg2) {
        this.reg2 = reg2;
    } 

    public int getParameter() {
        return this.parameter;
    }

    public void setParameter(int parameter) {
        this.parameter = parameter;
    }

    @Override
    public String toString() {
        if (opCode == "DATA") {
            return "MemoryPos{" +
                    "opCode='" + opCode + '\'' +
                    ", parameter=" + parameter +
                    '}';
        }
        return "MemoryPos{" +
                "opCode='" + opCode + '\'' +
                ", reg1='" + reg1 + '\'' +
                ", reg2='" + reg2 + '\'' +
                ", parameter=" + parameter +
                '}';
    }
}