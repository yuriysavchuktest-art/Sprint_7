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

    @Step("Orders - Создание заказа")
    public Response create(Order order) {
        return given()
                .spec(requestSpec())
                .body(order)
                .when()
                .post(ApiConfig.ORDERS_PATH);
    }

    @Step("Orders - Получение списка заказов")
    public Response getOrders() {
        return given()
                .spec(requestSpec())
                .when()
                .get(ApiConfig.ORDERS_PATH);
    }

    @Step("Orders - Получение списка заказов курьера с id {courierId}")
    public Response getOrders(int courierId) {
        return given()
                .spec(requestSpec())
                .queryParam("courierId", courierId)
                .when()
                .get(ApiConfig.ORDERS_PATH);
    }

    @Step("Orders - Получить заказ по его номеру {track}")
    public Response getOrder(int track) {
        return given()
                .spec(requestSpec())
                .queryParam("t", track)
                .when()
                .get(ApiConfig.ORDERS_PATH + "/track");
    }

    @Step("Orders - Получить заказ без указания номера")
    public Response getOrder() {
        return given()
                .spec(requestSpec())
                .when()
                .get(ApiConfig.ORDERS_PATH + "/track");
    }

    @Step("Orders - Принять заказ с id {orderId} курьером с id {courierId}")
    public Response accept(int orderId, int courierId) {
        return given()
                .spec(requestSpec())
                .queryParam("courierId", courierId)
                .when()
                .put(ApiConfig.ORDERS_PATH + "/accept/" + orderId);
    }

    @Step("Orders - Принять заказ с id {orderId} без указания курьера")
    public Response accept(int orderId) {
        return given()
                .spec(requestSpec())
                .when()
                .put(ApiConfig.ORDERS_PATH + "/accept/" + orderId);
    }

    @Step("Orders - Принять заказ без указания номера заказа курьером с id {courierId}")
    public Response acceptWithoutOrderId(int courierId) {
        return given()
                .spec(requestSpec())
                .queryParam("courierId", courierId)
                .when()
                .put(ApiConfig.ORDERS_PATH + "/accept");
    }

    @Step("Orders - Завершить заказ с id {orderId}")
    public Response finish(int orderId) {
        return given()
                .spec(requestSpec())
                .when()
                .put(ApiConfig.ORDERS_PATH + "/finish/" + orderId);
    }

    /**
     * Сервис принимает track заказа как query-параметр.
     */
    @Step("Orders - Отменить заказ с track {track}")
    public Response cancel(int track) {
        return given()
                .spec(requestSpec())
                .queryParam("track", track)
                .when()
                .put(ApiConfig.ORDERS_CANCEL_PATH);
    }
}
