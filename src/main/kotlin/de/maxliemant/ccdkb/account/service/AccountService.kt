package de.maxliemant.ccdkb.account.service

import de.maxliemant.ccdkb.account.model.Account
import de.maxliemant.ccdkb.account.model.AccountRepository
import de.maxliemant.ccdkb.account.model.AccountType
import org.springframework.stereotype.Service
import javax.persistence.EntityNotFoundException

@Service
class AccountService(
        val accountRepository: AccountRepository
) {

    fun getAllAccounts(typeFilters: List<AccountType>): Collection<Account> {
        val accounts = if (typeFilters.isEmpty()){
            accountRepository.findAll()
        }else{
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
            getAccount(account.referenceAccount)
        }
        return accountRepository.save(account)
    }

    fun updateAccount(account: Account): Account {
        return accountRepository.save(account)
    }

    fun lockAccount(iban: String): Account {
        TODO("Not yet implemented")
    }

    fun unlockAccount(iban: String): Account {
        TODO("Not yet implemented")
    }
}
