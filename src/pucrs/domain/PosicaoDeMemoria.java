package pucrs.domain;

public class PosicaoDeMemoria {
    public String opCode;
    public String reg1;
    public String reg2;
    public int parameter;

    //FAZER @OVERRIDE TOSTRING

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
}