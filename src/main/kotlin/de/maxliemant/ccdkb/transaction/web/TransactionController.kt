package de.maxliemant.ccdkb.transaction.web

import de.maxliemant.ccdkb.common.ResponseWrapper
import de.maxliemant.ccdkb.transaction.model.Transaction
import de.maxliemant.ccdkb.transaction.service.TransactionService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/transactions")
class TransactionController(
    private val transactionService: TransactionService
) {

    @GetMapping("/{iban}")
    fun getTransactionsForIban(@PathVariable("iban") iban: String): ResponseWrapper<Collection<Transaction>> =
        ResponseWrapper.of(transactionService.findTransactions(iban))

    @PostMapping("/deposit")
    fun depositMoney(@RequestBody transaction: DepositDto): ResponseWrapper<Transaction> {
        val deposit = transaction.toTransaction()
        return ResponseWrapper.of(transactionService.depositMoney(deposit))
    }

    @PostMapping("/withdraw")
    fun withdrawMoney(@RequestBody transaction: WithdrawDto): ResponseWrapper<Transaction> {
        val withdraw = transaction.toTransaction()
        return ResponseWrapper.of(transactionService.withdrawMoney(withdraw))
    }

    @PostMapping("transfer")
    fun transferMoney(@RequestBody transaction: TransferDto): ResponseWrapper<Transaction> {
        val transfer = transaction.toTransaction()
        return ResponseWrapper.of(transactionService.transferMoney(transfer))
    }
}
