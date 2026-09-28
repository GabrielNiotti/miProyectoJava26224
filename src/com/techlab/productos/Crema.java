package com.techlab.productos;

public class Crema extends Producto implements Vendible, Etiquetables {

     private String tipoPiel;

    public Crema(String nombre, Double precio, int stock, String tipoPiel) {
        super(nombre, precio, stock);
        this.tipoPiel = tipoPiel;
    }

    public String getTipoPiel() {
        return tipoPiel;
    }

    @Override
    public String getCategoria() {
        return "crema";
    }

    // Implementacion del metodo abstracto de la interfaz Vendible
    @Override
    public void aplicarDescuento(Double porcentaje) {
        System.out.println("Aplicando " + porcentaje + "%" + " de descuento a " + getNombre());
    }

    // Implementacion del metodo abstracto de la interfaz Etiquetable
    @Override
    public void generarEtiqueta() {
        System.out.println("Etiqueta : " + getNombre() + " - Tipo de Piel " + tipoPiel);
    }


}



