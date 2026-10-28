package com.techlab.productos;

public interface Vendible {
    // Todo atributo en una interfaz es public static final por defecto, es decir, constante.
    String ESTADO_DEFAULT = "Disponible";

    // Todo metodo en una interfaz es public abstract por defecto, es decir, metodo abstracto.
    void aplicarDescuento(Double porcentaje);

    // Todo metodo default en una interfaz es public por defecto, es decir, metodo concreto. La clase que lo implementa puede sobreescribirlo o no.
    default void mostrarEstado() {
        System.out.println("Estado del producto: " + ESTADO_DEFAULT);
    }

    // Método static: pertenece a la interfaz y no a la instancia. Se puede llamar sin crear un objeto de la interfaz.

    static Double calcularDescuento(Double precio, Double porcentaje) {
        return precio - (precio * porcentaje / 100);
    }

    static Double totalDescuento(Double precio, Double porcentaje ) {
        return (precio * porcentaje / 100);
    }

    // No tiene constructor no estado de instancia, ya que no se puede instanciar una interfaz. Solo se puede implementar en clases concretas.
    // Una clase puede implementar varias interfaces, pero solo puede extender una clase abstracta. Por eso es útil usar interfaces para definir comportamientos que pueden ser compartidos por diferentes clases.
    // implementar varias interfaces separadas por comas ( impmenets A,B )
}
