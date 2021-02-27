package com.example.connectuni

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.connectuni.CovidUpdate.MalaysiaCovidUpdate
import com.example.connectuni.QRcode.Splash
import com.example.connectuni.location.MapActivity
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    // declare an instance of FirebaseAuth
    lateinit var mAuth: FirebaseAuth
    lateinit var drawerLayout: DrawerLayout
    lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance()
        setSupportActionBar(findViewById(R.id.toolbar))
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigationview)

        val toggle = ActionBarDrawerToggle(this, drawerLayout,toolbar,0, 0)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navigationView.setNavigationItemSelectedListener(this)

        //Profile
        imageBtnProfile.setOnClickListener {
            val intent = Intent(this, Profile::class.java)
            startActivity(intent)
        }
        //QR code scanner
        imageBtnScan.setOnClickListener {
            val QRintent = Intent(this, Splash::class.java)
            startActivity(QRintent)
        }
        // Get current location
        imageBtnLocation.setOnClickListener {
            val GPSintent = Intent(this, MapActivity::class.java)
            startActivity(GPSintent)
        }
        //Get covid-19 update
        imageBtnInfo.setOnClickListener{
            val infointent = Intent(this, MalaysiaCovidUpdate::class.java)
            startActivity(infointent)
        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.FAQs -> {
                Toast.makeText(this, "FAQs selected", Toast.LENGTH_SHORT).show()
            }
            R.id.sop ->{
                Toast.makeText(this,"SOP clicked",Toast.LENGTH_SHORT).show()
            }
            R.id.dependants ->{
                addDependant()
            }
            //Logout
            R.id.logout ->{
               logout()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun logout(){
        mAuth.signOut()
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
        Toast.makeText(this,"logout",Toast.LENGTH_SHORT).show()
    }

    private fun addDependant(){
        val intent = Intent(this, Dependant::class.java)
        startActivity(intent)
    }

}



