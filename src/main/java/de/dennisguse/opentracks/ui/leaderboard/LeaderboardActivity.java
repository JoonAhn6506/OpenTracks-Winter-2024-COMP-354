package de.dennisguse.opentracks.ui.leaderboard;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import de.dennisguse.opentracks.R;
import de.dennisguse.opentracks.ui.leaderboard.leaderboardFragment.LeaderboardFragment;

public class LeaderboardActivity extends AppCompatActivity {

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

        findViewById(R.id.btnRefresh).setOnClickListener(v -> leaderboardPagerAdapter.refreshLeaderboardFragmentData());

        findViewById(R.id.back).setOnClickListener(v -> onBackPressed());

        findViewById(R.id.btnAverageScoreAggregation).setOnClickListener(
                v -> leaderboardPagerAdapter.setCurrentLeaderboardType(LeaderboardFragment.LeaderboardType.Average)
        );

        findViewById(R.id.btnBestScoreAggregation).setOnClickListener(
                v -> leaderboardPagerAdapter.setCurrentLeaderboardType(LeaderboardFragment.LeaderboardType.Best)
        );
    }
}
