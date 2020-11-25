package pucrs.components;

import pucrs.domain.ProcessControlBlock;
import pucrs.queues.FinishedQueue;
import pucrs.queues.ReadyQueue;

import java.util.Random;

public class ProcessManager {

    private final int MIN_PROCESS_ALLOWED = 4;
    private final int MAX_PROCESS_ALLOWED = 8;
    private MemoryManager memoryManager;
    private static int alives = 0;

    private int count = 0;

    public ProcessManager(int numeroParticoes) throws Exception {
        if (numeroParticoes > MAX_PROCESS_ALLOWED) {
            throw new Exception("O tamanho máximo de partições permitido para uma CPU é de " + MAX_PROCESS_ALLOWED + ". Encerrando execução.");
        }

        if (numeroParticoes < MIN_PROCESS_ALLOWED) {
            throw new Exception("O tamanho mínimo de partições permitido para uma CPU é de " + MIN_PROCESS_ALLOWED + ". Encerrando execução.");
        }

        memoryManager = new MemoryManager(numeroParticoes);
    }

    public void loadProgram(int programNumber) throws Exception {
        String filePath = "programs/P" + programNumber + ".txt";

        int particao = getRandomPartition();

        memoryManager.readFile(filePath, particao);

        count++;
        String processID = "P" + count;
        createPCB(processID, particao);
    }

    public int getRandomPartition() throws Exception {
        if (memoryManager.isFullMemory()) {
            throw new Exception("Impossível alocar mais processos na memória pois todas partições já estão alocadas.");
        }

        Random r = new Random();

        int randomPartition = r.nextInt(memoryManager.getPartitionsNumber());

        while (!memoryManager.isFreePartition(randomPartition)) {
            randomPartition = r.nextInt(memoryManager.getPartitionsNumber());
        }

        return randomPartition;
    }

    public void createPCB(String processID, int particao) {
        ProcessControlBlock pcb = new ProcessControlBlock(processID, particao);

        int offSet;
        int enderecoMax;

        offSet = memoryManager.calculatesOffset(particao);
        pcb.setPc(offSet);
        pcb.setOffSet(offSet);

        enderecoMax = memoryManager.calculatesMaxAddress(particao);
        pcb.setLimitAdress(enderecoMax);
        alives++;
        ReadyQueue.add(pcb);

        if (alives == 1) {
            Scheduler.semaphore.release();
        }
    }

    public static void finishProcess(ProcessControlBlock pcb) {
        pcb.setState(ProcessControlBlock.State.FINISHED);
        System.out.println("Terminou de executar o processo " + pcb.getProcessID());
        alives--;
        FinishedQueue.add(pcb);
        MemoryManager.deallocatePartition(pcb.getActualPartition(), pcb.getOffSet(), pcb.getLimitAdress());
        ProcessManager.printRegistersAndMemory(pcb);
        Scheduler.semaphore.release();
    }

    public static void printRegistersAndMemory(ProcessControlBlock pcb) {
        System.out.println("--------------------------------------------");
        System.out.println("DADOS DO PROCESSO:" + pcb.getProcessID());

        System.out.println("Valores finais dos registradores:");

        for (String key: pcb.getRegisters().keySet()) {
            System.out.println("Registrador " + key  + ": " + pcb.getRegisters().get(key));
        }

        System.out.println("--------------------------------------------");
        System.out.println("Estado final das posições de memória da particao");

        for (int i = pcb.getOffSet(); i < pcb.getLimitAdress(); i++) {
            if (MemoryManager.memory[i] != null) {
                System.out.println("Posição de memória " + i);
                System.out.println(MemoryManager.memory[i].toString());
            }
        }
        System.out.println("--------------------------------------------");
    }
}
