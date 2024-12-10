package fr.epita.mymeteo

import android.content.Intent
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.NestedScrollView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson


class ForecastDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_forecast_details)

        // get the object sent from main activity
        val forecastJson = intent.getStringExtra("forecastInfo")

        if (forecastJson != null) {
            val forecastInfo: ForecastInfo = Gson().fromJson(forecastJson, ForecastInfo::class.java)
            // Display the forecast details in the UI
            displayForecastDetails(forecastInfo)
        }

        configureScrollView()
        getApi()

        // Handle going back to the home page
        findViewById<ImageView>(R.id.back).setOnClickListener {
            val intent = Intent(this@ForecastDetailsActivity, MainActivity::class.java)
            startActivity(intent)
        }
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_info)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    /* Configure the scrolling behavior of the bottom sheet */
    private fun configureScrollView() {
        val bottomSheet = findViewById<ConstraintLayout>(R.id.bottomSheet)
        val behavior = BottomSheetBehavior.from(bottomSheet)

        behavior.peekHeight = resources.displayMetrics.heightPixels / 2
        behavior.isHideable = true


        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            val scrollView = findViewById<NestedScrollView>(R.id.scrollView)
            val baseView = findViewById<ConstraintLayout>(R.id.base)

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HALF_EXPANDED) {
                    baseView.alpha = 1f
                    baseView.visibility = View.VISIBLE
                } else if ((newState == BottomSheetBehavior.STATE_COLLAPSED ||
                            newState == BottomSheetBehavior.STATE_HIDDEN)) {
                    behavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                    scrollView.post { scrollView.scrollTo(0, 0) }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // Handle sliding here
                val alpha = 1 - 0.3f - slideOffset
                baseView.alpha = alpha
            }
        })
    }

    /* Display the forecast weather details in the UI */
    private fun displayWeatherInfo(forecastInfo: ForecastInfo) {
        findViewById<TextView>(R.id.temp).text = "${forecastInfo.current.temp_c.toInt() }°"
        findViewById<TextView>(R.id.description).text = forecastInfo.current.condition.text
        val imgageDescription : ImageView = findViewById(R.id.imageDescription)
        val imageUrl = "https://${forecastInfo.current.condition.icon}"
        Glide.with(this)
            .load(imageUrl)
            .into(imgageDescription)
        findViewById<TextView>(R.id.feelslike_text_value).text = "${forecastInfo.current.feelslike_c.toInt()}°"
        findViewById<TextView>(R.id.heat_index_text_value).text = "${forecastInfo.current.heatindex_c.toInt()}°"
        findViewById<TextView>(R.id.uv_text_value).text = "${forecastInfo.current.uv.toInt()}"
        findViewById<TextView>(R.id.precipitation_text_value).text = "${forecastInfo.current.precip_mm.toInt()}mm"
        findViewById<TextView>(R.id.humidity_text_value).text = "${forecastInfo.current.humidity} %"
        findViewById<TextView>(R.id.cloud_text_value).text = "${forecastInfo.current.cloud} %"
        findViewById<TextView>(R.id.wind_speed_text_value).text = "${forecastInfo.current.wind_kph.toInt()} km/h"
        findViewById<TextView>(R.id.wind_direction_text_value).text = forecastInfo.current.wind_dir
        findViewById<TextView>(R.id.wind_chill_text_value).text = "${forecastInfo.current.windchill_c.toInt()}°"
    }

    /* Display the sunrise and sunset times in the UI and change baseView background accordingly */
    private fun displayAstroAndBackground(forecastInfo: ForecastInfo, localtime: String) {
        val sunrise = convertTo24HourFormat(forecastInfo.forecast.forecastday[0].astro.sunrise)
        val sunset = convertTo24HourFormat(forecastInfo.forecast.forecastday[0].astro.sunset)
        findViewById<TextView>(R.id.sunrise_text_value).text = sunrise
        findViewById<TextView>(R.id.sunset_text_value).text = sunset

        val layoutInfo = findViewById<CoordinatorLayout>(R.id.main_info)
        if (isWithinTwoHours(localtime, sunrise)) {
            layoutInfo.background=getDrawable(R.drawable.gradient_bg_dawn)
        } else if (isWithinTwoHours(localtime, sunset)) {
            layoutInfo.background=getDrawable(R.drawable.gradient_bg_dusk)
        } else if (forecastInfo.current.is_day == 0){
            layoutInfo.background=getDrawable(R.drawable.gradient_bg_night)
            // change text color for visibility
            changeTextColor()
        } else {
            layoutInfo.background=getDrawable(R.drawable.gradient_bg_day)
        }
    }

    private fun changeTextColor() {
        val textColor = ContextCompat.getColor(this, R.color.lightGray)

        findViewById<TextView>(R.id.max_wind_speed_text).setTextColor(textColor)
        findViewById<TextView>(R.id.chance_of_rain_text).setTextColor(textColor)
        findViewById<TextView>(R.id.avg_humidity_text).setTextColor(textColor)

        findViewById<TextView>(R.id.feelslike_temp_text).setTextColor(textColor)
        findViewById<TextView>(R.id.heatIndex_text).setTextColor(textColor)
        findViewById<TextView>(R.id.uv_text).setTextColor(textColor)
        findViewById<TextView>(R.id.precipitation_text).setTextColor(textColor)
        findViewById<TextView>(R.id.humidity_text).setTextColor(textColor)
        findViewById<TextView>(R.id.cloud_text).setTextColor(textColor)
        findViewById<TextView>(R.id.wind_speed_text).setTextColor(textColor)
        findViewById<TextView>(R.id.wind_direction_text).setTextColor(textColor)
        findViewById<TextView>(R.id.wind_chill_text).setTextColor(textColor)

        findViewById<TextView>(R.id.sunrise_text).setTextColor(textColor)
        findViewById<TextView>(R.id.sunset_text).setTextColor(textColor)
        findViewById<TextView>(R.id.apiSource).setTextColor(textColor)
    }

    /* Display the forecast details in the UI */
    private fun displayForecastDetails(forecastInfo: ForecastInfo) {
        // Location information
        findViewById<TextView>(R.id.city).text = forecastInfo.location.name
        findViewById<TextView>(R.id.region).text = forecastInfo.location.region
        findViewById<TextView>(R.id.country).text = forecastInfo.location.country
        val localtime = forecastInfo.location.localtime.split(" ").last()
        findViewById<TextView>(R.id.localtime).text = localtime

        // Weather information
        displayWeatherInfo(forecastInfo)

        // Forecast information
        findViewById<TextView>(R.id.min_temp).text = "${forecastInfo.forecast.forecastday[0].day.mintemp_c.toInt()}°"
        findViewById<TextView>(R.id.max_temp).text = "${forecastInfo.forecast.forecastday[0].day.maxtemp_c.toInt()}°"
        findViewById<TextView>(R.id.max_wind_speed_text_value).text = "${forecastInfo.forecast.forecastday[0].day.maxwind_kph.toInt()} km/h"
        findViewById<TextView>(R.id.chance_of_rain_text_value).text = "${forecastInfo.forecast.forecastday[0].day.daily_chance_of_rain} %"
        findViewById<TextView>(R.id.avg_humidity_text_value).text = "${forecastInfo.forecast.forecastday[0].day.avghumidity} %"

        // Sunset and sunrise information
        displayAstroAndBackground(forecastInfo, localtime)
    }

    /* Redirection to Weather api used when clicking on the api name */
    private fun getApi() {
        val credits = findViewById<TextView>(R.id.apiSource)
        val text = getString(R.string.apiName)
        val url = getString(R.string.api_url)
        val spannableString = SpannableString(text)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // Handle the click event
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(browserIntent)
            }
        }

        val startIndex = text.indexOf("Weatherapi")
        val endIndex = text.indexOf("Weatherapi") + "Weatherapi".length
        spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        credits.text = spannableString
        credits.movementMethod = LinkMovementMethod.getInstance()
    }
}