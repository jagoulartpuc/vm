package pucrs.components;

import pucrs.domain.ProcessControlBlock;
import pucrs.queues.ReadyQueue;

import java.util.concurrent.Semaphore;

public class Scheduler extends Thread {

    public static Semaphore semaphore = new Semaphore(1);

    public void run() {
        while (true) {
            if (ReadyQueue.count() != 0) {
                ProcessControlBlock pcb = ReadyQueue.remove();
                Thread cpuProcess = new Thread(() -> {
                    try {
                        CPU.execute(pcb);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                cpuProcess.start();
                semaphore.tryAcquire();
                CPU.noCPU.release();
            }
        }
    }

    public static void printRegistersAndMemory(ProcessControlBlock pcb) {
        System.out.println("--------------------------------------------");
        System.out.println("DADOS DO PROCESSO:" + pcb.getProcessID());

        System.out.println("Valores finais dos registradores:\n");

        for (String key: pcb.getRegisters().keySet()) {
            for (int value: pcb.getRegisters().values()) {
                System.out.println("--------------------------------------------");
                System.out.println("Registrador " + key  + ":" + value);
            }
        }

        System.out.println("--------------------------------------------");
        System.out.println("Status finais das posições de memória (não nulas) da particao");

        for (int i = pcb.getOffSet(); i < pcb.getLimitAdress(); i++) {
            if (MemoryManager.memory[i] == null) {
                continue;
            }

            System.out.println("Posição de memória " + i);
            System.out.println(MemoryManager.memory[i].toString());
        }
    }
}
