package com.amirami.simapp.radiobroadcast.pairalarm.model

enum class AlarmModeType(val mode: Int) {
    NORMAL(0),
    CALCULATE(1)
}

data class CalculatorProblem(
    val number1: Int,
    val number2: Int,
    val answer: String
)