package com.example.connectuni.QRcode

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class Splash : AppCompatActivity() {

   /* companion object{
        private const val CAMERA_PERMISSION_REQUEST_CODE = 100
    }*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

            checkCameraPermission()
    }
    private fun checkCameraPermission()
    {
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED){
            startActivity(Intent(this, CheckIn::class.java))
            finish()
        }else{
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA),
                CheckIn.CAMERA_PERMISSION_REQUEST_CODE
            )
        }
    }
    //create a dialog
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == CheckIn.CAMERA_PERMISSION_REQUEST_CODE){
            if(grantResults.isNotEmpty() && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                startActivity(Intent(this,
                    MainScan::class.java))
                finish()
            }else if(isPermissionDenied()) {

                AlertDialog.Builder(this).setTitle("Grant Permissions!")
                    .setMessage("Camera Permission needed to scan the QR code")
                    .setPositiveButton("Grant") { dialog, which -> goToSettings() }
                    .setNegativeButton("Cancel") { dialog, which ->
                        Toast.makeText(this, "Permission needed to start", Toast.LENGTH_SHORT).show()
                        finish()
                    }.show()
            }else{
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA),
                    CheckIn.CAMERA_PERMISSION_REQUEST_CODE
                )
            }
        }
    }
    //never ask for camera permission again if user click allow access for camera
    private fun isPermissionDenied(): Boolean{
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA).not()
        }else{
            return false
        }
    }
    private fun goToSettings(){
        var intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package",packageName,null))
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun onRestart() {
        super.onRestart()
        checkCameraPermission()
    }
}