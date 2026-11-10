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

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;

@Epic("Яндекс.Самокат API")
@Feature("Список заказов")
class OrderListTest extends BaseTest {

    private static final int NON_EXISTENT_COURIER_ID = 999_999_999;

    @Test
    @Story("Получение списка заказов")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("В тело ответа возвращается список заказов")
    void ordersListIsReturned() {
        Response response = orderClient.getOrders();

        response.then()
                .statusCode(200)
                .body("orders", notNullValue())
                .body("orders", instanceOf(List.class));
    }

    @Test
    @Story("Ошибки при получении списка заказов")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Список заказов несуществующего курьера возвращает ошибку")
    void ordersForNonExistentCourierReturnsError() {
        orderClient.getOrders(NON_EXISTENT_COURIER_ID)
                .then()
                .statusCode(404)
                .body("message", containsString("не найден"));
    }
}
