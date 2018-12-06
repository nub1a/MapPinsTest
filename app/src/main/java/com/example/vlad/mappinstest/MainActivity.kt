package com.example.vlad.mappinstest

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.errors.ApiException
import com.google.maps.model.DirectionsResult
import com.google.maps.model.LatLng
import com.google.maps.model.TravelMode


import java.io.IOException
import java.util.ArrayList

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private val places = ArrayList<LatLng>()
    private var mapsApiKey: String? = null
    private var width: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        places.add(LatLng(55.754724, 37.621380))
        places.add(LatLng(55.760133, 37.618697))
        places.add(LatLng(55.764753, 37.591313))
        places.add(LatLng(55.728466, 37.604155))

        mapsApiKey = this.resources.getString(R.string.google_maps_key)

        width = resources.displayMetrics.widthPixels
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val markers = arrayOfNulls<MarkerOptions>(places.size)
        for (i in places.indices) {
            markers[i] = MarkerOptions()
                .position(com.google.android.gms.maps.model.LatLng(places[i].lat, places[i].lng))
            googleMap.addMarker(markers[i])
        }


        val geoApiContext = GeoApiContext.Builder()
            .apiKey(mapsApiKey)
            .build()
        var result: DirectionsResult? = null
        try {
            result = DirectionsApi.newRequest(geoApiContext)
                .mode(TravelMode.WALKING)
                .origin(places[0])
                .destination(places[places.size - 1])
                .waypoints(places[1], places[2]).await()
        } catch (e: ApiException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val path = result!!.routes[0].overviewPolyline.decodePath()
        val line = PolylineOptions()

        val latLngBuilder = LatLngBounds.Builder()

        for (i in path.indices) {
            line.add(com.google.android.gms.maps.model.LatLng(path[i].lat, path[i].lng))
            latLngBuilder.include(com.google.android.gms.maps.model.LatLng(path[i].lat, path[i].lng))
        }

        line.width(16f).color(R.color.colorPrimary)

        googleMap.addPolyline(line)

        val latLngBounds = latLngBuilder.build()
        val track = CameraUpdateFactory.newLatLngBounds(latLngBounds, width, width, 25)
        googleMap.moveCamera(track)
    }
}
