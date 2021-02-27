package com.example.connectuni.QRcode

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.connectuni.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_scan_history.view.*
import kotlinx.android.synthetic.main.headerscanhistory.view.*
import java.io.Serializable

class ScanHistory : Fragment() {

    private var resultList : MutableList<resultData> = ArrayList()
    private lateinit var mView: View
    private lateinit var Database: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mRecyclerView: RecyclerView
    private var resultListType: ResultListType? = null

    enum class ResultListType : Serializable {
        CHECKIN_RESULT, CHECKOUT_RESULT
    }

    companion object {
        private const val ARGUMENT_RESULT_LIST_TYPE = "ArgumentResultType"
        fun newInstance(screenType: ResultListType): ScanHistory {
            val bundle = Bundle()
            bundle.putSerializable(ARGUMENT_RESULT_LIST_TYPE, screenType)
            val fragment = ScanHistory()
                fragment.arguments = bundle
            //synchronized(resultData::class.java) {
            return fragment
            //}
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        Database = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_scan_history, container, false)
        mRecyclerView = mView.findViewById(R.id.scannedHistoryRecyclerView)
        setSwipeRefresh()
        showResults()
        onClicks()
        return mView.rootView
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

            resultListType =
                arguments!!.getSerializable(ARGUMENT_RESULT_LIST_TYPE) as ResultListType

    }


    private fun showResults() {
        when (resultListType) {
            ResultListType.CHECKIN_RESULT -> {
                mView.layoutHeader.titleText.text = getString(R.string.checkin)
                checkinData()
            }
            ResultListType.CHECKOUT_RESULT -> {
                mView.layoutHeader.titleText.text = getString(R.string.checkout)
                checkoutData()
            }
        }
    }

    private fun checkoutData() {

        Database.collection("Users").document(mAuth.currentUser!!.uid).collection("CheckOut")
            .orderBy("Date and Time", Query.Direction.DESCENDING).limit(10)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    resultList.clear()
                    //mView.favouriteIcon.isVisible
                    for (document in snapshot) {
                        Log.d(
                            TAG,
                            "Successfully retrieve favourite scan history result" + document.getData()
                        )
                        //obtain data from firestore and store in class resultData
                        val checkOutResult =
                            resultData(
                                document.data["Date and Time"].toString(),
                                document.data["Check In Location"].toString()
                            )
                        resultList.add(checkOutResult)
                    }
                    //initialise recycler view layout
                    val linearLayoutManager = LinearLayoutManager(context)
                    linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
                    mRecyclerView.layoutManager = linearLayoutManager

                    // show data
                    val adapter =
                        ScannedResultListAdapter(
                            resultList as ArrayList<resultData>,
                            context
                        )
                    mRecyclerView.adapter = adapter
                    adapter.notifyDataSetChanged()
                } else {
                    Log.d(TAG, "Failed to retrieve favourite scan history result")
                }

            }
    }

    private fun checkinData() {

        Database.collection("Users").document(mAuth.currentUser!!.uid).collection("CheckIn")
            .orderBy("Date and Time", Query.Direction.DESCENDING).limit(10)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    resultList.clear()
                    for (document in snapshot) {
                        Log.d(TAG, "Successfully retrieve scan history result" + document.getData())
                        //obtain data from firestore and store in class resultData
                        val checkinResult =
                            resultData(
                                document.data["Date and Time"].toString(),
                                document.data["Check In Location"].toString()
                            )
                        resultList.add(checkinResult)
                    }
                    //initialise recycler view layout
                    val linearLayoutManager = LinearLayoutManager(context)
                    linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
                    mRecyclerView.layoutManager = linearLayoutManager

                    // show data
                    val adapter =
                        ScannedResultListAdapter(
                            resultList as ArrayList<resultData>,
                            context
                        )
                    mRecyclerView.adapter = adapter
                    adapter.notifyDataSetChanged()
                } else {
                    Log.d(TAG, "Failed to retrieve scan history result")
                }

            }
    }

    private fun setSwipeRefresh() {
        mView.swipeRefresh.setOnRefreshListener {
            mView.swipeRefresh.isRefreshing = false
            //updateData()
        }
    }
    // to clear scan result list in the recycler view
    private fun onClicks() {
        mView.layoutHeader.removeAll.setOnClickListener {
            AlertDialog.Builder(context!!,
                R.style.CustomAlertDialog
            )
                .setTitle(getString(R.string.clear_all))
                .setMessage(getString(R.string.clear_all_result))
                .setPositiveButton(getString(R.string.clear)) { _, _ ->
                    resultList.clear()
                    mRecyclerView.adapter!!.notifyDataSetChanged()

                }.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                    dialog.cancel()
                }.show()
        }
    }
}



