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

    private val bicycleViewModel: BicycleViewModel by viewModels()

    companion object {
        val TAG = BicycleFragment::class.simpleName
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

        val textView: TextView = binding.textDashboard

        gv = binding.gvData

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

                    var v:View
                    if (convertView == null) {
                        v = inflater.inflate(R.layout.grid_item, parent, false)
                    } else {
                        v = convertView
                    }

                    val txt = v.findViewById<TextView>(R.id.text)
                    txt.text = it.second[position]
                    return v
                }
            }

            gv.adapter = adapter

        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    public fun refreshData(which:Int) {
        when(which) {
            R.id.bicycle_in_station -> bicycleViewModel.bicycleInStation()
        }
    }
}