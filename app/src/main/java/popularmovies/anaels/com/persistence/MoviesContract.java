package popularmovies.anaels.com.persistence;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Contract for the movie DB
 */
public class MoviesContract {

	public static final String CONTENT_AUTHORITY = "popularmovies.anaels.com";

	public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


	public static final class MovieEntry implements BaseColumns{
		// table name
		public static final String TABLE_MOVIES = "movie";
		// columns
		public static final String _ID = "_id";
		public static final String COLUMN_VOTE_AVERAGE = "vote_average";
		public static final String COLUMN_TITLE = "title";
		public static final String COLUMN_POSTER_PATH = "poster_path";
		public static final String COLUMN_OVERVIEW = "overview";
		public static final String COLUMN_RELEASE_DATE = "release_date";
		public static final String COLUMN_FAVORITE = "favorite";
		public static final String COLUMN_POPULARITY = "popularity";

		public static final String[] getAllColumn(){
			return new String[]{_ID,COLUMN_POSTER_PATH,COLUMN_RELEASE_DATE,COLUMN_TITLE,COLUMN_FAVORITE,COLUMN_OVERVIEW,COLUMN_POPULARITY,COLUMN_VOTE_AVERAGE};
		}

		// create content uri
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
			.appendPath(TABLE_MOVIES).build();
		// create cursor of base type directory for multiple entries
		public static final String CONTENT_DIR_TYPE =
		ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_MOVIES;
		// create cursor of base type item for single entry
		public static final String CONTENT_ITEM_TYPE =
			ContentResolver.CURSOR_ITEM_BASE_TYPE +"/" + CONTENT_AUTHORITY + "/" + TABLE_MOVIES;

		// for building URIs on insertion
		public static Uri buildMovieURI(long id){
        		return ContentUris.withAppendedId(CONTENT_URI, id);
		}
	}
}