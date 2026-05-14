package com.cloudshop.service;

import com.cloudshop.entity.Order;
import com.cloudshop.entity.OrderItem;
import com.cloudshop.repository.OrderItemRepository;
import com.cloudshop.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public List<Order> findByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
    }

    @Transactional
    public Order create(Order order, List<OrderItem> items) {
        String orderNo = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        order.setOrderNo(orderNo);
        order.setStatus("PENDING");
        Order saved = orderRepository.save(order);

        for (OrderItem item : items) {
            item.setOrderId(saved.getId());
            orderItemRepository.save(item);
        }
        return saved;
    }

    public Order updateStatus(Long id, String status) {
        Order order = findById(id);
        order.setStatus(status);
        return orderRepository.save(order);
    }

    public List<OrderItem> getOrderItems(Long orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    public long count() {
        return orderRepository.count();
    }
}
