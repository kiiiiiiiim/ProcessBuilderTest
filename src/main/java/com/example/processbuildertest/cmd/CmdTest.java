package com.example.processbuildertest.cmd;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Slf4j
public class CmdTest {
    @PostConstruct
    public void test() {
        List<Thread> tL = new CopyOnWriteArrayList<>();
        AtomicLong count = new AtomicLong(0);
        for (int i = 0; i < 20; i++) {
            Thread t = new Thread(() -> {
                try {
                    while (true) {
                        Process process = CommandExecutor.executeCommand("ls -l /root/8t");
                        CommandExecuteResponse commandExecuteResponse = CommandUtil.readProcessPrintData(process);
                        long l = count.incrementAndGet();
                        if (l % 1000 == 0) {
                            log.debug("[{}] exec : {}", l, commandExecuteResponse);
                        }
                    }
                } catch (Exception e) {
                    log.error("{} : {}",
                              e
                                  .getClass()
                                  .getSimpleName(),
                              e.getMessage(),
                              e);
                }
            });
            tL.add(t);
        }
        tL
            .parallelStream()
            .forEach(t -> {
                t.start();
                try {
                    t.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });

    }
}
