package br.ufs.dcomp.chat;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class Chat {
    private final static String QUEUE_NAME = "mensagens";
    private static boolean executarChat = true;
    private static Send emissor;
    private static Recv receptor;
    private static Scanner scanner;
    private static Channel channel;
    private static Connection connection;
    
    private static void receberMensagens() {
        String mensagem;
        
        System.out.print(">> ");
        mensagem = scanner.nextLine();
        
        if (mensagem.startsWith("@")) {
            Recv rececptor = new Recv();
            emissor.setDestinatario(rececptor);
        }
        if (mensagem.equalsIgnoreCase("sair")) {
            executarChat = false;
        }
        else {
            try {
                // (exchange, routingKey, props, message-body); 
                channel.basicPublish("", QUEUE_NAME, null,  mensagem.getBytes("UTF-8"));
            }
            catch(UnsupportedEncodingException exception) {
                System.out.println("Formato da entrada não suportado");
            }
            catch(IOException exception) {
                System.out.println("Erro ao estabelecer canal");
            }
        }
    }

    public static void main(String args[]) {
        String remoteHost = "ec2-52-207-85-62.compute-1.amazonaws.com";
        String porta = "15672";
        String admin = "admin";
        String senha = "password";
        Send emissor;

        ConnectionFactory factory = new ConnectionFactory();
        scanner = new Scanner(System.in);
    
        factory.setHost(remoteHost + ":" + porta);
        factory.setUsername(admin);
        factory.setPassword(senha);
        factory.setVirtualHost("/");
        
        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
        } 
        catch (IOException IOException) {
            System.out.println("IOException");
        }
        catch(TimeoutException timeout) {
            System.out.println("Não foi possível estabelecer a conexão");
        }
        finally {
            try {
                //(queue-name, durable, exclusive, auto-delete, params); 
                channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            }
            catch(IOException exception) {
                System.out.println("Erro ao declarar fila");
            }
        
            System.out.println("Sessão inciada. Para encerrar, digite \"sair\"");
            System.out.print("User: ");
            String usuario = scanner.nextLine();
            
            System.out.println("Bem vindo, " + usuario);
            System.out.println("Para enviar mensagem para outro usuário, digte \"@nome_usuario\"");
            
            System.out.print(">> ");
            String mensagem = scanner.nextLine();

            emissor = new Send(usuario);
            
            if (mensagem.startsWith("@")) {
                emissor.setDestinatario(receptor);
            }
            
            while (executarChat) {
                receberMensagens();
            }
        
            try {
                channel.close();
                connection.close();
            }
                catch (IOException IOException) {
                System.out.println("IOException");
            }
                catch(TimeoutException timeout) {
                System.out.println("Não foi possível encerrar a conexão");
            }
        }
    }
}