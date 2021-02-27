package com.example.connectuni.QRcode

import android.content.ContentValues.TAG
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.connectuni.MainActivity
import com.example.connectuni.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.Result
import kotlinx.android.synthetic.main.fragment_q_rfragment1.view.*
import me.dm7.barcodescanner.zxing.ZXingScannerView
import java.text.DateFormat

//check in activity
class QRfragment1 : Fragment(), ZXingScannerView.ResultHandler {

    private lateinit var auth: FirebaseAuth
    private lateinit var Database : FirebaseFirestore
    private lateinit var mView: View
    private lateinit var scannerView: ZXingScannerView
    private lateinit var calendar: Calendar
    private lateinit var resultDialog: ScanResultDialog

    companion object {
        fun newInstance(): QRfragment1 {
            return QRfragment1()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        auth = FirebaseAuth.getInstance()
        Database = FirebaseFirestore.getInstance()


        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_q_rfragment1, container, false)
        initQRscanner()
        //initialise class ScanResultDialog
        resultDialog = ScanResultDialog(context!!)
        //if close button selected ,return to camera preview
        resultDialog.setOnDismissListener(object :
            ScanResultDialog.OnDismissListener {
            override fun onDismiss() {
                //val intent = Intent(context, MainActivity::class.java)
                //startActivity(intent)
                scannerView.resumeCameraPreview(this@QRfragment1)

            }
        })
        //close scanner fragment
        mView.floatingActionButton.setOnClickListener {
            //create an Intent object and jump to main page
            val intent = Intent(context, MainActivity::class.java)
            startActivity(intent)
        }
        //on and off flash button
        onClicks()
        return mView.rootView
    }

    //switch on and off the flash
    private fun onClicks(){
        mView.flashToggle.setOnClickListener {
            if (it.isSelected){
                mView.flashToggle.isSelected = false
                scannerView.flash = false
            }else{
                mView.flashToggle.isSelected = true
                scannerView.flash = true

            }
        }
    }

    //initialise QR scanner
    private fun initQRscanner() {
        scannerView = ZXingScannerView(context!!)
        scannerView.setBackgroundColor(ContextCompat.getColor(context!!,
            R.color.translucent
        ))
        scannerView.setBackgroundColor(ContextCompat.getColor(context!!,
            R.color.widgetbackground
        ))
        scannerView.setLaserColor(ContextCompat.getColor(context!!,
            R.color.coloursecondary
        ))
        scannerView.setBorderStrokeWidth(10)
        scannerView.setAutoFocus(true)
        scannerView.setSquareViewFinder(true)
        scannerView.setResultHandler(this)
        mView.containerScanner.addView(scannerView)
        startQRcamera()
    }

    private fun startQRcamera() {
        scannerView.startCamera()
        scannerView.setAutoFocus(true)
    }

    override fun onResume() {
        super.onResume()
        //register ourselves as a handler for scan result
        scannerView.setResultHandler(this)
        scannerView.startCamera()
    }
    override fun onPause(){
        super.onPause()
        scannerView.stopCamera()
    }

    override fun onDestroy(){
        super.onDestroy()
        scannerView.stopCamera()
    }


    @RequiresApi(Build.VERSION_CODES.N)
    override fun handleResult(rawResult: Result?) {
        if(rawResult != null) {
            Log.v(TAG, "Successfully checked in")
            storedScanResult(rawResult)
        }else{
            Log.d(TAG,"Empty QR code")
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun storedScanResult(rawResult:Result){
        calendar = Calendar.getInstance()
        val checkInTime = DateFormat.getDateTimeInstance().format(calendar.time)
        val scanLocation =rawResult.text

        val scan: MutableMap<String,Any> = HashMap()
        scan["Check In Location"] = scanLocation
        scan["Date and Time"] = checkInTime

        val docRef =Database.collection("Users").document(auth.currentUser!!.uid).collection("CheckIn")
            docRef.add(scan).addOnSuccessListener { docRef ->
                Log.d("location visited", " $scanLocation successfully checked-in! ")
                resultDialog.showdialog(rawResult)
            }.addOnFailureListener {e->
                Log.e("Location visited", "Failed to check-in")
            }
    }
}

