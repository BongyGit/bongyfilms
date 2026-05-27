package bongydev.com.bongyfilms

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import bongydev.com.bongyfilms.database.DatabaseHelper
import bongydev.com.bongyfilms.models.Film
import com.bumptech.glide.Glide

class FilmAdapter(
    context: Context,
    private val films: List<Film>,
    private val databaseHelper: DatabaseHelper
) : ArrayAdapter<Film>(context, 0, films) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.film_list_item, parent, false)
        }

        val film = films[position]
        
        val posterImageView = view?.findViewById<ImageView>(R.id.film_poster)
        val titleTextView = view?.findViewById<TextView>(R.id.film_title)
        val yearTextView = view?.findViewById<TextView>(R.id.film_year)
        val ratingsTextView = view?.findViewById<TextView>(R.id.film_ratings)

        titleTextView?.text = film.title
        yearTextView?.text = "Year: ${film.year}"
        ratingsTextView?.text = "IMDb: ${film.imdbRating} | My Rating: ${film.myRating} | Watched: ${film.watched}"

        // Load poster image using Glide
        if (film.posterUrl.isNotEmpty()) {
            Glide.with(context)
                .load(film.posterUrl)
                .placeholder(R.drawable.ic_placeholder)
                .into(posterImageView!!)
        } else {
            posterImageView?.setImageResource(R.drawable.ic_placeholder)
        }

        return view!!
    }
}
