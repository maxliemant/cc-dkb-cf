package de.maxliemant.ccdkb.account.service

import de.maxliemant.ccdkb.account.model.Account
import de.maxliemant.ccdkb.account.model.AccountRepository
import org.springframework.stereotype.Service
import javax.persistence.EntityNotFoundException

@Service
class AccountService(
        val accountRepository: AccountRepository
) {

    fun getAllAccounts(): Collection<Account> {
        return accountRepository.findAll().toList()
    }

    fun getAccount(iban: String): Account {
        return accountRepository.findById(iban).orElseThrow { EntityNotFoundException("account with iban $iban not found") }
    }

    fun saveAccount(account: Account): Account {
        return accountRepository.save(account)
    }
}
