package de.maxliemant.ccdkb.transaction.service

import de.maxliemant.ccdkb.account.model.Account
import de.maxliemant.ccdkb.account.model.AccountType
import de.maxliemant.ccdkb.account.service.AccountService
import de.maxliemant.ccdkb.exception.BadRequestException
import de.maxliemant.ccdkb.transaction.model.Transaction
import de.maxliemant.ccdkb.transaction.model.TransactionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TransactionService(val transactionRepository: TransactionRepository,
                         val accountService: AccountService) {

    fun findTransactions(iban: String): Collection<Transaction> {
        return transactionRepository.findTransactionsByIban(iban)
    }

    @Transactional
    fun depositMoney(deposit: Transaction): Transaction {
        val account = accountService.getAccount(deposit.receivingIban!!)
        checkCurrency(account, deposit)
        account.balance = account.balance.plus(deposit.amount)
        accountService.updateAccount(account)
        return transactionRepository.save(deposit)
    }

    @Transactional
    fun withdrawMoney(withdraw: Transaction): Transaction {
        val account = accountService.getAccount(withdraw.sendingIban!!)
        if (!account.accountType.withdrawable) {
            throw BadRequestException("account is not allowed for withdrawing money")
        }
        checkAccountLocked(account)
        checkCurrency(account, withdraw)
        checkFunds(account, withdraw)
        account.balance = account.balance.minus(withdraw.amount)
        accountService.updateAccount(account)
        return transactionRepository.save(withdraw)
    }

    /**
     * transfers money from one account to another.
     * the sending account needs to be not locked.
     * if the sending account is not a checking account special rules apply.
     * Saving accounts can only send money to their reference account.
     */
    @Transactional
    fun transferMoney(transfer: Transaction): Transaction {
        val receiver = accountService.getAccount(transfer.receivingIban!!)
        val sender = accountService.getAccount(transfer.sendingIban!!)
        if (sender.accountType == AccountType.PRIVATE_LOAN) {
            throw BadRequestException("account is not allowed for transferring money")
        }
        if (sender.accountType == AccountType.SAVING && transfer.receivingIban != sender.referenceAccount) {
            throw BadRequestException("account is only allowed to transfer money to the reference account")
        }

        checkCurrency(sender, transfer)
        checkCurrency(receiver, transfer)

        checkAccountLocked(sender)
        checkFunds(sender, transfer)

        sender.balance = sender.balance.minus(transfer.amount)
        receiver.balance = receiver.balance.plus(transfer.amount)
        accountService.updateAccount(sender)
        accountService.updateAccount(receiver)
        return transactionRepository.save(transfer)
    }

    /**
     * This function throws an error if the currency of the account and the transaction currency doesn't match, as making use of exchange rates is not part of this PoC
     */
    private fun checkCurrency(account: Account, transaction: Transaction) {
        if (account.currency != transaction.currency) {
            throw BadRequestException("currency of account and transaction doesn't match")
        }
    }

    private fun checkAccountLocked(account: Account) {
        if (account.locked) {
            throw BadRequestException("account is locked")
        }
    }

    private fun checkFunds(account: Account, transfer: Transaction) {
        if (account.balance < transfer.amount) {
            throw BadRequestException("insufficient funds for transferring money")
        }
    }
}
