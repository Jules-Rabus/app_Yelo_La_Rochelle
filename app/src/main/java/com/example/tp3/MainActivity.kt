package com.example.tp3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.CountDownLatch

class MainActivity : AppCompatActivity() {


    private val stationList : ArrayList<Station> = ArrayList()  // On stock dans un arrayList les stations
    private val httpclient = OkHttpClient()         // Client http
    private var body : JSONObject? = null            // Retour de l'api en Json

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // appel de la fonction permettant l'initialisation de toutes les stations
        this.initListStations()

        // On rajoute un onclick listener sur le bouton afin de lancer la map avec toutes les stations
        val bouton = findViewById<Button>(R.id.bouton)
        bouton.setOnClickListener{

            // On passe l'arrayList des stations à MapsActivity via un intent
            val intent = Intent(this@MainActivity, MapsActivity::class.java).apply {
                putParcelableArrayListExtra("stationList", stationList)
            }

            // On lance la map
            startActivity(intent)
        }
    }

    private fun initListStations(){

        // On appel la fonction de requête api
        this.run()

        // On vérifie que le retour de l'api n'est pas null
        if(body != null){

            // On parse le JsonArray afin de récupèrer toutes les stations
            val stations = body!!.getJSONArray("records")

            // On itère sur chaque
            for (i in 0 until stations.length()) {

                // On récupère et traite toutes les informations d'une station
                val fields = stations.getJSONObject(i).getJSONObject("fields")
                val id = fields.getInt("_id")
                var nom = fields.getString("station_nom")
                nom = nom.substring(2,nom.length)
                val lon = fields.getString("station_longitude").toDouble()
                val lat = fields.getString("station_latitude").toDouble()
                val nombreVeloDispo = fields.getInt("velos_disponibles")
                val nombreVelo = fields.getInt("nombre_emplacements")

                // On crée la station avec ses informations
                val station = Station(id,nom,lon,lat,nombreVeloDispo,nombreVelo)

                // On rajoute la station à l'arrayList
                stationList.add(station)
            }

            // On donne l'arrayList au listView afin de faire l'affichage des stations
            val listViewStation = findViewById<ListView>(R.id.listViewStation)
            listViewStation.adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_list_item_1, stationList)

            // On rajoute un onItemClick lister afin de lancer la map avec la vue de la station sélectionné
            listViewStation.setOnItemClickListener { _, _, position, _ ->

                // On passe la station sélectionné à MapsActivity via un intent
                val intent = Intent(this@MainActivity, MapsActivity::class.java).apply {
                    putExtra("station", stationList[position])
                }
                // On lance la map
                startActivity(intent)
            }
        }
    }

    private fun run() {

        // On forme la requête à l'API
        val request = Request.Builder()
            .url("https://api.agglo-larochelle.fr/production/opendata/api/records/1.0/search/dataset=yelo___disponibilite_des_velos_en_libre_service&facet=station_nom&api-key=xxxxx")      // Penser à mettre la clé api
            .build()

        // On initialise le compteur permettant d'attendre le retour de la la requête async
        val countDownLatch = CountDownLatch(1)

        httpclient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                countDownLatch.countDown()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        // Si la reqûete ressort un code d'erreur on affiche l'url
                        println("Erreur 404 : ${request.url}")
                    }

                    // On récupère et transforme le retour de la requete en JsonObject
                    body = JSONObject(response.body!!.string())
                }
                // On débloque l'éxécution une fois la requête reçu
                countDownLatch.countDown()
            }
        })

        // On attends que le compteur soit à 0 pour relancer l'éxécution
        countDownLatch.await()

    }
}