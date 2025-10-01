package com.example.rrhh_kotlin

// Importaciones necesarias para la actividad y Jetpack Compose
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.rrhh_kotlin.ui.theme.RRHHkotlinTheme

// Clase principal de la actividad
class MainActivity : ComponentActivity() {
    // Método que se ejecuta al crear la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Habilita el uso de toda la pantalla
        setContent {
            // Aplica el tema de la app
            RRHHkotlinTheme {
                // Scaffold proporciona la estructura visual básica
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Llama al formulario principal de la app
                    RRHHForm(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

// Composable que representa el formulario de RRHH
@Composable
fun RRHHForm(modifier: Modifier = Modifier) {
    // Variables de estado para los campos y resultados
    var nombre by remember { mutableStateOf("") } // Nombre del trabajador
    var salarioInput by remember { mutableStateOf("") } // Salario ingresado como texto
    var inss by remember { mutableStateOf<Double?>(null) } // Resultado del INSS
    var ir by remember { mutableStateOf<Double?>(null) } // Resultado del IR
    var totalDeduccion by remember { mutableStateOf<Double?>(null) } // Total de deducciones
    var salarioNeto by remember { mutableStateOf<Double?>(null) } // Salario neto
    var error by remember { mutableStateOf("") } // Mensaje de error
    val context = LocalContext.current // Contexto de la app

    // Estructura vertical de la pantalla
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Título del formulario
        Text("Ingrese los datos del trabajador", style = MaterialTheme.typography.titleLarge)

        // Campo para ingresar el nombre completo
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre completo") },
            modifier = Modifier.fillMaxWidth()
        )

        // Campo para ingresar el salario mensual
        OutlinedTextField(
            value = salarioInput,
            onValueChange = { salarioInput = it },
            label = { Text("Salario mensual") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), // Solo permite números
            modifier = Modifier.fillMaxWidth()
        )

        // Muestra el mensaje de error si existe
        if (error.isNotEmpty()) {
            Text(text = error, color = MaterialTheme.colorScheme.error)
        }

        // Fila de botones: Calcular, Nuevo y Salir
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Botón para calcular deducciones y salario neto
            Button(onClick = {
                error = "" // Limpia el error anterior
                val salario = salarioInput.toDoubleOrNull() // Convierte el salario a Double
                // Validación de campos
                if (nombre.isBlank()) {
                    error = "Ingrese el nombre completo."
                } else if (salario == null || salario <= 0) {
                    error = "Ingrese un salario válido."
                } else {
                    // Cálculo del INSS (7%)
                    inss = salario * 0.07
                    // Cálculo del IR (15% si salario > 30000, si no 0)
                    ir = if (salario > 30000) salario * 0.15 else 0.0
                    // Suma de deducciones
                    totalDeduccion = inss!! + ir!!
                    // Salario neto
                    salarioNeto = salario - totalDeduccion!!
                }
            }) {
                Text("Calcular")
            }
            // Botón para limpiar todos los campos y resultados
            Button(onClick = {
                nombre = ""
                salarioInput = ""
                inss = null
                ir = null
                totalDeduccion = null
                salarioNeto = null
                error = ""
            }) {
                Text("Nuevo")
            }
            // Botón para salir de la aplicación
            Button(onClick = {
                (context as? ComponentActivity)?.finishAffinity() // Cierra la app
            }) {
                Text("Salir")
            }
        }

        // Mostrar resultados si ya se calculó
        if (salarioNeto != null) {
            Spacer(modifier = Modifier.height(12.dp)) // Espacio visual
            // Muestra el resultado de cada cálculo
            Text("INSS: %.2f".format(inss))
            Text("IR: %.2f".format(ir))
            Text("Total Deducción: %.2f".format(totalDeduccion))
            Text("Salario Neto: %.2f".format(salarioNeto))
        }

        Spacer(modifier = Modifier.height(16.dp)) // Espacio visual
        // Documentación paso a paso para el usuario
        Text("\uD83D\uDCDD Proceso paso a paso:", style = MaterialTheme.typography.titleMedium)
        Text("1. Ingrese el nombre completo y el salario mensual.")
        Text("2. Presione 'Calcular' para ver los resultados.")
        Text("3. Use 'Nuevo' para limpiar los campos.")
        Text("4. Use 'Salir' para cerrar la aplicación.")
    }
}
