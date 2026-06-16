
package integrado.prog2.ui;

import integrado.prog2.entities.Categoria;
import integrado.prog2.entities.Producto;
import integrado.prog2.exception.EntidadNoEncontradaException;
import integrado.prog2.exception.ValidacionException;
import integrado.prog2.service.CategoriaService;
import integrado.prog2.service.ProductoService;

import java.util.List;
import java.util.Scanner;

public class MenuProductos {
    private final ProductoService productoService;
    private final CategoriaService categoriaService;
    private final Scanner sc;

    public MenuProductos(ProductoService ps, CategoriaService cs, Scanner sc) {
        this.productoService = ps;
        this.categoriaService = cs;
        this.sc = sc;
    }

    public void mostrar() {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n--- PRODUCTOS ---");
            System.out.println("1. Listar todos");
            System.out.println("2. Listar por categoria");
            System.out.println("3. Crear");
            System.out.println("4. Editar");
            System.out.println("5. Eliminar");
            System.out.println("0. Volver");
            System.out.print("Seleccione: ");
            int op = Consola.leerEntero(sc);
            switch (op) {
                case 1 -> listar();
                case 2 -> listarPorCategoria();
                case 3 -> crear();
                case 4 -> editar();
                case 5 -> eliminar();
                case 0 -> volver = true;
                default -> System.out.println("[!] Opcion invalida.");
            }
        }
    }

    private void listar() {
        List<Producto> lista = productoService.listar();
        if (lista.isEmpty()) { System.out.println("No hay productos cargados."); return; }
        System.out.println("\n=== PRODUCTOS ===");
        lista.forEach(System.out::println);
    }

    private void listarPorCategoria() {
        List<Categoria> cats = categoriaService.listar();
        if (cats.isEmpty()) { System.out.println("No hay categorias."); return; }
        cats.forEach(System.out::println);
        System.out.print("ID de categoria: ");
        Long catId = Consola.leerLong(sc);
        List<Producto> lista = productoService.listarPorCategoria(catId);
        if (lista.isEmpty()) { System.out.println("No hay productos en esa categoria."); return; }
        lista.forEach(System.out::println);
    }

    private void crear() {
        List<Categoria> cats = categoriaService.listar();
        if (cats.isEmpty()) {
            System.out.println("[!] No hay categorias disponibles. Crea una primero.");
            return;
        }
        System.out.println("Categorias disponibles:");
        cats.forEach(System.out::println);
        try {
            System.out.print("ID de categoria: ");
            Long catId = Consola.leerLong(sc);
            Categoria cat = categoriaService.buscarPorId(catId);

            System.out.print("Nombre: ");
            String nombre = sc.nextLine();
            System.out.print("Descripcion: ");
            String desc = sc.nextLine();
            double precio;
            do {
                System.out.print("Precio: ");
                String precioStr = sc.nextLine().trim();
                while (!precioStr.matches("-?\\d+(\\.\\d+)?")) {
                    System.out.print("[!] Ingrese un valor numerico valido: ");
                    precioStr = sc.nextLine().trim();
                }
                precio = Double.parseDouble(precioStr);
                if (precio < 0) System.out.println("[!] El precio no puede ser negativo.");
            } while (precio < 0);

            int stock;
            do {
                System.out.print("Stock: ");
                String stockStr = sc.nextLine().trim();
                while (!stockStr.matches("-?\\d+")) {
                    System.out.print("[!] Ingrese un numero valido: ");
                    stockStr = sc.nextLine().trim();
                }
                stock = Integer.parseInt(stockStr);
                if (stock < 0) System.out.println("[!] El stock no puede ser negativo.");
            } while (stock < 0);

            System.out.print("Imagen (URL o nombre): ");
            String imagen = sc.nextLine();
            System.out.print("Disponible (S/N): ");
            boolean disp = sc.nextLine().equalsIgnoreCase("S");

            Producto p = productoService.crear(nombre, precio, desc, stock, imagen, disp, cat);
            System.out.println("[OK] Producto creado con ID: " + p.getId());
        } catch (EntidadNoEncontradaException | ValidacionException e) {
            System.out.println("[!] " + e.getMessage());
        }
    }

    private void editar() {
        listar();
        System.out.print("ID a editar: ");
        Long id = Consola.leerLong(sc);
        try {
            Producto p = productoService.buscarPorId(id);
            System.out.println("Editando: " + p);
            System.out.print("Nuevo nombre (Enter para no cambiar): ");
            String nombre = sc.nextLine();
            System.out.print("Nueva descripcion (Enter para no cambiar): ");
            String desc = sc.nextLine();
            System.out.print("Nuevo precio (Enter para no cambiar): ");
            String precioStr = sc.nextLine();
            System.out.print("Nuevo stock (Enter para no cambiar): ");
            String stockStr = sc.nextLine();
            System.out.print("Nueva imagen (Enter para no cambiar): ");
            String imagen = sc.nextLine();
            System.out.print("Disponible S/N (Enter para no cambiar): ");
            String dispStr = sc.nextLine();

            System.out.println("Categorias disponibles:");
            categoriaService.listar().forEach(System.out::println);
            System.out.print("ID de nueva categoria (Enter para no cambiar): ");
            String catStr = sc.nextLine();

            Double precio = precioStr.isBlank() ? null : Double.parseDouble(precioStr);
            Integer stock = stockStr.isBlank() ? null : Integer.parseInt(stockStr);
            Boolean disp = dispStr.isBlank() ? null : dispStr.equalsIgnoreCase("S");
            Categoria nuevaCategoria = null;
            if (!catStr.isBlank()) {
                nuevaCategoria = categoriaService.buscarPorId(Long.parseLong(catStr));
            }

            productoService.editar(id,
                    nombre.isBlank() ? null : nombre,
                    precio,
                    desc.isBlank() ? null : desc,
                    stock,
                    imagen.isBlank() ? null : imagen,
                    disp, nuevaCategoria);
            System.out.println("[OK] Producto actualizado.");
        } catch (EntidadNoEncontradaException | ValidacionException e) {
            System.out.println("[!] " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("[!] Valor numerico invalido.");
        }
    }

    private void eliminar() {
        listar();
        System.out.print("ID a eliminar: ");
        Long id = Consola.leerLong(sc);
        try {
            Producto p = productoService.buscarPorId(id);
            System.out.print("Confirmar eliminacion de '" + p.getNombre() + "' (S/N): ");
            if (sc.nextLine().equalsIgnoreCase("S")) {
                productoService.eliminar(id);
                System.out.println("[OK] Producto eliminado (baja logica).");
            } else {
                System.out.println("Operacion cancelada.");
            }
        } catch (EntidadNoEncontradaException e) {
            System.out.println("[!] " + e.getMessage());
        }
    }
}

