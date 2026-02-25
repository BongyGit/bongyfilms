package bongydev.com.bongyfilms

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import bongydev.com.bongyfilms.database.DatabaseHelper
import bongydev.com.bongyfilms.models.Film
import bongydev.com.bongyfilms.network.OmdbApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddMovieActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var apiClient: OmdbApiClient
    private lateinit var searchResultsListView: ListView
    private var searchResults: MutableList<Film> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_movie)

        databaseHelper = DatabaseHelper(this)
        apiClient = OmdbApiClient("8d7b2328")

        val searchTitleInput = findViewById<EditText>(R.id.search_title_input)
        val searchYearInput = findViewById<EditText>(R.id.search_year_input)
        val searchButton = findViewById<Button>(R.id.search_movie_button)
        val backButton = findViewById<Button>(R.id.back_button)
        searchResultsListView = findViewById(R.id.search_results_list_view)

        searchButton.setOnClickListener {
            val title = searchTitleInput.text.toString().trim()
            val year = searchYearInput.text.toString().trim()

            if (title.isNotEmpty()) {
                performSearch(title, year)
            } else {
                Toast.makeText(this, "Please enter a movie title", Toast.LENGTH_SHORT).show()
            }
        }

        backButton.setOnClickListener {
            finish()
        }

        searchResultsListView.setOnItemClickListener { _, _, position, _ ->
            val selectedMovie = searchResults[position]
            showAddConfirmDialog(selectedMovie)
        }
    }

    private fun performSearch(title: String, year: String) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val results = apiClient.searchMovies(title, if (year.isNotEmpty()) year else null)
                if (results.isNotEmpty()) {
                    searchResults = results.toMutableList()
                    val adapter = FilmAdapter(this@AddMovieActivity, searchResults, databaseHelper)
                    searchResultsListView.adapter = adapter
                } else {
                    Toast.makeText(this@AddMovieActivity, "No movie found, try again", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@AddMovieActivity, "Error searching movies: ${'$'}{e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showAddConfirmDialog(movie: Film) {
        AlertDialog.Builder(this)
            .setTitle("Add to My Films?")
            .setMessage("${'$'}{movie.title} (${movie.year})")
            .setPositiveButton("Save") { _, _ ->
                addMovieToDatabase(movie)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    private fun addMovieToDatabase(movie: Film) {
        val newFilm = Film(
            filmNum = 0,
            title = movie.title,
            year = movie.year,
            watched = "N",
            imdbRating = movie.imdbRating,
            myRating = 0,
            imdbID = movie.imdbID,
            posterUrl = movie.posterUrl
        )

        databaseHelper.addFilm(newFilm)
        Toast.makeText(this, "The movie saved to your library", Toast.LENGTH_SHORT).show()
        finish()
    }
}