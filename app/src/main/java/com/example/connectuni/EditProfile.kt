package com.example.connectuni

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class EditProfile : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var Database : FirebaseFirestore

    //create object
    lateinit var updateFirstName: TextView
    lateinit var updateLastName: TextView
    lateinit var updateEmailAddress : TextView
    lateinit var updateHomeAddress : TextView
    lateinit var savebutton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        auth = FirebaseAuth.getInstance()
        Database = FirebaseFirestore.getInstance()

        updateFirstName = findViewById(R.id.TIFullName)
        updateLastName = findViewById(R.id.TVregisterLastName)
        updateEmailAddress = findViewById(R.id.TIAge)
        updateHomeAddress = findViewById(R.id.TIHomeAddress)
        savebutton = findViewById(R.id.savebutton)

        val dataRef: DocumentReference =
            Database.collection("Users").document(auth.currentUser!!.uid).collection("Data").document("Profile")
        dataRef.get().addOnSuccessListener { documentSnapshot ->
            //document should always exist as it enter main page activity
            if (documentSnapshot.exists()) {
                //assign the data to the variable
                updateFirstName.text = documentSnapshot.getString("First Name")
                updateLastName.text = documentSnapshot.getString("Last Name")
                updateEmailAddress.text = documentSnapshot.getString("Email Address")
                updateHomeAddress.text = documentSnapshot.getString("Home Address")

            } else {
                Log.d("tag", "Profile Data Not Exist")
            }
        }

        savebutton.setOnClickListener {
            Updateprofile()
        }
    }
    private fun Updateprofile(){
        val FirstName = updateFirstName.text.toString().trim()
        val LastName = updateLastName.text.toString().trim()
        val HomeAddress = updateHomeAddress.text.toString().trim()
        val EmailAddress = updateEmailAddress.text.toString().trim()

        //firestore database
        val docRef: DocumentReference =
            Database.collection("Users").document(auth.currentUser!!.uid).collection("Data").document("Profile")
        val user: MutableMap<String,Any> = HashMap()
        //user["Last Name"] = LastName
        user["First Name"] = FirstName
        user["Last Name"] = LastName
        user["Home Address"] = HomeAddress
        user["Email Address"] = EmailAddress
        docRef.update(user).addOnSuccessListener {docRef->
            Log.d("update user","DocumentSnapshot successfully updated")
            Toast.makeText(applicationContext,"Update successfully",Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@EditProfile, Profile::class.java))
        }.addOnFailureListener {e->
            Log.e("update user","Error updating document",e)
        }
    }

}