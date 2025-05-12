
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author j0el9
 */


public class EFT_S9_Joel_Eyzaguirre_E {
  // Configuración de zonas y precios
  private static final String[] ZONAS = { "VIP", "Palco", "Platea baja", "Platea alta", "Galería" };
  private static final int[] CAPACIDAD_ZONAS = { 20, 30, 60, 50, 40 };
  private static final double[] PRECIOS_ZONAS = { 25000, 20000, 15000, 12000, 8000 };

  // Configuración de descuentos (ordenados de menor a mayor porcentaje)
  private static final String[] TIPOS_DESCUENTO = {
      "Ninguno",
      "Niños (10%)",
      "Estudiantes (15%)",
      "Mujeres (20%)",
      "Tercera edad (25%)"
  };
  private static final double[] VALORES_DESCUENTO = { 0.0, 0.10, 0.15, 0.20, 0.25 };

  // Estructuras de datos
  private static boolean[][] asientos = new boolean[ZONAS.length][];
  private static ArrayList<Integer> ventasZona = new ArrayList<>();
  private static ArrayList<Integer> ventasAsiento = new ArrayList<>();
  private static ArrayList<String> ventasCliente = new ArrayList<>();
  private static ArrayList<String> ventasDescuento = new ArrayList<>();
  private static ArrayList<Double> ventasTotal = new ArrayList<>();
  private static Scanner scanner = new Scanner(System.in);

  public static void main(String[] args) {
    inicializarAsientos();
    mostrarMenu();
  }

  private static void inicializarAsientos() {
    for (int i = 0; i < ZONAS.length; i++) {
      asientos[i] = new boolean[CAPACIDAD_ZONAS[i]];
      for (int j = 0; j < CAPACIDAD_ZONAS[i]; j++) {
        asientos[i][j] = true;
      }
    }
  }

  private static void mostrarMenu() {
    int opcion;
    do {
      System.out.println("\n=== Teatro Moro ===");
      System.out.println("1. Vender entrada");
      System.out.println("2. Ver ventas");
      System.out.println("3. Eliminar venta");
      System.out.println("4. Mostrar disponibilidad");
      System.out.println("5. Salir");
      System.out.print("Seleccione opción: ");
      opcion = leerEnteroEnRango(1, 5);

      switch (opcion) {
        case 1:
          venderEntrada();
          break;
        case 2:
          mostrarVentas();
          break;
        case 3:
          eliminarVenta();
          break;
        case 4:
          mostrarDisponibilidad();
          break;
        case 5:
          System.out.println("¡Gracias por usar el sistema!");
          break;
        default:
          System.out.println("Opción no válida");
      }
    } while (opcion != 5);
  }

  private static void venderEntrada() {
    System.out.println("\n=== Venta de Entrada ===");

    // Mostrar zonas disponibles
    for (int i = 0; i < ZONAS.length; i++) {
      int disponibles = contarDisponiblesZona(i);
      System.out.println((i + 1) + ". " + ZONAS[i] + " - Precio: $" + PRECIOS_ZONAS[i] +
          " - Disponibles: " + disponibles + "/" + CAPACIDAD_ZONAS[i]);
    }

    System.out.print("Seleccione zona (1-" + ZONAS.length + "): ");
    int zona = leerEnteroEnRango(1, ZONAS.length) - 1;

    if (contarDisponiblesZona(zona) == 0) {
      System.out.println("No hay asientos disponibles en " + ZONAS[zona]);
      return;
    }

    // Mostrar asientos disponibles
    System.out.println("Asientos disponibles en " + ZONAS[zona] + ":");
    for (int i = 0; i < CAPACIDAD_ZONAS[zona]; i++) {
      if (asientos[zona][i]) {
        System.out.print((i + 1) + " ");
      }
    }
    System.out.println();

    System.out.print("Seleccione asiento (1-" + CAPACIDAD_ZONAS[zona] + "): ");
    int asiento = leerEnteroEnRango(1, CAPACIDAD_ZONAS[zona]) - 1;

    if (!asientos[zona][asiento]) {
      System.out.println("El asiento seleccionado no está disponible");
      return;
    }

    // Datos del cliente
    String cliente = leerNombreCliente();

    System.out.print("Edad del cliente: ");
    int edad = leerEnteroEnRango(0, 120);

    System.out.println("Género:");
    System.out.println("1. Masculino");
    System.out.println("2. Femenino");
    System.out.print("Seleccione (1-2): ");
    int genero = leerEnteroEnRango(1, 2);

    System.out.println("¿Es estudiante?");
    System.out.println("1. Sí");
    System.out.println("2. No");
    System.out.print("Seleccione (1-2): ");
    int esEstudiante = leerEnteroEnRango(1, 2);

    // Determinar el mayor descuento aplicable

    double descuento = 0;
    String tipoDescuento = TIPOS_DESCUENTO[0]; // "Ninguno"

    if (edad > 60) {
      descuento = VALORES_DESCUENTO[4];
      tipoDescuento = TIPOS_DESCUENTO[4];
    } else if (genero == 2) {
      descuento = VALORES_DESCUENTO[3];
      tipoDescuento = TIPOS_DESCUENTO[3];
    } else if (esEstudiante == 1) {
      descuento = VALORES_DESCUENTO[2];
      tipoDescuento = TIPOS_DESCUENTO[2];
    } else if (edad < 8) {
      descuento = VALORES_DESCUENTO[1];
      tipoDescuento = TIPOS_DESCUENTO[1];
    }
    // Calcular total
    double total = PRECIOS_ZONAS[zona] * (1 - descuento);

    // Registrar venta
    ventasZona.add(zona);
    ventasAsiento.add(asiento + 1);
    ventasCliente.add(cliente);
    ventasDescuento.add(tipoDescuento);
    ventasTotal.add(total);

    // Marcar asiento como ocupado
    asientos[zona][asiento] = false;

    // Mostrar boleta
    imprimirBoleta(ventasZona.size() - 1); // esto puede generar race condition
  }

