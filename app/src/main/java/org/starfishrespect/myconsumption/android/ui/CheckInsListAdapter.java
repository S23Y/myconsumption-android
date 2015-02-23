package org.starfishrespect.myconsumption.android.ui;

import android.view.LayoutInflater;

import org.starfishrespect.myconsumption.android.core.CheckIn;

import java.util.List;


public class CheckInsListAdapter extends AlternatingColorListAdapter<CheckIn> {
    /**
     * @param inflater
     * @param items
     * @param selectable
     */
    public CheckInsListAdapter(final LayoutInflater inflater, final List<CheckIn> items,
                               final boolean selectable) {
        super(org.starfishrespect.myconsumption.android.R.layout.checkin_list_item, inflater, items, selectable);
    }

    /**
     * @param inflater
     * @param items
     */
    public CheckInsListAdapter(final LayoutInflater inflater, final List<CheckIn> items) {
        super(org.starfishrespect.myconsumption.android.R.layout.checkin_list_item, inflater, items);
    }

    @Override
    protected int[] getChildViewIds() {
        return new int[]{org.starfishrespect.myconsumption.android.R.id.tv_name, org.starfishrespect.myconsumption.android.R.id.tv_date};
    }

    @Override
    protected void update(final int position, final CheckIn item) {
        super.update(position, item);

        setText(0, item.getName());
    }
}
