package fr.epita.mymeteo

data class ForecastDay (
    val day: DayInfo,
    val astro: AstroInfo
)

data class DayInfo(
    val maxtemp_c: Double,
    val mintemp_c: Double,
    val avgtemp_c: Double,
    val maxwind_kph: Double,
    val totalprecip_mm: Double,
    val avghumidity: Int,
    val daily_chance_of_rain: Int,
    val condition: Condition
)

data class AstroInfo(
    val sunrise: String,
    val sunset: String
)