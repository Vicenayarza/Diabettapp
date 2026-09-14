package com.example.tfg.Modelos;
import com.google.firebase.Timestamp;

public class Mensaje {
    private String emisor;
    private String receptor;
    private String mensaje;
    private Timestamp timestamp;
    public Mensaje() {}

    public String getEmisor() { return emisor; }
    public void setEmisor(String emisor) { this.emisor = emisor; }

    public String getReceptor() { return receptor; }
    public void setReceptor(String receptor) { this.receptor = receptor; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }
}

