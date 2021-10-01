package de.maxliemant.ccdkb.account.model

import org.springframework.data.repository.CrudRepository

interface AccountRepository: CrudRepository<Account, String> {
}
