package com.medyas.itransfoapp


import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.*
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.failure
import com.github.kittinunf.result.success
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.joanzapata.iconify.Iconify
import com.joanzapata.iconify.fonts.FontAwesomeModule
import kotlinx.android.synthetic.main.activity_dashboard.view.*
import kotlinx.android.synthetic.main.fragment_user.*
import org.json.JSONArray
import org.json.JSONObject



class Dashboard : AppCompatActivity() , DashboardFragment.OnFragmentInteractionListener, DeviceFragment.OnFragmentInteractionListener, UserFragment.OnFragmentInteractionListener{

    private var mAuth: FirebaseAuth? = null
    private var client: FirebaseUser? = null
    private var refreshedToken: String? = null
    private val obj = JSONObject()
    private var navigation: BottomNavigationView? = null
    private var username:String=""
    private var uid :String =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Iconify.with( FontAwesomeModule())
        setContentView(R.layout.activity_dashboard)

        navigation = findViewById<View>(R.id.navigation) as BottomNavigationView
        navigation!!.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        mAuth = FirebaseAuth.getInstance()
        refreshedToken = FirebaseInstanceId.getInstance().token
        client = mAuth!!.currentUser
        username = client!!.displayName.toString()
        uid = client!!.uid

        navigation!!.getMenu().findItem(R.id.navigation_dashboard).setChecked(true)
        val dashboardFragment = DashboardFragment.newInstance(uid, username)
        openFragment(dashboardFragment)

        progressTask.run()
    }

    private val progressTask = Runnable {

        navigation!!.getMenu().findItem(R.id.navigation_user).title = username
        try {
            obj.put("client_uid", uid)
            obj.put("tokenId", refreshedToken)
        } catch (e: Exception) {
            Toast.makeText(this, "Could not send data", Toast.LENGTH_LONG).show()
        }

        FuelManager.instance.apply {
            basePath = "https://itransfo.tk"
            baseHeaders = mapOf("Content-Type" to "application/json")
        }

        this!!.runOnUiThread(Runnable {
            FirebaseMessaging.getInstance().subscribeToTopic("all")
            subscribeClient(uid, obj.toString())
        })
    }

    private fun subscribeClient(uid: String, body:String) {
        val obj = JSONObject()
        try {
            obj.put("client_uid", uid)
        } catch (e: Exception) {
            Toast.makeText(this, "Could not send data", Toast.LENGTH_LONG).show()
        }
        someTask(obj.toString(), body).execute()
    }

    inner class someTask(obj: String, txt:String) : AsyncTask<Void, Void, Result<Any, FuelError>>() {
        private var p = obj
        private var body = txt
        override fun doInBackground(vararg params: Void?): Result<Any, FuelError> {
            postData(body)
            val (request, response, result) = "/devicesub/".httpPost().body(p).responseString()
            return result
        }

        override fun onPreExecute() {
            super.onPreExecute()
            // ...
        }

        override fun onPostExecute(result: Result<Any, FuelError>) {
            super.onPostExecute(result)
            result.success { data ->
                val json = JSONArray(data.toString())
                for( i in 0 until (json.length())) {
                    FirebaseMessaging.getInstance().subscribeToTopic(json[i].toString())
                    Log.w("subscribed to", json[i].toString())
                }
            }
            result.failure { error ->
                Log.w("ResultError: ", error.toString())
            }
        }
    }



    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_dashboard -> {
                val dashboardFragment = DashboardFragment.newInstance(uid, username)
                openFragment(dashboardFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_devices -> {
                val deviceFragment = DeviceFragment.newInstance(uid, username)
                openFragment(deviceFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_user -> {
                val userFragment = UserFragment.newInstance(uid, username)
                openFragment(userFragment)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.menucontainer, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onFragmentInteraction(uri: Uri) {

    }

    private fun postData(p:String?) {
        Log.w("Sending data", "sending ")
        "/setToken/".httpPost().body(p!!)
                .responseString { request, _, result ->
                    Log.w("Request", result.toString())
                    //update(result)
                }
    }


    override fun onPause() {
        super.onPause()
    }


    override fun onDestroy() {
        super.onDestroy()
    }


    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }



}
