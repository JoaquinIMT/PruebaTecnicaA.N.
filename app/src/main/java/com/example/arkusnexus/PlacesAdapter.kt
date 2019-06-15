package com.example.arkusnexus

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.dog_row.view.*

class PlacesAdapter(val placeArray: Array<Place>, val lat: Float, val lng: Float) : RecyclerView.Adapter<DogViewHolder>() {

    override fun getItemCount(): Int {

        return placeArray.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val lineOfRow = layoutInflater.inflate(R.layout.dog_row,parent,false)
        return DogViewHolder(lineOfRow)

    }



    override fun onBindViewHolder(holder: DogViewHolder, position: Int) {
        val place = placeArray[position]

        val thumbnailImage = holder.view.imageView
        Picasso.get().load(place.Thumbnail).resize(200,200).centerCrop()
            .placeholder(R.drawable.ic_image_black_24dp).error(R.drawable.ic_broken_image_black_24dp).into(thumbnailImage)

        holder.view.place_name.text = place.PlaceName
        holder.view.adress.text = place.AddressLine1
        holder.view.city.text = place.AddressLine2
        if(!place.IsPetFriendly){
            holder.view.petfriendlyimage.setImageResource(R.drawable.ic_dog_not_friendly)
            holder.view.petfriendlytext.text = "Not Pet Friendly"
        }else{
            holder.view.petfriendlyimage.setImageResource(R.drawable.ic_dog_friendly)
            holder.view.petfriendlytext.text = "Pet Friendly"
        }
        holder.view.ratingbarnew.rating = place.Rating
        holder.view.distance.text = place.Distance.toString()+" m"
        holder.place = place

        holder.lat = lat
        holder.lng = lng

    }

}


class DogViewHolder(var view: View, var place: Place? = null,var lat: Float? = null ,var lng: Float? = null): RecyclerView.ViewHolder(view){

    init {
        view.setOnClickListener {
            val intent = Intent(view.context,PlaceDetail::class.java)
            intent.putExtra("Place",place)
            intent.putExtra("Lat",lat)
            intent.putExtra("Lng",lng)
            view.context.startActivity(intent)
        }
    }



}