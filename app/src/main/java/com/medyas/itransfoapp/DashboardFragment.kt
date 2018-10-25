package com.medyas.itransfoapp

import android.app.ActionBar
import android.content.Context
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.failure
import com.github.kittinunf.result.success
import com.google.firebase.messaging.FirebaseMessaging
import com.joanzapata.iconify.Iconify
import com.joanzapata.iconify.fonts.FontAwesomeModule
import kotlinx.android.synthetic.main.fragment_dashboard.*
import org.json.JSONArray
import org.json.JSONObject
import android.widget.ArrayAdapter
import java.util.ArrayList
import android.widget.AdapterView
import android.widget.TextView
import java.io.IOError


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "uid"
private const val ARG_PARAM2 = "username"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [DashboardFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [DashboardFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class DashboardFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var uid: String? = null
    private var username: String? = null
    private var listener: OnFragmentInteractionListener? = null
    var list: MutableList<String> = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Iconify.with( FontAwesomeModule())
        arguments?.let {
            uid = it.getString(ARG_PARAM1)
            username = it.getString(ARG_PARAM2)
        }
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        progress.visibility = View.VISIBLE

        FuelManager.instance.apply {
            baseHeaders = mapOf("Content-Type" to "application/json")
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                layout.visibility = View.GONE
                progress.visibility = View.VISIBLE
                Log.w("SelectedItem: ", list.get(position))
                val obj = JSONObject()
                try {
                    obj.put("device_ref", list.get(position))
                } catch (e: Exception) {
                }
                getmsg(obj.toString()).execute()
            }
        }

        val obj = JSONObject()
        try {
            obj.put("client_uid", uid)
        } catch (e: Exception) {
        }
        someTask(obj.toString()).execute()
    }



    inner class someTask(obj: String) : AsyncTask<Void, Void, Result<Any, FuelError>>() {
        private var p = obj
        override fun doInBackground(vararg params: Void?): Result<Any, FuelError> {
            val (request, response, result) = "https://itransfo.tk/devicesub/".httpPost().body(p).responseString()

            return result
        }

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun onPostExecute(result: Result<Any, FuelError>) {
            super.onPostExecute(result)
            result.success { data ->
                try {
                    val json = JSONArray(data.toString())
                    button!!.text = "{fa-bolt} ${json.length()}"

                    for( i in 0 until (json.length())) {
                        list!!.add(json[i].toString())
                    }

                    val adapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, list)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner!!.adapter = adapter
                }
                catch(e:KotlinNullPointerException) {

                }

            }
            result.failure { error ->
                Log.w("ResultError: ", error.toString())
                Toast.makeText(context, "Could not Get Data", Toast.LENGTH_LONG)
                progress.visibility = View.GONE
                Snackbar.make(spinner, "Could not get Data", Snackbar.LENGTH_LONG).setAction("Resend", null).show()
            }

            //progress.visibility = View.GONE

        }
    }

    inner class getmsg(obj: String) : AsyncTask<Void, Void, Result<Any, FuelError>>() {
        private var p = obj
        override fun doInBackground(vararg params: Void?): Result<Any, FuelError> {
            val (request, response, result) = "https://itransfo.tk/getmessages/".httpPost().body(p).responseString()

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
                //Log.w("MSG: ", json.toString())
                for( i in 0 until (json.length())) {
                    var data:JSONObject  = json.getJSONObject(i)
                    val msg = TextView(context)
                    msg.setTextColor(resources.getColor(R.color.colorWhite))
                    msg.text = data.getString("msg").toString()
                    msg.layoutParams = ViewGroup.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)
                    msg.setPadding(15, 15, 15, 15)
                    msg.textSize =  20.toFloat()
                    try {
                        layout!!.addView(msg)
                    }
                    catch(e:NullPointerException) {

                    }
                }
            }
            result.failure { error ->
                Log.w("ResultError: ", error.toString())
                Toast.makeText(context, "Could not Get Data", Toast.LENGTH_LONG)
            }
            try {
                progress!!.visibility = View.GONE
                layout!!.visibility = View.VISIBLE
            }
            catch(e:KotlinNullPointerException) {

            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DashboardFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                DashboardFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
