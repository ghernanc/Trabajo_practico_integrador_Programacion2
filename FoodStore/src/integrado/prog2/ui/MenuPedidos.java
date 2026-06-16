package integrado.prog2.ui;
 
import integrado.prog2.entities.Base;
import integrado.prog2.entities.DetallePedido;
import integrado.prog2.entities.Pedido;
import integrado.prog2.entities.Producto;
import integrado.prog2.entities.Usuario;
import integrado.prog2.enums.Estado;
import integrado.prog2.enums.FormaPago;
import integrado.prog2.exception.EntidadNoEncontradaException;
import integrado.prog2.exception.StockInvalidoException;
import integrado.prog2.exception.ValidacionException;
import integrado.prog2.service.PedidoService;
import integrado.prog2.service.ProductoService;
import integrado.prog2.service.UsuarioService;
 
import java.util.List;
import java.util.Scanner;
 
public class MenuPedidos {
    private final PedidoService pedidoService;
    private final UsuarioService usuarioService;
    private final ProductoService productoService;
    private final Scanner sc;
 
    public MenuPedidos(PedidoService ps, UsuarioService us, ProductoService prods, Scanner sc) {
        this.pedidoService = ps;
        this.usuarioService = us;
        this.productoService = prods;
        this.sc = sc;
    }
 
