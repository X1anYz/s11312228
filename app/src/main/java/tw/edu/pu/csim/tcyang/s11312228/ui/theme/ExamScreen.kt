package tw.edu.pu.csim.tcyang.s11312228

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import tw.edu.pu.csim.tcyang.s11312228.ui.theme.ExamViewModel
import kotlin.random.Random

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

    // 螢幕寬高 (像素)
    val screenWidthPx = viewModel.screenWidthPx.value
    val screenHeightPx = viewModel.screenHeightPx.value

    // 角色圖示尺寸
    val iconSizePx = 300 // 300 像素
    val centerIconSizePx = 350
    val iconSize = with(density) { iconSizePx.toDp() }
    val centerIconSize = with(density) { centerIconSizePx.toDp() }
    val halfScreenHeightDp = with(density) { (screenHeightPx / 2).toDp() }

    // --- 【服務圖示的狀態管理】 ---
    // 服務圖示的垂直位置 (Y 座標, 像素)
    var serviceIconY by remember { mutableFloatStateOf(0f) }
    // 服務圖示的水平偏移 (X 座標, 像素)。初始設定在水平中間
    var serviceIconXOffset by remember { mutableFloatStateOf((screenWidthPx - centerIconSizePx) / 2f) }
    // 當前顯示的服務圖示資源 ID
    var currentServiceIconResId by remember { mutableIntStateOf(serviceIcons.random()) }

    // 服務圖示的尺寸 (像素)，假設與中央圖示尺寸相同
    val fallingIconSizePx = centerIconSizePx.toFloat()
    val fallingIconSizeDp = centerIconSize

    // 每次下落的像素值
    val dropSpeedPx = 20f // 每 0.1 秒下落 20 像素

    // --- 【定時下落和碰撞檢測】 ---
    LaunchedEffect(Unit) {
        // 確保 screenHeightPx 已經取得
        while (screenHeightPx == 0) {
            delay(10)
        }

        while (true) {
            delay(100) // 每 0.1 秒

            // 1. 更新垂直位置 (往下掉)
            serviceIconY += dropSpeedPx

            // 2. 碰撞檢測 (到達螢幕底部)
            // 碰撞條件：圖示的底部 (serviceIconY + fallingIconSizePx) 大於或等於螢幕高度 (screenHeightPx)
            if (serviceIconY + fallingIconSizePx >= screenHeightPx) {
                // 重置：
                // a. 重設垂直位置到螢幕上方 (Y=0)
                serviceIconY = 0f
                // b. 隨機選擇新的圖示
                currentServiceIconResId = serviceIcons.random()
            }
        }
    }

    // --- 【水平拖曳邏輯】 ---
    val draggableState = rememberDraggableState(onDelta = { delta ->
        // delta 是水平移動的像素值
        var newOffset = serviceIconXOffset + delta

        // 邊界檢查 (確保圖示不會完全拖出螢幕範圍)
        // 最小 X 偏移：0
        // 最大 X 偏移：screenWidthPx - fallingIconSizePx
        val maxOffset = screenWidthPx - fallingIconSizePx

        if (newOffset < 0f) {
            newOffset = 0f
        } else if (newOffset > maxOffset) {
            newOffset = maxOffset
        }

        serviceIconXOffset = newOffset
    })

    // 將像素值轉換為 Dp 單位
    val serviceIconYDp: Dp = with(density) { serviceIconY.toDp() }
    val serviceIconXOffsetDp: Dp = with(density) { serviceIconXOffset.toDp() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF000)) // 黃色背景
    ) {
        // --- 【中央資訊與其他靜態 UI (底層)】 ---
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App 圖示 (happy.png)
            Image(
                painter = painterResource(id = R.drawable.happy),
                contentDescription = "App Icon",
                modifier = Modifier.size(centerIconSize)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 標題文字
            Text(
                text = "瑪麗亞基金會服務大考驗",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 作者文字
            Text(
                text = "作者:資管二A張佑先",
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(10.dp))

            // 顯示螢幕寬度 (像素)
            Text(
                text = "螢幕寬度 (Pixel/px)：\n$screenWidthPx px",
                fontSize = 12.sp,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(10.dp)
            )

            // 顯示螢幕高度 (像素)
            Text(
                text = "螢幕高度 (Pixel/px)：\n$screenHeightPx px",
                fontSize = 12.sp,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(10.dp)
            )
        }

        // 四個角色圖示 (中間層)
        Image(
            painter = painterResource(id = R.drawable.role0),
            contentDescription = "嬰幼兒",
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(iconSize)
                .offset(y = halfScreenHeightDp - iconSize),
            contentScale = ContentScale.Fit
        )

        Image(
            painter = painterResource(id = R.drawable.role1),
            contentDescription = "兒童",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(iconSize)
                .offset(y = halfScreenHeightDp - iconSize),
            contentScale = ContentScale.Fit
        )

        Image(
            painter = painterResource(id = R.drawable.role2),
            contentDescription = "成人",
            modifier = Modifier
                .align(Alignment.BottomStart)
                .size(iconSize),
            contentScale = ContentScale.Fit
        )

        Image(
            painter = painterResource(id = R.drawable.role3),
            contentDescription = "一般民眾",
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(iconSize),
            contentScale = ContentScale.Fit
        )

        // --- 【服務圖示 (最上層)】 ---
        Image(
            painter = painterResource(id = currentServiceIconResId),
            contentDescription = "Falling Service Icon",
            modifier = Modifier
                .size(fallingIconSizeDp)
                .offset(x = serviceIconXOffsetDp, y = serviceIconYDp) // 使用 offset 定位
                // 使圖示可水平拖曳
                .draggable(
                    state = draggableState,
                    orientation = Orientation.Horizontal
                )
                .align(Alignment.TopStart) // 配合 offset 定位
        )
    }
}