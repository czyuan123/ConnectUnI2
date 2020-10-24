package com.example.connectuni

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.hbb20.CountryCodePicker
import kotlinx.android.synthetic.main.activity_login.*
import android.widget.Toast.makeText as androidWidgetToastMakeText


class Login : AppCompatActivity() {

    //assign variable
    lateinit var mCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var mAuth: FirebaseAuth
    lateinit var verificationId: String
    private lateinit var Database: FirebaseFirestore
    lateinit var ccp: CountryCodePicker
    lateinit var phoneNumber : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        //initialize mAuth
        mAuth = FirebaseAuth.getInstance()
        Database = FirebaseFirestore.getInstance()
        ccp = findViewById(R.id.ccp)
        phoneNumber = findViewById(R.id.editTextloginPhoneNum)

        //when generate OTP buton is clicked
        buttonGenerateOTP.setOnClickListener {View->
                if(phoneNumber.text.toString().isNotEmpty() ) {
                        progressOTP.visibility = android.view.View.VISIBLE
                        Status.visibility = android.view.View.VISIBLE
                        editTextOTP.visibility= android.view.View.VISIBLE
                        val phoneNum =
                            ccp.selectedCountryCodeWithPlus + editTextloginPhoneNum.text.toString()
                        Log.d("phone", "Phone No.: $phoneNum")
                        //execute verify function to send OTP to the user's phone
                        verify(phoneNum)
                }
        }

        //When verify button is clicked
        buttonToLogin.setOnClickListener { view: View? ->
            progressOTP.visibility = View.INVISIBLE
            val OTP = editTextOTP.text.toString()
            //check whether OTP string is empty or not
            if (TextUtils.isEmpty(OTP)) {
                Toast.makeText(this@Login, "Please key in OTP", Toast.LENGTH_LONG).show()
                //to get qualified return
                return@setOnClickListener
            }
            login(verificationId, OTP)
        }
    }

    override fun onStart() {
        super.onStart()
        // [END_EXCLUDE]
        if (mAuth.currentUser != null) {
            Status.visibility = View.VISIBLE
            Status.text = "Checking..."
            userInfoCheck()
        }
    }


    //function for callback action handling request
    private fun Callbacks() {
        mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            override fun onVerificationCompleted(phoneAuthCredential : PhoneAuthCredential) {
                progressOTP.visibility = View.INVISIBLE
                signIn(phoneAuthCredential)
            }

            //situation when invalid request for authentication received
            override fun onVerificationFailed(p0: FirebaseException) {
                //Thrown when one or more of the credentials passed to a method fail to identify and/or authenticate the user subject of that operation
                if (p0 is FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(this@Login, "Invalid phone number", Toast.LENGTH_LONG).show()
                }
                //Exception thrown when a request to a Firebase service has been blocked due to having received too many consecutive requests from the same device.
                else if (p0 is FirebaseTooManyRequestsException) {
                    Toast.makeText(this@Login, "Quota exceeded", Toast.LENGTH_LONG).show()
                }
            }

            //situation when the OTP is sent
            override fun onCodeSent(verificationid: String, token: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(verificationid, token)
                //need to ask user to enter the OTP and create a credential by combining it with verificationId
                verificationId = verificationid
                progressOTP.visibility = View.INVISIBLE
                Status.visibility = View.GONE
                editTextOTP.visibility = View.VISIBLE
            }

        }
    }

    private fun userInfoCheck() {
        val dataRef: DocumentReference =
            Database.collection("Users").document(mAuth.currentUser!!.uid).collection("Data").document("Profile")
        dataRef.get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                } else {
                    startActivity(Intent(applicationContext, Registration::class.java))
                    finish()
                }
            }
        .addOnFailureListener {
                Toast.makeText(
                    this@Login,
                    "Profile Do Not Exists",
                    Toast.LENGTH_SHORT
                ).show()

        }
    }

    // verify function
    private fun verify(phoneNumber: String) {
        Callbacks()
        //pass the phone num to request Firebase verify user's phone num
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber, // Phone number to verify
            60, // Timeout duration
            java.util.concurrent.TimeUnit.SECONDS, // Unit of timeout
            this, // set up instance for current Activity (for callback binding)
            mCallbacks
        ) // OnVerificationStateChangedCallbacks
    }

    //function to start sign in with phone
    private fun signIn(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    userInfoCheck()
                    Toast.makeText(this@Login, "Phone successfully authenticated " + (mAuth.currentUser?.uid
                        ), Toast.LENGTH_SHORT).show()

                } else {
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        progressOTP.visibility = View.GONE
                        Status.visibility = View.GONE
                        androidWidgetToastMakeText(this@Login, "Authentication failed!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun login(verificationId: String, OTP: String) {
        //create a value called credential
        if (OTP.isNotEmpty() && OTP.length == 6) {
            val credential: PhoneAuthCredential =
                PhoneAuthProvider.getCredential(verificationId, OTP)
            signIn(credential)
        }
    }
}
