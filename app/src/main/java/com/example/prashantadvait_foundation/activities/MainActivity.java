package com.example.prashantadvait_foundation.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.example.prashantadvait_foundation.R;
import com.example.prashantadvait_foundation.Utils.ImageCache;
import com.example.prashantadvait_foundation.adapters.GridAdapter;
import com.example.prashantadvait_foundation.network.APIService;
import com.example.prashantadvait_foundation.network.ApiClient;
import com.example.prashantadvait_foundation.network.ResponseModel;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity{

    private RecyclerView recyclerView;
    private GridAdapter adapter;
    private Gson gson = new Gson();
    private List<String> imageList = new ArrayList<>();

    private List<ResponseModel> data;

    private List<String> keyList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Execute network request in a background thread
        new AsyncTask<Void, Void, List<ResponseModel>>() {
            @Override
            protected List<ResponseModel> doInBackground(Void... voids) {
                APIService apiService = ApiClient.getClient().create(APIService.class);
                Call<List<ResponseModel>> call = apiService.getResponseData(100);
                try {
                    Response<List<ResponseModel>> response = call.execute();
                    if (response.isSuccessful()) {
                        return response.body();
                    } else {
                        // Handle error response
                        Log.e("mytag", "Error response: " + response.message());
                        return null;
                    }
                } catch (IOException e) {
                    // Handle IO exception
                    Log.e("mytag", "IOException: " + e.getMessage());
                    return null;
                }
            }

            @Override
            protected void onPostExecute(List<ResponseModel> responseModels) {
                if (responseModels != null) {
                    // Handle successful response
                    data = responseModels;
                    Log.d("mytag", "Response data :" + gson.toJson(data));
                    Log.d("mytag", "Response data size :" + data.size());
                    for (int i = 0; i < data.size(); i++) {
                        String imageStr = data.get(i).getThumbnail().getDomain() + "/" + data.get(i).getThumbnail().getBasePath() + "/0/" + data.get(i).getThumbnail().getKey();
                        imageList.add(i, imageStr);
                        keyList.add(i, data.get(i).getThumbnail().getBasePath());
                    }
                    Log.d("mytag", "imageList :" + gson.toJson(imageList));
                    Log.d("mytag", "imageList :" + imageList.size());
                    // Set up RecyclerView and adapter after obtaining data
                    recyclerView = findViewById(R.id.recyclerView);
                    recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 3));
                    adapter = new GridAdapter(MainActivity.this, imageList, keyList);
                    recyclerView.setAdapter(adapter);

                    // Cache images
                    ImageCache imageCache = new ImageCache(MainActivity.this);
                    imageCache.cacheImages(imageList);
                } else {
                    // Handle failure
                    Toast.makeText(MainActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();

    }

}
