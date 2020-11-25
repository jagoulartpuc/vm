package pucrs.components;

import pucrs.domain.RequestIOConsole;
import pucrs.domain.MemoryPos;
import pucrs.domain.ProcessControlBlock;
import pucrs.exceptions.ImproperAccessException;
import pucrs.routines.TreatmentRoutineIO;

import java.util.concurrent.Semaphore;

import static pucrs.util.Util.removeParams;

public class CPU extends Thread {

    public static Semaphore semaphoreCPU = new Semaphore(0);
    public static ProcessControlBlock pcb;
    private static int commandsCount = 0;
    private String value;
    private boolean logicalResult;
    private int memoryPosition, reg;
    private MemoryPos currentLine;
    private MemoryPos memoryPos;
    private final int TIME_FACT = 30;


    public static void setPcb(ProcessControlBlock pcb) {
        commandsCount = 0;
        CPU.pcb = pcb;
    }

    public void run() {
        while (true) {
            try {
                CPU.semaphoreCPU.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            pcb.setState(ProcessControlBlock.State.RUNNING);
            System.out.println("Process Id: " + pcb.getProcessID());
            System.out.println("State: " + pcb.getState());
            try {
                execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void execute()  {
        try {
            while (true) {
                currentLine = MemoryManager.memory[pcb.getPc()];

                //Timer CPU
                if (commandsCount == TIME_FACT) {
                    TreatmentRoutineIO.treatTimerInterruption(pcb);
                    break;
                }

                if (currentLine.getOpcode().equals("STOP")) {
                    System.out.println("Entrando na rotina de finalização");
                    ProcessManager.finishProcess(pcb);
                    break;
                }

                if (currentLine.getOpcode().equals("TRAP")) {
                    System.out.println("Entrando na rotina de tratamento de IO");

                    String operation = removeParams(currentLine.getReg1());

                    value = removeParams(currentLine.getReg2());
                    memoryPosition = MemoryManager.calculatesMemoryAddress(pcb, pcb.getRegisters().get(value));

                    if (!(operation.contains("1") || operation.contains("2"))) {
                        throw new Exception("O valor " + operation + " é inválido para operação de IO. Somente é aceito '1' ou '2' como argumento.");
                    }

                    pcb.incrementPc();

                    if (operation.equals("1")) {
                        TreatmentRoutineIO.treatRequest(pcb, RequestIOConsole.IOType.READ, memoryPosition);
                        break;
                    } else {
                        TreatmentRoutineIO.treatRequest(pcb, RequestIOConsole.IOType.WRITE, memoryPosition);
                        break;
                    }
                } else {
                    currentLine = MemoryManager.memory[pcb.getPc()];
                    commandsCount++;
                    //System.out.println(commandsCount);

                    switch (currentLine.getOpcode()) {
                        case "JMP":
                            pcb.setPc(MemoryManager.calculatesMemoryAddress(pcb, currentLine.getParameter()));
                            break;
                        case "JMPI":
                            pcb.setPc(MemoryManager.calculatesMemoryAddress(pcb, pcb.getRegisters().get(currentLine.getReg1())));
                            break;
                        case "JMPIG":
                            if (pcb.getRegisters().get(currentLine.getReg2()) > 0) {
                                pcb.setPc(MemoryManager.calculatesMemoryAddress(pcb, pcb.getRegisters().get(currentLine.getReg1())));
                                currentLine = MemoryManager.memory[pcb.getPc()];
                            } else {
                                pcb.incrementPc();
                                currentLine = MemoryManager.memory[pcb.getPc()];
                            }
                            break;
                        case "JMPIL":
                            if (pcb.getRegisters().get(currentLine.getReg2()) < 0) {
                                pcb.setPc(MemoryManager.calculatesMemoryAddress(pcb, pcb.getRegisters().get(currentLine.getReg1())));
                                currentLine = MemoryManager.memory[pcb.getPc()];
                            } else {
                                pcb.incrementPc();
                                currentLine = MemoryManager.memory[pcb.getPc()];
                            }
                            break;
                        case "JMPIE":
                            if (pcb.getRegisters().get(currentLine.getReg2()) == 0) {
                                pcb.setPc(MemoryManager.calculatesMemoryAddress(pcb, pcb.getRegisters().get(currentLine.getReg1())));
                                currentLine = MemoryManager.memory[pcb.getPc()];
                            } else {
                                pcb.incrementPc();
                                currentLine = MemoryManager.memory[pcb.getPc()];
                            }
                            break;
                        case "ADDI":
                            reg = pcb.getRegisters().get(currentLine.getReg1());
                            reg += currentLine.getParameter();
                            pcb.getRegisters().put(currentLine.getReg1(), reg);
                            pcb.incrementPc();
                            break;
                        case "SUBI":
                            reg = pcb.getRegisters().get(currentLine.getReg1());
                            reg -= currentLine.getParameter();
                            pcb.getRegisters().put(currentLine.getReg1(), reg);
                            pcb.incrementPc();
                            currentLine = MemoryManager.memory[pcb.getPc()];
                            break;
                        case "LDI":
                            pcb.getRegisters().put(currentLine.getReg1(), currentLine.getParameter());
                            pcb.incrementPc();
                            currentLine = MemoryManager.memory[pcb.getPc()];
                            break;
                        case "LDD":
                            value = removeParams(currentLine.getReg2());
                            memoryPosition = MemoryManager.calculatesMemoryAddress(pcb, Integer.parseInt(value));

                            if (MemoryManager.memory[memoryPosition].getOpcode().equals("DATA")) {
                                pcb.getRegisters().put(currentLine.getReg1(), MemoryManager.memory[memoryPosition].getParameter());
                            } else {
                                throw new Exception("Não é possivel ler dados de uma posição de memória com OPCode diferente de [DATA]. Encerrando execução");
                            }

                            pcb.incrementPc();
                            currentLine = MemoryManager.memory[pcb.getPc()];
                            break;
                        case "STD":
                            value = removeParams(currentLine.getReg1());
                            memoryPosition = MemoryManager.calculatesMemoryAddress(pcb, Integer.parseInt(value));
                            memoryPos = new MemoryPos();
                            memoryPos.setOpcode("DATA");
                            memoryPos.setParameter(pcb.getRegisters().get(currentLine.getReg2()));

                            MemoryManager.memory[memoryPosition] = memoryPos;

                            pcb.incrementPc();
                            currentLine = MemoryManager.memory[pcb.getPc()];
                            break;
                        case "ADD":
                            reg = pcb.getRegisters().get(currentLine.getReg1());
                            reg += pcb.getRegisters().get(currentLine.getReg2());
                            pcb.getRegisters().put(currentLine.getReg1(), reg);

                            pcb.incrementPc();
                            break;
                        case "SUB":
                            reg = pcb.getRegisters().get(currentLine.getReg1());
                            reg -= pcb.getRegisters().get(currentLine.getReg2());
                            pcb.getRegisters().put(currentLine.getReg1(), reg);

                            pcb.incrementPc();
                            break;
                        case "MULT":
                            reg = pcb.getRegisters().get(currentLine.getReg1());
                            reg *= pcb.getRegisters().get(currentLine.getReg2());
                            pcb.getRegisters().put(currentLine.getReg1(), reg);

                            pcb.incrementPc();
                            currentLine = MemoryManager.memory[pcb.getPc()];
                            break;
                        case "ANDI":
                            if (pcb.getRegisters().get(currentLine.getReg1()) == 0 || pcb.getRegisters().get(currentLine.getReg1()) == 1) {
                                logicalResult = currentLine.getParameter() == 1 ||
                                        pcb.getRegisters().get(currentLine.getReg1()) == 1;
                                pcb.getRegisters().put(currentLine.getReg1(), logicalResult ? 1 : 0);
                            } else {
                                throw new Exception("O registrador não contém um valor lógico válido");
                            }

                            pcb.incrementPc();
                            break;
                        case "ORI":
                            if (pcb.getRegisters().get(currentLine.getReg1()) == 0 || pcb.getRegisters().get(currentLine.getReg1()) == 1) {
                                logicalResult = currentLine.getParameter() == 1 ||
                                        pcb.getRegisters().get(currentLine.getReg1()) == 1;
                                pcb.getRegisters().put(currentLine.getReg1(), logicalResult ? 1 : 0);
                            } else {
                                throw new Exception("O registrador não contém um valor lógico válido");
                            }

                            pcb.incrementPc();
                            break;
                        case "LDX":
                            value = removeParams(currentLine.getReg2());
                            memoryPosition = MemoryManager.calculatesMemoryAddress(pcb, pcb.getRegisters().get(value));

                            pcb.getRegisters().put(currentLine.getReg1(), MemoryManager.memory[memoryPosition].getParameter());
                            pcb.incrementPc();
                            break;
                        case "STX":
                            value = removeParams(currentLine.getReg1());
                            memoryPosition = MemoryManager.calculatesMemoryAddress(pcb, pcb.getRegisters().get(value));

                            memoryPos = new MemoryPos();
                            memoryPos.setOpcode("DATA");
                            memoryPos.setParameter(pcb.getRegisters().get(currentLine.getReg2()));
                            MemoryManager.memory[memoryPosition] = memoryPos;

                            pcb.incrementPc();
                            break;
                        case "SWAP":
                            pcb.getRegisters().put("r4", pcb.getRegisters().get("r0"));
                            pcb.getRegisters().put("r5", pcb.getRegisters().get("r1"));
                            pcb.getRegisters().put("r6", pcb.getRegisters().get("r2"));
                            pcb.getRegisters().put("r7", pcb.getRegisters().get("r3"));

                            pcb.incrementPc();
                            break;
                        default:
                            throw new Exception("Não foi possível encontrar o comando " + currentLine.getOpcode());

                    }
                }
            }
        } catch (ImproperAccessException e) {
            TreatmentRoutineIO.treatImproperAccess(pcb);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}