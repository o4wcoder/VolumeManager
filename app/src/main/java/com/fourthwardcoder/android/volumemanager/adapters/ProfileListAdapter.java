package com.fourthwardcoder.android.volumemanager.adapters;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fourthwardcoder.android.volumemanager.R;
import com.fourthwardcoder.android.volumemanager.data.ProfileManager;
import com.fourthwardcoder.android.volumemanager.fragments.ProfileMainFragment;
import com.fourthwardcoder.android.volumemanager.helpers.Util;
import com.fourthwardcoder.android.volumemanager.interfaces.Constants;
import com.fourthwardcoder.android.volumemanager.models.Profile;
import com.fourthwardcoder.android.volumemanager.services.VolumeManagerService;

import java.util.ArrayList;

/**
 * Time Profile's List Adapter
 * <p>
 * Adapter for the List of Time Profiles
 * <p>
 * Created: 2/7/2016.
 *
 * @author Chris Hare
 */
public class ProfileListAdapter extends ArrayAdapter<Profile> implements Constants {

    private final static String TAG = ProfileListAdapter.class.getSimpleName();

    private Context mContext;
    ValueAnimator mValueAnimator;

    public ProfileListAdapter(Context context,ArrayList<Profile> profiles) {
        super(context, 0, profiles);

        mContext = context;
    }

    //Override method needed from multiple layouts in listview
    //Determines the type of layout to display in the row
    @Override
    public int getItemViewType(int position) {

        if(getItem(position).isEnabled() == true)
            return NORMAL_PROFILE;
        else
            return DISABLED_PROFILE;

    }
    //Override method needed from multiple layouts in listview
    //Returns how many different layouts the listview can have
    @Override
    public int getViewTypeCount() {
        return 2;
    }
    //Override getView to return a view inflated from the custom
    //layout and inflated with Profile Data


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        int rowType = getItemViewType(position);


        //If we weren't given a view, inflate one
        if (convertView == null) {

            //Create ViewHolder and populate fields
            holder = new ViewHolder();

            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if(rowType == NORMAL_PROFILE) {
                convertView = inflater.inflate(R.layout.profile_list_item,null);

            }
            else {
                //Set up disabled profile
                convertView = inflater.inflate(R.layout.profile_list_item_off,null);
            }

            //Get views of list item
            holder.titleTextView = (TextView)convertView.findViewById(R.id.profileTitleTextView);
            holder.timeTextView = (TextView)convertView.findViewById(R.id.timeTextView);
            holder.daysTextView = (TextView)convertView.findViewById(R.id.profileDaysTextView);
            holder.iconImageView = (ImageView)convertView.findViewById(R.id.volumeStartImageView);
            //Change color of icon if we are currently in an active volume control
            if(getItem(position).isEnabled()) {
                mValueAnimator = Util.getIconAnimator(getContext(),holder.iconImageView);
                Util.setListIconColor(getContext(),mValueAnimator, holder.iconImageView, getItem(position).isInAlarm());
            }
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder)convertView.getTag();

        }

        holder.iconImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //get the position from the view's tag

                Integer listPosition = (Integer) v.getTag();
                // Log.e(TAG, "Click image at position " + v.getTag().toString());

                //Toggle Porfile's enabled state
                if (getItem(listPosition).isEnabled()) {

                    //Turn off alarms for this profile
                    getItem(listPosition).setEnabled(false);
                    VolumeManagerService.setServiceAlarm(mContext, getItem(listPosition), false);
                } else {
                    //Turn on alarms for this profile
                    getItem(listPosition).setEnabled(true);
                    VolumeManagerService.setServiceAlarm(mContext, getItem(listPosition), true);
                }
                //Save Profile change in DB
                ProfileManager.updateProfile(mContext, getItem(listPosition));
                notifyListViewChanged();
            }

        });

        //Set view's data
        holder.iconImageView.setTag(new Integer(position));
        holder.iconImageView.setContentDescription(getContext().getString(R.string.cont_desc_toggle_icon));
        holder.titleTextView.setText(getItem(position).getTitle());
        holder.timeTextView.setText(Util.getFullTimeForListItem(mContext,getItem(position)));

        //Display string "Daily" if alarm set for every day, otherwise display days set.
        if(checkIfSetDaily(getItem(position).getDaysOfTheWeek()))
            holder.daysTextView.setText(R.string.daily);
        else
            holder.daysTextView.setText(getItem(position).getDaysOfWeekString());
        //holder.daysTextView.setAlpha(SECONDARY_TEXT_DARK);

        setProfileContentDescription(getItem(position),convertView);
        return convertView;
    }

    /******************************************************************************/
    /*                              Private Methods                               */
    /******************************************************************************/

    /**
     * Set the Content Descriptions on a view in the list
     * @param profile data of the list item
     * @param view view to set the content description
     */
    private void setProfileContentDescription(Profile profile, View view) {

        String msg = getContext().getString(R.string.cont_desc_time_profile) + " " + profile.getTitle()
                + " " + getContext().getString(R.string.cont_desc_starts)
                + " " + Util.formatTime(getContext(), profile.getStartDate())
                + " " + getContext().getString(R.string.cont_desc_ends)
                + " " + Util.formatTime(getContext(), profile.getEndDate());

        String daysMsg = getContext().getString(R.string.cont_desc_set) + " ";

        if (checkIfSetDaily(profile.getDaysOfTheWeek()))
            daysMsg += getContext().getString(R.string.daily);
        else
            daysMsg += getDaysOfTheWeekContentDescription(profile);

        msg += " " + daysMsg + " ";

        //Get status of the Profile (enabled/disabled)
        String setting = (profile.isEnabled()) ? getContext().getString(R.string.cont_desc_enabled) :
                getContext().getString(R.string.cont_desc_disabled);

        msg += getContext().getString(R.string.cont_desc_profile_status) + " " + setting;

        if(profile.isInAlarm())
            msg += " " + getContext().getString(R.string.cont_desc_profile_active);

        view.setContentDescription(msg);
    }

    /**
     * Get the Content Description for the days of the week the profile is set
     * @param profile data of the list item
     * @return content description of the days of the week
     */
    private String getDaysOfTheWeekContentDescription(Profile profile) {

        String[] daysList = getContext().getResources().getStringArray(R.array.days_of_the_week);

        String daysStr = "";

        for(int i = 0; i < daysList.length; i++) {

            if(profile.getDaysOfTheWeek().get(i)) {
                daysStr += daysList[i];
            }
        }

        return daysStr;
    }

    /**
     * Check if the profile is set every day of the week
     * @param daysOfTheWeek list of days the profile is set
     * @return if set daily or not
     */
    private boolean checkIfSetDaily(ArrayList<Boolean> daysOfTheWeek) {

        for(int i = 0; i< DAYS_OF_THE_WEEK; i++) {
            if(daysOfTheWeek.get(i) == false) {
                return false;
            }
        }

        return true;
    }

    /**
     * Notify main fragment that the listview has changed.
     */
    private void notifyListViewChanged() {
        notifyDataSetChanged();
        ((ProfileMainFragment.Callback)mContext).onListViewChange();
    }

    /*************************************************************************/
    /*                           Inner Classes                               */
    /*************************************************************************/
    /**
     * ListView adapter ViewHolder class
     */
    private static class ViewHolder {

        public TextView titleTextView;
        public TextView timeTextView;
        public TextView daysTextView;
        public ImageView iconImageView;


    }

}
