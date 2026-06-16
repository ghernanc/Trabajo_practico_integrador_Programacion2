package integrado.prog2;

import integrado.prog2.service.*;
import integrado.prog2.ui.*;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Instanciar servicios
        CategoriaService categoriaService = new CategoriaService();
        ProductoService productoService   = new ProductoService();
        UsuarioService usuarioService     = new UsuarioService();
        PedidoService pedidoService       = new PedidoService();

        // Instanciar menus
        MenuCategorias menuCategorias = new MenuCategorias(categoriaService, productoService, sc);
        MenuProductos  menuProductos  = new MenuProductos(productoService, categoriaService, sc);
        MenuUsuarios   menuUsuarios   = new MenuUsuarios(usuarioService, sc);
        MenuPedidos    menuPedidos    = new MenuPedidos(pedidoService, usuarioService, productoService, sc);

        boolean salir = false;
        while (!salir) {
            System.out.println("\n========================================");
            System.out.println("   SISTEMA DE PEDIDOS (FOOD STORE)");
            System.out.println("========================================");
            System.out.println("1. Categorias");
            System.out.println("2. Productos");
            System.out.println("3. Usuarios");
            System.out.println("4. Pedidos");
            System.out.println("0. Salir");
            System.out.print("Seleccione: ");

            int op = Consola.leerEntero(sc);
            switch (op) {
                case 1 -> menuCategorias.mostrar();
                case 2 -> menuProductos.mostrar();
                case 3 -> menuUsuarios.mostrar();
                case 4 -> menuPedidos.mostrar();
                case 0 -> {
                    System.out.println("Hasta luego!");
                    salir = true;
                }
                default -> System.out.println("[!] Opcion invalida. Intente de nuevo.");
            }
        }

        sc.close();
    }
}
