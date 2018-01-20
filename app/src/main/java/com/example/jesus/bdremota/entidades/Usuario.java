package com.example.jesus.bdremota.entidades;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

/**
 * Created by jesus on 5/12/2017.
 */

public class Usuario {
    private String dni;
    private String nombre;
    private String profesion;
    private String dato;
    private Bitmap imagen;
    private String ruta;


    public String getDato() {
        return dato;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public void setDato(String dato) {
        this.dato = dato;
        try{
            //comprimir imagen
            byte []bytecode= Base64.decode(dato,Base64.DEFAULT);
            // this.imagen= BitmapFactory.decodeByteArray(bytecode,0,bytecode.length);
            int alto=200;
            int ancho=200;
            Bitmap foto= BitmapFactory.decodeByteArray(bytecode,0,bytecode.length);
            this.imagen=Bitmap.createScaledBitmap(foto,alto,ancho,true);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public Bitmap getImagen() {
        return imagen;
    }

    public void setImagen(Bitmap imagen) {
        this.imagen = imagen;
    }

    public String getDni() {
        return dni;

    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getProfesion() {
        return profesion;
    }

    public void setProfesion(String profesion) {
        this.profesion = profesion;
    }
}
