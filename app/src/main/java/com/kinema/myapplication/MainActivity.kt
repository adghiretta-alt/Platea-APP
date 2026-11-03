package com.kinema.myapplication

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.google.android.material.snackbar.Snackbar
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import java.net.URI
import java.nio.charset.StandardCharsets

class MainActivity : AppCompatActivity() {
    lateinit var wsClient: KinemaWSClient
    lateinit var servResponse: JSONObject

    private val xmlMapper: XmlMapper by lazy {
        XmlMapper().apply {
            registerKotlinModule()
            configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true)
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val configXml = "CONFIG.XML"

        val connectButton = findViewById<Button>(R.id.connectButton)

        val nameUser = findViewById<EditText>(R.id.userName)
        val emailUser = findViewById<EditText>(R.id.userEmail)
        val connectionUser = findViewById<EditText>(R.id.userConnetion)


        connectButton.setOnClickListener {
            val name = nameUser.text.toString().trim()
            val eMail = emailUser.text.toString().trim()
            val urlConnect = connectionUser.text.toString().trim()

            val nameFlag = checkNameUser(name)
            val emailFlag = checkEmailUser(eMail)

            if (nameFlag && emailFlag) {
                val configData = UserConfig(name, eMail)

                val xmlString = xmlMapper.writeValueAsString(configData)

                try {
                    openFileOutput(configXml, Context.MODE_PRIVATE).use { ofs ->
                        ofs.write(xmlString.toByteArray(StandardCharsets.UTF_8))
                    }
                    connectWS(urlConnect, name)
                } catch (e: Exception) {
                    Toast.makeText(this, "Someting went wrong: CONFIG.XML", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun checkNameUser(userName: String): Boolean {
        return userName.isNotEmpty()
    }

    fun checkEmailUser(userEmail: String): Boolean {
        // Funcion para verificar un correo válido, se ha buscado por internet https://stackoverflow.com/questions/29492168/android-util-patterns-email-address-is-validating-invalid-emails
        return userEmail.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()
    }

    fun connectWS(urlConect: String, name: String) {
        try {
            val uri = URI(urlConect)
            wsClient = KinemaWSClient(uri, name)
            wsClient.connect()
        } catch (e: Exception) {
            showToast("URL Wrong!", false)
        }

    }

    fun showToast(msg: String, success: Boolean) {
        val root = findViewById<View>(R.id.main)  // ya lo tienes en onCreate
        Snackbar.make(root, msg, Snackbar.LENGTH_LONG)
            .setBackgroundTint(if (success) Color.parseColor("#4CAF50") else Color.parseColor("#F44336"))
            .setTextColor(Color.WHITE)
            .show()
    }

    inner class KinemaWSClient(serverUri: URI, private val name: String): WebSocketClient(serverUri) {
        private val K_TYPE = "type";

        override fun onOpen(handshakedata: ServerHandshake?) {
            showToast("Connection successe", true)
            send("{ 'type' : 'esdev' }")
        }

        override fun onMessage(message: String?) {
            println("📥 Rebut: $message")

            // crides segures a la GUI (main thread)
            runOnUiThread {
                //val tv = findViewById<TextView>(R.id.textView)
                //tv.text = "Nou missatge: $message"

                servResponse = JSONObject(message)
                when (servResponse.optString(K_TYPE)) {
                    "esdev" -> {
                        val esdeveniments = servResponse.optJSONArray("esdeveniment")

                    }
                    else -> {}
                }
                println(servResponse)
            }
        }

        override fun onClose(code: Int, reason: String?, remote: Boolean) {
            println("🔌 Connexió tancada")
            println("📋 Codi: $code, Raó: $reason, Remote: $remote")
        }

        override fun onError(ex: Exception?) {
            showToast("Conection not successe: ${ex?.message}", false)
            ex?.printStackTrace()
        }

    }
}