/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.codetroopers.betterpickers.calendardatepicker;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.StateListDrawable;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.codetroopers.betterpickers.R;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment.OnDateChangedListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays a selectable list of years.
 */
public class YearPickerView extends ListView implements OnItemClickListener, OnDateChangedListener {

    private static final String TAG = "YearPickerView";

    private final CalendarDatePickerController mController;
    private YearAdapter mAdapter;
    private int mViewSize;
    private int mChildSize;
    private TextViewWithCircularIndicator mSelectedView;

    private int mTextColor;
    private int mCircleColor;
    private int mBackgroundColor;

    public void setTheme(TypedArray themeColors) {
        mCircleColor = themeColors.getColor(R.styleable.BetterPickersDialogs_bpRadialPointerColor, ContextCompat.getColor(getContext(), R.color.bpBlue));
        mTextColor = themeColors.getColor(R.styleable.BetterPickersDialogs_bpBodyUnselectedTextColor, ContextCompat.getColor(getContext(), R.color.ampm_text_color));
    }

    /**
     * @param context
     */
    public YearPickerView(Context context, CalendarDatePickerController controller) {
        super(context);
        mController = controller;
        mController.registerOnDateChangedListener(this);
        ViewGroup.LayoutParams frame = new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        setLayoutParams(frame);
        Resources res = context.getResources();
        mViewSize = res.getDimensionPixelOffset(R.dimen.date_picker_view_animator_height);
        mChildSize = res.getDimensionPixelOffset(R.dimen.year_label_height);
        setVerticalFadingEdgeEnabled(true);
        setFadingEdgeLength(mChildSize / 3);
        init(context);
        setOnItemClickListener(this);
        setSelector(new StateListDrawable());
        setDividerHeight(0);
        onDateChanged();

        mBackgroundColor = R.color.circle_background;
        mCircleColor = R.color.bpBlue;
        mTextColor = R.color.ampm_text_color;
    }

    private void init(Context context) {
        ArrayList<String> years = new ArrayList<String>();
        for (int year = mController.getMinDate().year; year <= mController.getMaxDate().year; year++) {
            years.add(String.format("%d", year));
        }
        mAdapter = new YearAdapter(context, R.layout.calendar_year_label_text_view, years);
        setAdapter(mAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mController.tryVibrate();
        TextViewWithCircularIndicator clickedView = (TextViewWithCircularIndicator) view;
        if (clickedView != null) {
            if (clickedView != mSelectedView) {
                if (mSelectedView != null) {
                    mSelectedView.drawIndicator(false);
                    mSelectedView.requestLayout();
                }
                clickedView.drawIndicator(true);
                clickedView.requestLayout();
                mSelectedView = clickedView;
            }
            mController.onYearSelected(getYearFromTextView(clickedView));
            mAdapter.notifyDataSetChanged();
        }
    }

    private int getYearFromTextView(TextView view) {
        return Integer.valueOf(view.getText().toString());
    }

    private class YearAdapter extends ArrayAdapter<String> {

        public YearAdapter(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            TextViewWithCircularIndicator view = (TextViewWithCircularIndicator)
                    super.getView(position, convertView, parent);
            view.requestLayout();
            int year = getYearFromTextView(view);

//            view.setBackgroundColor(mBackgroundColor);
            view.setCircleColor(mCircleColor);
            view.setTextColor(mTextColor);
            boolean selected = mController.getSelectedDay().year == year;
            view.drawIndicator(selected);
            if (selected) {
                mSelectedView = view;
            }
            return view;
        }
    }

    public void postSetSelectionCentered(final int position) {
        postSetSelectionFromTop(position, mViewSize / 2 - mChildSize / 2);
    }

    public void postSetSelectionFromTop(final int position, final int offset) {
        post(new Runnable() {

            @Override
            public void run() {
                setSelectionFromTop(position, offset);
                requestLayout();
            }
        });
    }

    public int getFirstPositionOffset() {
        final View firstChild = getChildAt(0);
        if (firstChild == null) {
            return 0;
        }
        return firstChild.getTop();
    }

    @Override
    public void onDateChanged() {
        mAdapter.notifyDataSetChanged();
        postSetSelectionCentered(mController.getSelectedDay().year - mController.getMinDate().year);
    }

    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
            event.setFromIndex(0);
            event.setToIndex(0);
        }
    }
}
