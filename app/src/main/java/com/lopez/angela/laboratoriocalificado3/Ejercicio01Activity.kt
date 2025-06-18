package com.lopez.angela.laboratoriocalificado3

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.lopez.angela.laboratoriocalificado3.databinding.ActivityEjercicio01Binding
import kotlinx.coroutines.launch

class Ejercicio01Activity : AppCompatActivity() {

    private lateinit var binding: ActivityEjercicio01Binding
    private lateinit var teacherAdapter: TeacherAdapter
    private var teachersList = mutableListOf<Teacher>()

    private var currentTeacherForCall: Teacher? = null

    private val requestCallPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                currentTeacherForCall?.let { makeCall(it) }
            } else {
                Toast.makeText(this, "Permiso de llamada denegado.", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEjercicio01Binding.inflate(layoutInflater)
        setContentView(binding.root)


        title = getString(R.string.title_activity_ejercicio01)

        setupRecyclerView()
        fetchTeachersData()
    }

    private fun setupRecyclerView() {
        teacherAdapter = TeacherAdapter(
            teachersList,
            onItemClick = { teacher ->
                currentTeacherForCall = teacher
                checkAndRequestCallPermission(teacher)
            },
            onItemLongClick = { teacher ->
                sendEmail(teacher)
            }
        )
        binding.recyclerViewTeachers.apply {
            adapter = teacherAdapter
        }
    }

    private fun fetchTeachersData() {
        Log.d("Ejercicio01Activity", "fetchTeachersData called")
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                Log.d("Ejercicio01Activity", "Attempting to fetch teachers from network...")
                val response = RetrofitClient.instance.getTeachers()
                Log.d("Ejercicio01Activity", "Response received. Successful: ${response.isSuccessful}, Code: ${response.code()}")

                if (response.isSuccessful) {
                    val teacherResponseObject = response.body()

                    val fetchedTeachersList = teacherResponseObject?.teachers

                    if (fetchedTeachersList != null) {
                        Log.d("Ejercicio01Activity", "Response body is not null. Fetched ${fetchedTeachersList.size} teachers.")
                        if (fetchedTeachersList.isEmpty()) {
                            Log.d("Ejercicio01Activity", "Fetched teachers list is empty.")
                            Toast.makeText(this@Ejercicio01Activity, "No se encontraron profesores (lista vacía desde API).", Toast.LENGTH_SHORT).show()
                        }
                        teachersList.clear()
                        teachersList.addAll(fetchedTeachersList) // Usamos la lista extraída
                        Log.d("Ejercicio01Activity", "Updating adapter. Teachers list size: ${teachersList.size}")
                        teacherAdapter.updateData(teachersList)
                    } else {

                        Log.e("Ejercicio01Activity", "Response successful but body or internal teachers list is null.")
                        Toast.makeText(this@Ejercicio01Activity, getString(R.string.error_fetching_teachers) + " (Datos internos nulos)", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Log.e("Ejercicio01Activity", "Error en la respuesta: ${response.code()} - ${response.message()}")
                    Toast.makeText(this@Ejercicio01Activity, getString(R.string.error_fetching_teachers) + " (Código: ${response.code()})", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Log.e("Ejercicio01Activity", "Excepción al obtener datos: ${e.message}", e)
                Toast.makeText(this@Ejercicio01Activity, getString(R.string.error_fetching_teachers) + " (Excepción: ${e.localizedMessage})", Toast.LENGTH_LONG).show()
            } finally {
                binding.progressBar.visibility = View.GONE
                Log.d("Ejercicio01Activity", "fetchTeachersData finished.")
            }
        }
    }

    private fun checkAndRequestCallPermission(teacher: Teacher) {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_GRANTED -> {
                makeCall(teacher)
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE) -> {
                Toast.makeText(this, "Se necesita permiso para llamar al profesor.", Toast.LENGTH_LONG).show()
                // Luego, solicitar el permiso
                requestCallPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
            }
            else -> {
                // Solicitar el permiso directamente
                requestCallPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
            }
        }
    }

    private fun makeCall(teacher: Teacher) {
        if (teacher.phone.isNotBlank()) {
            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:${teacher.phone}"))
            try {
                startActivity(intent)
                Toast.makeText(this, getString(R.string.making_call_to, "${teacher.name} ${teacher.lastname}"), Toast.LENGTH_SHORT).show()
            } catch (e: SecurityException) {
                Log.e("Ejercicio01Activity", "Error de seguridad al intentar llamar: ${e.message}")
                Toast.makeText(this, "Error de seguridad. No se pudo realizar la llamada.", Toast.LENGTH_SHORT).show()
            } catch (e: ActivityNotFoundException) {
                Log.e("Ejercicio01Activity", "No se encontró app para llamadas: ${e.message}")
                Toast.makeText(this, getString(R.string.no_app_to_handle_call), Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Número de teléfono no disponible.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendEmail(teacher: Teacher) {
        if (teacher.email.isNotBlank()) {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:") // Solo apps de email deberían manejar esto
                putExtra(Intent.EXTRA_EMAIL, arrayOf(teacher.email))
                putExtra(Intent.EXTRA_SUBJECT, "Contacto desde App Laboratorio03")
                putExtra(Intent.EXTRA_TEXT, "Estimado/a ${teacher.name} ${teacher.lastname},\n\n")
            }
            try {
                startActivity(Intent.createChooser(intent, "Enviar correo usando..."))
                Toast.makeText(this, getString(R.string.sending_email_to, teacher.email), Toast.LENGTH_SHORT).show()
            } catch (e: ActivityNotFoundException) {
                Log.e("Ejercicio01Activity", "No se encontró app para correos: ${e.message}")
                Toast.makeText(this, getString(R.string.no_app_to_handle_email), Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Email no disponible.", Toast.LENGTH_SHORT).show()
        }
    }
}