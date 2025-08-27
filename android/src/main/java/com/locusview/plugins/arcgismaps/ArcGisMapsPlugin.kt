package com.locusview.plugins.arcgismaps

import android.app.Activity
import android.content.Intent
import com.arcgismaps.portal.Portal
import com.arcgismaps.toolkit.authentication.AuthenticatorState
import com.arcgismaps.httpcore.authentication.OAuthUserConfiguration
import com.getcapacitor.JSObject
import com.getcapacitor.Plugin
import com.getcapacitor.PluginCall
import com.getcapacitor.PluginMethod
import com.getcapacitor.annotation.CapacitorPlugin
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@CapacitorPlugin(name = "ArcGisMaps")
class ArcGisMapsPlugin : Plugin() {

  companion object {
    internal var result: CompletableDeferred<Result>? = null
  }

  data class Result(val ok: Boolean, val error: String? = null)

  private var oAuthConfig: OAuthUserConfiguration? = null
  private val authenticatorState = AuthenticatorState()

  @PluginMethod
  fun init(call: PluginCall) {
    val portalUrl = call.getString("portalUrl")
    val clientId = call.getString("clientId")
    val redirectUrl = call.getString("redirectUrl")

    if (portalUrl.isNullOrBlank()) return call.reject("No portalUrl provided")
    if (clientId.isNullOrBlank()) return call.reject("No clientId provided")
    if (redirectUrl.isNullOrBlank()) return call.reject("No redirectUrl provided")

    oAuthConfig = OAuthUserConfiguration(
      portalUrl = portalUrl,
      clientId = clientId,
      redirectUrl = redirectUrl
    )
    authenticatorState.oAuthUserConfigurations = listOf(oAuthConfig!!)

    call.resolve()
  }

  @PluginMethod
  fun signIn(call: PluginCall) {
    val a: Activity = activity ?: return call.reject("No host activity")
    val cfg = oAuthConfig ?: return call.reject("Call init() before signIn()")

    // Only one sign-in at a time
    if (result?.isActive == true) return call.reject("A sign-in flow is already in progress")
    result = CompletableDeferred()

    // Fast path: already signed in? (Credential cached by the SDK)
    CoroutineScope(Dispatchers.Main).launch {
      try {
        val portal = Portal(cfg.portalUrl, Portal.Connection.Authenticated)
        // Load on background; load() is suspending in 200.8
        kotlinx.coroutines.withContext(Dispatchers.IO) { portal.load() }
        // If we got here without throwing, we were already signed in
        result?.complete(Result(ok = true))
      } catch (_: Throwable) {
        // Not signed in yet → launch UI
        a.runOnUiThread {
          val intent = Intent(a, AuthActivity::class.java).apply {
            putExtra("portalUrl", cfg.portalUrl)
            putExtra("clientId", cfg.clientId)
            putExtra("redirectUrl", cfg.redirectUrl)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
          }
          a.startActivity(intent)
        }
      }
    }

    result!!.invokeOnCompletion {
      val r = result!!.getCompleted()
      // Clear for next run
      result = null
      if (r.ok) call.resolve(JSObject().put("ok", true))
      else call.reject(r.error ?: "Sign-in failed")
    }
  }
}
