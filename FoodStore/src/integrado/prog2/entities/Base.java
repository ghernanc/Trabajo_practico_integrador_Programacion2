
package integrado.prog2.entities;
 
import java.time.LocalDateTime;
 
public abstract class Base {
    private Long id;
    private boolean eliminado;
    protected LocalDateTime createdAt; // protected: accesible desde subclases
 
    public Base(Long id) {
        this.id = id;
        this.eliminado = false;
        this.createdAt = LocalDateTime.now();
    }
 
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
 
    public boolean isEliminado() { return eliminado; }
    public void setEliminado(boolean eliminado) { this.eliminado = eliminado; }
 
    public LocalDateTime getCreatedAt() { return createdAt; }
}


