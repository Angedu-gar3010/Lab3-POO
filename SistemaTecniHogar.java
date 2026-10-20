import java.util.ArrayList;
import java.util.Locale;

public final class SistemaTecniHogar {
    private final ArrayList<OrdenServicio> ordenes = new ArrayList<>();
    private final ArrayList<Cuadrilla> cuadrillas = new ArrayList<>();
    private final ArrayList<Jornada> jornadas = new ArrayList<>();

    public SistemaTecniHogar() { }

    public String cotizar(OrdenServicio orden) {
        Validacion.exigir(orden != null, "Debe indicar una orden.");
        return orden.getCategoria() + ": " + dinero(Validacion.redondear(orden.calcularCosto()))
                + " | Atención estimada: " + orden.calcularTiempoAtencion() + " días hábiles.";
    }

    public void registrarOrden(OrdenServicio orden) {
        Validacion.exigir(orden != null, "Debe indicar una orden.");
        for (OrdenServicio existente : ordenes) {
            Validacion.exigir(!existente.getCodigo().equals(orden.getCodigo()), "El código de orden ya existe.");
        }
        orden.cobrarRegistro();
        ordenes.add(orden);
    }

    public void registrarCuadrilla(Cuadrilla cuadrilla) {
        Validacion.exigir(cuadrilla != null, "Debe indicar una cuadrilla.");
        for (Cuadrilla existente : cuadrillas) {
            Validacion.exigir(!existente.getCodigo().equals(cuadrilla.getCodigo()), "El código de cuadrilla ya existe.");
        }
        cuadrillas.add(cuadrilla);
    }

    public void crearJornada(String codigo, String codigoCuadrilla) {
        codigo = Validacion.texto(codigo, "Código de jornada");
        Cuadrilla cuadrilla = buscarCuadrilla(codigoCuadrilla);
        for (Jornada jornada : jornadas) {
            Validacion.exigir(!jornada.getCodigo().equals(codigo), "El código de jornada ya existe.");
            Validacion.exigir(jornada.getCuadrilla() != cuadrilla || jornada.getEstado() == EstadoJornada.CERRADA,
                    "La cuadrilla ya tiene una jornada sin cerrar.");
        }
        jornadas.add(new Jornada(codigo, cuadrilla));
    }

    public void asignarOrden(String codigoJornada, String codigoOrden) {
        Jornada destino = buscarJornada(codigoJornada);
        OrdenServicio orden = buscarOrden(codigoOrden);
        for (Jornada jornada : jornadas) {
            Validacion.exigir(jornada.getEstado() == EstadoJornada.CERRADA || !jornada.getOrdenes().contains(orden),
                    "La orden ya está asignada a una jornada sin cerrar.");
        }
        destino.asignar(orden);
    }

    public void retirarOrden(String codigoJornada, String codigoOrden) {
        buscarJornada(codigoJornada).retirar(buscarOrden(codigoOrden));
    }
    public void iniciarJornada(String codigoJornada) {
        buscarJornada(codigoJornada).iniciar();
    }
    public void registrarResultado(String codigoJornada, String codigoOrden, boolean exitosa) {
        buscarJornada(codigoJornada).registrarResultado(buscarOrden(codigoOrden), exitosa);
    }

    public OrdenServicio buscarOrden(String codigo) {
        codigo = Validacion.texto(codigo, "Código de orden");
        for (OrdenServicio orden : ordenes) {
            if (orden.getCodigo().equals(codigo)) return orden;
        }
        throw new IllegalArgumentException("No existe la orden.");
    }
    public Cuadrilla buscarCuadrilla(String codigo) {
        codigo = Validacion.texto(codigo, "Código de cuadrilla");
        for (Cuadrilla cuadrilla : cuadrillas) {
            if (cuadrilla.getCodigo().equals(codigo)) return cuadrilla;
        }
        throw new IllegalArgumentException("No existe la cuadrilla.");
    }
    public Jornada buscarJornada(String codigo) {
        codigo = Validacion.texto(codigo, "Código de jornada");
        for (Jornada jornada : jornadas) {
            if (jornada.getCodigo().equals(codigo)) return jornada;
        }
        throw new IllegalArgumentException("No existe la jornada.");
    }

    public ArrayList<OrdenServicio> getOrdenes() { return new ArrayList<>(ordenes); }
    public ArrayList<Cuadrilla> getCuadrillas() { return new ArrayList<>(cuadrillas); }
    public ArrayList<Jornada> getJornadas() { return new ArrayList<>(jornadas); }

