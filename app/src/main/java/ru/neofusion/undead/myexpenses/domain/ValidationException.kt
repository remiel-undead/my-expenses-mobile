package ru.neofusion.undead.myexpenses.domain

class ValidationException(val fields: List<Pair<String, String>>) : Exception()