
package integrado.prog2.service;

import integrado.prog2.entities.Usuario;
import integrado.prog2.enums.Rol;
import integrado.prog2.exception.EntidadNoEncontradaException;
import integrado.prog2.exception.ValidacionException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class UsuarioService {
    private final List<Usuario> usuarios = new ArrayList<>();
    private final AtomicLong contador = new AtomicLong(1);

    public Usuario crear(String nombre, String apellido, String mail,
                         String celular, String contrasena, Rol rol)
            throws ValidacionException {
        if (nombre == null || nombre.isBlank())
            throw new ValidacionException("El nombre no puede estar vacio.");
        if (mail == null || mail.isBlank())
            throw new ValidacionException("El mail no puede estar vacio.");
        if (!mail.contains("@"))
            throw new ValidacionException("El formato del mail no es valido.");
        for (Usuario u : usuarios) {
            if (!u.isEliminado() && u.getMail().equalsIgnoreCase(mail))
                throw new ValidacionException("Ya existe un usuario con el mail: " + mail);
        }
        if (celular == null || celular.isBlank())
            throw new ValidacionException("El celular no puede estar vacio.");
        if (!celular.trim().matches("\\d+"))
            throw new ValidacionException("El celular solo puede contener numeros.");
        for (Usuario u : usuarios) {
            if (!u.isEliminado() && u.getCelular().equalsIgnoreCase(celular.trim()))
                throw new ValidacionException("Ya existe un usuario con el celular: " + celular.trim());
        }
        Usuario u = new Usuario(contador.getAndIncrement(), nombre.trim(), apellido.trim(),
                mail.trim(), celular.trim(), contrasena, rol);
        usuarios.add(u);
        return u;
    }

    public List<Usuario> listar() {
        return usuarios.stream().filter(u -> !u.isEliminado()).toList();
    }

    public Usuario buscarPorId(Long id) throws EntidadNoEncontradaException {
        return usuarios.stream()
                .filter(u -> u.getId().equals(id) && !u.isEliminado())
                .findFirst()
                .orElseThrow(() -> new EntidadNoEncontradaException("Usuario con ID " + id + " no encontrado."));
    }

    public void editar(Long id, String nombre, String apellido, String mail,
                       String celular, Rol rol)
            throws EntidadNoEncontradaException, ValidacionException {
        Usuario u = buscarPorId(id);
        if (nombre != null && !nombre.isBlank()) u.setNombre(nombre.trim());
        if (apellido != null && !apellido.isBlank()) u.setApellido(apellido.trim());
        if (mail != null && !mail.isBlank()) {
            if (!mail.contains("@")) throw new ValidacionException("Formato de mail invalido.");
            for (Usuario otro : usuarios) {
                if (!otro.isEliminado() && !otro.getId().equals(id)
                        && otro.getMail().equalsIgnoreCase(mail))
                    throw new ValidacionException("Ese mail ya esta registrado.");
            }
            u.setMail(mail.trim());
        }
        if (celular != null && !celular.isBlank()) {
            if (!celular.trim().matches("\\d+"))
                throw new ValidacionException("El celular solo puede contener numeros.");
            for (Usuario otro : usuarios) {
                if (!otro.isEliminado() && !otro.getId().equals(id)
                        && otro.getCelular().equalsIgnoreCase(celular.trim()))
                    throw new ValidacionException("Ya existe un usuario con el celular: " + celular.trim());
            }
            u.setCelular(celular.trim());
        }
        if (rol != null) u.setRol(rol);
    }

    public boolean existeMail(String mail) {
        return usuarios.stream()
                .anyMatch(u -> !u.isEliminado() && u.getMail().equalsIgnoreCase(mail.trim()));
    }

    public boolean existeCelular(String celular) {
        return usuarios.stream()
                .anyMatch(u -> !u.isEliminado() && u.getCelular().equalsIgnoreCase(celular.trim()));
    }

    public void eliminar(Long id) throws EntidadNoEncontradaException {
        Usuario u = buscarPorId(id);
        u.setEliminado(true);
    }
}

