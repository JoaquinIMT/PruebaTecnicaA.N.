package com.example.arkusnexus

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Place(val PlaceId: String, val PlaceName: String, val Address: String,
            val Category: String, val IsOpenNow: String, val Latitude: Float,
            val Longitude: Float, val Thumbnail: String, val Rating: Float,
            val IsPetFriendly: Boolean, val AddressLine1: String, val AddressLine2: String,
            val PhoneNumber: String, val Site: String, var Distance : Float): Parcelable