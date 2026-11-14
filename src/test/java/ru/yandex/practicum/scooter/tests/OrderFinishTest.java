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
@Feature("Завершение заказа")
class OrderFinishTest extends BaseTest {

    @Test
    @Story("Успешное завершение заказа")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Orders - Завершить заказ")
    void finishOrder() {
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

        Response response = orderClient.finish(orderId);

        response.then()
                .statusCode(200)
                .body("ok", equalTo(true));
    }

    @Test
    @Story("Запрос без courierId")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Запрос accept без courierId возвращает ошибку")
    void acceptWithoutCourierIdReturnsError() {
        orderClient.accept(NON_EXISTENT_ID)
                .then()
                .statusCode(400)
                .body("message", containsString("Недостаточно данных для поиска"));
    }

    @Test
    @Story("Запрос с несуществующим номером")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Запрос с несуществующим номером заказа возвращает ошибку")
    void finishOrderWithNonExistentIdReturnsError() {
        orderClient.finish(NON_EXISTENT_ID)
                .then()
                .statusCode(404)
                .body("message", containsString("Заказа с таким id не существует"));
    }

    @Test
    @Story("Заказ нельзя завершить")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Завершение непринятого заказа возвращает ошибку")
    void finishNotAcceptedOrderReturnsError() {
        Order order = DataGenerator.randomOrder();
        Response createResponse = orderClient.create(order);
        createResponse.then().statusCode(201);
        
        int track = createResponse.jsonPath().getInt("track");
        registerOrder(track);
        
        Response orderResponse = orderClient.getOrder(track);
        int orderId = orderResponse.jsonPath().getInt("order.id");

        orderClient.finish(orderId)
                .then()
                .statusCode(409)
                .body("message", containsString("Этот заказ нельзя завершить"));
    }
}
