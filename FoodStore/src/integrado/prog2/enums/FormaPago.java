package integrado.prog2.enums;

public enum FormaPago {
    TARJETA, TRANSFERENCIA, EFECTIVO;

    public static FormaPago fromIndex(int i) {
        return switch (i) {
            case 1 -> TARJETA;
            case 2 -> TRANSFERENCIA;
            case 3 -> EFECTIVO;
            default -> throw new IllegalArgumentException("Opcion invalida");
        };
    }

    public static void mostrarOpciones() {
        System.out.println("  1. TARJETA");
        System.out.println("  2. TRANSFERENCIA");
        System.out.println("  3. EFECTIVO");
    }
}
