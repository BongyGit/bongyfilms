package bongydev.com.bongyfilms

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import bongydev.com.bongyfilms.database.BackupRestoreManager

class RestoreActivity : AppCompatActivity() {

    private lateinit var backupRestoreManager: BackupRestoreManager

    private val openFileLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null) {
                try {
                    // Get the file name from the URI
                    val fileName = getFileNameFromURI(uri)

                    if (!fileName.endsWith(".db")) {
                        Toast.makeText(
                            this,
                            "Database restore failed: File must be a database file (.db)",
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (!fileName.startsWith("bongyfilmsBackup")) {
                        Toast.makeText(
                            this,
                            "Database restore failed: File must be a bongyfilmsBackup file",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        // Perform the restore using content URI
                        val (success, message) = backupRestoreManager.restoreDatabaseFromUri(uri)
                        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        this,
                        "Database restore failed: ${e.message ?: "Unknown error"}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                Toast.makeText(this, "Restore cancelled", Toast.LENGTH_SHORT).show()
            }

            // Finish this activity
            finish()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        backupRestoreManager = BackupRestoreManager(this)

        // Trigger the restore process immediately
        triggerRestoreProcess()
    }

    private fun triggerRestoreProcess() {
        try {
            openFileLauncher.launch(arrayOf("application/octet-stream"))
        } catch (e: Exception) {
            Toast.makeText(
                this,
                "Failed to start restore: ${e.message ?: "Unknown error"}",
                Toast.LENGTH_LONG
            ).show()
            finish()
        }
    }

    private fun getFileNameFromURI(uri: android.net.Uri): String {
        var result = ""
        if (uri.scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    if (nameIndex >= 0) {
                        result = it.getString(nameIndex)
                    }
                }
            }
        }
        if (result.isEmpty()) {
            result = uri.path?.substringAfterLast('/') ?: ""
        }
        return result
    }
}

