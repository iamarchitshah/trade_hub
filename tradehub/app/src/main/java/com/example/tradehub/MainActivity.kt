@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.tradehub

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.*

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.navigation.NavController
import androidx.navigation.compose.*

import coil.compose.AsyncImage

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TradeHubApp()
        }
    }
}

data class Product(
    val id: Int,
    val title: String,
    val price: String,
    val description: String,
    val imageUrl: String,
    val seller: String,
    val rating: Float = 4.5f
)

val sampleProducts = listOf(
    Product(
        1,
        "iPhone 12",
        "₹30,000",
        "128GB, good condition",
        "https://via.placeholder.com/150",
        "Rahul"
    ),
    Product(
        2,
        "Gaming Chair",
        "₹5,000",
        "Very comfortable",
        "https://via.placeholder.com/150",
        "Amit"
    )
)

@Composable
fun TradeHubApp() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "home") {

        composable("home") {
            HomeScreen(navController)
        }

        composable("details/{id}") { backStack ->
            val id = backStack.arguments?.getString("id")?.toInt() ?: 0
            ProductDetailScreen(id, navController)
        }

        composable("chat") {
            ChatScreen()
        }
    }
}

@Composable
fun HomeScreen(navController: NavController) {

    var searchQuery by remember { mutableStateOf("") }

    val filteredProducts = sampleProducts.filter {
        it.title.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = { Text("TradeHub") }
                )

                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search products...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }
        }
    ) { padding ->

        LazyColumn(modifier = Modifier.padding(padding)) {

            items(filteredProducts) { product ->
                ProductCard(product, navController)
            }
        }
    }
}
@Composable
fun ProductCard(product: Product, navController: NavController) {

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable {
                navController.navigate("details/${product.id}")
            },
        elevation = CardDefaults.cardElevation(6.dp),
        shape = RoundedCornerShape(12.dp)
    ) {

        Row(modifier = Modifier.padding(10.dp)) {

            AsyncImage(
                model = product.imageUrl,
                contentDescription = null,
                modifier = Modifier.size(80.dp)
            )

            Column(modifier = Modifier.padding(start = 10.dp)) {

                Text(product.title, fontWeight = FontWeight.Bold)
                Text(product.price, color = Color(0xFF2E7D32))
                Text("⭐ ${product?.rating} / 5", fontWeight = FontWeight.Bold)
                Text("Trusted Seller ✔", color = Color.Gray)
            }
        }
    }
}
@Composable
fun AddProductScreen(navController: NavController) {

    var title by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {

        Text("Add Product", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        TextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
        TextField(value = price, onValueChange = { price = it }, label = { Text("Price") })
        TextField(value = description, onValueChange = { description = it }, label = { Text("Description") })

        Spacer(modifier = Modifier.height(10.dp))

        Button(onClick = {
            Toast.makeText(
                navController.context,
                "Product Added!",
                Toast.LENGTH_SHORT
            ).show()

            navController.popBackStack()
        }) {
            Text("Submit")
        }
    }
}
@Composable
fun ProductDetailScreen(id: Int, navController: NavController) {

    val context = LocalContext.current
    val product = sampleProducts.find { it.id == id }

    Column(modifier = Modifier.padding(16.dp)) {

        AsyncImage(
            model = product?.imageUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(product?.title ?: "", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text(product?.price ?: "", color = Color(0xFF2E7D32))

        Spacer(modifier = Modifier.height(10.dp))

        Text(product?.description ?: "")

        Spacer(modifier = Modifier.height(10.dp))

        Text("Seller: ${product?.seller}")
        Text("Rating: ⭐ ${product?.rating}")

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { navController.navigate("chat") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Chat with Seller")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                Toast.makeText(context, "Payment Successful!", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Buy Now")
        }
    }
}

@Composable
fun ChatScreen() {

    var message by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<String>() }

    Column(modifier = Modifier.fillMaxSize().padding(10.dp)) {

        Text("Chat", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(messages) {
                Text("• $it", modifier = Modifier.padding(4.dp))
            }
        }

        Row {
            TextField(
                value = message,
                onValueChange = { message = it },
                modifier = Modifier.weight(1f)
            )

            Button(onClick = {
                if (message.isNotEmpty()) {
                    messages.add(message)
                    message = ""
                }
            }) {
                Text("Send")
            }
        }
    }
}