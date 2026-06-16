package integrado.prog2.service;

import integrado.prog2.entities.Categoria;
import integrado.prog2.entities.Producto;
import integrado.prog2.exception.EntidadNoEncontradaException;
import integrado.prog2.exception.ValidacionException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class ProductoService {
    private final List<Producto> productos = new ArrayList<>();
    private final AtomicLong contador = new AtomicLong(1);

    public Producto crear(String nombre, double precio, String descripcion, int stock,
                          String imagen, boolean disponible, Categoria categoria)
            throws ValidacionException {
        if (nombre == null || nombre.isBlank())
            throw new ValidacionException("El nombre no puede estar vacio.");
        if (precio < 0)
            throw new ValidacionException("El precio no puede ser negativo.");
        if (stock < 0)
            throw new ValidacionException("El stock no puede ser negativo.");
        Producto p = new Producto(contador.getAndIncrement(), nombre.trim(), precio,
                descripcion, stock, imagen, disponible, categoria);
        productos.add(p);
        return p;
    }

    public List<Producto> listar() {
        return productos.stream().filter(p -> !p.isEliminado()).toList();
    }

    public List<Producto> listarPorCategoria(Long categoriaId) {
        return productos.stream()
                .filter(p -> !p.isEliminado() && p.getCategoria() != null
                        && p.getCategoria().getId().equals(categoriaId))
                .toList();
    }

    public Producto buscarPorId(Long id) throws EntidadNoEncontradaException {
        return productos.stream()
                .filter(p -> p.getId().equals(id) && !p.isEliminado())
                .findFirst()
                .orElseThrow(() -> new EntidadNoEncontradaException("Producto con ID " + id + " no encontrado."));
    }

    public void editar(Long id, String nombre, Double precio, String descripcion,
                       Integer stock, String imagen, Boolean disponible, Categoria categoria)
            throws EntidadNoEncontradaException, ValidacionException {
        Producto p = buscarPorId(id);
        if (nombre != null && !nombre.isBlank()) p.setNombre(nombre.trim());
        if (precio != null) {
            if (precio < 0) throw new ValidacionException("El precio no puede ser negativo.");
            p.setPrecio(precio);
        }
        if (descripcion != null && !descripcion.isBlank()) p.setDescripcion(descripcion.trim());
        if (stock != null) {
            if (stock < 0) throw new ValidacionException("El stock no puede ser negativo.");
            p.setStock(stock);
        }
        if (imagen != null && !imagen.isBlank()) p.setImagen(imagen.trim());
        if (disponible != null) p.setDisponible(disponible);
        if (categoria != null) p.setCategoria(categoria);
    }

    public void eliminar(Long id) throws EntidadNoEncontradaException {
        Producto p = buscarPorId(id);
        p.setEliminado(true);
    }

    public boolean tieneProductos(Long categoriaId) {
        return productos.stream()
                .anyMatch(p -> !p.isEliminado() && p.getCategoria() != null
                        && p.getCategoria().getId().equals(categoriaId));
    }
}
