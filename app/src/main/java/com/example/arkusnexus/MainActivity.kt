package com.example.arkusnexus

import android.Manifest
import android.annotation.SuppressLint
import android.provider.Settings
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException


const val PERMISSION_REQUEST = 10

class MainActivity : AppCompatActivity() {

    lateinit var reciclerView: RecyclerView
    private var permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)

    lateinit var locationManager: LocationManager
    private var hasGps = false
    private var hasNetwork = false
    private var locationGps: Location? = null
    private var locationNetwork: Location? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission(permissions)) {
                start()
            } else {
                requestPermissions(permissions, PERMISSION_REQUEST)
            }
        } else {
            start()
        }



    }

    fun start(){
        reciclerView = findViewById(R.id.reciclerviewplaces)

        reciclerView.layoutManager = LinearLayoutManager(this)

        val location = getLocation()

        fetchJson(location?.latitude!!.toFloat(), location.longitude.toFloat())

    }

    fun fetchJson(lat: Float, lng : Float){
        val urlAPI = "http://www.mocky.io/v2/5bf3ce193100008900619966"

        val request = Request.Builder().url(urlAPI).build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object: Callback{

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                println(body)

                val gson = GsonBuilder().create()

                val places: Array<Place> = gson.fromJson(body, Array<Place>::class.java)

                var j = 0
                for(i in places){
                    i.Distance = distFrom(lat, lng, i.Latitude, i.Longitude)
                    places[j] = i
                    j += 1
                }
                places.sortWith(compareBy({ it.Distance }))
                runOnUiThread {
                    reciclerView.adapter = PlacesAdapter(places, lat, lng)

                }

            }
            override fun onFailure(call: Call, e: IOException) {
                println("Failure")
                toast("Fail")
            }
        })

    }

    fun toast(comment: String){
        Toast.makeText(this,comment,Toast.LENGTH_SHORT).show()
    }

    fun distFrom(lat1: Float, lng1: Float, lat2: Float, lng2: Float): Float {

        val earthRadius = 6371000.0
        val dLat = Math.toRadians((lat2 - lat1).toDouble())
        val dLng = Math.toRadians((lng2 - lng1).toDouble())

        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1.toDouble())) * Math.cos(
            Math.toRadians(lat2.toDouble())
        ) * Math.sin(dLng / 2) * Math.sin(dLng / 2)

        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

        return (earthRadius * c).toFloat()
    }

    //Get location functions
    fun checkPermission(permissionArray: Array<String>): Boolean {
        var allSuccess = true
        for (i in permissionArray.indices) {
            if (checkCallingOrSelfPermission(permissionArray[i]) == PackageManager.PERMISSION_DENIED)
                allSuccess = false
        }
        return allSuccess
    }


    @SuppressLint("MissingPermission")
    private fun getLocation(): Location? {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        if(hasGps){

            if (hasGps) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0F, object: LocationListener {
                    override fun onLocationChanged(location: Location?) {
                        if (location != null) {
                            locationGps = location

                        }
                    }

                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

                    }

                    override fun onProviderEnabled(provider: String?) {

                    }

                    override fun onProviderDisabled(provider: String?) {

                    }

                })

                val localGpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (localGpsLocation != null)
                    locationGps = localGpsLocation
            }

            if(locationGps!= null){
                return locationGps
            }

        }else {
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
        return null
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST) {
            var allSuccess = true
            for (i in permissions.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    allSuccess = false
                    val requestAgain = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && shouldShowRequestPermissionRationale(permissions[i])
                    if (requestAgain) {
                        toast("You must grant permission to access the app")
                        finish()
                    } else {
                        toast("Go to settings and enable the permission")
                        finish()
                    }
                }
            }
            if(allSuccess){
                start()
            }

        }
    }

}
