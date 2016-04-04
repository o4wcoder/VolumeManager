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

import java.util.ArrayList;

/**
 * Location List's Adapter
 * <p>
 * Adapter for the List of Location Profiles
 * <p>
 * Created: 2/7/2016.
 *
 * @author Chris Hare
 */
public class LocationProfileListAdapter extends ArrayAdapter<Profile> implements Constants{

    private static final String TAG = LocationProfileListAdapter.class.getSimpleName();

    private Context mContext;
    ProfileMainFragment mCallingFragment;
    ValueAnimator mValueAnimator;

    private static final int NORMAL_PROFILE = 0;
    private static final int MOVING_PROFILE = 1;

    public LocationProfileListAdapter(Context context, ArrayList<Profile> profileList,
                                      ProfileMainFragment callingFragment) {
        super(context, 0, profileList);

        this.mContext = context;
        mCallingFragment = callingFragment;
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

        ViewHolder holder = null;
        int rowType = getItemViewType(position);

        //If we weren't given a view, inflate one
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if(rowType == NORMAL_PROFILE) {
                convertView = inflater.inflate(R.layout.location_profile_list_item,null);

            }
            else {
                //Set up disabled profile
                convertView = inflater.inflate(R.layout.location_profile_list_item_off,null);
            }

            holder.titleTextView = (TextView)convertView.findViewById(R.id.profileTitleTextView);
            holder.addressTextView = (TextView)convertView.findViewById(R.id.addressTextView);
            holder.cityTextView = (TextView)convertView.findViewById(R.id.cityTextView);
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
            //Log.e(TAG,"In getView(!null) with profile " +holder.titleTextView.getText() + " position: " + position);
        }

        //Set up click listner on volume image button to turn profile on/off
        holder.iconImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //get the position from the view's tag

                Integer listPosition = (Integer) v.getTag();

                //Toggle Profile's enabled state
                if (getItem(listPosition).isEnabled()) {

                    //Turn off geofence for this profile
                    getItem(listPosition).setEnabled(false);
                } else {
                    //Turn on geofence for this profile
                    getItem(listPosition).setEnabled(true);
                }

                //Save Profile change in DB
                ProfileManager.updateProfile(mContext, getItem(listPosition));
                //refresh listview
                notifyListViewChanged();

                //Callback to modify the geofence for this profile
                mCallingFragment.onToggleLocationIcon(getItem(listPosition).isEnabled(), getItem(listPosition).getId().toString());
            }

        });

        //Set view's data
        holder.iconImageView.setTag(new Integer(position));
        holder.iconImageView.setContentDescription(getContext().getString(R.string.cont_desc_toggle_icon));
        holder.titleTextView.setText(getItem(position).getTitle());
        holder.addressTextView.setText(getItem(position).getLocation().getAddress());
        holder.cityTextView.setText(getItem(position).getLocation().getCity());

        setProfileContentDescription(getItem(position),convertView);

        return convertView;
    }

    /*************************************************************************/
    /*                           Private Methods                             */
    /*************************************************************************/

    /**
     * Set the Content Descriptions on a view in the list
     * @param profile data of the list item
     * @param view view to set the content description
     */
    private void setProfileContentDescription(Profile profile, View view) {

        String msg = getContext().getString(R.string.cont_desc_location_profile) + " " + profile.getTitle()
                + " " + profile.getLocation().getAddress()
                + " " + profile.getLocation().getCity();

        //Get status of the Profile (enabled/disabled)
        String setting = (profile.isEnabled()) ? getContext().getString(R.string.cont_desc_enabled) :
                getContext().getString(R.string.cont_desc_disabled);

        msg += " " + getContext().getString(R.string.cont_desc_profile_status) + " " + setting;

        if(profile.isInAlarm())
            msg += " " + getContext().getString(R.string.cont_desc_profile_active);

        view.setContentDescription(msg);
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
        public TextView addressTextView;
        public TextView cityTextView;
        public TextView radiusTextView;
        public ImageView iconImageView;
    }

    /*************************************************************************/
    /*                           Interfaces                                  */
    /*************************************************************************/

    /**
     * Interface LocationAdapterCallback
     */
    public interface LocationAdapterCallback {

        /**
         * Notify the main fragment that the location icon of the profile has been toggles. Geofences
         * will be modified accordingly
         * @param enable statue of the toggle
         * @param requestId id of the profile that was modified.
         */
        void onToggleLocationIcon(boolean enable, String requestId);
    }


}
