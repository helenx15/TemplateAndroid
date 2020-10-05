package com.bdapps.templateandroid

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
//import com.example.myapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.prescreenscreen.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PreScreenScreen: AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.prescreenscreen)

        // Firebase initialize
        mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser

        symptomsLink.movementMethod = LinkMovementMethod.getInstance()

        // View my info button clicked; go to info screen
        viewMyInfoButton.setOnClickListener {
            startActivity(Intent(this, InfoScreen:: class.java))
            this.finish();
        }

        // Submit button pressed
        submitButton.setOnClickListener{

            // Checks if all questions are answered
            // TODO: Modify # of questions/ whether they want a fever input
            if ((q1Yes.isChecked || q1No.isChecked) && (q2Yes.isChecked || q2No.isChecked) && (q3Yes.isChecked || q3No.isChecked) && (q4Yes.isChecked || q4No.isChecked)) {

                // All questions are answered; save the questions answered yes
                val prescreenPref = getSharedPreferences("PRESCREEN_INFO", Context.MODE_PRIVATE)
                val editor = prescreenPref.edit()

                // TODO: Modify # of questions/ whether they want a fever input
                if (q1Yes.isChecked) {
                    editor.putString("Q1Prompt", question1Prompt.text.toString())
                }
                if (q2Yes.isChecked) {
                    editor.putString("Q2Prompt", question2Prompt.text.toString())
                }
                if (q3Yes.isChecked) {
                    editor.putString("Q3Prompt", question3Prompt.text.toString())
                }
                if (q4Yes.isChecked) {
                    editor.putString("Q4Prompt", question4Prompt.text.toString())
                }

                // Get Temperature, if entered
                var recordedTemperature = 0.0
                if (temperatureInput.text.toString().trim() != "") {
                    recordedTemperature = temperatureInput.text.toString().toDouble()
                    editor.putFloat("RecordedTemperature", recordedTemperature.toFloat())
                }
                editor.apply()

                // Load information to be sent to Firebase
                val userName = user?.displayName
                val userEmail = user?.email

                // Load questionnaire information into app (Not empty strings = question answered yes)
                val formPref = getSharedPreferences("PRESCREEN_INFO", Context.MODE_PRIVATE)

                // TODO: Modify # of questions/ whether they want a fever input
                var question1Temp = "No"
                var question2Temp = "No"
                var question3Temp = "No"
                var question4Temp = "No"

                // TODO: Modify # of questions/ whether they want a fever input
                if (formPref.getString("Q1Prompt", "") != "") {
                    question1Temp = "Yes"
                }
                if(formPref.getFloat("RecordedTemperature", 0.0F).toString().trim() != "" && formPref.getFloat("RecordedTemperature", 0.0F) != 0.0F) {
                    var temperatureTemp = formPref.getFloat("RecordedTemperature", 0.0F).toString().trim()
                    question1Temp = "$question1Temp $temperatureTemp °F"
                }
                if (formPref.getString("Q2Prompt", "") != "") {
                    question2Temp = "Yes"
                }
                if (formPref.getString("Q3Prompt", "") != "") {
                    question3Temp = "Yes"
                }
                if (formPref.getString("Q4Prompt", "") != "") {
                    question4Temp = "Yes"
                }

                // Load date
                val currentDate = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy")
                val formatter2 = DateTimeFormatter.ofPattern("hh:mm:ss a")
                val formattedDate = formatter.format(currentDate)
                val timeStamp = formatter2.format(currentDate)

                // Save date and time submitted
                editor.putString("DateSubmitted", formattedDate)
                editor.putString("TimeSubmitted", timeStamp)
                editor.apply()

                var root = ""
                val adjustedEmail = userEmail?.replace(".",",")

                if (userName == userEmail) {
                    root = adjustedEmail.toString()
                }

                else {
                    root = "$userName; $adjustedEmail"
                }

                // Creates path starting with the user's display name
                val ref = FirebaseDatabase.getInstance().getReference(root)

                // TODO: Modify # of questions/ whether they want a fever input
                // Creates the info loaded, which are children of the date
                ref.child(formattedDate).child("question1").setValue(question1Temp)
                ref.child(formattedDate).child("question2").setValue(question2Temp)
                ref.child(formattedDate).child("question3").setValue(question3Temp)
                ref.child(formattedDate).child("question4").setValue(question4Temp)
                ref.child(formattedDate).child("timestamp").setValue(timeStamp)

                val refdate = FirebaseDatabase.getInstance().getReference(formattedDate)
                refdate.child(root).child("question1").setValue(question1Temp)
                refdate.child(root).child("question2").setValue(question2Temp)
                refdate.child(root).child("question3").setValue(question3Temp)
                refdate.child(root).child("question4").setValue(question4Temp)
                refdate.child(root).child("timestamp").setValue(timeStamp)

                // CLEAR: Qs 2-4 No + No fever / Fever < 100.4; Othewise NOT CLEAR
                // TODO: Modify # of questions/ whether they want a fever input
                if ((q2No.isChecked && q3No.isChecked && q4No.isChecked) && (q1No.isChecked && (temperatureInput.text.toString().trim() == "" || temperatureInput.text.toString().toDouble() < 100.4))) {
                    editor.putString("ClearScreen", "clear")
                    editor.apply()
                    startActivity(Intent(this, ClearScreen:: class.java))
                    finish();
                }

                // NOT CLEAR: At least 1 Q answered yes / fever >= 100.4
                else {
                    editor.putString("NotClearScreen", "notclear")
                    editor.apply()
                    startActivity(Intent(this, NotClearScreen:: class.java))
                    finish();
                }
            }

            // Not all questions answered; display error message
            else {
                prescreenErrorLabel.isVisible = true
            }
        }
    }

}