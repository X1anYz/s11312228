package tw.edu.pu.csim.tcyang.s11312228

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import tw.edu.pu.csim.tcyang.s11312228.ui.theme.ExamViewModel

@Composable
fun ExamScreen(viewModel: ExamViewModel = viewModel()) {
    val context = LocalContext.current
    val density = LocalDensity.current

    val screenWidthPx = viewModel.screenWidthPx.value
    val screenHeightPx = viewModel.screenHeightPx.value

    val iconSizePx = 300 // 300 像素
    val centerIconSizePx = 350

    val iconSize = with(density) { iconSizePx.toDp() }
    val centerIconSize = with(density) { centerIconSizePx.toDp() }

    val halfScreenHeightDp = with(density) { (screenHeightPx / 2).toDp() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF000)) // 黃色背景
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App 圖示 (happy.png)
            Image(
                painter = painterResource(id = R.drawable.happy),
                contentDescription = "App Icon",
                modifier = Modifier.size(centerIconSize) // 使用加大的中央圖示尺寸
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

        // 四個角色圖示的佈局保持不變
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
    }
}