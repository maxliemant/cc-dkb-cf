package de.maxliemant.ccdkb.account.service

import de.maxliemant.ccdkb.account.model.Account
import de.maxliemant.ccdkb.account.model.AccountRepository
import de.maxliemant.ccdkb.account.model.AccountType
import de.maxliemant.ccdkb.account.web.AccountCreateDto
import de.maxliemant.ccdkb.exception.BadRequestException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal
import javax.persistence.EntityNotFoundException

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AccountServiceTest {

    @Autowired
    lateinit var accountService: AccountService

    @Autowired
    lateinit var accountRepository: AccountRepository

    val createIban = "DE231203000create123412"
    val checkingIban = "DE23120300001234123412"
    val savingIban = "DE73120300001234123433"
    val privateLoanIban = "DE11120300001234123400"

    @Nested
    @DisplayName("getAccounts")
    inner class GetAccountsTest {

        @Test
        fun getAllAccounts() {
            val accounts = accountService.getAllAccounts(emptyList())

            assertThat(accounts).hasSize(4)
        }

        @Test
        fun filterAccounts() {
            val accounts = accountService.getAllAccounts(listOf(AccountType.CHECKING))

            assertThat(accounts).hasSize(2)
        }
    }

    @Nested
    @DisplayName("lockAccount")
    inner class LockAccountTest {
        @Test
        fun lockAccount() {
            accountService.lockAccount(checkingIban)
            val account = accountService.getAccount(checkingIban)
            assertThat(account.locked).isEqualTo(true)
        }

        @Test
        fun unlockAccount() {
            accountService.unlockAccount(checkingIban)
            val account = accountService.getAccount(checkingIban)
            assertThat(account.locked).isEqualTo(false)
        }
    }

    @Nested
    @DisplayName("createAccount")
    inner class CreateAccountTest {

        @AfterEach
        fun afterEach() {
            if (accountRepository.existsById(createIban)) {
                accountRepository.deleteById(createIban)
            }
        }

        @Test
        fun failCreateSavingAccountWithoutReference() {
            val createAccount = AccountCreateDto(createIban, "Tony Stark", AccountType.SAVING, null, "EUR")
            assertThrows<BadRequestException> {
                accountService.saveAccount(createAccount.toAccount())
            }
            assertThrows<EntityNotFoundException> {
                accountService.getAccount(createIban)
            }
        }

        @Test
        fun createSavingAccountWithReference() {
            val createAccount = AccountCreateDto(createIban, "Tony Stark", AccountType.SAVING, checkingIban, "EUR")

            accountService.saveAccount(createAccount.toAccount())

            val createdAccount = accountService.getAccount(createIban)
            val expectedAccount = Account(createIban, "Tony Stark", AccountType.SAVING, BigDecimal("0.00"), checkingIban, "EUR", false)
            assertThat(createdAccount).isEqualTo(expectedAccount)
        }

        @Test
        fun createCheckingAccountWithoutReference() {
            val createAccount = AccountCreateDto(createIban, "Tony Stark", AccountType.CHECKING, null, "EUR")

            accountService.saveAccount(createAccount.toAccount())

            val createdAccount = accountService.getAccount(createIban)
            val expectedAccount = Account(createIban, "Tony Stark", AccountType.CHECKING, BigDecimal("0.00"), null, "EUR", false)
            assertThat(createdAccount).isEqualTo(expectedAccount)
        }
    }
}
