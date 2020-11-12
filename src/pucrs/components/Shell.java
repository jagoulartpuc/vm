package pucrs.components;

import pucrs.OpSystem;
import pucrs.queues.*;

import java.util.Scanner;

import static pucrs.OpSystem.semaforoShell;

public class Shell extends Thread {

    private ProcessManager processManager;

    public Shell(ProcessManager processManager) {
        this.processManager = processManager;
    }

    public void run() {
        while (true) {
            System.out.println("                LISTA DE PROGRAMAS                ");
            System.out.println("--------------------------------------------------");
            System.out.println("1- 10 primeiros números da sequência de Fibonacci");
            System.out.println("2- Quantidade de números da sequência de Fibonacci que deseja calcular");
            System.out.println("3- Calcular fatorial");
            System.out.println("4- Bubble sort para ordenar 5 valores");
            System.out.println("5- Programa para testar acesso indevido");
            System.out.println("--------------------------------------------------");
            System.out.println("6 - Acessar fila de IO");
            System.out.println("7 - Printar fila prontos");
            System.out.println("8 - Printar fila bloqueados por IO");
            System.out.println("9 - Printar fila de processos finalizados");
            System.out.println("0 - Shutdown\n");
            System.out.println("Digite o número do programa que deseja executar:");

            Scanner in = new Scanner(System.in);
            int program = Integer.parseInt(in.next());

            System.out.println("--------------------------------------------");

            switch (program) {
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                    try {
                        processManager.loadProgram(program);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case 6:
                    Console console = new Console();
                    console.start();
                    semaforoShell.tryAcquire();
                    console.interrupt();
                    break;

                case 7:
                    ReadyQueue.print();
                    break;
                case 8:
                    BlockedIOQueue.print();
                    break;
                case 9:
                    FinishedQueue.print();
                    break;
                case 0:
                    System.exit(0);
                    return;
                default:
                    System.out.println("Opção não existe");
                    break;
            }
        }
    }
}
