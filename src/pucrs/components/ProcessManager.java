package pucrs.components;

import pucrs.domain.ProcessControlBlock;

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

    public void loadProgram(int programNumber) {
        String filePath = Environment.CurrentDirectory + @ "\assemblyPrograms\P" + programNumber + ".txt";

        int particao = randomPartition();

        MemoryManager.ReadFile(filePath, particao);

        count++;
        String processID = "P" + count;
        createPCB(processID, particao);
    }

    public int randomPartition() {
        if (MemoryManager.memoriaCheia()) {
            throw new StackOverflowException("Impossível alocar mais processos na memória pois todas partições já estão alocadas. Encerrando execução da VM");
        }

        Random r = new Random();

        int particaoAleatoria = r.nextInt(MemoryManager.numeroParticoes);

        // enquanto a partição aleatoria estiver ocupada, procurar uma próxima aleatoria
        while (!MemoryManager.particaoEstaLivre(particaoAleatoria)) {
            particaoAleatoria = r.nextInt(MemoryManager.numeroParticoes);
        }

        return particaoAleatoria;
    }

    public void createPCB(String processID, int particao) {
        ProcessControlBlock pcb = new ProcessControlBlock(processID, particao);

        int offSet;
        int enderecoMax;

        offSet = GerenteDeMemoria.CalculaOffset(particao);
        pcb.pc = offSet; //AMBOS RECEBEM OFFSET ???
        pcb.offSet = offSet;

        enderecoMax = MemoryManager.calculaEnderecoMax(particao);
        pcb.enderecoLimite = enderecoMax;

        FilaDeProntos.AddProcess(pcb);
    }
}
}