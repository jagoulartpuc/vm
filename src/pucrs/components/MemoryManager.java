package pucrs.components;

import pucrs.domain.ParticaoMemoria;
import pucrs.domain.PosicaoDeMemoria;
import pucrs.domain.ProcessControlBlock;

import java.io.File;

public class MemoryManager {
    private int TAMANHO_MAXIMO_POSICOES_DE_MEMORIA = 1024;

    private int TAMANHO_MINIMO_PARTICOES_PERMITIDO = 4;

    private int TAMANHO_MAXIMO_PARTICOES_PERMITIDO = 8;

    public ParticaoMemoria[] particoes;

    public ParticaoMemoria[] getParticoes() {
        return this.particoes;
    }

    public int numeroParticoes;

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

    public PosicaoDeMemoria[] memoria;

    public PosicaoDeMemoria[] getMemoria() {
        return this.memoria;
    }

    public MemoryManager(int numeroParticoes) {
        if (numeroParticoes % 2 != 0) {
            throw new ArgumentException("Não é possivel criar um número impar de partições, use apenas números 2^n. Encerrando execução");
        }

        if (numeroParticoes > TAMANHO_MAXIMO_PARTICOES_PERMITIDO) {
            throw new ArgumentException($"O tamanho máximo de partições permitido para uma CPU é de [{TAMANHO_MAXIMO_PARTICOES_PERMITIDO}]. Encerrando execução.");
        }

        if (numeroParticoes < TAMANHO_MINIMO_PARTICOES_PERMITIDO) {
            throw new ArgumentException($"O tamanho mínimo de partições permitido para uma CPU é de [{TAMANHO_MINIMO_PARTICOES_PERMITIDO}]. Encerrando execução.");
        }

        this.numeroParticoes = numeroParticoes;
        this.particoesAlocadas = 0;

        particoes = new ParticaoMemoria[numeroParticoes];
        memoria = new PosicaoDeMemoria[TAMANHO_MAXIMO_POSICOES_DE_MEMORIA];

        for (int i = 0; i < numeroParticoes; i++) {
            particoes[i] = new ParticaoMemoria();
        }
    }

    public boolean particaoEstaLivre(int particao) {
        return particoes[particao].status == ParticaoMemoria.Status.DESALOCADO;
    }

    public boolean memoriaCheia() {
        return particoes.All(x = > x.Status == ParticaoMemoria.Status.ALOCADO);
    }

    public int calculaOffset(int particao) {
        return particao * (memoria.length / numeroParticoes);
    }

    public int calculaEnderecoMax(int particao) {
        // Como o array de partiçoes inicia em 0, se nao realizarmos a operação ++ o calculo de boundsRegister ia pegar o endereço mínimo ao invés do máximo,
        // Exemplo, caso nao tivesse esse comando abaixo, na partição 1 (segunda do array) o calculo iria retornar 127, ao invés de 255, que é realmente o endereço maximo dessa
        // partição em específico
        particao++;

        int boundsRegister = particao * (memoria.length / numeroParticoes) - 1;
        return boundsRegister;
    }

    public static int calculaEnderecoMemoria(ProcessControlBlock pcb, int endereco) {
        int offset = pcb.offSet;

        int enderecoCorrigido = offset + endereco;

        int maximumBound = pcb.enderecoLimite;

        if (enderecoCorrigido > maximumBound) {
            throw new AcessoIndevidoException($"SEGMENTATION FAULT, o endereço fornecido {enderecoCorrigido} está fora do limite da partição que é {maximumBound}");
        }

        return enderecoCorrigido;
    }

    public void readFile(String filePath, int particao) {
        String[] fileContent = File.readAllLines(filePath);

        int offsetParticao = calculaOffset(particao);

        for (int i = 0; i < fileContent.length; i++) {
            String[] dataContent = fileContent[i].split(' ');

            String command = dataContent[0];

            if (command == "STOP") {
                Memoria[i + offsetParticao] = new PosicaoDeMemoria
                {
                    OPCode = "STOP"
                } ;

                continue;
            }

            if (command == "SWAP") {
                Memoria[i + offsetParticao] = new PosicaoDeMemoria
                {
                    OPCode = "SWAP"
                } ;

                continue;
            }

            String[] parameters = dataContent[1].replace(" ", "").split(',');

            if (command == "JMP" || command == "JMPI") {
                Memoria[i + offsetParticao] = new PosicaoDeMemoria
                {
                    OPCode = command,
                            Reg1 = parameters[0].Contains("r") ? parameters[0] : null,
                            Parameter = Int32.TryParse(parameters[0], out int value) ?value:
                0
                } ;
            } else {
                Memoria[i + offsetParticao] = new PosicaoDeMemoria
                {
                    OPCode = command,
                            Reg1 = parameters[0].Contains("r") || parameters[0].Contains("[") ? parameters[0] : null,
                            Reg2 = parameters[1].Contains("r") || parameters[1].Contains("[") ? parameters[1] : null,
                            Parameter = Int32.TryParse(parameters[1], out int value) ?value:
                0
                } ;
            }
        }
        // indicando que a partição já está alocada
        particoes[particao].status = ParticaoMemoria.Status.ALOCADO;
        particoesAlocadas++;
    }

    public void desalocarParticao(int particao, int offset, int enderecoLimite) {
        particoes[particao].status = ParticaoMemoria.Status.DESALOCADO;

        for (int i = offset; i <= enderecoLimite; i++) {
            memoria[offset] = new PosicaoDeMemoria();
        }
    }
}
}