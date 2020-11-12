package pucrs.components;

import pucrs.domain.RequestIOConsole;
import pucrs.domain.MemoryPos;
import pucrs.domain.ProcessControlBlock;
import pucrs.exceptions.ImproperAccessException;
import pucrs.routines.TreatmentRoutineIO;

import java.util.concurrent.Semaphore;

public class CPU {

    public static Semaphore noCPU = new Semaphore(1);

    public static void execute(ProcessControlBlock pcb) throws Exception {
        pcb.setState(ProcessControlBlock.State.RUNNING);
        int commandsCount = 0;

        // Mostrando o processo rodando
        System.out.println("Process Id: " + pcb.getProcessID());
        System.out.println("State: " + pcb.getState());

        String value = "";
        boolean logicalResult = false;
        int memoryPosition = 0;
        int pc, reg;
        MemoryPos currentLine = MemoryManager.memory[pcb.getPc()];
        MemoryPos memoryPos;

        try {
            while (true) {
                if (TimerCPU.verifyTimeFact(commandsCount)) {

                    pcb.setState(ProcessControlBlock.State.WAITING);
                    TreatmentRoutineIO.treatTimerInterruption(pcb);
                    break;
                }

                if (currentLine.getOpcode().equals("STOP")) {
                    System.out.println("entrando na rotina de finalização");
                    TreatmentRoutineIO.finishProcess(pcb);
                    break;
                }

                //IO
                if (currentLine.getOpcode().equals("TRAP")) {
                    System.out.println("Entrando na rotina de tratamento de IO");

                    String operation = currentLine.getReg1();

                    value = currentLine.getReg2();
                    memoryPosition = MemoryManager.calculatesMemoryAddress(pcb, pcb.getRegisters().get(value));

                    if (operation.contains("1") || operation.contains("2")) {
                        throw new Exception("O valor " + operation + "é inválido para operação de IO. Somente é aceito '1' ou '2' como argumento.");
                    }

                    pcb.setPc(pcb.getPc()+1);

                    //Read
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

                    switch (currentLine.getOpcode()) {
                        // faz PC pular direto pra uma linha k
                        // JMP 12
                        case "JMP":
                            pcb.setPc(MemoryManager.calculatesMemoryAddress(pcb, currentLine.getParameter()));
                            break;

                        // faz PC pular direto pra linha contida no registrador r
                        // JMPI r1
                        case "JMPI":
                            pcb.setPc(MemoryManager.calculatesMemoryAddress(pcb, pcb.getRegisters().get(currentLine.getReg1())));
                            break;

                        // faz PC pular direto pra linha contida no registrador rx, caso ry seja maior que 0
                        // JMPIG rx,ry
                        case "JMPIG":
                            if (pcb.getRegisters().get(currentLine.getReg2()) > 0) {
                                pcb.setPc(MemoryManager.calculatesMemoryAddress(pcb, pcb.getRegisters().get(currentLine.getReg1())));
                                currentLine = MemoryManager.memory[pcb.getPc()];
                            } else {
                                pcb.setPc(pcb.getPc()+1);
                                currentLine = MemoryManager.memory[pcb.getPc()];
                            }
                            break;

                        // faz PC pular direto pra linha contida no registrador rx, caso ry seja menor que 0
                        // JMPIL rx,ry
                        case "JMPIL":
                            if (pcb.getRegisters().get(currentLine.getReg2()) < 0) {
                                pcb.setPc(MemoryManager.calculatesMemoryAddress(pcb, pcb.getRegisters().get(currentLine.getReg1())));
                                currentLine = MemoryManager.memory[pcb.getPc()];
                            } else {
                                pc = pcb.getPc();
                                pc++;
                                pcb.setPc(pc);
                                currentLine = MemoryManager.memory[pcb.getPc()];
                            }
                            break;

                        // faz PC pular direto pra linha contida no registrador rx, caso ry igual a 0
                        // JMPIE rx,ry
                        case "JMPIE":
                            if (pcb.getRegisters().get(currentLine.getReg2()) == 0) {
                                pcb.setPc(MemoryManager.calculatesMemoryAddress(pcb, pcb.getRegisters().get(currentLine.getReg1())));
                                currentLine = MemoryManager.memory[pcb.getPc()];
                            } else {
                                pc = pcb.getPc();
                                pc++;
                                pcb.setPc(pc);
                                currentLine = MemoryManager.memory[pcb.getPc()];
                            }
                            break;

                        // realiza a soma imediata de um valor k no registrador r
                        //ADDI r1,1
                        case "ADDI":
                            reg = pcb.getRegisters().get(currentLine.getReg1());
                            reg += currentLine.getParameter();
                            pcb.getRegisters().put(currentLine.getReg1(), reg);
                            pc = pcb.getPc();
                            pc++;
                            pcb.setPc(pc);
                            break;

                        // realiza a subtração imediata de um valor k no registrador r
                        //SUBI r1,1
                        case "SUBI":
                            reg = pcb.getRegisters().get(currentLine.getReg1());
                            reg -= currentLine.getParameter();
                            pcb.getRegisters().put(currentLine.getReg1(), reg);
                            pc = pcb.getPc();
                            pc++;
                            pcb.setPc(pc);
                            currentLine = MemoryManager.memory[pcb.getPc()];
                            break;

                        // carrega um valor k em um registrador
                        // LDI r1,10
                        case "LDI":
                            pcb.getRegisters().put(currentLine.getReg1(), currentLine.getParameter());
                            pc = pcb.getPc();
                            pc++;
                            pcb.setPc(pc);
                            currentLine = MemoryManager.memory[pcb.getPc()];
                            break;

                        // carrega um valor da memoria em um registrador
                        // LDD r1,[50]
                        case "LDD":
                            value = currentLine.getReg2();
                            memoryPosition = MemoryManager.calculatesMemoryAddress(pcb, Integer.parseInt(value));

                            if (MemoryManager.memory[memoryPosition].getOpcode().equals("DATA")) {
                                pcb.getRegisters().put(currentLine.getReg1(), MemoryManager.memory[memoryPosition].getParameter());
                            } else {
                                throw new Exception("Não é possivel ler dados de uma posição de memória com OPCode diferente de [DATA]. Encerrando execução");
                            }

                            pcb.setPc(pcb.getPc()+1);
                            currentLine = MemoryManager.memory[pcb.getPc()];
                            break;

                        // guarda na memoria um valor contido no registrador r
                        // STD [52],r1
                        case "STD":
                            value = currentLine.getReg1();
                            memoryPosition = MemoryManager.calculatesMemoryAddress(pcb, Integer.parseInt(value));
                            memoryPos = new MemoryPos();
                            memoryPos.setOpcode("DATA");
                            memoryPos.setParameter(pcb.getRegisters().get(currentLine.getReg2()));

                            MemoryManager.memory[memoryPosition] = memoryPos;

                            pc = pcb.getPc();
                            pc++;
                            pcb.setPc(pc);
                            currentLine = MemoryManager.memory[pcb.getPc()];
                            break;

                        // faz a operaçao: rx = rx + ry
                        // ADD rx,ry
                        case "ADD":
                            reg = pcb.getRegisters().get(currentLine.getReg1());
                            reg += pcb.getRegisters().get(currentLine.getReg2());
                            pcb.getRegisters().put(currentLine.getReg1(), reg);

                            pc = pcb.getPc();
                            pc++;
                            pcb.setPc(pc);
                            break;

                        // faz a operaçao: rx = rx - ry
                        // SUB rx,ry
                        case "SUB":
                            reg = pcb.getRegisters().get(currentLine.getReg1());
                            reg -= pcb.getRegisters().get(currentLine.getReg2());
                            pcb.getRegisters().put(currentLine.getReg1(), reg);

                            pc = pcb.getPc();
                            pc++;
                            pcb.setPc(pc);
                            break;

                        // faz a operaçao: rx = rx * ry
                        // MULT rx,ry
                        case "MULT":
                            reg = pcb.getRegisters().get(currentLine.getReg1());
                            reg *= pcb.getRegisters().get(currentLine.getReg2());
                            pcb.getRegisters().put(currentLine.getReg1(), reg);

                            pc = pcb.getPc();
                            pc++;
                            pcb.setPc(pc);
                            currentLine = MemoryManager.memory[pcb.getPc()];
                            break;

                        // faz a operaçao: rx = rx AND k
                        // AND rx,k
                        case "ANDI":
                            if (pcb.getRegisters().get(currentLine.getReg1()) == 0 || pcb.getRegisters().get(currentLine.getReg1()) == 1) {
                                logicalResult = currentLine.getParameter() == 1 ||
                                        pcb.getRegisters().get(currentLine.getReg1()) == 1;
                                pcb.getRegisters().put(currentLine.getReg1(), logicalResult ? 1 : 0);
                            } else {
                                throw new Exception("O registrador não contém um valor lógico válido");
                            }

                            pcb.setPc(pcb.getPc()+1);
                            break;

                        // faz a operaçao: rx = rx OR k
                        // OR rx,k
                        case "ORI":
                            if (pcb.getRegisters().get(currentLine.getReg1()) == 0 || pcb.getRegisters().get(currentLine.getReg1()) == 1) {
                                logicalResult = currentLine.getParameter() == 1 ||
                                        pcb.getRegisters().get(currentLine.getReg1()) == 1;
                                pcb.getRegisters().put(currentLine.getReg1(), logicalResult ? 1 : 0);
                            } else {
                                throw new Exception("O registrador não contém um valor lógico válido");
                            }

                            pcb.setPc(pcb.getPc()+1);
                            break;

                        // carrega em rx o dado contido na posiçao de memoria indicada por ry
                        // LDX rx,[ry]
                        case "LDX":
                            value = currentLine.getReg2();
                            memoryPosition = MemoryManager.calculatesMemoryAddress(pcb, pcb.getRegisters().get(value));

                            pcb.getRegisters().put(currentLine.getReg1(), MemoryManager.memory[memoryPosition].getParameter());
                            pcb.setPc(pcb.getPc()+1);
                            break;

                        // guarda na posição de memoria rx o dado contido em ry
                        // STX [rx],ry
                        case "STX":
                            value = currentLine.getReg1();
                            memoryPosition = MemoryManager.calculatesMemoryAddress(pcb, pcb.getRegisters().get(value));

                            memoryPos = new MemoryPos();
                            memoryPos.setOpcode("DATA");
                            memoryPos.setParameter(pcb.getRegisters().get(currentLine.getReg2()));
                            MemoryManager.memory[memoryPosition] = memoryPos;

                            pcb.setPc(pcb.getPc()+1);
                            break;

                        // troca os valores dos registradores; r7←r3, r6←r2, r5←r1, r4←r0
                        // SWAP
                        case "SWAP":
                            pcb.getRegisters().put("r4", pcb.getRegisters().get("r0"));
                            pcb.getRegisters().put("r5", pcb.getRegisters().get("r1"));
                            pcb.getRegisters().put("r6", pcb.getRegisters().get("r2"));
                            pcb.getRegisters().put("r7", pcb.getRegisters().get("r3"));

                            pcb.setPc(pcb.getPc()+1);
                            break;

                        default:
                            throw new Exception("Não foi possível encontrar o comando " + currentLine.getOpcode());

                    }
                }
            }
        } catch (ImproperAccessException e) {
            TreatmentRoutineIO.treatImproperAccess(pcb);
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }

        CPU.noCPU.acquire();
    }

}