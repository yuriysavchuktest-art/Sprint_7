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
import ru.yandex.practicum.scooter.model.Order;

import static io.restassured.RestAssured.given;

/**
 * Клиент для работы с ручками заказов.
 */
public class OrderClient {

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

    @Step("Создание заказа")
    public Response create(Order order) {
        return given()
                .spec(requestSpec())
                .body(order)
                .when()
                .post(ApiConfig.ORDERS_PATH);
    }

    @Step("Получение списка заказов")
    public Response getOrders() {
        return given()
                .spec(requestSpec())
                .when()
                .get(ApiConfig.ORDERS_PATH);
    }

    @Step("Получение списка заказов курьера с id {courierId}")
    public Response getOrders(int courierId) {
        return given()
                .spec(requestSpec())
                .queryParam("courierId", courierId)
                .when()
                .get(ApiConfig.ORDERS_PATH);
    }

    /**
     * Сервис принимает track заказа как query-параметр.
     */
    @Step("Отмена заказа с track {track}")
    public Response cancel(int track) {
        return given()
                .spec(requestSpec())
                .queryParam("track", track)
                .when()
                .put(ApiConfig.ORDERS_CANCEL_PATH);
    }
}
