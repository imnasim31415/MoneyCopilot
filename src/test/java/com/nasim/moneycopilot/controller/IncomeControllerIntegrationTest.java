package com.nasim.moneycopilot.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nasim.moneycopilot.model.dto.CreateIncomeRequest;
import com.nasim.moneycopilot.model.dto.LoginRequest;
import com.nasim.moneycopilot.model.dto.RegisterRequest;
import com.nasim.moneycopilot.model.dto.UpdateIncomeRequest;
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
class IncomeControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  private String accessToken;

  @BeforeEach
  void setUp() throws Exception {
    String email = "income_" + System.currentTimeMillis() + "@example.com";

    mockMvc.perform(post("/api/v1/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new RegisterRequest(email, "Password1"))))
        .andExpect(status().isCreated());

    MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new LoginRequest(email, "Password1"))))
        .andExpect(status().isOk())
        .andReturn();

    JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
    accessToken = body.get("accessToken").asText();
  }

  @Test
  void should_createAndRetrieveIncome_when_requestIsValid() throws Exception {
    CreateIncomeRequest request = new CreateIncomeRequest(
        new BigDecimal("50000.00"), 1, LocalDate.now(), "Monthly salary");

    MvcResult createResult = mockMvc.perform(post("/api/v1/incomes")
            .header("Authorization", "Bearer " + accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.amount").value(50000.00))
        .andExpect(jsonPath("$.sourceName").value("SALARY"))
        .andReturn();

    String id = objectMapper.readTree(createResult.getResponse().getContentAsString())
        .get("id").asText();

    mockMvc.perform(get("/api/v1/incomes/" + id)
            .header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id));
  }

  @Test
  void should_updateIncome_when_ownerRequests() throws Exception {
    CreateIncomeRequest create = new CreateIncomeRequest(
        new BigDecimal("30000.00"), 2, LocalDate.now(), "Freelance");

    MvcResult createResult = mockMvc.perform(post("/api/v1/incomes")
            .header("Authorization", "Bearer " + accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(create)))
        .andExpect(status().isCreated())
        .andReturn();

    String id = objectMapper.readTree(createResult.getResponse().getContentAsString())
        .get("id").asText();

    UpdateIncomeRequest update = new UpdateIncomeRequest(
        new BigDecimal("35000.00"), 2, LocalDate.now(), "Freelance updated");

    mockMvc.perform(put("/api/v1/incomes/" + id)
            .header("Authorization", "Bearer " + accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(update)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.amount").value(35000.00));
  }

  @Test
  void should_softDeleteIncome_when_ownerRequests() throws Exception {
    CreateIncomeRequest create = new CreateIncomeRequest(
        new BigDecimal("10000.00"), 1, LocalDate.now(), null);

    MvcResult createResult = mockMvc.perform(post("/api/v1/incomes")
            .header("Authorization", "Bearer " + accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(create)))
        .andExpect(status().isCreated())
        .andReturn();

    String id = objectMapper.readTree(createResult.getResponse().getContentAsString())
        .get("id").asText();

    mockMvc.perform(delete("/api/v1/incomes/" + id)
            .header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isNoContent());

    mockMvc.perform(get("/api/v1/incomes/" + id)
            .header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isNotFound());
  }

  @Test
  void should_return401_when_requestHasNoToken() throws Exception {
    mockMvc.perform(get("/api/v1/incomes"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void should_listIncomeSources() throws Exception {
    mockMvc.perform(get("/api/v1/income-sources")
            .header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").exists());
  }
}
