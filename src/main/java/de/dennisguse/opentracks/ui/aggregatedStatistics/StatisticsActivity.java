package de.dennisguse.opentracks.ui.aggregatedStatistics;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;

import java.util.List;

import de.dennisguse.opentracks.AbstractActivity;
import de.dennisguse.opentracks.R;
import de.dennisguse.opentracks.databinding.AggregatedStatsBinding;
import de.dennisguse.opentracks.stats.MockupData;
import de.dennisguse.opentracks.stats.TrackStatistics;
import de.dennisguse.opentracks.databinding.StatsBinding;
import de.dennisguse.opentracks.util.IntentDashboardUtils;

public class StatisticsActivity extends AbstractActivity {
    private static final String TAG = StatisticsActivity.class.getSimpleName();
    private StatsBinding viewBinding;
    private MenuItem mapItem;

    private AggregatedStatsCache aggregatedStatsCache;
    private Gson gson;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gson = new GsonBuilder().serializeSpecialFloatingPointValues().create();
        aggregatedStatsCache = new AggregatedStatsCache(getApplicationContext());

        TrackStatistics cachedStats = aggregatedStatsCache.get();
        if(cachedStats != null)
        {
            UpdateUI(cachedStats);
        }
        else
        {
            calculateStats();
        }
        setSupportActionBar(viewBinding.bottomAppBarLayout.bottomAppBar);
    }

    private void calculateStats()
    {
        MockupData mockupData = (MockupData) getIntent().getSerializableExtra("data");
        List<TrackStatistics> trackStatistics = mockupData.getTrackStatistics();
        TrackStatistics summary = TrackStatistics.sumOfTotalStats(trackStatistics);

        UpdateUI(summary);
        aggregatedStatsCache.put(summary);

    }
    private void UpdateUI(TrackStatistics summary)
    {
        TextView totalDistance = findViewById(R.id.totalTrackDistId);
        TextView totalVert = findViewById(R.id.totalVertId);
        TextView skiDays = findViewById(R.id.skiDaysId);
        TextView totalRuns = findViewById(R.id.totalRunsId);
        TextView avgSpeed = findViewById(R.id.avgSpeedId);
        TextView avgSlp = findViewById(R.id.avgSlopeId);

        totalDistance.setText((summary.getTotalTrackDistanceSeason().toM()) + "M");
        totalVert.setText(summary.getTotalVerticalDescentSeasonSeason().toM() + "M");
        skiDays.setText(String.valueOf(summary.getTotalSkiDaysSeason()));
        totalRuns.setText(String.valueOf(summary.getTotalRunsSeason()));
        avgSpeed.setText(summary.getAvgSpeedSeason().toKMH() + "KMH");
        avgSlp.setText((summary.getSlopePercentageSeason() + "%"));
    }
    @Override
    protected View getRootView() {
        viewBinding = StatsBinding.inflate(getLayoutInflater());

        return viewBinding.getRoot();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.overall_stats, menu);

        mapItem = menu.findItem(R.id.aggregated_statistics_map);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.aggregated_statistics_map) {
            try {
                IntentDashboardUtils.startOverallSeasonDashboard(this, "");
                return true;
            }
            catch (JSONException exc) {
                Log.e(TAG,
                        "[StatisticsActivity/onOptionsItemSelected] -- A handled error" +
                                " occurred involving JSON: " + exc.getMessage());
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
