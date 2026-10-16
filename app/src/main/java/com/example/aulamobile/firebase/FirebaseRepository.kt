package com.example.aulatelas.firebase

import androidx.compose.runtime.mutableStateListOf
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

data class Car(
    val id: String = "",
    val name: String = "",
)

object FirebaseRepository {
    private val database =
        Firebase.database("https://aulamobile-e45e0-default-rtdb.firebaseio.com/")
            .getReference("carros")

    val cars = mutableStateListOf<Car>()

    init {
        getCars {
            cars.clear()
            cars.addAll(it)
        }
    }

    fun createCar(car: Car) {
        val id = database.push().key!!
        database.child(id).setValue(car.copy(id = id))

    }

    fun editCar(car: Car) {
        database.child(car.id).setValue(car)
    }

    fun deleteCar(car: Car) {
        database.child(car.id).removeValue()
    }

    fun getCars(onDataChange: (List<Car>) -> Unit) {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val carsList = mutableListOf<Car>()
                snapshot.children.forEach {
                    it.getValue(Car::class.java)?.let { car -> carsList.add(car) }

                }
                onDataChange(carsList)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}