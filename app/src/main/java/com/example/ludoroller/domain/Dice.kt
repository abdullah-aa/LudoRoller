package com.beachvilletek.ludoroller.domain

class Dice (private val numSides: Int) {

    fun roll(): Int = (1..numSides).random()
}