package com.example.myapplication

import android.Manifest
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.amplifyframework.auth.AuthChannelEventName
import com.amplifyframework.auth.AuthUser
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.generated.model.Points
import com.amplifyframework.hub.HubChannel
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import kotlin.text.Charsets.UTF_8

class MainActivity : AppCompatActivity() {

    private val REQUEST_CODE_REQUIRED_PERMISSIONS = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        val currentUser: String = Amplify.Auth.currentUser.username

        when {
            intent?.action == Intent.ACTION_SEND -> {
                if(currentUser != null){
                    if ("text/plain" == intent.type) {
                        intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
                            val pointsData = it
                            val database = DatabaseAccess()
                            database.handleReceive(pointsData) // Handle text being sent
                            tasksManager(currentUser)
                        }
                    }
                }
                else{
                    val send = Intent(this, LoginActivity::class.java).apply{
                        if ("text/plain" == intent.type) {
                            intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
                                putExtra("Points Data", it)
                            }
                        }
                    }
                    startActivity(send)
                    finish()
                }
            }
            else -> {
                // Handle other intents, such as being started from the home screen
                if(currentUser != null) {
                    tasksManager(currentUser)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE_REQUIRED_PERMISSIONS
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val errMsg = "Cannot start without required permissions"
        if (requestCode == REQUEST_CODE_REQUIRED_PERMISSIONS) {
            grantResults.forEach {
                if (it == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, errMsg, Toast.LENGTH_LONG).show()
                    finish()
                    return
                }
            }
            recreate()
        }
    }

    fun tasksManager(currentUser: String) {
        val connectionsClient = Nearby.getConnectionsClient(this)

        val pointsInput: EditText = findViewById(R.id.points_maker)


        var friendCodeName: String? = null
        var friendEndpointId: String? = null
        var pointsSend: String? = null
        val myCodeName = currentUser

        val payloadCallback: PayloadCallback = object : PayloadCallback() {
            override fun onPayloadReceived(endpointId: String, payload: Payload) {
                println("Payload received")
                payload.asBytes()?.let {
                    pointsSend = String(it, UTF_8)
                    val database = DatabaseAccess()
                    database.handleReceive(pointsSend!!)
                }
            }

            override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
                // Determines the winner and updates game state/UI after both players have chosen.
                // Feel free to refactor and extract this code into a different method
                if (update.status == PayloadTransferUpdate.Status.SUCCESS) {
                    println("success")
                }
            }
        }

        val connectionLifecycleCallback = object:ConnectionLifecycleCallback() {
            override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
                Log.d(TAG, "onConnectionInitiated: accepting connection")
                //connectionsClient.acceptConnection(endpointId, payloadCallback)
                //friendCodeName = connectionInfo.endpointName
                Handler(Looper.getMainLooper()).post {
                    AlertDialog.Builder(this@MainActivity)
                        .setTitle("Accept connection to ${connectionInfo.endpointName}")
                        .setMessage("Confirm the code matches on both devices: ${connectionInfo.authenticationDigits}")
                        .setPositiveButton("Accept", DialogInterface.OnClickListener { dialog: DialogInterface, id: Int ->
                            connectionsClient.acceptConnection(endpointId, payloadCallback)
                            friendCodeName = connectionInfo.endpointName
                        })
                        .setNegativeButton(
                            android.R.string.cancel,
                            DialogInterface.OnClickListener { dialog: DialogInterface, id: Int ->
                                connectionsClient.rejectConnection(endpointId)
                            })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show()
                }
            }

            override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
                // you can do if(result.status.isSuccess){}else{} or check the statusCode with "when"

                when (result.status.statusCode) {
                    ConnectionsStatusCodes.STATUS_OK -> {
                        // We're connected! Can now start sending and receiving data.
                        println("Connected")
                        // Once you have successfully connected to your friends' devices, you can leave
                        // discovery mode so you can stop discovering other devices
                        connectionsClient.stopDiscovery()
                        // if you were advertising, you can stop as well
                        connectionsClient.stopAdvertising()

                        friendEndpointId = endpointId
                    }
                    ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
                        println("Connection rejection")
                        // The connection was rejected by one or both sides.
                    }
                    ConnectionsStatusCodes.STATUS_ERROR -> {
                        println("Connection error")
                        // The connection broke before it was able to be accepted.
                    }
                    else -> {
                        // Unknown status code
                        println("Unknown error")
                    }
                }
            }

            override fun onDisconnected(endpointId: String) {
                Log.d(TAG, "onDisconnected: from friend")
                // perform necessary clean up
            }
        }

        findViewById<Button>(R.id.connect_button).setOnClickListener {
            startAdvertising(connectionsClient, myCodeName, connectionLifecycleCallback)
            startDiscovery(connectionsClient, myCodeName, connectionLifecycleCallback)
        }

        findViewById<Button>(R.id.disconnect_button).setOnClickListener {
            connectionsClient.stopDiscovery()
            connectionsClient.stopAdvertising()
            friendEndpointId?.let { connectionsClient.disconnectFromEndpoint(it) }
        }

        findViewById<Button>(R.id.send_button).setOnClickListener {
            val input: String = pointsInput.text.toString()
            if (input.matches(Regex("^[0-9]+$"))) {
                val pointsToSend: Int = input.toInt()
                val pointsData = "$pointsToSend,$currentUser"
                if (friendEndpointId != null){
                    sharePoints(connectionsClient, pointsData, friendEndpointId!!)
                }
            }
        }


        findViewById<Button>(R.id.share_button).setOnClickListener {
            val input: String = pointsInput.text.toString()
            if (input.matches(Regex("^[0-9]+$"))) {
                val pointsToSend: Int = input.toInt()
                sendPoints(pointsToSend, currentUser)
            }
        }


        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            Amplify.Auth.signOut(
                { println("Sign out success") },
                { Log.e("AuthQuickstart", "Sign out failed", it) })
            Amplify.Hub.subscribe(
                HubChannel.AUTH,
                {
                    // Listen for sign out events.
                    it.name.equals(AuthChannelEventName.SIGNED_OUT.toString())
                },
                {
                    // When one arrives, clear the DataStore.
                    Amplify.DataStore.clear(
                        { Log.i("MyAmplifyApp", "DataStore is cleared") },
                        { Log.e("MyAmplifyApp", "Failed to clear DataStore") }
                    )
                }
            )
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        findViewById<Button>(R.id.show_table).setOnClickListener {
            val intent = Intent(this@MainActivity, ListView::class.java)
            startActivity(intent)

            /*val pointsList = mutableListOf<Points>()

            Amplify.DataStore.query(Points::class.java, Where.matches(User.ID.eq(currentUser)),
                { matches ->
                    while (matches.hasNext()) {
                        val points = matches.next()
                        pointsList.add(points)
                    }
                    println("Query Success")
                    println(pointsList)
                    showTable(pointsList)
                },
                { println("Query Failure")
                    showTable(pointsList) }
            )*/
        }
    }

    private fun sendPoints(points: Int, sender: String?)
    {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "$points,$sender")
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    private fun startAdvertising(connectionsClient: ConnectionsClient, myCodeName: String, connectionLifecycleCallback: ConnectionLifecycleCallback){
        // Note: Advertising may fail. To keep this demo simple, we don't handle failures.
        // Also we will demonstrate connectionLifecycleCallback later.
        connectionsClient.startAdvertising(myCodeName, packageName, connectionLifecycleCallback,
            AdvertisingOptions.Builder().setStrategy(Strategy.P2P_STAR).build())
    }

    private fun startDiscovery(connectionsClient: ConnectionsClient, myCodeName: String, connectionLifecycleCallback: ConnectionLifecycleCallback){
        val endpointDiscoveryCallback: EndpointDiscoveryCallback =
            object:EndpointDiscoveryCallback(){
                override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
                    connectionsClient.requestConnection(myCodeName, endpointId, connectionLifecycleCallback)
                }

                override fun onEndpointLost(endpointId: String) {
                    Log.d(TAG, "onEndpointLost")
                }

            }
        // Note: Discovery may fail. To keep this demo simple, we don't handle failures.
        connectionsClient.startDiscovery(packageName,endpointDiscoveryCallback,
            DiscoveryOptions.Builder().setStrategy(Strategy.P2P_STAR).build())
    }


    private fun sharePoints(connectionsClient: ConnectionsClient, pointsData: String, friendEndpointId: String){
        connectionsClient.sendPayload(
            friendEndpointId, Payload.fromBytes(pointsData.toByteArray(UTF_8)))
    }


    private fun showTable(pointsList: MutableList<Points>)
    {
        val intent = Intent(this@MainActivity, ListView::class.java)
        startActivity(intent)

        //val recyclerview = findViewById<RecyclerView>(R.id.recyclerview)
        //recyclerview.layoutManager = LinearLayoutManager(this)
        //val adapter = RecyclerAdapter(pointsList)
        //recyclerview.adapter = adapter

        /*val layout = findViewById<RelativeLayout>(R.id.points_layout)

        println("start success")

        val tl = TableLayout(this)
        val lp = TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        tl.apply {
            layoutParams = lp
            isShrinkAllColumns = true
        }

        val hr = TableRow(this)
        hr.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)

        val h1 = TextView(this)
        h1.apply {
            layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT)
            text = "From"
            setTextColor(Color.BLACK)
        }
        hr.addView(h1)

        val h2 = TextView(this)
        h2.apply {
            layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT)
            text = "Total Points"
            setTextColor(Color.BLACK)
        }
        hr.addView(h2)

        //val h3 = "Description"
        //h3.text = points.sender
        //h3.setTextColor(Color.BLACK)
        //hr.addView(h3)

        tl.addView(hr)

        for (points in pointsList){
            /*val sender = pointsList[i].sender
            val totalPoints = pointsList[i].totalPoints.toString()
            val description = pointsList[i].description
            addToTable(sender, totalPoints, description, t1)*/

            println(points.sender.toString())
            println(points.totalPoints.toString())
            val tr = TableRow(this)
            tr.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)

            val c1 = TextView(this)
            c1.apply {
                layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT)
                text = points.sender
                setTextColor(Color.BLACK)
            }
            tr.addView(c1)
            val c2 = TextView(this)
            c2.apply {
                layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT)
                text = points.totalPoints.toString()
                setTextColor(Color.BLACK)
            }
            tr.addView(c2)

            //val c3 = TextView(this)
            //c3.text = pointsList[i].description
            //c3.setTextColor(Color.BLACK)
            //tr.addView(c3)

            println("success$points")
            tl.addView(tr)
            println(c1.text.toString())
            println(c2.text.toString())
        }

        layout.addView(tl)
        println("yay")

        //tl.visibility = View.INVISIBLE
        //tl.visibility = View.VISIBLE
        findViewById<Button>(R.id.hide_table).visibility = View.VISIBLE
        findViewById<Button>(R.id.show_table).visibility = View.GONE

        */
    }

    /*private fun addToTable(sender: String, totalPoints: String, description: String?, t1: TableLayout)
    {
        val tr = TableRow(this)
        val c1 = TextView(this)
        val c2 = TextView(this)
        val c3 = TextView(this)
        println("success")
        c1.text = sender
        c1.setTextColor(Color.BLACK)
        tr.addView(c1)
        c2.text = totalPoints
        c2.setTextColor(Color.BLACK)
        tr.addView(c2)
        c3.text = description
        c3.setTextColor(Color.BLACK)
        tr.addView(c3)
        t1.addView(tr)
    }*/

}