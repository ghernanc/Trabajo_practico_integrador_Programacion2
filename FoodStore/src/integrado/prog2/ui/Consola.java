package integrado.prog2.ui;

import java.util.Scanner;

public class Consola {

    public static int leerEntero(Scanner sc) {
        while (true) {
            try {
                String linea = sc.nextLine().trim();
                return Integer.parseInt(linea);
            } catch (NumberFormatException e) {
                System.out.print("[!] Ingrese un numero valido: ");
            }
        }
    }

    public static Long leerLong(Scanner sc) {
        while (true) {
            try {
                String linea = sc.nextLine().trim();
                return Long.parseLong(linea);
            } catch (NumberFormatException e) {
                System.out.print("[!] Ingrese un ID valido: ");
            }
        }
    }

    public static double leerDouble(Scanner sc) {
        while (true) {
            try {
                String linea = sc.nextLine().trim();
                return Double.parseDouble(linea);
            } catch (NumberFormatException e) {
                System.out.print("[!] Ingrese un valor numerico valido: ");
            }
        }
    }
}
