package com.locusview.plugins.arcgismaps

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import com.arcgismaps.httpcore.authentication.OAuthUserConfiguration
import com.arcgismaps.portal.Portal
import com.arcgismaps.toolkit.authentication.AuthenticatorState
import com.arcgismaps.toolkit.authentication.DialogAuthenticator

class AuthActivity : ComponentActivity() {
  private val authenticatorState = AuthenticatorState()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val portalUrl   = intent.getStringExtra("portalUrl")
    val clientId    = intent.getStringExtra("clientId")
    val redirectUrl = intent.getStringExtra("redirectUrl")
    if (portalUrl.isNullOrBlank() || clientId.isNullOrBlank() || redirectUrl.isNullOrBlank()) {
      ArcGisMapsPlugin.result?.complete(ArcGisMapsPlugin.Result(false, "Missing OAuth params"))
      return finish()
    }

    authenticatorState.oAuthUserConfigurations = listOf(
      OAuthUserConfiguration(
        portalUrl = portalUrl,
        clientId = clientId,
        redirectUrl = redirectUrl
      )
    )

    setContent {
      DialogAuthenticator(authenticatorState)

      LaunchedEffect(Unit) {
          val portal = Portal(portalUrl, Portal.Connection.Authenticated)
          val result = portal.load()

          if (result.isSuccess) {
            ArcGisMapsPlugin.result?.let { if (!it.isCompleted) it.complete(ArcGisMapsPlugin.Result(true)) }
          } else {
            val err = result.exceptionOrNull()
            val msg = err?.message ?: "User canceled"
            ArcGisMapsPlugin.result?.let { if (!it.isCompleted) it.complete(ArcGisMapsPlugin.Result(false, msg)) }
          }
          finish()
      }
    }
  }
}
