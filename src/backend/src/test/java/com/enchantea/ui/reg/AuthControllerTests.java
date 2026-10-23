package com.enchantea.ui.reg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

// Exercises /api/auth/* over real HTTP, unlike AccountFacadeTests which calls the
// service directly. Covers status codes and session cookie behavior.
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:auth-controller-tests;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class AuthControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void registeringReturns201AndTheNewAccount() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"http-casey","password":"password123","accountType":"OPERATOR"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountType").value("OPERATOR"));
    }

    @Test
    void loggingInWithTheWrongPasswordReturns401() throws Exception {
        register("http-wrongpass");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"http-wrongpass","password":"not-the-right-password"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loggingInWithTheRightPasswordReturns200AndStartsASession() throws Exception {
        register("http-goodlogin");

        MockHttpSession session = loginAndGetSession("http-goodlogin");

        assertNotNull(session);
        assertNotNull(session.getAttribute("userId"));
        assertEquals("OPERATOR", session.getAttribute("accountType"));
    }

    @Test
    void loggingOutWithoutBeingLoggedInReturns401() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loggingOutWithARealSessionSucceedsAndEndsIt() throws Exception {
        register("http-logout");
        MockHttpSession session = loginAndGetSession("http-logout");

        mockMvc.perform(post("/api/auth/logout").session(session))
                .andExpect(status().isNoContent());

        // the session is invalidated, so logging out again with it fails
        mockMvc.perform(post("/api/auth/logout").session(session))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void registeringTwiceWithTheSameUsernameReturns409() throws Exception {
        register("http-dupe");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"http-dupe","password":"password123","accountType":"OPERATOR"}
                                """))
                .andExpect(status().isConflict());
    }

    private void register(String username) throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"%s\",\"password\":\"password123\",\"accountType\":\"OPERATOR\"}"
                        .formatted(username)));
    }

    private MockHttpSession loginAndGetSession(String username) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"%s\",\"password\":\"password123\"}".formatted(username)))
                .andExpect(status().isOk())
                .andReturn();
        return (MockHttpSession) result.getRequest().getSession(false);
    }
}
