public abstract class OrdenServicio {
    private final String codigo;
    private final String nombreCliente;
    private final String direccion;
    private final double horasEstimadas;
    private final Zona zona;
    private EstadoOrden estado;
    private int cantidadVisitas;
    private double ingresoRegistro;
    private double ingresoVisitas;

    protected OrdenServicio(String codigo, String nombreCliente, String direccion,
                            double horasEstimadas, Zona zona) {
        this.codigo = Validacion.texto(codigo, "Código de orden");
        this.nombreCliente = Validacion.texto(nombreCliente, "Cliente");
        this.direccion = Validacion.texto(direccion, "Dirección");
        this.horasEstimadas = Validacion.positivo(horasEstimadas, "Horas estimadas");
        Validacion.exigir(zona != null, "Debe indicar una zona.");
        this.zona = zona;
        estado = EstadoOrden.PENDIENTE;
        // Visitas e ingresos empiezan en cero, también para los datos iniciales.
    }

    public abstract double calcularCosto();
    public abstract int calcularTiempoAtencion();
    public abstract int getMaxVisitas();
    public abstract String getCategoria();

    public boolean esCompatible(Cuadrilla cuadrilla) {
        return cuadrilla != null;
    }

    protected double tarifaBase() {
        return zona == Zona.METROPOLITANA ? 100 : 175;
    }

    void cobrarRegistro() {
        Validacion.exigir(estado == EstadoOrden.PENDIENTE && cantidadVisitas == 0 && ingresoRegistro == 0,
                "Solo se puede cobrar una orden nueva.");
        ingresoRegistro = Validacion.redondear(calcularCosto());
    }

    void iniciarVisita() {
        Validacion.exigir(estado == EstadoOrden.PENDIENTE, "La orden debe estar pendiente.");
        Validacion.exigir(cantidadVisitas < getMaxVisitas(), "La orden agotó sus visitas.");
        if (cantidadVisitas > 0) ingresoVisitas += 40;
        cantidadVisitas++;
        estado = EstadoOrden.EN_ATENCION;
    }

    void registrarResultado(boolean exitosa) {
        Validacion.exigir(estado == EstadoOrden.EN_ATENCION, "La orden no está en atención.");
        if (exitosa) {
            estado = EstadoOrden.COMPLETADA;
        } else if (cantidadVisitas == getMaxVisitas()) {
            estado = EstadoOrden.CANCELADA;
        } else {
            estado = EstadoOrden.PENDIENTE;
        }
    }

    public String getCodigo() { return codigo; }
    public String getNombreCliente() { return nombreCliente; }
    public String getDireccion() { return direccion; }
    public double getHorasEstimadas() { return horasEstimadas; }
    public Zona getZona() { return zona; }
    public EstadoOrden getEstado() { return estado; }
    public int getCantidadVisitas() { return cantidadVisitas; }
    public double getIngresoRegistro() { return ingresoRegistro; }
    public double getIngresoVisitas() { return ingresoVisitas; }
}
