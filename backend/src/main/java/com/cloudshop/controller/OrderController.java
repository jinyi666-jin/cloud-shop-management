package com.cloudshop.controller;

import com.cloudshop.dto.ApiResponse;
import com.cloudshop.entity.Order;
import com.cloudshop.entity.OrderItem;
import com.cloudshop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public ApiResponse<List<Order>> list() {
        return ApiResponse.success(orderService.findAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<Map<String, Object>> detail(@PathVariable Long id) {
        Order order = orderService.findById(id);
        List<OrderItem> items = orderService.getOrderItems(id);
        Map<String, Object> result = new HashMap<>();
        result.put("order", order);
        result.put("items", items);
        return ApiResponse.success(result);
    }

    @PostMapping
    public ApiResponse<Order> create(@RequestBody Map<String, Object> params) {
        Order order = new Order();
        order.setUserId(Long.valueOf(params.get("userId").toString()));
        order.setTotalAmount(new java.math.BigDecimal(params.get("totalAmount").toString()));
        order.setRemark((String) params.getOrDefault("remark", ""));

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> itemMaps = (List<Map<String, Object>>) params.get("items");
        java.util.List<OrderItem> items = new java.util.ArrayList<>();
        for (Map<String, Object> itemMap : itemMaps) {
            OrderItem item = new OrderItem();
            item.setProductId(Long.valueOf(itemMap.get("productId").toString()));
            item.setQuantity(Integer.valueOf(itemMap.get("quantity").toString()));
            item.setPrice(new java.math.BigDecimal(itemMap.get("price").toString()));
            items.add(item);
        }
        return ApiResponse.success(orderService.create(order, items));
    }

    @PutMapping("/{id}/status")
    public ApiResponse<Order> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        return ApiResponse.success(orderService.updateStatus(id, status));
    }
}
