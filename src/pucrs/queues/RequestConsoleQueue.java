package pucrs.queues;

import pucrs.domain.RequestIOConsole;

import java.util.*;

public class RequestConsoleQueue {
    private static Queue<RequestIOConsole> requestQueue;

    public RequestConsoleQueue() {
        requestQueue = new LinkedList<>();
    }

    public static void add(RequestIOConsole request) {
        requestQueue.add(request);
        System.out.println("Adicionou o processo " + request.getProcessID() + " a fila de pedidos do console requisitando IO tipo [" + request.getIOOperation() + "].");
    }

    public static RequestIOConsole remove() {
        return requestQueue.remove();
    }

    public static int count() {
        return requestQueue.size();
    }

    public static void print() {
        System.out.println("Printando fila de pedidos do console:");

        for(RequestIOConsole request : requestQueue) {
            System.out.println("Process Id: {" + request.getProcessID() + "} | Operation: {" + request.getIOOperation() + "} | Endereço solicitado: {" + request.getAdress() + "}");
        }
    }
}