  private static void imprimirBoleta(int id) {
    System.out.println("\n=== BOLETA ===");
    System.out.println("Zona: " + ZONAS[ventasZona.get(id)]);
    System.out.println("Asiento: " + ventasAsiento.get(id));
    System.out.println("Cliente: " + ventasCliente.get(id));
    System.out.println("Descuento aplicado: " + ventasDescuento.get(id));
    System.out.println("Total a pagar: $" + ventasTotal.get(id));
    System.out.println("¡Gracias por su compra!");
  }

  private static int contarDisponiblesZona(int zona) {
    int count = 0;
    for (int i = 0; i < asientos[zona].length; i++) {
      if (asientos[zona][i]) {
        count++;
      }
    }
    return count;
  }

  private static String leerNombreCliente() {
    scanner.nextLine();
    while (true) {
      System.out.print("Nombre del cliente: ");
      String nombre = scanner.nextLine().trim();

      if (!nombre.isEmpty() && nombre.matches("[A-Za-z ]+")) { // regex simplecito
        return nombre.toUpperCase();
      }
      System.out.println("Ingrese un nombre válido (solo letras)");
    }
  }

  private static void mostrarVentas() {
    if (ventasZona.isEmpty()) {
      System.out.println("\nNo hay ventas registradas");
      return;
    }

    System.out.println("\n=== Listado de Ventas ===");
    System.out.println("ID  Zona          Asiento  Cliente          Descuento            Total");
    for (int i = 0; i < ventasZona.size(); i++) {
      System.out.println(
          String.format("%-3d %-13s %-8d %-16s %-20s $%.0f",
              (i + 1),
              ZONAS[ventasZona.get(i)],
              ventasAsiento.get(i),
              ventasCliente.get(i),
              ventasDescuento.get(i),
              ventasTotal.get(i)));
    }
  }

  private static void eliminarVenta() {
    if (ventasZona.isEmpty()) {
      System.out.println("\nNo hay ventas para eliminar");
      return;
    }

    mostrarVentas();
    System.out.print("\nIngrese ID de venta a eliminar (1-" + ventasZona.size() + "): ");
    int id = leerEnteroEnRango(1, ventasZona.size()) - 1;

    // Liberar asiento
    int zona = ventasZona.get(id);
    int asiento = ventasAsiento.get(id) - 1;
    asientos[zona][asiento] = true;

    // Eliminar venta
    ventasZona.remove(id);
    ventasAsiento.remove(id);
    ventasCliente.remove(id);
    ventasDescuento.remove(id);
    ventasTotal.remove(id);

    System.out.println("Venta eliminada correctamente");
  }

  private static void mostrarDisponibilidad() {
    System.out.println("\n=== Disponibilidad de Asientos ===");
    for (int i = 0; i < ZONAS.length; i++) {
      int disponibles = contarDisponiblesZona(i);
      System.out.println(ZONAS[i] + ": " + disponibles + "/" + CAPACIDAD_ZONAS[i] + " disponibles");
    }
  }

  private static int leerEnteroEnRango(int min, int max) {
    while (true) {
      try {
        int input = Integer.parseInt(scanner.next());
        if (input >= min && input <= max) {
          return input;
        }
        System.out.print("Por favor ingrese un número entre " + min + " y " + max + ": ");
      } catch (NumberFormatException e) {
        System.out.print("Entrada inválida. Ingrese un número: ");
      }
    }
  }
}