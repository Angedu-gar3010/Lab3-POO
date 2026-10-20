public final class InstalacionElectricaCertificada extends Instalacion {
    private final double valorMateriales;

    public InstalacionElectricaCertificada(String codigo, String nombreCliente, String direccion,
            double horasEstimadas, Zona zona, int cantidadPuntos, double valorMateriales) {
        super(codigo, nombreCliente, direccion, horasEstimadas, zona, cantidadPuntos);
        this.valorMateriales = Validacion.positivo(valorMateriales, "Valor de materiales");
    }

    @Override public double calcularCosto() {
        return super.calcularCosto() + 120 + valorMateriales * 0.05;
    }
    @Override public int calcularTiempoAtencion() { return super.calcularTiempoAtencion() + 2; }
    @Override public int getMaxVisitas() { return 2; }
    @Override public String getCategoria() { return "Instalación eléctrica certificada"; }
    public double getValorMateriales() { return valorMateriales; }
}
