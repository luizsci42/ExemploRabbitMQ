package br.ufs.dcomp.chat;

public class Send {
    private String usuario;
    private Recv destinatario;

    public Send(String usuario) {
        this.usuario = usuario;
    }

    public void setDestinatario(Recv destinatario) {
        this.destinatario = destinatario;
    }

    public Recv getDestinatario() {
        return this.destinatario;
    }

    public String getUsuario() {
        return this.usuario;
    }
}