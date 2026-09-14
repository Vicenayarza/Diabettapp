package com.example.tfg.Modelos;

public class Persona {
    String nombre;
    int img;
    String id;


   /* public Persona(String nombre, int img) {
        this.nombre = nombre;
        this.img = img;

    }
*/
    public String getNombre() {
        return nombre;
    }

    public int getimg() {
        return img;
    }
    public String getId(){return id;}
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public void setimg(int img) {
        this.img = img;
    }
    public void setId(String id){this.id = id;}

}


