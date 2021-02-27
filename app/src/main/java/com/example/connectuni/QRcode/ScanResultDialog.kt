package com.example.connectuni.QRcode

import android.app.Dialog
import android.content.Context
import android.icu.util.Calendar
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.connectuni.R
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