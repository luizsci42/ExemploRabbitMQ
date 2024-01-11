package br.ufs.dcomp.chat;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import com.rabbitmq.client.Channel;

public class Send {
    private String nomeUsuario;
    private Recv receptor;
    private Channel channel;
    private String nomeFila;

    public Send(String nomeUsuario, Channel channel, String nomeFila) {
        this.nomeUsuario = nomeUsuario;
        this.channel = channel;
        this.nomeFila = nomeFila;
    }

    public boolean enviarMensagens(String mensagem) {
        String nomeReceptor = receptor.getNome();
        
        if (mensagem.startsWith("@")) {
            nomeReceptor = mensagem.substring(1);
            receptor = new Recv(
                nomeReceptor,
                nomeUsuario,
                nomeFila,
                channel
            );
            this.setDestinatario(receptor);
        }
        else {
            try {
                // (exchange, routingKey, props, message-body); 
                channel.basicPublish("", nomeFila, null,  mensagem.getBytes("UTF-8"));
            }
            catch(UnsupportedEncodingException exception) {
                System.out.println("Formato da entrada não suportado");
                return false;
            }
            catch(IOException exception) {
                System.out.println("Erro ao estabelecer canal");
                return false;
            }
        }
        return true;
    }

    public void setDestinatario(Recv destinatario) {
        this.receptor = destinatario;
    }

    public Recv getDestinatario() {
        return this.receptor;
    }

    public String getNomeUsuario() {
        return this.nomeUsuario;
    }
}