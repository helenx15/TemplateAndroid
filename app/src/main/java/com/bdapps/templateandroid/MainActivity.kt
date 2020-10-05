package com.bdapps.templateandroid

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {

    //Firebase Initialize
    private lateinit var mAuth: FirebaseAuth

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Create saved preferences for accepting terms and conditions
        val termsPref = getSharedPreferences("TERMS_INFO", Context.MODE_PRIVATE)
        val acceptedTerms = termsPref.getString("AcceptedTerms", "")
        if (acceptedTerms == "") {
            Handler().postDelayed({
                startActivity(Intent(this, TermsScreen:: class.java))
                this.finish();
            }, 2500)
        }

        // If already submitted that day, go to correct screen, if not got to question screen
        val prescreenPref = getSharedPreferences("PRESCREEN_INFO", Context.MODE_PRIVATE)
        val editor = prescreenPref.edit()
        val dateSubmitted = prescreenPref.getString("DateSubmitted", "")

        // Get current date to compare with date submitted
        val currentDate = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy")
        val formattedDate = formatter.format(currentDate)

        // Not the same day, clear any saved info about previous day
        if (!dateSubmitted.equals(formattedDate)) {
            editor.clear()
            editor.apply()
        }

        // Firebase initialize
        mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser

        // If a user is signed in and email authenticated, let them enter the app
        if (user != null && user.isEmailVerified) {
            enterButtonMain.isVisible = true

        } else {
            signUpButtonMain.isVisible = true
            loginButtonMain.isVisible = true
        }

        // User presses the enter button
        enterButtonMain.setOnClickListener {

            // Get information if user has submitted the app for the current day
            val submittedClear = prescreenPref.getString("ClearScreen", "")
            val submittedNotClear = prescreenPref.getString("NotClearScreen", "")

            // No submission for the current day
            if (submittedClear == "" && submittedNotClear == "") {
                startActivity(Intent(this, PreScreenScreen:: class.java))
                this.finish();
            }

            // Already a submission for the current day
            else {
                if (submittedClear != "") {
                    startActivity(Intent(this, ClearScreen:: class.java))
                    this.finish();
                }
                else if (submittedNotClear != ""){
                    startActivity(Intent(this, NotClearScreen:: class.java))
                    this.finish();
                }
            }
        }

        // User clicks "Sign Up", goes to sign up screen
        signUpButtonMain.setOnClickListener {
            startActivity(Intent(this, SignUpScreen:: class.java))
            this.finish();
        }

        // User clicks "Log In", goes to log in screen
        loginButtonMain.setOnClickListener {
            startActivity(Intent(this, LogInScreen:: class.java))
            this.finish();
        }
    }


}