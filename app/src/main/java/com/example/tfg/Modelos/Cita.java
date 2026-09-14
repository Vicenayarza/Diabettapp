package com.example.tfg.Modelos;

public class Cita {
   String fecha;
   String nombre;
   int imagen;

    /*public Cita(String hora, String paciente) {
        this.hora = hora;
        this.paciente = paciente;

    }
*/
    public String getNombre() {
        return nombre;
    }
    public String getFecha() {
        return fecha;
    }

    public int getimg() {
        return imagen;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
    public void setimg(int img) {
        this.imagen = img;
    }

}

