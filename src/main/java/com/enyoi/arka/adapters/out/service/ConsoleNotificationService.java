package com.enyoi.arka.adapters.out.service;

import com.enyoi.arka.domain.ports.out.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsoleNotificationService implements NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(ConsoleNotificationService.class);

    @Override
    public void notifyOrderStatusChange(String orderId, String customerEmail, String status) {
        String message = String.format("[CAMBIO ESTADO ORDEN] Orden %s para cliente %s cambió a estado: %s", orderId, customerEmail, status);
        logger.info(message);
    }

    @Override
    public void notifyLowStockAlert(String productName, int currentStock) {
        String message = String.format("[ALERTA STOCK BAJO] El producto '%s' tiene stock bajo: %d unidades restantes", productName, currentStock);
        logger.warn(message);
    }
}
