package uz.akhmad.unit;

import uz.akhmad.controllers.AccountRestController;
import uz.akhmad.models.Account;
import uz.akhmad.services.AccountService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AccountRestController.class)
class AccountRestControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AccountService accountService;

    @Test
    void givenMissingInput_whenCheckingBalance_thenVerifyBadRequest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/accounts")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void givenInvalidInput_whenCheckingBalance_thenVerifyBadRequest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/accounts")
                .content("{\"sortCode\": \"38-73\",\"accountNumber\": \"42998\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void givenNoAccountForInput_whenCheckingBalance_thenVerifyNoContent() throws Exception {
        given(accountService.getAccount("38-73-63", "42998777")).willReturn(null);

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/accounts")
                .content("{\"sortCode\": \"38-73-63\",\"accountNumber\": \"42998777\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void givenAccountDetails_whenCheckingBalance_thenVerifyOk() throws Exception {
        given(accountService.getAccount(null, null)).willReturn(
                new Account(1L, "38-73-63", "42998777", 1000.10, "Bank of Ferghana", "Ahmadali Gulomov"));

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/accounts")
                .content("{\"sortCode\": \"38-73-63\",\"accountNumber\": \"42998777\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }
}
