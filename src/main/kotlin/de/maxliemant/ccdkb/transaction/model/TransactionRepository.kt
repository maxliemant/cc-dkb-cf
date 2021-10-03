package de.maxliemant.ccdkb.transaction.model

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface TransactionRepository : CrudRepository<Transaction, UUID> {
    @Query(
        value = "SELECT * FROM TRANSACTIONS WHERE RECEIVING_IBAN = ?1 or SENDING_IBAN = ?1",
        nativeQuery = true
    )
    fun findTransactionsByIban(iban: String): List<Transaction>
}
