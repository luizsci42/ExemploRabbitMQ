package br.ufs.dcomp.chat;

import com.rabbitmq.client.*;

import java.io.IOException;

public class Recv {

  // private final static String QUEUE_NAME = "minha-fila";

  // public static void main(String[] argv) throws Exception {
  //   ConnectionFactory factory = new ConnectionFactory();
  //   factory.setHost("ec2-52-207-85-62.compute-1.amazonaws.com:15672"); // Alterar
  //   factory.setUsername("admin"); // Alterar
  //   factory.setPassword("password"); // Alterar
  //   factory.setVirtualHost("/");   
  //   Connection connection = factory.newConnection();
  //   Channel channel = connection.createChannel();

  //                     //(queue-name, durable, exclusive, auto-delete, params); 
  //   channel.queueDeclare(QUEUE_NAME, false,   false,     false,       null);
  //   // channel.exchangeDeclare("ufs", "fanout");
    
  //   System.out.println(" [*] Esperando recebimento de mensagens...");

  //   Consumer consumer = new DefaultConsumer(channel) {
  //     public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)           throws IOException {

  //       String message = new String(body, "UTF-8");
  //       System.out.println(" [x] Mensagem recebida: '" + message + "'");

  //       //(deliveryTag,               multiple);
  //       //channel.basicAck(envelope.getDeliveryTag(), false);
  //     }
  //   };
  //   //(queue-name, autoAck, consumer);    
  //   channel.basicConsume(QUEUE_NAME, true,    consumer);
  // }
}