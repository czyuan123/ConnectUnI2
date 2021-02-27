package com.example.connectuni.QRcode

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
import com.example.connectuni.CustomAdapter
import com.example.connectuni.DependantData
import com.example.connectuni.R
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
    val Checked = arrayListOf<String>()
    private lateinit var calendar: Calendar
    private lateinit var view: View

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_code_scanner)

        Database = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()
        listView = findViewById<View>(R.id.listView) as ListView
        //checkCheckInResult()
        addDependantData()
        button.setOnClickListener {
            calendar = Calendar.getInstance()
            val dependantcheckin: MutableMap<String, Any> = HashMap()
            dependantcheckin["Date and Time"] = DateFormat.getDateTimeInstance().format(calendar.time)
            dependantcheckin["DependantName"] = Checked

            Database.collection("Users")
                .document(mAuth.currentUser!!.uid).collection("CheckIn")
                .document("Dependant").collection("Dependant checked-in")
                .add(dependantcheckin).addOnSuccessListener {
                    Log.d("Dependant", " $Checked successfully checked-in! ")
                    val intent =
                        Intent(this@CheckIn, WorkManagerNotificationActivity::class.java)
                    intent.putExtra("Fragment", "Fragment A")
                    startActivity(intent)

                }.addOnFailureListener { e ->
                    Log.e("Location visited", "Failed to check-in")
                }

        }

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
                        Checked.add(checkList.fullName)
                        adapter.notifyDataSetChanged()
                            /*checkBox.setOnCheckedChangeListener { compoundButton: CompoundButton, isChecked: Boolean ->
                                for (i in 0 until dependantList!!.size) {
                                    if (isChecked) {
                                        Checked.add(checkList.fullName)
                                        adapter.notifyDataSetChanged()
                                    } else {
                                        Checked.removeAt(position)
                                    }
                                }
                            }*/

                    }
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {

            }

        })

        // checkCheckInResult()
        checkIn.visibility = View.VISIBLE
        checkOut.visibility = View.VISIBLE
        checkIn.setOnClickListener {

            val intent = Intent(this, WorkManagerNotificationActivity::class.java)
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

                adapter = CustomAdapter(
                    dependantList!!,
                    applicationContext
                )
                listView.adapter = adapter
            }
    }

    /*private fun checkCheckInResult() {
        Database.collection("Users").document(mAuth.currentUser!!.uid).collection("CheckIn")
            //.orderBy("Date and Time", Query.Direction.DESCENDING).limit(10)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(ContentValues.TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    checkIn.visibility = View.INVISIBLE

                } else {
                    checkOut.visibility = View.INVISIBLE
                }

                Database.collection("Users").document(mAuth.currentUser!!.uid)
                    .collection("CheckOut")
                    //.orderBy("Date and Time", Query.Direction.DESCENDING).limit(10)
                    .addSnapshotListener { snapshot, e ->
                        if (e != null) {
                            Log.w(ContentValues.TAG, "Listen failed.", e)
                            return@addSnapshotListener
                        }
                        if (snapshot != null) {
                            checkOut.visibility = View.INVISIBLE

                        } else {
                            checkIn.visibility = View.INVISIBLE
                        }
                    }
            }
    }*/

}