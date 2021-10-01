package de.maxliemant.ccdkb.account.model

import java.math.BigDecimal
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class Account(
        @Id
        val iban: String,
        val accountOwner: String,
        val type: AccountType,
        val balance: BigDecimal,
        val referenceAccount: String?, //referenced by iban
        val currency: String
) {
}

enum class AccountType {
    CHECKING,
    SAVING,
    PRIVATE_LOAN
}
