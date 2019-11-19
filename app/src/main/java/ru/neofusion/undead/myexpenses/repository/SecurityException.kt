package ru.neofusion.undead.myexpenses.repository

sealed class SecurityException : Exception() {
    class InvalidLoginPassword : SecurityException()
    class KeyLimitIsOut : SecurityException()
    class KeyGenerationError : SecurityException()
}