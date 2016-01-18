package com.fourthwardcoder.android.volumemanager.data;

import android.net.Uri;

/**
 * Created by Chris Hare on 1/17/2016.
 */
public class ProfileContract {

    //Content provider authority for Movie DB
    public static final String CONTENT_AUTHORITY = "com.fourthwardcoder.android.volumemanage";

    //Base URI for content provider
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //path to movie data in content provider
    public static final String PATH_MOVIE = "profile";
}
