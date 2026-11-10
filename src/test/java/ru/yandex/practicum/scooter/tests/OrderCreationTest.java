package ru.yandex.practicum.scooter.tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.yandex.practicum.scooter.base.BaseTest;
import ru.yandex.practicum.scooter.model.Order;
import ru.yandex.practicum.scooter.utils.DataGenerator;

import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("Яндекс.Самокат API")
@Feature("Создание заказа")
class OrderCreationTest extends BaseTest {

    @ParameterizedTest(name = "Цвет: {0}")
    @Story("Создание заказа с разными цветами")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Заказ можно создать с одним цветом, с двумя цветами и без цвета")
    @MethodSource("colorVariants")
    void orderCanBeCreatedWithDifferentColors(String description, List<String> color) {
        Order order = DataGenerator.randomOrder(color);

        Response response = orderClient.create(order);

        if (response.statusCode() == 201 && response.jsonPath().get("track") != null) {
            registerOrder(response.jsonPath().getInt("track"));
        }

        response.then()
                .statusCode(201)
                .body("track", notNullValue());

        int track = response.jsonPath().getInt("track");
        assertTrue(track > 0, "track заказа должен быть положительным");
    }

    static Stream<Arguments> colorVariants() {
        return Stream.of(
                Arguments.of("BLACK", List.of("BLACK")),
                Arguments.of("GREY", List.of("GREY")),
                Arguments.of("BLACK и GREY", List.of("BLACK", "GREY")),
                Arguments.of("цвет не указан", null)
        );
    }
}
