package com.locusview.plugins.arcgismaps

import android.app.Activity
import android.content.Intent
import com.getcapacitor.JSObject
import com.getcapacitor.Plugin
import com.getcapacitor.PluginCall
import com.getcapacitor.PluginMethod
import com.getcapacitor.annotation.CapacitorPlugin
import kotlinx.coroutines.CompletableDeferred

@CapacitorPlugin(name = "ArcGisMaps")
class ArcGisMapsPlugin : Plugin() {

  companion object {
    internal var result: CompletableDeferred<Result>? = null
  }

  data class Result(val ok: Boolean, val error: String? = null)

  @PluginMethod
  fun signIn(call: PluginCall) {
    val a: Activity = activity ?: return call.reject("No host activity")

    val portalUrl = call.getString("portalUrl") ?: return call.reject("No portalUrl provided")
    val clientId = call.getString("clientId") ?: return call.reject("No clientId provided")
    val redirectUrl = call.getString("redirectUrl") ?: return call.reject("No redirectUrl provided")

    if (result?.isActive == true) return call.reject("A sign-in flow is already in progress")
    result = CompletableDeferred()

    // Launch Compose auth UI always — simpler, no need for "already signed in" fast path unless you want it
    a.startActivity(
      Intent(a, AuthActivity::class.java).apply {
        putExtra("portalUrl", portalUrl)
        putExtra("clientId", clientId)
        putExtra("redirectUrl", redirectUrl)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
    )

    result!!.invokeOnCompletion {
      val r = result!!.getCompleted()
      result = null
      if (r.ok) call.resolve(JSObject().put("ok", true))
      else call.reject(r.error ?: "Sign-in failed")
    }
  }
}
