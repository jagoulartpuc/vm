package pucrs.queues;

import pucrs.domain.ProcessControlBlock;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;

public class FilaBloqueadosIO {
    private Queue<ProcessControlBlock> filaBloqueados;

    //private FilaBloqueadosIO() {} ???
    public FilaBloqueadosIO() {
        filaBloqueados = new Queue<ProcessControlBlock>() {
            @Override
            public int size() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean contains(Object o) {
                return false;
            }

            @Override
            public Iterator<ProcessControlBlock> iterator() {
                return null;
            }

            @Override
            public Object[] toArray() {
                return new Object[0];
            }

            @Override
            public <T> T[] toArray(T[] a) {
                return null;
            }

            @Override
            public boolean add(ProcessControlBlock processControlBlock) {
                return false;
            }

            @Override
            public boolean remove(Object o) {
                return false;
            }

            @Override
            public boolean containsAll(Collection<?> c) {
                return false;
            }

            @Override
            public boolean addAll(Collection<? extends ProcessControlBlock> c) {
                return false;
            }

            @Override
            public boolean removeAll(Collection<?> c) {
                return false;
            }

            @Override
            public boolean retainAll(Collection<?> c) {
                return false;
            }

            @Override
            public void clear() {

            }

            @Override
            public boolean equals(Object o) {
                return false;
            }

            @Override
            public int hashCode() {
                return 0;
            }

            @Override
            public boolean offer(ProcessControlBlock processControlBlock) {
                return false;
            }

            @Override
            public ProcessControlBlock remove() {
                return null;
            }

            @Override
            public ProcessControlBlock poll() {
                return null;
            }

            @Override
            public ProcessControlBlock element() {
                return null;
            }

            @Override
            public ProcessControlBlock peek() {
                return null;
            }
        };
    }

    //COMO FAZER GET E SET ???

    public void addProcess(ProcessControlBlock pcb) {
        filaBloqueados.add(pcb);

        System.out.println("Adicionou o processo " + pcb.processID + " a fila de bloqueados por IO.");
    }

    public ProcessControlBlock dequeueProcess(String pcbID) {
        try {
            for(ProcessControlBlock pcb : filaBloqueados) {
                if(pcb.processID == pcbID) {
                    return filaBloqueados.remove();
                }
            }
        }
        catch(NoSuchElementException e) {
            System.out.println("O process id numero [" + pcbID + "] não existe na fila de processos bloqueados.");
        }

        return null;
    }

    public int contarProcessos() {
        return filaBloqueados.size();
    }

    public void printFilaDeBloqueados() {
        if (filaBloqueados.size() == 0) {
            System.out.println("Não há processos bloqueados por IO no momento.");
        } else {
            System.out.println("Printando fila de processos bloqueados:");

            for(ProcessControlBlock pcb : filaBloqueados) {
                System.out.println("Process Id: {" + pcb.processID + "} | State: {" + pcb.state + "} | OffSet: {" + pcb.offSet + "} | EndereçoLimite: {" + pcb.enderecoLimite + "}");
            }
        }
    }
}