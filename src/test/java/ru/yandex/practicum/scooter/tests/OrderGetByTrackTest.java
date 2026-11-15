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
import ru.yandex.practicum.scooter.model.Order;
import ru.yandex.practicum.scooter.utils.DataGenerator;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;

@Epic("Яндекс.Самокат API")
@Feature("Получение заказа по номеру")
class OrderGetByTrackTest extends BaseTest {

    private static final String INSUFFICIENT_DATA_FOR_SEARCH_MESSAGE = "Недостаточно данных для поиска";
    private static final String ORDER_NOT_FOUND_MESSAGE = "Заказ не найден";

    @Test
    @Story("Успешное получение заказа по номеру")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Orders - Получить заказ по его номеру")
    void getOrderByTrack() {
        Order order = DataGenerator.randomOrder();
        Response createResponse = orderClient.create(order);
        createResponse.then().statusCode(201);
        
        int track = createResponse.jsonPath().getInt("track");
        registerOrder(track);

        Response response = orderClient.getOrder(track);

        response.then()
                .statusCode(200)
                .body("order", notNullValue())
                .body("order.track", notNullValue());
    }

    @Test
    @Story("Запрос без номера")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Запрос без номера заказа возвращает ошибку")
    void getOrderByTrackWithoutTrackReturnsError() {
        orderClient.getOrder()
                .then()
                .statusCode(400)
                .body("message", containsString(INSUFFICIENT_DATA_FOR_SEARCH_MESSAGE));
    }

    @Test
    @Story("Запрос с несуществующим номером")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Запрос с несуществующим номером заказа возвращает ошибку")
    void getOrderByNonExistentTrackReturnsError() {
        orderClient.getOrder(NON_EXISTENT_ID)
                .then()
                .statusCode(404)
                .body("message", containsString(ORDER_NOT_FOUND_MESSAGE));
    }
}
