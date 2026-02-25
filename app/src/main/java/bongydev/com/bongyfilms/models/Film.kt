package bongydev.com.bongyfilms.models

data class Film(
    val filmNum: Int,
    val title: String,
    val year: String,
    val watched: String,
    val imdbRating: Double,
    val myRating: Int,
    val imdbID: String,
    val posterUrl: String = ""
)