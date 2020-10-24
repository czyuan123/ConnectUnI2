package com.example.connectuni

import android.app.Dialog
import android.content.Context
import android.icu.util.Calendar
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.zxing.Result
import kotlinx.android.synthetic.main.scanresult.*
import java.text.DateFormat

class ScanResultDialog(var context: Context){


    private lateinit var calendar: Calendar
    private lateinit var dialog : Dialog
    private var onDismissListener: OnDismissListener?= null

    init{
        initDialog()
    }

    private fun initDialog(){
        dialog = Dialog(context)
        dialog.setContentView(R.layout.scanresult)
        dialog.setCancelable(false)
        onClicks()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun showdialog(result: Result){

        calendar = Calendar.getInstance()
        dialog.scannedText.text = result.text
        dialog.scannedDate.text = DateFormat.getDateTimeInstance().format(calendar.time)
        dialog.show()

       /* auth = FirebaseAuth.getInstance()
        Database = FirebaseFirestore.getInstance()

        val id = Database.collection("Users").document(auth.currentUser!!.uid)
            .collection("QRcode").document().id
        val dataRef: DocumentReference =
            Database.collection("Users").document(auth.currentUser!!.uid).collection("QRcode").document(id)
            dataRef.get().addOnSuccessListener { documentSnapshot ->
            //check whether document exists
            if (documentSnapshot != null) {
                //assign the data to the variable
                dialog.scannedText.text  = documentSnapshot.getString("Check In Location")
                dialog.scannedDate.text = documentSnapshot.getString("Date and Time")
                dialog.show()
            } else {
                Log.d("tag", "Scanned Data Not Exist")
            }
        }*/

    }

    private fun onClicks(){
        dialog.closedialog.setOnClickListener {
            onDismissListener?.onDismiss()
            dialog.dismiss()
        }
    }
    fun setOnDismissListener(dismissListener: OnDismissListener){
        this.onDismissListener = dismissListener
    }
    interface OnDismissListener {
        fun onDismiss()
    }
}