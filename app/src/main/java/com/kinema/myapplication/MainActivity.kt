package com.kinema.myapplication

import android.content.Context
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.io.File
import java.lang.Exception
import java.net.URI

class MainActivity : AppCompatActivity() {
    lateinit var wsClient: KinemaWSClient

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
        val file = File(this.filesDir, configXml)

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
                val configXmlContent = name + "\n" + eMail
                try {
                    this.openFileOutput(configXml, Context.MODE_PRIVATE).use { ofs ->
                        ofs.write(configXmlContent.toByteArray())
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Someting went wrong: CONFIG.XML", Toast.LENGTH_SHORT).show()
                }
                connectWS(urlConnect, name)
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
            Toast.makeText(this, "URL wrong!", Toast.LENGTH_SHORT).show()
        }

    }

    fun sendCheckConnection(userName: String) {
        if (wsClient.isOpen) {
            wsClient.send(userName + ": start a connection")
        } else {
            Toast.makeText(this, "Something went wrong: CONNECTION", Toast.LENGTH_SHORT).show()
        }
    }

    inner class KinemaWSClient(serverUri: URI, private val name: String): WebSocketClient(serverUri) {
        override fun onOpen(handshakedata: ServerHandshake?) {
            println("✅ Connectat al servidor")
            println("🔗 Codi d'estat: ${handshakedata?.httpStatus}")
            println("📝 Descripció: ${handshakedata?.httpStatusMessage}")

            send("$name: start a connection")
        }

        override fun onMessage(message: String?) {
            println("📥 Rebut: $message")

            // crides segures a la GUI (main thread)
            runOnUiThread {
                val tv = findViewById<TextView>(R.id.textView)
                tv.text = "Nou missatge: $message"
            }
        }

        override fun onClose(code: Int, reason: String?, remote: Boolean) {
            println("🔌 Connexió tancada")
            println("📋 Codi: $code, Raó: $reason, Remote: $remote")
        }

        override fun onError(ex: Exception?) {
            println("❌ Error: ${ex?.message}")
            ex?.printStackTrace()
        }

    }
}