package fr.epita.mymeteo

data class Current (
    val last_updated : String,
    val temp_c : Float,
    val is_day : Int,
    val condition : Condition,
    val wind_kph : Float,
    val wind_dir : String,
    val precip_mm : Float,
    val humidity: Int,
    val cloud: Int,
    val feelslike_c : Float,
    val windchill_c : Float,
    val heatindex_c : Float,
    val uv : Float
)

data class Condition(
    val text: String,
    val icon: String
)