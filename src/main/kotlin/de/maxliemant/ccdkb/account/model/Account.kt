package de.maxliemant.ccdkb.account.model

import java.math.BigDecimal
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id

@Entity
data class Account(
        @Id
        val iban: String="",
        val accountOwner: String="",
        @Enumerated(EnumType.STRING)
        val type: AccountType=AccountType.CHECKING,
        val balance: BigDecimal = BigDecimal.ZERO,
        val referenceAccount: String?=null, //referenced by iban
        val currency: String="EUR"
)

enum class AccountType {
    CHECKING,
    SAVING,
    PRIVATE_LOAN
}
