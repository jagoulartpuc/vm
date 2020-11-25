package pucrs.components;

import pucrs.OpSystem;
import pucrs.domain.MemoryPos;
import pucrs.domain.RequestIOConsole;
import pucrs.queues.RequestConsoleQueue;
import pucrs.routines.TreatmentRoutineIO;

import java.util.Scanner;

public class Console extends Thread {

    public void run() {
        Scanner in = new Scanner(System.in);
        if (RequestConsoleQueue.count() != 0) {
            RequestIOConsole request = RequestConsoleQueue.remove();

            int adress = request.getAdress();

            if (request.getIOOperation() == RequestIOConsole.IOType.READ) {
                System.out.println("O processo " + request.getProcessID() + " está solicitando leitura de um valor que será salvo na posição de memória " + adress);
                System.out.println("Digite o valor para ser salvo:");

                int value = Integer.parseInt(in.next());

                MemoryPos memoryPos = new MemoryPos();
                memoryPos.setOpcode("DATA");
                memoryPos.setParameter(value);
                MemoryManager.memory[adress] = memoryPos;

                System.out.println("Valor salvo com sucesso na memória!");
            } else {
                int value = MemoryManager.memory[adress].getParameter();
                System.out.println("O processo " + request.getProcessID() + "solicitou o valor no endereço de memória " + adress + "e encontrou o valor " + value);
            }
            TreatmentRoutineIO.treatReturn(request.getProcessID());
        }

        System.out.println("Não há pedidos de IO para serem processados");
        OpSystem.semaforoShell.release();

    }
}
