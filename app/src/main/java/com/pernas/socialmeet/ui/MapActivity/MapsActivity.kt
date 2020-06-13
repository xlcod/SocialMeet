package com.pernas.socialmeet.ui.MapActivity

import android.Manifest
import android.app.ActivityOptions
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.pernas.socialmeet.R
import com.pernas.socialmeet.ui.quedadas.QuedadasActivity
import kotlinx.android.synthetic.main.activity_maps.*
import java.net.URL
import java.util.concurrent.Executors

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val LOCATION_REQUEST_CODE = 101


    override fun onPause() {
        super.onPause()
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)


        val bottomNavigation = findViewById<MeowBottomNavigation>(R.id.bottomNavigation)
        bottomNavigation.add(MeowBottomNavigation.Model(1, R.drawable.quedadas_icon))
        bottomNavigation.add(MeowBottomNavigation.Model(2, R.drawable.map_icon))

        bottomNavigation.show(2)

        bottomNavigation.setOnClickMenuListener {
            when (it.id) {
                1 -> {
                    startActivity(
                        Intent(this, QuedadasActivity::class.java),
                        ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
                    )
                }

                2 -> {
                    startActivity(
                        Intent(this, MapsActivity::class.java),
                        ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
                    )
                }
            }
        }
        init()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (mMap != null) {
            val permission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )

            if (permission == PackageManager.PERMISSION_GRANTED) {
                mMap.isMyLocationEnabled = true
            } else {
                requestPermission(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    LOCATION_REQUEST_CODE
                )
            }
        }
        mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
        val mapSettings = mMap.uiSettings
        mapSettings?.isZoomControlsEnabled = true

        mMap.setPadding(0, 0, 0, 150)
    }

    private fun init() {
        input_search.setOnEditorActionListener { v, actionId, event ->


            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE
                || event.action == KeyEvent.ACTION_DOWN || event.action == KeyEvent.KEYCODE_ENTER
            ) {
                geoLocate()
                true
            } else {
                false
            }
        }
    }

    private fun geoLocate() {
        val searchInfo = input_search.text.toString()

        val geocoder: Geocoder = Geocoder(this)

        var directions = listOf<Address>()

        try {
            directions = geocoder.getFromLocationName(searchInfo, 1)

        } catch (e: Exception) {
            Log.e("Error locate ", e.toString())
        }
        if (directions.size > 0) {
            var place: Address = directions[0]
            Log.e("geolocatesearched ", place.toString())

            moveCamera(LatLng(place.latitude, place.longitude), 14.0f, place.getAddressLine(0))

        }
    }

    private fun moveCamera(long: LatLng, zoom: Float, title: String) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(long, zoom))


        val marker: Marker = mMap.addMarker(
            MarkerOptions()
                .position(long)
                .title(title)
        )
    }

    private fun requestPermission(
        permissionType: String,
        requestCode: Int
    ) {

        ActivityCompat.requestPermissions(
            this,
            arrayOf(permissionType), requestCode
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {

        when (requestCode) {
            LOCATION_REQUEST_CODE -> {

                if (grantResults.isEmpty() || grantResults[0] !=
                    PackageManager.PERMISSION_GRANTED
                ) {
                    Toast.makeText(
                        this,
                        "Unable to show location - permission required",
                        Toast.LENGTH_LONG
                    ).show()
                } else {

                    val mapFragment = supportFragmentManager
                        .findFragmentById(R.id.map) as SupportMapFragment
                    mapFragment.getMapAsync(this)
                }
            }
        }
    }
}
