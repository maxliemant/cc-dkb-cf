package de.maxliemant.ccdkb.account.web

import de.maxliemant.ccdkb.account.model.Account
import de.maxliemant.ccdkb.account.model.AccountType
import de.maxliemant.ccdkb.exception.BadRequestException
import java.math.BigDecimal

data class AccountCreateDto(
    val iban: String,
    val accountOwner: String,
    val type: AccountType,
    val referenceAccount: String?,
    val currency: String
) {
    fun toAccount(): Account {
        if (type == AccountType.SAVING && referenceAccount == null) {
            throw BadRequestException("'saving' account need a reference account")
        }
        if (type != AccountType.SAVING && referenceAccount != null) {
            throw BadRequestException("only 'saving' account is allowed to have a reference account")
        }
        return Account(
            iban = iban,
            accountOwner = accountOwner,
            accountType = type,
            referenceAccount = referenceAccount,
            currency = currency,
            balance = BigDecimal.ZERO
        )
    }
}

