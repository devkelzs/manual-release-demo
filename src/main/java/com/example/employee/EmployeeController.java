package com.example.employee;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    @GetMapping
    public List<Map<String, Object>> getAllEmployees() {
        return List.of(
                Map.of("id", 1, "name", "Alice"),
                Map.of("id", 2, "name", "Bob")
        );
    }

    @GetMapping("/{id}")
    public Map<String, Object> getEmployee(@PathVariable int id) {
        return Map.of(
                "id", id,
                "name", "Employee-" + id
        );
    }

    @PostMapping
    public Map<String, String> createEmployee() {
        return Map.of("status", "Employee created");
    }
}