package com.example.connectuni

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*

class Profile : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var Database : FirebaseFirestore
    private val requestCode = 1000
    private lateinit var PPuri : Uri
    private lateinit var storageref : StorageReference
    private lateinit var  profileref : StorageReference
    //create object
    lateinit var registerFullName:TextView
    lateinit var registerEmailAddress : TextView
    lateinit var registerPhoneNumber : TextView
    lateinit var profilePic : ImageView
    lateinit var editprofileButton : ImageButton
   // lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        Database = FirebaseFirestore.getInstance()
        storageref = FirebaseStorage.getInstance().reference

        profileref = storageref.child("user/ ${auth.currentUser!!.uid} /profile.jpg")
        profileref.downloadUrl.addOnSuccessListener {Uri->
            Picasso.get().load(Uri).into(profilePic)
        }

       //To change the title of the layout to PROFILE
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.title = "User Profile"

        profilePic = findViewById(R.id.profilePic)
        registerFullName = findViewById(R.id.TIFullName)
        registerEmailAddress = findViewById(R.id.TIAge)
        registerPhoneNumber = findViewById(R.id.TVregisterPhoneNum)
        editprofileButton = findViewById(R.id.editprofileButton)

        // To retrieve stored data from firestore
        val dataRef: DocumentReference =
            Database.collection("Users").document(auth.currentUser!!.uid).collection("Data").document("Profile")
        dataRef.get().addOnSuccessListener { documentSnapshot ->
            //document should always exist as it enter main page activity
            if (documentSnapshot.exists()) {
                //assign the data to the variable
                registerFullName.text  = documentSnapshot.getString("First Name") + " " + documentSnapshot.getString("Last Name")
                registerEmailAddress.text = documentSnapshot.getString("Email Address")
                registerPhoneNumber.text = auth.currentUser!!.phoneNumber

            } else {
                Log.d("tag", "Profile Data Not Exist")
            }
        }
        //If return button clicked, back to main page
        BacktoMainBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        uploadimageButton.setOnClickListener {
            //open phone gallery TO GET URI OF THE IMAGE CHOSEN
            val galleryintent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryintent,requestCode)
        }
        editprofileButton.setOnClickListener {
            val editintent = Intent(this,EditProfile::class.java)
            startActivity(editintent)
        }
       /* profilePic.setOnClickListener {
            uploadPP()
        }*/
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @androidx.annotation.Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode== requestCode && resultCode== Activity.RESULT_OK){
            //val PPbitmap= data?.extras?.get("image") as Bitmap
            PPuri = data!!.data!!
            //profilePic.setImageURI(PPuri)
            uploadPPurl(PPuri)
           // changePPurl(PPbitmap)

        }
    }

    private fun uploadPPurl(PPuri : Uri){
        //upload image to firestorage
        uploadprogressBar.visibility = View.VISIBLE
        var fileref = storageref.child("user ${auth.currentUser!!.uid} /profile.jpg")
        fileref.putFile(PPuri).addOnSuccessListener {taskSnapshot->
            //get downloaded image url
            fileref.downloadUrl.addOnSuccessListener {Uri->
                Picasso.get().load(Uri).into(profilePic)
                Toast.makeText(this,"Profile pic uploaded",Toast.LENGTH_SHORT).show()
                uploadprogressBar.visibility = View.INVISIBLE
            }
        }.addOnFailureListener {
            Toast.makeText(this,"Failed to upload",Toast.LENGTH_SHORT).show()
        }
    }
    /*private fun uploadPP(){
       Intent(MediaStore.ACTION_IMAGE_CAPTURE).also {pictureIntent ->
           pictureIntent.resolveActivity(packageManager!!)?.also {
               startActivityForResult(pictureIntent,requestCamera)
           }
       }
   }*/

    //upload picture and save in firebase storage
    /*private fun changePPurl(bitmap: Bitmap){//take bitmap as parameter
        val Byte = ByteArrayOutputStream()
        //create storage reference
        val mStorageRef = FirebaseStorage.getInstance().reference.child("Profile Pic/${auth.currentUser?.uid}")
        //to put spec map in storage reference, get byte array from bitmap instance -> Bitmap (call compress function inside it)
        bitmap.compress(Bitmap.CompressFormat.PNG.also { Bitmap.CompressFormat.JPEG },100,Byte)
        val pic = Byte.toByteArray()
        val upload = mStorageRef.putBytes(pic)
        //upload process takes time, so create a complete listener
        uploadprogressBar.visibility = View.VISIBLE
        upload.addOnCompleteListener { task ->
            uploadprogressBar.visibility = View.INVISIBLE
            if (task.isSuccessful){
                mStorageRef.downloadUrl.addOnCompleteListener { uploadtask->
                    uploadtask.result?.let { PPuri = it
                    Toast.makeText(this,PPuri.toString(),Toast.LENGTH_SHORT).show()
                     //set spec map to image view
                    profilePic.setImageBitmap(bitmap) } }
            }
            else{
                task.exception?.let{ Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()}}
        }
    }*/
}