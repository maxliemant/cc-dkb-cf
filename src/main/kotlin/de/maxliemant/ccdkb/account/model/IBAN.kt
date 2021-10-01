package de.maxliemant.ccdkb.account.model

data class IBAN(
        val country: Country,
        val checksum: Int,
        val accountId: string
){

}
