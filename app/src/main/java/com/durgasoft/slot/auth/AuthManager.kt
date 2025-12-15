package com.durgasoft.slot.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.durgasoft.slot.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class AuthManager(private val context: Context) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun isSignedIn(): Boolean = auth.currentUser != null
    fun uid(): String? = auth.currentUser?.uid
    fun name(): String? = auth.currentUser?.displayName
    fun email(): String? = auth.currentUser?.email

    fun signInIntent(): Intent {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(context, gso).signInIntent
    }

    fun handleResult(
        activity: Activity,
        data: Intent?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)

        val account: GoogleSignInAccount = try {
            task.getResult(ApiException::class.java)
        } catch (e: Exception) {
            onError(e.message ?: "Error al iniciar sesión con Google")
            return
        }

        val token = account.idToken
        if (token.isNullOrBlank()) {
            onError("No se pudo obtener el token de Google")
            return
        }

        val cred = GoogleAuthProvider.getCredential(token, null)
        auth.signInWithCredential(cred)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { err ->
                onError(err.message ?: "Error autenticando en Firebase")
            }
    }

    fun signOut() {
        auth.signOut()
        GoogleSignIn.getClient(
            context,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        ).signOut()
    }
}
