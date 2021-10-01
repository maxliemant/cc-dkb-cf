package de.maxliemant.ccdkb.account.web

import de.maxliemant.ccdkb.account.model.Account
import de.maxliemant.ccdkb.account.service.AccountService
import mu.KLogging
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/accounts")
class AccountController(
        private val accountService: AccountService
) {

    @GetMapping
    fun getAccounts(): Collection<Account> {
        logger.info("get all accounts")
        return accountService.getAllAccounts()
    }

    @GetMapping("/{iban}")
    fun getAccount(@PathVariable("iban") iban: String): Account {
        return accountService.getAccount(iban)
    }

    @PostMapping
    fun createAccount(@RequestBody account: AccountCreateDto): Account {
        return accountService.saveAccount(account.toAccount())
    }

    @PutMapping
    fun updateAccount(@RequestBody account: Account): Account {
        return account
    }


    companion object: KLogging()
}
