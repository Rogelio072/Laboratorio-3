import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<OrdenServicio> ordenes = new ArrayList<>();
        List<Cuadrilla> cuadrillas = new ArrayList<>();
        List<Jornada> jornadas = new ArrayList<>();
        GestorIngresos gestorIngresos = new GestorIngresos();

        try {
            cuadrillas.add(new Cuadrilla("C01", 10.0, true));
            cuadrillas.add(new Cuadrilla("C02", 8.0, false));
            cuadrillas.add(new Cuadrilla("C03", 12.0, false));

            ordenes.add(new Diagnostico("D01", "Ana Gomez", "Zona 1", 1.0, "Metropolitana"));
            ordenes.add(new Diagnostico("D02", "Luis Perez", "Antigua", 1.5, "Periferica"));

            ordenes.add(new Instalacion("I01", "Carla Ruiz", "Zona 10", 2.0, "Metropolitana", 4));
            ordenes.add(new Instalacion("I02", "Mario Lopez", "Mixco", 3.0, "Periferica", 5));

            ordenes.add(new InstalacionElectricaCertificada("IEC01", "Tech Corp", "Zona 4", 2.5, "Metropolitana", 6, 2500.0));
            ordenes.add(new InstalacionElectricaCertificada("IEC02", "Juan Solares", "Carretera a El Salvador", 3.0, "Periferica", 2, 1200.0));

            ordenes.add(new MantenimientoAireAcondicionado("MAA01", "Hotel Plaza", "Zona 9", 2.0, "Metropolitana", 12000));
            ordenes.add(new MantenimientoAireAcondicionado("MAA02", "Oficinas Sur", "Zona 14", 1.5, "Metropolitana", 24000));
        } catch (Exception e) {
            System.out.println("Error al cargar datos iniciales: " + e.getMessage());
        }

        int opcion = 0;
        do {
            System.out.println("\n--- SISTEMA DE GESTION TECNIHOGAR ---");
            System.out.println("1. Cotizar Orden");
            System.out.println("2. Registrar Orden");
            System.out.println("3. Registrar Cuadrilla");
            System.out.println("4. Crear Jornada y Gestionar Ordenes");
            System.out.println("5. Iniciar Jornada");
            System.out.println("6. Registrar Resultado de Visita");
            System.out.println("7. Consultar Ordenes y Cuadrillas");
            System.out.println("8. Generar Reportes Gerenciales");
            System.out.println("9. Salir");
            System.out.print("Seleccione una opcion: ");

            try {
                if (scanner.hasNextInt()) {
                    opcion = scanner.nextInt();
                    scanner.nextLine();
                } else {
                    scanner.next();
                    System.out.println("Por favor ingrese un número válido.");
                    continue;
                }

                switch (opcion) {
                    case 1:
                        System.out.println("Cotización generada exitosamente.");
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                    case 5:
                        break;
                    case 6:
                        break;
                    case 7:
                        System.out.println("\n ESTADO DE ORDENES");
                        for (OrdenServicio o : ordenes) {
                            System.out.println("Código: " + o.getCodigo() + " | Cat: " + o.getCategoriaNombre() + " | Estado: " + o.getEstado() + " | Visitas: " + o.getVisitasRealizadas());
                        }
                        break;
                    case 8:
                        System.out.println("\n--- REPORTE GERENCIAL ---");
                        System.out.printf("Ingreso Total por Registro: Q%.2f\n", gestorIngresos.getIngresoRegistro());
                        System.out.printf("Ingreso Total por Visitas Adicionales: Q%.2f\n", gestorIngresos.getIngresoVisitasAdicionales());
                        System.out.println("Ingresos por Categoria:");
                        for (Map.Entry<String, Double> entry : gestorIngresos.getIngresosPorCategoria().entrySet()) {
                            System.out.printf(" - %s: Q%.2f\n", entry.getKey(), entry.getValue());
                        }
                        break;
                    case 9:
                        System.out.println("Saliendo del sistema");
                        break;
                    default:
                        System.out.println("Opción fuera de rango.");
                }
            } catch (Exception e) {
                System.out.println("[ERROR RECHAZADO]: " + e.getMessage());
            }
        } while (opcion != 9);
        scanner.close();
    }
}
