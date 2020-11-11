package pucrs.queues;

import pucrs.domain.ProcessControlBlock;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;

public class FinishedQueue {
    private Queue<ProcessControlBlock> finishedQueue;

    //private FilaDeFinalizados() {} ???
    public FinishedQueue() {
        finishedQueue = new Queue<ProcessControlBlock>() {
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
        finishedQueue.add(pcb);

        System.out.println("Adicionou o processo " + pcb.getProcessID() + " a fila de processos finalizados.");
    }

    public ProcessControlBlock dequeueProcess() {
        return finishedQueue.remove();
    }

    public int countProcess() {
        return finishedQueue.size();
    }

    public void printFilaDeFinalizados() {
        if (finishedQueue.size() == 0) {
            System.out.println("Não há processos finalizados no momento.");
        } else {
            System.out.println("Printando fila de processos finalizados:");

            for(ProcessControlBlock pcb : finishedQueue) {
                System.out.println("Process Id: {" + pcb.getProcessID() + "} | State: {" + pcb.getState() + "} | OffSet: {" + pcb.getOffSet() + "} | EndereçoLimite: {" + pcb.getLimitAdress() + "}");
            }
        }
    }
}