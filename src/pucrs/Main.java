package pucrs;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("Digite o numero de partições desejado para a CPU (4 ou 8):");
        Scanner in = new Scanner(System.in);
        OpSystem os = new OpSystem(Integer.parseInt(in.next()));
        os.executeShellAndScheduler();
    }
}
