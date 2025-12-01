package tw.edu.pu.csim.tcyang.s11312228.ui.theme

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * 負責儲存 App 狀態和邏輯的 ViewModel。
 * 初始傳入螢幕的寬度和高度 (像素, px)。
 */
class ExamViewModel(
    initialWidthPx: Int,
    initialHeightPx: Int
) : ViewModel() {

    // 螢幕寬度 (像素, px) - 使用 State 讓 Composable 能夠響應式更新
    private val _screenWidthPx = mutableStateOf(initialWidthPx)
    val screenWidthPx: State<Int> = _screenWidthPx

    // 螢幕高度 (像素, px)
    private val _screenHeightPx = mutableStateOf(initialHeightPx)
    val screenHeightPx: State<Int> = _screenHeightPx

    /**
     * 自定義 ViewModel Factory，用於在初始化時傳入螢幕尺寸參數。
     */
    class Factory(private val initialWidthPx: Int, private val initialHeightPx: Int) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ExamViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ExamViewModel(initialWidthPx, initialHeightPx) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}