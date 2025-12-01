package tw.edu.pu.csim.tcyang.s11312228

import android.content.pm.ActivityInfo // 必須匯入這個才能設定螢幕方向
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import tw.edu.pu.csim.tcyang.s11312228.ui.theme.S11312228Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 【設定 1】強制螢幕為直式 (Portrait)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // 【設定 2】隱藏狀態列及下方巡覽列 (全螢幕模式)
        // 取得視窗控制器
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)

        // 設定行為：當使用者從邊緣滑動時，暫時顯示系統列，幾秒後自動隱藏
        insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        // 隱藏系統列 (包含狀態列與導覽列)
        insetsController.hide(WindowInsetsCompat.Type.systemBars())

        // 【設定 3】設定畫面內容 (Compose 入口)
        setContent {
            S11312228Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // 這裡之後可以加入您的畫面程式碼
                    // 例如：GameScreen()
                }
            }
        }
    }
}