package ru.yandex.practicum.scooter.tests;

import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.scooter.base.BaseTest;
import ru.yandex.practicum.scooter.model.Courier;
import ru.yandex.practicum.scooter.utils.DataGenerator;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;

@Epic("Яндекс.Самокат API")
@Feature("Получение количества заказов курьера")
class CourierOrdersCountTest extends BaseTest {

    private static final int INVALID_COURIER_ID = 0;
    private static final String INSUFFICIENT_LOGIN_DATA_MESSAGE = "Недостаточно данных для входа";
    private static final String COURIER_NOT_FOUND_MESSAGE = "Курьер не найден";

    @Test
    @Story("Успешное получение количества заказов")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Couriers - Получить количество заказов курьера")
    void getCourierOrdersCount() {
        Courier courier = DataGenerator.randomCourier();
        registerCourier(courier);

        courierClient.create(courier)
                .then()
                .statusCode(201);

        int courierId = courierClient.getCourierId(
                new ru.yandex.practicum.scooter.model.CourierCredentials(
                        courier.getLogin(), courier.getPassword()));

        Response response = courierClient.getOrdersCount(courierId);

        response.then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("ordersCount", notNullValue());
    }

    @Test
    @Story("Запрос без номера")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Запрос без номера курьера возвращает ошибку")
    void getOrdersCountWithoutIdReturnsError() {
        courierClient.getOrdersCount(INVALID_COURIER_ID)
                .then()
                .statusCode(400)
                .body("message", containsString(INSUFFICIENT_LOGIN_DATA_MESSAGE));
    }

    @Test
    @Story("Запрос с несуществующим номером")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Запрос с несуществующим номером курьера возвращает ошибку")
    void getOrdersCountWithNonExistentIdReturnsError() {
        courierClient.getOrdersCount(NON_EXISTENT_ID)
                .then()
                .statusCode(404)
                .body("message", containsString(COURIER_NOT_FOUND_MESSAGE));
    }
}
