public final class Cuadrilla {
    private final String codigo;
    private final double capacidadHoras;
    private final boolean certificacionRefrigeracion;

    public Cuadrilla(String codigo, double capacidadHoras, boolean certificacionRefrigeracion) {
        this.codigo = Validacion.texto(codigo, "Código de cuadrilla");
        this.capacidadHoras = Validacion.positivo(capacidadHoras, "Capacidad de horas");
        this.certificacionRefrigeracion = certificacionRefrigeracion;
    }
    public String getCodigo() { return codigo; }
    public double getCapacidadHoras() { return capacidadHoras; }
    public boolean isCertificacionRefrigeracion() { return certificacionRefrigeracion; }
}
