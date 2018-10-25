package com.medyas.itransfoapp

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.failure
import com.github.kittinunf.result.success
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.iid.FirebaseInstanceId

import kotlinx.android.synthetic.main.activity_device_settings.*
import kotlinx.android.synthetic.main.content_device_settings.*
import org.json.JSONArray
import org.json.JSONObject

class DeviceSettings : AppCompatActivity() {

    private var ref: String = ""
    private var mAuth: FirebaseAuth? = null
    private var client: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_settings)
        setSupportActionBar(toolbar)
        if (supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true);
            supportActionBar?.setDisplayShowHomeEnabled(true);
        }

        mAuth = FirebaseAuth.getInstance()
        client = mAuth!!.currentUser

        if (savedInstanceState == null) {
            val extras = intent.extras
            if (extras == null) {
                ref = ""
            } else {
                ref = extras.getString("device_ref")
            }
        } else {
            try {
                ref = savedInstanceState.getSerializable("device_ref").toString()
            }
            catch(e:RuntimeException) {
                onBackPressed()
            }
        }

        supportActionBar!!.title = "Device Parameters"

        val obj = JSONObject()
        try {
            obj.put("device_ref", ref)
        } catch (e: Exception) {
            Toast.makeText(this, "Could not send data", Toast.LENGTH_LONG).show()
        }
        getParamTask(obj.toString()).execute();


        save_param.setOnClickListener {
            val obj = JSONObject()
            try {
                obj.put("device_ref", ref)
                obj.put("pri_voltage", pri_voltage.text)
                obj.put("sec_voltage", sec_voltage.text)
                obj.put("pri_current", pri_current.text)
                obj.put("sec_current", sec_current.text)
                obj.put("internal_temp", internal_temp.text)
                obj.put("external_temp", external_temp.text)
                obj.put("uid", client!!.uid)
            } catch (e: Exception) {
                Toast.makeText(this, "Could not send data", Toast.LENGTH_LONG).show()
            }
            setParamTask(obj.toString()).execute()
        }

    }

    inner class getParamTask(obj: String) : AsyncTask<Void, Void, Result<Any, FuelError>>() {
        private var p = obj
        override fun doInBackground(vararg params: Void?): Result<Any, FuelError> {
            val (request, response, result) = "https://itransfo.tk/getparameters/".httpPost().body(p).responseString()
            return result
        }

        override fun onPreExecute() {
            super.onPreExecute()
            scrollView.visibility = View.GONE
            progress.visibility = View.VISIBLE
        }

        override fun onPostExecute(result: Result<Any, FuelError>) {
            super.onPostExecute(result)
            result.success { data ->
                val obj = JSONArray(data.toString())
                val json = JSONObject(obj[0].toString());
                pri_voltage.text = Editable.Factory.getInstance().newEditable(json.get("pri_voltage").toString())
                sec_voltage.text = Editable.Factory.getInstance().newEditable(json.get("sec_voltage").toString())
                pri_current.text = Editable.Factory.getInstance().newEditable(json.get("pri_current").toString())
                sec_current.text = Editable.Factory.getInstance().newEditable(json.get("sec_current").toString())
                internal_temp.text = Editable.Factory.getInstance().newEditable(json.get("internal_temp").toString())
                external_temp.text = Editable.Factory.getInstance().newEditable(json.get("external_temp").toString())
            }
            result.failure { error ->
                Log.w("ResultError: ", error.toString())
            }
            progress.visibility = View.GONE
            scrollView.visibility = View.VISIBLE
        }
    }

    inner class setParamTask(obj: String) : AsyncTask<Void, Void, Result<Any, FuelError>>() {
        private var p = obj
        override fun doInBackground(vararg params: Void?): Result<Any, FuelError> {
            val (request, response, result) = "https://itransfo.tk/updateprameters/".httpPost().body(p).responseString()
            return result
        }

        override fun onPreExecute() {
            super.onPreExecute()
            scrollView.visibility = View.GONE
            progress.visibility = View.VISIBLE
        }

        override fun onPostExecute(result: Result<Any, FuelError>) {
            super.onPostExecute(result)
            progress.visibility = View.GONE
            scrollView.visibility = View.VISIBLE

            result.success { data ->
                Log.w("params data", data.toString())
                Snackbar.make(scrollView, "New Param Saved", Snackbar.LENGTH_LONG)
            }
            result.failure { error ->
                Log.w("ResultError: ", error.toString())
                Snackbar.make(scrollView, "Could not Save Param", Snackbar.LENGTH_LONG)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        //menuInflater.inflate(R.menu.device_info_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Toast.makeText(this, item.itemId, Toast.LENGTH_LONG)
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }

        }

        return super.onOptionsItemSelected(item)
    }

}
