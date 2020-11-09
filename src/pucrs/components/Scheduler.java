package pucrs.components;

import pucrs.domain.ProcessControlBlock;
import pucrs.queues.FilaDeProntos;

import java.util.concurrent.Semaphore;

public class Scheduler extends Thread {

    private Semaphore semaphore = new Semaphore(1);

    public void run() {
        while (true) {
            if (FilaDeProntos.contarProcessos() != 0) {
                ProcessControlBlock pcb = FilaDeProntos.dequeueProcess();
                Thread cpuProcess = new Thread(() = > CPU.ExecutarCPU(pcb));
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                cpuProcess.start();
                semaforoEscalonador.WaitOne();
                CPU.semCPU.Release();
            }
        }
    }

    public static void printRegistradoresEMemoria(ProcessControlBlock pcb) {
        System.out.println("--------------------------------------------");
        System.out.println("DADOS DO PROCESSO:" + pcb.processID);

        System.out.println("Valores finais dos registradores:\n");

        for (Item item: pcb.Registradores) {
            System.out.println("--------------------------------------------");

            System.out.println("Registrador " + item.Key - item.Value);
        }

        System.out.println("--------------------------------------------");

        System.out.println("Status finais das posições de memória (não nulas) da particao");

        for (int i = pcb.offSet; i < pcb.enderecoLimite; i++) {
            if (GerenteDeMemoria.Memoria[i] == null) {
                continue;
            }

            System.out.println($"Posição de memória [{i}]:");
            System.out.println(GerenteDeMemoria.Memoria[i].ToString() + "\n");
        }
    }
}
