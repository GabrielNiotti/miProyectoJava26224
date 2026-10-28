
import com.techlab.excepciones.*;
import com.techlab.service.*;
import com.techlab.ui.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        
        // Instancia de los servicios únicos
        ProductoService productoService = new ProductoService(); 
        PedidoService pedidoService = new PedidoService();

        // Inyección de dependencias en las vistas controladoras de UI
        MenuProducto menuProducto = new MenuProducto(sc, productoService);
        MenuPedido menuPedido = new MenuPedido(sc, productoService, pedidoService);
        
        int opcion = 0;

        do {
            menuProducto.mostrarMenu();

            try {
                String entrada = sc.nextLine().trim();
                if (entrada.isEmpty()) {
                    opcion = 0;
                    continue;
                }
                opcion = Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                System.out.println("  Error: Debe ingresar un número entero válido.");
                opcion = 0;
                continue;
            }

            try {
                switch (opcion) {
                    case 1 -> menuProducto.agregarProducto();
                    case 2 -> menuProducto.modificarProducto();
                    case 3 -> menuProducto.buscarProducto();
                    case 4 -> menuProducto.eliminarProducto();
                    case 5 -> menuProducto.listarProductos();
                    case 6 -> menuPedido.crearPedido(); // Conectado 🛒
                    case 7 -> menuPedido.listarPedidos(); // Conectado 📜
                    case 8 -> System.out.println("Saliendo del programa...");
                    default -> System.out.println("Opción incorrecta.");
                }
            } catch (PrecioInvalidoException e) {
                System.out.println("\n Error de Validación (Precio): " + e.getMessage());
            } catch (StockInvalidoException e) {
                System.out.println("\n  Error de Validación (Stock): " + e.getMessage());
            } catch (StockInsuficienteException e) {
                System.out.println("\n  Error en Pedido (Inventario): " + e.getMessage());
            } catch (ProductoNoEncontradoException e) {
                System.out.println("\n  Error de Búsqueda: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("\n  Error de Entrada: " + e.getMessage());
            }

        } while (opcion != 8);

        sc.close();
    }
}
