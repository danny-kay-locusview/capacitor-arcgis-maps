package com.locusview.plugins.arcgismaps

import android.content.Intent
import com.arcgismaps.ApiKey
import com.arcgismaps.ArcGISEnvironment
import com.arcgismaps.LicenseKey
import com.arcgismaps.httpcore.authentication.ArcGISCredentialStore
import com.arcgismaps.httpcore.authentication.NetworkCredentialStore
import com.arcgismaps.httpcore.authentication.OAuthUserConfiguration
import com.arcgismaps.httpcore.authentication.OAuthUserCredential
import com.arcgismaps.portal.Portal
import com.arcgismaps.toolkit.authentication.AuthenticatorState
import com.arcgismaps.toolkit.authentication.completeBrowserAuthenticationChallenge
import com.arcgismaps.toolkit.authentication.completeOAuthSignIn
import com.arcgismaps.toolkit.authentication.launchCustomTabs
import com.getcapacitor.JSArray
import com.getcapacitor.JSObject
import com.getcapacitor.Plugin
import com.getcapacitor.PluginCall
import com.getcapacitor.PluginMethod
import com.getcapacitor.annotation.CapacitorPlugin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@CapacitorPlugin(name = "ArcGisMaps")
class ArcGisMapsPlugin : Plugin() {

    // coroutine scope for async calls
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    // toolkit auth state + config we’ll set during init
    private lateinit var authState: AuthenticatorState
    private lateinit var oAuthConfig: OAuthUserConfiguration

    @PluginMethod
    fun init(call: PluginCall) {
        val portalUrl   = call.getString("portalUrl") ?: return call.reject("portalUrl required")
        val clientId    = call.getString("clientId") ?: return call.reject("clientId required")
        val redirectUri = call.getString("redirectUri") ?: return call.reject("redirectUri required")
        val licenseKey  = call.getString("licenseKey")
        val apiKey      = call.getString("apiKey")

        scope.launch {
            try {
                val arcgisStore = ArcGISCredentialStore.createWithPersistence().getOrThrow()
                val networkStore = NetworkCredentialStore.createWithPersistence().getOrThrow()
                ArcGISEnvironment.authenticationManager.arcGISCredentialStore = arcgisStore
                ArcGISEnvironment.authenticationManager.networkCredentialStore = networkStore

                // optional: production licensing + api key (e.g., basemap before login)
                licenseKey?.let {
                    val licenseKeyObj = LicenseKey.create(it) ?: throw IllegalArgumentException("Invalid license string")
                    ArcGISEnvironment.setLicense(licenseKeyObj)
                }

                apiKey?.let {
                    val apiKeyObj = ApiKey.create(it) ?: throw IllegalArgumentException("Invalid API key")
                    ArcGISEnvironment.apiKey = apiKeyObj
                }

                // toolkit: a single state object becomes our challenge handlers
                authState = AuthenticatorState(
                    setAsArcGISAuthenticationChallengeHandler = true,
                    setAsNetworkAuthenticationChallengeHandler = true
                )

                // provide OAuth config to toolkit
                oAuthConfig = OAuthUserConfiguration(
                    portalUrl   = portalUrl,
                    clientId    = clientId,
                    redirectUrl = redirectUri
                )

                authState.oAuthUserConfigurations = listOf(oAuthConfig)

                call.resolve()
            } catch (t: Throwable) {
                call.reject("Init failed: ${t.message}")
            }
        }
    }

    @PluginMethod
    fun signIn(call: PluginCall) {
        if (!::oAuthConfig.isInitialized) return call.reject("Call init() first")

        scope.launch {
            try {
                // Start OAuth; Toolkit opens Custom Tabs and manages the redirect for us
                val result = OAuthUserCredential.create(oAuthConfig) { signIn ->
                    bridge.activity.launchCustomTabs(signIn) // open authorize URL
                }

                // Will throw if cancelled/failed
                val credential = result.getOrThrow()

                // Store credential; subsequent requests auto-authenticate
                ArcGISEnvironment.authenticationManager.arcGISCredentialStore.add(credential)

                // Optionally load the authenticated portal to fetch profile details
                val portal = Portal(oAuthConfig.portalUrl, Portal.Connection.Authenticated)
                portal.load().getOrThrow()
                val u = portal.user
                val tokenInfo = credential.getTokenInfo().getOrNull()

                val userJson = JSObject().apply {
                    put("username", u?.username ?: "")
                    put("fullName", u?.fullName)
                    put("email", u?.email)
                    put("organizationId", u?.organizationId)
                    put("role", u?.role?.encoding)
                    put("privileges", JSArray((u?.privileges ?: emptyList()).map { it.type.toString() }.toTypedArray()))
                    put("tokenExpires", tokenInfo?.expirationDate?.toString())
                }

                call.resolve(userJson)
            } catch (t: Throwable) {
                call.reject("Sign-in failed: ${t.message}")
            }
        }
    }

    @PluginMethod
    fun testAuth(call: PluginCall) {
        val securedLayerUrl = call.getString("securedLayerUrl")

        scope.launch {
            try {
                val portal = Portal(oAuthConfig.portalUrl, Portal.Connection.Authenticated)
                portal.load().getOrThrow()
                val u = portal.user
                val portalInfo = portal.portalInfo

                val result = JSObject().apply {
                    put("portalUrl", oAuthConfig.portalUrl)
                    put("isAuthenticated", true)
                    put("username", u?.username ?: "")
                    put("fullName", u?.fullName)
                    put("email", u?.email)
                    put("organizationId", u?.organizationId)
                    put("role", u?.role?.encoding)
                    put("privileges", JSArray((u?.privileges ?: emptyList()).map { it.type.toString() }.toTypedArray()))
                    put("orgTitle", portalInfo?.organizationName)
                }

                // 2) (Optional) Probe a secured feature layer: return a quick count
                if (!securedLayerUrl.isNullOrBlank()) {
                    val table = com.arcgismaps.data.ServiceFeatureTable(securedLayerUrl)
                    table.load().getOrThrow()
                    val count = table.queryFeatureCount(com.arcgismaps.data.QueryParameters()).getOrThrow()
                    result.put("securedLayerUrl", securedLayerUrl)
                    result.put("securedLayerFeatureCount", count)
                }

                call.resolve(result)
            } catch (t: Throwable) {
                call.reject("Auth test failed: ${t.message}")
            }
        }
    }

    // Ensure Toolkit can finish OAuth if the redirect hits our main activity
    override fun handleOnNewIntent(intent: Intent?) {
        super.handleOnNewIntent(intent)
        if (::authState.isInitialized) {
            authState.completeBrowserAuthenticationChallenge(intent)
        }
    }
}
