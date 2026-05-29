package bongydev.com.bongyfilms.database

import android.content.Context
import android.net.Uri
import android.os.Environment
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class BackupRestoreManager(private val context: Context) {

    companion object {
        private const val DATABASE_NAME = "bongyfilms.db"
        private const val BACKUP_DATABASE_NAME = "bongyfilmsBackup.db"
    }

    /**
     * Get the path to the app's database file
     */
    fun getDatabasePath(): File {
        return context.getDatabasePath(DATABASE_NAME)
    }

    /**
     * Backup the database to a specified location
     * @param destinationFile The file where the backup should be saved
     * @return Pair of (success: Boolean, message: String)
     */
    fun backupDatabase(destinationFile: File): Pair<Boolean, String> {
        return try {
            val sourceFile = getDatabasePath()

            if (!sourceFile.exists()) {
                return Pair(false, "Backup failed: Source database not found")
            }

            // Create parent directories if needed
            destinationFile.parentFile?.mkdirs()

            // Copy the database file
            FileInputStream(sourceFile).use { input ->
                FileOutputStream(destinationFile).use { output ->
                    input.copyTo(output)
                }
            }

            val folderPath = destinationFile.parent ?: "Unknown location"
            Pair(true, "Database saved as ${destinationFile.name} in $folderPath")
        } catch (e: Exception) {
            Pair(false, "Backup failed: ${e.message ?: "Unknown error"}")
        }
    }

    /**
     * Backup the database using a content URI (for use with CreateDocument)
     * @param uri The content URI where the backup should be saved
     * @return Pair of (success: Boolean, message: String)
     */
    fun backupDatabaseToUri(uri: Uri): Pair<Boolean, String> {
        return try {
            val sourceFile = getDatabasePath()

            if (!sourceFile.exists()) {
                return Pair(false, "Backup failed: Source database not found")
            }

            // Use content resolver to write to the URI
            context.contentResolver.openOutputStream(uri)?.use { output ->
                FileInputStream(sourceFile).use { input ->
                    input.copyTo(output)
                }
            } ?: return Pair(false, "Backup failed: Could not open output stream")

            val fileName = getFileNameFromUri(uri)
            Pair(true, "Database saved as $fileName")
        } catch (e: Exception) {
            Pair(false, "Backup failed: ${e.message ?: "Unknown error"}")
        }
    }

    /**
     * Restore the database from a backup file
     * @param backupFile The backup file to restore from
     * @return Pair of (success: Boolean, message: String)
     */
    fun restoreDatabase(backupFile: File): Pair<Boolean, String> {
        return try {
            if (!backupFile.exists()) {
                return Pair(false, "Database restore failed: Backup file not found")
            }

            val destinationFile = getDatabasePath()

            // Close any open database connections
            val dbHelper = DatabaseHelper(context)
            dbHelper.close()

            // Create parent directories if needed
            destinationFile.parentFile?.mkdirs()

            // Copy the backup to the database location
            FileInputStream(backupFile).use { input ->
                FileOutputStream(destinationFile).use { output ->
                    input.copyTo(output)
                }
            }

            Pair(true, "Database restore successful")
        } catch (e: Exception) {
            Pair(false, "Database restore failed: ${e.message ?: "Unknown error"}")
        }
    }

    /**
     * Restore the database from a content URI
     * @param uri The content URI of the backup file
     * @return Pair of (success: Boolean, message: String)
     */
    fun restoreDatabaseFromUri(uri: Uri): Pair<Boolean, String> {
        return try {
            val destinationFile = getDatabasePath()

            // Close any open database connections
            val dbHelper = DatabaseHelper(context)
            dbHelper.close()

            // Create parent directories if needed
            destinationFile.parentFile?.mkdirs()

            // Read from the URI and write to the database location
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(destinationFile).use { output ->
                    input.copyTo(output)
                }
            } ?: return Pair(false, "Database restore failed: Could not open input stream")

            Pair(true, "Database restore successful")
        } catch (e: Exception) {
            Pair(false, "Database restore failed: ${e.message ?: "Unknown error"}")
        }
    }

    /**
     * Find backup files in a directory
     * @param directory The directory to search in
     * @return List of backup files with bongyfilmsBackup prefix
     */
    fun findBackupFiles(directory: File): List<File> {
        if (!directory.exists() || !directory.isDirectory) {
            return emptyList()
        }

        return directory.listFiles { file ->
            file.isFile && file.name.startsWith(BACKUP_DATABASE_NAME) && file.name.endsWith(".db")
        }?.sortedByDescending { it.lastModified() } ?: emptyList()
    }

    /**
     * Get the default backup file name
     */
    fun getDefaultBackupFileName(): String {
        return BACKUP_DATABASE_NAME
    }

    /**
     * Get file name from URI
     */
    private fun getFileNameFromUri(uri: Uri): String {
        return try {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    if (nameIndex >= 0) {
                        return it.getString(nameIndex)
                    }
                }
            }
            uri.lastPathSegment ?: "backup"
        } catch (e: Exception) {
            uri.lastPathSegment ?: "backup"
        }
    }
}
