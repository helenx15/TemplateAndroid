package com.bdapps.templateandroid

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
//import com.example.myapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

import kotlinx.android.synthetic.main.signupscreen.*

class SignUpScreen : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signupscreen)

        // User clicks "Sign Up" button
        signUpButton.setOnClickListener {
            val userName = signUpNameInput.text.toString().trim()
            val userEmail = signUpEmailInput.text.toString().trim()
            val userPassword = signUpPasswordInput.text.toString()

            // Checks if any fields are empty
            if (userName == null || userEmail == null || userPassword == null || userName == "" || userEmail == "" || userPassword == "") {
                signUpTextField.text = "Please fill out all entries."
                signUpTextField.isVisible = true
            }
            // Checks if password length is less than 6 characters
            else if (userPassword.length < 6){
                signUpTextField.text = "Your password needs to be at least 6 characters."
                signUpTextField.isVisible = true
            }
            // TODO: Checks is email entered is a INSERT SCHOOL email
//            else if (!userEmail.takeLast(13).equals("@SCHOOL.org") ) {
//                signUpTextField.text = "Please sign up with a SCHOOL email."
//                signUpTextField.isVisible = true
//            }

            // Attempt to create account
            else {
                signUpTextField.text = "Please wait while we create your account..."
                signUpTextField.isVisible = true

                // Initialize Firebase
                mAuth = FirebaseAuth.getInstance()

                // Attempt to create account with entries
                mAuth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's name entry
                        val user = mAuth.currentUser
                        val profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(userName).build()
                        user!!.updateProfile(profileUpdates).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                            } else {
                                signUpTextField.text = "Error: Unable to save name."
                            }
                        }

                        // Send user a verification email
                        user!!.sendEmailVerification()
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    signUpTextField.text =
                                        "An email has been sent to your email. Please verify your account then log in."
                                    signUpLoginButton.isVisible = true
                                } else {
                                    signUpTextField.text =
                                        "Unable to send verification email. Try again, or try a different email."
                                }
                            }

                        // Unable to create an account
                    } else {
                        signUpTextField.text =
                            "Error: User already exists or one or more entries are invalid. Try again, or log in."
                        signUpLoginButton.isVisible = true
                    }
                }
            }
        }

        // Log in button is pressed; go to log in screen
        signUpLoginButton.setOnClickListener {
            val intent = Intent(this, LogInScreen:: class.java)
            startActivity(intent)
            this.finish();
        }
    }


}