package pucrs.routines;

import pucrs.components.MemoryManager;
import pucrs.components.Scheduler;
import pucrs.domain.ProcessControlBlock;
import pucrs.queues.FinishedQueue;

public class TreatmentRoutineFin {
    
    public static void treatImproperAccess(ProcessControlBlock pcb) {
        System.out.println("Encaminhando para rotina de tratamento finalização por acesso indevido");
        System.out.println("O processo " + pcb.getProcessID() + "está sendo encerrado");

        pcb.setState(ProcessControlBlock.State.ABORTED_DUE_TO_EXCEPTION);
        FinishedQueue.add(pcb);

        MemoryManager.deallocatePartition(pcb.getActualPartition(), pcb.getOffSet(), pcb.getLimitAdress());

        Scheduler.semaphore.release();
    }

    public static void finishProcess(ProcessControlBlock pcb) {
        pcb.setState(ProcessControlBlock.State.FINISHED);
        System.out.println("Terminou de executar o processo " + pcb.getProcessID());

        FinishedQueue.add(pcb);
        MemoryManager.deallocatePartition(pcb.getActualPartition(), pcb.getOffSet(), pcb.getLimitAdress());
        Scheduler.semaphore.release();

    }
}