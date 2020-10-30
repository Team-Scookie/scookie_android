package com.example.scookie

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    companion object {
        const val TAG = "MainActivity TAG"
        const val PERMISSION_REQUEST_CODE = 1001
        const val ZOOM_LEVEL = 17f
        const val MIN_TIME_MS: Long = 0 // 1초 1000
        const val MIN_DISTANCE_M : Float = 50f // 50M 50f
        const val LINE_WIDTH : Float = 15f // 15px
        const val THREAD_MS : Long = 1000 // 1초

        // 시간 계산 관련 변수들
        var startTime : Long = System.currentTimeMillis()
        var startDate: Date = Date(startTime)
        var now : Long = System.currentTimeMillis()
        var nowDate : Date = Date(now)
        const val BASE_TIME : Long = 3 // 3분

        // 위치 관련 변수
        lateinit var mGoogleMap : GoogleMap
        lateinit var currentLatLng: LatLng
        lateinit var endLatLng: LatLng
        lateinit var preLatLng : LatLng

        // 스레드 관련 변수들
        private var mHandler: Handler? = null
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkSelfPermission()
        checkMapsApiKey()
        setOnBtnClickListener()
        initMap()
    }

    private fun getMinOfStay() : Long {
        now = System.currentTimeMillis()
        nowDate = Date(now)
        var duration : Long = nowDate.time - startDate.time

        return duration/60000
    }

    var polylines : MutableList<Polyline> = mutableListOf()

    private fun setOnBtnClickListener() {
        actMainBtnStart.setOnClickListener {
            preLatLng = currentLatLng

            @SuppressLint("HandlerLeak")
            mHandler = object : Handler() {
                // THREAD_MS 마다 실행됩니다.
                override fun handleMessage(msg: Message) {
                    // 이전 위치에서 이동해서(AND) 3분 이상 있었던 경우
                    if(preLatLng != endLatLng && getMinOfStay() >= BASE_TIME) {
                        drawPath(preLatLng, endLatLng)
                        preLatLng = endLatLng
                        Toast.makeText(applicationContext, "3분 경과", Toast.LENGTH_SHORT).show()
                    }
                    Toast.makeText(applicationContext, "hi", Toast.LENGTH_SHORT).show()
                }
            }

            thread(start = true) {
                while (true) {
                    Thread.sleep(THREAD_MS)
                    mHandler?.sendEmptyMessage(0)
                }
            }
        }
    }

    //define the listener
    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            startTime = System.currentTimeMillis()
            startDate = Date(startTime)

            currentLatLng = LatLng(location.latitude, location.longitude)
            endLatLng = currentLatLng

            cameraZoom()
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    private fun drawPath(startLatLng: LatLng, endLatLng: LatLng) {
        /** TODO
         *  width dpToPx 사용하기
         */
        val dash : PatternItem =  Dash(PATTERN_WIDTH)
        val gap : PatternItem = Gap(PATTERN_WIDTH)
        var dashedLine : List<PatternItem> = listOf(gap, dash);

        val options :PolylineOptions = PolylineOptions()
            .add(startLatLng)
            .add(endLatLng)
            .geodesic(true)
            .color(R.color.colorBrown)
            .width(20f)
            .pattern(dashedLine)
            .startCap(RoundCap())

        polylines.add(mGoogleMap.addPolyline(options))
    }

    @SuppressLint("MissingPermission")
    private fun requestLocation() {
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        // MIN_TIME_MS 와 MIN_DISTANCE_M 를 만족할 시, onLocationChanged 함수를 호출합니다.
        locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_MS,
            MIN_DISTANCE_M,
            locationListener);
        locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME_MS,MIN_DISTANCE_M,
            locationListener);
    }

    private fun checkMapsApiKey() {
        if (getString(R.string.maps_api_key).isEmpty()) {
            Toast.makeText(this, "Add your own API key in secure.properties as MAPS_API_KEY=YOUR_API_KEY", Toast.LENGTH_LONG).show()
        }
    }

    private fun initMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.actMainMapFrag) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap?.apply {
            mGoogleMap = googleMap
            checkPermission()
            mGoogleMap.isMyLocationEnabled = true
            mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15F))
            setDefaultLocation(mGoogleMap)
        }
    }

    /** TODO
     * RequiresApi 알아보
     */
    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkPermission() {
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            checkSelfPermission()
        }
        return
    }

    private fun setDefaultLocation(mGoogleMap: GoogleMap) {
        mGoogleMap.apply {
            val sydney = LatLng(-33.852, 151.211)
            addMarker(
                MarkerOptions()
                    .position(sydney)
                    .title("Marker in Sydney")
            )
            moveCamera(CameraUpdateFactory.newLatLng(sydney))
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkSelfPermission() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG,"Permission granted")
            requestLocation()
        } else {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), // 1
                PERMISSION_REQUEST_CODE) // 2
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {  // 1
                if (grantResults.isEmpty()) {  // 2
                    throw RuntimeException("Empty permission result")
                }
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {  // 3
                    Log.d(TAG, "permission granted")
                } else {
                    if (shouldShowRequestPermissionRationale(
                            Manifest.permission.ACCESS_FINE_LOCATION)) { // 4
                        Log.d(TAG, "User declined, but i can still ask for more")
                        requestPermissions(
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            PERMISSION_REQUEST_CODE)
                    } else {
                        Log.d(TAG, "User declined and i can't ask")
                        showDialogToGetPermission()   // 5
                    }
                }
            }
        }
    }

    private fun showDialogToGetPermission(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Permisisons request")
            .setMessage("We need the location permission for some reason. " +
                    "You need to move on Settings to grant some permissions")

        builder.setPositiveButton("OK") { dialogInterface, i ->
            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", packageName, null))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)   // 6
        }
        builder.setNegativeButton("Later") { dialogInterface, i ->
            // ignore
        }
        val dialog = builder.create()
        dialog.show()
    }
}

