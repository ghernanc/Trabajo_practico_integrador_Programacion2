
package integrado.prog2.entities;

public interface Calculable {
    // Método abstracto: obligatorio implementar
    void calcularTotal();

    // Método default: implementación base disponible en la interfaz
    default String resumenTotal(double total) {
        return String.format("Total calculado: $%.2f", total);
    }
}

