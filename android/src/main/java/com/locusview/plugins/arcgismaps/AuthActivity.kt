package com.locusview.plugins.arcgismaps

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import com.arcgismaps.portal.Portal
import com.arcgismaps.toolkit.authentication.AuthenticatorState
import com.arcgismaps.toolkit.authentication.DialogAuthenticator
import com.arcgismaps.httpcore.authentication.OAuthUserConfiguration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthActivity : ComponentActivity() {

  private val authenticatorState = AuthenticatorState()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val portalUrl = intent.getStringExtra("portalUrl")
    val clientId = intent.getStringExtra("clientId")
    val redirectUrl = intent.getStringExtra("redirectUrl")

    if (portalUrl.isNullOrBlank() || clientId.isNullOrBlank() || redirectUrl.isNullOrBlank()) {
      ArcGisMapsPlugin.result?.complete(ArcGisMapsPlugin.Result(false, "Missing OAuth params"))
      finish(); return
    }

    authenticatorState.oAuthUserConfigurations = listOf(
      OAuthUserConfiguration(
        portalUrl = portalUrl,
        clientId = clientId,
        redirectUrl = redirectUrl
      )
    )

    setContent {
      // 200.8 signature: just pass the state
      DialogAuthenticator(authenticatorState)

      // Kick a protected load; Toolkit handles the browser login
      LaunchedEffect(Unit) {
        try {
          val portal = Portal(portalUrl, Portal.Connection.Authenticated)
          withContext(Dispatchers.IO) { portal.load() }
          ArcGisMapsPlugin.result?.complete(ArcGisMapsPlugin.Result(ok = true))
        } catch (t: Throwable) {
          ArcGisMapsPlugin.result?.complete(ArcGisMapsPlugin.Result(false, t.message))
        } finally {
          finish()
        }
      }
    }
  }
}
