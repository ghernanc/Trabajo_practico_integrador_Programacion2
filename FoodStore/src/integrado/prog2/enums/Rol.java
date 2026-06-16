package integrado.prog2.enums;

public enum Rol {
    ADMIN, USUARIO;

    public static Rol fromIndex(int i) {
        return switch (i) {
            case 1 -> ADMIN;
            case 2 -> USUARIO;
            default -> throw new IllegalArgumentException("Opcion invalida");
        };
    }

    public static void mostrarOpciones() {
        System.out.println("  1. ADMIN");
        System.out.println("  2. USUARIO");
    }
}
