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
import ru.yandex.practicum.scooter.model.CourierCredentials;
import ru.yandex.practicum.scooter.utils.DataGenerator;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("Яндекс.Самокат API")
@Feature("Удаление курьера")
class CourierDeleteTest extends BaseTest {

    @Test
    @Story("Успешное удаление курьера")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Удаление существующего курьера возвращает ok: true")
    void deleteExistingCourierReturnsOk() {
        Courier courier = DataGenerator.randomCourier();
        registerCourier(courier);

        courierClient.create(courier)
                .then()
                .statusCode(201);

        int courierId = courierClient.getCourierId(
                new CourierCredentials(courier.getLogin(), courier.getPassword()));
        assertTrue(courierId > 0, "id курьера должен быть положительным");

        Response response = courierClient.delete(courierId);

        response.then()
                .statusCode(200)
                .body("ok", equalTo(true));
    }

    @Test
    @Story("Удаление несуществующего курьера")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Удаление курьера с несуществующим id возвращает ошибку")
    void deleteNonExistentCourierReturnsError() {
        Response response = courierClient.delete(999999999);

        response.then()
                .statusCode(404)
                .body("message", containsString("Курьер не найден"));
    }

    @Test
    @Story("Удаление курьера без id")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Удаление курьера без id возвращает ошибку")
    void deleteCourierWithoutIdReturnsError() {
        Response response = courierClient.delete(0);

        response.then()
                .statusCode(400)
                .body("message", containsString("Недостаточно данных"));
    }

    @Test
    @Story("Повторное удаление курьера")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Повторное удаление уже удаленного курьера возвращает ошибку")
    void deleteAlreadyDeletedCourierReturnsError() {
        Courier courier = DataGenerator.randomCourier();
        registerCourier(courier);

        courierClient.create(courier)
                .then()
                .statusCode(201);

        int courierId = courierClient.getCourierId(
                new CourierCredentials(courier.getLogin(), courier.getPassword()));
        assertTrue(courierId > 0, "id курьера должен быть положительным");

        courierClient.delete(courierId)
                .then()
                .statusCode(200)
                .body("ok", equalTo(true));

        Response secondDeleteAttempt = courierClient.delete(courierId);

        secondDeleteAttempt.then()
                .statusCode(404)
                .body("message", containsString("Курьер не найден"));
    }
}
