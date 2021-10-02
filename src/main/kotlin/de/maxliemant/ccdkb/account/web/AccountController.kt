package de.maxliemant.ccdkb.account.web

import de.maxliemant.ccdkb.account.model.Account
import de.maxliemant.ccdkb.account.model.AccountType
import de.maxliemant.ccdkb.account.service.AccountService
import de.maxliemant.ccdkb.exception.BadRequestException
import mu.KLogging
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/accounts")
class AccountController(
        private val accountService: AccountService
) {

    @GetMapping
    fun getAccounts(@RequestParam(required = false) accountType: String?): Collection<Account> {
        val typeFilters = if(accountType != null){
            try {
                val list = accountType.split(",")
                list.map { AccountType.valueOf(it) }
            }catch (ex: Exception){
                throw BadRequestException("Could not parse account type filter. Allowed values are '${AccountType.values().asList().joinToString(", ")}'")
            }
        }else {
            emptyList()
        }
        return accountService.getAllAccounts(typeFilters)
    }

    @GetMapping("/{iban}")
    fun getAccount(@PathVariable("iban") iban: String): Account {
        return accountService.getAccount(iban)
    }

    @PostMapping
    fun createAccount(@RequestBody accountCreateDto: AccountCreateDto): Account {
        return accountService.saveAccount(accountCreateDto.toAccount())
    }

    @PutMapping("/{iban}/lock")
    fun lockAccount(@PathVariable("iban") iban: String): Account {
        return accountService.lockAccount(iban)
    }

    @PutMapping("/{iban}/unlock")
    fun unlockAccount(@PathVariable("iban") iban: String): Account {
        return accountService.unlockAccount(iban)
    }

    companion object : KLogging()
}
