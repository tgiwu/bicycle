package com.mine.bicycle.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.mine.bicycle.R
import com.mine.bicycle.databinding.FragmentDashboardBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BicycleFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var gv: GridView
    private lateinit var rvList : RecyclerView
    private lateinit var textView: TextView
    private var mType : Int = TYPE_TXT
    private val bicycleViewModel: BicycleViewModel by viewModels()

    companion object {
        val TAG = BicycleFragment::class.simpleName
        const val TYPE_TXT = 0
        const val TYPE_LIST = 1
        const val TYPE_GRID = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        textView = binding.textDashboard
        gv = binding.gvData
        rvList = binding.rvList

        bicycleViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        bicycleViewModel.bicycleInStationLiveData.observe(viewLifecycleOwner) {
            textView.visibility = View.GONE
            gv.visibility = View.VISIBLE
            gv.numColumns = it.first
            Log.i(TAG, "onCreateView: ${it.second}")
            val adapter = object: BaseAdapter() {

                override fun getCount(): Int {
                    return it.second.size
                }

                override fun getItem(position: Int): Any {
                    return it.second[position]
                }

                override fun getItemId(position: Int): Long {
                    return position.toLong()
                }

                override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                    val v:View = convertView ?: inflater.inflate(R.layout.grid_item, parent, false)

                    val txt = v.findViewById<TextView>(R.id.text)
                    txt.text = it.second[position]
                    return v
                }
            }

            gv.adapter = adapter

        }

        bicycleViewModel.bicyclesLiveData.observe(viewLifecycleOwner) {

        }
//        bicycleViewModel.listBicycle("station='骑行中'", "")
        bicycleViewModel.listBicycle("station='gongyuanmenkou'", "")
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun refreshData(which:Int) {
        when(which) {
            R.id.bicycle_in_station -> {
                setType(TYPE_GRID)
                bicycleViewModel.bicycleInStation()
            }
        }
    }

    fun listBicycle(filter:String, sortStr:String) {
        setType(TYPE_LIST)
        bicycleViewModel.listBicycle(filter, sortStr)
    }

    private fun setType(type:Int) {
        mType = type
        when (type) {
            TYPE_TXT -> {
                textView.visibility = View.VISIBLE
                gv.visibility = View.GONE
                rvList.visibility = View.GONE
            }
            TYPE_LIST -> {
                textView.visibility = View.GONE
                gv.visibility = View.GONE
                rvList.visibility = View.VISIBLE
            }
            TYPE_GRID -> {
                textView.visibility = View.GONE
                gv.visibility = View.VISIBLE
                rvList.visibility = View.GONE
            }
        }
    }
}