package com.mine.bicycle.ui.dashboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mine.bicycle.annotation.BicycleAnnotation
import com.mine.bicycle.net.IBicycleServices
import com.mine.bicycle.net.NetManager
import com.mine.bicycle.repository.model.bicycle.BicycleInStation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BicycleViewModel @Inject constructor(): ViewModel() {

    @Inject lateinit var mNetManager: NetManager

    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }

    private val _bicycleInStation = MutableLiveData<Pair<Int, ArrayList<String>>>().apply {
        value = Pair(0, ArrayList())
    }
    val text: LiveData<String> = _text

    val bicycleInStationLiveData : LiveData<Pair<Int, ArrayList<String>>> = _bicycleInStation

    fun bicycleInStation() {
        Log.i(BicycleViewModel::class.simpleName, "bicycleInStation: ${mNetManager.hashCode()}")
        viewModelScope.launch {
            try {
                Log.i(BicycleViewModel::class.simpleName, "bicycleInStation: ${mNetManager.bicycleService == null}")
                val bicycleStation = mNetManager.bicycleService?.bicycleStation(BicycleInStation().toField())
                Log.i("TAG", "bicycleInStation: ${bicycleStation?.body()}")
                val allData = ArrayList<String>()
                val cols = bicycleStation?.body()!!.stations.size
                with(allData) {
                    add("*")
                    addAll(bicycleStation.body()?.btypes!!.toCollection(ArrayList()))

                    for (i in 0 until  cols) {
                        add(bicycleStation.body()!!.stations[i])
                        addAll(bicycleStation.body()!!.data[i].toCollection(ArrayList()))
                    }
                    Log.i("TAG", "bicycleInStation: $this")
                }

                _bicycleInStation.postValue(Pair(cols, allData))
            } catch (e : Exception) {
                e.printStackTrace()
                _text.postValue("e ${e.message}")
            }
        }
    }
}