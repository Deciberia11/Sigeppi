package pe.deliciasdelvalle.sigeppi.modelo;

/** Excepción para errores de reglas de negocio. El mensaje se muestra tal cual al usuario (RNF-07). */
public class NegocioException extends Exception {
    public NegocioException(String mensaje) {
        super(mensaje);
    }
}
