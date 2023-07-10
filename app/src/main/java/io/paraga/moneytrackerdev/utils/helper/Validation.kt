package io.paraga.moneytrackerdev.utils.helper

class Validation {


    fun lengthValidation(str: String, length: Int): Boolean {
        if (str.length < length) {
            return false
        }
        return true
    }

    fun compareText(str1: String, str2: String): Boolean {
        if (str1 != str2) {
            return false
        }
        return true
    }

    fun emailValidation(email: String): Boolean{

        val pattern = Regex("""
            [a-zA-Z0-9._-]+@[a-z]+\.[a-z]+
        """.trimIndent())
        if (pattern.containsMatchIn(email)) {
            return true
        }
        return false
    }



}