package com.connect.connect

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class EditProfileActivity : AppCompatActivity() {

    private lateinit var dateOfBirth: TextView
    private var cal = Calendar.getInstance()

    private lateinit var firstNameEt: EditText
    private lateinit var lastNameEt: EditText
    private lateinit var countryEt: EditText
    private lateinit var genderEt: EditText
    private lateinit var editProfileSubmitBtn: Button
    private lateinit var profilePicIv: CircleImageView
    private lateinit var bioEt: EditText
    private lateinit var backBtn: ImageView

    private lateinit var userId:String

    private lateinit var sharedPreferences: SharedPreferences
    private val SHARED_PREF_NAME = "myPref"
    private val KEY_APIKEY = "APIKey"

    private var imageData: ByteArray? = null

    companion object {
        private const val IMAGE_PICK_CODE = 999
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME,MODE_PRIVATE)
        val apiKey = sharedPreferences.getString(KEY_APIKEY, null)

        dateOfBirth = findViewById(R.id.date_of_birth_et)
        firstNameEt = findViewById(R.id.first_name_et)
        lastNameEt = findViewById(R.id.last_name_et)
        countryEt = findViewById(R.id.country_et)
        editProfileSubmitBtn = findViewById(R.id.edit_profile_submit_btn)
        profilePicIv = findViewById(R.id.profile_pic_upload)
        genderEt = findViewById(R.id.gender_et)
        bioEt = findViewById(R.id.bio_et)
        backBtn = findViewById(R.id.back_btn_edit_profile)

        backBtn.setOnClickListener {
            startActivity(Intent(this,ProfileActivity::class.java))
        }

        if (apiKey != null){
            getUserInfo(apiKey)
        }

        profilePicIv.setOnClickListener {
            launchGallery()
        }

        editProfileSubmitBtn.setOnClickListener{
            if(firstNameEt.text.toString().isEmpty()){
                firstNameEt.error = "This is a required field"
                firstNameEt.requestFocus()
                return@setOnClickListener
            }
            if(lastNameEt.text.toString().isEmpty()){
                lastNameEt.error = "This is a required field"
                lastNameEt.requestFocus()
                return@setOnClickListener
            }
            if(countryEt.text.toString().isEmpty()){
                countryEt.error = "This is a required field"
                countryEt.requestFocus()
                return@setOnClickListener
            }
            if(genderEt.text.toString().isEmpty()){
                genderEt.error = "This is a required field"
                genderEt.requestFocus()
                return@setOnClickListener
            }
            if(dateOfBirth.text.toString().isEmpty()){
                dateOfBirth.error = "This is a required field"
                dateOfBirth.requestFocus()
                return@setOnClickListener
            }

            if(apiKey != null){
                if(imageData != null){
                    uploadPhoto(apiKey)
                }
                editProfile(apiKey,firstNameEt.text.toString(),lastNameEt.text.toString(),
                    countryEt.text.toString(),dateOfBirth.text.toString(),bioEt.text.toString(),
                    genderEt.text.toString())
            }
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
    }

    private fun updateDateInView() {
        val myFormat = "yyyy-MM-dd" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        dateOfBirth.text = sdf.format(cal.time)
    }

    private fun getUserInfo(apiKey:String){
        val url = "https://connect-api-social.herokuapp.com/user/get-user-info"
        val stringRequest: StringRequest = object: StringRequest(
            Method.POST,
            url,
            Response.Listener {
                    response ->
                val responseJson = JSONObject(response)
                if(responseJson.getInt("status") == 200){
                    val resultObj: JSONObject = responseJson.getJSONObject("result")
                    userId  = resultObj.getString("user_id")
                    firstNameEt.setText(resultObj.getString("first_name").toString())
                    lastNameEt.setText(resultObj.getString("last_name").toString())
                    countryEt.setText(resultObj.getString("country").toString())
                    dateOfBirth.text =  resultObj.getString("dob")
                    genderEt.setText(resultObj.getString("gender"))
                    bioEt.setText(resultObj.getString("bio"))
                    Glide.with(this)
                        .load(resultObj.getString("profile_pic"))
                        .placeholder(R.drawable.ic_baseline_account_circle_24)
                        .error(R.drawable.ic_baseline_account_circle_24)
                        .override(120,120)
                        .centerCrop()
                        .into(profilePicIv)
                }
                else{
                    Toast.makeText(this,"Invalid API Key", Toast.LENGTH_LONG).show()
                }
            }, Response.ErrorListener {
                    error ->
                Log.d("Volley Error",error.toString())
            }){
            override fun getParams(): MutableMap<String, String> {
                val parameters: MutableMap<String, String> = HashMap()
                // Add your parameters in HashMap
                parameters["api_key"] = apiKey
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

    private fun editProfile(apiKey: String, firstName:String,lastName:String,country:String,
                            dateOfBirth:String,bio:String,gender:String){
        val url = "https://connect-api-social.herokuapp.com/user/edit_profile"
        val stringRequest: StringRequest = object: StringRequest(
            Method.POST,
            url,
            Response.Listener {
                    response ->
                val responseJson = JSONObject(response)
                if(responseJson.getInt("status") == 200){
                    Toast.makeText(this,responseJson.getString("result"), Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(this,"Invalid API Key", Toast.LENGTH_LONG).show()
                }
            }, Response.ErrorListener {
                    error ->
                Log.d("Volley Error",error.toString())
            }){
            override fun getParams(): MutableMap<String, String> {
                val parameters: MutableMap<String, String> = HashMap()
                // Add your parameters in HashMap
                parameters["api_key"] = apiKey
                parameters["first_name"] = firstName
                parameters["last_name"] = lastName
                parameters["country"] = country
                parameters["dob"] = dateOfBirth
                parameters["bio"] = bio
                parameters["gender"] = gender
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

    private fun uploadPhoto(apiKey: String){
        val url = "https://connect-api-social.herokuapp.com/user/upload-profile-pic"
        val multipartRequest: VolleyMultipartRequest = object : VolleyMultipartRequest(
            Method.POST,
            url,
            Response.Listener {
                val json = String(it.data)
                try{
                    val jsonObject:JSONObject = JSONObject(json)
                    if(jsonObject.getInt("status") == 201){
                        Toast.makeText(this,"Success",Toast.LENGTH_SHORT).show()
                    }
                    else{
                        Toast.makeText(this,"Failed",Toast.LENGTH_SHORT).show()
                    }
                }catch (e:Exception){
                    Toast.makeText(this,"Failed",Toast.LENGTH_SHORT).show()
                }
            }, Response.ErrorListener {
                Log.d("Volley error",it.toString())
                Toast.makeText(this,"Failed",Toast.LENGTH_SHORT).show()
            }
        ){
            override fun getParams(): MutableMap<String, String> {
                val parameters: MutableMap<String, String> = HashMap()
                // Add your parameters in HashMap
                parameters["api_key"] = apiKey
                return parameters
            }

            override fun getByteData(): Map<String, FileDataPart> {
                val params = HashMap<String, FileDataPart>()
                val title:String = userId.replace(" ","-") + "_image"
                params["user_profile_pic"] = FileDataPart(title.toString(), imageData!!, "jpeg")
                return params
            }
        }
        multipartRequest.retryPolicy = DefaultRetryPolicy(
            TimeUnit.SECONDS.toMillis(20).toInt(),  //After the set time elapses the request will timeout
            1,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        SingletonRequestQueue.getInstance(baseContext).addToRequestQueue(multipartRequest)
    }

    private fun launchGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    @Throws(IOException::class)
    private fun createImageData(uri: Uri) {
        val inputStream = contentResolver.openInputStream(uri)
        inputStream?.buffered()?.use {
            imageData = it.readBytes()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            val uri = data?.data
            if (uri != null) {
                profilePicIv.setImageURI(uri)
                createImageData(uri)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        startActivity(Intent(this,ProfileActivity::class.java))
    }
}