package de.maxliemant.ccdkb.transaction.service

import de.maxliemant.ccdkb.account.model.Account
import de.maxliemant.ccdkb.account.model.AccountType
import de.maxliemant.ccdkb.account.service.AccountService
import de.maxliemant.ccdkb.exception.BadRequestException
import de.maxliemant.ccdkb.transaction.web.DepositDto
import de.maxliemant.ccdkb.transaction.web.TransferDto
import de.maxliemant.ccdkb.transaction.web.WithdrawDto
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TransactionServiceTest {

    @Autowired
    lateinit var accountService: AccountService

    @Autowired
    lateinit var transactionService: TransactionService

    val lockedIban = "DE2312locked1234123432"
    val checkingIban = "DE23120300001234123412"
    val savingIban = "DE73120300001234123433"
    val privateLoanIban = "DE11120300001234123400"

    @BeforeAll
    fun before() {
        accountService.saveAccount(Account(lockedIban, "Tony Stark", AccountType.CHECKING, BigDecimal("315.20"), null, "EUR", true))
    }

    @Nested
    @DisplayName("getTransactions")
    inner class GetTransactionsTest {

        @Test
        fun getAllTransactionsForAccount() {
            val checkingAccount = accountService.getAccount(checkingIban)

            val transactions = transactionService.findTransactions(checkingIban)
            assertThat(transactions).hasSize(5)
        }
    }


    @Nested
    @DisplayName("transferMoney")
    inner class TransferMoneyTest {

        @Test
        fun transferMoneyFromSavingToCheckingAccount() {
            val checkingAccount = accountService.getAccount(checkingIban)
            val savingAccount = accountService.getAccount(savingIban)

            val transaction = TransferDto(
                receivingIban = checkingIban,
                sendingIban = savingIban,
                amount = "1.00",
                currency = checkingAccount.currency
            ).toTransaction()

            transactionService.transferMoney(transaction)

            val updatedChecking = accountService.getAccount(checkingIban)
            val updatedSaving = accountService.getAccount(savingIban)

            assertThat(updatedChecking.balance).isEqualTo(checkingAccount.balance.plus(transaction.amount))
            assertThat(updatedSaving.balance).isEqualTo(savingAccount.balance.minus(transaction.amount))
        }

        @Test
        fun doNotTransferMoneyToOtherThanReferenceAccountForSaving() {
            val savingAccount = accountService.getAccount(savingIban)

            val transaction = TransferDto(
                privateLoanIban,
                savingIban,
                "10.00",
                savingAccount.currency
            ).toTransaction()

            assertThrows<BadRequestException> { transactionService.transferMoney(transaction) }

            val updatedSaving = accountService.getAccount(savingIban)

            assertThat(updatedSaving.balance).isEqualTo(savingAccount.balance)
        }

        @Test
        fun doNotTransferMoneyForLockedAccount() {
            val lockedAccount = accountService.getAccount(lockedIban)
            val checkingAccount = accountService.getAccount(checkingIban)

            val transaction = TransferDto(
                receivingIban = checkingIban,
                sendingIban = lockedIban,
                amount = "10.00",
                currency = lockedAccount.currency
            ).toTransaction()

            assertThrows<BadRequestException> { transactionService.transferMoney(transaction) }

            val updatedLocked = accountService.getAccount(lockedIban)

            assertThat(updatedLocked.balance).isEqualTo(lockedAccount.balance)
        }

        @Test
        fun canNotTransferMoneyHigherThanBalance() {
            val checkingAccount = accountService.getAccount(checkingIban)
            val savingAccount = accountService.getAccount(savingIban)

            val transaction = TransferDto(
                receivingIban = savingIban,
                sendingIban = checkingIban,
                amount = checkingAccount.balance.plus(BigDecimal.ONE).toPlainString(),
                currency = checkingAccount.currency
            ).toTransaction()

            assertThrows<BadRequestException> {
                transactionService.transferMoney(transaction)
            }

            val updatedChecking = accountService.getAccount(checkingIban)

            assertThat(updatedChecking.balance).isEqualTo(checkingAccount.balance)
        }

        @Test
        fun canNotTransferMoneyToSameAccount() {
            val transaction = TransferDto(
                receivingIban = checkingIban,
                sendingIban = checkingIban,
                amount = "1.00",
                currency = "EUR"
            ).toTransaction()

            assertThrows<BadRequestException> {
                transactionService.transferMoney(transaction)
            }
        }

        @Test
        fun receivingIbanNeedToBeNotNull() {
            val transaction = TransferDto(
                receivingIban = null,
                sendingIban = checkingIban,
                amount = "1.00",
                currency = "EUR"
            ).toTransaction()

            assertThrows<BadRequestException> {
                transactionService.transferMoney(transaction)
            }
        }

        @Test
        fun sendingIbanNeedToBeNotNull() {
            val transaction = TransferDto(
                receivingIban = checkingIban,
                sendingIban = null,
                amount = "1.00",
                currency = "EUR"
            ).toTransaction()

            assertThrows<BadRequestException> {
                transactionService.transferMoney(transaction)
            }
        }
    }

    @Nested
    @DisplayName("withdrawMoney")
    inner class WithdrawMoneyTest {

        @Test
        fun withdrawMoneyFromCheckingAccount() {
            val checkingAccount = accountService.getAccount(checkingIban)

            val transaction = WithdrawDto(
                iban = checkingIban,
                amount = "1.00",
                currency = checkingAccount.currency
            ).toTransaction()

            transactionService.withdrawMoney(transaction)

            val updatedChecking = accountService.getAccount(checkingIban)

            assertThat(updatedChecking.balance).isEqualTo(checkingAccount.balance.minus(transaction.amount))
        }

        @Test
        fun doNotWithdrawMoneyFromSavingAccount() {
            val savingAccount = accountService.getAccount(savingIban)

            val transaction = WithdrawDto(
                iban = savingIban,
                amount = "1.00",
                currency = savingAccount.currency
            ).toTransaction()

            assertThrows<BadRequestException> {
                transactionService.withdrawMoney(transaction)
            }

            val updatedSaving = accountService.getAccount(savingIban)

            assertThat(updatedSaving.balance).isEqualTo(savingAccount.balance)
        }

        @Test
        fun canNotWithdrawMoneyHigherThanBalance() {
            val checkingAccount = accountService.getAccount(checkingIban)

            val transaction = WithdrawDto(
                iban = checkingIban,
                amount = checkingAccount.balance.plus(BigDecimal.ONE).toPlainString(),
                currency = checkingAccount.currency
            ).toTransaction()

            assertThrows<BadRequestException> {
                transactionService.withdrawMoney(transaction)
            }

            val updatedChecking = accountService.getAccount(checkingIban)

            assertThat(updatedChecking.balance).isEqualTo(checkingAccount.balance)
        }
    }

    @Nested
    @DisplayName("depositMoney")
    inner class DepositMoneyTest {

        @Test
        fun depositMoneyFromCheckingAccount() {
            val checkingAccount = accountService.getAccount(checkingIban)

            val transaction = DepositDto(
                iban = checkingIban,
                amount = "1.00",
                currency = checkingAccount.currency
            ).toTransaction()

            transactionService.depositMoney(transaction)

            val updatedChecking = accountService.getAccount(checkingIban)

            assertThat(updatedChecking.balance).isEqualTo(checkingAccount.balance.plus(transaction.amount))
        }
    }

}
