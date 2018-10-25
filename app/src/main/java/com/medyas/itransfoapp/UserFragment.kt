package com.medyas.itransfoapp

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import android.content.Intent
import android.support.design.widget.Snackbar
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.httpPost
import com.google.firebase.auth.FirebaseUser
import com.joanzapata.iconify.Iconify
import com.joanzapata.iconify.fonts.FontAwesomeModule
import kotlinx.android.synthetic.main.fragment_user.*
import org.json.JSONObject
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.UserProfileChangeRequest






// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "uid"
private const val ARG_PARAM2 = "username"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [UserFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [UserFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class UserFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var uid: String? = null
    private var username: String? = null
    private var listener: OnFragmentInteractionListener? = null

    private var mAuth: FirebaseAuth? = null
    private var client: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Iconify.with( FontAwesomeModule())
        arguments?.let {
            uid = it.getString(ARG_PARAM1)
            username = it.getString(ARG_PARAM2)
        }

        mAuth = FirebaseAuth.getInstance()
        client = mAuth!!.currentUser

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_user, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {

        this.signOut?.setOnClickListener {
            signOutUser()
        }

        this.support.setOnClickListener( {
            sendEmail()
        })

        this.update.setOnClickListener( {
            updateUserInfo()
        })

        val user = FirebaseAuth.getInstance().currentUser
        var usernameText = view!!.findViewById<EditText>(R.id.clientusername) as EditText
        var emailText = view!!.findViewById<EditText>(R.id.clientemail) as EditText

        if (user != null) {
            // Name, email address, and profile photo Url
            usernameText.setText(user.displayName)
            emailText.setText(user.email)

            val photoUrl = user.photoUrl

            // Check if user's email is verified
            if(!user.isEmailVerified) {
                Snackbar.make(view!!, "Please Verifiy You Email.", Snackbar.LENGTH_LONG).setAction("Resend", ({
                    sendVerification()
                })).show()
            }

        }
    }


    // signout user
    private fun signOutUser() {
        val obj = JSONObject()
        try {
            obj.put("client_uid", client!!.uid)
            obj.put("tokenId", "")
        } catch (e: Exception) {
            Toast.makeText(context, "Could not send data", Toast.LENGTH_LONG).show()
        }

        FuelManager.instance.apply {
            basePath = "https://itransfo.tk"
            baseHeaders = mapOf("Content-Type" to "application/json")
        }
        postData(obj.toString())
        FirebaseAuth.getInstance().signOut()

    }

    private fun sendEmail() {
        val intent = Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "support@itransfo.tk", null))
        startActivity(Intent.createChooser(intent, "Choose an Email client :"))
    }

    private fun sendVerification() {
        val user = FirebaseAuth.getInstance().currentUser
        user!!.sendEmailVerification()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(context, "Verification Email Sent", Toast.LENGTH_LONG).show()
                    }
                }
    }

    private fun updateUserInfo() {
        val user = FirebaseAuth.getInstance().currentUser

        val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(this.clientusername.text.toString())
                .build()

        user!!.updateProfile(profileUpdates)
                .addOnCompleteListener(OnCompleteListener<Void> { task ->
                    if (task.isSuccessful) {
                        Log.d("Update", "User profile updated.")
                    }
                })

        user!!.updateEmail(this.clientemail.text.toString())
                .addOnCompleteListener(OnCompleteListener<Void> { task ->
                    if (task.isSuccessful) {
                        Log.d("Update", "User email address updated.")
                        Snackbar.make(view!!, "Saved", Snackbar.LENGTH_LONG).setAction("action", null).show()
                        sendVerification()
                    }
                })
    }

    private fun postData(p:String?) {
        Log.w("Sending data", "sending ")
        "/setToken/".httpPost().body(p!!)
                .responseString { request, _, result ->
                    Log.w("Request", result.toString())
                    startActivity(Intent(context,  LoginActivity::class.java))
                }
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
         * @return A new instance of fragment UserFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                UserFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
