package com.vladgad.tablebudgeter.google

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
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
import com.google.android.gms.auth.api.identity.AuthorizationRequest
import com.google.android.gms.auth.api.identity.AuthorizationResult
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.vladgad.tablebudgeter.R
import com.vladgad.tablebudgeter.utils.Constants
import com.vladgad.tablebudgeter.utils.Constants.WEB_CLIENT_ID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.security.SecureRandom
import java.util.Base64


class GoogleSignInUtils {

    companion object {
        private val requestedScopes: List<Scope> = listOf(
            Scope("https://www.googleapis.com/auth/spreadsheets"),
            Scope("https://www.googleapis.com/auth/drive.readonly")
        )

        private const val TAG = "GoogleSignInUtils"
        fun clearState(context: Context, scope: CoroutineScope, clear: () -> Unit) {
            val credentialManager = CredentialManager.create(context)
            val req = ClearCredentialStateRequest(requestType = TYPE_CLEAR_CREDENTIAL_STATE)
            scope.launch {
                credentialManager.clearCredentialState(req)
                clear.invoke()
            }

        }

        suspend fun doGoogleSignIn(
            context: Context,
        ): Boolean {
            val credentialManager = CredentialManager.create(context)

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(getCredentialOptions(context))
                .build()
            try {
                when (val credential = credentialManager.getCredential(context, request)) {


                    // GoogleIdToken credential
                    is CustomCredential -> {
                        if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                            try {
                                val googleIdTokenCredential = GoogleIdTokenCredential
                                    .createFrom(credential.data)
                                return true
                            } catch (e: GoogleIdTokenParsingException) {
                                Log.e(TAG, "Received an invalid google id token response", e)
                                return false
                            }
                        } else {
                            Log.e(TAG, "Unexpected type of credential")
                            return false
                        }
                    }

                    else -> {
                        Log.e(TAG, "Unexpected type of credential")
                        return true
                    }
                }

            } catch (e: NoCredentialException) {
                //launcher?.launch(getIntent())
                return false
            } catch (e: GetCredentialException) {
                e.printStackTrace()
                return false
            }

        }

        private fun getCredentialOptions(context: Context): CredentialOption {
            return GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(true)
                .setAutoSelectEnabled(true)
                .setServerClientId(WEB_CLIENT_ID)
                .setNonce(generateSecureRandomNonce())
                .build()
        }

        private fun generateSecureRandomNonce(byteLength: Int = 32): String {
            val randomBytes = ByteArray(byteLength)
            SecureRandom.getInstanceStrong().nextBytes(randomBytes)
            return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes)
        }


        //=======================AUTH======================
        fun requestSheetsAuthorization(
            onSuccess: (authorizationResult: AuthorizationResult) -> Unit,
            startAuthorizationIntent: ActivityResultLauncher<IntentSenderRequest>,
            activity: Activity,
        ) {
            val authorizationRequest = AuthorizationRequest.Builder()
                .setRequestedScopes(requestedScopes)
                .build()
            Identity.getAuthorizationClient(activity)
                .authorize(authorizationRequest)
                .addOnSuccessListener { authorizationResult ->
                    if (authorizationResult.hasResolution()) {
                        // Показываем диалог согласия Google
                        val pendingIntent = authorizationResult.pendingIntent
                        startAuthorizationIntent.launch(
                            IntentSenderRequest.Builder(pendingIntent!!.intentSender).build()
                        )
                    } else {
                        // Доступ уже предоставлен ранее
                        Log.d(TAG, "Доступ уже предоставлен ранее")
                        // handleAuthorizationSuccess(authorizationResult)
                        onSuccess(authorizationResult)

                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Failed to authorize", e)
                }
        }

        fun handleAuthorizationResult(
            activityResult: ActivityResult,
            pendingAction: (() -> Unit)?,
            activity: Activity,
            onSuccess: (authorizationResult: AuthorizationResult) -> Unit
        ) {
            try {
                if (activityResult.resultCode == RESULT_OK) {
                    val authorizationResult = Identity.getAuthorizationClient(activity)
                        .getAuthorizationResultFromIntent(activityResult.data)
                    pendingAction?.invoke()
                    onSuccess(authorizationResult)
                }
            } catch (e: ApiException) {
                Log.e(TAG, "Authorization failed", e)
            }
        }
    }
}

