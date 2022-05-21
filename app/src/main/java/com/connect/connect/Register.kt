package com.connect.connect

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class Register : AppCompatActivity() {

    private lateinit var countriesSpinner: Spinner
    private lateinit var countries: Array<String>
    private lateinit var countryAdapter: ArrayAdapter<String>

    private lateinit var dateOfBirth: TextView
    private var cal = Calendar.getInstance()

    private lateinit var genderSpinner: Spinner
    private lateinit var gender: Array<String>
    private lateinit var genderAdapter: ArrayAdapter<String>

    private lateinit var phoneNumberIp: EditText
    private lateinit var userIdIp:EditText
    private lateinit var firstNameIp: EditText
    private lateinit var lastNameIp: EditText
    private lateinit var emailAddressIp: EditText
    private lateinit var passwordIp: EditText
    private lateinit var confirmPasswordIp: EditText

    private lateinit var termsNConditions: CheckBox

    private lateinit var registerUserBtn: Button

    private lateinit var country:String
    private lateinit var genderStr:String

    private lateinit var registerLayout: LinearLayout
    private lateinit var secondaryLayout: LinearLayout
    private lateinit var registrationProgressBar: ProgressBar

    private lateinit var successAnimation: LottieAnimationView
    private lateinit var failedAnimation: LottieAnimationView
    private lateinit var progressText: TextView
    private lateinit var backToLogin: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        phoneNumberIp = findViewById(R.id.phone_input_et)
        userIdIp = findViewById(R.id.user_id_input_et)
        firstNameIp = findViewById(R.id.first_name_input_et)
        lastNameIp = findViewById(R.id.last_name_input_et)
        emailAddressIp = findViewById(R.id.email_input_et)
        passwordIp = findViewById(R.id.password_input_et)
        confirmPasswordIp = findViewById(R.id.confirm_password_input_et)
        dateOfBirth = findViewById(R.id.reg_date_of_birth)
        countriesSpinner = findViewById(R.id.reg_country_spinner)
        genderSpinner = findViewById(R.id.gender_input_spinner)
        termsNConditions = findViewById(R.id.tnc_checkbox)
        registerUserBtn = findViewById(R.id.register_user_btn)
        registerLayout = findViewById(R.id.register_layout)
        secondaryLayout = findViewById(R.id.secondary_lyt)
        registrationProgressBar = findViewById(R.id.register_progress_bar)
        successAnimation = findViewById(R.id.success_animation)
        failedAnimation = findViewById(R.id.failed_animation)
        progressText = findViewById(R.id.progress_text)
        backToLogin = findViewById(R.id.back_to_login_btn)

        countries = resources.getStringArray(R.array.countries_list)
        countryAdapter = ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,countries)
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        countriesSpinner.adapter = countryAdapter
        countriesSpinner.setSelection(0)

        countriesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                Log.d("Country",countriesSpinner.selectedItem.toString())
                country = countriesSpinner.selectedItem.toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }

        dateOfBirth.setOnClickListener {
            DatePickerDialog(this,dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        gender = resources.getStringArray(R.array.gender)
        genderAdapter = ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,gender)
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        genderSpinner.adapter = genderAdapter
        genderSpinner.setSelection(0)

        genderSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                Log.d("Gender",genderSpinner.selectedItem.toString())
                genderStr = genderSpinner.selectedItem.toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        registerUserBtn.setOnClickListener {
            if(phoneNumberIp.text.equals("")){
                phoneNumberIp.error = "This is a required field"
                phoneNumberIp.requestFocus()
                return@setOnClickListener
            }
            if(phoneNumberIp.text.length < 10){
                phoneNumberIp.error = "Enter a valid phone number"
                phoneNumberIp.requestFocus()
                return@setOnClickListener
            }
            if(userIdIp.text.equals("")){
                userIdIp.error = "This is a required field"
                userIdIp.requestFocus()
                return@setOnClickListener
            }
            if(firstNameIp.text.equals("")){
                firstNameIp.error = "This is a required field"
                firstNameIp.requestFocus()
                return@setOnClickListener
            }
            if(lastNameIp.text.equals("")){
                lastNameIp.error = "This is a required field"
                lastNameIp.requestFocus()
                return@setOnClickListener
            }

            if(emailAddressIp.text.equals("")){
                emailAddressIp.error = "This is a required field"
                emailAddressIp.requestFocus()
                return@setOnClickListener
            }
            if(passwordIp.text.equals("")){
                passwordIp.error = "This is a required field"
                passwordIp.requestFocus()
                return@setOnClickListener
            }
            if(dateOfBirth.text.equals(R.string.date_of_birth)){
                dateOfBirth.error = "This is a required field"
                dateOfBirth.requestFocus()
                return@setOnClickListener
            }
            if(genderSpinner.selectedItem.equals("Select your gender!")){
                Toast.makeText(this,"Please select your gender",Toast.LENGTH_SHORT).show()
                genderSpinner.requestFocus()
                return@setOnClickListener
            }
            if(passwordIp.text.toString() != confirmPasswordIp.text.toString()){
                confirmPasswordIp.error = "Please enter the password correctly"
                confirmPasswordIp.requestFocus()
                return@setOnClickListener
            }
            if(!termsNConditions.isChecked){
                Toast.makeText(this,"Please accept the terms and conditions",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            registerLayout.visibility = View.GONE
            secondaryLayout.visibility = View.VISIBLE
            registerUser()
        }

        backToLogin.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
        }

    }
    private fun updateDateInView() {
        val myFormat = "yyyy-MM-dd" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        dateOfBirth.text = sdf.format(cal.time)
    }

    private fun registerUser(){
        val url = "https://connect-api-social.herokuapp.com/user/register"
        val stringRequest: StringRequest = object: StringRequest(
            Method.POST,
            url,
            Response.Listener {
                response ->
                Log.d("reg_response",response)
                val responseJson = JSONObject(response)
                registrationProgressBar.visibility = View.GONE
                backToLogin.visibility = View.VISIBLE
                if(responseJson.getInt("status") == 201){
                    successAnimation.visibility = View.VISIBLE
                    progressText.text = getString(R.string.reg_success_msg)
                }
                else{
                    failedAnimation.visibility = View.VISIBLE
                    progressText.text = getString(R.string.reg_failed_msg)
                }
                Toast.makeText(this,responseJson.getString("result"),Toast.LENGTH_SHORT).show()

            },
            Response.ErrorListener {
                error ->
                Log.d("Volley Error",error.toString())
            }
        ){
            override fun getParams(): MutableMap<String, String> {
                val parameters: MutableMap<String, String> = HashMap()
                // Add your parameters in HashMap
                parameters["phone_number"] = phoneNumberIp.text.toString()
                parameters["user_id"] = userIdIp.text.toString()
                parameters["first_name"] = firstNameIp.text.toString()
                parameters["last_name"] = lastNameIp.text.toString()
                parameters["email_address"] = emailAddressIp.text.toString()
                parameters["country"] = country
                parameters["password"] = passwordIp.text.toString()
                parameters["gender_field"] = genderStr
                parameters["date_of_birth"] = dateOfBirth.text.toString()
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

    override fun onBackPressed() {
        startActivity(Intent(this,MainActivity::class.java))
    }
}