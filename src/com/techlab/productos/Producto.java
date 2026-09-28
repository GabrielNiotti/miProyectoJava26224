package com.techlab.productos;

public abstract class Producto {

    // contador estatico :
    // static vive a nivel de clase, no de instancia -> por eso sirve como contador global
    public static Long contadorId = 0L;
    private static int totalProductos = 0;

    // ATRIBUTOS
    private Long id;
    private String nombre;
    private Double precio;
    private int stock;

    // CONSTRUCTORES

    public Producto (String nombre,Double precio,int stock){
        this.id = ++contadorId;
        // el ++ al principio es = suma y luego ejecuta.
        // el ++ al final es  = ejecuta y después suma + 1
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        totalProductos++;
    }     

    // metodos abstractos
    public abstract String getCategoria();

    // GETTERS Y SETTERS
    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    // metodo estatico
    public static int getTotalProductos(){
        return totalProductos;
    }

    
    

    // metodos propios de la clase
    public void mostrarDatos() {
        System.out.println("ID: " + id);
        System.out.println("Nombre: " + nombre);
        System.out.println("Precio: " + precio);
        System.out.println("Stock: " + stock);
    }

    protected abstract void aplicarDescuento(double d);


    

    

   
}
