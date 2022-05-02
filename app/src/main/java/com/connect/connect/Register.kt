package com.connect.connect

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.CheckBox

class Register : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        fun onCheckboxClicked(view: View) {
            if (view is CheckBox) {
                val checked: Boolean = view.isChecked

                when (view.id) {
                    R.id.checkbox -> {
                        if (checked) {
                            // Put some meat on the sandwich
                        } else {
                            // Remove the meat
                        }
                    }
                }
            }
        }
    }
}