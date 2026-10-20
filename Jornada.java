import java.util.ArrayList;

public final class Jornada {
    private final String codigo;
    private EstadoJornada estado;
    private final Cuadrilla cuadrilla;
    private final ArrayList<OrdenServicio> ordenes;
    private final ArrayList<String> resultados;

    public Jornada(String codigo, Cuadrilla cuadrilla) {
        this.codigo = Validacion.texto(codigo, "Código de jornada");
        Validacion.exigir(cuadrilla != null, "Debe indicar una cuadrilla.");
        this.cuadrilla = cuadrilla;
        estado = EstadoJornada.EN_PREPARACION;
        ordenes = new ArrayList<>();
        resultados = new ArrayList<>();
    }

    void asignar(OrdenServicio orden) {
        Validacion.exigir(estado == EstadoJornada.EN_PREPARACION, "Solo se asignan órdenes en preparación.");
        Validacion.exigir(orden.getEstado() == EstadoOrden.PENDIENTE, "La orden debe estar pendiente.");
        Validacion.exigir(!ordenes.contains(orden), "La orden ya está en esta jornada.");
        Validacion.exigir(orden.esCompatible(cuadrilla), "La cuadrilla no es compatible con esta orden.");
        double horas = Validacion.sumar(getHorasAsignadas(), orden.getHorasEstimadas());
        Validacion.exigir(horas <= cuadrilla.getCapacidadHoras(), "La asignación excede la capacidad.");
        ordenes.add(orden);
    }

    void retirar(OrdenServicio orden) {
        Validacion.exigir(estado == EstadoJornada.EN_PREPARACION, "Solo se retiran órdenes en preparación.");
        Validacion.exigir(ordenes.contains(orden), "La orden no pertenece a esta jornada.");
        ordenes.remove(orden);
    }

    void iniciar() {
        Validacion.exigir(estado == EstadoJornada.EN_PREPARACION, "La jornada no está en preparación.");
        Validacion.exigir(!ordenes.isEmpty(), "No se puede iniciar una jornada vacía.");
        // Primero se revisan todas las órdenes; después se modifican.
        for (OrdenServicio orden : ordenes) {
            Validacion.exigir(orden.getEstado() == EstadoOrden.PENDIENTE, "Hay una orden que no está pendiente.");
            Validacion.exigir(orden.getCantidadVisitas() < orden.getMaxVisitas(), "Hay una orden sin visitas disponibles.");
        }
        for (OrdenServicio orden : ordenes) orden.iniciarVisita();
        estado = EstadoJornada.INICIADA;
    }

    void registrarResultado(OrdenServicio orden, boolean exitosa) {
        Validacion.exigir(estado == EstadoJornada.INICIADA, "La jornada debe estar iniciada.");
        Validacion.exigir(ordenes.contains(orden), "La orden no pertenece a esta jornada.");
        Validacion.exigir(!resultados.contains(orden.getCodigo()), "Esta orden ya tiene resultado registrado.");
        orden.registrarResultado(exitosa);
        resultados.add(orden.getCodigo());
        if (resultados.size() == ordenes.size()) estado = EstadoJornada.CERRADA;
    }

    public double getHorasAsignadas() {
        double horas = 0;
        for (OrdenServicio orden : ordenes) horas = Validacion.sumar(horas, orden.getHorasEstimadas());
        return horas;
    }
    public double getCapacidadDisponible() {
        return Validacion.sumar(cuadrilla.getCapacidadHoras(), -getHorasAsignadas());
    }
    public boolean tieneResultado(String codigoOrden) { return resultados.contains(codigoOrden); }
    public String getCodigo() { return codigo; }
    public EstadoJornada getEstado() { return estado; }
    public Cuadrilla getCuadrilla() { return cuadrilla; }
    public ArrayList<OrdenServicio> getOrdenes() { return new ArrayList<>(ordenes); }
}
