package com.vladgad.tablebudgeter


import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.ClearCredentialStateRequest.Companion.TYPE_CLEAR_CREDENTIAL_STATE
import androidx.credentials.CredentialManager
import androidx.credentials.CredentialOption
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.PasswordCredential
import androidx.credentials.PublicKeyCredential
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.security.SecureRandom
import java.util.Base64


class GoogleSignInUtils {

    companion object {
        private const val TAG = "GoogleSignInUtils"
        fun clearState(context: Context, scope: CoroutineScope, clear: () -> Unit) {
            val credentialManager = CredentialManager.create(context)
            val req = ClearCredentialStateRequest(requestType = TYPE_CLEAR_CREDENTIAL_STATE)

            scope.launch {
                credentialManager.clearCredentialState(req)
                clear.invoke()
            }

        }

        // синимся с гуглом боттом
        fun doGoogleSignInBottom(
            context: Context,
            scope: CoroutineScope,
            // launcher: ManagedActivityResultLauncher<Intent, ActivityResult>?,
            login: () -> Unit
        ) {
            val credentialManager = CredentialManager.create(context)

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(getCredentialOptionsBottom(context))
                .build()

            scope.launch {
                try {
                    val credential = credentialManager.getCredential(context, request)
                    val googleIdTokenCredential =
                        GoogleIdTokenCredential.createFrom(credential.credential.data)

                    when (credential) {
                        is GoogleIdTokenCredential -> {

                        }

                        is CustomCredential -> {
                            if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                                try {
                                    // Use googleIdTokenCredential and extract id to validate and
                                    // authenticate on your server.
                                    val googleIdTokenCredential = GoogleIdTokenCredential
                                        .createFrom(credential.data)
                                    login.invoke()
                                } catch (e: GoogleIdTokenParsingException) {
                                }
                            } else {
                                // Catch any unrecognized credential type here.
                            }
                        }

                        else -> {
                            Log.e(TAG, "Unexpected type of credential")
                        }
                    }


                } catch (e: NoCredentialException) {
                    //  launcher?.launch(getIntent())
                } catch (e: GetCredentialException) {
                    e.printStackTrace()
                }
            }
        }

        //вход без гугла кнрпки


        fun doGoogleSignIn(
            context: Context,
            scope: CoroutineScope,
            //launcher: ManagedActivityResultLauncher<Intent, ActivityResult>?,
            login: () -> Unit
        ) {
            val credentialManager = CredentialManager.create(context)

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(getCredentialOptions(context))
                .build()
            scope.launch {
                try {
                    val credential = credentialManager.getCredential(context, request)
                    when (credential) {

                        // Passkey credential
                        is PublicKeyCredential -> {

                        }

                        // Password credential
                        is PasswordCredential -> {

                        }

                        // GoogleIdToken credential
                        is CustomCredential -> {
                            if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                                try {
                                    val googleIdTokenCredential = GoogleIdTokenCredential
                                        .createFrom(credential.data)
                                } catch (e: GoogleIdTokenParsingException) {
                                    Log.e(TAG, "Received an invalid google id token response", e)
                                }
                            } else {
                                Log.e(TAG, "Unexpected type of credential")
                            }
                        }

                        else -> {
                            Log.e(TAG, "Unexpected type of credential")
                        }
                    }

                } catch (e: NoCredentialException) {
                    //launcher?.launch(getIntent())
                } catch (e: GetCredentialException) {
                    e.printStackTrace()
                }
            }
        }

        private fun getIntent(): Intent {
            return Intent(Settings.ACTION_ADD_ACCOUNT).apply {
                putExtra(Settings.EXTRA_ACCOUNT_TYPES, arrayOf("com.google"))
            }
        }

        private fun getCredentialOptions(context: Context): CredentialOption {
            return GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setAutoSelectEnabled(true)
                .setServerClientId(context.getString(R.string.web_client_id))
                .setNonce(generateSecureRandomNonce())
                .build()
        }

        private fun getCredentialOptionsBottom(context: Context): CredentialOption {
            return GetSignInWithGoogleOption.Builder(context.getString(R.string.web_client_id))
                .setNonce(generateSecureRandomNonce())
                .build()
        }


        private fun generateSecureRandomNonce(byteLength: Int = 32): String {
            val randomBytes = ByteArray(byteLength)
            SecureRandom.getInstanceStrong().nextBytes(randomBytes)
            return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes)
        }

    }
}