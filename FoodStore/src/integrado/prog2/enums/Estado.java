package integrado.prog2.enums;

public enum Estado {
    PENDIENTE, CONFIRMADO, TERMINADO, CANCELADO;

    public static Estado fromIndex(int i) {
        return switch (i) {
            case 1 -> PENDIENTE;
            case 2 -> CONFIRMADO;
            case 3 -> TERMINADO;
            case 4 -> CANCELADO;
            default -> throw new IllegalArgumentException("Opcion invalida");
        };
    }

    public static void mostrarOpciones() {
        System.out.println("  1. PENDIENTE");
        System.out.println("  2. CONFIRMADO");
        System.out.println("  3. TERMINADO");
        System.out.println("  4. CANCELADO");
    }
}
