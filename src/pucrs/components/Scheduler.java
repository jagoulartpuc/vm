package pucrs.components;

import pucrs.domain.ProcessControlBlock;
import pucrs.queues.ReadyQueue;

import java.util.concurrent.Semaphore;

public class Scheduler extends Thread {

    public static Semaphore semaphore = new Semaphore(0);

    public void run() {
        while (true) {
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (ReadyQueue.count() != 0) {
                ProcessControlBlock pcb = ReadyQueue.remove();
                CPU.setPcb(pcb);
                CPU.semaphoreCPU.release();
            }
        }
    }
}
