package com.pojiang.examplespringbootconsumer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class ExampleServiceImplTest {
    @Resource
    ExampleServiceImpl exampleService;

    @Test
    void testService() {
        exampleService.test();
    }
}
