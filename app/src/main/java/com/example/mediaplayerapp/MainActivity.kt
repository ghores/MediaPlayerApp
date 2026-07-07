package com.example.mediaplayerapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.example.mediaplayerapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var drawBehindStatusBar = false

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (shouldFinishForDuplicateLaunch()) {
            finish()
            return
        }
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestNotificationPermissionIfNeeded()
        handleOpenPlayerIntent(intent)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val topPadding = if (drawBehindStatusBar) 0 else systemBars.top
            view.setPadding(systemBars.left, topPadding, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleOpenPlayerIntent(intent)
    }

    fun setDrawBehindStatusBar(enabled: Boolean) {
        drawBehindStatusBar = enabled
        ViewCompat.requestApplyInsets(binding.main)
    }

    /**
     * When the app is first launched from a non-launcher entry point (e.g. the
     * media notification), tapping the launcher icon later can spawn a duplicate
     * instance on top of the existing task, re-showing the splash screen. In that
     * case this activity is not the task root, so we finish it and let the already
     * running instance resume.
     */
    private fun shouldFinishForDuplicateLaunch(): Boolean {
        return !isTaskRoot &&
            intent.hasCategory(Intent.CATEGORY_LAUNCHER) &&
            intent.action == Intent.ACTION_MAIN
    }

    private fun handleOpenPlayerIntent(intent: Intent) {
        if (!intent.getBooleanExtra(EXTRA_OPEN_PLAYER, false)) return
        intent.removeExtra(EXTRA_OPEN_PLAYER)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainerView) as? NavHostFragment ?: return
        navigateToPlayer(navHostFragment.navController)
    }

    private fun navigateToPlayer(navController: NavController) {
        when (navController.currentDestination?.id) {
            R.id.currentPlayingFragment -> return

            R.id.splashFragment, null -> {
                navController.navigate(
                    R.id.viewPagerFragment,
                    null,
                    NavOptions.Builder()
                        .setPopUpTo(R.id.splashFragment, /* inclusive = */ true)
                        .build()
                )
                navController.navigate(R.id.currentPlayingFragment)
            }

            else -> navController.navigate(R.id.currentPlayingFragment)
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

        val isGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

        if (!isGranted) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    companion object {
        const val EXTRA_OPEN_PLAYER = "extra_open_player"
    }
}
