package pucrs.queues;

import pucrs.domain.ProcessControlBlock;
import java.util.*;
import java.util.concurrent.Semaphore;

public class ReadyQueue {
    private static Queue<ProcessControlBlock> readyQueue = new LinkedList<>();

    public static void add(ProcessControlBlock pcb) {
        readyQueue.add(pcb);
        System.out.println("Adicionou o processo " + pcb.getProcessID() + " a fila de prontos com status " + pcb.getState());
    }

    public static ProcessControlBlock remove() {
        return readyQueue.remove();
    }

    public static int count() {
        try {
            return readyQueue.size();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void print() {
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