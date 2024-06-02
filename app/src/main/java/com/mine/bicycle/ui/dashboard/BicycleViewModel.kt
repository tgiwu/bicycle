package com.mine.bicycle.ui.dashboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mine.bicycle.annotation.BicycleAnnotation
import com.mine.bicycle.filter.FilterManager
import com.mine.bicycle.filter.FilterManager.Companion.FILTER_TYPE_STATION
import com.mine.bicycle.net.IBicycleServices
import com.mine.bicycle.net.NetManager
import com.mine.bicycle.net.respone.Bicycle
import com.mine.bicycle.repository.model.bicycle.BicycleInStation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.Collections
import javax.inject.Inject

@HiltViewModel
class BicycleViewModel @Inject constructor(): ViewModel() {

    companion object {
        val TAG = BicycleViewModel::class.simpleName
    }

    @Inject lateinit var mNetManager: NetManager
    @Inject lateinit var mFilterManager: FilterManager

    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }

    private val _bicycles = MutableLiveData<List<Bicycle>>()

    private val _bicycleInStation = MutableLiveData<Pair<Int, ArrayList<String>>>().apply {
        value = Pair(0, ArrayList())
    }
    val text: LiveData<String> = _text

    val bicycleInStationLiveData : LiveData<Pair<Int, ArrayList<String>>> = _bicycleInStation

    val bicyclesLiveData : LiveData<List<Bicycle>> = _bicycles

    fun bicycleInStation() {
        Log.i(TAG, "bicycleInStation: ${mNetManager.hashCode()}")
        viewModelScope.launch {
            try {
                Log.i(TAG, "bicycleInStation: ${mNetManager.bicycleService == null}")

                val deferredInStation = async { doListBicycleInStation() }
                val deferredRiding = async { doListBicycleRiding() }
                val (listRiding, count) = deferredRiding.await()
                val (allData, cols) = deferredInStation.await()
                allData += ("骑行中($count)")
                allData += listRiding
                Log.i(TAG, "bicycleInStation: $allData")
                _bicycleInStation.postValue(Pair(cols, allData))
            } catch (e : Exception) {
                e.printStackTrace()
                _text.postValue("e ${e.message}")
            }
        }
    }

    //grid bicycle in station
    private suspend fun doListBicycleInStation(): Pair<ArrayList<String>, Int> {
        val bicycleStation =
            mNetManager.bicycleService?.bicycleStation(BicycleInStation().toField())
        Log.i(TAG, "bicycleInStation: ${bicycleStation?.body()}")
        val allData = ArrayList<String>()
        val cols = bicycleStation?.body()!!.stations.size
        with(allData) {
            add("*")
            addAll(bicycleStation.body()?.btypes!!.toCollection(ArrayList()))

            for (i in 0 until cols) {
                add(bicycleStation.body()!!.stations[i])
                addAll(bicycleStation.body()!!.data[i].toCollection(ArrayList()))
            }
            Log.i(TAG, "bicycleInStation: $this")
        }
        return Pair(allData, cols)
    }

    //grid bicycle riding
    private suspend fun doListBicycleRiding(): Pair<ArrayList<String>, Int> {
        val onRidingParams =
            mFilterManager.buildFilter(FILTER_TYPE_STATION, "骑行中", null, null, null, null)

        Log.i(TAG, "doListBicycleRiding: $onRidingParams")
        val listBicycleRiding =
            mNetManager.bicycleService?.listBicycleByStation(onRidingParams.toMap())

        val r = arrayListOf(0,0,0,0,0,0,0)
        var count = 0

        listBicycleRiding?.body()?.also {

            if (it.isNotEmpty()) {

                it.forEach { ele ->
                    when (ele[1]) {
                        "普通单车" -> r[0] += 1
                        "捷安特" -> r[1] += 1
                        "前后亲子车" -> r[2] += 1
                        "双人亲子车" -> r[3] += 1
                        "小观光车" -> r[4] += 1
                        "四人车" -> r[5] += 1
                        "大观光车" -> r[6] += 1
                    }
                    count ++
                }
            }
        }

        val map = ArrayList(r.map { it.toString() })

        Log.i(TAG, "doListBicycleRiding: $r")
        return Pair(map, count)
    }

    fun listBicycle(filter:String, sortStr:String) {
        Log.i(TAG, "listBicycle: $filter, \n sort: $sortStr")
        viewModelScope.launch {
            val resp = mNetManager.bicycleService?.listBicycleByStation(mapOf("op" to "tablesettings", "filter" to filter, "sortstr" to sortStr))
            val bicycles = arrayListOf<Bicycle>()
            if (resp?.body() != null) {
                (resp.body() as Array<Array<String>>).forEach {
                    if (it.isNotEmpty()) {
                        bicycles.add(
                            Bicycle(imei = it[0], name = it[1], status = it[2].toInt(), station = it[3], power = it[4].toInt(),
                            locked = it[5] == "true", online = it[6] == "true", unknownB1 = it[7] == "true", location = it[8], unknownB2 = it[9] == "true"))
                    }
                }
            }

            Log.d(TAG, "listBicycle: $bicycles")
            _bicycles.postValue(bicycles)
        }
    }
}