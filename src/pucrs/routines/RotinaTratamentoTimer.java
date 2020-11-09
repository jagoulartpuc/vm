package pucrs.routines;

import pucrs.components.Scheduler;
import pucrs.domain.ProcessControlBlock;
import pucrs.queues.FilaDeProntos;

public class RotinaTratamentoTimer {
    FilaDeProntos filaDeProntos = new FilaDeProntos();

    public void tratarInterrupcaoTimer(ProcessControlBlock pcb) {
        //Decidindo se o processo volta pra fila
        if (pcb.state == ProcessControlBlock.State.WAITING) {
            filaDeProntos.addProcess(pcb);
            System.out.println("Voltou pra fila de prontos.");
        }

        //Liberado o escalonador para puxar outro processo da fila de prontos, já
        //que o que estava rodando foi interrompido por timer
        Escalonador.semaforoEscalonador.Release();
        //Segue o flow de volta no escalonador

        //Console.WriteLine("liberei o escalonador");
    }
}