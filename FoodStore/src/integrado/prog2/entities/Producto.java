package integrado.prog2.entities;

public class Producto extends Base {
    private String nombre;
    private double precio;
    private String descripcion;
    private int stock;
    private String imagen;
    private boolean disponible;
    private Categoria categoria;

    public Producto(Long id, String nombre, double precio, String descripcion,
                    int stock, String imagen, boolean disponible, Categoria categoria) {
        super(id);
        this.nombre = nombre;
        this.precio = precio;
        this.descripcion = descripcion;
        this.stock = stock;
        this.imagen = imagen;
        this.disponible = disponible;
        this.categoria = categoria;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }

    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    /*@Override
    public String toString() {
        return String.format("[ID: %d] %-20s | Precio: $%-8.2f | Stock: %-4d | Disponible: %-5s | Categoria: %s",
                getId(), nombre, precio, stock, disponible ? "Si" : "No",
                categoria != null ? categoria.getNombre() : "N/A");
    }*/
   @Override
public String toString() {
    return String.format("[ID: %d] %-20s | Precio: $%-8.2f | Stock: %-4d | Disponible: %-5s | Categoria: %-15s | Descripcion: %s",
            getId(), nombre, precio, stock, disponible ? "Si" : "No",
            categoria != null ? categoria.getNombre() : "N/A",
            descripcion);
}
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Producto p)) return false;
        return getId().equals(p.getId());
    }
}
