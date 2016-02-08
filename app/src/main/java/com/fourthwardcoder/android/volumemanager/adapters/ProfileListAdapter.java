package com.fourthwardcoder.android.volumemanager.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.fourthwardcoder.android.volumemanager.R;
import com.fourthwardcoder.android.volumemanager.fragments.ProfileListFragment;
import com.fourthwardcoder.android.volumemanager.interfaces.Constants;
import com.fourthwardcoder.android.volumemanager.models.Profile;
import com.fourthwardcoder.android.volumemanager.services.VolumeManagerService;

import java.util.ArrayList;

/**
 * Created by Chris Hare on 2/7/2016.
 */
public class ProfileListAdapter extends ArrayAdapter<Profile> implements Constants {

    private Context mContext;


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


        //Log.e(TAG,"VP: " + listView.getFirstVisiblePosition() +": " + getItem(listView.getFirstVisiblePosition()).getHour() + " hp: " + headerPosition);


        //If we weren't given a view, inflate one
        if (convertView == null) {
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
               // Log.e(TAG, "Click image at position " + v.getTag().toString());

                //Toggle Porfile's enabled state
                if(getItem(listPosition).isEnabled()) {

                    //Turn off alarms for this profile
                    getItem(listPosition).setEnabled(false);
                    VolumeManagerService.setServiceAlarm(mContext, getItem(listPosition), false);
                }
                else {
                    //Turn on alarms for this profile
                    getItem(listPosition).setEnabled(true);
                    VolumeManagerService.setServiceAlarm(mContext.getApplicationContext(), getItem(listPosition), true);
                }
                //refresh listview
                //profileAdapter.notifyDataSetChanged();

                notifyListViewChanged();
            }

        });

        volumeImage.setTag(new Integer(position));

        holder.titleTextView = (TextView)convertView.findViewById(R.id.profileTitleTextView);
        holder.titleTextView.setText(getItem(position).getTitle());
        //holder.titleTextView.setAlpha(PRIMARY_TEXT_DARK);

        holder.timeTextView = (TextView)convertView.findViewById(R.id.timeTextView);
        holder.timeTextView.setText(getItem(position).getFullTimeForListItem());
        //holder.timeTextView.setAlpha(SECONDARY_TEXT_DARK);
        holder.daysTextView = (TextView)convertView.findViewById(R.id.profileDaysTextView);
        if(checkIfSetDaily(getItem(position).getDaysOfTheWeek()))
            holder.daysTextView.setText(R.string.daily);
        else
            holder.daysTextView.setText(getItem(position).getDaysOfWeekString());
        //holder.daysTextView.setAlpha(SECONDARY_TEXT_DARK);

        return convertView;
    }

    private boolean checkIfSetDaily(ArrayList<Boolean> daysOfTheWeek) {

        for(int i = 0; i< daysOfTheWeek.size(); i++) {
            if(daysOfTheWeek.get(i) == false)
                return false;
        }

        return true;
    }

    private void notifyListViewChanged() {
        notifyDataSetChanged();
        ((ProfileListFragment.Callback)mContext).onListViewChange();
    }
//    public interface Callback {
//
//        void onListViewChange();
//    }

    //Class to hold different views of the listview. This helps
    //it run smoothly when scrolling
    private static class ViewHolder {

        public TextView titleTextView;
        public TextView timeTextView;
        public TextView daysTextView;
    }

}
