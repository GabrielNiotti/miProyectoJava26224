package com.techlab.laperfu.model;

import com.fasterxml.jackson.annotation.JsonIgnore; // <-- IMPORTANTE
import lombok.Getter;
import lombok.Setter; 

@Getter 
@Setter 
public class Shampoo extends Producto implements Vendible, Etiquetables {

    private String tipoCabello;

    public Shampoo() {
        super();
    }

    public Shampoo (String nombre, Double precio, int stock, String tipoCabello) {
        super (nombre, precio, stock);
        this.tipoCabello = tipoCabello;
    }

    @Override 
    @JsonIgnore // <-- SOLUCIÓN AL DUPLICADO
    public String getCategoria() { return "shampoo"; }

    @Override
    public void aplicarDescuento(Double porcentaje) {
        System.out.println("Aplicando " + porcentaje + "% de descuento a " + getNombre());
    }

    @Override
    public void generarEtiqueta() {
        System.out.println("Etiqueta : " + getNombre() + " - Tipo de Cabello: " + tipoCabello);
    }
}
