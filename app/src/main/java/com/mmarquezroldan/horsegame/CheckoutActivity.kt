package com.mmarquezroldan.horsegame

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class CheckoutActivity : AppCompatActivity() {

    companion object {
        private const val BACKEND_URL = "http://10.0.2.2:3000"
    }

    private lateinit var paymentSheet: PaymentSheet
    private var paymentIntentClientSecret: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        val payButton = findViewById<Button>(R.id.pay_button)
        payButton.isEnabled = false

        paymentSheet = PaymentSheet(this) { paymentResult ->
            when (paymentResult) {
                is PaymentSheetResult.Completed -> showToast("¡Pago completado!")
                is PaymentSheetResult.Canceled -> showToast("Pago cancelado")
                is PaymentSheetResult.Failed -> (paymentResult.error.localizedMessage ?: paymentResult.error.message)?.let {
                    showError(
                        it
                    )
                }
            }
        }

        fetchPaymentIntent { result ->
            runOnUiThread {
                result.onSuccess { clientSecret ->
                    paymentIntentClientSecret = clientSecret
                    payButton.isEnabled = true
                }.onFailure { error ->
                    showError(error.localizedMessage ?: error.message ?: "Error desconocido")
                }
            }
        }

        payButton.setOnClickListener {
            paymentIntentClientSecret?.let { secret ->
                presentPaymentSheet(secret)
            }
        }
    }

    private fun fetchPaymentIntent(callback: (Result<String>) -> Unit) {
        val url = "$BACKEND_URL/create-payment-intent"

        val shoppingCartContent = """
            {
                "items": [
                    {"id":"xl-tshirt"}
                ]
            }
        """

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = shoppingCartContent.toRequestBody(mediaType)

        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(Result.failure(e))
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    callback(Result.failure(Exception(response.message)))
                } else {
                    val clientSecret = extractClientSecretFromResponse(response)
                    clientSecret?.let {
                        callback(Result.success(it))
                    } ?: callback(Result.failure(Exception("No se encontró el client secret")))
                }
            }
        })
    }

    private fun extractClientSecretFromResponse(response: Response): String? {
        return try {
            val responseData = response.body?.string()
            val responseJson = responseData?.let { JSONObject(it) } ?: JSONObject()
            responseJson.getString("clientSecret")
        } catch (e: JSONException) {
            null
        }
    }

    private fun presentPaymentSheet(clientSecret: String) {
        val configuration = PaymentSheet.Configuration.Builder("Mi Comercio")
            .build()
        paymentSheet.presentWithPaymentIntent(clientSecret, configuration)
    }

    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }
    }

    private fun showError(message: String) {
        runOnUiThread {
            AlertDialog.Builder(this)
                .setTitle("Error en el pago")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show()
        }
    }
}
