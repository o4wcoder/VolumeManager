package com.fourthwardcoder.android.volumemanager.helpers;

import android.annotation.TargetApi;
import android.os.Build;
import android.transition.Transition;

/**
 * Profile Title Transition Listener
 * <p>
 * Stub class used to detect shared transitions states in the fragments. These functions are
 * overriden where they are needed. In this case this is used for for the Shared Element
 * transition of the Profile Title.
 * <p>
 * Created 3/10/2016
 *
 * @author Chris Hare
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class ProfileTitleTransitionListener implements Transition.TransitionListener {
    @Override
    public void onTransitionStart(Transition transition) {
    }

    @Override
    public void onTransitionEnd(Transition transition) {
    }

    @Override
    public void onTransitionCancel(Transition transition) {
    }

    @Override
    public void onTransitionPause(Transition transition) {
    }

    @Override
    public void onTransitionResume(Transition transition) {
    }
}
