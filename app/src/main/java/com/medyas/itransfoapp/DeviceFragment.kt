package com.medyas.itransfoapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.recyclerview.R.attr.layoutManager
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.*
import android.view.View.*
import android.widget.ProgressBar
import android.widget.Toast
import com.github.kittinunf.fuel.android.extension.responseJson
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.failure
import com.github.kittinunf.result.success
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.joanzapata.iconify.Iconify
import com.joanzapata.iconify.fonts.FontAwesomeModule
import kotlinx.android.synthetic.main.fragment_device.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.ArrayList


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "uid"
private const val ARG_PARAM2 = "username"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [DeviceFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [DeviceFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class DeviceFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var uid: String? = null
    private var username: String? = null
    private var listener: OnFragmentInteractionListener? = null

    lateinit var recyclerView: RecyclerView
    lateinit var viewAdapter: DeviceAdapter
    lateinit var viewManager: RecyclerView.LayoutManager
    var ml: MutableList<DeviceList> = ArrayList<DeviceList>()
    var progress:ProgressBar? = null
    val obj = JSONObject()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Iconify.with( FontAwesomeModule())
        arguments?.let {
            uid = it.getString(ARG_PARAM1)
            username = it.getString(ARG_PARAM2)
        }
        setHasOptionsMenu(true)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //(activity as AppCompatActivity).menuInflater.inflate(R.menu.device_info_menu, menu)
        inflater.inflate(R.menu.device_list_menu, menu)

        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView
        searchView.queryHint = "Filter By Name Or Company"

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(query: String): Boolean {
                viewAdapter.filter(query)
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                viewAdapter.filter(query)
                return false
            }

        })


        super.onCreateOptionsMenu(menu,inflater)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        (activity as AppCompatActivity).setSupportActionBar(this.toolbar)
        progress = this.progressBar
        progress!!.visibility = VISIBLE

        progressTask.run()

    }

    private val progressTask = Runnable {
        try {
            obj.put("client_uid", uid)
        } catch (e: Exception) {
            Toast.makeText(context, "Could not send data", Toast.LENGTH_LONG).show()
        }

        FuelManager.instance.apply {
            basePath = "https://itransfo.tk"
            baseHeaders = mapOf("Content-Type" to "application/json")
        }

        viewManager = LinearLayoutManager(context)
        viewAdapter = DeviceAdapter(ml)

        recyclerView = (this.my_recycler_view).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter

        }
        someTask(obj.toString()).execute()

        activity!!.runOnUiThread(Runnable {
            //postData(obj.toString())

        })
    }

    inner class someTask(obj: String) : AsyncTask<Void, Void, Result<Any, FuelError>>() {
        private var p = obj
        override fun doInBackground(vararg params: Void?): Result<Any, FuelError> {
            val (request, response, result) = "https://itransfo.tk/getUserDevicesInfo/".httpPost().body(p).responseString()
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
                for (i in 0 until (json.length())) {
                    try {
                        var obj:JSONObject = json.getJSONObject(i)
                        var device  = DeviceList(obj.getString("device_name"), obj.getString("company_name"), obj.getString("device_ref"), obj.getString("device_uid"))
                        ml.add(device)
                    }
                    catch (e :RuntimeException) {
                        Toast.makeText(context, "Could not get data", Toast.LENGTH_LONG)
                    }
                }

                viewAdapter.notifyDataSetChanged()
                viewAdapter.filter("")
                progress!!.visibility = View.GONE
            }
            result.failure { error ->
                Log.w("ResultError: ", error.toString())
                progress!!.visibility = View.GONE
                Snackbar.make(my_recycler_view, "Could not get Data", Snackbar.LENGTH_LONG).setAction("Resend", null).show()
            }
        }
    }

    private fun postData(p:String?) {

        "/getUserDevices/".httpPost().body(p!!)
                .responseString { request, _, result ->
                    result.failure { error ->
                        Toast.makeText(context, "Could not Get Data!", Toast.LENGTH_LONG)
                    }
                    result.success { data ->
                        val json = JSONArray(data)
                        for (i in 0 until (json.length())) {
                            var obj:JSONObject = json.getJSONObject(i)
                            var device  = DeviceList(obj.getString("device_name"), obj.getString("company_name"), obj.getString("device_ref"), obj.getString("device_uid"))
                            ml.add(device)
                        }
                    }
                    viewAdapter.notifyDataSetChanged()
                    viewAdapter.filter("")
                    progress!!.visibility = View.GONE
                }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_device, container, false)
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
         * @return A new instance of fragment DeviceFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                DeviceFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
