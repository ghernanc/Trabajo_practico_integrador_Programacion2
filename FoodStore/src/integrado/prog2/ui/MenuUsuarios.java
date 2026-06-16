

package integrado.prog2.ui;

import integrado.prog2.entities.Usuario;
import integrado.prog2.enums.Rol;
import integrado.prog2.exception.EntidadNoEncontradaException;
import integrado.prog2.exception.ValidacionException;
import integrado.prog2.service.UsuarioService;

import java.util.List;
import java.util.Scanner;

public class MenuUsuarios {
    private final UsuarioService usuarioService;
    private final Scanner sc;

    public MenuUsuarios(UsuarioService us, Scanner sc) {
        this.usuarioService = us;
        this.sc = sc;
    }

    public void mostrar() {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n--- USUARIOS ---");
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

    public void listar() {
        List<Usuario> lista = usuarioService.listar();
        if (lista.isEmpty()) { System.out.println("No hay usuarios cargados."); return; }
        System.out.println("\n=== USUARIOS ===");
        lista.forEach(System.out::println);
    }

    private void crear() {
        System.out.print("Nombre: ");
        String nombre = sc.nextLine();
        while (nombre == null || nombre.isBlank()) {
            System.out.print("[!] El nombre no puede estar vacio. Ingrese nuevamente: ");
            nombre = sc.nextLine();
        }
        System.out.print("Apellido: ");
        String apellido = sc.nextLine();
        while (apellido == null || apellido.isBlank()) {
            System.out.print("[!] El apellido no puede estar vacio. Ingrese nuevamente: ");
            apellido = sc.nextLine();
        }
        System.out.print("Mail: ");
        String mail = sc.nextLine();
        while (true) {
            if (mail == null || mail.isBlank()) {
                System.out.print("[!] El mail no puede estar vacio. Ingrese nuevamente: ");
            } else if (!mail.contains("@")) {
                System.out.print("[!] El formato del mail no es valido (debe contener @). Ingrese nuevamente: ");
            } else if (usuarioService.existeMail(mail)) {
                System.out.print("[!] Ya existe un usuario con ese mail. Ingrese otro: ");
            } else {
                break;
            }
            mail = sc.nextLine();
        }
        System.out.print("Celular: ");
        String celular = sc.nextLine();
        while (true) {
            if (celular == null || celular.isBlank()) {
                System.out.print("[!] El celular no puede estar vacio. Ingrese nuevamente: ");
            } else if (!celular.trim().matches("\\d+")) {
                System.out.print("[!] El celular solo puede contener numeros. Ingrese nuevamente: ");
            } else if (usuarioService.existeCelular(celular)) {
                System.out.print("[!] Ya existe un usuario con ese celular. Ingrese otro: ");
            } else {
                break;
            }
            celular = sc.nextLine();
        }
        System.out.print("Contrasena: ");
        String pass = sc.nextLine();
        try {
            System.out.println("Rol:");
            Rol.mostrarOpciones();
            System.out.print("Seleccione: ");
            Rol rol = Rol.fromIndex(Consola.leerEntero(sc));

            Usuario u = usuarioService.crear(nombre, apellido, mail, celular, pass, rol);
            System.out.println("[OK] Usuario creado con ID: " + u.getId());
        } catch (ValidacionException | IllegalArgumentException e) {
            System.out.println("[!] " + e.getMessage());
        }
    }

    private void editar() {
        listar();
        System.out.print("ID a editar: ");
        Long id = Consola.leerLong(sc);
        try {
            Usuario u = usuarioService.buscarPorId(id);
            System.out.println("Editando: " + u);
            System.out.print("Nuevo nombre (Enter para no cambiar): ");
            String nombre = sc.nextLine();
            System.out.print("Nuevo apellido (Enter para no cambiar): ");
            String apellido = sc.nextLine();
            System.out.print("Nuevo mail (Enter para no cambiar): ");
            String mail = sc.nextLine();
            System.out.print("Nuevo celular (Enter para no cambiar): ");
            String celular = sc.nextLine();
            System.out.println("Nuevo rol (0 para no cambiar):");
            Rol.mostrarOpciones();
            System.out.print("Seleccione: ");
            int rolOp = Consola.leerEntero(sc);
            Rol rol = rolOp == 0 ? null : Rol.fromIndex(rolOp);

            usuarioService.editar(id,
                    nombre.isBlank() ? null : nombre,
                    apellido.isBlank() ? null : apellido,
                    mail.isBlank() ? null : mail,
                    celular.isBlank() ? null : celular,
                    rol);
            System.out.println("[OK] Usuario actualizado.");
        } catch (EntidadNoEncontradaException | ValidacionException | IllegalArgumentException e) {
            System.out.println("[!] " + e.getMessage());
        }
    }

    private void eliminar() {
        listar();
        System.out.print("ID a eliminar: ");
        Long id = Consola.leerLong(sc);
        try {
            Usuario u = usuarioService.buscarPorId(id);
            System.out.print("Confirmar eliminacion de '" + u.getNombre() + " " + u.getApellido() + "' (S/N): ");
            if (sc.nextLine().equalsIgnoreCase("S")) {
                usuarioService.eliminar(id);
                System.out.println("[OK] Usuario eliminado (baja logica).");
            } else {
                System.out.println("Operacion cancelada.");
            }
        } catch (EntidadNoEncontradaException e) {
            System.out.println("[!] " + e.getMessage());
        }
    }
}
