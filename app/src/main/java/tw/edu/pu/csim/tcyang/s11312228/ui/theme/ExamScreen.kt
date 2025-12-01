package tw.edu.pu.csim.tcyang.s11312228

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.* // 確保導入所有必要的 runtime 元素
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
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

// 角色圖示名稱
val roleNames = arrayOf("嬰幼兒", "兒童", "成人", "一般民眾")

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
    val halfScreenHeightPx = screenHeightPx / 2f

    // 服務圖示的尺寸 (像素)
    val fallingIconSizePx = 250f // 250 像素
    val fallingIconSizeDp = with(density) { fallingIconSizePx.toDp() }

    // 每次下落的像素值
    val dropSpeedPx = 20f // 每 0.1 秒下落 20 像素

    // --- 【服務圖示的狀態管理】 ---
    var serviceIconY by remember { mutableFloatStateOf(0f) }
    var serviceIconXOffset by remember { mutableFloatStateOf((screenWidthPx - centerIconSizePx) / 2f) }
    var currentServiceIconResId by remember { mutableIntStateOf(serviceIcons.random()) }

    // 修正: 由於 viewModel.scoreMessage 是 State<String>，直接使用 .value
    val scoreMessage = viewModel.scoreMessage.value

    // --- 【角色圖示的碰撞區域定義 (像素)】 ---
    val roleWidth = iconSizePx.toFloat()
    val roleHeight = iconSizePx.toFloat()

    // 角色圖示的位置 (左上角座標)
    val roles = remember(screenWidthPx, screenHeightPx) {
        if (screenWidthPx == 0 || screenHeightPx == 0) emptyList() else listOf(
            // role0 (嬰幼兒): 左上角
            Rect(
                left = 0f,
                top = halfScreenHeightPx - roleHeight,
                right = roleWidth,
                bottom = halfScreenHeightPx
            ),
            // role1 (兒童): 右上角
            Rect(
                left = screenWidthPx - roleWidth,
                top = halfScreenHeightPx - roleHeight,
                right = screenWidthPx.toFloat(),
                bottom = halfScreenHeightPx
            ),
            // role2 (成人): 左下角
            Rect(
                left = 0f,
                top = screenHeightPx - roleHeight,
                right = roleWidth,
                bottom = screenHeightPx.toFloat()
            ),
            // role3 (一般民眾): 右下角
            Rect(
                left = screenWidthPx - roleWidth,
                top = screenHeightPx - roleHeight,
                right = screenWidthPx.toFloat(),
                bottom = screenHeightPx.toFloat()
            )
        )
    }

    // --- 【碰撞檢測與重置邏輯】 ---
    val checkCollisionAndReset: (Float, Float) -> Boolean = { currentY, currentXOffset ->
        var collisionOccurred = false // 使用區域變數來追蹤碰撞狀態

        // 1. 服務圖示的碰撞區域 (Rect)
        val fallingIconRect = Rect(
            left = currentXOffset,
            top = currentY,
            right = currentXOffset + fallingIconSizePx,
            bottom = currentY + fallingIconSizePx
        )

        // 2. 底部邊界碰撞檢測
        if (fallingIconRect.bottom >= screenHeightPx) {
            // 掉到最下方
            viewModel.updateScoreMessage("(掉到最下方)")
            collisionOccurred = true
        } else {
            // 3. 角色圖示碰撞檢測 (只有在沒有碰撞底部時才檢查，以避免雙重碰撞報告)
            roles.forEachIndexed { index, roleRect ->
                if (fallingIconRect.overlaps(roleRect)) {
                    // 發生碰撞
                    viewModel.updateScoreMessage("(碰撞${roleNames[index]}圖示)")
                    collisionOccurred = true
                    // 碰撞角色後，直接跳出 forEach 循環
                    return@forEachIndexed
                }
            }
        }

        // 如果發生碰撞，執行重置
        if (collisionOccurred) {
            serviceIconY = 0f
            currentServiceIconResId = serviceIcons.random()
        }

        // 修正: 在 lambda 結束時返回區域變數
        collisionOccurred
    }

    // --- 【定時下落】 ---
    LaunchedEffect(Unit) {
        while (screenHeightPx == 0) { delay(10) }

        while (true) {
            delay(100) // 每 0.1 秒

            // 1. 預測新位置
            val nextY = serviceIconY + dropSpeedPx

            // 2. 檢測碰撞和重置 (現在返回 true/false)
            val collisionOccurred = checkCollisionAndReset(nextY, serviceIconXOffset)

            // 3. 如果沒有碰撞，則更新位置
            if (!collisionOccurred) {
                serviceIconY = nextY
            }
        }
    }

    // --- 【水平拖曳邏輯】 ---
    val draggableState = rememberDraggableState(onDelta = { delta ->
        var newOffset = serviceIconXOffset + delta
        val maxOffset = screenWidthPx - fallingIconSizePx

        if (newOffset < 0f) {
            newOffset = 0f
        } else if (newOffset > maxOffset) {
            newOffset = maxOffset
        }

        // 更新水平位置
        serviceIconXOffset = newOffset

        // 拖曳時，檢查當前位置是否已發生碰撞 (邊緣情況)
        // 這裡只需要呼叫，checkCollisionAndReset 會在內部處理重置
        checkCollisionAndReset(serviceIconY, serviceIconXOffset)
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
            // ... (保持不變的 UI 元素)
            Image(
                painter = painterResource(id = R.drawable.happy),
                contentDescription = "App Icon",
                modifier = Modifier.size(centerIconSize)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "瑪麗亞基金會服務大考驗",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "作者:資管二A張佑先",
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "螢幕寬度 (Pixel/px)：\n$screenWidthPx px",
                fontSize = 12.sp,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(10.dp)
            )

            Text(
                text = "螢幕高度 (Pixel/px)：\n$screenHeightPx px",
                fontSize = 12.sp,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(10.dp)
            )

            // --- 【成績/訊息顯示 (修正 `scoreMessage` 存取)】 ---
            Spacer(modifier = Modifier.height(10.dp)) // 間隔 10.dp
            Text(
                text = scoreMessage, // 直接使用 value，不需要 collectAsState()
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Red,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 10.dp)
            )
        }

        // ... (四個角色圖示保持不變)
        Image(
            painter = painterResource(id = R.drawable.role0),
            contentDescription = "嬰幼兒",
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(iconSize)
                .offset(y = with(density) { (halfScreenHeightPx - iconSizePx).toDp() }),
            contentScale = ContentScale.Fit
        )

        Image(
            painter = painterResource(id = R.drawable.role1),
            contentDescription = "兒童",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(iconSize)
                .offset(y = with(density) { (halfScreenHeightPx - iconSizePx).toDp() }),
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
                .offset(x = serviceIconXOffsetDp, y = serviceIconYDp)
                .draggable(
                    state = draggableState,
                    orientation = Orientation.Horizontal
                )
                .align(Alignment.TopStart)
        )
    }
}