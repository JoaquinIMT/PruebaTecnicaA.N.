package com.example.arkusnexus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_place_detail.*

class PlaceDetail : AppCompatActivity() {

    lateinit var placeName: TextView
    lateinit var placeUbication: TextView
    lateinit var distance: TextView
    lateinit var url: TextView
    lateinit var phoneNumber: TextView
    lateinit var direccions: View
    lateinit var call: View
    lateinit var wbst: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_detail)

        val place = intent.getParcelableExtra<Place>("Place")

        placeName = findViewById(R.id.place_name)
        placeUbication = findViewById(R.id.place_ubication)
        distance = findViewById(R.id.distance)
        url = findViewById(R.id.url)
        phoneNumber = findViewById(R.id.phone_number)

        placeName.text = place.PlaceName

        if(place.IsPetFriendly){
            placeUbication.append("\nDogs Allowed")
        }else{
            placeUbication.append("\nDogs Not Allowed")
            petfriendlyimage.visibility = View.INVISIBLE
        }

        placeUbication.append("\n${place.AddressLine1}")
        placeUbication.append("\n${place.AddressLine2}")

        distance.text = place.Distance.toString()
        phoneNumber.text = place.PhoneNumber
        url.text = place.Site

    }
}
