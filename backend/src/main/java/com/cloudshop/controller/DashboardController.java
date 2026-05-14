package com.cloudshop.controller;

import com.cloudshop.dto.ApiResponse;
import com.cloudshop.entity.User;
import com.cloudshop.repository.UserRepository;
import com.cloudshop.service.OrderService;
import com.cloudshop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DashboardController {

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/dashboard")
    public ApiResponse<Map<String, Object>> dashboard() {
        Map<String, Object> data = new HashMap<>();
        data.put("productCount", productService.count());
        data.put("orderCount", orderService.count());
        data.put("userCount", userRepository.count());
        data.put("todayOrders", 0);
        return ApiResponse.success(data);
    }

    @GetMapping("/users")
    public ApiResponse<List<User>> userList() {
        return ApiResponse.success(userRepository.findAll());
    }

    @GetMapping("/users/{id}")
    public ApiResponse<User> userDetail(@PathVariable Long id) {
        return ApiResponse.success(userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在")));
    }

    @PutMapping("/users/{id}/status")
    public ApiResponse<User> updateUserStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        user.setStatus(body.get("status"));
        return ApiResponse.success(userRepository.save(user));
    }
}
