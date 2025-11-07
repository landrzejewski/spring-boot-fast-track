package pl.training.payments.integration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import pl.training.Application;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.number.IsCloseTo.closeTo;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.training.payments.CardTestFixtures.TEST_CARD_NUMBER;
import static pl.training.payments.CardTestFixtures.TEST_MONEY;

@Sql(value = "/scripts/truncate_cards.sql", executionPhase = AFTER_TEST_METHOD)
@ActiveProfiles("test")
@WithMockUser(roles = "ADMIN")
@SpringBootTest(classes = Application.class, webEnvironment = DEFINED_PORT)
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class AddTransactionUseCaseTest {

    private static final String CARD_NUMBER = TEST_CARD_NUMBER.value();
    private static final String INFLOW_PAYLOAD = """
            {
               "type": "IN",
               "amount": 100.0,
               "currencyCode": "PLN"
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @Sql(value = "/scripts/add_transaction_test.sql", executionPhase = BEFORE_TEST_METHOD)
    @Test
    void add_transaction() throws Exception {
        mockMvc.perform(post("/api/cards/" + CARD_NUMBER + "/transactions").contentType(APPLICATION_JSON).content(INFLOW_PAYLOAD))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/cards/" + CARD_NUMBER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number", is(CARD_NUMBER)))
                .andExpect(jsonPath("$.transactions.size()", is(1)))
                .andExpect(jsonPath("$.transactions[0].type", is("IN")))
                .andExpect(jsonPath("$.transactions[0].value", closeTo(TEST_MONEY.amount().doubleValue(), 0.1)));
    }

}
