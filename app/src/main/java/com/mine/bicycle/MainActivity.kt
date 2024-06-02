package com.mine.bicycle

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import android.util.Base64
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.mine.bicycle.config.ConfigManager
import com.mine.bicycle.databinding.ActivityMainBinding
import com.mine.bicycle.net.NetManager
import com.mine.bicycle.ui.dashboard.BicycleFragment
import com.mine.bicycle.ui.dashboard.BicycleViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.function.Function
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    Handler.Callback {

    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navHostFragment: Fragment

    private lateinit var mHandler:Handler
    @Inject lateinit var mNetManager: NetManager

    companion object {
        const val PERMISSION_READ_EXTERNAL = 99
        const val MSG_TYPE_CONFIG_READY = 100
        const val MSG_TYPE_READ_CONFIG = 101
    }

    @SuppressLint("RtlHardcoded")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mHandler = Handler(Looper.getMainLooper(), this)
        checkAndReadConfig()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val toolbar:Toolbar = binding.activityMainBar
        drawerLayout = binding.drawerLayout
        val drawerNav = binding.drawerNavView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main)!!
        navController.addOnDestinationChangedListener { controller, destination, _ ->
            toolbar.title = destination.label
            drawerLayout.closeDrawer(GravityCompat.START)

            controller.currentDestination
            when (destination.id) {
                R.id.navigation_home, R.id.navigation_notifications -> {
                    drawerNav.visibility = View.INVISIBLE
                    drawerNav.menu.clear()
                }

                R.id.navigation_dashboard -> {
                    drawerNav.visibility = View.VISIBLE
                    drawerNav.menu.clear()
                    drawerNav.inflateMenu(R.menu.menu_main)


                }
            }
        }

        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val drawerToggle = object : ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close) {

        }

        drawerToggle.syncState()
        drawerLayout.addDrawerListener(drawerToggle)
        navView.setupWithNavController(navController)

        drawerNav.setNavigationItemSelectedListener(this)

    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        val title = menuItem.title as String
        Toast.makeText(this, title, Toast.LENGTH_SHORT).show()
        drawerLayout.closeDrawer(GravityCompat.START)

        val dashFragment = navHostFragment.childFragmentManager.fragments[0] as BicycleFragment
        dashFragment.gridInStation(menuItem.itemId)
        return false
    }

    private fun checkAndReadConfig() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && null == ConfigManager.mConfig -> {
                //if permission is granted read config has been called in app
                mHandler.sendEmptyMessage(MSG_TYPE_CONFIG_READY)
            }
            else -> {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_READ_EXTERNAL)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_READ_EXTERNAL) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mHandler.sendEmptyMessage(MSG_TYPE_READ_CONFIG)
            } else {
                Toast.makeText(this, "can not read config cause permission denied !", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun handleMessage(msg: Message): Boolean {

        when(msg.what) {
            MSG_TYPE_CONFIG_READY -> {
                Log.i(MainActivity::class.simpleName, "bicycleInStation: ${mNetManager.hashCode()}")
                mNetManager.rebuildClient()
            }
            MSG_TYPE_READ_CONFIG -> {
                ConfigManager.readConfig()
                mHandler.sendEmptyMessage(MSG_TYPE_CONFIG_READY)
            }
        }

        return true
    }
}