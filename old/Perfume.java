package com.techlab.laperfu.model;

import com.fasterxml.jackson.annotation.JsonIgnore; // <-- IMPORTANTE
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter 
public class Perfume extends Producto implements Vendible, Etiquetables {
    private String genero;

    public Perfume() {
        super();
    }

    public Perfume (String nombre, Double precio, int stock, String genero) {
        super (nombre, precio, stock);
        this.genero = genero;
    }

    @Override 
    @JsonIgnore // <-- SOLUCIÓN AL DUPLICADO
    public String getCategoria() { return "perfume"; }

    @Override
    public void aplicarDescuento(Double porcentaje) {
        System.out.println("Aplicando " + porcentaje + "% de descuento a " + getNombre());
    }

    @Override
    public void generarEtiqueta() {
        System.out.println("Etiqueta : " + getNombre() + " - Perfume " + genero);
    }
}
