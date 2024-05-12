package com.mine.bicycle.ui.home

import android.Manifest
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mine.bicycle.App
import com.mine.bicycle.R
import com.mine.bicycle.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.function.Function
import kotlin.random.Random

@AndroidEntryPoint
class HomeFragment : Fragment(), View.OnClickListener, Handler.Callback {

    private val TAG = HomeFragment::class.simpleName

    companion object {
        const val MSG_TYPE_RESET_COUNTER = 0
        const val MSG_TYPE_POET_ROLL = 1
        const val MSG_TYPE_HIDE_BUTTON = 2
    }

    private var _binding: FragmentHomeBinding? = null

    private var isPermissionGrant = false

    private val random = Random(10)

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels()

    private lateinit var requestInternetPermissionLauncher: ActivityResultLauncher<String>

    private var clickCount = 0

    private val mHandler : Handler = Handler(Looper.getMainLooper(), this)

    private var txtPoet:TextView? = null
    private var llBtn :LinearLayout? = null

//    private var mClockLocalDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestInternetPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                isPermissionGrant = it
                if (!it)
                    Toast.makeText(requireContext(), "need internet permission", Toast.LENGTH_SHORT)
                        .show()
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

        txtPoet = binding.txtPoet
        llBtn = binding.llBtn

        btClockOut.setOnClickListener(this)
        btClockIn.setOnClickListener(this)

        txtPoet?.setOnClickListener(this)

        val textView: TextView = binding.tvResult
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        homeViewModel.poetText.observe(viewLifecycleOwner) {
            txtPoet?.text = it
            mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_TYPE_POET_ROLL), 60_000)
        }

        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_TYPE_POET_ROLL), 1_000)


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

            R.id.txt_poet -> {
                clickCount++
                mHandler.removeMessages(MSG_TYPE_RESET_COUNTER)
                if (clickCount == 5) {
                    txtPoet?.visibility = View.GONE
                    llBtn?.visibility = View.VISIBLE
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_TYPE_HIDE_BUTTON), 10_000)
                } else {
                    mHandler.removeMessages(MSG_TYPE_HIDE_BUTTON)
                    mHandler.sendMessageDelayed(Message.obtain(mHandler, MSG_TYPE_RESET_COUNTER), 5000)
                }
            }

            else -> Log.i(TAG, "onClick: undefined!")
        }
    }

    private fun checkPermission(function: Function<Any, Void>) {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.INTERNET
            ) == PackageManager.PERMISSION_GRANTED -> {
                function.apply(Any())
            }

            else -> {
                requestInternetPermissionLauncher.launch(Manifest.permission.INTERNET)
            }
        }
    }

    override fun handleMessage(msg: Message): Boolean {
        when(msg.what) {
            MSG_TYPE_RESET_COUNTER -> {
                clickCount = 0
            }
            MSG_TYPE_POET_ROLL -> {

                val poets: Array<CharSequence> = resources.getTextArray(R.array.poets)
                if (poets.isNotEmpty())
                    homeViewModel.poetRoll(poets[random.nextInt(poets.size)].toString())

            }
            MSG_TYPE_HIDE_BUTTON -> {
                txtPoet?.visibility = View.VISIBLE
                llBtn?.visibility = View.GONE
            }
        }
        return true
    }

//    fun buildLocationDialog() {
//        AlertDialog.Builder(requireContext())
//            .setItems(
//                R.array.clock_locations
//            ) { dialog, which ->
//                {
//
//                    dialog.dismiss()
//                }
//            }
//            .setPositiveButton("cancel") { dialogInterface, _ ->
//                run {
//                    dialogInterface.dismiss()
//                }
//            }
//    }

}