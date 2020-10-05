package com.bdapps.templateandroid

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.termsscreen.*

class TermsScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.termsscreen)

        acceptTermsButton.setOnClickListener {
            // Set accepted terms to true
            val termsPref = getSharedPreferences("TERMS_INFO", Context.MODE_PRIVATE)
            val termsEditor = termsPref.edit()
            val acceptedTerms = termsPref.getString("AcceptedTerms", "")
            if (acceptedTerms == "") {
                termsEditor.putString("AcceptedTerms", "Yes")
                termsEditor.apply()
                startActivity(Intent(this, MainActivity:: class.java))
                this.finish();
            }
        }

        declineTermsButton.setOnClickListener {
            termsErrorLabel.isVisible = true
        }
    }
}