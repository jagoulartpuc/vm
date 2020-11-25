package pucrs.queues;

import pucrs.domain.ProcessControlBlock;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

public class ReadyQueue {
    private static Queue<ProcessControlBlock> readyQueue = new LinkedBlockingQueue<>();

    public synchronized static void add(ProcessControlBlock pcb) {
        readyQueue.add(pcb);
        System.out.println("Adicionou o processo " + pcb.getProcessID() + " a fila de prontos com status " + pcb.getState());
    }

    public synchronized static ProcessControlBlock remove() {
        return readyQueue.remove();
    }

    public synchronized static int count() {
        try {
            return readyQueue.size();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public synchronized static void print() {
        if (readyQueue.size() == 0) {
            System.out.println("Não há processos prontos no momento.");
        } else {
            System.out.println("Printando fila de prontos:");

            for(ProcessControlBlock pcb : readyQueue) {
                System.out.println("Process Id: {" + pcb.getProcessID() + "} | State: {" + pcb.getState() + "} | OffSet: {" + pcb.getOffSet() + "} | EndereçoLimite: {" + pcb.getLimitAdress() + "}");
            }
        }
    }
}