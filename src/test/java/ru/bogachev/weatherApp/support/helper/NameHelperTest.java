package ru.bogachev.weatherApp.support.helper;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;


class NameHelperTest {

    @ParameterizedTest
    @CsvSource({
            "mOsCow, Moscow",
            "rio de janEIro, Rio De Janeiro",
            "saiNt-peterSbuRg, Saint-Petersburg"
    })
    void getCorrectNameOfLocationTest(String input, String expected) {
        String result = NameHelper.getCorrectNameOfLocation(input);
        assertEquals(expected, result);
    }
}
