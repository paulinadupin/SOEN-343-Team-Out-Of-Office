package com.enchantea.ui.ord;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.enchantea.foundation.reg.UserRepository;

// Exercises /api/orders over real HTTP, to confirm CurrentUser comes from the real
// login session rather than a client-supplied field, and that logged-out requests
// are rejected instead of silently trusting whatever the caller claims to be.
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:order-controller-tests;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class OrderControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository users;

    private String restaurantId;

    @BeforeEach
    void setUp() {
        // "restaurant" is one of the seeded demo accounts, OPEN with a real menu in city.json.
        restaurantId = users.findByUsername("restaurant").orElseThrow().getUserId().toString();
    }

    @Test
    void placingAnOrderWithoutALoginSessionIsRejected() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(placeOrderJson()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void placingAnOrderWithARealSessionUsesTheLoggedInCustomerAsItsOwner() throws Exception {
        MockHttpSession customerSession = loginAndGetSession("customer", "pass1234");

        mockMvc.perform(post("/api/orders")
                        .session(customerSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(placeOrderJson()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId")
                        .value(customerSession.getAttribute("userId").toString()));
    }

    private String placeOrderJson() {
        return "{\"restaurantId\":\"%s\",\"lines\":[{\"itemId\":\"classic-milk-tea\",\"quantity\":1}]}"
                .formatted(restaurantId);
    }

    private MockHttpSession loginAndGetSession(String username, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"%s\",\"password\":\"%s\"}".formatted(username, password)))
                .andExpect(status().isOk())
                .andReturn();
        return (MockHttpSession) result.getRequest().getSession(false);
    }
}
