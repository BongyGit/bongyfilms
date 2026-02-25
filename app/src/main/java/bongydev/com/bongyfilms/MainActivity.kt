package bongydev.com.bongyfilms

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val enterButton = findViewById<Button>(R.id.enter_button)
        enterButton.setOnClickListener {
            val intent = Intent(this, FilmListActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}