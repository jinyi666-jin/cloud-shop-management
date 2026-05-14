package com.cloudshop.config;

import com.cloudshop.entity.Category;
import com.cloudshop.entity.Product;
import com.cloudshop.entity.User;
import com.cloudshop.repository.CategoryRepository;
import com.cloudshop.repository.ProductRepository;
import com.cloudshop.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;

@Component
public class DataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        try {
            Thread.sleep(1000);
            
            if (userRepository.count() == 0) {
                log.info("Initializing user data...");
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setNickname("系统管理员");
                admin.setEmail("admin@cloudshop.com");
                admin.setRole("ADMIN");
                admin.setStatus(1);
                userRepository.save(admin);

                User user = new User();
                user.setUsername("user");
                user.setPassword(passwordEncoder.encode("user123"));
                user.setNickname("普通用户");
                user.setEmail("user@cloudshop.com");
                user.setRole("USER");
                user.setStatus(1);
                userRepository.save(user);
            }

            if (categoryRepository.count() == 0) {
                log.info("Initializing category data...");
                String[][] cats = {
                    {"电子产品", "手机、电脑、数码配件等"},
                    {"服装鞋帽", "男装、女装、鞋类、帽子等"},
                    {"食品饮料", "零食、饮料、生鲜水果等"},
                    {"家居用品", "家具、家纺、家居装饰等"},
                    {"图书音像", "图书、电子书、音乐、影视等"}
                };
                for (String[] c : cats) {
                    Category cat = new Category();
                    cat.setName(c[0]);
                    cat.setDescription(c[1]);
                    cat.setStatus(1);
                    categoryRepository.save(cat);
                }
            }

            if (productRepository.count() == 0) {
                log.info("Initializing product data...");
                Product p1 = new Product();
                p1.setName("iPhone 15 Pro Max");
                p1.setDescription("Apple iPhone 15 Pro Max 256GB 原色钛金属");
                p1.setPrice(new BigDecimal("9999.00"));
                p1.setStock(100);
                p1.setCategoryId(1L);
                p1.setStatus(1);
                productRepository.save(p1);

                Product p2 = new Product();
                p2.setName("MacBook Pro 16英寸");
                p2.setDescription("Apple MacBook Pro 16英寸 M3 Pro芯片 18GB内存 512GB存储");
                p2.setPrice(new BigDecimal("19999.00"));
                p2.setStock(50);
                p2.setCategoryId(1L);
                p2.setStatus(1);
                productRepository.save(p2);

                Product p3 = new Product();
                p3.setName("男士休闲夹克");
                p3.setDescription("春秋季新款男士休闲夹克 修身版型 多色可选");
                p3.setPrice(new BigDecimal("399.00"));
                p3.setStock(200);
                p3.setCategoryId(2L);
                p3.setStatus(1);
                productRepository.save(p3);

                Product p4 = new Product();
                p4.setName("有机坚果礼盒");
                p4.setDescription("精选有机坚果混合装 每日坚果 1kg礼盒装");
                p4.setPrice(new BigDecimal("128.00"));
                p4.setStock(500);
                p4.setCategoryId(3L);
                p4.setStatus(1);
                productRepository.save(p4);

                Product p5 = new Product();
                p5.setName("北欧简约台灯");
                p5.setDescription("Led护眼台灯 北欧简约风格 三档调光 触控开关");
                p5.setPrice(new BigDecimal("199.00"));
                p5.setStock(300);
                p5.setCategoryId(4L);
                p5.setStatus(1);
                productRepository.save(p5);
            }
        } catch (Exception e) {
            log.warn("Data initialization skipped due to database connection issue: {}", e.getMessage());
        }
    }
}