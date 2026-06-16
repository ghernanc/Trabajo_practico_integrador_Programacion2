package integrado.prog2.service;

import integrado.prog2.entities.Categoria;
import integrado.prog2.exception.EntidadNoEncontradaException;
import integrado.prog2.exception.ValidacionException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class CategoriaService {
    private final List<Categoria> categorias = new ArrayList<>();
    private final AtomicLong contador = new AtomicLong(1);

    public Categoria crear(String nombre, String descripcion) throws ValidacionException {
        if (nombre == null || nombre.isBlank())
            throw new ValidacionException("El nombre no puede estar vacio.");
        if (descripcion == null || descripcion.isBlank())
            throw new ValidacionException("La descripcion no puede estar vacia.");
        for (Categoria c : categorias) {
            if (!c.isEliminado() && c.getNombre().equalsIgnoreCase(nombre))
                throw new ValidacionException("Ya existe una categoria con el nombre: " + nombre);
        }
        Categoria nueva = new Categoria(contador.getAndIncrement(), nombre.trim(), descripcion.trim());
        categorias.add(nueva);
        return nueva;
    }

    public List<Categoria> listar() {
        return categorias.stream().filter(c -> !c.isEliminado()).toList();
    }

    public Categoria buscarPorId(Long id) throws EntidadNoEncontradaException {
        return categorias.stream()
                .filter(c -> c.getId().equals(id) && !c.isEliminado())
                .findFirst()
                .orElseThrow(() -> new EntidadNoEncontradaException("Categoria con ID " + id + " no encontrada."));
    }

    public void editar(Long id, String nuevoNombre, String nuevaDesc)
            throws EntidadNoEncontradaException, ValidacionException {
        Categoria c = buscarPorId(id);
        if (nuevoNombre != null && !nuevoNombre.isBlank()) {
            for (Categoria otro : categorias) {
                if (!otro.isEliminado() && !otro.getId().equals(id)
                        && otro.getNombre().equalsIgnoreCase(nuevoNombre))
                    throw new ValidacionException("Ya existe otra categoria con ese nombre.");
            }
            c.setNombre(nuevoNombre.trim());
        }
        if (nuevaDesc != null && !nuevaDesc.isBlank()) c.setDescripcion(nuevaDesc.trim());
    }

    public void eliminar(Long id) throws EntidadNoEncontradaException {
        Categoria c = buscarPorId(id);
        c.setEliminado(true);
    }
}
