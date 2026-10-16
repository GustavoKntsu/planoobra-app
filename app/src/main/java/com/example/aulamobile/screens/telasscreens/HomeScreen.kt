package com.example.aulatelas.screens.telasscreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.example.aulatelas.components.EditCarBottomSheet
import com.example.aulatelas.firebase.Car
import com.example.aulatelas.firebase.FirebaseRepository
import com.example.aulatelas.ui.theme.green

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {

    var showDialog by remember { mutableStateOf(false) }
    var newCarInput by remember { mutableStateOf("") }
    var showEditCarSheet by remember { mutableStateOf(false) }
    var currentEditTitle by remember { mutableStateOf("") }
    var carToEdit by remember { mutableStateOf<Car?>(null) }

    val cars = remember { mutableStateListOf<Car>() }
    LaunchedEffect(Unit) {
        FirebaseRepository.getCars {
            cars.clear()
            cars.addAll(it)
        }
    }
    Scaffold(
        floatingActionButton = {
            Example(
                onClick = { showDialog = true }
            )
        }
    ) { padding ->

        if (showDialog) {
            AlertDialog(
                onDismissRequest = {
                    showDialog = false
                    newCarInput = ""
                },
                title = { Text("Cadastrar Carro") },
                text = {
                    OutlinedTextField(
                        value = newCarInput,
                        onValueChange = { newCarInput = it },
                        label = { Text("Novo carro") },
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (newCarInput.isNotEmpty()) {
                                FirebaseRepository.createCar(Car(name = newCarInput))
                                newCarInput = ""
                                showDialog = false
                            }
                        }
                    ) {
                        Text("Cadastrar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showDialog = false
                        newCarInput = ""
                    }) { Text("Cancelar") }
                },
                properties =
                    DialogProperties(
                        dismissOnBackPress = true,
                        dismissOnClickOutside = false
                    )
            )
        }

        LazyColumn(
            contentPadding = padding,
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(
                items = cars,
                key = { car -> car.id }) { car ->
                Card(
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,

                                ) {
                                Text(
                                    text = car.name,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.weight(6f)
                                )
                                IconButton(
                                    modifier = Modifier.weight(2f),
                                    onClick = {
                                        showEditCarSheet = true
                                        currentEditTitle = car.name
                                        carToEdit = car

                                    }) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Editar Carro"
                                    )
                                }


                                IconButton(
                                    modifier = Modifier.weight(2f),
                                    onClick = {
                                        FirebaseRepository.deleteCar(car)
                                    }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Deletar Carro"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        if (showEditCarSheet) {
            EditCarBottomSheet(
                modifier = Modifier.fillMaxWidth(),
                onDismissRequest = {
                    showEditCarSheet = false
                    carToEdit = null

                },
                onConfirmClick = {
                    FirebaseRepository.editCar(carToEdit!!.copy(name = currentEditTitle))
                    showEditCarSheet = false
                    carToEdit = null
                },
                onCarNameChange = { newTitle ->
                    currentEditTitle = newTitle
                },
                carName = currentEditTitle,

                )
        }
    }
}


@Composable
fun Example(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = { onClick() },
        containerColor = green,
        contentColor = Color.White,


        ) {
        Icon(Icons.Filled.Add, "Floating action button.")
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    MaterialTheme {
        HomeScreen()
    }

}
