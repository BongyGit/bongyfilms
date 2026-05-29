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
                    // Get the file from content URI
                    val file = if (uri.scheme == "content") {
                        // For content URIs, we need to get the real path or use the content resolver
                        getRealPathFromURI(uri) ?: File(uri.path ?: "")
                    } else {
                        File(uri.path ?: "")
                    }
                    
                    // Perform the backup
                    val (success, message) = backupRestoreManager.backupDatabaseToUri(uri)
                    
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
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

    private fun getRealPathFromURI(uri: android.net.Uri): String? {
        var cursor: android.database.Cursor? = null
        return try {
            val projection = arrayOf(android.provider.MediaStore.Images.ImageColumns.DATA)
            cursor = contentResolver.query(uri, projection, null, null, null)
            val columnIndex = cursor?.getColumnIndexOrThrow(android.provider.MediaStore.Images.ImageColumns.DATA)
            cursor?.moveToFirst()
            if (columnIndex != null) cursor?.getString(columnIndex) else null
        } finally {
            cursor?.close()
        }
    }
}

