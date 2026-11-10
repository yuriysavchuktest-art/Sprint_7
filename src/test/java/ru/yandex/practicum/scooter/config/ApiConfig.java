package ru.yandex.practicum.scooter.config;

/**
 * Конфигурация тестового стенда.
 */
public final class ApiConfig {

    public static final String BASE_URL = System.getProperty(
            "base.url", "https://qa-scooter.education-services.ru");

    public static final String COURIER_PATH = "/api/v1/courier";
    public static final String COURIER_LOGIN_PATH = "/api/v1/courier/login";
    public static final String ORDERS_PATH = "/api/v1/orders";
    public static final String ORDERS_CANCEL_PATH = "/api/v1/orders/cancel";

    private ApiConfig() {
    }
}
