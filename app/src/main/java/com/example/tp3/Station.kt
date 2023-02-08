package com.example.tp3

import android.os.Parcel
import android.os.Parcelable

class Station(
    private val id: Int,                // id de la station yélo
    private val name: String,           // nom de la station yélo
    private val lon: Double,            // longitude de la station yélo
    private val lat: Double,                    // latitude de la station yélo
    private val nombreVeloDisponible: Int,      // nombre de vélo disponible à la station yélo
    private val nombreVelo: Int                 // nombre de vélo accepté à la station yélo
    ) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readInt(),
        parcel.readInt()
    )

    fun getName(): String {
        return name
    }

    fun getLon(): Double {
        return lon
    }

    fun getLat(): Double {
        return lat
    }

    fun getStatut() : String{
        return "$nombreVeloDisponible / $nombreVelo places"
    }

    override fun toString(): String {
        return "$name \n $nombreVeloDisponible place(s) libre(s) sur $nombreVelo"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeDouble(lon)
        parcel.writeDouble(lat)
        parcel.writeInt(nombreVeloDisponible)
        parcel.writeInt(nombreVelo)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Station> {
        override fun createFromParcel(parcel: Parcel): Station {
            return Station(parcel)
        }

        override fun newArray(size: Int): Array<Station?> {
            return arrayOfNulls(size)
        }
    }

}
