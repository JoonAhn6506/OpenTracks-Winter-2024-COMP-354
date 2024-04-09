package de.dennisguse.opentracks.ui.leaderboard;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import de.dennisguse.opentracks.R;
import de.dennisguse.opentracks.ui.leaderboard.leaderboardFragment.LeaderboardFragment;

public class LeaderboardActivity extends AppCompatActivity {

    private int numberOfUsers = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        LeaderboardPagerAdapter leaderboardPagerAdapter = new LeaderboardPagerAdapter(getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(leaderboardPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                leaderboardPagerAdapter.setCurrentLeaderboardFragment(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        /* Button Events */
        android.view.View averageButton = findViewById(R.id.btnAverageScoreAggregation);
        android.view.View bestButton = findViewById(R.id.btnBestScoreAggregation);
        android.view.View everyoneButton = findViewById(R.id.btnEveryone);
        android.view.View thisSeasonButton = findViewById(R.id.btnThisSeason);
        android.view.View allResortsButton = findViewById(R.id.btnAllResorts);

        android.view.View tenButton = findViewById(R.id.btnTen);
        android.view.View twentyFiveButton = findViewById(R.id.btnTwentyFive);
        android.view.View fiftyButton = findViewById(R.id.btnFifty);

        android.view.View refreshButton = findViewById(R.id.btnRefresh);
        android.view.View backButton = findViewById(R.id.back);

        int optionAvailableColor = ContextCompat.getColor(getApplicationContext(), R.color.opentracks);
        int optionSelectedColor = ContextCompat.getColor(getApplicationContext(), R.color.opentracks_transparent);

        refreshButton.setOnClickListener(v -> leaderboardPagerAdapter.refreshLeaderboardFragmentData());

        backButton.setOnClickListener(v -> onBackPressed());

        tenButton.setOnClickListener(v -> {
            numberOfUsers = 10;
            leaderboardPagerAdapter.setNumberOfUsers(numberOfUsers);
            findViewById(R.id.btnTen).setBackgroundColor(optionSelectedColor);
            findViewById(R.id.btnTwentyFive).setBackgroundColor(optionAvailableColor);
            findViewById(R.id.btnFifty).setBackgroundColor(optionAvailableColor);
        });

        twentyFiveButton.setOnClickListener(v -> {
            numberOfUsers = 25;
            leaderboardPagerAdapter.setNumberOfUsers(numberOfUsers);
            findViewById(R.id.btnTen).setBackgroundColor(optionAvailableColor);
            findViewById(R.id.btnTwentyFive).setBackgroundColor(optionSelectedColor);
            findViewById(R.id.btnFifty).setBackgroundColor(optionAvailableColor);
        });

        fiftyButton.setOnClickListener(v -> {
            numberOfUsers = 0;
            leaderboardPagerAdapter.setNumberOfUsers(numberOfUsers);
            findViewById(R.id.btnTen).setBackgroundColor(optionAvailableColor);
            findViewById(R.id.btnTwentyFive).setBackgroundColor(optionAvailableColor);
            findViewById(R.id.btnFifty).setBackgroundColor(optionSelectedColor);
        });

        averageButton.setBackgroundColor(optionSelectedColor);
        everyoneButton.setBackgroundColor(optionSelectedColor);

        averageButton.setOnClickListener(v -> {
            leaderboardPagerAdapter.setCurrentLeaderboardType(LeaderboardFragment.LeaderboardType.Average);
            averageButton.setBackgroundColor(optionSelectedColor);
            bestButton.setBackgroundColor(optionAvailableColor);
        });

        bestButton.setOnClickListener(v -> {
            leaderboardPagerAdapter.setCurrentLeaderboardType(LeaderboardFragment.LeaderboardType.Best);
            averageButton.setBackgroundColor(optionAvailableColor);
            bestButton.setBackgroundColor(optionSelectedColor);
        });

        everyoneButton.setOnClickListener(v -> {

            everyoneButton.setBackgroundColor(optionSelectedColor);
            thisSeasonButton.setBackgroundColor(optionAvailableColor);
            allResortsButton.setBackgroundColor(optionAvailableColor);
        });

        thisSeasonButton.setOnClickListener(v -> {

            everyoneButton.setBackgroundColor(optionAvailableColor);
            thisSeasonButton.setBackgroundColor(optionSelectedColor);
            allResortsButton.setBackgroundColor(optionAvailableColor);
        });

        allResortsButton.setOnClickListener(v -> {

            everyoneButton.setBackgroundColor(optionAvailableColor);
            thisSeasonButton.setBackgroundColor(optionAvailableColor);
            allResortsButton.setBackgroundColor(optionSelectedColor);
        });
    }
}