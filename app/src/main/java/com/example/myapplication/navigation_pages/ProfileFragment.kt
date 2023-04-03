package com.example.myapplication.navigation_pages

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.myapplication.R

class ProfileFragment : Fragment() {
    private lateinit var myImageView: ImageView
override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
    val view = inflater.inflate(R.layout.fragment_profile, container, false)
    myImageView = view.findViewById(R.id.circle_image)
    val randomImageUrl = "https://picsum.photos/200" // Replace with URL of your image
    Glide.with(this)
        .load(randomImageUrl)
        .placeholder(R.drawable.circular_background) // placeholder image while the actual image loads
        //.error(R.drawable.error_image) // image to display if there is an error loading the actual image
        .into(myImageView)
    return view
    }

}