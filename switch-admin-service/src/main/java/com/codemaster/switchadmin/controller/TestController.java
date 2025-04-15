package com.codemaster.switchadmin.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class TestController {

    @GetMapping("/hello")
    @PreAuthorize("@permissionChecker.hasAllPermissions(authentication, 'USER_CREATE')")
    public String sayHello()
    {
        return "Hello";
    }
}
