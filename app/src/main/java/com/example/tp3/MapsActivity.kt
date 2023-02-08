package com.example.tp3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.tp3.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private var station: Station? = null
    private var listStation: ArrayList<Station>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        station = intent.getParcelableExtra("station")      // On récupère une station

        if(station != null){
            val textViewStation = findViewById<TextView>(R.id.textViewStation)

            // On affiche les informations de la station dans le textView
            textViewStation.text = station!!.getName()
        }

        listStation = intent.getParcelableArrayListExtra("stationList")     // On récupère l'arraylist des stations


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // On effectue le traitement si l'arrayList est non null et comporte des stations
        if(listStation != null){
            if(listStation!!.size > 0){
                for (i in 0 until listStation!!.size) {
                    val stationVelo = listStation!![i]
                    addMarker(stationVelo)
                }
            }
            // On déplace la caméra et zoom sur le point de la première station
            val coord = LatLng(listStation!![0].getLat(),listStation!![0].getLon())
            mMap.moveCamera(CameraUpdateFactory.zoomTo(13F))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(coord))
        }

        // On effectue le traitement si la station est non null
        if(station != null){

            // On rajoute le marker avec la station
            addMarker(station!!)

            // On déplace la caméra et zoom sur le marker de la station
            val coord = LatLng(station!!.getLat(),station!!.getLon())
            mMap.moveCamera(CameraUpdateFactory.zoomTo(14F))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(coord))       // On déplace la caméra sur ces coordonnées
        }

    }

    private fun addMarker(station : Station){

        // On extrait les coordonnées de la station
        val coord = LatLng(station.getLat(),station.getLon())

        // On crée un marker
        val marker: MarkerOptions = MarkerOptions()
            .position(coord)
            .title(station.getName())           // On rajoute le nom de la station
            .snippet(station.getStatut())       // On rajoute le statut de la station

        // On rajoute le marker
        mMap.addMarker(marker)          // On rajoute à le marker de cette station
    }
}