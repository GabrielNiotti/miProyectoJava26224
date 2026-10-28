package com.techlab.productos;

public class Perfume extends Producto implements Vendible, Etiquetables {
    private String genero;

    public Perfume (String nombre, Double precio, int stock, String genero) {
        super (nombre, precio, stock);
        this.genero = genero;
    }

    public String getGenero() {
        return genero;
    }

    // Método Setter para permitir modificaciones desde el menú
    public void setGenero(String genero) {
        this.genero = genero;
    }

    @Override 
    public String getCategoria() {
        return "perfume";
    }

    // Implementacion del metodo abstracto de la interfaz Vendible
    @Override
    public void aplicarDescuento(Double porcentaje) {
        System.out.println("Aplicando " + porcentaje + "%" + " de descuento a " + getNombre());
    }

    // Implementacion del metodo abstracto de la interfaz Etiquetable
    @Override
    public void generarEtiqueta() {
        System.out.println("Etiqueta : " + getNombre() + " - Perfume " + genero);
    }
}
