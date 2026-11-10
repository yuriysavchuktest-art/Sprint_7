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

@Epic("Яндекс.Самокат API")
@Feature("Создание курьера")
class CourierCreationTest extends BaseTest {

    @Test
    @Story("Успешное создание курьера")
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
    @Story("Создание дубликата")
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
                .body("message", containsString("уже используется"));
    }

    @Test
    @Story("Создание курьера с занятым логином")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Создание курьера с существующим логином возвращает ошибку")
    void cannotCreateCourierWithExistingLogin() {
        Courier courier = DataGenerator.randomCourier();
        registerCourier(courier);

        courierClient.create(courier)
                .then()
                .statusCode(201);

        Courier courierWithSameLogin = new Courier(
                courier.getLogin(), "another_password", "Другое имя");

        courierClient.create(courierWithSameLogin)
                .then()
                .statusCode(409)
                .body("message", containsString("уже используется"));
    }

    @Test
    @Story("Обязательные поля")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Нельзя создать курьера без логина")
    void cannotCreateCourierWithoutLogin() {
        Courier courierWithoutLogin = new Courier(null, "some_password", "Тест");

        courierClient.create(courierWithoutLogin)
                .then()
                .statusCode(400)
                .body("message", containsString("Недостаточно данных"));
    }

    @Test
    @Story("Обязательные поля")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Нельзя создать курьера без пароля")
    void cannotCreateCourierWithoutPassword() {
        Courier courierWithoutPassword = new Courier(DataGenerator.uniqueLogin(), null, "Тест");

        courierClient.create(courierWithoutPassword)
                .then()
                .statusCode(400)
                .body("message", containsString("Недостаточно данных"));
    }
}
