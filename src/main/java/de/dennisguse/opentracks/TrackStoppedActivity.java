package de.dennisguse.opentracks;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ArrayAdapter;

import com.google.gson.JsonObject;

import java.util.UUID;

import java.util.UUID;

import de.dennisguse.opentracks.data.ContentProviderUtils;
import de.dennisguse.opentracks.data.interfaces.JSONSerializable;
import de.dennisguse.opentracks.data.models.ActivityType;
import de.dennisguse.opentracks.data.interfaces.ActionCallback;
import de.dennisguse.opentracks.data.interfaces.ReadCallback;
import de.dennisguse.opentracks.data.models.CRUDConstants;
import de.dennisguse.opentracks.data.models.DistanceFormatter;
import de.dennisguse.opentracks.data.models.SpeedFormatter;
import de.dennisguse.opentracks.data.models.Track;
import de.dennisguse.opentracks.databinding.TrackStoppedBinding;
import de.dennisguse.opentracks.fragments.ChooseActivityTypeDialogFragment;
import de.dennisguse.opentracks.services.TrackRecordingServiceConnection;
import de.dennisguse.opentracks.settings.PreferencesUtils;
import de.dennisguse.opentracks.ui.aggregatedStatistics.ConfirmDeleteDialogFragment;
import de.dennisguse.opentracks.util.ExportUtils;
import de.dennisguse.opentracks.util.IntentUtils;
import de.dennisguse.opentracks.util.StringUtils;
import de.dennisguse.opentracks.util.TrackUtils;
import de.dennisguse.opentracks.data.FirestoreCRUDUtil;

public class TrackStoppedActivity extends AbstractTrackDeleteActivity implements ChooseActivityTypeDialogFragment.ChooseActivityTypeCaller {

    private static final String TAG = TrackStoppedActivity.class.getSimpleName();

    public static final String EXTRA_TRACK_ID = "track_id";

    private TrackStoppedBinding viewBinding;

    private Track.Id trackId;

    private boolean isDiscarding = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        trackId = getIntent().getParcelableExtra(EXTRA_TRACK_ID);
        if (trackId == null) {
            Log.e(TAG, "TrackStoppedActivity needs EXTRA_TRACK_ID.");
            finish();
        }

        ContentProviderUtils contentProviderUtils = new ContentProviderUtils(this);
        Track track = contentProviderUtils.getTrack(trackId);

        //Temporary code to illustrate JSON serialization and loading of Track model
        //TODO! - Remove
        //Assignee - Jean Robatto

//        final String JSON_SERIALIZER_LOG_TAG = "JSONSerializerTest";
//
//        final String trackJSONString = track.toJSON();
//        Log.i(JSON_SERIALIZER_LOG_TAG, trackJSONString);
//
//        final Track trackCopy = JSONSerializable.fromJSON(trackJSONString, Track.class);
//        Log.i(JSON_SERIALIZER_LOG_TAG, trackCopy.toString());
//        Log.i(JSON_SERIALIZER_LOG_TAG, trackCopy.getName());

        //End

        viewBinding.trackEditName.setText(track.getName());

