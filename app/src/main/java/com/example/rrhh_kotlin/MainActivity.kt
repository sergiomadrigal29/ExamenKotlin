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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Habilita el uso de toda la pantalla
        setContent {
            RRHHkotlinTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RRHHForm(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

// Data class para encapsular los resultados
data class SalaryResult(
    val inss: Double,
    val ir: Double,
    val totalDeduccion: Double,
    val salarioNeto: Double
)

// Función para calcular el INSS
fun calculateINSS(salary: Double): Double {
    return salary * 0.07
}

// Función para calcular el IR (recibe también el INSS para evitar recalcular)
fun calculateIR(salary: Double, inss: Double): Double {
    val netMonthly = salary - inss
    val netAnnual = netMonthly * 12

    // Cálculo del IR según tabla
    val irAnual = when {
        netAnnual <= 100_000 -> 0.0
        netAnnual <= 200_000 -> (netAnnual - 100_000) * 0.15
        netAnnual <= 350_000 -> 15_000 + (netAnnual - 200_000) * 0.20
        netAnnual <= 500_000 -> 45_000 + (netAnnual - 350_000) * 0.25
        else -> 82_500 + (netAnnual - 500_000) * 0.30
    }

    return irAnual / 12
}

// Función principal de cálculo de salario
fun calculateSalary(nombre: String, salario: Double): SalaryResult? {
    if (nombre.isBlank() || salario <= 0) return null

    val inss = calculateINSS(salario)
    val ir = calculateIR(salario, inss)
    val total = inss + ir
    val neto = salario - total

    return SalaryResult(inss, ir, total, neto)
}

// Composable que representa el formulario de RRHH
@Composable
fun RRHHForm(modifier: Modifier = Modifier) {
    var nombre by remember { mutableStateOf("") }
    var salarioInput by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<SalaryResult?>(null) }
    var error by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Título
        Text("Ingrese los datos del trabajador", style = MaterialTheme.typography.titleLarge)

        // Campo nombre
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre completo") },
            modifier = Modifier.fillMaxWidth()
        )

        // Campo salario
        OutlinedTextField(
            value = salarioInput,
            onValueChange = { salarioInput = it },
            label = { Text("Salario mensual") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        // Mensaje de error
        if (error.isNotEmpty()) {
            Text(text = error, color = MaterialTheme.colorScheme.error)
        }

        // Botones
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = {
                error = ""
                val salario = salarioInput.toDoubleOrNull()
                if (salario == null || salario <= 0) {
                    error = "Ingrese un salario válido."
                    result = null
                } else if (nombre.isBlank()) {
                    error = "Ingrese el nombre completo."
                    result = null
                } else {
                    result = calculateSalary(nombre, salario)
                }
            }) {
                Text("Calcular")
            }

            Button(onClick = {
                nombre = ""
                salarioInput = ""
                result = null
                error = ""
            }) {
                Text("Nuevo")
            }

            Button(onClick = {
                (context as? ComponentActivity)?.finishAffinity()
            }) {
                Text("Salir")
            }
        }

        // Mostrar resultados
        result?.let {
            Spacer(modifier = Modifier.height(12.dp))
            Text("INSS: %.2f".format(it.inss))
            Text("IR: %.2f".format(it.ir))
            Text("Total Deducción: %.2f".format(it.totalDeduccion))
            Text("Salario Neto: %.2f".format(it.salarioNeto))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Instrucciones
        Text("Proceso paso a paso:", style = MaterialTheme.typography.titleMedium)
        Text("1. Ingrese el nombre completo y el salario mensual.")
        Text("2. Presione 'Calcular' para ver los resultados.")
        Text("3. Use 'Nuevo' para limpiar los campos.")
        Text("4. Use 'Salir' para cerrar la aplicación.")
    }
}
