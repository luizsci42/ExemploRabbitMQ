package br.ufs.dcomp.chat;

import com.rabbitmq.client.AlreadyClosedException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class Chat {
    private final static String QUEUE_NAME = "mensagens";
    private static Channel channel;
    private static Connection connection;
    private static Send usuario;
    private static Recv receptor;
    private static boolean execucaoChat = true;

    private static void executarChat() {
        Scanner entrada;
        String mensagem;
        String nomeUsuario = usuario.getNomeUsuario();
        String nomeReceptor = receptor.getNome();
        String prompt = "@"+ nomeReceptor + " >> ";

        System.out.print(prompt);
        entrada = new Scanner(System.in);
        mensagem = entrada.nextLine();

        if (mensagem.startsWith("@")) {
            nomeReceptor = mensagem.substring(1);

            receptor = new Recv(
                nomeReceptor,
                nomeUsuario,
                QUEUE_NAME,
                channel
            );
            usuario.setDestinatario(receptor);
        }
        else if (mensagem.equalsIgnoreCase("sair")) {
            entrada.close();
            try {
                channel.close();
            }
            catch (IOException | TimeoutException e) {
                System.out.println("Erro ao fechar canal");
            }
            catch (AlreadyClosedException e) {
                System.out.println("Conexão finalizada");
            }
        }
        usuario.enviarMensagens(mensagem);
    }

    public static void main(String args[]) {
        String remoteHost = "ec2-34-224-65-82.compute-1.amazonaws.com";
        // String porta = "15672";
        String admin = "admin";
        String senha = "password";

        ConnectionFactory factory = new ConnectionFactory();
        Scanner scanner = new Scanner(System.in);

        factory.setHost(remoteHost);
        factory.setUsername(admin);
        factory.setPassword(senha);
        factory.setVirtualHost("/");

        try {
            connection = factory.newConnection();
            channel = connection.createChannel();

            // (queue-name, durable, exclusive, auto-delete, params);
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);

            System.out.println("Sessão inciada. Para encerrar, digite \"sair\"");
            System.out.print("User: ");
            String nomeUsuario = scanner.nextLine();

            System.out.println("Bem vindo, @" + nomeUsuario + "!");
            System.out.println("Para enviar mensagem para outro usuário, digte \"@nome_usuario\"");
            System.out.print(">> ");
            String nomeReceptor = scanner.nextLine();

            receptor = new Recv(nomeReceptor, nomeUsuario, QUEUE_NAME, channel);
            usuario = new Send(nomeUsuario, channel, QUEUE_NAME);
            usuario.setDestinatario(receptor);
            executarChat();

            while (execucaoChat) {
                executarChat();
            }

            scanner.close();
        }
        catch (IOException IOException) {
            System.out.println("IOException");
        }
        catch (TimeoutException timeout) {
            System.out.println("Não foi possível estabelecer a conexão");
        }
        try {
            connection.close();
        }
        catch (IOException IOException) {
            System.out.println("IOException");
        }
    }
}