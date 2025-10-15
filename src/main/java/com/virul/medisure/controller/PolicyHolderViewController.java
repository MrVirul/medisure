package com.virul.medisure.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/policyholder")
public class PolicyHolderViewController {
    
    @GetMapping("/available-policies")
    public String availablePolicies() {
        return "policyholder/available-policies";
    }
    
    @GetMapping("/purchase-policy")
    public String purchasePolicy() {
        return "policyholder/purchase-policy";
    }
}