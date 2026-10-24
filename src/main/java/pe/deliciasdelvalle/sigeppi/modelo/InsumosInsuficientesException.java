package pe.deliciasdelvalle.sigeppi.modelo;

import java.util.List;

/** Se lanza cuando faltan insumos y no hay solicitud de compra (RN008, ESC11). */
public class InsumosInsuficientesException extends NegocioException {
    private final List<Faltante> faltantes;

    public InsumosInsuficientesException(List<Faltante> faltantes) {
        super("No hay insumos suficientes y no existe solicitud de compra para: "
                + faltantes.stream().map(f -> f.getInsumo().getNombre()).toList());
        this.faltantes = faltantes;
    }

    public List<Faltante> getFaltantes() {
        return faltantes;
    }
}
