package com.example.unitconvertor


import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Brush
import android.animation.TypeConverter
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateListOf
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.unitconvertor.ui.theme.UnitConvertorTheme
import kotlin.math.roundToInt






class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UnitConvertorTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    "Unit Converter",
                                    color = Color.White,
                                    modifier = Modifier.fillMaxWidth().wrapContentSize(Alignment.Center),
                                    fontSize = 34.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color.Black)
                        )
                    }
                ) { paddingValues ->
                    // Determine gradient colors based on the system theme
                    val gradientColors = if (isSystemInDarkTheme()) {
                        listOf(Color.Black, Color(0xFF800080)) // Dark mode colors (black to purple)
                    } else {
                        listOf(Color(0xFFe66465), Color(0xFF9198e5)) // Light mode colors (gray to light purple)
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    colors = gradientColors
                                )
                            )
                            .padding(paddingValues)
                    ) {
                        UnitConverter() // Your UnitConverter content
                    }
                }
            }
        }
    }
}


@SuppressLint("RememberReturnType")
@Composable
fun UnitConverter() {
    var inputValue by remember { mutableStateOf("") }
    var outputValue by remember { mutableStateOf("0.0") }
    var inputUnit by remember { mutableStateOf("Meters") }
    var outputUnit by remember { mutableStateOf("Meters") }
    var iExpanded by remember { mutableStateOf(false) }
    var oExpanded by remember { mutableStateOf(false) }
    var convertor = remember { mutableStateOf(1.0) }
    var oConvertor = remember { mutableStateOf(1.0) }
    val conversionHistory = remember { mutableStateListOf<String>() }
    var shareKey by remember { mutableStateOf(false) }


    fun convertUnits() {
        val inputValueAsADouble = inputValue.toDoubleOrNull()?: 0.0
        val result = (inputValueAsADouble * convertor.value *100.0 / oConvertor.value ).roundToInt() / 100.0
        outputValue = result.toString()

    }
    fun addToHistory(input: String, result: String, inputUnit: String, outputUnit: String) {
        if (conversionHistory.size >= 5) {
            conversionHistory.removeAt(0) // Keep only last 5 conversions
        }
        conversionHistory.add("$input $inputUnit to $result $outputUnit")
    }

    val context = LocalContext.current

    fun saveHistoryToPreferences(context: Context, conversionHistory: List<String>) {
        val sharedPref = context.getSharedPreferences("conversion_history", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        // Join history items with a delimiter (e.g., "|") to save as a single string
        val historyString = conversionHistory.joinToString("|")
        editor.putString("history", historyString)
        editor.apply()
    }

    // Function to load conversion history from SharedPreferences
    fun loadHistoryFromPreferences(context: Context): MutableList<String> {
        val sharedPref = context.getSharedPreferences("conversion_history", Context.MODE_PRIVATE)
        val historyString = sharedPref.getString("history", "")

        // Convert the saved string back to a list of history items
        return if (historyString.isNullOrEmpty()) mutableStateListOf() else historyString.split("|").toMutableList()
    }



    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = CenterHorizontally) {

        Spacer(modifier = Modifier.height(40.dp))
        Column {
            Text("Conversion History", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            conversionHistory.forEach { historyItem ->
                Text(historyItem)
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
        OutlinedTextField(
            value = inputValue,
            onValueChange = {
                // Ensure only digits are accepted
                inputValue = it.filter { char -> char.isDigit() || char == '.' } // Include decimal points for floating numbers
            },
            label = { Text("Input Value") },
            placeholder = { Text("Ex: 10") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number // Set keyboard type to number
            )
        )

        Text("$inputUnit \t\t\tto \t\t\t\t $outputUnit", fontSize = 20.sp)

        Spacer(modifier = Modifier.height(10.dp))
        Row (modifier = Modifier.padding(10.dp), horizontalArrangement = Arrangement.spacedBy(50.dp)) {
            Box {
                Button(onClick = {
                    iExpanded = !iExpanded
                }) {
                    Text(inputUnit)
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Arrow Drop Down")
                }
                DropdownMenu(modifier = Modifier.animateContentSize(),expanded = iExpanded, onDismissRequest = {
                    iExpanded = !iExpanded
                }) {
                    DropdownMenuItem(
                        text = { Text("Centimeters") },
                        onClick = {
                            iExpanded = !iExpanded
                            inputUnit = "Centimeters"
                            convertor.value = 0.01

                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Meters") },
                        onClick = {
                            iExpanded = !iExpanded
                            inputUnit = "Meters"
                            convertor.value = 1.0

                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Kilometers") },
                        onClick = {
                            iExpanded = !iExpanded
                            inputUnit = "Kilometers"
                            convertor.value = 100.0

                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Feet") },
                        onClick = {
                            iExpanded = !iExpanded
                            inputUnit = "Feet"
                            convertor.value = 0.3048

                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Inches") },
                        onClick = {
                            iExpanded = !iExpanded
                            inputUnit = "Inches"
                            convertor.value = 0.0254

                        }
                    )

                }
            }
            Box {
                Button(onClick = {
                    oExpanded = !oExpanded}) {
                    Text(outputUnit)
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Arrow Drop Down")
                }
                DropdownMenu(expanded = oExpanded, onDismissRequest = {
                    oExpanded = !oExpanded
                }) {
                    DropdownMenuItem(
                        text = { Text("Centimeters") },
                        onClick = {
                            oExpanded = !oExpanded
                            outputUnit = "Centimeters"
                            oConvertor.value = 0.01

                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Meters") },
                        onClick = {
                            oExpanded = !oExpanded
                            outputUnit = "Meters"
                            oConvertor.value = 1.0

                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Kilometers") },
                        onClick = {
                            oExpanded = !oExpanded
                            outputUnit = "Kilometers"
                            oConvertor.value = 100.0

                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Feet") },
                        onClick = {
                            oExpanded = !oExpanded
                            outputUnit = "Feet"
                            oConvertor.value = 0.3048

                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Inches") },
                        onClick = {
                            oExpanded = !oExpanded
                            outputUnit = "Inches"
                            oConvertor.value = 0.0254

                        }
                    )

                }
            }
            }
        Row(modifier = Modifier.padding(10.dp), horizontalArrangement = Arrangement.spacedBy(50.dp)) {
            Button(onClick = {
                convertUnits()
                addToHistory(inputValue, outputValue, inputUnit, outputUnit)
                shareKey = true
            Toast.makeText(context, "Conversion Successful!", Toast.LENGTH_SHORT).show()
        }) {
            Text("Convert")
        }
        }
        Row (modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(50.dp)) {
            Text("Result ≈ ${outputValue} $outputUnit", fontSize = 20.sp)
            Button(
                onClick = {
                    if (shareKey){
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, "Conversion result: $inputValue $inputUnit to ≈ $outputValue $outputUnit")
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Share result via"))
                }
                    },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Gray
                )
            ) {
                Icon(imageVector = Icons.Filled.Share, contentDescription = "Share")
            }
        }
        }

    }

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewUnitConverterApp() {
    UnitConvertorTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Unit Converter") },
                    colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color.Black)
                )
            }
        ) { paddingValues ->
            // Determine gradient colors based on the system theme
            val gradientColors = if (isSystemInDarkTheme()) {
                listOf(Color.Black, Color(0xFF800080)) // Dark mode colors (black to purple)
            } else {
                listOf(Color(0xFFe0e0e0), Color(0xFFd1c4e9)) // Light mode colors (gray to light purple)
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = gradientColors
                        )
                    )
                    .padding(paddingValues)
            ) {
                UnitConverter() // Your UnitConverter content
            }
        }
    }
}