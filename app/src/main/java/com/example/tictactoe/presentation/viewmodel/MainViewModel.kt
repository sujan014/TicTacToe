package com.example.tictactoe.presentation.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tictactoe.R
import com.example.tictactoe.presentation.state.BoxState
import com.example.tictactoe.presentation.state.Sound
import com.example.tictactoe.presentation.state.User
import com.example.tictactoe.util.UiEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {

    var _user by mutableStateOf(User.user1)
    private set

    var _prevUser by mutableStateOf(User.user1)
    private set

    var gameOver by mutableStateOf(false)

    var _boxState = mutableStateListOf(
        BoxState.empty, BoxState.empty, BoxState.empty,
        BoxState.empty, BoxState.empty, BoxState.empty,
        BoxState.empty, BoxState.empty, BoxState.empty,
    )
    private set

    var primaryColor
        get() = Color.Gray
        set(value) = TODO()

    var _backColor = mutableStateListOf(
        primaryColor, primaryColor, primaryColor,
        primaryColor, primaryColor, primaryColor,
        primaryColor, primaryColor, primaryColor
    )

    private val _elements = mutableStateListOf<Boolean>(
        false, false, false,
        false, false, false,
        false, false, false
    )

    val elements: SnapshotStateList<Boolean> = _elements

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    @SuppressLint("StaticFieldLeak")
    private lateinit var context: Context
    private lateinit var buttonPlay: MediaPlayer
    private lateinit var errorPlay: MediaPlayer
    private lateinit var winnerPlay: MediaPlayer
    fun useContext(passedContext: Context){
        if (!::context.isInitialized)
            context = passedContext
        // do things with context here
        if (!::buttonPlay.isInitialized) buttonPlay = MediaPlayer.create(context, R.raw.buttonclick)
        if (!::errorPlay.isInitialized) errorPlay= MediaPlayer.create(context, R.raw.wrongclick)
        if (!::winnerPlay.isInitialized) winnerPlay= MediaPlayer.create(context, R.raw.winner)
    }

    var soundJob: Job? = null
    fun playSound(sound: Sound){
        soundJob?.cancel()
        soundJob = viewModelScope.launch {
            when(sound){
                Sound.Error -> {
                    errorPlay.start()
                }
                Sound.Click -> {
                    buttonPlay.start()
                }
                Sound.Winner -> {
                    winnerPlay.start()
                }
                Sound.Stop -> {
                }
            }
        }
    }

    fun nextUser(){
        _user = if (_user == User.user1){
            User.user2
        } else{
            User.user1
        }
        Log.d("LogVal","$_user")
    }

    fun onButtonClicked(){
        var rowgame = true
        var colgame = true
        var fullBox = true
        var frontdiagonalgame = true
        var backdiagonalgame = true
        var winner = ""

        // check horizontal
        for ( row in 0 until 3){
            rowgame = true
            Log.d("LogVal", "rowStart:$row")
            for(col in 0  until 2) {
                if (_boxState[row * 3 + col] == BoxState.empty && _boxState[row * 3 + col + 1] == BoxState.empty) {
                    rowgame = false
                    break
                }
                if (_boxState[row * 3 + col] != _boxState[row * 3 + col + 1]) {
                    rowgame = false
                    break
                }
            }
            Log.d("LogVal", "rowEnd:$row")
            if (rowgame){
                when (row) {
                    0 -> {
                        _backColor[0] = Color.Green
                        _backColor[1] = Color.Green
                        _backColor[2] = Color.Green
                    }
                    1 -> {
                        _backColor[3] = Color.Green
                        _backColor[4] = Color.Green
                        _backColor[5] = Color.Green
                    }
                    2 -> {
                        _backColor[6] = Color.Green
                        _backColor[7] = Color.Green
                        _backColor[8] = Color.Green
                    }
                }

                gameOver = true
                winner = if (_prevUser == User.user1) "Player 1" else "Player 2"
                sendUiEvent(UiEvent.ShowMessage("$winner won."))
                playSound(Sound.Winner)
                return
            }
        }

        // check vertical
        for ( col in 0 until 3){
            colgame = true
            Log.d("LogVal", "colStart:$col")
            for(row in 0  until 2) {
                if (_boxState[col + row * 3]  == BoxState.empty && _boxState[col + (row+1) * 3] == BoxState.empty) {
                    colgame = false
                    break
                }
                if (_boxState[col + row * 3] != _boxState[col + (row+1) * 3]) {
                    colgame = false
                    break
                }
            }
            Log.d("LogVal", "colEnd:$col")
            if (colgame){
                when (col) {
                    0 -> {
                        _backColor[0] = Color.Green
                        _backColor[3] = Color.Green
                        _backColor[6] = Color.Green
                    }
                    1 -> {
                        _backColor[1] = Color.Green
                        _backColor[4] = Color.Green
                        _backColor[7] = Color.Green
                    }
                    2 -> {
                        _backColor[2] = Color.Green
                        _backColor[5] = Color.Green
                        _backColor[8] = Color.Green
                    }
                }
                gameOver = true
                winner = if (_prevUser == User.user1) "Player 1" else "Player 2"
                sendUiEvent(UiEvent.ShowMessage("$winner won."))
                playSound(Sound.Winner)
                return
            }
        }

        // check left to right diagonal
        for(i in 0 until 2){
            val row = i
            val col = i
            if (_boxState[row * 3 + col] == BoxState.empty && _boxState[(row+1) * 3 + (col + 1)] == BoxState.empty) {
                frontdiagonalgame = false
                break
            }
            if (_boxState[row * 3 + col] != _boxState[(row+1) * 3 + (col + 1)]) {
                frontdiagonalgame = false
                break
            }
        }
        Log.d("LogVal", "frontDiagonalEnd")
        if (frontdiagonalgame){
            gameOver = true
            var highLight1: Int = 0+1
            var highLight2: Int = 4+1
            var highLight3: Int = 8+1
            _backColor[0] = Color.Green
            _backColor[4] = Color.Green
            _backColor[8] = Color.Green
            winner = if (_prevUser == User.user1) "Player 1" else "Player 2"
            sendUiEvent(UiEvent.ShowMessage("$winner won."))
            playSound(Sound.Winner)
            return
        }

        // check right to left diagonal
        var t = 0
        for(i in 2 downTo 1){
            val row = i
            val col = 2 - i
            if (_boxState[row * 3 + col] == BoxState.empty && _boxState[(row-1) * 3 + (col + 1)] == BoxState.empty) {
                backdiagonalgame = false
                break
            }
            if (_boxState[row * 3 + col] != _boxState[(row-1) * 3 + (col + 1)]) {
                backdiagonalgame = false
                break
            }
        }
        Log.d("LogVal", "backDiagonalEnd")
        if (backdiagonalgame){
            gameOver = true
            var highLight1: Int = 2+1
            var highLight2: Int = 4+1
            var highLight3: Int = 6+1
            _backColor[2] = Color.Green
            _backColor[4] = Color.Green
            _backColor[6] = Color.Green
            winner = if (_prevUser == User.user1) "Player 1" else "Player 2"
            sendUiEvent(UiEvent.ShowMessage("$winner won."))
            playSound(Sound.Winner)
            return
        }

        // check if all full
        for(i in 0 until _boxState.size){
            if (_boxState[i] == BoxState.empty){
                fullBox = false
            }
        }

        if (fullBox){
            sendUiEvent(UiEvent.ShowMessage("Game draw. Restart"))
        }
    }

    fun onEvent(event: UiEvent){
        when(event){
            is UiEvent.ShowMessage -> {

            }
            is UiEvent.onBoxClicked -> {
                if (!gameOver) {
                    val index = event.row * 3 + event.col
                    if (_boxState[index] == BoxState.empty) {
                        _elements[index] = true
                        _boxState[index] =
                            if (_user == User.user1) BoxState.circle else BoxState.cross
                        _prevUser = _user
                        //sendUiEvent(UiEvent.backgroundSound(Sound.Click))
                        playSound(Sound.Click)
                        nextUser()
                    } else {
                        //sendUiEvent(UiEvent.backgroundSound(Sound.Error))
                        playSound(Sound.Error)
                    }
                } else {
                    //sendUiEvent(UiEvent.backgroundSound(Sound.Error))
                    playSound(Sound.Error)
                }
            }

            UiEvent.onReset -> {
                gameOver = false
                _user = User.user1
                for( index in 0 until _elements.size){
                    _elements[index] = false
                    _boxState[index] = BoxState.empty
                    _backColor[index] = primaryColor
                }
            }
            UiEvent.onResetCount -> {
                //player1Cnt = 0
                //player2Cnt = 0
            }
            else -> {}
        }
    }

    private fun sendUiEvent(uiEvent: UiEvent){
        viewModelScope.launch {
            _uiEvent.send(uiEvent)
        }
    }

    /*fun onBoxClicked(row: Int, col: Int){
        val index = row * 3 + col
        if (_boxState[index] == BoxState.empty) {
            _elements[index] = true
            _boxState[index] = if (_user == User.user1) BoxState.circle else BoxState.cross
            Log.d("LogVal","index: $index box:${_boxState[index]}")
            onPressed()
        }
    }

    fun onReset(){

    }*/
}