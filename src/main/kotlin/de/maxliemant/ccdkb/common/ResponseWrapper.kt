package de.maxliemant.ccdkb.common

data class ResponseWrapper<T>(
    val data: T?,
    val errors: List<String>?
) {

    companion object {

        fun <T> of(data: T): ResponseWrapper<T> {
            return ResponseWrapper(data, null)
        }

        fun <T> ofErrors(errors: List<String>): ResponseWrapper<T> {
            return ResponseWrapper(null, errors)
        }
    }
}
