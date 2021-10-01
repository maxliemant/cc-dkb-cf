package de.maxliemant.ccdkb.transaction.model

import java.math.BigDecimal
import java.time.ZonedDateTime
import java.util.*
import javax.persistence.Id

data class Transaction(
        @Id
        val id: UUID,
        val receivingIban: String,
        val sendingIban: String,
        val amount: BigDecimal,
        val currency: String,
        val timestamp: ZonedDateTime
)

