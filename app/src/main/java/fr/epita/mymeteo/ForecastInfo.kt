package fr.epita.mymeteo

data class ForecastInfo (
    val location : Location,
    val current : Current,
    val forecast : Forecast
)
