package com.techlab.productos;

public class Shampoo extends Producto implements Vendible, Etiquetables {
    private String tipoCabello;

    public Shampoo (String nombre, Double precio, int stock, String tipoCabello) {
        super (nombre, precio, stock);
        this.tipoCabello = tipoCabello;

    }
    public String getTipoCabello() {
        return tipoCabello;
    }
    public void setTipoCabello(String tipoCabello) {
        this.tipoCabello = tipoCabello;
    }

    @Override 
    public String getCategoria() {
        return "shampoo";

    }
    // Implementacion del metodo abstracto de la interfaz Vendible
    @Override
    public void aplicarDescuento(Double porcentaje) {
        System.out.println("Aplicando " + porcentaje + "%" + " de descuento a " + getNombre());
    }

    // Implementacion del metodo abstracto de la interfaz Etiquetable
    @Override
    public void generarEtiqueta() {
        System.out.println("Etiqueta : " + getNombre() + " - Tipo de Cabello: " + tipoCabello);
    }



}
