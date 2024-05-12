package com.mine.bicycle.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mine.bicycle.annotation.ClockAnnotation
import com.mine.bicycle.net.IClockService
import com.mine.bicycle.net.NetManager
import com.mine.bicycle.repository.model.clock.ClockIn
import com.mine.bicycle.repository.model.clock.ClockOut
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(): ViewModel() {

    @Inject lateinit var mNetManager:NetManager


    private val _text = MutableLiveData<String>().apply {
        value = ""
    }
    val text: LiveData<String> = _text

    fun clockOut() {

        viewModelScope.launch {
            val datetime = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now())
            val form = ClockOut(tmv = datetime)
            try {

                val clockInResult = mNetManager.clockService?.clockOut(fields = form.toFields())

                clockInResult?.body()?.apply {

                    _text.postValue(
                        error.ifBlank {
                            info
                        })
                }
            } catch (e:Exception) {
                e.printStackTrace()
                _text.postValue(e.message)
            }


        }
    }

    fun clockIn() {
        viewModelScope.launch {
            val datetime = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now())
            val form = ClockIn(tmv = datetime)
            try {

                val clockInResult = mNetManager.clockService?.clockIn(fields = form.toFields())

                clockInResult?.body()?.apply {
                    _text.postValue(
                        error.ifBlank {
                            info
                        })
                }
            } catch (e:Exception) {
                e.printStackTrace()
                _text.postValue(e.message)
            }


        }
    }
}