package com.techlab.productos;

public class Perfume extends Producto implements Vendible, Etiquetables {

    private int mililitros;

    public Perfume(String nombre, Double precio, int stock, int mililitros) {
        super(nombre, precio, stock);
        this.mililitros = mililitros;
    }

    public int getMililitros() {
        return mililitros;
    }

    @Override
    public String getCategoria() {
        return "perfume";
    }

    @Override
    public void aplicarDescuento(Double porcentaje) {
        System.out.println("Aplicando " + porcentaje + "% de descuento a " + getNombre());
    }

    @Override
    public void generarEtiqueta() {
        System.out.println("Etiqueta: " + getNombre() + " - " + mililitros + " ml");
    }
}