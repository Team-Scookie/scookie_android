package com.example.scookie

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.scookie.model.NearByPlaceData
import com.example.scookie.model.PlaceData
import com.example.scookie.ui.adapter.map.LocationRVAdapter
import com.example.scookie.ui.adapter.map.NearByPlaceRVAdapter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_map.*
import java.util.*
import kotlin.concurrent.thread

class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    companion object {
        const val TAG = "MainActivity TAG"
        const val PERMISSION_REQUEST_CODE = 1001
        const val ZOOM_LEVEL = 17f
        const val MIN_TIME_MS: Long = 0 // 1초 1000
        const val MIN_DISTANCE_M : Float = 50f // 50M 50f
        const val LINE_WIDTH : Float = 8f // 8px
        const val GAP_WIDTH : Float = 2f // 2px
        const val THREAD_MS : Long = 1000 // 1초

        // 시간 계산 관련 변수들
        var startTime : Long = System.currentTimeMillis()
        var startDate: Date = Date(startTime)
        var now : Long = System.currentTimeMillis()
        var nowDate : Date = Date(now)
        const val BASE_TIME : Long = 1 // 1분
        const val MARKER_TIME : Long = 30 // 30분

        // 위치 관련 변수
        lateinit var mGoogleMap : GoogleMap
        lateinit var currentLatLng: LatLng
        lateinit var endLatLng: LatLng
        var preLatLng : LatLng? = null

        // 스레드 관련 변수들
        private var mHandler: Handler? = null

        // Marker RVView
        lateinit var locationRVAdapter: LocationRVAdapter

        // NearByRVView
        lateinit var nearByPlaceRVAdapter: NearByPlaceRVAdapter

        // Bottom drawer
        lateinit var behavior : BottomSheetBehavior<ConstraintLayout>
        lateinit var drawer: ConstraintLayout
        lateinit var coordinator: CoordinatorLayout

