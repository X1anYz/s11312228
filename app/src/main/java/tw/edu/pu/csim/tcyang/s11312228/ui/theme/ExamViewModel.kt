package tw.edu.pu.csim.tcyang.s11312228.ui.theme

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.delay
import kotlin.random.Random

// --- 答案和訊息定義 (常數) ---

// 定義角色索引與名稱
val roleNames = arrayOf("嬰幼兒", "兒童", "成人", "一般民眾")

// 定義服務圖示的答案 (索引 0-3 對應 service0-service3)
// 值為正確的角色索引 (0-3)
val serviceAnswers = mapOf(
    0 to 0, // service0 (極早期療育) -> 嬰幼兒
    1 to 1, // service1 (離島服務) -> 兒童
    2 to 2, // service2 (極重多障) -> 成人
    3 to 3  // service3 (輔具服務) -> 一般民眾
)

// 定義服務圖示的詳細訊息
val serviceMessages = mapOf(
    0 to "極早期療育",
    1 to "離島服務",
    2 to "極重多障",
    3 to "輔具服務"
)

/**
 * 負責儲存 App 狀態和邏輯的 ViewModel。
 */
class ExamViewModel(
    initialWidthPx: Int,
    initialHeightPx: Int
) : ViewModel() {

    // 螢幕尺寸狀態
    private val _screenWidthPx = mutableStateOf(initialWidthPx)
    val screenWidthPx: State<Int> = _screenWidthPx

    private val _screenHeightPx = mutableStateOf(initialHeightPx)
    val screenHeightPx: State<Int> = _screenHeightPx

    // 遊戲狀態
    private val _score = mutableIntStateOf(0)
    val score: State<Int> = _score

    private val _isPaused = mutableStateOf(true) // 初始為暫停狀態
    val isPaused: State<Boolean> = _isPaused

    // 當前服務圖示的索引 (0-3)
    private val _currentServiceIndex = mutableIntStateOf(0)
    val currentServiceIndex: State<Int> = _currentServiceIndex

    // 顯示在螢幕上的狀態訊息
    private val _statusMessage = mutableStateOf("目前狀態: 等待開始")
    val statusMessage: State<String> = _statusMessage

    // Toast 訊息和事件觸發
    private val _toastMessage = mutableStateOf("")
    val toastMessage: State<String> = _toastMessage

    private val _toastEvent = mutableStateOf(false)
    val toastEvent: State<Boolean> = _toastEvent

    // --- 遊戲控制邏輯 ---

    fun startGame() {
        if (_isPaused.value) {
            _isPaused.value = false
            _statusMessage.value = "目前狀態: 遊戲開始"
            generateNewServiceIcon()
        }
    }

    /**
     * 生成新的服務圖示 (隨機選擇 service0 到 service3)
     */
    fun generateNewServiceIcon() {
        _currentServiceIndex.value = Random.nextInt(serviceAnswers.size)
    }

    /**
     * 處理碰撞事件，包括計分、訊息更新和暫停。
     * @param roleIndex 碰撞到的角色索引；若為底部碰撞則為 null
     * @param isBoundaryCollision 是否為掉到最下方 (邊界碰撞)
     */
    suspend fun processCollision(roleIndex: Int?, isBoundaryCollision: Boolean) {
        // 1. 立即暫停遊戲，阻止圖示繼續下落/拖曳
        _isPaused.value = true

        if (isBoundaryCollision) {
            // 掉到最下方
            _statusMessage.value = "目前狀態: (掉到最下方)"
            _toastMessage.value = "服務圖示掉到最下方，不計分。"
        } else if (roleIndex != null) {
            // 碰撞到角色圖示
            val currentService = _currentServiceIndex.value
            val correctRoleIndex = serviceAnswers[currentService]
            val serviceName = serviceMessages[currentService] ?: "未知服務"
            val targetRole = roleNames[roleIndex]

            if (roleIndex == correctRoleIndex) {
                // 正確配對
                _score.intValue += 1
                _statusMessage.value = "目前狀態: +1 分 (碰撞${targetRole}圖示)"
                _toastMessage.value = "✅ 正確! $serviceName 屬於 $targetRole。"
            } else {
                // 錯誤配對
                _score.intValue -= 1
                val correctRole = roleNames[correctRoleIndex ?: 0]
                _statusMessage.value = "目前狀態: -1 分 (碰撞${targetRole}圖示)"
                _toastMessage.value = "❌ 錯誤! $serviceName 應屬於 $correctRole。"
            }
        }

        // 2. 觸發 Toast
        _toastEvent.value = true

        // 3. 暫停 3 秒 (3000 毫秒)
        delay(3000L)

        // 4. 重置狀態並出下一題
        _toastEvent.value = false // 重置事件
        _statusMessage.value = "目前狀態: 等待出題..."
        generateNewServiceIcon()
        _isPaused.value = false // 恢復遊戲
    }

    /**
     * Composable 呼叫此函式表示 Toast 已顯示
     */
    fun toastShown() {
        _toastEvent.value = false
    }

    /**
     * 自定義 ViewModel Factory
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