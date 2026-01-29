package com.enyoi.arka.domain.ports.out;

public interface NotificationService {
    void notifyOrderStatusChange(String orderId, String customerEmail, String newStatus);
    void notifyLowStockAlert(String productName, int currentStock);
}
