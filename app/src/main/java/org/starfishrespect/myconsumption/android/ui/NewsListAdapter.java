package org.starfishrespect.myconsumption.android.ui;

import android.view.LayoutInflater;

import org.starfishrespect.myconsumption.android.core.News;

import java.util.List;

public class NewsListAdapter extends AlternatingColorListAdapter<News> {
    /**
     * @param inflater
     * @param items
     * @param selectable
     */
    public NewsListAdapter(final LayoutInflater inflater, final List<News> items,
                           final boolean selectable) {
        super(org.starfishrespect.myconsumption.android.R.layout.news_list_item, inflater, items, selectable);
    }

    /**
     * @param inflater
     * @param items
     */
    public NewsListAdapter(final LayoutInflater inflater, final List<News> items) {
        super(org.starfishrespect.myconsumption.android.R.layout.news_list_item, inflater, items);
    }

    @Override
    protected int[] getChildViewIds() {
        return new int[]{org.starfishrespect.myconsumption.android.R.id.tv_title, org.starfishrespect.myconsumption.android.R.id.tv_summary,
                org.starfishrespect.myconsumption.android.R.id.tv_date};
    }

    @Override
    protected void update(final int position, final News item) {
        super.update(position, item);

        setText(0, item.getTitle());
        setText(1, item.getContent());
        //setNumber(R.id.tv_date, item.getCreatedAt());
    }
}
