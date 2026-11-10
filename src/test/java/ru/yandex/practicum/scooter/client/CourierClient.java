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

    @Step("Создание курьера с логином {courier.login}")
    public Response create(Courier courier) {
        return given()
                .spec(requestSpec())
                .body(courier)
                .when()
                .post(ApiConfig.COURIER_PATH);
    }

    @Step("Авторизация курьера с логином {credentials.login}")
    public Response login(CourierCredentials credentials) {
        return given()
                .spec(requestSpec())
                .body(credentials)
                .when()
                .post(ApiConfig.COURIER_LOGIN_PATH);
    }

    @Step("Удаление курьера с id {courierId}")
    public Response delete(int courierId) {
        return given()
                .spec(requestSpec())
                .when()
                .delete(ApiConfig.COURIER_PATH + "/" + courierId);
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
