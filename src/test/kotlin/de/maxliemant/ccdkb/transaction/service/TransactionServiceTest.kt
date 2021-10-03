package de.maxliemant.ccdkb.transaction.service

import de.maxliemant.ccdkb.account.model.Account
import de.maxliemant.ccdkb.account.model.AccountRepository
import de.maxliemant.ccdkb.account.model.AccountType
import de.maxliemant.ccdkb.account.service.AccountService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TransactionServiceTest {

    @Autowired
    lateinit var accountService: AccountService

    @Autowired
    lateinit var accountRepository: AccountRepository

    @Autowired
    lateinit var transactionService: TransactionService

    val lockedIban = "DE2312locked1234123432"

    @BeforeAll
    fun before() {
        accountService.saveAccount(Account(lockedIban, "Max Liemant", AccountType.CHECKING, BigDecimal("10.20"), null, "EUR", true))
    }

    @Test
    fun test() {
        assertThat(true).isTrue
        val findTransactions = transactionService.findTransactions(lockedIban)
        assertThat(findTransactions).isEmpty()
    }
}
