package com.noken29.vrpjobs.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DebugUtils {

    public static final String STATISTICS_PATH = "src/main/resources/debug/";

    @SneakyThrows
    public static void writeToCSVFile(String filePattern, List<List<String>> dataLines) {
        File csvOutputFile = new File(filePattern + ".csv");
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            dataLines.stream()
                    .map(DebugUtils::convertToCSVFormat)
                    .forEach(pw::println);
        }
    }

    public static String convertToCSVFormat(List<String> data) {
        return data.stream()
                .map(DebugUtils::escapeSpecialCharacters)
                .collect(Collectors.joining(","));
    }

    private static String escapeSpecialCharacters(String data) {
        if (data == null) {
            throw new IllegalArgumentException("Input data cannot be null");
        }
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }

}
