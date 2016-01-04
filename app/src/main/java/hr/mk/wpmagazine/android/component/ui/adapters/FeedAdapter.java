
/*
 *
 *  *
 *  *  * ****************************************************************************
 *  *  * Copyright (c) 2015. Muriel Kamgang Mabou
 *  *  * All rights reserved.
 *  *  *
 *  *  * This file is part of project AndroidWPTemplate.
 *  *  * It can not be copied and/or distributed without the
 *  *  * express permission of Muriel Kamgang Mabou
 *  *  * ****************************************************************************
 *  *
 *  *
 *
 */

package hr.mk.wpmagazine.android.component.ui.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import hr.mk.wpmagazine.Utils.TagFactoryUtils;
import hr.mk.wpmagazine.Utils.TypeFaceUtils;
import hr.mk.wpmagazine.android.component.R;
import hr.mk.wpmagazine.helper.ShowDateHelper;
import hr.mk.wpmagazine.model.WP.WPPost;
import io.realm.RealmResults;

/**
 * Created by Mur0 on 3/21/2015.
 */
public class FeedAdapter extends WPAdapter {

    private static final int MAX_FLUSH_SIZE = 30;
    private static final String TAG = TagFactoryUtils.getTag(FeedAdapter.class);
    private static final int VIEW_TYPE_PADDING = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    private final Context context;
    private final View padding;
    private final ShowDateHelper showDateHelper;
    private final List<String> calledUrl = new ArrayList<>(MAX_FLUSH_SIZE); //
    private RealmResults<WPPost> realmResults;
    private WPAdapterOnClickListener listener;

    public FeedAdapter(Context context, View padding, RealmResults<WPPost> realmResults) {
        this.context = context;
        this.padding = padding;
        this.realmResults = realmResults;
        showDateHelper = new ShowDateHelper(context);
    }

    public RealmResults<WPPost> getRealmResults() {
        return realmResults;
    }

    @Override
    public void setListener(WPAdapterOnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void flush() {
        if (calledUrl.isEmpty())
            return;

        try {

            Log.d(TAG, String.format("Total url to invalidate: %d", calledUrl.size()));

            for (final String url : calledUrl) {
                Log.d(TAG, String.format("invalidate cached image: %s ", url));
                Picasso.with(context).invalidate(url);
            }

        } catch (Exception e) {
            //
        }
    }

    @Override
    public void removeListener() {
        this.listener = null;
    }


    @Override
    public void updateRealmResult(RealmResults<WPPost> realmResults) {
        this.realmResults = realmResults;
        // notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_PADDING)
            return new HeaderViewHolder(padding);
        else
            return new Holder(LayoutInflater.from(context).inflate(R.layout.row_feed, parent, false));
    }

    @Override
    public int getItemViewType(int position) {

        return (position == 0 && padding != null) ? VIEW_TYPE_PADDING : VIEW_TYPE_ITEM;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof Holder) {

            final int finalPosition = padding == null ? position : position - 1;
            final Holder myHolder = (Holder) holder;
            final WPPost wpPost = realmResults.get(finalPosition);

            myHolder.title.setText(Html.fromHtml(wpPost.getTitle() == null ? "" : wpPost.getTitle()));
            myHolder.date.setText(showDateHelper.getFormattedDate(wpPost.getDate()));
            myHolder.fav.setImageDrawable(wpPost.isFavorite() ? context.getResources().getDrawable(R.drawable.ic_fav_main_true)
                    : context.getResources().getDrawable(R.drawable.ic_fav_main_false));

            final String url = wpPost.getFeatured_image() == null || wpPost.getFeatured_image().getSource() == null ? ""
                    : wpPost.getFeatured_image().getSource();

            if (!url.isEmpty()) {

                Picasso.with(context)
                        .load(url)
                        .fit()
                        .centerCrop()
                        .config(Bitmap.Config.RGB_565)
                        .error(R.drawable.img_holder)
                        .into(myHolder.image);

                  /*temporary disabling this */
//                if (!calledUrl.contains(url)) {
//                    if (calledUrl.size() == MAX_FLUSH_SIZE)
//                        calledUrl.remove(0);
//                    calledUrl.add(url);
//                }

            } else
                myHolder.image.setImageDrawable(context.getResources().getDrawable(R.drawable.img_holder));

        }
    }

    @Override
    public int getItemCount() {
        final int size = realmResults == null ? 0 : realmResults.size();
        return padding == null ? size : size + 1;
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderViewHolder(View view) {
            super(view);
        }
    }

    class Holder extends RecyclerView.ViewHolder {

        @InjectView(R.id.title)
        TextView title;
        @InjectView(R.id.date)
        TextView date;
        @InjectView(R.id.image)
        ImageView image;
        @InjectView(R.id.imageViewFav)
        ImageView fav;
        @InjectView(R.id.imageViewShare)
        ImageView share;
        @InjectView(R.id.textViewShare)
        TextView textShare;
        @InjectView(R.id.textViewFav)
        TextView textFav;

        @InjectView(R.id.layoutFavorite)
        LinearLayout layoutFav;
        @InjectView(R.id.layoutShare)
        LinearLayout layoutShare;
        @InjectView(R.id.clickView)
        View clickView;


        public Holder(View itemView) {
            super(itemView);

            ButterKnife.inject(this, itemView);

            final Typeface typeface = TypeFaceUtils.getTypeFace(context, "OpenSans-Regular.ttf");
            if (typeface != null) {
                title.setTypeface(typeface);
                date.setTypeface(typeface);
                textFav.setTypeface(typeface);
                textShare.setTypeface(typeface);
            }
        }

        @OnClick(R.id.clickView)
        public void onClickClickView(View v) {
            handleOnClick(v);
        }

        @OnClick(R.id.layoutFavorite)
        public void onClickLayouFav(View v) {
            handleOnClick(v);
        }

        @OnClick(R.id.layoutShare)
        public void onClickLayoutShare(View v) {
            handleOnClick(v);
        }

        private void handleOnClick(View v) {
            if (listener != null)
                listener.onClick(v, padding == null ? getAdapterPosition() : getAdapterPosition() - 1);
        }
    }
}
