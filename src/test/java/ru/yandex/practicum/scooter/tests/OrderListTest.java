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
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;

@Epic("Яндекс.Самокат API")
@Feature("Список заказов")
class OrderListTest extends BaseTest {

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

        List<Map<String, Object>> orders = response.jsonPath().getList("orders");
        assertThat("список заказов не должен быть пустым", orders, not(empty()));
    }

    @Test
    @Story("Ошибки при получении списка заказов")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Список заказов несуществующего курьера возвращает ошибку")
    void ordersForNonExistentCourierReturnsError() {
        orderClient.getOrders(999_999_999)
                .then()
                .statusCode(404)
                .body("message", containsString("не найден"));
    }
}
