package ru.yandex.practicum.scooter.base;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.practicum.scooter.client.CourierClient;
import ru.yandex.practicum.scooter.client.OrderClient;
import ru.yandex.practicum.scooter.config.ApiConfig;
import ru.yandex.practicum.scooter.model.Courier;
import ru.yandex.practicum.scooter.model.CourierCredentials;

import java.util.ArrayList;
import java.util.List;

/**
 * Базовый класс тестов: настройка RestAssured и удаление тестовых данных после каждого теста.
 */
public abstract class BaseTest {

    private static final Logger LOG = LoggerFactory.getLogger(BaseTest.class);

    protected final CourierClient courierClient = new CourierClient();
    protected final OrderClient orderClient = new OrderClient();

    private final List<Courier> createdCouriers = new ArrayList<>();
    private final List<Integer> createdOrderTracks = new ArrayList<>();

    @BeforeAll
    static void configureRestAssured() {
        RestAssured.baseURI = ApiConfig.BASE_URL;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.config = RestAssuredConfig.config().httpClient(
                HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", 20_000)
                        .setParam("http.socket.timeout", 20_000));
    }

    /**
     * Запоминает курьера, чтобы удалить его после теста.
     */
    protected void registerCourier(Courier courier) {
        createdCouriers.add(courier);
    }

    /**
     * Запоминает track заказа, чтобы отменить заказ после теста.
     */
    protected void registerOrder(int track) {
        createdOrderTracks.add(track);
    }

    @AfterEach
    @Step("Удаление тестовых данных")
    void cleanUpTestData() {
        createdOrderTracks.forEach(this::cancelOrderQuietly);
        createdCouriers.forEach(this::deleteCourierQuietly);
        createdOrderTracks.clear();
        createdCouriers.clear();
    }

    private void cancelOrderQuietly(int track) {
        try {
            Response response = orderClient.cancel(track);
            if (response.statusCode() >= 400) {
                LOG.warn("Не удалось отменить заказ track={}: {} {}", track,
                        response.statusCode(), response.asString());
            }
        } catch (Exception e) {
            LOG.warn("Ошибка при отмене заказа track={}: {}", track, e.getMessage());
        }
    }

    private void deleteCourierQuietly(Courier courier) {
        try {
            int courierId = courierClient.getCourierId(
                    new CourierCredentials(courier.getLogin(), courier.getPassword()));
            if (courierId > 0) {
                Response response = courierClient.delete(courierId);
                if (response.statusCode() >= 400) {
                    LOG.warn("Не удалось удалить курьера id={}: {} {}", courierId,
                            response.statusCode(), response.asString());
                }
            }
        } catch (Exception e) {
            LOG.warn("Ошибка при удалении курьера {}: {}", courier.getLogin(), e.getMessage());
        }
    }
}
