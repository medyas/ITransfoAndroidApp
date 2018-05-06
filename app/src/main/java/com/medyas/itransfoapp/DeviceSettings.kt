package com.medyas.itransfoapp

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.failure
import com.github.kittinunf.result.success

import kotlinx.android.synthetic.main.activity_device_settings.*
import org.json.JSONArray
import org.json.JSONObject

class DeviceSettings : AppCompatActivity() {

    private var uid: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_settings)
        setSupportActionBar(toolbar)
        if (supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true);
            supportActionBar?.setDisplayShowHomeEnabled(true);
        }

        if (savedInstanceState == null) {
            val extras = intent.extras
            if (extras == null) {
                uid = ""
            } else {
                uid = extras.getString("device_uid")
            }
        } else {
            try {
                uid = savedInstanceState.getSerializable("device_uid").toString()
            }
            catch(e:RuntimeException) {
                onBackPressed()
            }
        }

    }

    inner class someTask(obj: String) : AsyncTask<Void, Void, Result<Any, FuelError>>() {
        private var p = obj
        override fun doInBackground(vararg params: Void?): Result<Any, FuelError> {
            val (request, response, result) = "".httpPost().body(p).responseString()
            return result
        }

        override fun onPreExecute() {
            super.onPreExecute()
            // ...
        }

        override fun onPostExecute(result: Result<Any, FuelError>) {
            super.onPostExecute(result)
            result.success { data ->
                val json = JSONObject(data.toString())
                Log.w("subscribed to", json.toString())
            }
            result.failure { error ->
                Log.w("ResultError: ", error.toString())
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
