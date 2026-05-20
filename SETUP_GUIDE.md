
```markdown name=SETUP_GUIDE.md
# BongyFilms Android App - Complete Setup Guide

## Prerequisites

- Windows PC with Android Studio installed
- Android SDK API Level 26 (Android 8.0) and above
- SQLite database file: `BongyFilms.db`
- welcome_background.jpg image (black and white)

## Step-by-Step Setup Instructions

### Step 1: Create New Android Project

1. Open Android Studio
2. Click "New Project"
3. Select "Empty Activity" template
4. Configure project:
   - **Name**: bongyfilms
   - **Package name**: bongydev.com.bongyfilms
   - **Save location**: Choose your preferred location
   - **Language**: Kotlin
   - **Minimum SDK**: API 26 (Android 8.0)
   - Click "Finish"

### Step 2: Prepare Database

1. On your Android phone, create the folder structure:

/storage/emulated/0/Android/data/bongyfilms/filmdata/


2. Copy your `BongyFilms.db` file to this location

3. Verify the database contains the "films" table with all required fields:
- filmNum (INTEGER PRIMARY KEY AUTOINCREMENT)
- title (REAL)
- year (TEXT)
- watched (TEXT)
- imdbRating (NUMERIC)
- myRating (INTEGER)
- imdbID (TEXT)

### Step 3: Prepare Background Image

1. Place your welcome_background.jpg image in:

app/src/main/res/drawable/


2. Rename it to: `welcome_background.jpg`

3. If the drawable folder doesn't exist, create it manually

### Step 4: Update build.gradle.kts

1. In Android Studio, open `app/build.gradle.kts`
2. Replace the entire content with the provided `build.gradle.kts` file
3. Sync Gradle files (click "Sync Now" when prompted)

### Step 5: Create Package Structure

1. In Android Studio, right-click on `bongydev.com.bongyfilms` package
2. Create new packages:
- `database`
- `models`
- `network`

### Step 6: Add Source Files

1. **Main Activities**: Copy to `bongydev.com.bongyfilms/`
- MainActivity.kt
- FilmListActivity.kt
- MovieDetailsActivity.kt
- AddMovieActivity.kt
- FilmAdapter.kt

2. **Database**: Copy to `bongydev.com.bongyfilms/database/`
- DatabaseHelper.kt

3. **Models**: Copy to `bongydev.com.bongyfilms/models/`
- Film.kt

4. **Network**: Copy to `bongydev.com.bongyfilms/network/`
- OmdbApiClient.kt

### Step 7: Add Layout Files

Copy all XML layout files to `app/src/main/res/layout/`:
- activity_main.xml
- activity_film_list.xml
- film_list_item.xml
- activity_movie_details.xml
- activity_add_movie.xml
- dialog_search.xml

### Step 8: Update Manifest

Replace `app/src/main/AndroidManifest.xml` with the provided version

### Step 9: Update Strings

Replace `app/src/main/res/values/strings.xml` with the provided version

### Step 10: Build and Test

1. Click "Build" → "Clean Project"
2. Click "Build" → "Build App Bundle(s) / APK(s)" → "Build APK(s)"
3. Wait for the build to complete
4. Click "Run" or select your device/emulator
5. Select your target device and click "OK"

## Testing the App

### Test Case 1: Splash Screen
1. App launches with welcome_background.jpg image
2. "Bongy Films" text is displayed
3. "Enter The Emporium" button is visible
4. Tap button → navigates to Film List

### Test Case 2: Film List
1. All films from database are displayed
2. Each row shows: Poster | Title | Year | IMDb Rating | My Rating | Watched
3. List is scrollable (shows up to 6 films per screen)

### Test Case 3: Search
1. Tap "Search" button
2. Enter movie title
3. Tap "Search" in dialog
4. Matching films are displayed
5. Tap film → opens Movie Details

### Test Case 4: Sort
1. Tap "Sort" button
2. Select "Title (A-Z)" or "Title (Z-A)"
3. List is reordered accordingly

### Test Case 5: Filter by Rating
1. Tap "Rating" button
2. Select minimum rating (0-10)
3. List shows only films with that rating or higher

### Test Case 6: Filter by Watched
1. Tap "Watched" button
2. Select "All Movies", "Watched (Y)", or "Not Watched (N)"
3. List is filtered accordingly

### Test Case 7: Movie Details
1. Tap any film in the list
2. Details screen opens showing:
- Full poster image
- Title, Year, Genre, Plot
- IMDb Rating (read-only)
- My Rating (dropdown 0-10)
- Watched (dropdown Y/N)
3. Change a value and tap "Go Back"
4. Save/Cancel dialog appears
5. Select "Save" to persist changes

### Test Case 8: Add Movie
1. Tap "+" button on Film List
2. Enter movie title (e.g., "The Shawshank Redemption")
3. Optionally enter year
4. Tap "Search"
5. Results display with posters
6. Tap a movie to select
7. Confirmation dialog appears
8. Tap "Save" to add to library

## Troubleshooting

### Issue: "BongyFilms.db not found"
**Solution**: Ensure the database file is in the correct location:
`/storage/emulated/0/Android/data/bongyfilms/filmdata/BongyFilms.db`

### Issue: Image not loading from OMDb API
**Solution**: 
- Check internet connection
- Verify API Key: `8d7b2328`
- Check manifest has `<uses-permission android:name="android.permission.INTERNET" />`

### Issue: App crashes on startup
**Solution**:
- Check AndroidManifest.xml is updated correctly
- Verify all Activities are declared in manifest
- Clean and rebuild project

### Issue: Gradle sync fails
**Solution**:
- Check build.gradle.kts syntax
- Verify SDK versions match your Android Studio setup
- Try: Build → Clean Project → Sync Now

## Important Notes

- The app requires Android 8.0 (API 26) or higher
- Internet connection is required for OMDb API calls
- First launch might be slow as it loads images from API
- All changes are saved to the local SQLite database
- The OMDb API has a free tier with request limits

## Next Steps

After successful setup and testing:
1. Customize the app theme/colors as desired
2. Add more features or UI improvements
3. Test on multiple devices for compatibility
4. Consider publishing to Google Play Store

## Support

For issues with the OMDb API, visit: https://www.omdbapi.com/
