package com.techlab.laperfu.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter @Setter 
@NoArgsConstructor // <-- Lombok genera el constructor vacío compatible con Jackson perfectamente
public class Crema extends Producto implements Vendible, Etiquetables {

    private String tipoPiel;

    // Constructor con parámetros
    public Crema(String nombre, Double precio, int stock, String tipoPiel) {
        super(nombre, precio, stock);
        this.tipoPiel = tipoPiel;
    }

    @Override
    @JsonIgnore
    public String getCategoria() { 
        return "crema"; 
    }

    @Override
    public void aplicarDescuento(Double porcentaje) {
        System.out.println("Aplicando " + porcentaje + "% de descuento a " + getNombre());
    }

    @Override
    public void generarEtiqueta() {
        System.out.println("Etiqueta : " + getNombre() + " - Tipo de Piel " + tipoPiel);
    }
}
