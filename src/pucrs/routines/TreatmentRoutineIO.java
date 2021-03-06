package pucrs.routines;

import pucrs.components.MemoryManager;
import pucrs.components.ProcessManager;
import pucrs.components.Scheduler;
import pucrs.domain.ProcessControlBlock;
import pucrs.domain.RequestIOConsole;
import pucrs.queues.BlockedIOQueue;
import pucrs.queues.FinishedQueue;
import pucrs.queues.ReadyQueue;
import pucrs.queues.RequestConsoleQueue;

public class TreatmentRoutineIO {

    public static void treatRequest(ProcessControlBlock pcb, RequestIOConsole.IOType operation, int endereco) {
        pcb.setState(ProcessControlBlock.State.BLOCKED_BY_IO_OPERATION);

        BlockedIOQueue.add(pcb);
        RequestIOConsole request = new RequestIOConsole(pcb.getProcessID(), operation, endereco);

        RequestConsoleQueue.add(request);
        Scheduler.semaphore.release();
    }

    public static void treatReturn(String pcbID) {
        ProcessControlBlock pcb = BlockedIOQueue.remove(pcbID);

        assert pcb != null;
        pcb.setState(ProcessControlBlock.State.WAITING);

        System.out.println("Desbloqueou o processo " + pcb.getProcessID());
        ReadyQueue.add(pcb);
    }

    public static void treatTimerInterruption(ProcessControlBlock pcb) {
        pcb.setState(ProcessControlBlock.State.READY);
        System.out.println("Foi interrompido e voltou pra fila de prontos.");
        ReadyQueue.add(pcb);
        Scheduler.semaphore.release();
    }

    public static void treatImproperAccess(ProcessControlBlock pcb) {
        System.out.println("Encaminhando para rotina de tratamento finalização por acesso indevido");
        System.out.println("O processo " + pcb.getProcessID() + "está sendo encerrado");

        pcb.setState(ProcessControlBlock.State.ABORTED_DUE_TO_EXCEPTION);
        FinishedQueue.add(pcb);

        MemoryManager.deallocatePartition(pcb.getActualPartition(), pcb.getOffSet(), pcb.getLimitAdress());

        Scheduler.semaphore.release();
    }
}
