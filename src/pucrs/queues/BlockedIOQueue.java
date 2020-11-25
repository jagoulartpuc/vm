package pucrs.queues;

import pucrs.domain.ProcessControlBlock;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class BlockedIOQueue {
    private static Queue<ProcessControlBlock> blokedQueue  = new LinkedBlockingQueue<>();

    public synchronized static void add(ProcessControlBlock pcb) {
        blokedQueue.add(pcb);

        System.out.println("Adicionou o processo " + pcb.getProcessID() + " a fila de bloqueados por IO.");
    }

    public synchronized static ProcessControlBlock remove(String pcbID) {
        try {
            for(ProcessControlBlock pcb : blokedQueue) {
                if(pcb.getProcessID() == pcbID) {
                    return blokedQueue.remove();
                }
            }
        }
        catch(NoSuchElementException e) {
            System.out.println("O process id numero [" + pcbID + "] não existe na fila de processos bloqueados.");
        }

        return null;
    }

    public synchronized static int count() {
        return blokedQueue.size();
    }

    public synchronized static void print() {
        if (blokedQueue.size() == 0) {
            System.out.println("Não há processos bloqueados por IO no momento.");
        } else {
            System.out.println("Printando fila de processos bloqueados:");

            for(ProcessControlBlock pcb : blokedQueue) {
                System.out.println("Process Id: {" + pcb.getProcessID() + "} | State: {" + pcb.getState() + "} | OffSet: {" + pcb.getOffSet() + "} | EndereçoLimite: {" + pcb.getLimitAdress() + "}");
            }
        }
    }
}