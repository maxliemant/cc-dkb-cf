package de.maxliemant.ccdkb.account.service

import de.maxliemant.ccdkb.account.model.Account
import de.maxliemant.ccdkb.account.model.AccountRepository
import de.maxliemant.ccdkb.account.model.AccountType
import de.maxliemant.ccdkb.exception.BadRequestException
import org.springframework.stereotype.Service
import javax.persistence.EntityNotFoundException

@Service
class AccountService(
        val accountRepository: AccountRepository
) {

    fun getAllAccounts(typeFilters: List<AccountType>): Collection<Account> {
        val accounts = if (typeFilters.isEmpty()) {
            accountRepository.findAll()
        } else {
            accountRepository.findAllByAccountTypeIn(typeFilters)
        }
        return accounts.toList()
    }

    fun getAccount(iban: String): Account {
        return accountRepository.findById(iban).orElseThrow { EntityNotFoundException("account with iban $iban not found") }
    }

    fun saveAccount(account: Account): Account {
        if (account.referenceAccount != null) {
            //reference account must exist
            val referenceAccount = getAccount(account.referenceAccount)
            if (referenceAccount.accountType != AccountType.CHECKING) {
                throw BadRequestException("reference account must be a checking account")
            }
        }
        return accountRepository.save(account)
    }

    fun updateAccount(account: Account): Account {
        return accountRepository.save(account)
    }

    fun lockAccount(iban: String): Account {
        val account = getAccount(iban)
        account.locked = true
        return updateAccount(account)
    }

    fun unlockAccount(iban: String): Account {
        val account = getAccount(iban)
        account.locked = false
        return updateAccount(account)
    }
}
