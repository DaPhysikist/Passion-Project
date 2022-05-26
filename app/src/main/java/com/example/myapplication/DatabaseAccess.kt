package com.example.myapplication

import android.content.Intent
import android.util.Log
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.model.query.Where
import com.amplifyframework.datastore.generated.model.Points
import com.amplifyframework.datastore.generated.model.User
import kotlinx.coroutines.*

class DatabaseAccess {
    fun createUser(name: String, email: String, birthday: String){
        val userID: String = Amplify.Auth.currentUser.username.toString()

        val user = User.builder()
            .id(userID)
            .name(name)
            .email(email)
            .birthday(birthday)
            .build()

        Amplify.DataStore.save(user,
            { println("Create Success") },
            { println("Error creating user") }
        )

        //val data = Database()
        //data.putItemInTable("User-Information", "userid", username, "Name", name, "Email", email, "Birthday", birthday)
        //showMessage("Information added")
    }

    fun handleReceive(pointsData: String) {
        println("item received")
        val delim = ","
        val list = pointsData.split(delim)
        val numPoints = list.elementAt(0).toInt()
        val sender: String = list.elementAt(1)
        println("123$numPoints$sender")
        val userID = Amplify.Auth.currentUser.username.toString()
        var sameSender: String? = null

        Amplify.DataStore.query(Points::class.java, Where.matches(User.ID.eq(userID)),
            { dupSender ->
                while (dupSender.hasNext()) {
                    val points = dupSender.next()
                    if (points.sender.equals(sender))
                    {
                        sameSender = points.id
                    }
                }
                findUser(numPoints, sender, userID, sameSender)
            },
            { findUser(numPoints, sender, userID, sameSender) }
        )
    }

    private fun findUser(numPoints: Int, sender: String, userID: String, sameSender: String?){
        println("find user")
        Amplify.DataStore.query(User::class.java,
            Where.matches(User.ID.eq(userID)),
            { wantedUser ->
                while (wantedUser.hasNext()) {
                    val storedUser = wantedUser.next()
                    processPoints(storedUser, numPoints, sender, sameSender)
                }
            },
            { Log.e("MyAmplifyApp", "Query failed", it) }
        )
    }

    private fun processPoints(storedUser: User, numPoints: Int, sender: String, sameSender: String?){
        println("process points")
        var prevPoints = 0
        if (sameSender != null){
            Amplify.DataStore.query(Points::class.java, Where.id(sameSender),
                {
                    while (it.hasNext()) {
                        val points = it.next()
                        prevPoints = points.totalPoints

                        Amplify.DataStore.delete(points,
                            { println("delete completed")},
                            { println("Delete failed") }
                        )
                    }
                    addPoints(storedUser, numPoints, prevPoints, sender)
                },
                { Log.e("MyAmplifyApp", "Query failed", it) }
            )
        }
        else
        {
            addPoints(storedUser, numPoints, prevPoints, sender)
        }
    }

    private fun addPoints(storedUser: User, numPoints: Int, prevPoints: Int, sender: String){
        println("add points")
        val pointsSend = Points.builder()
            .user(storedUser)
            .totalPoints(numPoints + prevPoints)
            .sender(sender)
            .build()

        Amplify.DataStore.save(pointsSend, {println("Received Points")}, {println("Failed to receive")})
    }
}