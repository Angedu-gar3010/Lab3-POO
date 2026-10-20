import java.util.Scanner;

/** Consola: recoge entradas y delega la lógica al sistema. */
public final class Main {
    private final Scanner entrada;
    private final SistemaTecniHogar sistema;

    public Main() {
        entrada = new Scanner(System.in, "UTF-8");
        sistema = new SistemaTecniHogar();
        sistema.cargarDatosIniciales();
    }
    public static void main(String[] args) { new Main().ejecutar(); }

    public void ejecutar() {
        boolean continuar = true;
        System.out.println("TecniHogar - datos iniciales cargados, ingresos Q0.00.");
        while (continuar) {
            System.out.println("\n1 Cotizar o registrar orden\n2 Registrar cuadrilla\n3 Crear jornada"
                    + "\n4 Asignar orden\n5 Retirar orden\n6 Iniciar jornada\n7 Registrar resultado"
                    + "\n8 Consultar órdenes\n9 Consultar cuadrillas\n10 Consultar jornadas\n11 Reportes\n0 Salir");
            try {
                int opcion = entero("Opción: ");
                switch (opcion) {
                    case 1: cotizarORegistrar(); break;
                    case 2:
                        sistema.registrarCuadrilla(new Cuadrilla(texto("Código: "), decimal("Capacidad en horas: "),
                                siNo("¿Certificada en refrigeración? (s/n): ")));
                        System.out.println("Cuadrilla registrada."); break;
                    case 3:
                        sistema.crearJornada(texto("Código de jornada: "), texto("Código de cuadrilla: "));
                        System.out.println("Jornada creada."); break;
                    case 4:
                        sistema.asignarOrden(texto("Código de jornada: "), texto("Código de orden: "));
                        System.out.println("Orden asignada."); break;
                    case 5:
                        sistema.retirarOrden(texto("Código de jornada: "), texto("Código de orden: "));
                        System.out.println("Orden retirada."); break;
                    case 6:
                        sistema.iniciarJornada(texto("Código de jornada: "));
                        System.out.println("Jornada iniciada; cargos adicionales registrados si corresponden."); break;
                    case 7:
                        sistema.registrarResultado(texto("Código de jornada: "), texto("Código de orden: "),
                                siNo("¿Visita completada con éxito? (s/n): "));
                        System.out.println("Resultado registrado."); break;
                    case 8: consultarOrdenes(); break;
                    case 9: consultarCuadrillas(); break;
                    case 10: consultarJornadas(); break;
                    case 11: System.out.println(sistema.generarReportes()); break;
                    case 0: continuar = false; break;
                    default: System.out.println("Opción no válida.");
                }
            } catch (IllegalArgumentException ex) {
                System.out.println("Operación rechazada: " + ex.getMessage());
            } catch (java.util.NoSuchElementException ex) {
                continuar = false;
            }
        }
        System.out.println("Programa finalizado.");
    }

    private void cotizarORegistrar() {
        OrdenServicio propuesta = leerOrden();
        System.out.println(sistema.cotizar(propuesta));
        if (siNo("¿Acepta registrar y pagar la orden? (s/n): ")) {
            sistema.registrarOrden(propuesta);
            System.out.println("Orden registrada y cobrada; estado pendiente.");
        } else System.out.println("Cotización finalizada sin registrar ni cobrar.");
    }
    private OrdenServicio leerOrden() {
        System.out.println("1 Diagnóstico | 2 Instalación | 3 Instalación eléctrica certificada | 4 Mantenimiento de aire acondicionado");
        int tipo = entero("Categoría: ");
        Validacion.exigir(tipo >= 1 && tipo <= 4, "Categoría inválida.");
        String codigo = texto("Código: ");
        String nombre = texto("Cliente: ");
        String direccion = texto("Dirección: ");
        double horas = decimal("Horas estimadas (puede usar punto o coma decimal): ");
        int seleccion = entero("Zona (1 metropolitana / 2 periférica): ");
        Validacion.exigir(seleccion == 1 || seleccion == 2, "Zona inválida.");
        Zona zona = seleccion == 1 ? Zona.METROPOLITANA : Zona.PERIFERICA;
        // Esta selección crea objetos; las decisiones de negocio viven en sus clases.
        switch (tipo) {
            case 1: return new Diagnostico(codigo, nombre, direccion, horas, zona);
            case 2: return new Instalacion(codigo, nombre, direccion, horas, zona, entero("Puntos: "));
            case 3: return new InstalacionElectricaCertificada(codigo, nombre, direccion, horas, zona,
                    entero("Puntos: "), decimal("Valor de materiales: "));
            default: return new MantenimientoAireAcondicionado(codigo, nombre, direccion, horas, zona, entero("BTU: "));
        }
    }
    private void consultarOrdenes() {
        for (OrdenServicio orden : sistema.getOrdenes()) {
            System.out.println(orden.getCodigo() + " | " + orden.getCategoria() + " | Cliente: " + orden.getNombreCliente()
                    + " | Dirección: " + orden.getDireccion() + " | " + orden.getZona()
                    + " | Horas: " + orden.getHorasEstimadas() + " | " + orden.getEstado()
                    + " | Visitas: " + orden.getCantidadVisitas() + "/" + orden.getMaxVisitas()
                    + " | Costo: " + SistemaTecniHogar.dinero(orden.calcularCosto())
                    + " | Atención: " + orden.calcularTiempoAtencion() + " días hábiles");
        }
    }
    private void consultarCuadrillas() {
        for (Cuadrilla cuadrilla : sistema.getCuadrillas()) {
            System.out.println(cuadrilla.getCodigo() + " | Capacidad: " + cuadrilla.getCapacidadHoras()
                    + " horas | Certificación en refrigeración: " + (cuadrilla.isCertificacionRefrigeracion() ? "Sí" : "No"));
        }
    }
    private void consultarJornadas() {
        for (Jornada jornada : sistema.getJornadas()) {
            System.out.println(jornada.getCodigo() + " | " + jornada.getEstado() + " | Cuadrilla: "
                    + jornada.getCuadrilla().getCodigo() + " | Horas: " + jornada.getHorasAsignadas()
                    + " | Disponible: " + jornada.getCapacidadDisponible());
            for (OrdenServicio orden : jornada.getOrdenes()) {
                System.out.println("  " + orden.getCodigo() + (jornada.tieneResultado(orden.getCodigo()) ? " — resultado registrado" : ""));
            }
        }
    }
    private String texto(String mensaje) {
        System.out.print(mensaje);
        return Validacion.texto(entrada.nextLine(), "El dato");
    }
    private int entero(String mensaje) {
        try { return Integer.parseInt(texto(mensaje)); }
        catch (NumberFormatException ex) { throw new IllegalArgumentException("Se esperaba un número entero."); }
    }
    private double decimal(String mensaje) {
        try { return Validacion.positivo(Double.parseDouble(texto(mensaje).replace(',', '.')), "El número"); }
        catch (NumberFormatException ex) { throw new IllegalArgumentException("Se esperaba un número decimal."); }
    }
    private boolean siNo(String mensaje) {
        String valor = texto(mensaje);
        Validacion.exigir(valor.equalsIgnoreCase("s") || valor.equalsIgnoreCase("n"), "Responda s o n.");
        return valor.equalsIgnoreCase("s");
    }
}
