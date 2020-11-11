package pucrs.components;

import pucrs.queues.*;

import java.util.Scanner;

public class Shell extends Thread {
    BlockedIOQueue blockedIOQueue = new BlockedIOQueue();
    FinishedQueue finishedQueue = new FinishedQueue();
    ReadyQueue readyQueue = new ReadyQueue();

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
                    ProcessManager.loadProgram(program);
                    Thread.sleep(100);
                    break;
                case 6:
                    consoleIO = new Thread(new ThreadStart(ConsoleIO.ExecutarConsoleIO));
                    consoleIO.Start();
                    semaforoShell.WaitOne();
                    SistemaOperacional.consoleIO.Interrupt();
                    break;

                case 7:
                    readyQueue.print();
                    break;
                case 8:
                    blockedIOQueue.print();
                    break;
                case 9:
                    finishedQueue.printFilaDeFinalizados();
                    break;
                case 0:
                    System.Environment.Exit(0);
                    return;
                default:
                    System.out.println("Opção não existe");
                    break;
            }
        }
    }
}
