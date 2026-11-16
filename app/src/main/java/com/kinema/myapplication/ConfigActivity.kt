
package com.kinema.myapplication

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator
import com.fasterxml.jackson.databind.SerializationFeature
import java.net.URI
import java.nio.charset.StandardCharsets

class ConfigActivity : AppCompatActivity() {

    // Crea el lector y escritor de archivos XML.
    private val xmlMapper = XmlMapper().apply {
        registerKotlinModule()
        configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true)
        enable(SerializationFeature.INDENT_OUTPUT)
    }

    // Guarda la configuración actual.
    private lateinit var configActual: UserConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config)

        // Recupera los elementos de la pantalla.
        val nombre = findViewById<EditText>(R.id.etNombreComprador)
        val correo = findViewById<TextView>(R.id.tvCorreoComprador)
        val servidor = findViewById<EditText>(R.id.etUrlServidor)

        val btnGuardar = findViewById<Button>(R.id.btnGuardarConfig)
        val btnVolver = findViewById<Button>(R.id.btnVolverConfig)

        // Lee la configuración guardada.
        try {
            openFileInput("CONFIG.XML").use { archivo ->
                configActual = xmlMapper.readValue(
                    archivo,
                    UserConfig::class.java
                )
            }

            // Muestra los datos actuales en pantalla.
            nombre.setText(configActual.nombre)
            correo.text = configActual.correo
            servidor.setText(configActual.servidor)

        } catch (e: Exception) {
            Toast.makeText(
                this,
                "No s'ha pogut carregar la configuració",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        // Guarda los cambios realizados.
        btnGuardar.setOnClickListener {

            val nuevoNombre = nombre.text.toString().trim()
            val nuevaUrl = servidor.text.toString().trim()

            // Comprueba que el nombre no esté vacío.
            if (nuevoNombre.isEmpty()) {
                nombre.error = "Introdueix un nom"
                return@setOnClickListener
            }

            // Comprueba que la URL tenga un formato válido.
            try {
                val uri = URI(nuevaUrl)

                if ((uri.scheme != "ws" && uri.scheme != "wss") ||
                    uri.host == null
                ) {
                    servidor.error = "Introdueix una URL vàlida"
                    return@setOnClickListener
                }

            } catch (e: Exception) {
                servidor.error = "Introdueix una URL vàlida"
                return@setOnClickListener
            }

            // Crea la nueva configuración manteniendo el correo original.
            val nuevaConfig = UserConfig(
                nuevoNombre,
                configActual.correo,
                nuevaUrl
            )

            try {
                // Conviertee la nueva configuración a XML.
                val xmlString = xmlMapper.writeValueAsString(nuevaConfig)

                // Guarda los cambios en CONFIG.XML.
                openFileOutput("CONFIG.XML", Context.MODE_PRIVATE).use { archivo ->
                    archivo.write(
                        xmlString.toByteArray(StandardCharsets.UTF_8)
                    )
                }

                // Actualiza los datos del comprador en la aplicación.
                WebSocketManager.nombre = nuevoNombre

                // Reconectar si se cambia la url del servidor
                if (nuevaUrl != configActual.servidor) {
                    WebSocketManager.reconectar?.invoke(nuevaUrl, nuevoNombre)
                }

                Toast.makeText(
                    this,
                    "Configuració desada correctament",
                    Toast.LENGTH_SHORT
                ).show()

                finish()

            } catch (e: Exception) {
                Toast.makeText(
                    this,
                    "Error en desar la configuració",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Volver a la pantalla anterior.
        btnVolver.setOnClickListener {
            finish()
        }
    }
}