        private var polylines : MutableList<Polyline> = mutableListOf()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
//        setStatusBar()
        checkSelfPermission()
        checkMapsApiKey()
        initMap()
        setRecyclerView()
    }

    private fun setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //make fullscreen, so we can draw behind status bar
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE

            //make status bar color transparent
            window.statusBarColor = Color.TRANSPARENT
            var flags = window.decorView.systemUiVisibility
            // make dark status bar icons
            flags =
                flags xor View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //make navigation bar color white
                window.navigationBarColor = Color.WHITE
                // make dark navigation bar icons
                flags =
                    flags xor View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            }
            window.decorView.systemUiVisibility = flags
        }
    }

    private fun setRecyclerView() {
        var dataList = mutableListOf<PlaceData>()
        dataList.add(PlaceData("2020년 12월 13일","주소를 입력해주세요","서울특별시 강남구 도곡동 418-10","37"))
        dataList.add(PlaceData("2020년 12월 13일","주소를 입력해주세요","서울특별시 강남구 도곡동 418-10","37"))
        locationRVAdapter =
            LocationRVAdapter(this, dataList)
        act_main_rv_location_card.adapter = locationRVAdapter
        act_main_rv_location_card.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
    }

    fun setDrawer() {
        coordinator = findViewById(R.id.bottom_sheet_coordinator)
        drawer = findViewById(R.id.bottom_sheet_drawer) as ConstraintLayout
        behavior = BottomSheetBehavior.from(drawer)

        val btn_close = drawer.findViewById(R.id.bottom_sheet_close_btn) as Button
        btn_close.setOnClickListener {
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        if (behavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        } else {
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        setNearByRecyclerView()
    }

    private fun setNearByRecyclerView() {
        var dataList = mutableListOf<NearByPlaceData>()
        dataList.add(NearByPlaceData("장소 분류","아름다운 이땅에 금수강산","서울특별시 강남구 도곡동 418-10"))
        dataList.add(NearByPlaceData("장소 분류","주소를 입력해주세요","이문동 111-9"))
        dataList.add(NearByPlaceData("장소 분류","아름다운 이땅에 금수강산","서울특별시 강남구 도곡동 418-10"))
        dataList.add(NearByPlaceData("장소 분류","주소를 입력해주세요","이문동 111-9"))
        dataList.add(NearByPlaceData("장소 분류","아름다운 이땅에 금수강산","서울특별시 강남구 도곡동 418-10"))
        dataList.add(NearByPlaceData("장소 분류","주소를 입력해주세요","이문동 111-9"))
        dataList.add(NearByPlaceData("장소 분류","아름다운 이땅에 금수강산","서울특별시 강남구 도곡동 418-10"))
        dataList.add(NearByPlaceData("장소 분류","주소를 입력해주세요","이문동 111-9"))
        dataList.add(NearByPlaceData("장소 분류","아름다운 이땅에 금수강산","서울특별시 강남구 도곡동 418-10"))
        dataList.add(NearByPlaceData("장소 분류","주소를 입력해주세요","이문동 111-9"))
        nearByPlaceRVAdapter =
            NearByPlaceRVAdapter(
                this,
                dataList
            )
        val rvNearByPlace= drawer.findViewById(R.id.bottom_sheet_rv_item_near_by_place) as RecyclerView
        rvNearByPlace.adapter = nearByPlaceRVAdapter
        rvNearByPlace.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
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

        trackingLocation()
    }

    private fun setDefaultLocation(mGoogleMap: GoogleMap) {
        mGoogleMap.apply {
            val sydney = LatLng(-33.852, 151.211)
            moveCamera(CameraUpdateFactory.newLatLng(sydney))
        }
    }

    private fun trackingLocation() {
        @SuppressLint("HandlerLeak")
        mHandler = object : Handler() {
            // THREAD_MS 마다 실행됩니다.
            override fun handleMessage(msg: Message) {
                if(preLatLng != null) {
                    // 이전 위치에서 이동해서(AND) 1분 이상 있었던 경우
                    if (preLatLng != endLatLng && getMinOfStay() >= BASE_TIME) {
                        drawPath(preLatLng!!, endLatLng)
                        preLatLng = endLatLng
                    }

                    // 30분 이상 머문 경우
                    if(preLatLng != endLatLng && getMinOfStay() >= MARKER_TIME) {
                        createMarker(preLatLng!!, getMinOfStay())
                    }
                }
            }
        }

        thread(start = true) {
            while (true) {
                Thread.sleep(THREAD_MS)
                mHandler?.sendEmptyMessage(0)
            }
        }
    }

    private fun createMarker(latLng: LatLng, minOfStay: Long) {
        mGoogleMap.apply {
            val newLatLng = latLng
            addMarker(
                MarkerOptions()
                    .position(newLatLng)
                    .title(minOfStay.toString())

            )
            /** TODO
             *  마커 생성시 마커 비춰야하는지?
             */
            // moveCamera(CameraUpdateFactory.newLatLng(newLatLng))
        }
    }

    private fun getMinOfStay() : Long {
        now = System.currentTimeMillis()
        nowDate = Date(now)
        val duration : Long = nowDate.time - startDate.time

        return duration/60000
    }

    private fun drawPath(startLatLng: LatLng, endLatLng: LatLng) {
        /** TODO
         *  width dpToPx 사용하기
         */
        val dash : PatternItem =  Dash(LINE_WIDTH)
        val gap : PatternItem = Gap(GAP_WIDTH)
        var dashedLine : List<PatternItem> = listOf(gap, dash);

        val options :PolylineOptions = PolylineOptions()
            .add(startLatLng)
            .add(endLatLng)
            .geodesic(true)
            .color(R.color.colorBrown)
            .width(LINE_WIDTH)
            .pattern(dashedLine)
            .startCap(RoundCap())

        polylines.add(mGoogleMap.addPolyline(options))
    }

    @SuppressLint("MissingPermission")
    private fun requestLocation() {
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        // MIN_TIME_MS 와 MIN_DISTANCE_M 를 만족할 시, onLocationChanged 함수를 호출합니다.
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_MS,
            MIN_DISTANCE_M,
            locationListener)
    }

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            startTime = System.currentTimeMillis()
            startDate = Date(startTime)

            currentLatLng = LatLng(location.latitude, location.longitude)
            if(preLatLng == null) setPreLatLng(currentLatLng)
            endLatLng = currentLatLng!!

            cameraZoom()
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    private fun setPreLatLng(latLng: LatLng) {
        preLatLng = latLng
    }

    fun cameraZoom() {
        val zoom = CameraUpdateFactory.zoomTo(ZOOM_LEVEL);
        mGoogleMap.apply {
            moveCamera(CameraUpdateFactory.newLatLng(currentLatLng))
            animateCamera(zoom)
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

