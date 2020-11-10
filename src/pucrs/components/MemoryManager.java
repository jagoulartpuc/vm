package pucrs.components;

import pucrs.domain.MemoryPos;
import pucrs.domain.Partition;
import pucrs.domain.ProcessControlBlock;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class MemoryManager {
    private int MAX_MEMORY_SIZE = 1024;

    private int MIN_MEMORY_SIZE = 4;

    private int MAX_MEMORY_ALLOWED = 8;

    private Partition[] partitions;

    public Partition[] getParticoes() {
        return this.partitions;
    }

    int numeroParticoes;

    public int getNumeroParticoes() {
        return this.numeroParticoes;
    }

    public void setNumeroParticoes(int numeroParticoes) {
        this.numeroParticoes = numeroParticoes;
    }

    private int particoesAlocadas;

    public int getParticoesAlocadas() {
        return this.particoesAlocadas;
    }

    public void setParticoesAlocadas(int particoesAlocadas) {
        this.particoesAlocadas = particoesAlocadas;
    }

    public static MemoryPos[] memory;

    public MemoryManager(int numeroParticoes) throws Exception {
        if (numeroParticoes % 2 != 0) {
            throw new Exception("Não é possivel criar um número impar de partições, use apenas números 2^n. Encerrando execução");
        }

        if (numeroParticoes > MAX_MEMORY_SIZE) {
            throw new Exception("O tamanho máximo de partições permitido para uma CPU é de " + MAX_MEMORY_SIZE + ". Encerrando execução.");
        }

        if (numeroParticoes < MIN_MEMORY_SIZE) {
            throw new Exception("O tamanho mínimo de partições permitido para uma CPU é de " + MIN_MEMORY_SIZE + ". Encerrando execução.");
        }

        this.numeroParticoes = numeroParticoes;
        this.particoesAlocadas = 0;

        partitions = new Partition[numeroParticoes];
        memory = new MemoryPos[MAX_MEMORY_SIZE];

        for (int i = 0; i < numeroParticoes; i++) {
            partitions[i] = new Partition();
        }
    }

    public boolean isFreePartition(int partition) {
        return partitions[partition].status == Partition.Status.DEALLOCATED;
    }

    public boolean isFullMemory() {
        for (Partition partition : partitions) {
            if (partition.status.equals(Partition.Status.DEALLOCATED)) {
                return false;
            }
        }
        return true;
    }

    public int calculaOffset(int particao) {
        return particao * (memory.length / numeroParticoes);
    }

    public int calculaEnderecoMax(int particao) {
        particao++;
        int boundsRegister = particao * (memory.length / numeroParticoes) - 1;
        return boundsRegister;
    }

    public static int calculaEnderecoMemoria(ProcessControlBlock pcb, int endereco) throws Exception {
        int offset = pcb.getOffSet();
        int trueAdress = offset + endereco;

        if (trueAdress > pcb.getLimitAdress()) {
            throw new Exception("SEGMENTATION FAULT, o endereço fornecido " + trueAdress + "está fora do limite da partição que é " + pcb.getLimitAdress());
        }

        return trueAdress;
    }

    public void readFile(String filePath, int particao) throws IOException {
        Path path = Paths.get(filePath);
        int counter = 0;
        try (Scanner sc = new Scanner(Files.newBufferedReader(path, StandardCharsets.UTF_8))) {
            sc.useDelimiter("[\n]");
            while (sc.hasNext()) {
                String line = sc.nextLine();
                int offsetParticao = calculaOffset(particao);

                String[] dataContent = line.split(" ");

                String command = dataContent[0];

                if (command.equals("STOP") || command.equals("SWAP")) {
                    MemoryPos memoryPos = new MemoryPos();
                    memoryPos.setOpcode(command);
                    memory[counter + offsetParticao] = memoryPos;
                }

                String[] parameters = dataContent[1].replace(" ", "").split(",");

                if (command.equals("JMP") || command.equals("JMPI")) {
                    MemoryPos memoryPos = new MemoryPos();
                    memoryPos.setOpcode(command);
                    memoryPos.setReg1(parameters[0].contains("r") ? parameters[0] : null);
                    memoryPos.setParameter(tryParseInt(parameters[0]));
                    memory[counter + offsetParticao] = memoryPos;
                } else {
                    MemoryPos memoryPos = new MemoryPos();
                    memoryPos.setOpcode(command);
                    memoryPos.setReg1(parameters[0].contains("r") || parameters[0].contains("[") ? parameters[0] : null);
                    memoryPos.setReg2(parameters[1].contains("r") || parameters[1].contains("[") ? parameters[1] : null);
                    memoryPos.setParameter(tryParseInt(parameters[1]));
                    memory[counter + offsetParticao] = memoryPos;
                }
                counter++;
            }
            partitions[particao].status = Partition.Status.ALLOCATED;
            particoesAlocadas++;
        }
    }

    public void desalocarParticao(int particao, int offset, int enderecoLimite) {
        partitions[particao].status = Partition.Status.DEALLOCATED;

        for (int i = offset; i <= enderecoLimite; i++) {
            memory[offset] = new MemoryPos();
        }
    }

    public int tryParseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

}