        viewBinding.trackEditActivityType.setText(track.getActivityTypeLocalized());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, ActivityType.getLocalizedStrings(this));
        viewBinding.trackEditActivityType.setAdapter(adapter);
        viewBinding.trackEditActivityType.setOnItemClickListener((parent, view, position, id) -> {
            String localizedActivityType = (String) viewBinding.trackEditActivityType.getAdapter().getItem(position);
            setActivityTypeIcon(ActivityType.findByLocalizedString(this, localizedActivityType));
        });
        viewBinding.trackEditActivityType.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String localizedActivityType = viewBinding.trackEditActivityType.getText().toString();
                setActivityTypeIcon(ActivityType.findByLocalizedString(this, localizedActivityType));
            }
        });

        setActivityTypeIcon(track.getActivityType());
        viewBinding.trackEditActivityTypeIcon.setOnClickListener(v -> ChooseActivityTypeDialogFragment.showDialog(getSupportFragmentManager(), this, viewBinding.trackEditActivityType.getText().toString()));

        viewBinding.trackEditDescription.setText(track.getDescription());

        viewBinding.time.setText(StringUtils.formatElapsedTime(track.getTrackStatistics().getMovingTime()));

        {
            Pair<String, String> parts = SpeedFormatter.Builder()
                    .setUnit(PreferencesUtils.getUnitSystem())
                    .setReportSpeedOrPace(PreferencesUtils.isReportSpeed(track))
                    .build(this)
                    .getSpeedParts(track.getTrackStatistics().getAverageMovingSpeed());
            viewBinding.speed.setText(parts.first);
            viewBinding.speedUnit.setText(parts.second);
        }

        {
            Pair<String, String> parts = DistanceFormatter.Builder()
                    .setUnit(PreferencesUtils.getUnitSystem())
                    .build(this)
                    .getDistanceParts(track.getTrackStatistics().getTotalDistance());
            viewBinding.distance.setText(parts.first);
            viewBinding.distanceUnit.setText(parts.second);
        }

        viewBinding.finishButton.setOnClickListener(v -> {
            storeTrackMetaData(contentProviderUtils, track);
            ExportUtils.postWorkoutExport(this, trackId);
            finish();
        });

        viewBinding.resumeButton.setOnClickListener(v -> {
            storeTrackMetaData(contentProviderUtils, track);
            resumeTrackAndFinish();
        });

        viewBinding.discardButton.setOnClickListener(v -> ConfirmDeleteDialogFragment.showDialog(getSupportFragmentManager(), trackId));
    }

    private void storeTrackMetaData(ContentProviderUtils contentProviderUtils, Track track) {

        //Save run on the external DB
        //NOTE - The UI should be augmented to display success/failure to the user
        //TODO: The next line should not be commented out
        //FirestoreCRUDUtil.getInstance().createEntry(CRUDConstants.RUNS_TABLE, track.getUuid().toString(), track.toJSON(), null);

        //Save run on the external DB
        //NOTE - The UI should be augmented to display success/failure to the user
        //TODO: The next line should not be commented out
        //FirestoreCRUDUtil.getInstance().createEntry(CRUDConstants.RUNS_TABLE, track.getUuid().toString(), track.toJSON(), null);

        TrackUtils.updateTrack(TrackStoppedActivity.this, track, viewBinding.trackEditName.getText().toString(),
                viewBinding.trackEditActivityType.getText().toString(), viewBinding.trackEditDescription.getText().toString(),
                contentProviderUtils);
    }

    @Override
    public void onBackPressed() {
        if (isDiscarding) {
            return;
        }
        super.onBackPressed();
        resumeTrackAndFinish();
    }

    @Override
    protected View getRootView() {
        viewBinding = TrackStoppedBinding.inflate(getLayoutInflater());
        return viewBinding.getRoot();
    }

    private void setActivityTypeIcon(ActivityType activityType) {
        viewBinding.trackEditActivityTypeIcon.setImageResource(activityType.getIconDrawableId());
    }

    @Override
    public void onChooseActivityTypeDone(ActivityType activityType) {
        setActivityTypeIcon(activityType);
        viewBinding.trackEditActivityType.setText(getString(activityType.getLocalizedStringId()));
    }

    private void resumeTrackAndFinish() {
        TrackRecordingServiceConnection.execute(this, (service, connection) -> {
            service.resumeTrack(trackId);

            Intent newIntent = IntentUtils.newIntent(TrackStoppedActivity.this, TrackRecordingActivity.class)
                    .putExtra(TrackRecordingActivity.EXTRA_TRACK_ID, trackId);
            startActivity(newIntent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            finish();
        });
    }

    @Override
    protected void onDeleteConfirmed() {
        isDiscarding = true;
        viewBinding.loadingLayout.loadingText.setText(getString(R.string.track_discarding));
        viewBinding.contentLinearLayout.setVisibility(View.GONE);
        viewBinding.loadingLayout.loadingIndeterminate.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDeleteFinished() {
        finish();
    }

    @Override
    protected Track.Id getRecordingTrackId() {
        return null;
    }
}
