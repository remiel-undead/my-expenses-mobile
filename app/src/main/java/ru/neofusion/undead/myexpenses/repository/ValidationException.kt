package ru.neofusion.undead.myexpenses.repository

class ValidationException(val fields: List<Pair<String, String>>) : Exception()