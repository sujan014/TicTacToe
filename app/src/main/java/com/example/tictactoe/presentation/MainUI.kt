package com.example.tictactoe.presentation

import android.content.Context
import android.media.MediaPlayer
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tictactoe.R
import com.example.tictactoe.presentation.state.BoxState
import com.example.tictactoe.presentation.state.Sound
import com.example.tictactoe.presentation.viewmodel.MainViewModel
import com.example.tictactoe.util.UiEvent
import kotlin.reflect.KFunction2

data class Square(
    val id: Int,
    val xOffset: Dp,
    val yOffset: Dp,
)

@Composable
fun MainUI(
    mainVM: MainViewModel,
    doWithContext: (Context) -> Unit
) {
    val context = LocalContext.current
    val user = mainVM._user
    var message by remember { mutableStateOf("Hello there") }

    var counter1 by remember { mutableStateOf(0) }
    var counter2 by remember { mutableStateOf(0) }

    var buttonPlay: MediaPlayer = MediaPlayer.create(context, R.raw.buttonclick)
    var errorPlay: MediaPlayer = MediaPlayer.create(context, R.raw.wrongclick)
    var winnerPlay: MediaPlayer = MediaPlayer.create(context, R.raw.winner)

    LaunchedEffect(key1 = true) {
        mainVM.uiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowMessage -> {
                    message = event.message
                    if (message.contains("Player 1 won")) counter1 = counter1.plus(1)
                    else if (message.contains("Player 2 won")) counter2 = counter2.plus(1)
                }
                /*is UiEvent.backgroundSound -> {
                    if (event.sound == Sound.Stop){
                        if (errorPlay.isPlaying){
                            errorPlay.stop()
                        }
                        if (buttonPlay.isPlaying){
                            buttonPlay.stop()
                        }
                        if (winnerPlay.isPlaying){
                            winnerPlay.stop()
                        }
                    } else if (event.sound == Sound.Error) {
                        if (buttonPlay.isPlaying){
                            buttonPlay.stop()
                        }
                        if (winnerPlay.isPlaying){
                            winnerPlay.stop()
                        }
                        errorPlay.start()
                    } else if (event.sound == Sound.Click){
                        if (errorPlay.isPlaying){
                            errorPlay.stop()
                        }
                        if (winnerPlay.isPlaying){
                            winnerPlay.stop()
                        }
                        buttonPlay.start()
                    } else if (event.sound == Sound.Winner){
                        if (errorPlay.isPlaying){
                            errorPlay.stop()
                        }
                        if (buttonPlay.isPlaying){
                            buttonPlay.stop()
                        }
                        winnerPlay.start()
                    }
                }*/
                else -> {
                    TODO()
                }
            }
        }
    }
    Column() {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Main Game UI
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                val configuration = LocalConfiguration.current

                val screenWidth: Dp = configuration.screenWidthDp.dp
                val screenHeight = configuration.screenHeightDp.dp
                val offsetAdjusted: Dp = screenWidth * 0.95f
                val squarelen: Dp = offsetAdjusted / 3

                val yofs: Dp = 30.dp
                val xofs: Dp = screenWidth * 0.025f
                //Log.d("DrawLog", "Screen Width: $screenWidth   Screen Height: $screenHeight")

                var va by remember { mutableStateOf(false) }
                Column() {
                    for (row in 0 until 3) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 1.dp, horizontal = 2.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            for (col in 0 until 3) {
                                val count = row * 3 + col
                                Box(modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .background(color = mainVM._backColor[count])//MaterialTheme.colorScheme.primary)
                                    .border(width = 1.dp, color = Color.Black)
                                    .clickable {
                                        doWithContext(context)
                                        mainVM.onEvent(UiEvent.onBoxClicked(row, col))
                                    }
                                ) {
                                    val index = row * 3 + col
                                    Text(text = "$row, $col")

                                    if (mainVM._boxState[index] == BoxState.circle) {
                                        Canvas(modifier = Modifier.fillMaxSize()) {
                                            val width: Float = size.width
                                            val height: Float = size.height
                                            val radius: Float = width / 2 * 0.7f
                                            drawCircle(
                                                Color.Black,
                                                radius = radius,
                                                style = Stroke(width = 10f),
                                                center = Offset(size.width / 2f, size.height / 2f)
                                            )
                                        }
                                        mainVM.onButtonClicked()
                                    } else if (mainVM._boxState[index] == BoxState.cross) {
                                        Canvas(modifier = Modifier.fillMaxSize()) {
                                            val width: Float = size.width
                                            val height: Float = size.height

                                            drawLine(
                                                start = Offset(x = 0f, y = 0f),
                                                end = Offset(x = width, y = height),
                                                strokeWidth = 5f,
                                                color = Color.Blue
                                            )
                                            drawLine(
                                                start = Offset(x = width, y = 0f),
                                                end = Offset(x = 0f, y = height),
                                                strokeWidth = 5f,
                                                color = Color.Blue
                                            )
                                        }
                                        mainVM.onButtonClicked()
                                    }
                                }
                                if (col != 2) {
                                    Spacer(modifier = Modifier.width(2.dp))
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(50.dp))
            Divider(
                color = Color.DarkGray,
                modifier = Modifier
                    .fillMaxWidth()
                    .width(5.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(color = Color.Red, shape = RoundedCornerShape(20.dp))
                        .padding(vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Player 1",
                        color = Color.White,
                        fontSize = 24.sp,
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(color = Color.Green, shape = RoundedCornerShape(20.dp))
                        .padding(vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Player 2",
                        color = Color.Black,
                        fontSize = 24.sp,
                    )
                }
            }
            Divider(
                color = Color.DarkGray,
                modifier = Modifier
                    .fillMaxWidth()
                    .width(5.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = message,
                color = Color.Blue,
                style = TextStyle(
                    fontSize = 24.sp,
                    shadow = Shadow(
                        color = Color.Blue,
                        offset = Offset.Zero,
                        blurRadius = 3f
                    )
                ),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(10.dp))
            // win counter header
            /*Row(
                modifier = Modifier
                    .height(IntrinsicSize.Min)
                    .border(width = 1.dp, color = Color.Black, shape = RectangleShape),
                horizontalArrangement = Arrangement.Center
            ){
                Text(
                    text = "Player 1",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Divider(modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp),
                    color = Color.Black
                )
                Text(
                    text = "Player 2",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
            // win count
            Row(
                modifier = Modifier
                    .height(IntrinsicSize.Min)
                    .border(width = 1.dp, color = Color.Black, shape = RectangleShape),
                horizontalArrangement = Arrangement.Center
            ){
                Text(
                    text = "${counter1}",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Divider(modifier = Modifier
                    .fillMaxHeight()
                    .width(1.dp),
                    color = Color.Black
                )
                Text(
                    text = "${counter2}",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }*/
        }

        Button(
            modifier = Modifier
                .fillMaxWidth(),
            onClick = {
                mainVM.onEvent(UiEvent.onReset)
                message = ""
            }
        ) {
            Text(
                text = "New game", fontSize = 20.sp, fontStyle = FontStyle.Italic
            )
        }
    }
}

/*
@Composable
@Preview(showBackground = true)
fun DefaultPreview() {
    TicTacToeTheme {
        // A surface container using the 'background' color from the theme
        val mainVM: MainViewModel = viewModel()
        MainUI(mainVM)
    }
}*/

