package com.cloudshop.controller;

import com.cloudshop.dto.ApiResponse;
import com.cloudshop.entity.Category;
import com.cloudshop.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public ApiResponse<List<Category>> list() {
        return ApiResponse.success(categoryService.findAll());
    }

    @GetMapping("/active")
    public ApiResponse<List<Category>> listActive() {
        return ApiResponse.success(categoryService.findActive());
    }

    @GetMapping("/{id}")
    public ApiResponse<Category> detail(@PathVariable Long id) {
        return ApiResponse.success(categoryService.findById(id));
    }

    @PostMapping
    public ApiResponse<Category> create(@RequestBody Category category) {
        return ApiResponse.success(categoryService.save(category));
    }

    @PutMapping("/{id}")
    public ApiResponse<Category> update(@PathVariable Long id, @RequestBody Category category) {
        return ApiResponse.success(categoryService.update(id, category));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<?> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ApiResponse.success("删除成功", null);
    }
}
