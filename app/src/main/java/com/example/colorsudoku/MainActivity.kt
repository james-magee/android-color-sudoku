package com.example.colorsudoku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.colorsudoku.ui.theme.ColorSudokuTheme
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.colorsudoku.ui.theme.ViridisLight1
import com.example.colorsudoku.ui.theme.ViridisLight2
import com.example.colorsudoku.ui.theme.ViridisLight3
import com.example.colorsudoku.ui.theme.ViridisLight4
import com.example.colorsudoku.ui.theme.ViridisMedium
import com.example.colorsudoku.ui.theme.ViridisDark1
import com.example.colorsudoku.ui.theme.ViridisDark2
import com.example.colorsudoku.ui.theme.ViridisDark3
import com.example.colorsudoku.ui.theme.ViridisDark4
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ColorSudokuTheme {
                App()
            }
        }
    }
}

@Composable
fun App(
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Board()
    }
}


val colors = listOf(
    Color.White,
    ViridisLight1,
    ViridisLight2,
    ViridisLight3,
    ViridisLight4,
    ViridisMedium,
    ViridisDark1,
    ViridisDark2,
    ViridisDark3,
    ViridisDark4
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Board() {
    val initState = Array(81) { 0 }
    val state = remember { mutableStateListOf(*initState) }
    // state for BottomSheet
    val scope = rememberCoroutineScope()
    var openBottomSheet by remember { mutableStateOf(false) }
    val skipPartiallyExpanded by remember { mutableStateOf(false) }
    val edgeToEdgeEnabled by remember { mutableStateOf(true) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = skipPartiallyExpanded)
    var selectingColorForIndex by remember { mutableStateOf<Int?>(null) }

    // determining width of small grid squares
    val configuration = LocalConfiguration.current
    val horizontalPadding = 10
    val sqWidth = (configuration.screenWidthDp - horizontalPadding) / 9

    fun handleClick(row: Int, col: Int) {
        selectingColorForIndex = row * 9 + col
        openBottomSheet = true
    }

    fun computeColor(row: Int, col: Int): Color {
        val index = row  * 9 + col
        return colors.get(state.get(index))
    }

    Column (
        modifier = Modifier.fillMaxSize().background(color = MaterialTheme.colorScheme.secondaryContainer),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding((horizontalPadding / 2).dp),
        ) {
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Color",
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Light,
                    )
                    Text(
                        text = "Sudoku",
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Light,
                    )
                }
                Text(
                    text = "v0.1",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier.padding(end = 25.dp, bottom = 5.dp, start = 70.dp)
                )
            }
            Text(
                modifier = Modifier.padding(top = 15.dp, start = 2.dp, bottom = 15.dp),
                text = "The goal is to fill the squares such that\neach row and column has the entire\ngradient shown below:"
            )
            Row {
                for (i in 1..9) {
                    Box(modifier = Modifier
                        .padding(horizontal = 2.dp)
                        .background(color = colors.get(i), shape = RoundedCornerShape(3.dp))
                        .width((sqWidth / 2).dp)
                        .height((sqWidth / 2).dp)
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .padding((horizontalPadding / 2).dp)
        ) {
            for (i in 0 until 9) {
                Row {
                    for (j in 0 until 9) {
                        Box(
                            modifier = Modifier
                                .clickable { handleClick(i, j) }
                                .width(sqWidth.dp)
                                .height(sqWidth.dp)
                                .padding(2.dp)
                                .background(color = computeColor(i, j), shape = RoundedCornerShape(5.dp))
                        )
                    }
                }
            }
        }
    }

    fun handleColorSelect(i: Int) {
        if (selectingColorForIndex == null) {
            println("selectingColorForIndex shouldn't be null...")
            return
        }
        state.set(selectingColorForIndex!!, i)
        scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
            selectingColorForIndex = null
            openBottomSheet = false
        }
    }

    if (openBottomSheet) {
        val windowInsets = if (edgeToEdgeEnabled) WindowInsets(0) else BottomSheetDefaults.windowInsets
        AnimatedVisibility(
            visible = openBottomSheet
        ) {
            ModalBottomSheet(
                onDismissRequest = { openBottomSheet = !openBottomSheet },
                sheetState = bottomSheetState,
                windowInsets = windowInsets,
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .padding(top = 25.dp),
                    horizontalArrangement = Arrangement.Center) {
                    for (i in 1..9) {
                        Box(
                            modifier = Modifier
                                .padding(7.5.dp)
                                .width(25.dp)
                                .height(25.dp)
                                .background(
                                    shape = CircleShape,
                                    color = colors.get(i)
                                )
                                .clickable { handleColorSelect(i) }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyAppPreview() {
    ColorSudokuTheme {
        App()
    }
}

