package tw.edu.pu.csim.tcyang.s11312228

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import tw.edu.pu.csim.tcyang.s11312228.ui.theme.ExamViewModel

@Composable
fun ExamScreen(viewModel: ExamViewModel = viewModel()) {
    // 從 ViewModel 取得螢幕尺寸
    val widthPx = viewModel.screenWidthPx.value
    val heightPx = viewModel.screenHeightPx.value

    // 使用 Box 設置黃色背景，並將內容置中
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF000)), // 黃色背景
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 1. App 圖示 (happy.png) - 引用自 res/drawable/happy
            Image(
                painter = painterResource(id = R.drawable.happy),
                contentDescription = "App Icon",
                modifier = Modifier.size(120.dp) // 設定圖片大小
            )

            // 間距高度 10dp
            Spacer(modifier = Modifier.height(10.dp))

            // 2. 標題文字
            Text(
                text = "瑪麗亞基金會服務大考驗",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color.Black
            )

            // 間距高度 10dp
            Spacer(modifier = Modifier.height(10.dp))

            // 3. 作者文字
            Text(
                text = "作者:資管二A張佑先",
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                color = Color.Black
            )

            // 間距高度 10dp
            Spacer(modifier = Modifier.height(10.dp))

            // 4. 顯示讀取到的螢幕尺寸 (for 驗證)
            Text(
                text = "螢幕尺寸 (像素)：\n寬度: $widthPx px\n高度: $heightPx px",
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = Color.DarkGray
            )
        }
    }
}