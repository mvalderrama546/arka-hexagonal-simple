package com.enyoi.arka.domain.entities;

public enum OrderStatus {
    PENDIENTE,
    CONFIRMADO,
    EN_DESPACHO,
    ENTREGADO;

    public boolean canBeConfirmed() {
        return this == PENDIENTE;
    }

    public boolean canBeShipped() {
        return this == CONFIRMADO;
    }

    public boolean canBeDelivered() {
        return this == EN_DESPACHO;
    }

    public boolean isPending() {
        return this == PENDIENTE;
    }
}
