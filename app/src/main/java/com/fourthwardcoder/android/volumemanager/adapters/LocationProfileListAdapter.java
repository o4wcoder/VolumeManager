package com.fourthwardcoder.android.volumemanager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fourthwardcoder.android.volumemanager.R;
import com.fourthwardcoder.android.volumemanager.data.ProfileManager;
import com.fourthwardcoder.android.volumemanager.fragments.ProfileMainFragment;
import com.fourthwardcoder.android.volumemanager.interfaces.Constants;
import com.fourthwardcoder.android.volumemanager.models.Profile;

import java.util.ArrayList;

/**
 * Class LocationProfileListAdapter
 * Author: Chris Hare
 * Created: 2/7/2016.
 */
public class LocationProfileListAdapter extends ArrayAdapter<Profile> implements Constants{

    private static final String TAG = LocationProfileListAdapter.class.getSimpleName();

    private Context mContext;
    ProfileMainFragment mCallingFragment;

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
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder)convertView.getTag();
            //Log.e(TAG,"In getView(!null) with profile " +holder.titleTextView.getText() + " position: " + position);
        }

        //Set up click listner on volume image button to turn profile on/off
        ImageView volumeImage = (ImageView)convertView.findViewById(R.id.volumeStartImageView);

        volumeImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //get the position from the view's tag

                Integer listPosition = (Integer)v.getTag();

                //Toggle Porfile's enabled state
                if(getItem(listPosition).isEnabled()) {

                    //Turn off geofence for this profile
                    getItem(listPosition).setEnabled(false);
                }
                else {
                    //Turn on geofence for this profile
                    getItem(listPosition).setEnabled(true);
                }

                //Save Profile change in DB
                ProfileManager.updateProfile(mContext, getItem(listPosition));
                //refresh listview
                notifyListViewChanged();

                //Callback to modify the geofence for this profile
                mCallingFragment.onToggleLocationIcon();

            }

        });

        volumeImage.setTag(new Integer(position));
        //Set title
        holder.titleTextView = (TextView)convertView.findViewById(R.id.profileTitleTextView);
        holder.titleTextView.setText(getItem(position).getTitle());
        //Set address
        holder.addressTextView = (TextView)convertView.findViewById(R.id.addressTextView);
        holder.addressTextView.setText(getItem(position).getLocation().getAddress());
        //Set city
        holder.cityTextView = (TextView)convertView.findViewById(R.id.cityTextView);
        holder.cityTextView.setText(getItem(position).getLocation().getCity());



        return convertView;
    }

    private void notifyListViewChanged() {
        notifyDataSetChanged();
        ((ProfileMainFragment.Callback)mContext).onListViewChange();
    }


    private static class ViewHolder {

        public TextView titleTextView;
        public TextView addressTextView;
        public TextView cityTextView;
        public TextView radiusTextView;
    }

    public interface LocationAdapterCallback {

        void onToggleLocationIcon();
    }


}
