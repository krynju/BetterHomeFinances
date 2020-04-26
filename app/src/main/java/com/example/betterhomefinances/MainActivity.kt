package com.example.betterhomefinances

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.betterhomefinances.databinding.ActivityMainBinding
import com.example.betterhomefinances.dummy.DummyContent
import com.example.betterhomefinances.handlers.FirestoreHandler
import com.example.betterhomefinances.handlers.TransactionHandler
import com.example.betterhomefinances.handlers.UserHandler.currentUserReference
import com.firebase.ui.auth.AuthUI
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.app_bar_main.view.*

class MainActivity : AppCompatActivity(), ItemFragment.OnListFragmentInteractionListener {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val toolbar: Toolbar = binding.root.toolbar
        setSupportActionBar(toolbar)

        val fab: FloatingActionButton = binding.root.fab
        fab.setOnClickListener { view ->
//            GroupHandler.createGroup()

            TransactionHandler.createTransaction(
                groupReference = FirestoreHandler.groups.document("C7uKXUkRJ5osSgaOvIs5").path,
                borrowers = hashMapOf(
                    currentUserReference.path to 5.0,
                    "users/uNMjYrRUfhDiGUboqD79" to 5.0
                ),
                title = "TEST TRANSACTION",
                category = "TEST CATEGORY",
                description = "yeet",
                lender = currentUserReference.path,
                value = 10.0
            )


            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        val drawerLayout: DrawerLayout = binding.root.drawer_layout
        val navView: NavigationView = binding.navView
        val navController =
            findNavController(R.id.nav_host_fragment)

//        appBarConfiguration = AppBarConfiguration.Builder(navController.graph)
//            .setDrawerLayout(drawerLayout)
//            .build()
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_groups, R.id.nav_slideshow
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_signout -> {
                AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener { // user is now signed out
                        startActivity(Intent(this, EntryActivity::class.java))
                        finish()
                    }
                true
            }
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onListFragmentInteraction(item: DummyContent.DummyItem?) {
        TODO("Not yet implemented")
    }
}