    public void mostrar() {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n--- PEDIDOS ---");
            System.out.println("1. Listar todos");
            System.out.println("2. Listar por usuario");
            System.out.println("3. Ver detalles de un pedido");
            System.out.println("4. Crear pedido");
            System.out.println("5. Actualizar estado / forma de pago");
            System.out.println("6. Eliminar pedido");
            System.out.println("0. Volver");
            System.out.print("Seleccione: ");
            int op = Consola.leerEntero(sc);
            switch (op) {
                case 1 -> listar();
                case 2 -> listarPorUsuario();
                case 3 -> verDetalles();
                case 4 -> crear();
                case 5 -> actualizar();
                case 6 -> eliminar();
                case 0 -> volver = true;
                default -> System.out.println("[!] Opcion invalida.");
            }
        }
    }
 
    private void listar() {
        List<Pedido> lista = pedidoService.listar();
        if (lista.isEmpty()) { System.out.println("No hay pedidos registrados."); return; }
        System.out.println("\n=== PEDIDOS ===");
        lista.forEach(System.out::println);
    }
 
    private void listarPorUsuario() {
        usuarioService.listar().forEach(System.out::println);
        System.out.print("ID de usuario: ");
        Long uid = Consola.leerLong(sc);
        List<Pedido> lista = pedidoService.listarPorUsuario(uid);
        if (lista.isEmpty()) { System.out.println("No hay pedidos para ese usuario."); return; }
        lista.forEach(System.out::println);
    }
 
    private void verDetalles() {
        listar();
        System.out.print("ID de pedido: ");
        Long id = Consola.leerLong(sc);
        try {
            Pedido p = pedidoService.buscarPorId(id);
            System.out.println("\n" + p);
            System.out.println("  Detalles:");
            // Upcasting: recorremos la lista usando el tipo padre Base
            for (Base item : p.getDetalles()) {
                // instanceof: verificamos el tipo real en tiempo de ejecucion
                if (item instanceof DetallePedido d && !d.isEliminado()) {
                    // Downcasting: convertimos Base a DetallePedido para acceder a sus métodos
                    DetallePedido detalle = (DetallePedido) item;
                    System.out.println(detalle);
                }
            }
            System.out.printf("  TOTAL: $%.2f%n", p.getTotal());
        } catch (EntidadNoEncontradaException e) {
            System.out.println("[!] " + e.getMessage());
        }
    }
 
    private void crear() {
        List<Usuario> usuarios = usuarioService.listar();
        if (usuarios.isEmpty()) {
            System.out.println("[!] No hay usuarios disponibles. Crea uno primero.");
            return;
        }
        System.out.println("Usuarios disponibles:");
        usuarios.forEach(System.out::println);
        System.out.print("ID de usuario: ");
        Long uid = Consola.leerLong(sc);
 
        try {
            Usuario usuario = usuarioService.buscarPorId(uid);
 
            System.out.println("Forma de pago:");
            FormaPago.mostrarOpciones();
            System.out.print("Seleccione: ");
            FormaPago fp = FormaPago.fromIndex(Consola.leerEntero(sc));
 
            // Creamos el pedido en memoria (no se agrega aun a la coleccion)
            Pedido pedido = pedidoService.crear(usuario, fp);
            boolean agregarMas = true;
 
            while (agregarMas) {
                List<Producto> productos = productoService.listar();
                if (productos.isEmpty()) {
                    System.out.println("[!] No hay productos disponibles.");
                    break;
                }
                System.out.println("\nProductos disponibles:");
                productos.forEach(System.out::println);
                System.out.print("ID de producto (0 para terminar): ");
                Long pid = Consola.leerLong(sc);
                if (pid == 0) break;
 
                try {
                    Producto prod = productoService.buscarPorId(pid);
                    int cant;
                    while (true) {
                        System.out.print("Cantidad: ");
                        String cantStr = sc.nextLine().trim();
                        if (cantStr.matches("\\d+") && Integer.parseInt(cantStr) > 0) {
                            cant = Integer.parseInt(cantStr);
                            break;
                        }
                        System.out.println("[!] Ingrese un numero entero mayor a 0.");
                    }
                    pedidoService.agregarDetalle(pedido, cant, prod);
                    System.out.printf("[OK] Detalle agregado. Subtotal parcial: $%.2f%n", pedido.getTotal());
                } catch (EntidadNoEncontradaException e) {
                    System.out.println("[!] " + e.getMessage());
                } catch (StockInvalidoException e) {
                    // HU-PED-02: capturar error y cancelar pedido para no dejar inconsistencia
                    System.out.println("[!] " + e.getMessage());
                    System.out.println("[!] El pedido fue cancelado.");
                    return;
                }
 
                System.out.print("Agregar otro producto? (S/N): ");
                agregarMas = sc.nextLine().equalsIgnoreCase("S");
            }
 
            if (pedido.getDetalles().isEmpty()) {
                System.out.println("[!] El pedido no tiene detalles. No se guardo.");
                return;
            }
 
            pedido.calcularTotal();
            // Método default de la interfaz Calculable
            System.out.println(pedido.getResumen());
            pedidoService.confirmarPedido(pedido);
            System.out.printf("[OK] Pedido creado con ID: %d | Total: $%.2f%n",
                    pedido.getId(), pedido.getTotal());
 
        } catch (EntidadNoEncontradaException | ValidacionException | IllegalArgumentException e) {
            System.out.println("[!] " + e.getMessage());
        }
    }
 
    private void actualizar() {
        listar();
        System.out.print("ID de pedido: ");
        Long id = Consola.leerLong(sc);
        try {
            pedidoService.buscarPorId(id);
            System.out.println("Nuevo estado (0 para no cambiar):");
            Estado.mostrarOpciones();
            System.out.print("Seleccione: ");
            int estOp = Consola.leerEntero(sc);
            Estado estado = estOp == 0 ? null : Estado.fromIndex(estOp);
 
            System.out.println("Nueva forma de pago (0 para no cambiar):");
            FormaPago.mostrarOpciones();
            System.out.print("Seleccione: ");
            int fpOp = Consola.leerEntero(sc);
            FormaPago fp = fpOp == 0 ? null : FormaPago.fromIndex(fpOp);
 
            pedidoService.actualizarEstado(id, estado, fp);
            System.out.println("[OK] Pedido actualizado.");
        } catch (EntidadNoEncontradaException | IllegalArgumentException e) {
            System.out.println("[!] " + e.getMessage());
        }
    }
 
    private void eliminar() {
        listar();
        System.out.print("ID a eliminar: ");
        Long id = Consola.leerLong(sc);
        try {
            Pedido p = pedidoService.buscarPorId(id);
            System.out.print("Confirmar eliminacion del pedido ID " + p.getId() + " (S/N): ");
            if (sc.nextLine().equalsIgnoreCase("S")) {
                pedidoService.eliminar(id);
                System.out.println("[OK] Pedido eliminado (baja logica).");
            } else {
                System.out.println("Operacion cancelada.");
            }
        } catch (EntidadNoEncontradaException e) {
            System.out.println("[!] " + e.getMessage());
        }
    }
}
