package me.tylerbwong.stack.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import coil.load
import coil.transform.CircleCropTransformation
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.Insetter
import me.tylerbwong.stack.R
import me.tylerbwong.stack.data.work.WorkScheduler
import me.tylerbwong.stack.databinding.ActivityMainBinding
import me.tylerbwong.stack.ui.profile.ProfileActivity
import me.tylerbwong.stack.ui.settings.Experimental
import me.tylerbwong.stack.ui.settings.SettingsActivity
import me.tylerbwong.stack.ui.utils.setThrottledOnClickListener
import me.tylerbwong.stack.ui.utils.showLogInDialog
import me.tylerbwong.stack.ui.utils.showRegisterOnSiteDialog
import me.tylerbwong.stack.ui.utils.showSnackbar
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    @Inject
    lateinit var workScheduler: WorkScheduler

    @Inject
    lateinit var experimental: Experimental

    private val viewModel by viewModels<MainViewModel>()

    private val authTabIds = listOf(R.id.create, R.id.bookmarks, R.id.drafts)
    private val navController: NavController
        get() = (supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment)
            .navController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.toolbar)
        binding.bottomNav.setupWithNavController(navController)

        supportActionBar?.title = ""

        viewModel.isAuthenticatedLiveData.observe(this) { isAuthenticated ->
            val bottomNav = binding.bottomNav
            val isCreateQuestionEnabled = experimental.createQuestionEnabled
            authTabIds.forEach {
                bottomNav.menu.findItem(it)?.isVisible = if (it == R.id.create) {
                    isAuthenticated && isCreateQuestionEnabled
                } else {
                    isAuthenticated
                }
            }
            if (bottomNav.selectedItemId in authTabIds) {
                bottomNav.selectedItemId = R.id.home
            }

            if (isAuthenticated) {
                viewModel.fetchUser()
            } else {
                with(binding.profileIcon) {
                    setImageResource(R.drawable.ic_account_circle)
                    setThrottledOnClickListener { showLogInDialog() }
                }
            }
        }
        viewModel.user.observe(this) { user ->
            binding.profileIcon.apply {
                if (user != null) {
                    load("https://images.weserv.nl/?url=%s&output=webp".format(user.profileImage)) {
                        error(R.drawable.user_image_placeholder)
                        placeholder(R.drawable.user_image_placeholder)
                        transformations(CircleCropTransformation())
                    }
                    setThrottledOnClickListener {
                        ProfileActivity.startActivity(this@MainActivity, userId = user.userId)
                    }
                } else if (viewModel.isAuthenticatedLiveData.value == true) {
                    setImageResource(R.drawable.ic_account_circle)
                    setThrottledOnClickListener {
                        viewModel.currentSite.value?.let {
                            showRegisterOnSiteDialog(
                                site = it,
                                siteUrl = viewModel.buildSiteJoinUrl(it),
                            )
                        }
                    }
                } else {
                    setImageResource(R.drawable.ic_account_circle)
                    setThrottledOnClickListener { showLogInDialog() }
                }
            }
        }

        workScheduler.schedule()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchUser()
        viewModel.fetchSites()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings -> {
                SettingsActivity.startActivity(this)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun applyFullscreenWindowInsets() {
        super.applyFullscreenWindowInsets()
        Insetter.builder().setOnApplyInsetsListener { view, insets, initialState ->
            view.updatePadding(
                bottom = initialState.paddings.bottom + insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                ).bottom
            )
        }.applyToView(binding.bottomNav)
    }

    companion object {
        fun makeIntentClearTop(context: Context) = Intent(context, MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    }
}
