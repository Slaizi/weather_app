package ru.bogachev.weatherApp.support.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NameHelper {

    public static String getCorrectNameOfLocation(
            final @NotNull String nameOfLocation) {
        String trimmed = nameOfLocation.trim();
        boolean containsDash = trimmed.contains("-");

        String[] words = trimmed.split("[\\s-]+");

        return Arrays.stream(words)
                .map(word ->
                        word.substring(0, 1).toUpperCase()
                        + word.substring(1).toLowerCase()
                ).collect(Collectors.joining(containsDash ? "-" : " "));
    }
}
