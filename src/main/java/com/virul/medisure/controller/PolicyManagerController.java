package com.virul.medisure.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/policy-manager")
public class PolicyManagerController {
    
    @GetMapping("/policies")
    public String policiesList() {
        return "policy-manager/policies-list";
    }
    
    @GetMapping("/policies/create")
    public String createPolicy() {
        return "policy-manager/policy-create";
    }
    
    @GetMapping("/policies/edit")
    public String editPolicy() {
        return "policy-manager/policy-edit";
    }
    
    @GetMapping("/policy-holders")
    public String policyHolders() {
        return "policy-manager/policy-holders";
    }
}