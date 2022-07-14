package com.connect.connect

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import org.json.JSONObject
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private lateinit var goToRegister: Button
    private lateinit var loginBtn: Button
    private lateinit var phoneNumberEt: EditText
    private lateinit var passwordEt: EditText
    private lateinit var resetPasswordBtn:TextView

    private lateinit var sharedPreferences: SharedPreferences
    private val SHARED_PREF_NAME = "myPref"
    private val KEY_APIKEY = "APIKey"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME,MODE_PRIVATE)
        val apiKey = sharedPreferences.getString(KEY_APIKEY, null)

        phoneNumberEt = findViewById(R.id.phone_input_et)
        passwordEt = findViewById(R.id.password_input_et)
        loginBtn = findViewById(R.id.login_btn)
        resetPasswordBtn = findViewById(R.id.reset_password_btn)

        resetPasswordBtn.setOnClickListener {
            startActivity(Intent(this,ForgotPasswordActivity::class.java))
        }

        goToRegister = findViewById(R.id.register_btn)
        goToRegister.setOnClickListener{
            startActivity(Intent(this,Register::class.java))
        }

        if(apiKey != null){
            startActivity(Intent(this,HomeScreen::class.java))
        }
        else{
            loginBtn.setOnClickListener{
                if(phoneNumberEt.text.equals("")){
                    phoneNumberEt.error = "This is a required field"
                    phoneNumberEt.requestFocus()
                    return@setOnClickListener
                }
                if(passwordEt.text.equals("")){
                    passwordEt.error = "This is a required field"
                    passwordEt.requestFocus()
                    return@setOnClickListener
                }
                if(phoneNumberEt.text.length < 10){
                    phoneNumberEt.error = "Enter valid phone number"
                    phoneNumberEt.requestFocus()
                    return@setOnClickListener
                }
                login(phoneNumberEt.text.toString(), passwordEt.text.toString())
            }
        }
    }

    private fun login(phone:String, password:String){
        val url = "https://connect-social-api-prod.herokuapp.com/user/login"
        val stringRequest: StringRequest = object: StringRequest(
            Method.POST,
            url,
            Response.Listener {
                    response ->
                val responseJson = JSONObject(response)
                if(responseJson.getInt("status") == 200){
                    val resultJson = responseJson.getJSONObject("result")
                    val editor = sharedPreferences.edit()
                    editor.putString(KEY_APIKEY, resultJson.getString("api_key"))
                    editor.apply()
                    startActivity(Intent(this,HomeScreen::class.java))
                }
                else{
                    Toast.makeText(this,"You have entered wrong user credentials or user does not exist",Toast.LENGTH_LONG).show()
                }
        }, Response.ErrorListener {
                    error ->
                Log.d("Volley Error",error.toString())
        }){
            override fun getParams(): MutableMap<String, String> {
                val parameters: MutableMap<String, String> = HashMap()
                // Add your parameters in HashMap
                parameters.put("phone_number",phone)
                parameters.put("password",password)
                return parameters
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(
            TimeUnit.SECONDS.toMillis(20).toInt(),  //After the set time elapses the request will timeout
            1,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        SingletonRequestQueue.getInstance(this).addToRequestQueue(stringRequest);
    }
}