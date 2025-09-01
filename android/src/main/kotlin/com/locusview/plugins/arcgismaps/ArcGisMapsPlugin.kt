package com.locusview.plugins.arcgismaps

import android.app.Activity
import android.content.Intent
import com.arcgismaps.ArcGISEnvironment
import com.arcgismaps.data.ArcGISFeature
import com.arcgismaps.data.QueryFeatureFields
import com.arcgismaps.data.QueryParameters
import com.arcgismaps.data.ServiceFeatureTable
import com.arcgismaps.data.SpatialRelationship
import com.arcgismaps.geometry.Envelope
import com.arcgismaps.geometry.Geometry
import com.arcgismaps.geometry.GeometryEngine
import com.arcgismaps.geometry.GeometryType
import com.arcgismaps.geometry.Point
import com.arcgismaps.geometry.Polyline
import com.arcgismaps.geometry.SpatialReference
import com.getcapacitor.JSArray
import com.getcapacitor.JSObject
import com.getcapacitor.Plugin
import com.getcapacitor.PluginCall
import com.getcapacitor.PluginMethod
import com.getcapacitor.annotation.CapacitorPlugin
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@CapacitorPlugin(name = "ArcGisMaps")
class ArcGisMapsPlugin : Plugin() {

  companion object {
    internal var result: CompletableDeferred<Result>? = null
  }

  data class Result(val ok: Boolean, val error: String? = null)

  private val ioScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

  @OptIn(ExperimentalCoroutinesApi::class)
  @PluginMethod
  fun signIn(call: PluginCall) {
    val a: Activity = activity ?: return call.reject("No host activity")

    val portalUrl = call.getString("portalUrl") ?: return call.reject("No portalUrl provided")
    val clientId = call.getString("clientId") ?: return call.reject("No clientId provided")
    val redirectUrl =
      call.getString("redirectUrl") ?: return call.reject("No redirectUrl provided")

    if (result?.isActive == true) return call.reject("A sign-in flow is already in progress")
    result = CompletableDeferred()

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
      if (r.ok) call.resolve()
      else call.reject(r.error ?: "Sign-in failed")
    }
  }

  @PluginMethod
  fun signOut(call: PluginCall) {
    try {
      val mgr = ArcGISEnvironment.authenticationManager
      mgr.arcGISCredentialStore.removeAll()
      call.resolve()
    } catch (t: Throwable) {
      call.reject("Failed to sign out: ${t.message}")
    }
  }

  @PluginMethod
  fun query(call: PluginCall) {
    val url = call.getString("layerUrl") ?: return call.reject("No layerUrl provided")
    val where = call.getString("where") ?: "1=1"
    val limit = call.getInt("limit") ?: 500

    val bbox = call.getObject("bbox")?.let { o ->
      val xmin = o.getDouble("xmin")
      val ymin = o.getDouble("ymin")
      val xmax = o.getDouble("xmax")
      val ymax = o.getDouble("ymax")
      Envelope(
        xMin = xmin, yMin = ymin, xMax = xmax, yMax = ymax,
        spatialReference = SpatialReference.wgs84()
      )
    }

    ioScope.launch {
      try {
        val table = ServiceFeatureTable(url).apply { load().getOrThrow() }

        val qp = QueryParameters().apply {
          whereClause = where
          maxFeatures = limit
          returnGeometry = true
          outSpatialReference = SpatialReference.wgs84()
          if (bbox != null) {
            geometry = if (table.spatialReference != null &&
              table.spatialReference != SpatialReference.wgs84()
            ) {
              GeometryEngine.projectOrNull(bbox, table.spatialReference!!)
            } else bbox
            spatialRelationship = SpatialRelationship.Intersects
          }
        }

        val fqResult = table
          .queryFeatures(qp, QueryFeatureFields.LoadAll)
          .getOrThrow()

        val features = JSArray()
        for (f in fqResult) {
          val ags = f as ArcGISFeature
          val attributes = JSObject().also { jo ->
            ags.attributes.forEach { (k, v) -> jo.put(k, v) }
          }
          val geometry = GeoJsonUtil.toJson(ags.geometry)

          features.put(
            JSObject().apply {
              put("geometry", geometry)
              put("attributes", attributes)
            }
          )
        }

        val info = table.layerInfo

        val fields = JSArray()
        val fieldAliases = JSObject()
        info?.fields?.forEach { f ->
          fields.put(JSObject().apply {
            put("name", f.name)
            put("type", f.fieldType)
            put("alias", f.alias)
            put("length", f.length)
          })
          fieldAliases.put(f.name, f.alias)
        }

        val wkid = table.spatialReference?.wkid ?: 4326
        val spatialReference = JSObject().apply {
          put("wkid", wkid)
          put("latestWkid", normalizeWkid(wkid))
        }

        call.resolve(
          JSObject().apply {
            put("displayFieldName", info?.displayFieldName)
            put("features", features)
            put("fieldAliases", fieldAliases)
            put("fields", fields)
            put("geometryType", geometryTypeString(info?.geometryType))
            put("spatialReference", spatialReference)
            put("url", info?.url)
            put("layerId", info?.serviceLayerId)
            put("layerName", info?.serviceLayerName)
            put("symbology", info?.drawingInfo?.renderer?.toJson())
          }
        )
      } catch (t: Throwable) {
        call.reject("Failed to get features: ${t.message}")
      }
    }
  }

  private fun geometryTypeString(type: GeometryType?): String? = when (type) {
    GeometryType.Point      -> "esriGeometryPoint"
    GeometryType.Multipoint -> "esriGeometryMultipoint"
    GeometryType.Polyline   -> "esriGeometryPolyline"
    GeometryType.Polygon    -> "esriGeometryPolygon"
    GeometryType.Envelope   -> "esriGeometryEnvelope"
    else                    -> null
  }

  private object GeoJsonUtil {
    fun toJson(g: Geometry?): JSObject? =
      when (g) {
        null -> null
        is Point -> point(g)
        is Polyline -> line(g)
        else -> null
      }

    private fun point(p: Point) =
      JSObject().apply {
        put("x", p.x)
        put("y", p.y)
      }

    private fun line(pl: Polyline): JSObject {
      val paths = JSArray()
      for (part in pl.parts) {
        val coords = JSArray()
        for (pt in part.points) coords.put(JSArray().apply { put(pt.x); put(pt.y) })
        paths.put(coords)
      }
      return JSObject().apply {
        put("paths", paths)
      }
    }
  }

  private fun normalizeWkid(wkid: Int?): Int? {
    return when (wkid) {
      102100, 102113 -> 3857  // Web Mercator
      else -> wkid
    }
  }
}
