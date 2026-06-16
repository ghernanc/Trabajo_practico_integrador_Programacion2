package integrado.prog2.entities;

import integrado.prog2.enums.Estado;
import integrado.prog2.enums.FormaPago;
import integrado.prog2.exception.StockInvalidoException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Pedido extends Base implements Calculable {
    private LocalDate fecha;
    private Estado estado;
    private double total;
    private FormaPago formaPago;
    private Usuario usuario;
    private List<DetallePedido> detalles;

    public Pedido(Long id, Usuario usuario, FormaPago formaPago) {
        super(id);
        this.fecha = LocalDate.now();
        this.estado = Estado.PENDIENTE;
        this.total = 0.0;
        this.formaPago = formaPago;
        this.usuario = usuario;
        this.detalles = new ArrayList<>();
    }

    // HU-PED-02: addDetallePedido obligatorio
    public void addDetallePedido(int cantidad, Double precioUnitario, Producto producto)
            throws StockInvalidoException {
        if (cantidad <= 0) {
            throw new StockInvalidoException("La cantidad debe ser mayor a 0.");
        }
        if (producto.getStock() < cantidad) {
            throw new StockInvalidoException("Stock insuficiente para '" + producto.getNombre()
                    + "'. Disponible: " + producto.getStock());
        }
        producto.setStock(producto.getStock() - cantidad);
        double subtotal = cantidad * precioUnitario;
        long detalleId = System.nanoTime();
        DetallePedido detalle = new DetallePedido(detalleId, cantidad, subtotal, producto);
        detalles.add(detalle);
        calcularTotal();
    }

    public DetallePedido findDetallePedidoByProducto(Producto producto) {
        return detalles.stream()
                .filter(d -> d.getProducto().equals(producto) && !d.isEliminado())
                .findFirst().orElse(null);
    }

    public void deleteDetallePedidoByProducto(Producto producto) {
        DetallePedido d = findDetallePedidoByProducto(producto);
        if (d != null) {
            producto.setStock(producto.getStock() + d.getCantidad());
            d.setEliminado(true);
            calcularTotal();
        }
    }

    // Interfaz Calculable
    @Override
    public void calcularTotal() {
        this.total = detalles.stream()
                .filter(d -> !d.isEliminado())
                .mapToDouble(DetallePedido::getSubtotal)
                .sum();
    }

    // Usa el método default de la interfaz Calculable
    public String getResumen() {
        return resumenTotal(this.total);
    }

    public LocalDate getFecha() { return fecha; }
    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }
    public double getTotal() { return total; }
    public FormaPago getFormaPago() { return formaPago; }
    public void setFormaPago(FormaPago formaPago) { this.formaPago = formaPago; }
    public Usuario getUsuario() { return usuario; }
    public List<DetallePedido> getDetalles() { return detalles; }

    @Override
    public String toString() {
        String usuarioStr = usuario != null ? usuario.getNombre() + " " + usuario.getApellido() : "N/A";
        return String.format("[ID: %d] Usuario: %-20s | Estado: %-12s | Pago: %-14s | Total: $%-8.2f | Fecha: %s",
                getId(), usuarioStr, estado, formaPago, total, fecha);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pedido p)) return false;
        return getId().equals(p.getId());
    }
}

