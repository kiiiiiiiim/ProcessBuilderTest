package com.example.processbuildertest.cmd;

import java.io.IOException;

public class CommandExecutor {
    public static Process executeCommand(String cmd) throws IOException {
        String[] command;

        String osName = System.getProperty("os.name");
        boolean isWindows = osName
            .toLowerCase()
            .startsWith("windows");
        if (isWindows) {
            command = new String[]{"cmd", "/c", cmd};
        } else {
            command = new String[]{"sh", "-c", cmd};
        }

        ProcessBuilder builder = new ProcessBuilder(command);
        Process process = builder.start();
        return process;
    }
}
