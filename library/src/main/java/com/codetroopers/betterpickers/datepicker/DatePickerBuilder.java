package com.codetroopers.betterpickers.datepicker;

import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.codetroopers.betterpickers.OnDialogDismissListener;
import com.codetroopers.betterpickers.datepicker.DatePickerDialogFragment.DatePickerDialogHandler;

import java.util.Vector;

/**
 * User: derek Date: 5/2/13 Time: 7:55 PM
 */
public class DatePickerBuilder {

    private FragmentManager manager; // Required
    private Integer styleResId; // Required
    private Fragment targetFragment;
    private Integer monthOfYear;
    private Integer dayOfMonth;
    private Integer year;
    private Boolean yearOptional = false;
    private int mReference = -1;
    private Vector<DatePickerDialogHandler> mDatePickerDialogHandlers = new Vector<DatePickerDialogHandler>();
    private OnDialogDismissListener mOnDismissListener;

    /**
     * Attach a FragmentManager. This is required for creation of the Fragment.
     *
     * @param manager the FragmentManager that handles the transaction
     * @return the current Builder object
     */
    public DatePickerBuilder setFragmentManager(FragmentManager manager) {
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
    public DatePickerBuilder setStyleResId(int styleResId) {
        this.styleResId = styleResId;
        return this;
    }

    /**
     * Attach a target Fragment. This is optional and useful if creating a Picker within a Fragment.
     *
     * @param targetFragment the Fragment to attach to
     * @return the current Builder object
     */
    public DatePickerBuilder setTargetFragment(Fragment targetFragment) {
        this.targetFragment = targetFragment;
        return this;
    }

    /**
     * Attach a reference to this Picker instance. This is used to track multiple pickers, if the user wishes.
     *
     * @param reference a user-defined int intended for Picker tracking
     * @return the current Builder object
     */
    public DatePickerBuilder setReference(int reference) {
        this.mReference = reference;
        return this;
    }

    /**
     * Pre-set a zero-indexed month of year. This is highly frowned upon as it contributes to user confusion.  The
     * Pickers do a great job of making input quick and easy, and thus it is preferred to always start with a blank
     * slate.
     *
     * @param monthOfYear the zero-indexed month of year to pre-set
     * @return the current Builder object
     */
    public DatePickerBuilder setMonthOfYear(int monthOfYear) {
        this.monthOfYear = monthOfYear;
        return this;
    }

    /**
     * Pre-set a day of month. This is highly frowned upon as it contributes to user confusion.  The Pickers do a great
     * job of making input quick and easy, and thus it is preferred to always start with a blank slate.
     *
     * @param dayOfMonth the day of month to pre-set
     * @return the current Builder object
     */
    public DatePickerBuilder setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
        return this;
    }

    /**
     * Pre-set a year. This is highly frowned upon as it contributes to user confusion.  The Pickers do a great job of
     * making input quick and easy, and thus it is preferred to always start with a blank slate.
     *
     * @param year the year to pre-set
     * @return the current Builder object
     */
    public DatePickerBuilder setYear(int year) {
        this.year = year;
        return this;
    }

    /**
     * Set if year should be an optional field in the picker. False by default.
     *
     * @param yearOptional whether the year is optional
     * @return the current Builder object
     */
    public DatePickerBuilder setYearOptional(boolean yearOptional) {
        this.yearOptional = yearOptional;
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
    public DatePickerBuilder addDatePickerDialogHandler(DatePickerDialogHandler handler) {
        this.mDatePickerDialogHandlers.add(handler);
        return this;
    }

    /**
     * Remove objects previously added as handlers.
     *
     * @param handler the Object to remove
     * @return the current Builder object
     */
    public DatePickerBuilder removeDatePickerDialogHandler(DatePickerDialogHandler handler) {
        this.mDatePickerDialogHandlers.remove(handler);
        return this;
    }


    public DatePickerBuilder setOnDismissListener(OnDialogDismissListener onDismissListener) {
        this.mOnDismissListener = onDismissListener;
        return this;
    }

    /**
     * Instantiate and show the Picker
     */
    public void show() {
        if (manager == null || styleResId == null) {
            Log.e("DatePickerBuilder", "setFragmentManager() and setStyleResId() must be called.");
            return;
        }
        FragmentTransaction ft = manager.beginTransaction();
        final Fragment prev = manager.findFragmentByTag("date_dialog");
        if (prev != null) {
            ft.remove(prev).commit();
            ft = manager.beginTransaction();
        }
        ft.addToBackStack(null);

        final DatePickerDialogFragment fragment = DatePickerDialogFragment
                .newInstance(mReference, styleResId, monthOfYear, dayOfMonth, year, yearOptional);
        if (targetFragment != null) {
            fragment.setTargetFragment(targetFragment, 0);
        }
        fragment.setDatePickerDialogHandlers(mDatePickerDialogHandlers);
        fragment.setOnDismissListener(mOnDismissListener);
        fragment.show(ft, "date_dialog");
    }
}
