package com.fourthwardcoder.android.volumemanager.helpers;

import android.annotation.TargetApi;
import android.os.Build;
import android.transition.Transition;

/**
 * Created by Chris Hare on 3/10/2016.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class ImageTransitionListener implements Transition.TransitionListener {
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