    // Los totales se calculan desde las órdenes; no se guardan por duplicado.
    public double getIngresoRegistro() {
        double total = 0;
        for (OrdenServicio orden : ordenes) total = Validacion.sumar(total, orden.getIngresoRegistro());
        return total;
    }
    public double getIngresoVisitas() {
        double total = 0;
        for (OrdenServicio orden : ordenes) total = Validacion.sumar(total, orden.getIngresoVisitas());
        return total;
    }
    public double getIngresoTotal() {
        return Validacion.sumar(getIngresoRegistro(), getIngresoVisitas());
    }
    public double getIngresoCategoria(String categoria) {
        double total = 0;
        for (OrdenServicio orden : ordenes) {
            if (orden.getCategoria().equals(categoria)) {
                total = Validacion.sumar(total, orden.getIngresoRegistro());
                total = Validacion.sumar(total, orden.getIngresoVisitas());
            }
        }
        return total;
    }
    public static String dinero(double monto) {
        return String.format(Locale.US, "Q%.2f", monto);
    }

    private ArrayList<String> getCategorias() {
        ArrayList<String> categorias = new ArrayList<>();
        for (OrdenServicio orden : ordenes) {
            if (!categorias.contains(orden.getCategoria())) categorias.add(orden.getCategoria());
        }
        return categorias;
    }

    public String generarReportes() {
        String reporte = "ÓRDENES POR CATEGORÍA Y ESTADO\n";
        for (String categoria : getCategorias()) {
            reporte += categoria + ": ";
            for (EstadoOrden estado : EstadoOrden.values()) {
                int cantidad = 0;
                for (OrdenServicio orden : ordenes) {
                    if (orden.getCategoria().equals(categoria) && orden.getEstado() == estado) cantidad++;
                }
                reporte += estado + "=" + cantidad + "  ";
            }
            reporte += "\n";
        }
        reporte += "\nINGRESOS\nRegistro: " + dinero(getIngresoRegistro());
        reporte += "\nVisitas adicionales: " + dinero(getIngresoVisitas());
        reporte += "\nTotal: " + dinero(getIngresoTotal()) + "\n";
        for (String categoria : getCategorias()) {
            reporte += categoria + ": " + dinero(getIngresoCategoria(categoria)) + "\n";
        }
        reporte += "\nJORNADAS SIN CERRAR\n";
        boolean hayAbiertas = false;
        for (Jornada jornada : jornadas) {
            if (jornada.getEstado() != EstadoJornada.CERRADA) {
                hayAbiertas = true;
                reporte += jornada.getCodigo() + " | " + jornada.getEstado();
                reporte += " | Cuadrilla: " + jornada.getCuadrilla().getCodigo();
                reporte += " | Horas asignadas: " + jornada.getHorasAsignadas();
                reporte += " | Capacidad disponible: " + jornada.getCapacidadDisponible() + "\n";
            }
        }
        if (!hayAbiertas) reporte += "No hay jornadas abiertas.\n";
        return reporte;
    }

    public void cargarDatosIniciales() {
        Validacion.exigir(ordenes.isEmpty() && cuadrillas.isEmpty() && jornadas.isEmpty(),
                "Los datos iniciales solo se cargan en un sistema vacío.");
        // Agregar directamente evita cobrar estas órdenes de demostración.
        ordenes.add(new Diagnostico("D01", "Ana López", "Zona 1", 1, Zona.METROPOLITANA));
        ordenes.add(new Diagnostico("D02", "Luis Pérez", "Villa Nueva", 1.5, Zona.PERIFERICA));
        ordenes.add(new Instalacion("I01", "Marta Ruiz", "Zona 7", 2.5, Zona.METROPOLITANA, 5));
        ordenes.add(new Instalacion("I02", "Carlos Díaz", "Carretera periférica", 2.2, Zona.PERIFERICA, 1));
        ordenes.add(new InstalacionElectricaCertificada("E01", "Sara Paz", "Zona 10", 2.5, Zona.METROPOLITANA, 5, 2000));
        ordenes.add(new InstalacionElectricaCertificada("E02", "Jorge Gil", "Sector periférico", 1.1, Zona.PERIFERICA, 1, 1000));
        ordenes.add(new MantenimientoAireAcondicionado("A01", "Sofía León", "Zona 9", 1.5, Zona.METROPOLITANA, 9000));
        ordenes.add(new MantenimientoAireAcondicionado("A02", "Pedro Sol", "Zona 14", 2, Zona.METROPOLITANA, 60000));
        registrarCuadrilla(new Cuadrilla("C01", 8, true));
        registrarCuadrilla(new Cuadrilla("C02", 8, false));
        registrarCuadrilla(new Cuadrilla("C03", 4, false));
    }
}
