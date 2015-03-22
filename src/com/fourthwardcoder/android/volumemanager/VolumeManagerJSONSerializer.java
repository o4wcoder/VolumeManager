package com.fourthwardcoder.android.volumemanager;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import android.content.Context;
import android.util.Log;

public class VolumeManagerJSONSerializer {
	
	/******************************************************/
	/*                   Constants                        */
	/******************************************************/
	private static final String TAG = "VolumeManagerJSONSerializer";
	/******************************************************/
	/*                   Local Data                       */
	/******************************************************/
	private Context mContext;
	private String mFilename;
	
	/******************************************************/
	/*                 Constructors                       */
	/******************************************************/
	public VolumeManagerJSONSerializer(Context c, String f) {
		mContext = c;
		mFilename = f;
	}
	
	/******************************************************/
	/*                Public Methods                      */
	/******************************************************/
	public ArrayList<Profile> loadProfiles() throws IOException, JSONException {
		ArrayList<Profile> profiles = new ArrayList<Profile>();
		
		BufferedReader reader = null;
		
		try {
			//Open and read the file into a StringBuilder
			InputStream in = mContext.openFileInput(mFilename);
			reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder jsonString = new StringBuilder();
			
			String line = null;
			while((line = reader.readLine()) != null) {
				//Line breaks are omitted and irrelevant
				jsonString.append(line);	
			}
			//Parse the JSON using JSONTokener
			JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
			//Build the array of profiles from JSONObjects
			for(int i = 0; i< array.length(); i++) {
				profiles.add(new Profile(array.getJSONObject(i)));
			}
		} catch (FileNotFoundException e) {
			//Ignore this on; it happens when starting fresh
		} finally {
			if(reader != null)
				reader.close();
		}
		return profiles;
	}
	
	public void saveProfiles(ArrayList<Profile> profiles) throws JSONException, IOException {
		
		//Build and array in JSON
		JSONArray array = new JSONArray();
		for(Profile p : profiles) {
			
		    //Log.d(TAG,"JSon Data");
		    //Log.d(TAG,p.toJSON().toString());
			array.put(p.toJSON());
		}
		//Write the file to disk
		Writer writer = null;
		try {
			OutputStream out = mContext.openFileOutput(mFilename, Context.MODE_PRIVATE);
			writer = new OutputStreamWriter(out);
			writer.write(array.toString());
	
			
		} finally {
			if(writer != null)
				writer.close();
		}
	}

}
