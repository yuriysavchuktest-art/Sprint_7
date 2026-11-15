package ru.yandex.practicum.scooter.tests;

import io.qameta.allure.*;
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

    private static final String COURIER_ID_POSITIVE_MESSAGE = "id курьера должен быть положительным";
    private static final String COURIER_NOT_FOUND_MESSAGE = "Курьера с таким id нет.";
    private static final String INSUFFICIENT_DATA_MESSAGE = "Недостаточно данных для удаления курьера";

    @Test
    @Story("Успешный запрос возвращает ok: true")
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
        assertTrue(courierId > 0, COURIER_ID_POSITIVE_MESSAGE);

        Response response = courierClient.delete(courierId);

        response.then()
                .statusCode(200)
                .body("ok", equalTo(true));
    }

    @Test
    @Story("Если отправить запрос с несуществующим id, вернётся ошибка")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Удаление курьера с несуществующим id возвращает ошибку")
    void deleteNonExistentCourierReturnsError() {
        Response response = courierClient.delete(NON_EXISTENT_ID);

        response.then()
                .statusCode(404)
                .body("message", containsString(COURIER_NOT_FOUND_MESSAGE));
    }

    @Test
    @Story("Если отправить запрос без id, вернётся ошибка")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Удаление курьера без id возвращает ошибку")
    void deleteCourierWithoutIdReturnsError() {
        Response response = courierClient.deleteWithoutId();

        response.then()
                .statusCode(400)
                .body("message", containsString(INSUFFICIENT_DATA_MESSAGE));
    }

    @Test
    @Story("Неуспешный запрос возвращает соответствующую ошибку")
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
        assertTrue(courierId > 0, COURIER_ID_POSITIVE_MESSAGE);

        courierClient.delete(courierId)
                .then()
                .statusCode(200)
                .body("ok", equalTo(true));

        Response secondDeleteAttempt = courierClient.delete(courierId);

        secondDeleteAttempt.then()
                .statusCode(404)
                .body("message", containsString(COURIER_NOT_FOUND_MESSAGE));
    }
}
