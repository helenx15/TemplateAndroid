package com.bdapps.templateandroid

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
//import com.example.myapplication.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.infoscreen.*

class InfoScreen: AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.infoscreen)

        // Firebase loading info from email
        mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser

        if (user?.displayName == user?.email) {
            infoNameTitleLabel.isVisible = false
            infoNameLabel.isVisible = false
        }

        infoNameLabel.text = user?.displayName
        infoEmailLabel.text = user?.email

        // Sign out button pressed, user is signed out
        signOutButton.setOnClickListener {
            mAuth.signOut()
            val intent = Intent(this, MainActivity:: class.java)
            startActivity(intent)
            this.finish();
        }

        // Next button pressed; go to pre-screen
        infoNextButton.setOnClickListener{
            val intent = (Intent(this, PreScreenScreen:: class.java))
            startActivity(intent)
            this.finish();

        }

    }

}