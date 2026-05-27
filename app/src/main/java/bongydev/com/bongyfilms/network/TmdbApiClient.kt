package bongydev.com.bongyfilms.network

import bongydev.com.bongyfilms.models.Film
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.net.URLEncoder

class TmdbApiClient(private val apiKey: String) {

    private val baseUrl = "https://api.themoviedb.org/3"
    private val imageBaseUrl = "https://image.tmdb.org/t/p/w500"

    suspend fun searchMovies(title: String, year: String? = null): List<Film> = withContext(Dispatchers.IO) {
        try {
            val encodedTitle = URLEncoder.encode(title, "UTF-8")
            var url = "$baseUrl/search/movie?api_key=$apiKey&query=$encodedTitle"
            
            if (year != null && year.isNotEmpty()) {
                url += "&year=$year"
            }

            val response = URL(url).readText()
            val jsonResponse = JSONObject(response)

            val films = mutableListOf<Film>()
            
            if (jsonResponse.has("results")) {
                val searchResults = jsonResponse.getJSONArray("results")
                
                for (i in 0 until searchResults.length()) {
                    val movieJson = searchResults.getJSONObject(i)
                    
                    val tmdbId = movieJson.getInt("id")
                    val posterPath = movieJson.optString("poster_path", "")
                    val releaseDate = movieJson.optString("release_date", "")
                    val year = if (releaseDate.isNotEmpty()) releaseDate.substring(0, 4) else "N/A"
                    
                    // Fetch full details including external IDs (IMDB ID)
                    val details = getMovieDetails(tmdbId)
                    val imdbId = details["imdbID"] ?: ""
                    
                    films.add(Film(
                        filmNum = 0,
                        title = movieJson.optString("title", "N/A"),
                        year = year,
                        watched = "N",
                        imdbRating = movieJson.optDouble("vote_average", 0.0),
                        myRating = 0,
                        imdbID = imdbId,
                        posterUrl = if (posterPath.isNotEmpty()) "$imageBaseUrl$posterPath" else "",
                        plot = movieJson.optString("overview", "N/A"),
                        genre = details["genres"] ?: "N/A"
                    ))
                }
            }
            
            films
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getMovieDetails(tmdbId: Int): Map<String, String> = withContext(Dispatchers.IO) {
        try {
            val url = "$baseUrl/movie/$tmdbId?api_key=$apiKey&append_to_response=external_ids"
            val response = URL(url).readText()
            val jsonResponse = JSONObject(response)

            val imdbId = jsonResponse.optJSONObject("external_ids")?.optString("imdb_id", "") ?: ""
            val releaseDate = jsonResponse.optString("release_date", "")
            val year = if (releaseDate.isNotEmpty()) releaseDate.substring(0, 4) else "N/A"
            
            // Extract genres
            val genresList = mutableListOf<String>()
            if (jsonResponse.has("genres")) {
                val genres = jsonResponse.getJSONArray("genres")
                for (i in 0 until genres.length()) {
                    genresList.add(genres.getJSONObject(i).getString("name"))
                }
            }
            val genresString = genresList.joinToString(", ")

            val posterPath = jsonResponse.optString("poster_path", "")
            val posterUrl = if (posterPath.isNotEmpty()) "$imageBaseUrl$posterPath" else ""

            mapOf(
                "Title" to jsonResponse.optString("title", "N/A"),
                "Year" to year,
                "Genre" to genresString.ifEmpty { "N/A" },
                "Plot" to jsonResponse.optString("overview", "N/A"),
                "imdbRating" to jsonResponse.optDouble("vote_average", 0.0).toString(),
                "Poster" to posterUrl,
                "imdbID" to imdbId,
                "vote_average" to jsonResponse.optDouble("vote_average", 0.0).toString()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            emptyMap()
        }
    }

    suspend fun getMovieDetailsByImdbId(imdbId: String): Map<String, String> = withContext(Dispatchers.IO) {
        try {
            val url = "$baseUrl/find/$imdbId?api_key=$apiKey&external_source=imdb_id"
            val response = URL(url).readText()
            val jsonResponse = JSONObject(response)

            if (jsonResponse.has("movie_results")) {
                val results = jsonResponse.getJSONArray("movie_results")
                if (results.length() > 0) {
                    val movieJson = results.getJSONObject(0)
                    return@withContext getMovieDetails(movieJson.getInt("id"))
                }
            }
            emptyMap()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyMap()
        }
    }
}
