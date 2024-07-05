package com.example.verifikasiemail

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Datawarga : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var customListAdapter: CustomListAdapter
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_datawarga)

        recyclerView = findViewById(R.id.rv_users)
        recyclerView.layoutManager = LinearLayoutManager(this)
        customListAdapter = CustomListAdapter()
        recyclerView.adapter = customListAdapter

        databaseReference = FirebaseDatabase.getInstance().reference.child("reports")
        fetchReports()
    }

    private fun fetchReports() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val reports = mutableListOf<Report>()
                for (dataSnapshot in snapshot.children) {
                    val report = dataSnapshot.getValue(Report::class.java)
                    if (report != null) {
                        reports.add(report)
                    }
                }
                customListAdapter.submitList(reports)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Datawarga", "Failed to read data", error.toException())
            }
        })
    }
}
