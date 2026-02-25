package bongydev.com.bongyfilms.network

import bongydev.com.bongyfilms.models.Film
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.net.URLEncoder

class OmdbApiClient(private val apiKey: String) {

    private val baseUrl = "https://www.omdbapi.com/"

    suspend fun searchMovies(title: String, year: String? = null): List<Film> = withContext(Dispatchers.IO) {
        try {
            val encodedTitle = URLEncoder.encode(title, "UTF-8")
            var url = "${baseUrl}?s=[0m$encodedTitle&type=movie&apikey=$apiKey"
            
            if (year != null) {
                url += "&y=$year"
            }

            val response = URL(url).readText()
            val jsonResponse = JSONObject(response)

            val films = mutableListOf<Film>()
            
            if (jsonResponse.has("Search")) {
                val searchResults = jsonResponse.getJSONArray("Search")
                
                for (i in 0 until searchResults.length()) {
                    val movieJson = searchResults.getJSONObject(i)
                    val imdbId = movieJson.getString("imdbID")
                    val posterUrl = movieJson.getString("Poster")
                    
                    // Fetch full details for each movie
                    val details = getMovieDetails(imdbId)
                    
                    films.add(Film(
                        filmNum = 0,
                        title = movieJson.getString("Title"),
                        year = movieJson.getString("Year"),
                        watched = "N",
                        imdbRating = details["imdbRating"]?.toDoubleOrNull() ?: 0.0,
                        myRating = 0,
                        imdbID = imdbId,
                        posterUrl = if (posterUrl != "N/A") posterUrl else ""
                    ))
                }
            }
            
            films
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getMovieDetails(imdbId: String): Map<String, String> = withContext(Dispatchers.IO) {
        try {
            val url = "${baseUrl}?i=$imdbId&apikey=$apiKey"
            val response = URL(url).readText()
            val jsonResponse = JSONObject(response)

            mapOf(
                "Title" to jsonResponse.optString("Title", "N/A"),
                "Year" to jsonResponse.optString("Year", "N/A"),
                "Genre" to jsonResponse.optString("Genre", "N/A"),
                "Plot" to jsonResponse.optString("Plot", "N/A"),
                "imdbRating" to jsonResponse.optString("imdbRating", "N/A"),
                "Poster" to jsonResponse.optString("Poster", "N/A")
            )
        } catch (e: Exception) {
            e.printStackTrace()
            emptyMap()
        }
    }
}