package com.repair.config;

import com.repair.ui.MainMenu;
import com.repair.service.RepairService;
import com.repair.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
@ConditionalOnProperty(prefix = "dormitory-repair.console", name = "enabled", havingValue = "true")
public class ConsoleMenuRunner implements CommandLineRunner {

    private final UserService userService;
    private final RepairService repairService;

    public ConsoleMenuRunner(UserService userService, RepairService repairService) {
        this.userService = userService;
        this.repairService = repairService;
    }

    @Override
    public void run(String... args) {
        try (Scanner scanner = new Scanner(System.in)) {
            new MainMenu(scanner, userService, repairService).start();
        }
    }
}
