package com.kinema.myapplication

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import org.json.JSONObject

class CistellaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cistella)

        // Obtenemos los elementos de la pantalla
        val recycler = findViewById<RecyclerView>(R.id.recyclerCistella)
        val tvTotal = findViewById<TextView>(R.id.tvTotalCistella)
        val btnValidar = findViewById<Button>(R.id.btnValidarCompra)

        // Configuramos la lista de entradas
        recycler.layoutManager = LinearLayoutManager(this)
        // Configuramos la lista de entradas y la acción de eliminar
        recycler.adapter = CistellaAdapter(Cistella.entrades) {

            // Actualizamos el precio total después de eliminar una entrada
            tvTotal.text = "Total: ${Cistella.calcularTotal()}€"

            // Activamos o desactivamos el botón según si quedan entradas
            btnValidar.isEnabled = Cistella.entrades.isNotEmpty()
        }

        // Mostramos el precio total de la cesta
        tvTotal.text = "Total: ${Cistella.calcularTotal()}€"

        // Configuramos el botón para validar la compra
        btnValidar.setOnClickListener {

            // Comprobamos que existe conexión con el servidor
            val client = WebSocketManager.client

            if (client == null || !client.isOpen) {
                Toast.makeText(this, "No hay conexión con el servidor", Toast.LENGTH_LONG).show()

                // Comprobamos que la cesta no esté vacía
            } else if (Cistella.entrades.isEmpty()) {
                Toast.makeText(this, "La cesta está vacía", Toast.LENGTH_LONG).show()

            } else {

                // Creamos una lista JSON para guardar las entradas de la cesta
                val entradasJSON = JSONArray()

                // Recorremos las entradas que hay en la cesta
                for (entrada in Cistella.entrades) {

                    // Creamos un objeto JSON con los datos de cada entrada
                    val entradaJSON = JSONObject()

                    entradaJSON.put("esdevenimentId", entrada.esdevenimentId)
                    entradaJSON.put("tipusEntradaId", entrada.entrada.id)
                    entradaJSON.put("quantitat", entrada.quantitat)

                    // Añadimos la entrada a la lista JSON
                    entradasJSON.put(entradaJSON)
                }

                // Creamos el mensaje JSON que enviaremos al servidor
                val compraJSON = JSONObject()

                compraJSON.put("type", "purchase")
                compraJSON.put("nombre", WebSocketManager.nombre)
                compraJSON.put("correo", WebSocketManager.correo)
                compraJSON.put("entradas", entradasJSON)

                // Desactivamos el botón para evitar enviar varias compras
                btnValidar.isEnabled = false

                // Esperamos la respuesta del servidor
                WebSocketManager.respuestaCompra = { message ->

                    // Actualizamos la pantalla con la respuesta recibida
                    runOnUiThread {

                        // Convertimos el mensaje recibido en un objeto JSON
                        val respuesta = JSONObject(message)

                        // Comprobamos si la compra se ha realizado correctamente
                        if (respuesta.optString("status") == "OK") {

                            // Obtenemos la lista de localizadores
                            val localizadores = respuesta.optJSONArray("localitzadors")
                            var mensaje = "COMPRA CORRECTA\n\n"

                            // Recorremos los localizadores recibidos
                            if (localizadores != null) {
                                for (i in 0 until localizadores.length()) {

                                    val reserva = localizadores.getJSONObject(i)

                                    // Añadimos el localizador y la cantidad al mensaje
                                    mensaje += "Localizador: ${reserva.optString("localitzador")}\n"
                                    mensaje += "Cantidad: ${reserva.optInt("quantitat")}\n\n"
                                }
                            }

                            // Mostramos un diálogo con la información de la compra
                            AlertDialog.Builder(this)
                                .setTitle("Compra realizada correctamente")
                                .setMessage(mensaje)
                                .setPositiveButton("Aceptar", null)
                                .show()

                            // Vaciamos la cesta después de realizar la compra
                            Cistella.entrades.clear()

                            // Actualizamos el total y la lista de entradas
                            tvTotal.text = "Total: 0.0€"
                            recycler.adapter?.notifyDataSetChanged()

                            // Mantenemos desactivado el botón porque la cesta está vacía
                            btnValidar.isEnabled = false

                        } else {

                            // Mostramos un diálogo con el error de la compra
                            AlertDialog.Builder(this)
                                .setTitle("Error en la compra")
                                .setMessage(respuesta.optString("message"))
                                .setPositiveButton("Aceptar", null)
                                .show()

                            // Permitimos volver a intentar la compra
                            btnValidar.isEnabled = true
                        }

                        // Dejamos de esperar la respuesta de esta compra
                        WebSocketManager.respuestaCompra = null
                    }
                }

                // Enviamos los datos de la compra al servidor
                client.send(compraJSON.toString())
            }
        }
    }

    override fun onDestroy() {

        // Eliminamos la respuesta pendiente al cerrar la pantalla
        WebSocketManager.respuestaCompra = null

        super.onDestroy()
    }
}