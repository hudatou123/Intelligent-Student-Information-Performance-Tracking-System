package com.gradems.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gradems.config.SecurityConfig;
import com.gradems.dto.request.LoginRequest;
import com.gradems.dto.request.RegisterRequest;
import com.gradems.dto.response.AuthResponse;
import com.gradems.entity.Role;
import com.gradems.exception.GlobalExceptionHandler;
import com.gradems.security.JwtAuthenticationFilter;
import com.gradems.security.JwtTokenProvider;
import com.gradems.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class, JwtAuthenticationFilter.class})
@DisplayName("AuthController Integration Tests")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private UserDetailsService userDetailsService;

    private AuthResponse sampleAuthResponse;

    @BeforeEach
    void setUp() {
        sampleAuthResponse = new AuthResponse(
                "eyJhbGciOiJIUzI1NiJ9.sampleAccessToken",
                "eyJhbGciOiJIUzI1NiJ9.sampleRefreshToken",
                "Bearer",
                1L,
                "admin",
                "System Administrator",
                "ADMIN"
        );
    }

    @Test
    @DisplayName("POST /api/auth/login - valid credentials returns 200 with token")
    void login_withValidCredentials_returns200AndToken() throws Exception {
        LoginRequest loginRequest = new LoginRequest("admin", "Admin@123");

        when(authService.login(any(LoginRequest.class))).thenReturn(sampleAuthResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.username").value("admin"))
                .andExpect(jsonPath("$.data.role").value("ADMIN"));
    }

    @Test
    @DisplayName("POST /api/auth/login - invalid credentials returns 401")
    void login_withInvalidCredentials_returns401() throws Exception {
        LoginRequest loginRequest = new LoginRequest("admin", "wrongpassword");

        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new BadCredentialsException("Invalid username or password"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid username or password"));
    }

    @Test
    @DisplayName("POST /api/auth/login - missing fields returns 400")
    void login_withMissingFields_returns400() throws Exception {
        String invalidBody = "{\"username\": \"\", \"password\": \"\"}";

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("POST /api/auth/register - without ADMIN role returns 403")
    @WithMockUser(roles = "STUDENT")
    void register_withoutAdminRole_returns403() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(
                "newuser",
                "Password@123",
                "New User",
                "newuser@test.com",
                Role.STUDENT
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /api/auth/register - with ADMIN role returns 200")
    @WithMockUser(roles = "ADMIN")
    void register_withAdminRole_returns200() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(
                "newstudent",
                "Password@123",
                "New Student",
                "newstudent@test.com",
                Role.STUDENT
        );

        AuthResponse registerResponse = new AuthResponse(
                "eyJhbGciOiJIUzI1NiJ9.newToken",
                "eyJhbGciOiJIUzI1NiJ9.newRefresh",
                "Bearer",
                10L,
                "newstudent",
                "New Student",
                "STUDENT"
        );

        when(authService.register(any(RegisterRequest.class))).thenReturn(registerResponse);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value("newstudent"))
                .andExpect(jsonPath("$.data.role").value("STUDENT"));
    }
}
