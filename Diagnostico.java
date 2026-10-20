public final class Diagnostico extends OrdenServicio {
    public Diagnostico(String codigo, String nombreCliente, String direccion,
                       double horasEstimadas, Zona zona) {
        super(codigo, nombreCliente, direccion, horasEstimadas, zona);
        Validacion.exigir(horasEstimadas <= 1.5, "El diagnóstico no puede superar 1.5 horas.");
    }

    @Override public double calcularCosto() {
        return getZona() == Zona.METROPOLITANA ? 150 : 225;
    }
    @Override public int calcularTiempoAtencion() {
        return getZona() == Zona.METROPOLITANA ? 1 : 2;
    }
    @Override public int getMaxVisitas() { return 3; }
    @Override public String getCategoria() { return "Diagnóstico"; }
}
