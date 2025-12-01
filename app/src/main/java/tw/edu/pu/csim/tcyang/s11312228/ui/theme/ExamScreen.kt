package tw.edu.pu.csim.tcyang.s11312228

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tw.edu.pu.csim.tcyang.s11312228.ui.theme.ExamViewModel
import tw.edu.pu.csim.tcyang.s11312228.ui.theme.roleNames

// 服務圖示資源 ID 陣列
val serviceIcons = arrayOf(
    R.drawable.service0,
    R.drawable.service1,
    R.drawable.service2,
    R.drawable.service3
)

@Composable
fun ExamScreen(viewModel: ExamViewModel = viewModel()) {
    val density = LocalDensity.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // --- 【從 ViewModel 取得狀態】 ---
    val screenWidthPx = viewModel.screenWidthPx.value
    val screenHeightPx = viewModel.screenHeightPx.value
    val score = viewModel.score.value
    val statusMessage = viewModel.statusMessage.value
    val isPaused = viewModel.isPaused.value
    val currentServiceIndex = viewModel.currentServiceIndex.value
    val toastEvent = viewModel.toastEvent.value
    val toastMessage = viewModel.toastMessage.value

    // --- 【UI 尺寸定義】 ---
    val iconSizePx = 300 // 角色圖示尺寸
    val iconSize = with(density) { iconSizePx.toDp() }
    val halfScreenHeightPx = screenHeightPx / 2f
    val centerAppIconSizePx = 350
    val centerAppIconSize = with(density) { centerAppIconSizePx.toDp() }

    val fallingIconSizePx = 250f // 服務圖示尺寸 (250px)
    val fallingIconSizeDp = with(density) { fallingIconSizePx.toDp() }
    val dropSpeedPx = 20f

    // --- 【服務圖示的 UI 狀態 (位置)】 ---
    var serviceIconY by remember { mutableFloatStateOf(0f) }
    var serviceIconXOffset by remember { mutableFloatStateOf((screenWidthPx - fallingIconSizePx) / 2f) }
    val currentServiceIconResId = serviceIcons.getOrElse(currentServiceIndex) { R.drawable.service0 }

    // --- 【角色圖示的碰撞區域定義 (像素)】 ---
    val roleWidth = iconSizePx.toFloat()
    val roleHeight = iconSizePx.toFloat()
    val roles = remember(screenWidthPx, screenHeightPx) {
        if (screenWidthPx == 0 || screenHeightPx == 0) emptyList() else listOf(
            // role0 (嬰幼兒): 左上角
            Rect(left = 0f, top = halfScreenHeightPx - roleHeight, right = roleWidth, bottom = halfScreenHeightPx),
            // role1 (兒童): 右上角
            Rect(left = screenWidthPx - roleWidth, top = halfScreenHeightPx - roleHeight, right = screenWidthPx.toFloat(), bottom = halfScreenHeightPx),
            // role2 (成人): 左下角
            Rect(left = 0f, top = screenHeightPx - roleHeight, right = roleWidth, bottom = screenHeightPx.toFloat()),
            // role3 (一般民眾): 右下角
            Rect(left = screenWidthPx - roleWidth, top = screenHeightPx - roleHeight, right = screenWidthPx.toFloat(), bottom = screenHeightPx.toFloat())
        )
    }

    // --- 【碰撞檢測與處理邏輯】 (已修復 return@ 錯誤) ---
    val checkCollisionAndReset: (Float, Float) -> Boolean = { currentY, currentXOffset ->
        // 暫停時不檢查碰撞
        if (isPaused) {
            false
        } else {
            var collisionOccurred = false

            val fallingIconRect = Rect(
                left = currentXOffset,
                top = currentY,
                right = currentXOffset + fallingIconSizePx,
                bottom = currentY + fallingIconSizePx
            )

            // 1. 底部邊界碰撞檢測
            if (fallingIconRect.bottom >= screenHeightPx) {
                coroutineScope.launch {
                    viewModel.processCollision(roleIndex = null, isBoundaryCollision = true)
                }
                collisionOccurred = true
            }

            // 2. 角色圖示碰撞檢測
            if (!collisionOccurred) {
                roles.forEachIndexed { index, roleRect ->
                    if (fallingIconRect.overlaps(roleRect)) {
                        coroutineScope.launch {
                            viewModel.processCollision(roleIndex = index, isBoundaryCollision = false)
                        }
                        collisionOccurred = true
                        return@forEachIndexed
                    }
                }
            }
            // 返回碰撞狀態 (隱含返回)
            collisionOccurred
        }
    }

    // --- 【定時下落和暫停控制】 ---
    LaunchedEffect(isPaused, screenHeightPx) {
        if (screenHeightPx > 0 && isPaused) {
            viewModel.startGame()
        }

        while (screenHeightPx > 0) {
            delay(100)

            if (!isPaused) {
                // 遊戲進行中：執行下落
                val nextY = serviceIconY + dropSpeedPx
                val collisionOccurred = checkCollisionAndReset(nextY, serviceIconXOffset)

                if (!collisionOccurred) {
                    serviceIconY = nextY
                }
            } else {
                // 暫停中：將圖示位置重設到頂部中央
                serviceIconY = 0f
                serviceIconXOffset = (screenWidthPx - fallingIconSizePx) / 2f
            }
        }
    }

    // --- 【Toast 觸發效果】 ---
    LaunchedEffect(toastEvent) {
        if (toastEvent) {
            Toast.makeText(context, toastMessage, Toast.LENGTH_LONG).show()
            viewModel.toastShown()
        }
    }

    // --- 【水平拖曳邏輯】 ---
    val draggableState = rememberDraggableState(onDelta = { delta ->
        if (isPaused) return@rememberDraggableState // 暫停時不允許拖曳

        var newOffset = serviceIconXOffset + delta
        val maxOffset = screenWidthPx - fallingIconSizePx

        // 邊界檢查
        if (newOffset < 0f) {
            newOffset = 0f
        } else if (newOffset > maxOffset) {
            newOffset = maxOffset
        }

        serviceIconXOffset = newOffset
        checkCollisionAndReset(serviceIconY, serviceIconXOffset)
    })

    // 將像素值轉換為 Dp 單位
    val serviceIconYDp: Dp = with(density) { serviceIconY.toDp() }
    val serviceIconXOffsetDp: Dp = with(density) { serviceIconXOffset.toDp() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF000))
    ) {
        // --- 【中央資訊與計分 UI (底層)】 ---
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(painter = painterResource(id = R.drawable.happy), contentDescription = "App Icon", modifier = Modifier.size(centerAppIconSize))
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = "瑪麗亞基金會服務大考驗", fontSize = 15.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, color = Color.Black)
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = "作者:資管二A張佑先", fontSize = 12.sp, textAlign = TextAlign.Center, color = Color.Black)
            Spacer(modifier = Modifier.height(10.dp))

            Text(text = "分數：$score 分", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.Blue, textAlign = TextAlign.Center, modifier = Modifier.padding(bottom = 10.dp))

            Text(text = "螢幕寬度 (Pixel/px)：\n$screenWidthPx px", fontSize = 12.sp, color = Color.Black, textAlign = TextAlign.Center, modifier = Modifier.padding(10.dp))
            Text(text = "螢幕高度 (Pixel/px)：\n$screenHeightPx px", fontSize = 12.sp, color = Color.Black, textAlign = TextAlign.Center, modifier = Modifier.padding(10.dp))

            Spacer(modifier = Modifier.height(10.dp))
            Text(text = statusMessage, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Red, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 10.dp))
        }

        // --- 【四個角色圖示 (中間層)】 ---
        Image(painter = painterResource(id = R.drawable.role0), contentDescription = "嬰幼兒", modifier = Modifier.align(Alignment.TopStart).size(iconSize).offset(y = with(density) { (halfScreenHeightPx - iconSizePx).toDp() }), contentScale = ContentScale.Fit)
        Image(painter = painterResource(id = R.drawable.role1), contentDescription = "兒童", modifier = Modifier.align(Alignment.TopEnd).size(iconSize).offset(y = with(density) { (halfScreenHeightPx - iconSizePx).toDp() }), contentScale = ContentScale.Fit)
        Image(painter = painterResource(id = R.drawable.role2), contentDescription = "成人", modifier = Modifier.align(Alignment.BottomStart).size(iconSize), contentScale = ContentScale.Fit)
        Image(painter = painterResource(id = R.drawable.role3), contentDescription = "一般民眾", modifier = Modifier.align(Alignment.BottomEnd).size(iconSize), contentScale = ContentScale.Fit)

        // --- 【服務圖示 (最上層) - 碰撞後消失/重設邏輯】 ---
        // 只有在非暫停狀態 (!isPaused) 時才繪製圖示
        if (!isPaused) {
            Image(
                painter = painterResource(id = currentServiceIconResId),
                contentDescription = "Falling Service Icon",
                modifier = Modifier
                    .size(fallingIconSizeDp)
                    .offset(x = serviceIconXOffsetDp, y = serviceIconYDp)
                    .draggable(
                        state = draggableState,
                        orientation = Orientation.Horizontal
                    )
                    .align(Alignment.TopStart)
            )
        }
    }
}