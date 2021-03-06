package pucrs.queues;

import pucrs.domain.ProcessControlBlock;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class FinishedQueue {

    private static Queue<ProcessControlBlock> finishedQueue = new LinkedBlockingQueue<>();

    public synchronized static void add(ProcessControlBlock pcb) {
        finishedQueue.add(pcb);
        System.out.println("Adicionou o processo " + pcb.getProcessID() + " a fila de processos finalizados.");
    }

    public synchronized static ProcessControlBlock remove() {
        return finishedQueue.remove();
    }

    public synchronized static int count() {
        return finishedQueue.size();
    }

    public synchronized static void print() {
        if (finishedQueue.size() == 0) {
            System.out.println("Não há processos finalizados no momento.");
        } else {
            System.out.println("Printando fila de processos finalizados:");

            for(ProcessControlBlock pcb : finishedQueue) {
                System.out.println("Process Id: {" + pcb.getProcessID() + "} | State: {" + pcb.getState() + "} | OffSet: {" + pcb.getOffSet() + "} | EndereçoLimite: {" + pcb.getLimitAdress() + "}");
            }
        }
    }
}