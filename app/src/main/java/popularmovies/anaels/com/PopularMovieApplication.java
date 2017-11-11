package popularmovies.anaels.com;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Used to init Stetho
 */
public class PopularMovieApplication extends Application {
  public void onCreate() {
    super.onCreate();
    Stetho.initializeWithDefaults(this);
  }
}