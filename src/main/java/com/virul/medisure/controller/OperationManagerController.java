package com.virul.medisure.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/operation-manager")
public class OperationManagerController {
    
    @GetMapping("/employees")
    public String employeesList() {
        return "operation-manager/employees-list";
    }
    
    @GetMapping("/employees/create")
    public String createEmployee() {
        return "operation-manager/employee-create";
    }
    
    @GetMapping("/employees/edit")
    public String editEmployee() {
        return "operation-manager/employee-edit";
    }
}