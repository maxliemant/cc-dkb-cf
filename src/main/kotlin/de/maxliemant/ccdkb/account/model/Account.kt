package de.maxliemant.ccdkb.account.model

import java.math.BigDecimal
import javax.persistence.*

@Entity
@Table(name="ACCOUNTS")
data class Account(
        @Id
        val iban: String="",
        val accountOwner: String="",
        @Enumerated(EnumType.STRING)
        val accountType: AccountType=AccountType.CHECKING,
        var balance: BigDecimal = BigDecimal.ZERO,
        val referenceAccount: String?=null, //referenced by iban
        val currency: String="EUR",
        var locked: Boolean = false
)

enum class AccountType(val withdrawable: Boolean) {
    CHECKING(true),
    SAVING(false),
    PRIVATE_LOAN(false);
}
