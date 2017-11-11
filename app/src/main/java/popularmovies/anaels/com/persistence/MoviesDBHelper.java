package popularmovies.anaels.com.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class MoviesDBHelper extends SQLiteOpenHelper {
	public static final String LOG_TAG = MoviesDBHelper.class.getSimpleName();

	//name & version
	private static final String DATABASE_NAME = "movies.db";
	private static final int DATABASE_VERSION = 12;

	public MoviesDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Create the database
	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase) {
		final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " +
				MoviesContract.MovieEntry.TABLE_MOVIES + "(" + MoviesContract.MovieEntry._ID +
				" INTEGER PRIMARY KEY, " +
				MoviesContract.MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
				MoviesContract.MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
				MoviesContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
				MoviesContract.MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
				MoviesContract.MovieEntry.COLUMN_POPULARITY + " DOUBLE NOT NULL, " +
				MoviesContract.MovieEntry.COLUMN_FAVORITE + " INTEGER DEFAULT 0, " +
				MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE + " DOUBLE NOT NULL);";

		sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
	}

	// Upgrade database when version is changed.
	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
		Log.w(LOG_TAG, "Upgrading database from version " + oldVersion + " to " +
				newVersion + ". OLD DATA WILL BE DESTROYED");
		// Drop the table
		sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MovieEntry.TABLE_MOVIES);
        sqLiteDatabase.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                MoviesContract.MovieEntry.TABLE_MOVIES + "'");

		// re-create database
		onCreate(sqLiteDatabase);
	}
}