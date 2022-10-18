package uz.akhmad.integration;

import uz.akhmad.controllers.AccountRestController;
import uz.akhmad.models.Account;
import uz.akhmad.utils.AccountInput;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(value = "local")
class CheckBalanceIntegrationTest {

    @Autowired
    private AccountRestController accountRestController;

    @Test
    void givenAccountDetails_whenCheckingBalance_thenVerifyAccountCorrect() {
        // given
        var input = new AccountInput();
        input.setSortCode("38-73-63");
        input.setAccountNumber("42998777");

        // when
        var body = accountRestController.checkAccountBalance(input).getBody();

        // then
        var account = (Account) body;
        assertThat(account).isNotNull();
        assertThat(account.getOwnerName()).isEqualTo("Ahmadali Gulomov");
        assertThat(account.getSortCode()).isEqualTo("38-73-63");
        assertThat(account.getAccountNumber()).isEqualTo("42998777");
    }
}
