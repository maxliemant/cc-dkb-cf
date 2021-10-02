package de.maxliemant.ccdkb.transaction.service

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
        account.balance = account.balance.plus(deposit.amount)
        accountService.updateAccount(account)
        return transactionRepository.save(deposit)
    }

    @Transactional
    fun withdrawMoney(withdraw: Transaction): Transaction {
        val account = accountService.getAccount(withdraw.sendingIban!!)
        if(!account.accountType.withdrawable){
            throw BadRequestException("account is not allowed for withdrawing money")
        }
        if(account.balance < withdraw.amount){
            throw BadRequestException("insufficient funds for withdrawing money")
        }
        account.balance = account.balance.minus(withdraw.amount)
        accountService.updateAccount(account)
        return transactionRepository.save(withdraw)
    }

    @Transactional
    fun transferMoney(transfer: Transaction): Transaction {
        val receiver = accountService.getAccount(transfer.receivingIban!!)
        val sender = accountService.getAccount(transfer.sendingIban!!)
        if(sender.accountType == AccountType.PRIVATE_LOAN){
            throw BadRequestException("account is not allowed for transferring money")
        }
        if(sender.accountType == AccountType.SAVING && transfer.receivingIban != sender.referenceAccount){
            throw BadRequestException("account is only allowed to transfer money to the reference account")
        }
        if(sender.balance < transfer.amount){
            throw BadRequestException("insufficient funds for transferring money")
        }
        sender.balance = sender.balance.minus(transfer.amount)
        receiver.balance = receiver.balance.plus(transfer.amount)
        accountService.updateAccount(sender)
        accountService.updateAccount(receiver)
        return transactionRepository.save(transfer)
    }
}
