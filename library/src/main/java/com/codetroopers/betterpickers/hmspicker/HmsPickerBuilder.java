package com.codetroopers.betterpickers.hmspicker;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.util.Log;

import com.codetroopers.betterpickers.OnDialogDismissListener;

import java.util.Vector;

/**
 * User: derek Date: 5/2/13 Time: 7:55 PM
 */
public class HmsPickerBuilder {

    private FragmentManager manager; // Required
    private Integer styleResId; // Required
    private Fragment targetFragment;
    private int mReference;
    private Vector<HmsPickerDialogFragment.HmsPickerDialogHandlerV2> mHmsPickerDialogHandlerV2s = new Vector<HmsPickerDialogFragment.HmsPickerDialogHandlerV2>();
    private int mHours;
    private int mMinutes;
    private int mSeconds;
    private Integer plusMinusVisibility;
    private OnDialogDismissListener mOnDismissListener;

    /**
     * Set the visibility of the +/- button. This takes an int corresponding to Android's View.VISIBLE, View.INVISIBLE,
     * or View.GONE.  When using View.INVISIBLE, the +/- button will still be present in the layout but be
     * non-clickable. When set to View.GONE, the +/- button will disappear entirely, and the "0" button will occupy its
     * space.
     *
     * @param plusMinusVisibility an int corresponding to View.VISIBLE, View.INVISIBLE, or View.GONE
     * @return the current Builder object
     */
    public HmsPickerBuilder setPlusMinusVisibility(int plusMinusVisibility) {
        this.plusMinusVisibility = plusMinusVisibility;
        return this;
    }


    /**
     * Attach a FragmentManager. This is required for creation of the Fragment.
     *
     * @param manager the FragmentManager that handles the transaction
     * @return the current Builder object
     */
    public HmsPickerBuilder setFragmentManager(FragmentManager manager) {
        this.manager = manager;
        return this;
    }

    /**
     * Attach a style resource ID for theming. This is required for creation of the Fragment. Two stock styles are
     * provided using R.style.BetterPickersDialogFragment and R.style.BetterPickersDialogFragment.Light
     *
     * @param styleResId the style resource ID to use for theming
     * @return the current Builder object
     */
    public HmsPickerBuilder setStyleResId(int styleResId) {
        this.styleResId = styleResId;
        return this;
    }

    /**
     * Attach a target Fragment. This is optional and useful if creating a Picker within a Fragment.
     *
     * @param targetFragment the Fragment to attach to
     * @return the current Builder object
     */
    public HmsPickerBuilder setTargetFragment(Fragment targetFragment) {
        this.targetFragment = targetFragment;
        return this;
    }

    /**
     * Attach a reference to this Picker instance. This is used to track multiple pickers, if the user wishes.
     *
     * @param reference a user-defined int intended for Picker tracking
     * @return the current Builder object
     */
    public HmsPickerBuilder setReference(int reference) {
        this.mReference = reference;
        return this;
    }

    /**
     * Attach universal objects as additional handlers for notification when the Picker is set. For most use cases, this
     * method is not necessary as attachment to an Activity or Fragment is done automatically.  If, however, you would
     * like additional objects to subscribe to this Picker being set, attach Handlers here.
     *
     * @param handler an Object implementing the appropriate Picker Handler
     * @return the current Builder object
     */
    public HmsPickerBuilder addHmsPickerDialogHandler(HmsPickerDialogFragment.HmsPickerDialogHandlerV2 handler) {
        this.mHmsPickerDialogHandlerV2s.add(handler);
        return this;
    }

    /**
     * Remove objects previously added as handlers.
     *
     * @param handler the Object to remove
     * @return the current Builder object
     */
    public HmsPickerBuilder removeHmsPickerDialogHandler(HmsPickerDialogFragment.HmsPickerDialogHandlerV2 handler) {
        this.mHmsPickerDialogHandlerV2s.remove(handler);
        return this;
    }

    /**
     * Set some initial values for the picker
     *
     * @param hours   the initial hours value
     * @param minutes the initial minutes value
     * @param seconds the initial seconds value
     * @return the current Builder object
     */
    public HmsPickerBuilder setTime(int hours, int minutes, int seconds) {
        this.mHours = bounded(hours, 0, 99);
        this.mMinutes = bounded(minutes, 0, 99);
        this.mSeconds = bounded(seconds, 0, 99);
        return this;
    }

    /**
     * Set some initial values for the picker
     *
     * @param timeInSeconds the time in seconds
     * @return the current Builder object
     */
    public HmsPickerBuilder setTimeInSeconds(int timeInSeconds) {
        int hours = timeInSeconds / 3600;
        int remaining = timeInSeconds % 3600;
        int minutes = remaining / 60;
        int seconds = remaining % 60;

        return this.setTime(hours, minutes, seconds);
    }

    /**
     * Set some initial values for the picker
     *
     * @param timeInMilliseconds the time in milliseconds
     * @return the current Builder object
     */
    public HmsPickerBuilder setTimeInMilliseconds(long timeInMilliseconds) {
        return this.setTimeInSeconds((int) (timeInMilliseconds / 1000L));
    }

    /**
     * Instantiate and show the Picker
     */
    public void show() {
        if (manager == null || styleResId == null) {
            Log.e("HmsPickerBuilder", "setFragmentManager() and setStyleResId() must be called.");
            return;
        }
        FragmentTransaction ft = manager.beginTransaction();
        final Fragment prev = manager.findFragmentByTag("hms_dialog");
        if (prev != null) {
            ft.remove(prev).commit();
            ft = manager.beginTransaction();
        }
        ft.addToBackStack(null);

        final HmsPickerDialogFragment fragment = HmsPickerDialogFragment.newInstance(mReference, styleResId, plusMinusVisibility);
        if (targetFragment != null) {
            fragment.setTargetFragment(targetFragment, 0);
        }
        fragment.setHmsPickerDialogHandlersV2(mHmsPickerDialogHandlerV2s);

        if ((mHours | mMinutes | mSeconds) != 0) {
            fragment.setTime(mHours, mMinutes, mSeconds);
        }
        fragment.setOnDismissListener(mOnDismissListener);
        fragment.show(ft, "hms_dialog");
    }

    private static int bounded(int i, int min, int max) {
        return Math.min(Math.max(i, min), max);
    }

    public HmsPickerBuilder setOnDismissListener(OnDialogDismissListener onDismissListener) {
        this.mOnDismissListener = onDismissListener;
        return this;
    }
}
