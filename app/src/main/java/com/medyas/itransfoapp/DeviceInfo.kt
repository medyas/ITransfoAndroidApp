package com.medyas.itransfoapp

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.failure
import com.github.kittinunf.result.success
import kotlinx.android.synthetic.main.activity_device_info.*
import kotlinx.android.synthetic.main.content_device_info.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class DeviceInfo : AppCompatActivity() {
    private var ref: String = ""
    private var company: String = ""
    private var name:String = ""


    private var progress:ProgressBar? = null
    private var scroll:ScrollView? = null
    private var taskRunner:Timer = Timer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_info)
        setSupportActionBar(this.toolbar)
        if (supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true);
            supportActionBar?.setDisplayShowHomeEnabled(true);
        }

        if (savedInstanceState == null) {
            val extras = intent.extras
            if (extras == null) {
            } else {
                ref = extras.getString("device_ref")
                company = extras.getString("company")
                name = extras.getString("device_name")
            }
        } else {
            try {
                ref = savedInstanceState.getSerializable("device_ref").toString()
                ref = savedInstanceState.getSerializable("company").toString()
                name = savedInstanceState.getSerializable("device_name").toString()
            }
            catch(e:RuntimeException) {
                onBackPressed()
            }
        }

        progress = this.progressBar
        scroll = this.scrollView

        supportActionBar!!.title = name

        val obj = JSONObject()
        try {
            obj.put("device_ref", ref)
        } catch (e: Exception) {
            Toast.makeText(this, "Could not send data", Toast.LENGTH_LONG).show()
        }

        taskRunner.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                var task:someTask? = null
                task = someTask(obj.toString())
                task!!.execute()
            }
        }, 0, 5000)

        this.r.text = ref
        this.n.text = name
        this.c.text = company

        scroll!!.visibility = View.GONE
        progress!!.visibility = View.VISIBLE

            //onBackPressed()
            /*Toast.makeText(this, uid, Toast.LENGTH_LONG)
            Snackbar.make(view, uid, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()*/

    }
    inner class someTask(obj: String) : AsyncTask<Void, Void, Result<Any, FuelError>>() {
        private var p = obj
        override fun doInBackground(vararg params: Void?): Result<Any, FuelError> {
            val (request, response, result) = "https://itransfo.tk/getlatestdata/".httpPost().body(p).responseString()
            return result
        }

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun onPostExecute(result: Result<Any, FuelError>) {
            super.onPostExecute(result)
            scroll!!.visibility = View.GONE
            progress!!.visibility = View.VISIBLE
            result.success { data ->
                val json = JSONObject(data.toString())
                editText11.text = json.getString("pri_voltage_p1")
                editText12.text = json.getString("pri_voltage_p2")
                editText13.text = json.getString("pri_voltage_p3")
                editText21.text = json.getString("sec_voltage_p1")
                editText22.text = json.getString("sec_voltage_p2")
                editText23.text = json.getString("sec_voltage_p3")
                editText31.text = json.getString("pri_current_p1")
                editText32.text = json.getString("pri_current_p2")
                editText33.text = json.getString("pri_current_p3")
                editText321.text = json.getString("sec_current_p1")
                editText322.text = json.getString("sec_current_p2")
                editText323.text = json.getString("sec_current_p3")
                editText41.text = json.getString("internal_temp")
                editText42.text = json.getString("external_temp")
                editText51.text = json.getString("gas")
                editText61.text = json.getString("pressure")
                editText71.text = json.getString("timestamp")
            }
            result.failure { error ->
                Log.w("ResultError: ", error.toString())
            }
            progress!!.visibility = View.GONE
            scroll!!.visibility = View.VISIBLE

        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.device_info_menu, menu)
        
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Toast.makeText(this, item.itemId, Toast.LENGTH_LONG)
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.action_settings -> {
                val intent = Intent(this@DeviceInfo, DeviceSettings::class.java)
                intent.putExtra("device_ref", ref)
                startActivity(intent)
                return true
            }

        }

        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        super.onPause()
        taskRunner.cancel()
        Log.w("Canceled", "Task canceled !")
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
