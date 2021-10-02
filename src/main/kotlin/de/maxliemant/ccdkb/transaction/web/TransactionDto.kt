package de.maxliemant.ccdkb.transaction.web

import de.maxliemant.ccdkb.exception.BadRequestException
import de.maxliemant.ccdkb.transaction.model.Transaction
import de.maxliemant.ccdkb.transaction.model.TransactionType
import java.math.BigDecimal

data class TransactionDto(
        val receivingIban: String?,
        val sendingIban: String?,
        val amount: String,
        val currency: String,
) {
    fun toDeposit(iban: String): Transaction {
        val depositAmount = BigDecimal(amount)
        if(depositAmount <= BigDecimal.ZERO){
            throw BadRequestException("cannot deposit a zero or negative amount")
        }
        return Transaction(
                receivingIban = iban,
                currency = currency,
                amount = depositAmount,
                transactionType = TransactionType.DEPOSIT
        )
    }

    fun toWithdraw(iban: String): Transaction {
        val withdrawAmount = BigDecimal(amount)
        if(withdrawAmount >= BigDecimal.ZERO){
            throw BadRequestException("cannot withdraw a positive amount")
        }
        return Transaction(
                sendingIban = iban,
                currency = currency,
                amount = withdrawAmount,
                transactionType = TransactionType.WITHDRAW
        )
    }

    fun toTransfer(): Transaction {
        val transferAmount = BigDecimal(amount)
        if(transferAmount <= BigDecimal.ZERO){
            throw BadRequestException("cannot transfer a negative amount")
        }
        return Transaction(
                receivingIban = receivingIban,
                sendingIban = sendingIban,
                currency = currency,
                amount = transferAmount,
                transactionType = TransactionType.TRANSFER
        )
    }
}

