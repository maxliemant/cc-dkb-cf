package de.maxliemant.ccdkb.transaction.web

import de.maxliemant.ccdkb.common.ResponseWrapper
import de.maxliemant.ccdkb.transaction.model.Transaction
import de.maxliemant.ccdkb.transaction.service.TransactionService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/transactions")
class TransactionController(
        val transactionService: TransactionService
) {

    @GetMapping("/{iban}")
    fun getTransactionsForIban(@PathVariable("iban") iban: String): ResponseWrapper<Collection<Transaction>> =
            ResponseWrapper.of(transactionService.findTransactions(iban))

    @PostMapping("/{iban}/deposit")
    fun depositMoney(@PathVariable("iban") iban: String,
                     @RequestBody transaction: TransactionDto): ResponseWrapper<Transaction> {
        val deposit = transaction.toDeposit(iban)
        return ResponseWrapper.of(transactionService.depositMoney(deposit))
    }

    @PostMapping("/{iban}/withdraw")
    fun withdrawMoney(@PathVariable("iban") iban: String,
                     @RequestBody transaction: TransactionDto): ResponseWrapper<Transaction> {
        val withdraw = transaction.toWithdraw(iban)
        return ResponseWrapper.of(transactionService.withdrawMoney(withdraw))
    }

    @PostMapping("transfer")
    fun transferMoney(@RequestBody transaction: TransactionDto): ResponseWrapper<Transaction> {
        val transfer = transaction.toTransfer()
        return ResponseWrapper.of(transactionService.transferMoney(transfer))
    }
}
