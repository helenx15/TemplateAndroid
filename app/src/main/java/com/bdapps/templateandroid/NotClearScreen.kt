package com.bdapps.templateandroid

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
//import com.example.myapplication.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.notclearscreen.*
import java.text.DateFormat
import java.util.*

class NotClearScreen: AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.notclearscreen)

        // Firebase initialize
        mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser

        // Get current date
        val calendar = Calendar.getInstance()
        val currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.time)

        // Load saved information from questionnaire
        val prescreenPref = getSharedPreferences("PRESCREEN_INFO",Context.MODE_PRIVATE)

        notClearNameLabel.text = user?.displayName
        notClearDateLabel.text = currentDate
        notClearTimeLabel.text = prescreenPref.getString("TimeSubmitted", "")

        // TODO: Modify # of questions
        val question1yes = prescreenPref.getString("Q1Prompt","")
        val question2yes = prescreenPref.getString("Q2Prompt","")
        val question3yes = prescreenPref.getString("Q3Prompt","")
        val question4yes = prescreenPref.getString("Q4Prompt","")
        var recordedTemperature = prescreenPref.getFloat("RecordedTemperature", 0.0F)

        // TODO: Modify # of questions
        if (question1yes != "") {
            promptsTextField.text = question1yes + "\n" + "\n"
        }
        if (question2yes != "") {
            promptsTextField.text = promptsTextField.text.toString() + question2yes + "\n" + "\n"
        }
        if (question3yes != "") {
            promptsTextField.text = promptsTextField.text.toString() + question3yes + "\n" + "\n"
        }
        if (question4yes != "") {
            promptsTextField.text = promptsTextField.text.toString() + question4yes + "\n" + "\n"
        }
        if (question1yes == "" && question2yes == "" && question3yes == "" && question4yes == "") {
            promptsTextField.text = "\n" + "None" + "\n"
        }

        if (recordedTemperature != 0.0F) {
            if(recordedTemperature.toString().trim().length > 5) {
                recordedTemperature = recordedTemperature.toString().take(5).toFloat()
            }
            promptsTextField.text = promptsTextField.text.toString() + "\n" + "Temp Recorded: " + recordedTemperature + " °F" + "\n"
        }

        // Go Back button pressed; go to questionnaire and clear all saved information
        goBackButton.setOnClickListener{
            val editor = prescreenPref.edit()
            editor.clear()
            editor.apply()
            startActivity(Intent(this, PreScreenScreen:: class.java))
            this.finish()
        }
    }
}