package com.example.connectuni

import android.content.ContentValues
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.ListView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_code_scanner.*
import kotlinx.android.synthetic.main.popupwindow.*
import java.text.DateFormat


class CheckIn : AppCompatActivity() {
    companion object {
        const val CAMERA_PERMISSION_REQUEST_CODE = 100
    }

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var Database: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth
    private var dependantList: ArrayList<DependantData>? = null
    private lateinit var listView: ListView
    private lateinit var adapter: CustomAdapter

    private lateinit var calendar: Calendar
    private lateinit var view: View

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_code_scanner)

        Database = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()
        listView = findViewById<View>(R.id.listView) as ListView
        addDependantData()
        bottomSheetBehavior = BottomSheetBehavior.from(bottomsheet)
        bottomSheetBehavior.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            @RequiresApi(Build.VERSION_CODES.N)
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                listView.onItemClickListener =
                    AdapterView.OnItemClickListener { _, _, position, _ ->
                        val checkList: DependantData =
                            dependantList!![position]
                        checkList.checked = !checkList.checked
                        adapter.notifyDataSetChanged()
                        val checked = ArrayList<String>()
                        for(i in 0  until dependantList!!.size) {
                            if (checkList.checked) {
                                checked.add(checkList.fullName)
                            }
                        }
                        button.setOnClickListener {
                            calendar = Calendar.getInstance()
                            val dependantcheckin: MutableMap<String, Any> = HashMap()
                            dependantcheckin["Dependants"] = checked.toString()
                            dependantcheckin["Date and Time"] =
                                DateFormat.getDateTimeInstance().format(calendar.time)

                            val docRef = Database.collection("Users")
                                .document(mAuth.currentUser!!.uid).collection("CheckIn")
                                .document("Dependant").collection("Dependant checked-in")
                            docRef.add(dependantcheckin).addOnSuccessListener {
                                Log.d(
                                    "Dependant",
                                    " $checked successfully checked-in! "
                                )
                                val intent =
                                    Intent(this@CheckIn, MainScan::class.java)
                                intent.putExtra("Fragment", "Fragment A")
                                startActivity(intent)

                            }.addOnFailureListener { e ->
                                Log.e("Location visited", "Failed to check-in")
                            }

                        }

                    }
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {

            }

        })

        // checkCheckInResult()
        checkIn.visibility = View.VISIBLE
        checkOut.visibility = View.VISIBLE
        checkIn.setOnClickListener {

            val intent = Intent(this, MainScan::class.java)
            intent.putExtra("Fragment", "Fragment A")
            startActivity(intent)
        }
        checkOut.setOnClickListener {
            val intent = Intent(this, MainScan::class.java)
            intent.putExtra("Fragment", "Fragment B")
            startActivity(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun addDependantData() {

        dependantList = ArrayList<DependantData>()
        Database.collection("Users").document(mAuth.currentUser!!.uid).collection("Dependents")
            .get().addOnSuccessListener { result ->
                for (document in result) {
                    //obtain data from firestore and store in class resultData
                    val data = DependantData(
                        document.data["Full Name"].toString(),
                        false
                    )
                    dependantList!!.add(data)
                }

                adapter = CustomAdapter(dependantList!!, applicationContext)
                listView.adapter = adapter

            }
    }

    private fun checkCheckInResult() {
        Database.collection("Users").document(mAuth.currentUser!!.uid).collection("CheckIn")
            //.orderBy("Date and Time", Query.Direction.DESCENDING).limit(10)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(ContentValues.TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val intent = Intent(this,CheckOut::class.java)
                    startActivity(intent)

                }else {
                    checkIn.visibility = View.VISIBLE
                    }
            }
       Database.collection("Users").document(mAuth.currentUser!!.uid).collection("CheckOut")
            //.orderBy("Date and Time", Query.Direction.DESCENDING).limit(10)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(ContentValues.TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    checkIn.visibility = View.VISIBLE

                }else {
                    checkOut.visibility = View.VISIBLE
                }
            }
    }


}