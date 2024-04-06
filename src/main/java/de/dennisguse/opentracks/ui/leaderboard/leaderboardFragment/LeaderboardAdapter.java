package de.dennisguse.opentracks.ui.leaderboard.leaderboardFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import de.dennisguse.opentracks.R;
import de.dennisguse.opentracks.data.models.Ranking;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {
    private List<Ranking> displayedRankingList;
    private List<Ranking> averageRankingList;
    private List<Ranking> bestRankingList;

    public LeaderboardAdapter(List<Ranking> displayedRankingList) {
        this.displayedRankingList = displayedRankingList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_leaderboard_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ranking ranking = displayedRankingList.get(position);
        holder.usernameText.setText(ranking.getUsername());
        holder.locationText.setText(ranking.getLocation());
        holder.rankText.setText(String.valueOf(ranking.getRank()));
        holder.scoreText.setText(String.valueOf(ranking.getScore()));
    }

    @Override
    public int getItemCount() {
        return displayedRankingList.size();
    }

    public void setDisplayedRankingList(List<Ranking> displayedRankingList, LeaderboardFragment.AggregationStrategy aggregationStrategy) {
        this.displayedRankingList = displayedRankingList;
        if (aggregationStrategy == LeaderboardFragment.AggregationStrategy.Average)
            this.averageRankingList = displayedRankingList;
        else if (aggregationStrategy == LeaderboardFragment.AggregationStrategy.Best)
            this.bestRankingList = displayedRankingList;
        // Since the rankingList could have been remade from the ground up, we have to call notifyDataSetChanged();
        notifyDataSetChanged();
    }

    public void swapDisplayedRankingList(LeaderboardFragment.AggregationStrategy aggregationStrategy) {
        if (aggregationStrategy == LeaderboardFragment.AggregationStrategy.Average)
            this.displayedRankingList = this.averageRankingList;
        else if (aggregationStrategy == LeaderboardFragment.AggregationStrategy.Best)
            this.displayedRankingList = this.bestRankingList;
        // Since the rankingList could have been remade from the ground up, we have to call notifyDataSetChanged();
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView usernameText;
        TextView locationText;
        TextView rankText;
        TextView scoreText;

        ViewHolder(View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar);
            usernameText = itemView.findViewById(R.id.usernameText);
            locationText = itemView.findViewById(R.id.locationText);
            rankText = itemView.findViewById(R.id.rankText);
            scoreText = itemView.findViewById(R.id.scoreText);
        }
    }
}