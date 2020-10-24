package com.example.connectuni

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.dependant.*
import kotlin.collections.set


class Dependant : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var Database : FirebaseFirestore
    private lateinit var arrayAdapter : ArrayAdapter<CharSequence>

    //create object
    private lateinit var dFullName: EditText
    private lateinit var dNRIC : EditText
    private lateinit var dHomeAddress : EditText
    private lateinit var savebutton : Button
    private lateinit var dAge :EditText
    private lateinit var cancel : TextView
    private lateinit var addanother : TextView
    private lateinit var back :ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dependant)

        auth = FirebaseAuth.getInstance()
        Database = FirebaseFirestore.getInstance()
        dFullName = findViewById(R.id.TIFullName)
        dAge = findViewById(R.id.TIAge)
        dNRIC = findViewById(R.id.TINRIC)
        dHomeAddress = findViewById(R.id.TIHomeAddress)
        savebutton = findViewById(R.id.savebutton)
        cancel = findViewById(R.id.cancel)
        addanother = findViewById(R.id.addanother)
        back = findViewById(R.id.returnButton)
        spinner2()
        spinner1()

        back.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        savebutton.setOnClickListener {
            savedependent()
        }

        cancel.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        addanother.setOnClickListener {
            val intent = Intent(this, Dependant::class.java)
            startActivity(intent)
        }
    }
    private fun savedependent() {
        val FullName = dFullName.text.toString().trim()
        val HomeAddress = dHomeAddress.text.toString().trim()
        val Age = dAge.text.toString().trim()
        val IC = dNRIC.text.toString().trim()
        val gender = Gender.selectedItem.toString()
        val relationship = Relationship.selectedItem.toString()

        if (FullName.isEmpty()) {
            dFullName.error = "Kindly enter your full name"
            return//stop execution using return
        }
        if (HomeAddress.isEmpty()) {
            dHomeAddress.error = "Kindly enter your home address"
            return//stop execution using return
        }
        if (Age.isEmpty()) {
            dAge.error = "Kindly enter your phone number"
            return//stop execution using return
        }
        if (IC.isEmpty()) {
            dNRIC.error = "Kindly enter your email address"
            return//stop execution using return
        }
        //firestore database
        val docRef: CollectionReference =
            Database.collection("Users").document(auth.currentUser!!.uid).collection("Dependents")
        val dependent: MutableMap<String, Any> = HashMap()
        dependent["Full Name"] = FullName
        dependent["Age"] = Age
        dependent["NRIC"] = IC
        dependent["Home Address"] = HomeAddress
        dependent["Gender"] = gender
        dependent ["Relationship"] = relationship

        //add new user detail to database
        docRef.add(dependent).addOnSuccessListener { docRef ->
            Log.d("new dependent", "Dependent added successfully")
            Toast.makeText(
                applicationContext,
                "New dependent added successfully",
                Toast.LENGTH_LONG
            ).show()
            startActivity(Intent(this, Dependant::class.java))
        }.addOnFailureListener { e ->
            Log.d("new dependent", "Error adding dependent", e)
        }
    }


    private fun spinner2(){
        val gender :Spinner = findViewById(R.id.Gender)
        arrayAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.gender_array,
            android.R.layout.simple_spinner_dropdown_item
        )
        gender.adapter = arrayAdapter
        gender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(arg0: AdapterView<*>) {

            }
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val item: String = parent.getItemAtPosition(position).toString()
                Log.d("tag", "Gender: $item")
            }
        }
    }

    private fun spinner1() {
        val relationship: Spinner = findViewById(R.id.Relationship)
        arrayAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.relationship_array,
            android.R.layout.simple_spinner_dropdown_item
        )
        relationship.adapter = arrayAdapter
        relationship.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(arg0: AdapterView<*>) {

            }
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val item: String = parent.getItemAtPosition(position).toString()
                Log.d("TAG", "Relationship : $item")
            }
        }
    }
}