package com.example.pokeremotionapplication.util;

import android.content.Context;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class JsonFileUtils {
    public static String loadJSONFromAsset(Context context, String fileName) {
        StringBuilder json = new StringBuilder();
        try {
            InputStream is = context.getAssets().open(fileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null) {
                json.append(line);
            }
            br.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json.toString();
    }
}