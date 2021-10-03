package de.maxliemant.ccdkb.transaction.web

import de.maxliemant.ccdkb.exception.BadRequestException
import de.maxliemant.ccdkb.transaction.model.Transaction
import de.maxliemant.ccdkb.transaction.model.TransactionType
import java.math.BigDecimal


data class DepositDto(
    val iban: String,
    val amount: String,
    val currency: String,
) {
    fun toTransaction(): Transaction {
        val depositAmount = BigDecimal(amount)
        checkAmountNotNegativeOrZero(depositAmount)
        return Transaction(
            receivingIban = iban,
            currency = currency,
            amount = depositAmount,
            transactionType = TransactionType.DEPOSIT
        )
    }
}

data class WithdrawDto(
    val iban: String,
    val amount: String,
    val currency: String,
) {
    fun toTransaction(): Transaction {
        val withdrawAmount = BigDecimal(amount)
        checkAmountNotNegativeOrZero(withdrawAmount)
        return Transaction(
            sendingIban = iban,
            currency = currency,
            amount = withdrawAmount,
            transactionType = TransactionType.WITHDRAW
        )
    }
}

data class TransferDto(
    val receivingIban: String?,
    val sendingIban: String?,
    val amount: String,
    val currency: String,
) {

    fun toTransaction(): Transaction {
        val transferAmount = BigDecimal(amount)
        checkAmountNotNegativeOrZero(transferAmount)
        return Transaction(
            receivingIban = receivingIban,
            sendingIban = sendingIban,
            currency = currency,
            amount = transferAmount,
            transactionType = TransactionType.TRANSFER
        )
    }

}

fun checkAmountNotNegativeOrZero(transferAmount: BigDecimal) {
    if (transferAmount <= BigDecimal.ZERO) {
        throw BadRequestException("cannot transfer a zero or negative amount")
    }
}
