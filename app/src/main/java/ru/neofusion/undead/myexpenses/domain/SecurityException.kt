package ru.neofusion.undead.myexpenses.domain

sealed class SecurityException : Exception() {
    class InvalidLoginPassword : SecurityException()
    class KeyLimitIsOut : SecurityException()
    class KeyGenerationError : SecurityException()
}