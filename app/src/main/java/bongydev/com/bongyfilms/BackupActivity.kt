package bongydev.com.bongyfilms

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import bongydev.com.bongyfilms.database.BackupRestoreManager
import java.io.File

class BackupActivity : AppCompatActivity() {

    private lateinit var backupRestoreManager: BackupRestoreManager

    private val createBackupFileLauncher =
        registerForActivityResult(ActivityResultContracts.CreateDocument("application/octet-stream")) { uri ->
            if (uri != null) {
                try {
                    // Get the file path from the URI
                    val file = File(uri.path ?: return@registerForActivityResult)
                    
                    // Perform the backup
                    val (success, message) = backupRestoreManager.backupDatabase(file)
                    
                    if (success) {
                        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        this,
                        "Backup failed: ${e.message ?: "Unknown error"}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                Toast.makeText(this, "Backup cancelled", Toast.LENGTH_SHORT).show()
            }
            
            // Finish this activity
            finish()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        backupRestoreManager = BackupRestoreManager(this)

        // Trigger the backup process immediately
        triggerBackupProcess()
    }

    private fun triggerBackupProcess() {
        try {
            val defaultFileName = backupRestoreManager.getDefaultBackupFileName()
            createBackupFileLauncher.launch(defaultFileName)
        } catch (e: Exception) {
            Toast.makeText(
                this,
                "Failed to start backup: ${e.message ?: "Unknown error"}",
                Toast.LENGTH_LONG
            ).show()
            finish()
        }
    }
}
