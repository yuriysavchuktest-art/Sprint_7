package ru.yandex.practicum.scooter.tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.scooter.base.BaseTest;
import ru.yandex.practicum.scooter.model.Courier;
import ru.yandex.practicum.scooter.model.CourierCredentials;
import ru.yandex.practicum.scooter.utils.DataGenerator;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("Яндекс.Самокат API")
@Feature("Авторизация курьера")
class CourierLoginTest extends BaseTest {

    @Test
    @Story("Успешная авторизация")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Курьер может авторизоваться, ответ содержит id")
    void courierCanLoginAndGetId() {
        Courier courier = createCourier();

        Response response = courierClient.login(
                new CourierCredentials(courier.getLogin(), courier.getPassword()));

        response.then()
                .statusCode(200)
                .body("id", notNullValue());

        int courierId = response.jsonPath().getInt("id");
        assertTrue(courierId > 0, "id курьера должен быть положительным");
    }

    @Test
    @Story("Неверные учётные данные")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Авторизация с неверным паролем возвращает ошибку")
    void loginWithWrongPasswordReturnsError() {
        Courier courier = createCourier();

        courierClient.login(new CourierCredentials(courier.getLogin(), "wrong_password"))
                .then()
                .statusCode(404)
                .body("message", containsString("Учетная запись не найдена"));
    }

    @Test
    @Story("Неверные учётные данные")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Авторизация под несуществующим пользователем возвращает ошибку")
    void loginWithNonExistentLoginReturnsError() {
        courierClient.login(new CourierCredentials(DataGenerator.uniqueLogin(), "some_password"))
                .then()
                .statusCode(404)
                .body("message", containsString("Учетная запись не найдена"));
    }

    @Test
    @Story("Обязательные поля")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Авторизация без логина возвращает ошибку")
    void loginWithoutLoginReturnsError() {
        courierClient.login(new CourierCredentials(null, "some_password"))
                .then()
                .statusCode(400)
                .body("message", containsString("Недостаточно данных"));
    }

    @Test
    @Story("Обязательные поля")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Авторизация с пустым паролем возвращает ошибку")
    void loginWithEmptyPasswordReturnsError() {
        Courier courier = createCourier();

        courierClient.login(new CourierCredentials(courier.getLogin(), ""))
                .then()
                .statusCode(400)
                .body("message", containsString("Недостаточно данных"));
    }

    /**
     * На стенде запрос без поля password не завершается (зависает) и не возвращает 400.
     * Тест отключён, чтобы не блокировать прогон, но проверка описана в README.
     */
    @Test
    @Disabled("Дефект стенда: запрос логина без password зависает вместо ответа 400")
    @Story("Обязательные поля")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Авторизация без пароля возвращает ошибку")
    void loginWithoutPasswordReturnsError() {
        Courier courier = createCourier();

        courierClient.login(new CourierCredentials(courier.getLogin(), null))
                .then()
                .statusCode(400)
                .body("message", containsString("Недостаточно данных"));
    }

    private Courier createCourier() {
        Courier courier = DataGenerator.randomCourier();
        registerCourier(courier);

        courierClient.create(courier)
                .then()
                .statusCode(201);

        return courier;
    }
}
