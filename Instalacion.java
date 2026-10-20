public class Instalacion extends OrdenServicio {
    private final int cantidadPuntos;

    public Instalacion(String codigo, String nombreCliente, String direccion,
                       double horasEstimadas, Zona zona, int cantidadPuntos) {
        super(codigo, nombreCliente, direccion, horasEstimadas, zona);
        Validacion.exigir(cantidadPuntos > 0, "Los puntos deben ser un entero positivo.");
        this.cantidadPuntos = cantidadPuntos;
    }

    @Override public double calcularCosto() {
        double horasPorPuntos = cantidadPuntos * 0.75;
        double horasFacturables = Math.ceil(Math.max(getHorasEstimadas(), horasPorPuntos));
        return tarifaBase() + 90 * horasFacturables;
    }
    @Override public int calcularTiempoAtencion() {
        return getZona() == Zona.METROPOLITANA ? 3 : 5;
    }
    @Override public int getMaxVisitas() { return 3; }
    @Override public String getCategoria() { return "Instalación"; }
    public int getCantidadPuntos() { return cantidadPuntos; }
}
