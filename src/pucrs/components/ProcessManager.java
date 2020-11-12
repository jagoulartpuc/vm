package pucrs.components;

import pucrs.domain.ProcessControlBlock;
import pucrs.queues.ReadyQueue;

import java.util.Random;

public class ProcessManager {

    private final int MIN_PROCESS_ALLOWED = 4;
    private final int MAX_PROCESS_ALLOWED = 8;
    private MemoryManager memoryManager;

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
            throw new Exception("Impossível alocar mais processos na memória pois todas partições já estão alocadas. Encerrando execução da VM");
        }

        Random r = new Random();

        int randomPartition = r.nextInt(memoryManager.getPartitionsNumber());

        // enquanto a partição aleatoria estiver ocupada, procurar uma próxima aleatoria
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
        pcb.setPc(offSet); //AMBOS RECEBEM OFFSET ???
        pcb.setOffSet(offSet);

        enderecoMax = memoryManager.calculatesMaxAddress(particao);
        pcb.setLimitAdress(enderecoMax);

        ReadyQueue.add(pcb);
    }
}
