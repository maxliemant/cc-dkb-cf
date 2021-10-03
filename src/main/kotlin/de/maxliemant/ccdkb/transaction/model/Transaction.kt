package de.maxliemant.ccdkb.transaction.model

import java.math.BigDecimal
import java.time.ZonedDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "TRANSACTIONS")
data class Transaction(
    @Id
    val id: UUID = UUID.randomUUID(),
    val receivingIban: String? = null,
    val sendingIban: String? = null,
    val amount: BigDecimal = BigDecimal.ZERO,
    val currency: String = "EUR",
    val timestamp: ZonedDateTime = ZonedDateTime.now(),
    @Enumerated(EnumType.STRING)
    val transactionType: TransactionType = TransactionType.TRANSFER
)

enum class TransactionType {
    WITHDRAW,
    DEPOSIT,
    TRANSFER
}

