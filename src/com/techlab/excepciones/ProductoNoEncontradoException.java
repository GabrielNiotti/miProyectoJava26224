package com.techlab.excepciones;

/**
 * Excepcion personalizada que se lanza cuando se busca un producto por su id
 * y no existe en el sistema
 * 
 * hereda de RuntimeException (excepciones no chequeadas); no obliga a quien usa el 
 * metodo a envolver la llamada tn try/catch, pero si premite caputrarla cuando nos interesa
 * 
 * Crea nustras propias, nos permite comunicar errores de domino con nombres claros, en lugar de 
 * usar excepciones genericas como exception o IllegalArgumentException
 * 
 */

public class ProductoNoEncontradoException extends RuntimeException{
    public ProductoNoEncontradoException (String mensaje) {
        //super() llama el constructor de la clase padre (RuntimeException)
        // que es quien guarda el mensaje y lo expone con getmessage().
        super(mensaje);

    }


}
