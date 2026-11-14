package ru.yandex.practicum.scooter.client;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import ru.yandex.practicum.scooter.config.ApiConfig;
import ru.yandex.practicum.scooter.model.Courier;
import ru.yandex.practicum.scooter.model.CourierCredentials;

import static io.restassured.RestAssured.given;

/**
 * Клиент для работы с ручками курьера.
 */
public class CourierClient {

    private RequestSpecification requestSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(ApiConfig.BASE_URL)
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilter(new AllureRestAssured())
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();
    }

    @Step("Courier - Создание курьера с логином {courier.login}")
    public Response create(Courier courier) {
        return given()
                .spec(requestSpec())
                .body(courier)
                .when()
                .post(ApiConfig.COURIER_PATH);
    }

    @Step("Courier - Авторизация курьера в системе с логином {credentials.login}")
    public Response login(CourierCredentials credentials) {
        return given()
                .spec(requestSpec())
                .body(credentials)
                .when()
                .post(ApiConfig.COURIER_LOGIN_PATH);
    }

    @Step("Courier - Удаление курьера с id {courierId}")
    public Response delete(int courierId) {
        return given()
                .spec(requestSpec())
                .when()
                .delete(ApiConfig.COURIER_PATH + "/" + courierId);
    }

    @Step("Courier - Удаление курьера без id")
    public Response deleteWithoutId() {
        return given()
                .spec(requestSpec())
                .when()
                .delete(ApiConfig.COURIER_PATH);
    }

    @Step("Couriers - Получить количество заказов курьера с id {courierId}")
    public Response getOrdersCount(int courierId) {
        return given()
                .spec(requestSpec())
                .when()
                .get(ApiConfig.COURIER_PATH + "/" + courierId + "/ordersCount");
    }

    /**
     * Возвращает id курьера или -1, если авторизоваться не удалось.
     */
    public int getCourierId(CourierCredentials credentials) {
        Response response = login(credentials);
        if (response.statusCode() == 200) {
            Integer id = response.jsonPath().get("id");
            return id == null ? -1 : id;
        }
        return -1;
    }
}
