package com.nasim.moneycopilot.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nasim.moneycopilot.model.dto.CreateInvestmentRequest;
import com.nasim.moneycopilot.model.dto.CreateIncomeRequest;
import com.nasim.moneycopilot.model.dto.LoginRequest;
import com.nasim.moneycopilot.model.dto.RegisterRequest;
import com.nasim.moneycopilot.model.dto.UpdateTaxpayerProfileRequest;
import com.nasim.moneycopilot.model.enums.InvestmentCategory;
import com.nasim.moneycopilot.model.enums.LocationType;
import com.nasim.moneycopilot.model.enums.TaxpayerCategory;
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
class TaxControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  private String accessToken;

  @BeforeEach
  void setUp() throws Exception {
    String email = "tax_" + System.currentTimeMillis() + "@example.com";

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
  void should_returnDefaultProfile_when_noProfileSet() throws Exception {
    mockMvc.perform(get("/api/v1/tax/profile")
            .header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.category").value("GENERAL_MALE"))
        .andExpect(jsonPath("$.locationType").value("DHAKA_CHATTOGRAM"));
  }

  @Test
  void should_upsertProfile_when_requestIsValid() throws Exception {
    UpdateTaxpayerProfileRequest request = new UpdateTaxpayerProfileRequest(
        TaxpayerCategory.FEMALE,
        LocationType.OTHER_CITY_CORP,
        LocalDate.of(1990, 1, 1),
        false);

    mockMvc.perform(put("/api/v1/tax/profile")
            .header("Authorization", "Bearer " + accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.category").value("FEMALE"))
        .andExpect(jsonPath("$.locationType").value("OTHER_CITY_CORP"));

    // get should return updated value
    mockMvc.perform(get("/api/v1/tax/profile")
            .header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.category").value("FEMALE"));
  }

  @Test
  void should_createAndListInvestment() throws Exception {
    CreateInvestmentRequest request = new CreateInvestmentRequest(
        InvestmentCategory.DPS,
        new BigDecimal("50000.00"),
        "2024-2025",
        "Monthly DPS");

    MvcResult result = mockMvc.perform(post("/api/v1/investments")
            .header("Authorization", "Bearer " + accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.category").value("DPS"))
        .andExpect(jsonPath("$.amount").value(50000.00))
        .andReturn();

    mockMvc.perform(get("/api/v1/investments")
            .param("fiscalYear", "2024-2025")
            .header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].category").value("DPS"));
  }

  @Test
  void should_softDeleteInvestment() throws Exception {
    CreateInvestmentRequest request = new CreateInvestmentRequest(
        InvestmentCategory.LIFE_INSURANCE,
        new BigDecimal("10000.00"),
        "2024-2025",
        null);

    MvcResult result = mockMvc.perform(post("/api/v1/investments")
            .header("Authorization", "Bearer " + accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andReturn();

    String id = objectMapper.readTree(result.getResponse().getContentAsString())
        .get("id").asText();

    mockMvc.perform(delete("/api/v1/investments/" + id)
            .header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isNoContent());

    // Should no longer appear in list
    mockMvc.perform(get("/api/v1/investments")
            .param("fiscalYear", "2024-2025")
            .header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[?(@.id == '" + id + "')]").doesNotExist());
  }

  @Test
  void should_calculateTax_returningValidStructure() throws Exception {
    // Add income so the calculation has something to work with
    mockMvc.perform(post("/api/v1/incomes")
            .header("Authorization", "Bearer " + accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new CreateIncomeRequest(
                new BigDecimal("800000.00"), 1, LocalDate.of(2024, 10, 1), "Salary"))))
        .andExpect(status().isCreated());

    mockMvc.perform(get("/api/v1/tax/calculate")
            .param("fiscalYear", "2024-2025")
            .header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.grossIncome").value(800000.00))
        .andExpect(jsonPath("$.finalTax").isNumber())
        .andExpect(jsonPath("$.slabBreakdowns").isArray());
  }

  @Test
  void should_return401_when_unauthenticated() throws Exception {
    mockMvc.perform(get("/api/v1/tax/calculate").param("fiscalYear", "2024-2025"))
        .andExpect(status().isUnauthorized());
  }
}
