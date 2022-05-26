package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.model.query.Where
import com.amplifyframework.datastore.generated.model.Points
import com.amplifyframework.datastore.generated.model.User

class ListView : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        val pointsList = mutableListOf<Points>()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_view)
        supportActionBar?.hide()
        val recyclerview = findViewById<RecyclerView>(R.id.recyclerview)
        recyclerview.layoutManager = LinearLayoutManager(this)
        querydata(pointsList, recyclerview)
        hidetable()
    }

    private fun querydata(pointsList: MutableList<Points>, recyclerview: RecyclerView){
        val currentUser: String? = Amplify.Auth.currentUser.username

        Amplify.DataStore.query(Points::class.java, Where.matches(User.ID.eq(currentUser)),
            { matches ->
                while (matches.hasNext()) {
                    val points = matches.next()
                    pointsList.add(points)
                }
                println("Query Success")
                println(pointsList)
                displaydata(pointsList, recyclerview)
            },
            { println("Query Failure") }
        )
    }

    private fun displaydata(pointsList: MutableList<Points>, recyclerview: RecyclerView){
        Handler(Looper.getMainLooper()).post {
            val adapter = RecyclerAdapter(pointsList)
            recyclerview.adapter = adapter
        }
    }

    private fun hidetable(){
        println("hi")

        findViewById<Button>(R.id.hide_table).setOnClickListener {
            println("hello")
            val intent = Intent(this@ListView, MainActivity::class.java)
            startActivity(intent)
        }
    }
}