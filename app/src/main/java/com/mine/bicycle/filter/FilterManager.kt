package com.mine.bicycle.filter

import com.mine.bicycle.net.request.BicycleReq
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class FilterManager constructor(@ApplicationContext context: ApplicationContext){

    companion object {

        const val FILTER_TYPE_SN = 0
        const val FILTER_TYPE_TYPE = 1
        const val FILTER_TYPE_STATE = 2
        const val FILTER_TYPE_STATION = 3
        const val FILTER_TYPE_BATTERY = 4
        const val FILTER_TYPE_LOCK = 5
        const val FILTER_TYPE_ONLINE = 6

        @Singleton
        @Provides
        fun getFilterManager() : FilterManager{
            val filterManager = FilterManager(ApplicationContext())
            return filterManager
        }
    }

    lateinit var mBicycleType:Array<String>

    fun loadStateAndValue() {

    }

    fun buildFilter(filterType:Int, arg1:String?, arg2:Int?, arg3:Boolean?, sort: String?, isDesc: Boolean?): BicycleReq {
        return when(filterType) {
            FILTER_TYPE_SN -> filterBySn(arg1!!, sort, isDesc)
            FILTER_TYPE_TYPE -> filterByType(arg1!!, sort, isDesc)
            FILTER_TYPE_STATE -> filterByState(arg2!!, sort, isDesc)
            FILTER_TYPE_STATION -> filterByStation(arg1!!, sort, isDesc)
            FILTER_TYPE_BATTERY -> filterByBattery(arg2!!, sort, isDesc)
            FILTER_TYPE_LOCK -> filterByLock(arg3, sort, isDesc)
            FILTER_TYPE_ONLINE -> filterByOnline(arg3, sort, isDesc)
            else -> BicycleReq("tablesettings")
        }
    }


    private fun filterBySn(sn:String, sort:String?, isDesc:Boolean?): BicycleReq {
        return BicycleReq(op = "tablesettings",
            filter = "sn like %$sn%",
            sortstr = if (sort == null) null else "$sort ${if (isDesc == null) null else if (isDesc) "desc" else null}" )
    }

    private fun filterByType(type:String, sort: String?, isDesc: Boolean?) : BicycleReq {
        return BicycleReq(op = "tablesettings",
            filter = "type='$type'",
            sortstr = if (sort == null) null else "$sort ${if (isDesc == null) null else if (isDesc) "desc" else null}")
    }

    private fun filterByState(state:Int, sort: String?, isDesc: Boolean?): BicycleReq {
        return BicycleReq(op = "searchstate",
            state = state,
            sortstr = if (sort == null) null else "$sort ${if (isDesc == null) null else if (isDesc) "desc" else null}")
    }

    private fun filterByStation(station:String, sort: String?, isDesc: Boolean?) : BicycleReq {
        return BicycleReq(op = "tablesettings",
            filter = "station='$station'",
            sortstr = if (sort == null) null else "$sort ${if (isDesc == null) null else if (isDesc) "desc" else null}")
    }

    private fun filterByBattery(power : Int, sort: String?, isDesc: Boolean?): BicycleReq {
        return BicycleReq(op = "tablesettings",
            filter = "Bat<$power",
            sortstr = if (sort == null) null else "$sort ${if (isDesc == null) null else if (isDesc) "desc" else null}")
    }

    private fun filterByLock(locked:Boolean?, sort: String?, isDesc: Boolean?): BicycleReq {
        return BicycleReq(op = "tablesettings",
            filter = if (locked == null) null else "islock=${if (locked) "true" else "false"}",
            sortstr = if (sort == null) null else "$sort ${if (isDesc == null) null else if (isDesc) "desc" else null}")
    }

    private fun filterByOnline(online:Boolean?, sort: String?, isDesc: Boolean?): BicycleReq {
        return BicycleReq(op = "tablesettings",
            filter = if (online == null) null else "online=${if (online) "true" else "false"}",
            sortstr = if (sort == null) null else "$sort ${if (isDesc == null) null else if (isDesc) "desc" else null}")
    }
}