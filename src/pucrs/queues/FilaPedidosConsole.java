package pucrs.queues;

import pucrs.domain.PedidoConsoleIO;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;

public class FilaPedidosConsole {
    private Queue<PedidoConsoleIO> filaPedidos;

    //private FilaPedidosConsole() {} ???
    public FilaPedidosConsole() {
        filaPedidos = new Queue<PedidoConsoleIO>() {
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
            public Iterator<PedidoConsoleIO> iterator() {
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
            public boolean add(PedidoConsoleIO pedidoConsoleIO) {
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
            public boolean addAll(Collection<? extends PedidoConsoleIO> c) {
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
            public boolean offer(PedidoConsoleIO pedidoConsoleIO) {
                return false;
            }

            @Override
            public PedidoConsoleIO remove() {
                return null;
            }

            @Override
            public PedidoConsoleIO poll() {
                return null;
            }

            @Override
            public PedidoConsoleIO element() {
                return null;
            }

            @Override
            public PedidoConsoleIO peek() {
                return null;
            }
        };
    }

    //COMO FAZER GET E SET ???

    public void addPedidoIO(PedidoConsoleIO pedido) {
        filaPedidos.add(pedido);

        System.out.println("Adicionou o processo " + pedido.processID + " a fila de pedidos do console requisitando IO tipo [" + pedido.ioOperation + "].");
    }

    public PedidoConsoleIO dequeuePedido() {
        return filaPedidos.remove();
    }

    public int contarProcessos() {
        return filaPedidos.size();
    }

    public void printFilaPedidosConsole() {
        System.out.println("Printando fila de pedidos do console:");

        for(PedidoConsoleIO pedido : filaPedidos) {
            System.out.println("Process Id: {" + pedido.processID + "} | Operation: {" + pedido.ioOperation + "} | Endereço solicitado: {" + pedido.endereco + "}");
        }
    }
}