package com.example.processbuildertest.cmd;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class CommandUtil {
    public static String getStringFromFile(String path) throws IOException {
        Path filePath = Paths.get(path);
        if (!Files.exists(filePath)) {
            return null;
        }
        String read = Files.readString(filePath, StandardCharsets.UTF_8);
        return read == null || read.isBlank()
               ? null
               : read
                   .stripTrailing()
                   .replaceAll("\u0000", " ");
    }

    public static String findValueFromFile(String path, String key, String spliter) throws IOException {
        List<String> lines = getStringLinesFromFile(path);
        if (lines == null) {
            return null;
        }
        for (String line : lines) {
            if (line
                .strip()
                .startsWith(key)) {
                return line.split(spliter)[1].strip();
            }
        }
        return null;
    }

    public static List<String> getStringLinesFromFile(String path) throws IOException {
        Path filePath = Paths.get(path);
        if (!Files.exists(filePath)) {
            return null;
        }
        List<String> read = Files.readAllLines(filePath, StandardCharsets.UTF_8);
        return read == null || read.size() == 0 ? null : read;
    }

    public static CommandExecuteResponse readProcessPrintData(Process process) throws IOException, InterruptedException {
        try {
            InputStream resultInputStream = process.getInputStream();
            ByteArrayOutputStream resultStream = new ByteArrayOutputStream();
            CommandExecuteResponse response = new CommandExecuteResponse();
            byte[] buffer = new byte[1024];

            while (resultInputStream.read(buffer) != -1) {
                resultStream.write(buffer);
            }

            process.waitFor();
            ByteArrayOutputStream errorOutputStream = new ByteArrayOutputStream();
            String resultString = resultStream.toString(StandardCharsets.UTF_8);
            resultStream.close();
            resultInputStream.close();
            response.setSuccess(true);
            int exitValue = process.exitValue();
            if (exitValue == 0) {
                process.destroy();
                String result = resultString.isBlank() ? null : resultString.trim();
                if (result != null) {
                    response.setData(result);
                }

                return response;
            } else {
                InputStream errorInputStream = process.getErrorStream();

                while (errorInputStream.read(buffer) != -1) {
                    errorOutputStream.write(buffer);
                }

                String errorString = errorOutputStream.toString(StandardCharsets.UTF_8);
                response.setData(errorString);
                errorOutputStream.close();
                errorInputStream.close();
                response.setSuccess(false);
                response.setErrorCode(String.valueOf(exitValue));
                return response;
            }
        } catch (Throwable var11) {
            throw var11;
        }
    }
}