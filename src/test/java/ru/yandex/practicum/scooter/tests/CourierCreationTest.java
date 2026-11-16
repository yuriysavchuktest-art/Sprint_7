package ru.yandex.practicum.scooter.tests;

import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.scooter.base.BaseTest;
import ru.yandex.practicum.scooter.model.Courier;
import ru.yandex.practicum.scooter.utils.DataGenerator;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

@Epic("Яндекс.Самокат API")
@Feature("Создание курьера")
class CourierCreationTest extends BaseTest {

    private static final String DUPLICATE_LOGIN_ERROR_MESSAGE = "Этот логин уже используется. Попробуйте другой.";
    private static final String INSUFFICIENT_DATA_ERROR_MESSAGE = "Недостаточно данных для создания учетной записи";

    @Test
    @Story("Курьера можно создать")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Курьера можно создать")
    void courierCanBeCreated() {
        Courier courier = DataGenerator.randomCourier();
        registerCourier(courier);

        Response response = courierClient.create(courier);

        response.then()
                .statusCode(201)
                .body("ok", equalTo(true));
    }

    @Test
    @Story("Нельзя создать двух одинаковых курьеров")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Нельзя создать двух одинаковых курьеров")
    void cannotCreateTwoIdenticalCouriers() {
        Courier courier = DataGenerator.randomCourier();
        registerCourier(courier);

        courierClient.create(courier)
                .then()
                .statusCode(201);

        Response secondAttempt = courierClient.create(
                new Courier(courier.getLogin(), courier.getPassword(), courier.getFirstName()));

        secondAttempt.then()
                .statusCode(409)
                .body("message", containsString(DUPLICATE_LOGIN_ERROR_MESSAGE));
    }

    @Test
    @Story("Если одного из полей нет, запрос возвращает ошибку")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Нельзя создать курьера без логина")
    void cannotCreateCourierWithoutLogin() {
        Courier courier = DataGenerator.randomCourier();
        Courier courierWithoutLogin = new Courier(null, courier.getPassword(), courier.getFirstName());

        courierClient.create(courierWithoutLogin)
                .then()
                .statusCode(400)
                .body("message", containsString(INSUFFICIENT_DATA_ERROR_MESSAGE));
    }

    @Test
    @Story("Если одного из полей нет, запрос возвращает ошибку")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Нельзя создать курьера без пароля")
    void cannotCreateCourierWithoutPassword() {
        Courier courier = DataGenerator.randomCourier();
        Courier courierWithoutPassword = new Courier(courier.getLogin(), null, courier.getFirstName());

        courierClient.create(courierWithoutPassword)
                .then()
                .statusCode(400)
                .body("message", containsString(INSUFFICIENT_DATA_ERROR_MESSAGE));
    }

    @Test
    @Story("Чтобы создать курьера, нужно передать в ручку все обязательные поля")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Нельзя создать курьера без обязательных полей")
    void cannotCreateCourierWithoutRequiredFields() {
        Courier courier = DataGenerator.randomCourier();
        Courier emptyCourier = new Courier(null, null, courier.getFirstName());

        courierClient.create(emptyCourier)
                .then()
                .statusCode(400)
                .body("message", containsString(INSUFFICIENT_DATA_ERROR_MESSAGE));
    }
}
