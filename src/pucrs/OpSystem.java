package pucrs;

import pucrs.components.Scheduler;
import pucrs.components.Shell;
import pucrs.components.ProcessManager;
import java.util.concurrent.Semaphore;

public class OpSystem {

    private ProcessManager processManager;

    private Shell shell = new Shell();

    private Scheduler scheduler = new Scheduler();

    private Thread consoleIO;

    public static Semaphore semaforoShell = new Semaphore(1);

    public OpSystem(int partitions) throws Exception {
        processManager = new ProcessManager(partitions);
    }

    public void executeShellAndScheduler() {
        shell.start();
        scheduler.start();
    }

    public Shell getShell() {
        return shell;
    }

    public void setShell(Shell shell) {
        this.shell = shell;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public Thread getConsoleIO() {
        return consoleIO;
    }

    public void setConsoleIO(Thread consoleIO) {
        this.consoleIO = consoleIO;
    }
}