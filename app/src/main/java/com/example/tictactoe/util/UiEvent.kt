package com.example.tictactoe.util

import com.example.tictactoe.presentation.state.Sound

sealed class UiEvent {
    object onReset: UiEvent()
    object onResetCount: UiEvent()
    data class onBoxClicked(val row: Int, val col: Int): UiEvent()
    data class ShowMessage(val message: String): UiEvent()
    data class backgroundSound(val sound: Sound): UiEvent()
}