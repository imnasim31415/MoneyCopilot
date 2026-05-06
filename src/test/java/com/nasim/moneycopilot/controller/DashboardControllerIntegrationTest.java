package com.nasim.moneycopilot.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nasim.moneycopilot.model.dto.CreateExpenseRequest;
import com.nasim.moneycopilot.model.dto.CreateIncomeRequest;
import com.nasim.moneycopilot.model.dto.LoginRequest;
import com.nasim.moneycopilot.model.dto.RegisterRequest;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class DashboardControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  private String accessToken;
  private final int currentYear = LocalDate.now().getYear();
  private final int currentMonth = LocalDate.now().getMonthValue();

  @BeforeEach
  void setUp() throws Exception {
    String email = "dashboard_" + System.currentTimeMillis() + "@example.com";

    mockMvc.perform(post("/api/v1/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new RegisterRequest(email, "Password1"))))
        .andExpect(status().isCreated());

    MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new LoginRequest(email, "Password1"))))
        .andExpect(status().isOk())
        .andReturn();

    JsonNode body = objectMapper.readTree(loginResult.getResponse().getContentAsString());
    accessToken = body.get("accessToken").asText();

    mockMvc.perform(post("/api/v1/incomes")
            .header("Authorization", "Bearer " + accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new CreateIncomeRequest(
                new BigDecimal("80000.00"), 1, LocalDate.now(), "Salary"))))
        .andExpect(status().isCreated());

    mockMvc.perform(post("/api/v1/expenses")
            .header("Authorization", "Bearer " + accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new CreateExpenseRequest(
                new BigDecimal("20000.00"), 1, LocalDate.now(), "Groceries"))))
        .andExpect(status().isCreated());
  }

  @Test
  void should_returnMonthlySummary_with_correctTotals() throws Exception {
    mockMvc.perform(get("/api/v1/dashboard/monthly-summary")
            .header("Authorization", "Bearer " + accessToken)
            .param("year", String.valueOf(currentYear))
            .param("month", String.valueOf(currentMonth)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalIncome").value(80000.00))
        .andExpect(jsonPath("$.totalExpenses").value(20000.00))
        .andExpect(jsonPath("$.netSavings").value(60000.00));
  }

  @Test
  void should_returnCategoryBreakdown_with_percentages() throws Exception {
    mockMvc.perform(get("/api/v1/dashboard/category-breakdown")
            .header("Authorization", "Bearer " + accessToken)
            .param("year", String.valueOf(currentYear))
            .param("month", String.valueOf(currentMonth)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items[0].percentage").value(100.00));
  }

  @Test
  void should_returnIncomeBreakdown_with_percentages() throws Exception {
    mockMvc.perform(get("/api/v1/dashboard/income-breakdown")
            .header("Authorization", "Bearer " + accessToken)
            .param("year", String.valueOf(currentYear))
            .param("month", String.valueOf(currentMonth)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items[0].percentage").value(100.00));
  }

  @Test
  void should_returnTrends_with_correctMonthCount() throws Exception {
    mockMvc.perform(get("/api/v1/dashboard/trends")
            .header("Authorization", "Bearer " + accessToken)
            .param("months", "3"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.months.length()").value(3));
  }

  @Test
  void should_return401_when_requestHasNoToken() throws Exception {
    mockMvc.perform(get("/api/v1/dashboard/monthly-summary"))
        .andExpect(status().isUnauthorized());
  }
}
