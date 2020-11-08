package pucrs.components;

import java.util.concurrent.Semaphore;

public class Scheduler extends Thread {

    private Semaphore semaphore = new Semaphore(1);

    public void run() {
        while (true) {
            if (FilaDeProntos.ContarProcessos() != 0) {
                ProcessControlBlock pcb = FilaDeProntos.DequeueProcess();
                Thread cpuProcess = new Thread(() = > CPU.ExecutarCPU(pcb));
                Thread.Sleep(2000);
                cpuProcess.Start();
                semaforoEscalonador.WaitOne();
                CPU.semCPU.Release();
            }
        }
    }

    public static void PrintRegistradoresEMemoria(ProcessControlBlock pcb) {
        System.out.println("--------------------------------------------");
        System.out.println("DADOS DO PROCESSO:" + pcb.ProcessID);

        System.out.println("Valores finais dos registradores:\n");

        for (Item item: pcb.Registradores) {
            System.out.println("--------------------------------------------");

            System.out.println("Registrador " + item.Key - item.Value);
        }

        System.out.println("--------------------------------------------");

        System.out.println("Status finais das posições de memória (não nulas) da particao");

        for (int i = pcb.OffSet; i < pcb.EnderecoLimite; i++) {
            if (GerenteDeMemoria.Memoria[i] == null) {
                continue;
            }

            System.out.println($"Posição de memória [{i}]:");
            System.out.println(GerenteDeMemoria.Memoria[i].ToString() + "\n");
        }
    }
}
