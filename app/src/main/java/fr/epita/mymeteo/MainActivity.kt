package fr.epita.mymeteo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        getCityName()
        getCredits()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    /* Redirection to author linkedin when clicking on the author name */
    private fun getCredits() {
        val credits = findViewById<TextView>(R.id.credits)
        val text = getString(R.string.credits)
        val url = getString(R.string.profile_url)
        val spannableString = SpannableString(text)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // Handle the click event
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(browserIntent)
            }
        }

        val startIndex = text.indexOf("Rawane")
        val endIndex = text.indexOf("Ouffa") + "Ouffa".length
        spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        credits.text = spannableString
        credits.movementMethod = LinkMovementMethod.getInstance()
    }

    /* Get the city name from the user input and make a request to the WebService */
    private fun getCityName() {
        val searchBtn : Button = findViewById(R.id.search_button)
        // when the button search is clicked
        searchBtn.setOnClickListener {
            val city : EditText = findViewById(R.id.editTextCity)
            // if the input of the user is not empty
            if (city.getText().toString().isNotBlank()) {
                // send the request with the city written in the input
                sendRequest(city.getText().toString())
            }
        }
    }

    /* Send a request to the WebService and get the desired object */
    private fun sendRequest(city: String) {
        // The base URL where the WebService is located
        val baseUrl = "https://api.weatherapi.com/v1/"

        // Json parser
        val jsonConverter = GsonConverterFactory.create(GsonBuilder().create())

        // Retrofit client for the requests
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(jsonConverter)
            .build()

        // create a service with the client to implement the Meteo Info Interface
        val service: MeteoInfoInterface = retrofit.create(MeteoInfoInterface::class.java)

        val callback : Callback<ForecastInfo> = object : Callback<ForecastInfo> {
            override fun onFailure(call: Call<ForecastInfo>, t: Throwable) {
                Log.w("TAG", "WebService call failed: " + t.message)
            }

            override fun onResponse(call: Call<ForecastInfo>, response: Response<ForecastInfo>) {
                if (!response.isSuccessful) {
                    // handle error when code is not 2**
                    val errorMsg = response.errorBody()?.let { errorBody ->
                        try {
                            val gson = Gson()
                            val errorResponse = gson.fromJson(errorBody.charStream(), ErrorResponse::class.java)
                            errorResponse.error.message
                        } catch (e: IOException) {
                            e.printStackTrace()
                            "An unknown error occurred"
                        }
                    } ?: "An unknown error occurred"
                    // display the error message in a text view
                    val errorTextBox : TextView = findViewById(R.id.errorMsg)
                    errorTextBox.text = errorMsg
                    Log.e("ERROR", errorMsg)
                } else {
                    val forecastInfo : ForecastInfo? = response.body()
                    if (forecastInfo != null) {
                        Log.i("INFO", forecastInfo.toString())
                        // serialize forecastInfo
                        val forecastJson = GsonBuilder().create().toJson(forecastInfo)
                        // and send it to the second page through an intent
                        val intent = Intent(this@MainActivity, ForecastDetailsActivity::class.java)
                        intent.putExtra("forecastInfo", forecastJson)
                        startActivity(intent)
                    }
                }
            }
        }

        // asynchronously call the getForecastInfo method to get the desired object
        service.getForecastInfo(city, "1bb75d27510b4ab181b100932240807").enqueue(callback)
    }
}