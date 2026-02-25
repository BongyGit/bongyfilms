package bongydev.com.bongyfilms.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import bongydev.com.bongyfilms.models.Film
import java.io.File

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "bongyfilmsDB.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_FILMS = "films"
        private const val COLUMN_FILM_NUM = "filmNum"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_YEAR = "year"
        private const val COLUMN_WATCHED = "watched"
        private const val COLUMN_IMDB_RATING = "imdbRating"
        private const val COLUMN_MY_RATING = "myRating"
        private const val COLUMN_IMDB_ID = "imdbID"
    }

    private val context = context

    override fun onCreate(db: SQLiteDatabase) {
        // Database already exists in external storage, no need to create
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Handle upgrades if needed
    }

    fun getAllFilms(): List<Film> {
        val films = mutableListOf<Film>()
        val db = getExternalDatabase()
        
        db?.use {
            val cursor = it.query(
                TABLE_FILMS,
                null,
                null,
                null,
                null,
                null,
                "$COLUMN_TITLE ASC"
            )
            
            cursor?.use {
                while (it.moveToNext()) {
                    val filmNum = it.getInt(it.getColumnIndexOrThrow(COLUMN_FILM_NUM))
                    val title = it.getString(it.getColumnIndexOrThrow(COLUMN_TITLE))
                    val year = it.getString(it.getColumnIndexOrThrow(COLUMN_YEAR))
                    val watched = it.getString(it.getColumnIndexOrThrow(COLUMN_WATCHED))
                    val imdbRating = it.getDouble(it.getColumnIndexOrThrow(COLUMN_IMDB_RATING))
                    val myRating = it.getInt(it.getColumnIndexOrThrow(COLUMN_MY_RATING))
                    val imdbID = it.getString(it.getColumnIndexOrThrow(COLUMN_IMDB_ID))
                    
                    films.add(Film(
                        filmNum = filmNum,
                        title = title,
                        year = year,
                        watched = watched,
                        imdbRating = imdbRating,
                        myRating = myRating,
                        imdbID = imdbID,
                        posterUrl = "" // Will be fetched from OMDb API
                    ))
                }
            }
        }
        
        return films
    }

    fun getFilmById(filmNum: Int): Film? {
        val db = getExternalDatabase()
        
        db?.use {
            val cursor = it.query(
                TABLE_FILMS,
                null,
                "$COLUMN_FILM_NUM = ?",
                arrayOf(filmNum.toString()),
                null,
                null,
                null
            )
            
            cursor?.use {
                if (it.moveToFirst()) {
                    val title = it.getString(it.getColumnIndexOrThrow(COLUMN_TITLE))
                    val year = it.getString(it.getColumnIndexOrThrow(COLUMN_YEAR))
                    val watched = it.getString(it.getColumnIndexOrThrow(COLUMN_WATCHED))
                    val imdbRating = it.getDouble(it.getColumnIndexOrThrow(COLUMN_IMDB_RATING))
                    val myRating = it.getInt(it.getColumnIndexOrThrow(COLUMN_MY_RATING))
                    val imdbID = it.getString(it.getColumnIndexOrThrow(COLUMN_IMDB_ID))
                    
                    return Film(
                        filmNum = filmNum,
                        title = title,
                        year = year,
                        watched = watched,
                        imdbRating = imdbRating,
                        myRating = myRating,
                        imdbID = imdbID,
                        posterUrl = ""
                    )
                }
            }
        }
        
        return null
    }

    fun updateFilmRatings(filmNum: Int, myRating: Int, watched: String) {
        val db = getExternalDatabase()
        
        db?.use {
            val contentValues = android.content.ContentValues().apply {
                put(COLUMN_MY_RATING, myRating)
                put(COLUMN_WATCHED, watched)
            }
            
            it.update(
                TABLE_FILMS,
                contentValues,
                "$COLUMN_FILM_NUM = ?",
                arrayOf(filmNum.toString())
            )
        }
    }

    fun addFilm(film: Film) {
        val db = getExternalDatabase()
        
        db?.use {
            val contentValues = android.content.ContentValues().apply {
                put(COLUMN_TITLE, film.title)
                put(COLUMN_YEAR, film.year)
                put(COLUMN_WATCHED, film.watched)
                put(COLUMN_IMDB_RATING, film.imdbRating)
                put(COLUMN_MY_RATING, film.myRating)
                put(COLUMN_IMDB_ID, film.imdbID)
            }
            
            it.insert(TABLE_FILMS, null, contentValues)
        }
    }

    private fun getExternalDatabase(): SQLiteDatabase? {
        val dbPath = "/storage/emulated/0/Android/data/bongyfilms/filmdata/bongyfilmsDB.db"
        val dbFile = File(dbPath)
        
        return if (dbFile.exists()) {
            try {
                SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE)
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }
}