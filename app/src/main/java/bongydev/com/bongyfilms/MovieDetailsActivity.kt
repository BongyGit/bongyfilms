package bongydev.com.bongyfilms

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Button
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import bongydev.com.bongyfilms.database.DatabaseHelper
import bongydev.com.bongyfilms.models.Film
import bongydev.com.bongyfilms.network.TmdbApiClient
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MovieDetailsActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var apiClient: TmdbApiClient
    private var filmId: Int = 0
    private var imdbId: String = ""
    private var originalMyRating: Int = 0
    private var originalWatched: String = ""
    private var isModified = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_details)

        databaseHelper = DatabaseHelper(this)
        apiClient = TmdbApiClient("e04f7d260ec670160a9d2aaa7f9a3bef")

        filmId = intent.getIntExtra("film_id", 0)
        imdbId = intent.getStringExtra("imdb_id") ?: ""

        val backButton = findViewById<Button>(R.id.back_button)
        backButton.setOnClickListener {
            if (isModified) {
                showSaveDialog()
            } else {
                finish()
            }
        }

        loadMovieDetails()
    }

    private fun loadMovieDetails() {
        val film = databaseHelper.getFilmById(filmId)
        if (film != null) {
            originalMyRating = film.myRating
            originalWatched = film.watched

            // Fetch additional details from TMDB API using imdbID as external ID
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val movieDetails = apiClient.getMovieDetailsByImdbId(imdbId)
                    displayMovieDetails(film, movieDetails)
                } catch (e: Exception) {
                    Toast.makeText(this@MovieDetailsActivity, "Error loading movie details", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun displayMovieDetails(film: Film, movieDetails: Map<String, String>) {
        val posterImageView = findViewById<ImageView>(R.id.movie_poster)
        val titleTextView = findViewById<TextView>(R.id.movie_title)
        val yearTextView = findViewById<TextView>(R.id.movie_year)
        val genreTextView = findViewById<TextView>(R.id.movie_genre)
        val plotTextView = findViewById<TextView>(R.id.movie_plot)
        val imdbRatingTextView = findViewById<TextView>(R.id.movie_imdb_rating)
        val myRatingSpinner = findViewById<Spinner>(R.id.movie_my_rating_spinner)
        val watchedSpinner = findViewById<Spinner>(R.id.movie_watched_spinner)

        titleTextView.text = movieDetails["Title"] ?: film.title
        yearTextView.text = "Year: ${movieDetails["Year"] ?: film.year}"
        genreTextView.text = "Genre: ${movieDetails["Genre"] ?: "N/A"}"
        plotTextView.text = "Plot: ${movieDetails["Plot"] ?: "N/A"}"
        imdbRatingTextView.text = "IMDb Rating: ${movieDetails["imdbRating"] ?: film.imdbRating}"

        // Load poster
        val posterUrl = movieDetails["Poster"]
        if (!posterUrl.isNullOrEmpty() && posterUrl != "N/A") {
            Glide.with(this)
                .load(posterUrl)
                .placeholder(R.drawable.ic_placeholder)
                .into(posterImageView)
        } else {
            posterImageView.setImageResource(R.drawable.ic_placeholder)
        }

        // Setup My Rating Spinner
        val ratingAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, (0..10).map { it.toString() })
        ratingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        myRatingSpinner.adapter = ratingAdapter
        myRatingSpinner.setSelection(film.myRating)
        myRatingSpinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                isModified = true
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }

        // Setup Watched Spinner
        val watchedAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, arrayOf("Y", "N"))
        watchedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        watchedSpinner.adapter = watchedAdapter
        watchedSpinner.setSelection(if (film.watched == "Y") 0 else 1)
        watchedSpinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                isModified = true
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }
    }

    private fun showSaveDialog() {
        AlertDialog.Builder(this)
            .setTitle("Made any Changes?")
            .setMessage("If you've made changes. Do you want to save?")
            .setPositiveButton("Save") { _, _ ->
                saveChanges()
                finish()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
                finish()
            }
            .show()
    }

    private fun saveChanges() {
        val myRatingSpinner = findViewById<Spinner>(R.id.movie_my_rating_spinner)
        val watchedSpinner = findViewById<Spinner>(R.id.movie_watched_spinner)

        val newMyRating = myRatingSpinner.selectedItem.toString().toInt()
        val newWatched = watchedSpinner.selectedItem.toString()

        databaseHelper.updateFilmRatings(filmId, newMyRating, newWatched)
        Toast.makeText(this, "Changes saved", Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        if (isModified) {
            showSaveDialog()
        } else {
            super.onBackPressed()
        }
    }
}
