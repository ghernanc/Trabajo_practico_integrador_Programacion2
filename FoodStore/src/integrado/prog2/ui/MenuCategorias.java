
package integrado.prog2.ui;
 
import integrado.prog2.entities.Categoria;
import integrado.prog2.exception.EntidadNoEncontradaException;
import integrado.prog2.exception.ValidacionException;
import integrado.prog2.service.CategoriaService;
import integrado.prog2.service.ProductoService;
 
import java.util.List;
import java.util.Scanner;
 
public class MenuCategorias {
    private final CategoriaService categoriaService;
    private final ProductoService productoService;
    private final Scanner sc;
 
    public MenuCategorias(CategoriaService cs, ProductoService ps, Scanner sc) {
        this.categoriaService = cs;
        this.productoService = ps;
        this.sc = sc;
    }
 
    public void mostrar() {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n--- CATEGORIAS ---");
            System.out.println("1. Listar");
            System.out.println("2. Crear");
            System.out.println("3. Editar");
            System.out.println("4. Eliminar");
            System.out.println("0. Volver");
            System.out.print("Seleccione: ");
            int op = Consola.leerEntero(sc);
            switch (op) {
                case 1 -> listar();
                case 2 -> crear();
                case 3 -> editar();
                case 4 -> eliminar();
                case 0 -> volver = true;
                default -> System.out.println("[!] Opcion invalida.");
            }
        }
    }
 
    private void listar() {
        List<Categoria> lista = categoriaService.listar();
        if (lista.isEmpty()) {
            System.out.println("No hay categorias cargadas.");
            return;
        }
        System.out.println("\n=== CATEGORIAS ===");
        lista.forEach(System.out::println);
    }
 
    private void crear() {
        System.out.print("Nombre: ");
        String nombre = sc.nextLine();
        System.out.print("Descripcion: ");
        String desc = sc.nextLine();
        try {
            Categoria c = categoriaService.crear(nombre, desc);
            System.out.println("[OK] Categoria creada con ID: " + c.getId());
        } catch (ValidacionException e) {
            System.out.println("[!] " + e.getMessage());
        } finally {
            // finally: siempre se ejecuta, haya error o no
            System.out.println("--- Operacion finalizada ---");
        }
    }
 
    private void editar() {
        listar();
        System.out.print("ID a editar: ");
        Long id = Consola.leerLong(sc);
        try {
            categoriaService.buscarPorId(id);
            System.out.print("Nuevo nombre (Enter para no cambiar): ");
            String nombre = sc.nextLine();
            System.out.print("Nueva descripcion (Enter para no cambiar): ");
            String desc = sc.nextLine();
            categoriaService.editar(id,
                    nombre.isBlank() ? null : nombre,
                    desc.isBlank() ? null : desc);
            System.out.println("[OK] Categoria actualizada.");
        } catch (EntidadNoEncontradaException | ValidacionException e) {
            System.out.println("[!] " + e.getMessage());
        }
    }
 
    private void eliminar() {
        listar();
        System.out.print("ID a eliminar: ");
        Long id = Consola.leerLong(sc);
        try {
            Categoria c = categoriaService.buscarPorId(id);
            if (productoService.tieneProductos(id)) {
                System.out.println("[!] La categoria tiene productos activos asociados. No se puede eliminar.");
                return;
            }
            System.out.print("Confirmar eliminacion de '" + c.getNombre() + "' (S/N): ");
            String conf = sc.nextLine();
            if (conf.equalsIgnoreCase("S")) {
                categoriaService.eliminar(id);
                System.out.println("[OK] Categoria eliminada (baja logica).");
            } else {
                System.out.println("Operacion cancelada.");
            }
        } catch (EntidadNoEncontradaException e) {
            System.out.println("[!] " + e.getMessage());
        }
    }
}
 

