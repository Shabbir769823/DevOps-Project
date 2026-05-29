package com.devops.employee.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class AdminController {

    @GetMapping("/admin/logs")
    public String logs() {
        return "admin/logs";
    }

    @GetMapping("/admin/pipeline")
    public String pipeline() {
        return "admin/pipeline";
    }

    @GetMapping("/admin/metrics")
    public String metrics() {
        return "admin/metrics";
    }

    @GetMapping("/admin/kubernetes")
    public String kubernetes() {
        return "admin/kubernetes";
    }

    @GetMapping("/admin/api/logs")
    @ResponseBody
    public ResponseEntity<List<String>> getLogs() {
        File file = new File("logs/app.log");
        List<String> lines = new ArrayList<>();
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
                // Keep only the latest 150 lines to prevent overwhelming the browser client
                if (lines.size() > 150) {
                    lines = lines.subList(lines.size() - 150, lines.size());
                }
            } catch (IOException e) {
                lines.add("Error reading log file: " + e.getMessage());
            }
        } else {
            lines.add("Log file 'logs/app.log' is not available yet. Please generate some activity or restart the portal.");
        }
        return ResponseEntity.ok(lines);
    }
}
