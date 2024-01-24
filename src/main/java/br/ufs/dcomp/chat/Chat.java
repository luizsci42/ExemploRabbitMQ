package br.ufs.dcomp.chat;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Chat {
  public static String currentReceiver = "";
  public static String prompt = ">> ";
  
  public static void setReceiver(String user) {
    currentReceiver = user;
    setPrompt();
  }
  
  public static void setPrompt() {
    prompt = "@" + currentReceiver + ">> ";
  }
  
  public static String getPrompt() {
    return prompt;
  }
  
  public static String getReceiver() {
    return currentReceiver;
  }
  
  public static String getTime() {
    LocalDateTime now = LocalDateTime.now();

    DateTimeFormatter data = DateTimeFormatter.ofPattern("dd/MM/uuuu");
    String dataFormatada = data.format(now);

    DateTimeFormatter hora = DateTimeFormatter.ofPattern("HH:mm");
    String horaFormatada = hora.format(now);

    String dataHora = "(" + dataFormatada + " às " + horaFormatada + ") ";
		
		return dataHora;
  }

  public static void main(String[] argv) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("ec2-54-221-30-86.compute-1.amazonaws.com");
    factory.setUsername("admin");
    factory.setPassword("password");
    factory.setVirtualHost("/");
    Connection connection = factory.newConnection();
    Channel channel = connection.createChannel();
    
    Scanner sc = new Scanner(System.in);
    
    System.out.print("User: ");
    String user = sc.nextLine();
    
    String QUEUE_NAME = user;
                      //(queue-name, durable, exclusive, auto-delete, params); 
    channel.queueDeclare(QUEUE_NAME, false,   false,     false,       null);
    
    Consumer consumer = new DefaultConsumer(channel) {
      public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)           throws IOException {

        String message = new String(body, "UTF-8");
        System.out.println("\n" + message);
        System.out.print(getPrompt());

      }
    };
                      //(queue-name, autoAck, consumer);    
    channel.basicConsume(QUEUE_NAME, true,    consumer);
    
    String msg = "";
    while(true) {
      System.out.print(getPrompt());
      msg = sc.nextLine();
      
      if(msg.isEmpty()) {
        continue;
      }
      if(msg.charAt(0) == '@') {
        setReceiver(msg.substring(1));
        continue;
      }
      if(msg.equals(":quit")) {
        break;
      }
      
      String dataHora = getTime();
      
      String fullMsg = dataHora + user + " diz: " + msg;
		
		  channel.basicPublish("", getReceiver(), null,  fullMsg.getBytes("UTF-8"));
    }
    
    channel.close();
    connection.close();
		
  }
}