package com.mine.bicycle.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mine.bicycle.R
import com.mine.bicycle.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.function.Function

@AndroidEntryPoint
class HomeFragment : Fragment(), View.OnClickListener {

    private val TAG = HomeFragment::class.simpleName

    private var _binding: FragmentHomeBinding? = null

    private var isPermissionGrant = false

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels()

    lateinit var requestInternetPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestInternetPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            isPermissionGrant = it
            if (!it)
                Toast.makeText(requireContext(), "need internet permission", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val btClockIn = binding.btClockIn
        val btClockOut = binding.btClockOut

        btClockOut.setOnClickListener(this)
        btClockIn.setOnClickListener(this)

        val textView: TextView = binding.tvResult
        homeViewModel?.text?.observe(viewLifecycleOwner) {
            textView.text = it
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.bt_clock_in -> {
                Log.i(TAG, "onClick: bt_clock_out")
                checkPermission(Function<Any, Void> {
                    homeViewModel.clockIn()
                    return@Function null
                })
            }
            R.id.bt_clock_out -> {
                Log.i(TAG, "onClick: bt_clock_out")
                checkPermission(Function<Any, Void> {
                    homeViewModel.clockOut()
                    return@Function null
                })
            }
            else -> Log.i(TAG, "onClick: undefined!")
        }
    }

    private fun checkPermission(function: Function<Any, Void>) {
        when {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED -> {
                function.apply(Any())
            }
            else -> {
                requestInternetPermissionLauncher.launch(Manifest.permission.INTERNET)
            }
        }
    }

}