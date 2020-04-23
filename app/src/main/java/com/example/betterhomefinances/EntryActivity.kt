package com.example.betterhomefinances

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.AuthUI.IdpConfig.EmailBuilder
import com.firebase.ui.auth.AuthUI.IdpConfig.GoogleBuilder
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse

const val RC_SIGN_IN = 123;

class EntryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry)

        if (UserHandler.currentUser != null) {
            successfulLogin()
        }
    }


    fun onClickSignInButton(v: View) {
        val authUI = AuthUI.getInstance()

        startActivityForResult(
            authUI
                .createSignInIntentBuilder()
                .setAvailableProviders(
                    listOf(
                        GoogleBuilder().build(),
                        EmailBuilder().build()
                    )
                )
                .build(),
            RC_SIGN_IN
        )
    }

    fun successfulLogin() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent);
        finish();
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // TODO: add error codes and handle everything apart from successful login
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == RESULT_OK) {
//                UserHandler.refresh()
                successfulLogin()
            } else {
                // Sign in failed
                if (response == null) {
//                    val snack = Snackbar.make(, "snackbar",Snackbar.LENGTH_LONG)
//                    // User pressed back button
//                    showSnackbar(R.string.sign_in_cancelled);
                    return;
                }

                if (response.error!!.errorCode == ErrorCodes.NO_NETWORK) {
//                    showSnackbar(R.string.no_internet_connection);
                    return;
                }

                Log.e("SIGN_IN", "Signin error: " + response.error)

//                showSnackbar(R.string.unknown_error);
            }
        }
    }
}
