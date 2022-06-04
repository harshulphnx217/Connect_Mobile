package com.connect.connect

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.airbnb.lottie.LottieAnimationView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.bumptech.glide.Glide
import org.json.JSONObject
import org.w3c.dom.Text
import java.util.concurrent.TimeUnit

class ChangePassword : AppCompatActivity() {

    private lateinit var oldPasswordEt: EditText
    private lateinit var newPasswordEt: EditText
    private lateinit var confirmPasswordEt: EditText
    private lateinit var submitPasswordBtn: Button
    private lateinit var backIconPassword: ImageView

    private lateinit var changePasswordPrimaryLayout: LinearLayout
    private lateinit var changePasswordSecondaryLayout: LinearLayout
    private lateinit var changePasswordProgressBar: ProgressBar
    private lateinit var changePasswordProgressTv: TextView
    private lateinit var backToHomeBtn: Button
    private lateinit var changePasswordSuccessAnim: LottieAnimationView
    private lateinit var changePasswordFailedAnim: LottieAnimationView


    private lateinit var sharedPreferences: SharedPreferences
    private val SHARED_PREF_NAME = "myPref"
    private val KEY_APIKEY = "APIKey"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME,MODE_PRIVATE)
        val apiKey = sharedPreferences.getString(KEY_APIKEY, null)

        oldPasswordEt = findViewById(R.id.old_password_input_et)
        newPasswordEt = findViewById(R.id.new_password_input_et)
        confirmPasswordEt = findViewById(R.id.confirm_new_password_input_et)
        submitPasswordBtn = findViewById(R.id.confirm_passwordBtn)
        backIconPassword = findViewById(R.id.back_icon_change_password)

        changePasswordPrimaryLayout = findViewById(R.id.change_password_primary_layout)
        changePasswordSecondaryLayout = findViewById(R.id.change_password_secondary_lyt)
        changePasswordProgressBar = findViewById(R.id.change_password_progress_bar)
        changePasswordProgressTv = findViewById(R.id.change_password_progress_text)
        backToHomeBtn = findViewById(R.id.back_to_home_btn)
        changePasswordSuccessAnim = findViewById(R.id.change_password_success_animation)
        changePasswordFailedAnim = findViewById(R.id.change_password_failed_animation)



        submitPasswordBtn.setOnClickListener {
            if (oldPasswordEt.text.toString().isEmpty()){
                oldPasswordEt.error = "This is a required field"
                oldPasswordEt.requestFocus()
                return@setOnClickListener
            }
            if (newPasswordEt.text.toString().isEmpty()){
                newPasswordEt.error = "This is a required field"
                newPasswordEt.requestFocus()
                return@setOnClickListener
            }
            if (confirmPasswordEt.text.toString().isEmpty()){
                confirmPasswordEt.error = "This is a required field"
                confirmPasswordEt.requestFocus()
                return@setOnClickListener
            }
            if (confirmPasswordEt.text.toString() != newPasswordEt.text.toString()){
                confirmPasswordEt.error = "Password is not matching!"
                confirmPasswordEt.requestFocus()
                return@setOnClickListener
            }
            if (apiKey != null) {
                changePasswordPrimaryLayout.visibility = View.GONE
                changePasswordSecondaryLayout.visibility = View.VISIBLE
                changePassword(apiKey)
            }
        }

        backIconPassword.setOnClickListener{
            startActivity(Intent(this,ProfileActivity::class.java))
        }

        backToHomeBtn.setOnClickListener{
            startActivity(Intent(this,ProfileActivity::class.java))
        }

    }

    private fun changePassword(apiKey:String){
        val url = "https://connect-api-social.herokuapp.com/user/change_password"
        val stringRequest: StringRequest = object: StringRequest(
            Method.POST,
            url,
            Response.Listener {
                    response ->
                val responseJson = JSONObject(response)
                val resultStr: String = responseJson.getString("result")
                changePasswordProgressBar.visibility = View.GONE

                if(responseJson.getInt("status") == 200){
                    changePasswordSuccessAnim.visibility = View.VISIBLE
                    backToHomeBtn.visibility = View.VISIBLE
                    changePasswordProgressTv.text = resultStr
                }
                else{
                    changePasswordFailedAnim.visibility = View.VISIBLE
                    backToHomeBtn.visibility = View.VISIBLE
                    changePasswordProgressTv.text = resultStr

                }
            }, Response.ErrorListener {
                    error ->
                Log.d("Volley Error",error.toString())
            }){
            override fun getParams(): MutableMap<String, String> {
                val parameters: MutableMap<String, String> = HashMap()
                // Add your parameters in HashMap
                parameters["api_key"] = apiKey
                parameters["old_password"] = oldPasswordEt.text.toString()
                parameters["new_password"] = newPasswordEt.text.toString()
                return parameters
            }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(
            TimeUnit.SECONDS.toMillis(20).toInt(),  //After the set time elapses the request will timeout
            1,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        SingletonRequestQueue.getInstance(this).addToRequestQueue(stringRequest)
    }
}