package com.medyas.itransfoapp

import android.util.Log
import android.widget.Toast
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.httpPost
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import org.json.JSONObject


/**
 * Created by Mohamed Yassine on 4/26/2018.
 */

class MyfirebaseInstanceIDService : FirebaseInstanceIdService() {
    private var mAuth: FirebaseAuth? = null
    private var client: FirebaseUser? = null


    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    override fun onTokenRefresh() {
        // Get updated InstanceID token.
        val refreshedToken = FirebaseInstanceId.getInstance().token
        Log.d(TAG, "Refreshed token: " + refreshedToken!!)

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken)
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private fun sendRegistrationToServer(token: String?) {
        // TODO: Implement this method to send token to your app server.
        mAuth = FirebaseAuth.getInstance()
        client = mAuth!!.currentUser
        if(client != null) {
            val obj = JSONObject()
            try {
                obj.put("client_uid", client!!.uid)
                obj.put("tokenId", token)
            } catch (e: Exception) {
                Log.d("Error", e.toString())
            }

            FuelManager.instance.apply {
                basePath = "https://itransfo.tk"
                baseHeaders = mapOf("Content-Type" to "application/json")
            }

            postData(obj.toString())
        }

    }
    private fun postData(p:String?) {
        Log.w("Sending data", "sending ")
        "/setToken/".httpPost().body(p!!)
                .responseString { request, _, result ->
                    Log.w("Request", result.toString())
                    //update(result)
                }
    }



    companion object {

        private val TAG = "MyFirebaseIIDService"
    }
}
