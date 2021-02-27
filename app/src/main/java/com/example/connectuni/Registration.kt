package com.example.connectuni

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore


class Registration : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var Database : FirebaseFirestore
   // private lateinit var dataRef : DatabaseReference

    //create object
    lateinit var registerFirstName: EditText
    lateinit var registerEmailAddress : EditText
    lateinit var registerHomeAddress : EditText
    lateinit var registerLastName : EditText
    lateinit var buttonNewRegister : Button
    lateinit var userID : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        auth = FirebaseAuth.getInstance()
        Database = FirebaseFirestore.getInstance()
        userID = auth.currentUser!!.uid

        registerFirstName = findViewById(R.id.editTextFirstName)
        registerEmailAddress = findViewById(R.id.editTextEmailAddress)
        registerHomeAddress = findViewById(R.id.editTextHomeAddress)
        registerLastName = findViewById(R.id.editTextLastName)
        buttonNewRegister = findViewById(R.id.buttonNewRegister)

 //when register button is clicked
        buttonNewRegister.setOnClickListener {
            registerNewuser()
            if (registerFirstName.text.trim().isNotEmpty()&& registerEmailAddress.text.trim().isNotEmpty() && registerHomeAddress.text.trim().isNotEmpty() && registerLastName.text.trim().isNotEmpty()){
            //if all info is entered, show "registered"
            Toast.makeText(this,"Registered",Toast.LENGTH_LONG).show()}
            else{
            //otherwise, ask user to fill it all
                Toast.makeText(this,"Input Required",Toast.LENGTH_LONG).show()}
                return@setOnClickListener
            }
        }

    private fun registerNewuser() {
        val FirstName = registerFirstName.text.toString().trim()
        val HomeAddress = registerHomeAddress.text.toString().trim()
        val LastName = registerLastName.text.toString().trim()
        val EmailAddress = registerEmailAddress.text.toString().trim()

        if (FirstName.isEmpty()) {
            registerFirstName.error = "Kindly enter your full name"
            return//stop execution using return
        }
        if (HomeAddress.isEmpty()) {
            registerHomeAddress.error = "Kindly enter your home address"
            return//stop execution using return
        }
        if (LastName.isEmpty()) {
            registerLastName.error = "Kindly enter your phone number"
            return//stop execution using return
        }
        if (EmailAddress.isEmpty()) {
            registerEmailAddress.error = "Kindly enter your email address"
            return//stop execution using return
        }
        //firestore database
        val docRef: DocumentReference =
            Database.collection("Users").document(userID)
        val user: MutableMap<String, Any> = HashMap()
        user["First Name"] = FirstName
        user["Last Name"] = LastName
        user["Home Address"] = HomeAddress
        user["Email Address"] = EmailAddress

        //add new user detail to database
        docRef.collection("Data").document("Profile").set(user).addOnSuccessListener { docRef ->
            Log.d("new user", "onSuccess: User Profile Created : $userID")
            Toast.makeText(
                applicationContext,
                "New user's info saved successfully",
                Toast.LENGTH_LONG
            ).show()
            startActivity(Intent(this@Registration, MainActivity::class.java))
        }.addOnFailureListener { e ->
            Log.d("new user", "Error creating new user", e)
        }
    }}


