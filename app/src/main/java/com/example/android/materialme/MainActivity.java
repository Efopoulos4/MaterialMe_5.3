/*
 * Copyright (C) 2018 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.materialme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

/***
 * Main Activity for the Material Me app, a mock sports news application
 * with poor design choices.
 */
public class MainActivity extends AppCompatActivity {

    // Member variables.
    private RecyclerView mRecyclerView;
    private ArrayList<Sport> mSportsData;
    private SportsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int gridColumnCount = getResources().getInteger(R.integer.grid_column_count);

        // Initialize the RecyclerView.
        mRecyclerView = findViewById(R.id.recyclerView);
        // Set the Layout Manager.
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, gridColumnCount));

        if(savedInstanceState != null){
            mSportsData = savedInstanceState.getParcelableArrayList("key");
            mAdapter = new SportsAdapter(this, mSportsData);
            mRecyclerView.setAdapter(mAdapter);
        }
        if(mSportsData == null){
            mSportsData = new ArrayList<>();
            mAdapter = new SportsAdapter(this, mSportsData);
            mRecyclerView.setAdapter(mAdapter);
            initializeData();
        }

        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.DOWN | ItemTouchHelper.UP, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int from = viewHolder.getAdapterPosition();
                int to = target.getAdapterPosition();
                Collections.swap(mSportsData, from, to);
                mAdapter.notifyItemMoved(from, to);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                mSportsData.remove(viewHolder.getAdapterPosition());
                mAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
            }
        });
        helper.attachToRecyclerView(mRecyclerView);
    }

    /**
     * Initialize the sports data from resources.
     */
    private void initializeData() {
        TypedArray sportsImageResources = getResources().obtainTypedArray(R.array.sport_images);
        // Get the resources from the XML file.
        String[] sportsList = getResources()
                .getStringArray(R.array.sports_titles);
        String[] sportsInfo = getResources()
                .getStringArray(R.array.sports_info);
        // Clear the existing data (to avoid duplication).
        mSportsData.clear();

        for(int i=0;i<sportsList.length;i++){
            mSportsData.add(new Sport(sportsImageResources.getResourceId(i,0), sportsList[i],sportsInfo[i]));
        }
        sportsImageResources.recycle();

        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArrayList("key", mSportsData);
        super.onSaveInstanceState(outState);
    }

    public void resetSports(View view) {
        initializeData();
    }
}
