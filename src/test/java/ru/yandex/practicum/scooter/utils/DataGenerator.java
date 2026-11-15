package ru.yandex.practicum.scooter.utils;

import ru.yandex.practicum.scooter.model.Courier;
import ru.yandex.practicum.scooter.model.Order;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Генератор уникальных тестовых данных.
 */
public final class DataGenerator {

    private static final AtomicLong COUNTER = new AtomicLong(System.currentTimeMillis());

    private DataGenerator() {
    }

    public static String uniqueLogin() {
        return "scooter_autotest_" + COUNTER.incrementAndGet();
    }

    public static String uniquePassword() {
        return "pass_" + COUNTER.incrementAndGet();
    }

    public static Courier randomCourier() {
        String suffix = String.valueOf(COUNTER.incrementAndGet());
        return new Courier("scooter_autotest_" + suffix, "pass_" + suffix, "Тест" + suffix);
    }

    public static Order randomOrder() {
        List<String> colors = List.of("BLACK", "GREY");
        List<String> randomColor = List.of(colors.get(ThreadLocalRandom.current().nextInt(colors.size())));
        return randomOrder(randomColor);
    }

    public static Order randomOrder(List<String> color) {
        long suffix = COUNTER.incrementAndGet();
        return new Order(
                "Арнольд",
                "Шварценеггер" + suffix,
                "Лос-Анджелес, Брентвуд, Бульвар Сан-Висенте, " + ThreadLocalRandom.current().nextInt(1, 100),
                "4",
                "+7999" + String.format("%07d", ThreadLocalRandom.current().nextInt(10_000_000)),
                1 + ThreadLocalRandom.current().nextInt(7),
                LocalDate.now().plusDays(1).toString(),
                "Тестовый заказ " + suffix,
                color);
    }
}
