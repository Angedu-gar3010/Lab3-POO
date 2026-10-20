public final class MantenimientoAireAcondicionado extends OrdenServicio {
    private final int btu;

    public MantenimientoAireAcondicionado(String codigo, String nombreCliente, String direccion,
                                          double horasEstimadas, Zona zona, int btu) {
        super(codigo, nombreCliente, direccion, horasEstimadas, zona);
        Validacion.exigir(zona == Zona.METROPOLITANA, "El mantenimiento requiere zona metropolitana.");
        Validacion.exigir(btu >= 9000 && btu <= 60000, "El equipo debe tener entre 9000 y 60000 BTU.");
        this.btu = btu;
    }

    @Override public double calcularCosto() {
        return tarifaBase() + 110 * getHorasEstimadas() + 80;
    }
    @Override public int calcularTiempoAtencion() { return 2; }
    @Override public int getMaxVisitas() { return 1; }
    @Override public String getCategoria() { return "Mantenimiento de aire acondicionado"; }
    @Override public boolean esCompatible(Cuadrilla cuadrilla) {
        return cuadrilla != null && cuadrilla.isCertificacionRefrigeracion();
    }
    public int getBtu() { return btu; }
}
