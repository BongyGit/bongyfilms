package bongydev.com.bongyfilms.database

import android.content.Context
import android.os.Environment
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.StandardCopyOption

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
}
