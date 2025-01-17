package com.lsecotaro.login_challenge.health;

import com.lsecotaro.login_challenge.auth.controller.AuthController;
import com.lsecotaro.login_challenge.auth.controller.AuthMapper;
import com.lsecotaro.login_challenge.auth.service.AuthService;
import com.lsecotaro.login_challenge.config.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(HealthController.class)
@ContextConfiguration(classes = TestSecurityConfig.class)
public class HealthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    private static final String PING_URL = "/ping";


    @Test
    public void ping() throws Exception {
        mockMvc.perform(get(PING_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pong").value("pong"));

    }
}