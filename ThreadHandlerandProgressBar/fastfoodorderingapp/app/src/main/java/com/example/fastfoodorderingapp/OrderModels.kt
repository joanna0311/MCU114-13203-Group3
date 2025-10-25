// OrderViewModel.kt
package com.example.fastfoodorderingapp

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update// 定義一個數據類來封裝訂單狀態
data class OrderUiState(
    val mainMeal: String? = null,
    val sideDish: String? = null,
    val drink: String? = null
)

class OrderViewModel : ViewModel() {

    // 使用 StateFlow 來保存 UI 狀態，私有的 _uiState 可供內部修改
    private val _uiState = MutableStateFlow(OrderUiState())
    // 公開的 uiState 是唯讀的，供 Activity 觀察
    val uiState = _uiState.asStateFlow()

    // 更新主餐選項
    fun setMainMeal(meal: String?) {
        _uiState.update { currentState ->
            currentState.copy(mainMeal = meal)
        }
    }

    // 更新副餐選項
    fun setSideDish(dish: String?) {
        _uiState.update { currentState ->
            currentState.copy(sideDish = dish)
        }
    }

    // 更新飲料選項
    fun setDrink(drink: String?) {
        _uiState.update { currentState ->
            currentState.copy(drink = drink)
        }
    }

    // 檢查是否所有選項都已選擇
    fun isOrderComplete(): Boolean {
        val currentState = _uiState.value
        return currentState.mainMeal != null &&
                currentState.sideDish != null &&
                currentState.drink != null
    }

    // 重置所有選項
    fun resetOrder() {
        _uiState.value = OrderUiState()
    }
}
