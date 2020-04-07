package com.michael.gateway;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * @author Michael Chu
 * @since 2020-03-27 10:26
 */
@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(classes= WebfluxGatewayAuthorizeApplication.class)
public class BaseTest {
    
}
