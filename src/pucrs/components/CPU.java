package pucrs.components;

import pucrs.domain.PedidoConsoleIO;
import pucrs.domain.PosicaoDeMemoria;
import pucrs.domain.ProcessControlBlock;
import pucrs.routines.RotinaTratamentoTimer;

import java.util.concurrent.Semaphore;

public class CPU {

    public Semaphore semCPU = new Semaphore(1);

    public static void ExecutarCPU(ProcessControlBlock pcb) {
        pcb.state = ProcessControlBlock.State.RUNNING;
        int CommandsCount = 0;

        // só pra mostrar que o processo ta RUNNING
        System.out.println($"\nProcess Id: {pcb.ProcessID} ; State: {pcb.State}\n");

        //System.out.println("\nTo na cpu");
        String value = "";
        boolean logicalResult = false;
        int memoryPosition = 0;

        PosicaoDeMemoria currentLine = GerenteDeMemoria.Memoria[pcb.pc];

        try {
            while (true) {
                //System.out.println($"Current OPCode: {currentLine.OPCode}");

                //Verificando interrupção por fatia de tempo
                if (TimerCPU.verifyTimeFact(CommandsCount)) {
                    //System.out.println($"atingi a fatia de tempo {CommandsCount}");

                    pcb.state = ProcessControlBlock.State.WAITING;
                    //System.out.println($"Status da pcb {pcb.State}");

                    //Trata interrupção ocorrida por TIMER
                    RotinaTratamentoTimer.tratarInterrupcaoTimer(pcb);
                    //Break para sair do while(true) e travar o semáforo da CPU lá embaixo
                    break;
                }

                if (currentLine.opCode == "STOP") {
                    System.out.println("entrando na rotina de finalização");
                    RotinaTratamentoFinalizacao.FinalizarProcesso(pcb); // NAO EXISTE ???
                    break;
                }

                //IO
                if (currentLine.opCode == "TRAP") {
                    System.out.println("Entrando na rotina de tratamento de IO");

                    String operation = currentLine.reg1.Trim(new char[]{'[', ']'});

                    value = currentLine.reg2.Trim(new char[]{'[', ']'});
                    memoryPosition = GerenteDeMemoria.CalculaEnderecoMemoria(pcb, pcb.registradores[value]);

                    //TRAP 1 [r1]
                    //TRAP 2 [50]
                    if (!new string[]{"1", "2"}.Contains(operation)) {
                        throw new ArgumentException($"O valor [{operation}] é inválido para operação de IO. Somente é aceito '1' ou '2' como argumento.");
                    }

                    // incrementa o PC pq senao nunca sai da linha TRAP
                    pcb.pc++;

                    //Read
                    if (operation == "1") {
                        RotinaTratamentoIO.TratarPedidoIO(pcb, PedidoConsoleIO.IOType.READ, memoryPosition);
                        break;
                    }
                    //Write
                    else {
                        RotinaTratamentoIO.TratarPedidoIO(pcb, PedidoConsoleIO.IOType.WRITE, memoryPosition);
                        break;
                    }
                } else {
                    //System.out.println($"entrei no swtich com {CommandsCount} comandos");

                    currentLine = GerenteDeMemoria.Memoria[pcb.pc];
                    CommandsCount++;

                    switch (currentLine.opCode) {
                        // faz PC pular direto pra uma linha k
                        // JMP 12
                        case "JMP":
                            pcb.pc = GerenteDeMemoria.CalculaEnderecoMemoria(pcb, Convert.ToInt32(currentLine.parameter));
                            break;

                        // faz PC pular direto pra linha contida no registrador r
                        // JMPI r1
                        case "JMPI":
                            pcb.pc = GerenteDeMemoria.CalculaEnderecoMemoria(pcb, Convert.ToInt32(pcb.registradores[currentLine.reg1]));
                            break;

                        // faz PC pular direto pra linha contida no registrador rx, caso ry seja maior que 0
                        // JMPIG rx,ry
                        case "JMPIG":
                            if (Convert.ToInt32(pcb.registradores[currentLine.reg2]) > 0) {
                                pcb.pc = GerenteDeMemoria.CalculaEnderecoMemoria(pcb, Convert.ToInt32(pcb.registradores[currentLine.reg1]));
                                currentLine = GerenteDeMemoria.Memoria[pcb.pc];
                            } else {
                                pcb.pc++;
                                currentLine = GerenteDeMemoria.Memoria[pcb.pc];
                            }
                            break;

                        // faz PC pular direto pra linha contida no registrador rx, caso ry seja menor que 0
                        // JMPIL rx,ry
                        case "JMPIL":
                            if (Convert.ToInt32(pcb.registradores[currentLine.reg2]) < 0) {
                                pcb.pc = GerenteDeMemoria.CalculaEnderecoMemoria(pcb, Convert.ToInt32(pcb.registradores[currentLine.reg1]));
                                currentLine = GerenteDeMemoria.Memoria[pcb.pc];
                            } else {
                                pcb.pc++;
                                currentLine = GerenteDeMemoria.Memoria[pcb.pc];
                            }
                            break;

                        // faz PC pular direto pra linha contida no registrador rx, caso ry igual a 0
                        // JMPIE rx,ry
                        case "JMPIE":
                            if (Convert.ToInt32(pcb.registradores[currentLine.reg2]) == 0) {
                                pcb.pc = GerenteDeMemoria.CalculaEnderecoMemoria(pcb, Convert.ToInt32(pcb.registradores[currentLine.reg1]));
                                currentLine = GerenteDeMemoria.Memoria[pcb.pc];
                            } else {
                                pcb.pc++;
                                currentLine = GerenteDeMemoria.Memoria[pcb.pc];
                            }
                            break;

                        // realiza a soma imediata de um valor k no registrador r
                        //ADDI r1,1
                        case "ADDI":
                            pcb.registradores[currentLine.reg1] += currentLine.parameter;

                            pcb.pc++;
                            break;

                        // realiza a subtração imediata de um valor k no registrador r
                        //SUBI r1,1
                        case "SUBI":
                            pcb.registradores[currentLine.reg1] -= currentLine.parameter;

                            pcb.pc++;
                            currentLine = GerenteDeMemoria.Memoria[pcb.pc];
                            break;

                        // carrega um valor k em um registrador
                        // LDI r1,10
                        case "LDI":
                            pcb.registradores[currentLine.reg1] = currentLine.parameter;

                            pcb.pc++;
                            currentLine = GerenteDeMemoria.Memoria[pcb.pc];
                            break;

                        // carrega um valor da memoria em um registrador
                        // LDD r1,[50]
                        case "LDD":
                            value = currentLine.reg2.Trim(new char[]{'[', ']'});
                            memoryPosition = GerenteDeMemoria.CalculaEnderecoMemoria(pcb, Convert.ToInt32(value));

                            if (GerenteDeMemoria.Memoria[memoryPosition].OPCode == "DATA") {
                                pcb.registradores[currentLine.reg1] = GerenteDeMemoria.Memoria[memoryPosition].parameter;
                            } else {
                                throw new InvalidOperationException("Não é possivel ler dados de uma posição de memória com OPCode diferente de [DATA]. Encerrando execução");
                            }

                            pcb.pc++;
                            currentLine = GerenteDeMemoria.Memoria[pcb.pc];
                            break;

                        // guarda na memoria um valor contido no registrador r
                        // STD [52],r1
                        case "STD":
                            value = currentLine.reg1.Trim(new char[]{'[', ']'});
                            memoryPosition = GerenteDeMemoria.CalculaEnderecoMemoria(pcb, Convert.ToInt32(value));

                            GerenteDeMemoria.Memoria[memoryPosition] = new PosicaoDeMemoria
                        {
                            OPCode = "DATA",
                                    parameter = pcb.registradores[currentLine.reg2]
                        } ;

                        pcb.pc++;
                        currentLine = GerenteDeMemoria.Memoria[pcb.pc];
                        break;

                        // faz a operaçao: rx = rx + ry
                        // ADD rx,ry
                        case "ADD":
                            pcb.registradores[currentLine.reg1] += pcb.registradores[currentLine.reg2];

                            pcb.pc++;
                            break;

                        // faz a operaçao: rx = rx - ry
                        // SUB rx,ry
                        case "SUB":
                            pcb.registradores[currentLine.reg1] -= pcb.registradores[currentLine.reg2];

                            pcb.pc++;
                            break;

                        // faz a operaçao: rx = rx * ry
                        // MULT rx,ry
                        case "MULT":
                            pcb.registradores[currentLine.reg1] *= pcb.registradores[currentLine.reg2];

                            pcb.pc++;
                            currentLine = GerenteDeMemoria.Memoria[pcb.pc];
                            break;

                        // faz a operaçao: rx = rx AND k
                        // AND rx,k
                        case "ANDI":
                            if (new int[]{0, 1}.Contains(pcb.registradores[currentLine.reg1])) {
                                logicalResult = Convert.ToBoolean(currentLine.parameter) &&
                                        Convert.ToBoolean(pcb.registradores[currentLine.reg1]);

                                pcb.registradores[currentLine.reg1] = Convert.ToInt32(logicalResult);
                            } else {
                                throw new ArgumentException("O registrador não contém um valor lógico válido");
                            }

                            pcb.pc++;
                            break;

                        // faz a operaçao: rx = rx OR k
                        // OR rx,k
                        case "ORI":
                            if (new int[]{0, 1}.Contains(pcb.registradores[currentLine.reg1])) {
                                logicalResult = Convert.ToBoolean(currentLine.parameter) ||
                                        Convert.ToBoolean(pcb.registradores[currentLine.reg1]);

                                pcb.registradores[currentLine.reg1] = Convert.ToInt32(logicalResult);
                            } else {
                                throw new ArgumentException("O registrador não contém um valor lógico válido");
                            }

                            pcb.pc++;
                            break;

                        // carrega em rx o dado contido na posiçao de memoria indicada por ry
                        // LDX rx,[ry]
                        case "LDX":
                            value = currentLine.reg2.Trim(new char[]{'[', ']'});
                            memoryPosition = GerenteDeMemoria.CalculaEnderecoMemoria(pcb, pcb.registradores[value]);

                            pcb.registradores[currentLine.reg1] = GerenteDeMemoria.Memoria[memoryPosition].parameter;

                            pcb.pc++;
                            break;

                        // guarda na posição de memoria rx o dado contido em ry
                        // STX [rx],ry
                        case "STX":
                            value = currentLine.reg1.Trim(new char[]{'[', ']'});
                            memoryPosition = GerenteDeMemoria.CalculaEnderecoMemoria(pcb, pcb.registradores[value]);

                            GerenteDeMemoria.Memoria[memoryPosition] = new PosicaoDeMemoria
                        {
                            OPCode = "DATA",
                                    parameter = pcb.registradores[currentLine.reg2]
                        } ;

                        pcb.pc++;
                        break;

                        // troca os valores dos registradores; r7←r3, r6←r2, r5←r1, r4←r0
                        // SWAP
                        case "SWAP":
                            pcb.registradores["r4"] = pcb.registradores["r0"];

                            pcb.registradores["r5"] = pcb.registradores["r1"];

                            pcb.registradores["r6"] = pcb.registradores["r2"];

                            pcb.registradores["r7"] = pcb.registradores["r3"];

                            pcb.pc++;
                            break;

                        default:
                            throw new ArgumentException($"Não foi possível encontrar o comando [{currentLine.OPCode}]");

                    }
                }
            }
        } catch (AcessoIndevidoException) {
            RotinaTratamentoFinalizacao.TratamentoAcessoIndevido(pcb);
        } catch (DivideByZeroException) {
            RotinaTratamentoFinalizacao.TratamentoDivisaoPorZero(pcb);
        } catch (Exception ex) {
            //Caso erro genérico
            throw new Exception($"Ocorreu um erro ao executar o comando na VM: [{ex.Message}]");
        }

        //System.out.println("indo interromper a CPU");

        //Bloqueando a execução da CPU até que o escalonador libere a CPU
        //para estar pronta a executar um novo processo
        CPU.semCPU.WaitOne();
    }
}
}