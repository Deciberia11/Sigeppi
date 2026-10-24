package pe.deliciasdelvalle.sigeppi.modelo;

/** Estados del pedido en el orden de la RN007 (sin saltos ni retrocesos). */
public enum EstadoPedido {
    REGISTRADO, EN_PRODUCCION, LISTO_ENTREGA, ENTREGADO;

    /** Devuelve el estado que sigue, o null si el pedido ya fue entregado. */
    public EstadoPedido siguiente() {
        if (this == ENTREGADO) {
            return null;
        }
        return values()[this.ordinal() + 1];
    }
}
