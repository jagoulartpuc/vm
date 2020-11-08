package pucrs.components;

import java.util.Scanner;

public class Console {

    public static void ExecutarConsoleIO() {
        Scanner in = new Scanner(System.in);
        if (FilaPedidosConsole.ContarProcessos() != 0) {
            PedidoConsoleIO pedido = FilaPedidosConsole.DequeuePedido();

            int endereco = pedido.Endereco;

            if (pedido.IOOperation == IOType.READ) {
                System.out.println("O processo" + pedido.ProcessID + "está solicitando leitura de um valor que será salvo na posição de memória " + endereco);
                System.out.println("Digite o valor para ser salvo:");

                int value = Integer.parseInt(in.next());

                GerenteDeMemoria.Memoria[endereco] = new PosicaoDeMemoria
                {
                    OPCode = "DATA", Parameter = value
                };

                System.out.println("Valor salvo com sucesso na memória!");
            } else {
                int value = GerenteDeMemoria.Memoria[endereco].Parameter;
                System.out.println("O processo " + pedido.ProcessID + "solicitou o valor no endereço de memória " + endereco + "e encontrou o valor " + value);
            }
            RotinaTratamentoRetornoIO.TratarRetornoIO(pedido.ProcessID);
        }

        System.out.println("Não há pedidos de IO para serem processados");
        //Liberando shell para voltar a executar
        OpSystem.semaforoShell.release();

    }
}
