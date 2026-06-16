package integrado.prog2.service;

import integrado.prog2.entities.Pedido;
import integrado.prog2.entities.Producto;
import integrado.prog2.entities.Usuario;
import integrado.prog2.enums.Estado;
import integrado.prog2.enums.FormaPago;
import integrado.prog2.exception.EntidadNoEncontradaException;
import integrado.prog2.exception.StockInvalidoException;
import integrado.prog2.exception.ValidacionException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class PedidoService {
    private final List<Pedido> pedidos = new ArrayList<>();
    private final AtomicLong contador = new AtomicLong(1);

    public Pedido crear(Usuario usuario, FormaPago formaPago) throws ValidacionException {
        if (usuario == null)
            throw new ValidacionException("El pedido debe tener un usuario asociado.");
        return new Pedido(contador.getAndIncrement(), usuario, formaPago);
    }

    public void agregarDetalle(Pedido pedido, int cantidad, Producto producto)
            throws StockInvalidoException {
        pedido.addDetallePedido(cantidad, producto.getPrecio(), producto);
    }

    public void confirmarPedido(Pedido pedido) {
        pedidos.add(pedido);
    }

    public List<Pedido> listar() {
        return pedidos.stream().filter(p -> !p.isEliminado()).toList();
    }

    public List<Pedido> listarPorUsuario(Long usuarioId) {
        return pedidos.stream()
                .filter(p -> !p.isEliminado() && p.getUsuario() != null
                        && p.getUsuario().getId().equals(usuarioId))
                .toList();
    }

    public Pedido buscarPorId(Long id) throws EntidadNoEncontradaException {
        return pedidos.stream()
                .filter(p -> p.getId().equals(id) && !p.isEliminado())
                .findFirst()
                .orElseThrow(() -> new EntidadNoEncontradaException("Pedido con ID " + id + " no encontrado."));
    }

    public void actualizarEstado(Long id, Estado estado, FormaPago formaPago)
            throws EntidadNoEncontradaException {
        Pedido p = buscarPorId(id);
        if (estado != null) p.setEstado(estado);
        if (formaPago != null) p.setFormaPago(formaPago);
    }

    public void eliminar(Long id) throws EntidadNoEncontradaException {
        Pedido p = buscarPorId(id);
        p.setEliminado(true);
        p.getDetalles().forEach(d -> d.setEliminado(true));
    }
}
