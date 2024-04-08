package de.dennisguse.opentracks.ui.leaderboard.leaderboardFragment;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.dennisguse.opentracks.data.models.Ranking;
import de.dennisguse.opentracks.data.models.Speed;
import de.dennisguse.opentracks.ui.leaderboard.LeaderboardPagerAdapter;

public class AverageMovingSpeedLeaderboardFragment extends LeaderboardFragment {
    private boolean averageRefresh;

    @Override
    protected List<Ranking> calculateLatestAverageRankingsData(List<LeaderboardPagerAdapter.PlaceHolderTrackUser> latestLeaderboardData) {
        Map<String, SummedStatTrackUser> statsMap = new HashMap<>();
        for (LeaderboardPagerAdapter.PlaceHolderTrackUser trackUser : latestLeaderboardData) {
            if (!trackUser.socialAllow)
                continue;

            if (!statsMap.containsKey(trackUser.nickname)) {
                statsMap.put(trackUser.nickname, new SummedStatTrackUser(trackUser));
            }
            else {
                SummedStatTrackUser existingRecord = statsMap.get(trackUser.nickname);
                existingRecord.getPlaceHolderTrackUser().trackStatistics.setMaxSpeed(
                        new Speed(existingRecord.getPlaceHolderTrackUser().trackStatistics.getAverageMovingSpeed().speed_mps()
                                + trackUser.trackStatistics.getAverageMovingSpeed().speed_mps())
                );
                existingRecord.incrementSumFactorCount();
            }
        }
        List<SummedStatTrackUser> latestSummedLeaderboardData = new ArrayList<>(statsMap.values());
        latestSummedLeaderboardData.sort(new SortByAverageAverageMovingSpeed());

        List<Ranking> rankingsData = new ArrayList<>();
        int rank = 0;
        for (SummedStatTrackUser summedStatTrackUser : latestSummedLeaderboardData) {
            rankingsData.add(new Ranking(
                    ++rank,
                    summedStatTrackUser.getPlaceHolderTrackUser().nickname,
                    summedStatTrackUser.getPlaceHolderTrackUser().location,
                    getAverageMovingSpeedDisplay(getAverageSpeedFromSummedStatTrackUser(summedStatTrackUser))
            ));
        }
        return rankingsData;
    }

    @Override
    protected List<Ranking> calculateLatestBestRankingsData(List<LeaderboardPagerAdapter.PlaceHolderTrackUser> latestLeaderboardData) {
        Map<String, LeaderboardPagerAdapter.PlaceHolderTrackUser> statsMap = new HashMap<>();
        for (LeaderboardPagerAdapter.PlaceHolderTrackUser trackUser : latestLeaderboardData) {
            if (!trackUser.socialAllow)
                continue;

            if (!statsMap.containsKey(trackUser.nickname) ||
                    statsMap.get(trackUser.nickname).trackStatistics.getAverageMovingSpeed().lessThan(trackUser.trackStatistics.getAverageMovingSpeed())) {
                statsMap.put(trackUser.nickname, trackUser);
            }
        }
        latestLeaderboardData = new ArrayList<>(statsMap.values());
        latestLeaderboardData.sort(new SortByBestAverageMovingSpeed());

        List<Ranking> rankingsData = new ArrayList<>();
        int rank = 0;
        for (LeaderboardPagerAdapter.PlaceHolderTrackUser trackUser : latestLeaderboardData) {
            rankingsData.add(new Ranking(++rank, trackUser.nickname, trackUser.location, getAverageMovingSpeedDisplay(trackUser.trackStatistics.getAverageMovingSpeed())));
        }
        return rankingsData;
    }

    private String getAverageMovingSpeedDisplay(Speed averageMovingSpeed) {
        return getScoreDecimalFormat().format(averageMovingSpeed.speed_mps()) + " mps";
    }

    private Speed getAverageSpeedFromSummedStatTrackUser(SummedStatTrackUser summedStatTrackUser) {
        return new Speed(summedStatTrackUser.getPlaceHolderTrackUser().trackStatistics.getAverageMovingSpeed().speed_mps() / summedStatTrackUser.getSumFactorCount());
    }

    private class SortByAverageAverageMovingSpeed implements Comparator<SummedStatTrackUser> {
        @Override
        public int compare(SummedStatTrackUser user1, SummedStatTrackUser user2) {
            Speed user1AverageSpeed = getAverageSpeedFromSummedStatTrackUser(user1);
            Speed user2AverageSpeed = getAverageSpeedFromSummedStatTrackUser(user2);

            if (user1AverageSpeed.lessThan(user2AverageSpeed))
                return 1;
            else if (user1AverageSpeed.greaterThan(user2AverageSpeed))
                return -1;
            return 0;
        }
    }

    private class SortByBestAverageMovingSpeed implements Comparator<LeaderboardPagerAdapter.PlaceHolderTrackUser> {
        @Override
        public int compare(LeaderboardPagerAdapter.PlaceHolderTrackUser user1, LeaderboardPagerAdapter.PlaceHolderTrackUser user2) {
            Speed user1AverageSpeed = user1.trackStatistics.getAverageMovingSpeed();
            Speed user2AverageSpeed = user2.trackStatistics.getAverageMovingSpeed();

            if (user1AverageSpeed.lessThan(user2AverageSpeed))
                return 1;
            else if (user1AverageSpeed.greaterThan(user2AverageSpeed))
                return -1;
            return 0;
        }
    }
}