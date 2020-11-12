package pucrs;

import pucrs.components.Scheduler;
import pucrs.components.Shell;
import pucrs.components.ProcessManager;
import java.util.concurrent.Semaphore;

public class OpSystem {

    private Shell shell;

    private Scheduler scheduler;

    public static Semaphore semaforoShell = new Semaphore(1);

    public OpSystem(int partitions) throws Exception {
        ProcessManager processManager = new ProcessManager(partitions);
        shell = new Shell(processManager);
        scheduler = new Scheduler();
    }

    public void executeShellAndScheduler() {
        shell.start();
        scheduler.start();
    }
}