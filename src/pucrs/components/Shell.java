package pucrs.components;

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
            System.out.println("==================================================");
            System.out.println("1- 10 primeiros números da sequência de Fibonacci");
            System.out.println("2- Quantidade de números da sequência de Fibonacci que deseja calcular");
            System.out.println("3- Calcular fatorial");
            System.out.println("4- Bubble sort para ordenar 5 valores");
            System.out.println("--------------------------------------------------");
            System.out.println("5 - Acessar fila de IO");
            System.out.println("6 - Exibir fila prontos");
            System.out.println("7 - Exibir fila bloqueados por IO");
            System.out.println("8 - Exibir fila de processos finalizados");
            System.out.println("0 - Shutdown");
            System.out.println("==================================================");
            System.out.println("Digite o número do programa que deseja executar:");

            Scanner in = new Scanner(System.in);
            int program = Integer.parseInt(in.next());

            System.out.println("--------------------------------------------");

            if (program > 0 && program <= 4) {
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
            } else if (program == 5) {
                Console console = new Console();
                console.start();
                try {
                    semaforoShell.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                console.interrupt();
            } else if (program == 6) {
                ReadyQueue.print();
            } else if (program == 7) {
                BlockedIOQueue.print();
            } else if (program == 8) {
                FinishedQueue.print();
            } else if (program == 0) {
                System.exit(0);
            } else {
                System.out.println("Opção inválida");
            }
        }
    }
}
