package tw.edu.pu.csim.tcyang.s11312228

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import tw.edu.pu.csim.tcyang.s11312228.ui.theme.ExamViewModel
import tw.edu.pu.csim.tcyang.s11312228.ui.theme.S11312228Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 【步驟 1】強制螢幕為直式 (Portrait)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // 【步驟 2】隱藏上方狀態列及下方巡覽列 (全螢幕模式)
        // 取得視窗控制器，並隱藏系統列
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        insetsController.hide(WindowInsetsCompat.Type.systemBars())

        // 【步驟 3】讀取螢幕寬度與高度 (像素 px)
        val displayMetrics = resources.displayMetrics
        val screenWidthPx = displayMetrics.widthPixels
        val screenHeightPx = displayMetrics.heightPixels

        // 建立 ViewModel Factory，將螢幕尺寸傳入
        val examViewModelFactory = ExamViewModel.Factory(screenWidthPx, screenHeightPx)

        setContent {
            // 使用 Factory 初始化 ViewModel
            val viewModel: ExamViewModel = viewModel(factory = examViewModelFactory)

            S11312228Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // 載入我們的主畫面 ExamScreen
                    ExamScreen(viewModel = viewModel)
                }
            }
        }
    }
}