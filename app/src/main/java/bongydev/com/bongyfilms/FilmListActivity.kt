package bongydev.com.bongyfilms

import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import bongydev.com.bongyfilms.database.DatabaseHelper
import bongydev.com.bongyfilms.models.Film

class FilmListActivity : AppCompatActivity() {

    private lateinit var filmListView: ListView
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var filmAdapter: FilmAdapter
    private var allFilms: MutableList<Film> = mutableListOf()
    private var filteredFilms: MutableList<Film> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_film_list)

        databaseHelper = DatabaseHelper(this)
        filmListView = findViewById(R.id.film_list_view)

        val searchButton = findViewById<Button>(R.id.search_button)
        val sortButton = findViewById<Button>(R.id.sort_button)
        val filterRatingButton = findViewById<Button>(R.id.filter_rating_button)
        val filterWatchedButton = findViewById<Button>(R.id.filter_watched_button)
        val addMovieButton = findViewById<Button>(R.id.add_movie_button)

        loadAllFilms()

        searchButton.setOnClickListener { showSearchDialog() }
        sortButton.setOnClickListener { showSortDialog() }
        filterRatingButton.setOnClickListener { showRatingFilterDialog() }
        filterWatchedButton.setOnClickListener { showWatchedFilterDialog() }
        addMovieButton.setOnClickListener {
            val intent = Intent(this, AddMovieActivity::class.java)
            startActivity(intent)
        }

        filmListView.setOnItemClickListener { _, _, position, _ ->
            val selectedFilm = filteredFilms[position]
            val intent = Intent(this, MovieDetailsActivity::class.java)
            intent.putExtra("film_id", selectedFilm.filmNum)
            intent.putExtra("imdb_id", selectedFilm.imdbID)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadAllFilms()
    }

    private fun loadAllFilms() {
        allFilms = databaseHelper.getAllFilms().toMutableList()
        filteredFilms = allFilms.toMutableList()
        updateListView()
    }

    private fun updateListView() {
        filmAdapter = FilmAdapter(this, filteredFilms, databaseHelper)
        filmListView.adapter = filmAdapter
    }

    private fun showSearchDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_search, null)
        val searchInput = dialogView.findViewById<EditText>(R.id.search_input)

        AlertDialog.Builder(this)
            .setTitle("Search Films")
            .setView(dialogView)
            .setPositiveButton("Search") { _, _ ->
                val query = searchInput.text.toString().trim()
                if (query.isNotEmpty()) {
                    filteredFilms = allFilms.filter {
                        it.title.contains(query, ignoreCase = true)
                    }.toMutableList()

                    if (filteredFilms.isEmpty()) {
                        Toast.makeText(this, "No movie found, try again", Toast.LENGTH_SHORT).show()
                        filteredFilms = allFilms.toMutableList()
                    } else {
                        updateListView()
                    }
                }
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
            .show()
    }

    private fun showSortDialog() {
        val sortOptions = arrayOf("Title (A-Z)", "Title (Z-A)")
        AlertDialog.Builder(this)
            .setTitle("Sort By")
            .setItems(sortOptions) { _, which ->
                when (which) {
                    0 -> filteredFilms.sortBy { it.title }
                    1 -> filteredFilms.sortByDescending { it.title }
                }
                updateListView()
            }
            .show()
    }

    private fun showRatingFilterDialog() {
        val ratings = (0..10).map { it.toString() }.toTypedArray()
        var selectedRating = 0

        AlertDialog.Builder(this)
            .setTitle("Filter by My Rating")
            .setSingleChoiceItems(ratings, selectedRating) { _, which ->
                selectedRating = which
            }
            .setPositiveButton("Apply") { _, _ ->
                filteredFilms = allFilms.filter {
                    it.myRating >= selectedRating
                }.sortedBy { it.title }.toMutableList()
                updateListView()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
            .show()
    }

    private fun showWatchedFilterDialog() {
        val watchedOptions = arrayOf("All Movies", "Watched (Y)", "Not Watched (N)")
        AlertDialog.Builder(this)
            .setTitle("Filter by Watched")
            .setItems(watchedOptions) { _, which ->
                when (which) {
                    0 -> filteredFilms = allFilms.toMutableList()
                    1 -> filteredFilms = allFilms.filter { it.watched == "Y" }.toMutableList()
                    2 -> filteredFilms = allFilms.filter { it.watched == "N" }.toMutableList()
                }
                filteredFilms = filteredFilms.sortedBy { it.title }.toMutableList()
                updateListView()
            }
            .show()
    }
}