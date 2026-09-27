package com.github.ljl1leina.sololog.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.jdbc.core.JdbcTemplate;

@RestController
@RequestMapping("/text")
public class TextRedisMYSQLController {
    // Spring Boot 自动配置好的 JDBC 工具，可以直接执行 SQL
    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Spring Data Redis 自动配置好的 Redis 工具
    @Autowired
    private StringRedisTemplate redisTemplate;

    @GetMapping("/db")
    public String testDb() {
        try {
            // 执行一条最简单的 SQL，看是否能连通
            Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            return "✅ MySQL 连接成功！查询结果：" + result;
        } catch (Exception e) {
            return "❌ MySQL 连接失败，错误信息：" + e.getMessage();
        }
    }

    @GetMapping("/redis")
    public String testRedis() {
        try {
            // 往 Redis 存一个值，再读出来
            redisTemplate.opsForValue().set("test:key", "hello redis");
            String value = redisTemplate.opsForValue().get("test:key");
            return "✅ Redis 连接成功！取出的值：" + value;
        } catch (Exception e) {
            return "❌ Redis 连接失败，错误信息：" + e.getMessage();
        }
    }
}
