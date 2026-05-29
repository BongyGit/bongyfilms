package bongydev.com.bongyfilms

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.view.Menu
import android.view.MenuItem

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_backup -> {
                val intent = Intent(this, BackupActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_restore -> {
                val intent = Intent(this, RestoreActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
