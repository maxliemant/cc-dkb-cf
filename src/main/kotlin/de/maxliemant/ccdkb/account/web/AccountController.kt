package de.maxliemant.ccdkb.account.web

import de.maxliemant.ccdkb.account.model.Account
import de.maxliemant.ccdkb.account.service.AccountService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/account")
class AccountController(
        private val accountService: AccountService
) {

    @GetMapping
    fun getAccounts(): Collection<Account> {
        return emptyList()
    }

    @GetMapping("/{iban}")
    fun getAccount(@PathVariable("iban") iban: String): Account {
        TODO("not yet implemented")
//        return Account()
    }

    @PostMapping
    fun createAccount(@RequestBody account: Account): Account {
        return account
    }

    @PutMapping
    fun updateAccount(@RequestBody account: Account): Account {
        return account
    }


}
