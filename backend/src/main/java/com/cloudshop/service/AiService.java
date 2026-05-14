package com.cloudshop.service;

import com.cloudshop.entity.Product;
import com.cloudshop.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiService {

    private static final Logger logger = LoggerFactory.getLogger(AiService.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${deepseek.api.key:}")
    private String apiKey;

    @Value("${deepseek.api.url:https://api.deepseek.com/v1/chat/completions}")
    private String apiUrl;

    public String chat(String message, Long userId) {
        logger.info("AI chat request: message={}, userId={}", message, userId);
        String systemPrompt = "你是一个友好的智能助手，可以回答各种问题。当用户询问商品、订单相关问题时，你是电商助手；对于其他问题，你也可以提供帮助和解答。";
        
        if (message.contains("商品") || message.contains("产品") || 
            message.contains("买") || message.contains("推荐")) {
            return recommendProducts(message, userId);
        }
        
        if (message.contains("订单") || message.contains("购买") || 
            message.contains("发货") || message.contains("物流")) {
            return analyzeOrder(message, userId);
        }
        
        return callDeepSeek(systemPrompt, message);
    }

    public String recommendProducts(String query, Long userId) {
        logger.info("推荐商品: query={}, userId={}", query, userId);
        List<Product> products = productRepository.findAll();
        
        if (products.isEmpty()) {
            return "暂无商品推荐，快来看看我们的商品吧！";
        }
        
        StringBuilder productList = new StringBuilder();
        productList.append("为您推荐以下商品：\n");
        
        int count = 0;
        for (Product product : products) {
            if (count >= 5) break;
            productList.append(String.format("- %s（¥%.2f）：%s\n", 
                product.getName(), product.getPrice(), product.getDescription()));
            count++;
        }
        
        String prompt = String.format("作为电商助手，请根据用户查询和商品列表给出推荐建议。\n" +
            "用户查询：%s\n商品列表：\n%s\n请用友好自然的语言回复。", query, productList.toString());
        
        return callDeepSeek("你是专业的电商推荐助手", prompt);
    }

    public String analyzeOrder(String query, Long userId) {
        logger.info("分析订单: query={}, userId={}", query, userId);
        String prompt = String.format("用户询问订单相关问题：%s\n" +
            "用户ID：%d\n请给出友好的回复，提示用户可以在订单页面查看详细信息。", query, userId != null ? userId : 0);
        
        return callDeepSeek("你是订单服务助手", prompt);
    }

    private String callDeepSeek(String systemPrompt, String userMessage) {
        logger.info("调用DeepSeek API: systemPrompt={}, userMessage={}", systemPrompt, userMessage);
        
        if (apiKey == null || apiKey.isEmpty()) {
            logger.warn("API密钥为空");
            return getDefaultReply(userMessage);
        }
        
        try {
            Map<String, Object> request = new HashMap<>();
            request.put("model", "deepseek-chat");
            
            var messages = new java.util.ArrayList<Map<String, String>>();
            messages.add(Map.of("role", "system", "content", systemPrompt));
            messages.add(Map.of("role", "user", "content", userMessage));
            request.put("messages", messages);
            
            request.put("temperature", 0.7);
            request.put("max_tokens", 1024);

            var headers = new HashMap<String, String>();
            headers.put("Authorization", "Bearer " + apiKey);
            headers.put("Content-Type", "application/json");

            org.springframework.http.HttpHeaders httpHeaders = new org.springframework.http.HttpHeaders();
            httpHeaders.setAll(headers);
            
            org.springframework.http.HttpEntity<Map<String, Object>> entity = 
                new org.springframework.http.HttpEntity<>(request, httpHeaders);

            logger.info("发送请求到: {}", apiUrl);
            
            var response = restTemplate.postForObject(apiUrl, entity, Map.class);
            
            logger.info("API响应类型: {}, 响应内容: {}", 
                response != null ? response.getClass().getName() : "null", 
                response);
            
            if (response == null) {
                logger.error("API响应为空");
                return "抱歉，服务暂时不可用，请稍后重试。错误：响应为空";
            }
            
            if (!response.containsKey("choices")) {
                logger.error("响应不包含 choices 字段，响应内容: {}", response);
                return "抱歉，服务暂时不可用，请稍后重试。错误：响应格式不正确";
            }
            
            var choices = response.get("choices");
            if (!(choices instanceof List)) {
                logger.error("choices 不是列表类型，类型: {}, 值: {}", 
                    choices != null ? choices.getClass().getName() : "null", choices);
                return "抱歉，服务暂时不可用，请稍后重试。错误：choices格式不正确";
            }
            
            List<?> choicesList = (List<?>) choices;
            if (choicesList.isEmpty()) {
                logger.error("choices 列表为空");
                return "抱歉，服务暂时不可用，请稍后重试。错误：choices为空";
            }
            
            var choice = choicesList.get(0);
            if (!(choice instanceof Map)) {
                logger.error("choice 不是 Map 类型，类型: {}", 
                    choice != null ? choice.getClass().getName() : "null");
                return "抱歉，服务暂时不可用，请稍后重试。错误：choice格式不正确";
            }
            
            Map<?, ?> choiceMap = (Map<?, ?>) choice;
            if (!choiceMap.containsKey("message")) {
                logger.error("choice 不包含 message 字段，choice内容: {}", choice);
                return "抱歉，服务暂时不可用，请稍后重试。错误：缺少message字段";
            }
            
            var messageObj = choiceMap.get("message");
            if (!(messageObj instanceof Map)) {
                logger.error("message 不是 Map 类型，类型: {}", 
                    messageObj != null ? messageObj.getClass().getName() : "null");
                return "抱歉，服务暂时不可用，请稍后重试。错误：message格式不正确";
            }
            
            Map<?, ?> messageMap = (Map<?, ?>) messageObj;
            if (!messageMap.containsKey("content")) {
                logger.error("message 不包含 content 字段，message内容: {}", messageMap);
                return "抱歉，服务暂时不可用，请稍后重试。错误：缺少content字段";
            }
            
            Object contentObj = messageMap.get("content");
            if (!(contentObj instanceof String)) {
                logger.error("content 不是 String 类型，类型: {}, 值: {}", 
                    contentObj != null ? contentObj.getClass().getName() : "null", contentObj);
                return "抱歉，服务暂时不可用，请稍后重试。错误：content格式不正确";
            }
            
            String content = (String) contentObj;
            logger.info("AI回复: {}", content);
            return content;
            
        } catch (HttpClientErrorException e) {
            logger.error("HTTP客户端错误: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            String errorMsg = e.getResponseBodyAsString();
            if (errorMsg != null && errorMsg.contains("Unauthorized")) {
                return "抱歉，API密钥无效，请检查配置。错误：认证失败";
            }
            return "抱歉，服务暂时不可用，请稍后重试。错误：" + e.getStatusCode();
        } catch (HttpServerErrorException e) {
            logger.error("HTTP服务端错误: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            return "抱歉，服务暂时不可用，请稍后重试。错误：服务器内部错误";
        } catch (ResourceAccessException e) {
            logger.error("网络访问错误: {}", e.getMessage());
            return "抱歉，网络连接失败，请检查网络连接后重试。";
        } catch (Exception e) {
            logger.error("调用DeepSeek API失败: {}", e.getMessage(), e);
            return "抱歉，服务暂时不可用，请稍后重试。错误: " + e.getMessage();
        }
    }

    private String getDefaultReply(String userMessage) {
        if (userMessage.contains("商品") || userMessage.contains("推荐")) {
            return "🎁 您可以在商品管理页面浏览和搜索商品，我们提供丰富的商品选择！";
        }
        if (userMessage.contains("订单")) {
            return "📦 您可以在订单管理页面查看您的订单状态和物流信息。";
        }
        if (userMessage.contains("帮助") || userMessage.contains("怎么")) {
            return "💡 有什么需要帮助的吗？您可以查看商品、管理订单或浏览用户信息。";
        }
        return "您好！我是您的智能购物助手。您可以询问关于商品、订单等方面的问题。";
    }
}