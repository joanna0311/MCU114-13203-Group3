package com.example.threadhandlerandprogressbar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*

class WorkViewModel : ViewModel() {

    // 使用 sealed class 來定義更嚴謹的狀態
    sealed class WorkStatus {
        object Idle : WorkStatus() // 閒置
        object Preparing : WorkStatus() // 準備中
        data class InProgress(val progress: Int) : WorkStatus() // 進行中
        object WorkFinished : WorkStatus() // 工作結束
        object Cancelled : WorkStatus() // 已取消
    }

    private val _progressStatus = MutableLiveData<WorkStatus>(WorkStatus.Idle)
    val progressStatus: LiveData<WorkStatus> = _progressStatus

    private val _resultStatus = MutableLiveData<String>("Idle")
    val resultStatus: LiveData<String> = _resultStatus

    private var job: Job? = null

    fun startWork() {
        // 如果工作正在進行，就不要再啟動
        if (job?.isActive == true) return

        job = viewModelScope.launch {
            // 1. 準備階段
            _progressStatus.value = WorkStatus.Preparing
            _resultStatus.value = "Preparing..."
            delay(2000) // 模擬準備工作

            // 2. 執行階段
            _resultStatus.value = "Working..."
            for (i in 1..100) {
                ensureActive() // 檢查 Coroutine 是否被取消
                _progressStatus.value = WorkStatus.InProgress(i)
                delay(50) // 模擬工作進度
            }

            // 3. 完成階段
            _progressStatus.value = WorkStatus.WorkFinished
            _resultStatus.value = "背景工作結束!"
        }
    }

    fun cancelWork() {
        // 如果沒有工作或工作已結束，則不執行任何操作
        if (job == null || job?.isCompleted == true || job?.isCancelled == true) return

        job?.cancel()
        _progressStatus.value = WorkStatus.Cancelled
        _resultStatus.value = "Canceled"
    }
}
