package ru.yandex.practicum.scooter.tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.scooter.base.BaseTest;
import ru.yandex.practicum.scooter.model.Courier;
import ru.yandex.practicum.scooter.utils.DataGenerator;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Epic("Яндекс.Самокат API")
@Feature("Получение количества заказов курьера")
class CourierOrdersCountTest extends BaseTest {

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
        courierClient.getOrdersCount(0)
                .then()
                .statusCode(400)
                .body("message", containsString("Недостаточно данных для поиска"));
    }

    @Test
    @Story("Запрос с несуществующим номером")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Запрос с несуществующим номером курьера возвращает ошибку")
    void getOrdersCountWithNonExistentIdReturnsError() {
        courierClient.getOrdersCount(999999999)
                .then()
                .statusCode(404)
                .body("message", containsString("Курьер не найден"));
    }
}
