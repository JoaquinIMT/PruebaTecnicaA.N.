package com.example.arkusnexus

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RatingBar
import android.widget.TextView
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.maps.GeoApiContext
import kotlinx.android.synthetic.main.activity_place_detail.*
import com.google.maps.model.DirectionsResult
import com.google.maps.DirectionsApiRequest
import com.google.android.gms.maps.model.Marker
import com.google.maps.PendingResult


class PlaceDetail : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var lat: Float? = null
    private var lng: Float? = null

    lateinit var placeName: TextView
    lateinit var placeUbication: TextView
    lateinit var distance: TextView
    lateinit var url: TextView
    lateinit var phoneNumber: TextView
    lateinit var duration: TextView
    lateinit var ratingBar: RatingBar
    lateinit var direccions: View
    lateinit var call: View
    lateinit var wbst: View

    lateinit var mGeoApiContext: GeoApiContext

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_detail)

        val place = intent.getParcelableExtra<Place>("Place")
        val lat2 = intent.getFloatExtra("Lat", Float.MAX_VALUE)
        val lng2 = intent.getFloatExtra("Lng", Float.MAX_VALUE)

        placeName = findViewById(R.id.place_name)
        placeUbication = findViewById(R.id.place_ubication)
        distance = findViewById(R.id.distance)
        url = findViewById(R.id.url)
        phoneNumber = findViewById(R.id.phone_number)
        ratingBar = findViewById(R.id.ratingbarnew)
        direccions = findViewById(R.id.directionsbtn)
        call = findViewById(R.id.callbtn)
        wbst = findViewById(R.id.wbstbtn)
        duration = findViewById(R.id.duration)

        lat = place.Latitude
        lng = place.Longitude
        /*mapView = findViewById(R.id.mapView)

        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)*/
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map1) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mGeoApiContext = GeoApiContext.Builder().apiKey(getString(R.string.google_maps_key)).build()



        placeName.text = place.PlaceName

        if(place.IsPetFriendly){
            placeUbication.append("\nDogs Allowed")
        }else{
            placeUbication.append("\nDogs Not Allowed")
            petfriendlyimage.setImageResource(R.drawable.ic_dog_not_friendly)
        }

        placeUbication.append("\n${place.AddressLine1}")
        placeUbication.append("\n${place.AddressLine2}")

        distance.text = place.Distance.toString() +" m"
        phoneNumber.text = place.PhoneNumber
        url.text = place.Site
        ratingBar.rating = place.Rating

        direccions.setOnClickListener {
            val uri = Uri.parse("geo:$lat,$lng?q=$lat,$lng")
            val intent = Intent(Intent.ACTION_VIEW,uri)
            intent.`package` ="com.google.android.apps.maps"
            startActivity(intent)
        }

        call.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:"+place.PhoneNumber)
            startActivity(intent)
        }

        wbst.setOnClickListener {

            val intent = Intent(applicationContext,BrowserActivity::class.java)

            intent.putExtra("WebPage",place.Site)

            startActivity(intent)


        }
        duration.text = getTime(lat!!,lng!!, lat2, lng2)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val location = LatLng(lat!!.toDouble(),lng!!.toDouble())

        val bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker)
        val marker : Marker = mMap.addMarker(MarkerOptions().position(location).icon(bitmapDescriptor).alpha(1.0f))
        //mMap.addMarker(MarkerOptions().position(location).title("Marker Place"))
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(location))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location,15.5f))

    }

    fun getTime(lat: Float, lng: Float, lat2: Float, lng2: Float): String{
        var time = "Cargando"

        val destination = com.google.maps.model.LatLng(lat2.toDouble(), lng2.toDouble())
        val directions = DirectionsApiRequest(mGeoApiContext)
        directions.origin(com.google.maps.model.LatLng(lat.toDouble(), lng.toDouble()))

        fun modify(string: String){
            time = string
        }

        directions.destination(destination).setCallback(object : PendingResult.Callback<DirectionsResult> {

            override fun onResult(result: DirectionsResult) {
                modify(result.routes[0].legs[0].duration.humanReadable)
                /* Log.d(FragmentActivity.TAG, "onResult: routes: " + result.routes[0].toString())
                 Log.d(FragmentActivity.TAG, "onResult: geocodedWayPoints: " + result.geocodedWaypoints[0].toString())*/
            }

            override fun onFailure(e: Throwable) {
                modify("error $e")
                /*Log.e(FragmentActivity.TAG, "onFailure: " + e.message)*/

            }
        })


        return time
    }
}
