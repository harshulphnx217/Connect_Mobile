package com.connect.connect

import android.content.Intent
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
import java.util.concurrent.TimeUnit

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var emailInputEt: EditText
    private lateinit var resetPasswordBtn: Button
    private lateinit var resetPasswordSecondaryLayout: LinearLayout
    private lateinit var resetPasswordInputLayout: LinearLayout
    private lateinit var resetPasswordProgressBar: ProgressBar
    private lateinit var resetPasswordSuccessAnim: LottieAnimationView
    private lateinit var resetPasswordFailedAnim: LottieAnimationView
    private lateinit var resetPasswordTv: TextView
    private lateinit var resetPasswordBackToLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        emailInputEt = findViewById(R.id.forget_email_input_et)
        resetPasswordBtn = findViewById(R.id.forget_reset_password_btn)
        resetPasswordInputLayout = findViewById(R.id.reset_input_layout)
        resetPasswordSecondaryLayout = findViewById(R.id.reset_password_secondary_lyt)
        resetPasswordProgressBar = findViewById(R.id.reset_password_progress_bar)
        resetPasswordSuccessAnim = findViewById(R.id.reset_password_success_animation)
        resetPasswordFailedAnim = findViewById(R.id.reset_password_failed_animation)
        resetPasswordTv = findViewById(R.id.reset_password_progress_text)
        resetPasswordBackToLogin = findViewById(R.id.reset_back_to_login_btn)


        resetPasswordBtn.setOnClickListener{
            if(emailInputEt.text.toString().isEmpty()){
                emailInputEt.error = "This is a required field!"
                emailInputEt.requestFocus()
                return@setOnClickListener
            }
            resetPasswordInputLayout.visibility = View.GONE
            resetPasswordSecondaryLayout.visibility = View.VISIBLE
            resetPassword(emailInputEt.text.toString())
        }
        resetPasswordBackToLogin.setOnClickListener{
            startActivity(Intent(this,MainActivity::class.java))
        }
    }

    private fun resetPassword(emailAddress:String){
        val url = "https://connect-api-social.herokuapp.com/forgot_passoword"
        val stringRequest: StringRequest = object: StringRequest(
            Method.POST,
            url,
            Response.Listener {
                    response ->
                val responseJson = JSONObject(response)
                resetPasswordProgressBar.visibility = View.GONE
                resetPasswordBackToLogin.visibility = View.VISIBLE
                if(responseJson.getInt("status") == 200){
                    resetPasswordSuccessAnim.visibility = View.VISIBLE
                    resetPasswordTv.text = "The Email has been sent, please check your spam and junk for the mail!"
                }
                else{
//                    Toast.makeText(this,"Invalid API Key",Toast.LENGTH_LONG).show()
                    resetPasswordFailedAnim.visibility = View.VISIBLE
                    resetPasswordTv.text = "Some error occurred please try again later!"
                }
            }, Response.ErrorListener {
                    error ->
                Log.d("Volley Error",error.toString())
            }){
            override fun getParams(): MutableMap<String, String> {
                val parameters: MutableMap<String, String> = HashMap()
                // Add your parameters in HashMap
                parameters["email"] = emailAddress
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

    override fun onBackPressed() {
        startActivity(Intent(this,MainActivity::class.java))
    }
}