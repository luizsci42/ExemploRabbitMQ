package br.ufs.dcomp.chat;

import com.rabbitmq.client.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Recv {
    private final static String QUEUE_NAME = "mensagens";
    private Consumer consumer;
    private String nome = "";

    public Recv(String nome, String nome_emissor, String nome_fila, Channel channel) {
        this.setNome(nome);

        this.consumer = new DefaultConsumer(channel) {
            public void handleDelivery(
                String consumerTag,
                Envelope envelope,
                AMQP.BasicProperties properties,
                byte[] body
            ) throws IOException {
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm"); 
                Date date = new Date();
                String data = formatter.format(date);
                String dataHora[] = data.split(" ");

                String mensagem = new String(body, "UTF-8");
                String saida = "\n(" + dataHora[0] + " às " + dataHora[1] + ") " + nome_emissor + " diz: " + mensagem;
                System.out.println(saida);
            }
        };
        receberMensagens(channel, consumer);
    }

    public void receberMensagens(Channel channel, Consumer consumer) {
        try {
            channel.basicConsume(QUEUE_NAME, true, consumer);
        }
        catch (IOException e) {
            System.out.println("Erro ao consumir fila");
        }
    }

    public String getNome() {
        return this.nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
