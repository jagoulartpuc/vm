package pucrs.routines;

import pucrs.components.Scheduler;
import pucrs.domain.ProcessControlBlock;
import pucrs.domain.RequestIOConsole;
import pucrs.queues.BlockedIOQueue;
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
        if (pcb.getState() == ProcessControlBlock.State.WAITING) {
            ReadyQueue.add(pcb);
            System.out.println("Voltou pra fila de prontos.");
        }

        Scheduler.semaphore.release();
    }
}
