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
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("Яндекс.Самокат API")
@Feature("Логин курьера")
class CourierLoginTest extends BaseTest {

    private static final String COURIER_ID_POSITIVE_MESSAGE = "id курьера должен быть положительным";
    private static final String ACCOUNT_NOT_FOUND_MESSAGE = "Учетная запись не найдена";
    private static final String INSUFFICIENT_LOGIN_DATA_MESSAGE = "Недостаточно данных для входа";

    @Test
    @Story("Курьер может авторизоваться")
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
        assertTrue(courierId > 0, COURIER_ID_POSITIVE_MESSAGE);
    }

    @Test
    @Story("Система вернёт ошибку, если неправильно указать логин или пароль")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Авторизация с неверным паролем возвращает ошибку")
    void loginWithWrongPasswordReturnsError() {
        Courier courier = createCourier();

        courierClient.login(new CourierCredentials(courier.getLogin(), DataGenerator.uniquePassword()))
                .then()
                .statusCode(404)
                .body("message", containsString(ACCOUNT_NOT_FOUND_MESSAGE));
    }

    @Test
    @Story("Система вернёт ошибку, если неправильно указать логин или пароль")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Авторизация с неверным логином возвращает ошибку")
    void loginWithWrongLoginReturnsError() {
        Courier courier = createCourier();

        courierClient.login(new CourierCredentials(DataGenerator.uniqueLogin(), courier.getPassword()))
                .then()
                .statusCode(404)
                .body("message", containsString(ACCOUNT_NOT_FOUND_MESSAGE));
    }

    @Test
    @Story("Если авторизоваться под несуществующим пользователем, запрос возвращает ошибку")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Авторизация под несуществующим пользователем возвращает ошибку")
    void loginWithNonExistentLoginReturnsError() {
        courierClient.login(new CourierCredentials(DataGenerator.uniqueLogin(), DataGenerator.uniquePassword()))
                .then()
                .statusCode(404)
                .body("message", containsString(ACCOUNT_NOT_FOUND_MESSAGE));
    }

    @Test
    @Story("Если авторизоваться под несуществующим пользователем, запрос возвращает ошибку")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Удаленный курьер не может авторизоваться")
    void deletedCourierCannotLogin() {
        Courier courier = createCourier();

        int courierId = courierClient.getCourierId(
                new CourierCredentials(courier.getLogin(), courier.getPassword()));
        courierClient.delete(courierId);

        courierClient.login(new CourierCredentials(courier.getLogin(), courier.getPassword()))
                .then()
                .statusCode(404)
                .body("message", containsString(ACCOUNT_NOT_FOUND_MESSAGE));
    }

    @Test
    @Story("Если какого-то поля нет, запрос возвращает ошибку")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Авторизация без логина возвращает ошибку")
    void loginWithoutLoginReturnsError() {
        Courier courier = createCourier();
        courierClient.login(new CourierCredentials(null, courier.getPassword()))
                .then()
                .statusCode(400)
                .body("message", containsString(INSUFFICIENT_LOGIN_DATA_MESSAGE));
    }

    @Test
    @Story("Если какого-то поля нет, запрос возвращает ошибку")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Авторизация с пустым логина возвращает ошибку")
    void loginEmptyLoginReturnsError() {
        Courier courier = createCourier();
        courierClient.login(new CourierCredentials("", courier.getPassword()))
                .then()
                .statusCode(400)
                .body("message", containsString(INSUFFICIENT_LOGIN_DATA_MESSAGE));
    }

    @Test
    @Story("Если какого-то поля нет, запрос возвращает ошибку")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Авторизация с пустым паролем возвращает ошибку")
    void loginWithEmptyPasswordReturnsError() {
        Courier courier = createCourier();

        courierClient.login(new CourierCredentials(courier.getLogin(), ""))
                .then()
                .statusCode(400)
                .body("message", containsString(INSUFFICIENT_LOGIN_DATA_MESSAGE));
    }

    @Test
    @Story("Если какого-то поля нет, запрос возвращает ошибку")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Авторизация без пароля возвращает ошибку")
    void loginWithoutPasswordReturnsError() {
        Courier courier = createCourier();

        courierClient.login(new CourierCredentials(courier.getLogin(), null))
                .then()
                .statusCode(400)
                .body("message", containsString(INSUFFICIENT_LOGIN_DATA_MESSAGE));
    }

    /**
     * Создает курьера со случайными данными и регистрирует его для автоматического удаления после теста.
     *
     * @return созданный курьер
     */
    private Courier createCourier() {
        Courier courier = DataGenerator.randomCourier();
        registerCourier(courier);

        courierClient.create(courier)
                .then()
                .statusCode(201);

        return courier;
    }
}
