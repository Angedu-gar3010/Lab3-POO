import java.math.BigDecimal;
import java.math.RoundingMode;

/** Funciones pequeñas que se reutilizan para no repetir validaciones. */
final class Validacion {
    private Validacion() { }

    static String texto(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException(campo + " no puede estar vacío.");
        }
        return valor.trim();
    }

    static double positivo(double valor, String campo) {
        if (!Double.isFinite(valor) || valor <= 0) {
            throw new IllegalArgumentException(campo + " debe ser positivo y finito.");
        }
        return valor;
    }

    static void exigir(boolean condicion, String mensaje) {
        if (!condicion) throw new IllegalArgumentException(mensaje);
    }

    // BigDecimal se utiliza solo aquí para calcular decimales sin errores de suma.
    static double sumar(double primero, double segundo) {
        return BigDecimal.valueOf(primero).add(BigDecimal.valueOf(segundo)).doubleValue();
    }

    static double redondear(double monto) {
        positivo(monto, "El monto");
        return BigDecimal.valueOf(monto).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
