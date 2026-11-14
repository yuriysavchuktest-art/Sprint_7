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
import ru.yandex.practicum.scooter.model.Order;
import ru.yandex.practicum.scooter.utils.DataGenerator;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

@Epic("Яндекс.Самокат API")
@Feature("Принятие заказа курьером")
class OrderAcceptTest extends BaseTest {

    @Test
    @Story("Успешный запрос возвращает ok: true")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Принять заказ - успешный запрос")
    void acceptOrder() {
        Courier courier = DataGenerator.randomCourier();
        registerCourier(courier);
        courierClient.create(courier).then().statusCode(201);
        
        int courierId = courierClient.getCourierId(
                new CourierCredentials(courier.getLogin(), courier.getPassword()));

        Order order = DataGenerator.randomOrder();
        Response createResponse = orderClient.create(order);
        createResponse.then().statusCode(201);
        
        int track = createResponse.jsonPath().getInt("track");
        registerOrder(track);

        Response orderResponse = orderClient.getOrder(track);
        int orderId = orderResponse.jsonPath().getInt("order.id");

        Response response = orderClient.accept(orderId, courierId);

        response.then()
                .statusCode(200)
                .body("ok", equalTo(true));
    }

    @Test
    @Story("Если не передать id курьера, запрос вернёт ошибку")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Принять заказ - без id курьера")
    void acceptOrderWithoutCourierIdReturnsError() {
        Courier courier = DataGenerator.randomCourier();
        registerCourier(courier);
        courierClient.create(courier).then().statusCode(201);

        Order order = DataGenerator.randomOrder();
        Response createResponse = orderClient.create(order);
        createResponse.then().statusCode(201);

        int track = createResponse.jsonPath().getInt("track");
        registerOrder(track);

        Response orderResponse = orderClient.getOrder(track);
        int orderId = orderResponse.jsonPath().getInt("order.id");

        orderClient.accept(orderId)
                .then()
                .statusCode(400);
    }

    @Test
    @Story("Если передать неверный id курьера, запрос вернёт ошибку")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Принять заказ - неверный id курьера")
    void acceptOrderWithNonExistentCourierIdReturnsError() {
        Order order = DataGenerator.randomOrder();
        Response createResponse = orderClient.create(order);
        createResponse.then().statusCode(201);

        int track = createResponse.jsonPath().getInt("track");
        registerOrder(track);

        Response orderResponse = orderClient.getOrder(track);
        int orderId = orderResponse.jsonPath().getInt("order.id");

        orderClient.accept(orderId, 999999999)
                .then()
                .statusCode(404)
                .body("message", containsString("Курьера с таким id не существует"));
    }

    @Test
    @Story("Если не передать номер заказа, запрос вернёт ошибку")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Принять заказ - без номера заказа")
    void acceptOrderWithoutIdReturnsError() {
        Courier courier = DataGenerator.randomCourier();
        registerCourier(courier);
        courierClient.create(courier).then().statusCode(201);
        
        int courierId = courierClient.getCourierId(
                new CourierCredentials(courier.getLogin(), courier.getPassword()));

        orderClient.acceptWithoutOrderId(courierId)
                .then()
                .statusCode(404);
    }

    @Test
    @Story("Если передать неверный номер заказа, запрос вернёт ошибку")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Запрос с несуществующим номером заказа возвращает ошибку")
    void acceptOrderWithNonExistentIdReturnsError() {
        Courier courier = DataGenerator.randomCourier();
        registerCourier(courier);
        courierClient.create(courier).then().statusCode(201);
        
        int courierId = courierClient.getCourierId(
                new CourierCredentials(courier.getLogin(), courier.getPassword()));

        orderClient.accept(999999999, courierId)
                .then()
                .statusCode(404)
                .body("message", containsString("Заказа с таким id не существует"));
    }

    @Test
    @Story("Заказ уже был принят курьером")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Повторное принятие заказа возвращает ошибку")
    void acceptAlreadyAcceptedOrderReturnsError() {
        Courier courier = DataGenerator.randomCourier();
        registerCourier(courier);
        courierClient.create(courier).then().statusCode(201);
        
        int courierId = courierClient.getCourierId(
                new CourierCredentials(courier.getLogin(), courier.getPassword()));

        Order order = DataGenerator.randomOrder();
        Response createResponse = orderClient.create(order);
        createResponse.then().statusCode(201);
        
        int track = createResponse.jsonPath().getInt("track");
        registerOrder(track);
        
        Response orderResponse = orderClient.getOrder(track);
        int orderId = orderResponse.jsonPath().getInt("order.id");

        orderClient.accept(orderId, courierId).then().statusCode(200);

        orderClient.accept(orderId, courierId)
                .then()
                .statusCode(409)
                .body("message", containsString("Этот заказ уже принят"));
    }
}
