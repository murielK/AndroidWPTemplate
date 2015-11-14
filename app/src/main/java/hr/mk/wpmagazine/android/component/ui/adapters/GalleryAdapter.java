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
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import hr.mk.wpmagazine.Utils.TagFactoryUtils;
import hr.mk.wpmagazine.android.component.R;
import hr.mk.wpmagazine.model.WP.WPPost;
import io.realm.RealmResults;

/**
 * Created by Mur0 on 5/2/2015.
 */
public class GalleryAdapter extends WPAdapter {

    private static final String TAG = TagFactoryUtils.getTag(GalleryAdapter.class);
    private final Context context;
    private RealmResults<WPPost> realmResults;
    private WPAdapterOnClickListener listener;

    public GalleryAdapter(Context context, RealmResults<WPPost> realmResults) {
        this.context = context;
        this.realmResults = realmResults;
    }

    @Override
    public void setListener(WPAdapterOnClickListener listener) {
        this.listener = listener;

    }

    @Override
    public void flush() {
        //
    }

    @Override
    public void removeListener() {
        this.listener = null;
    }

    @Override
    public void updateRealmResult(RealmResults<WPPost> realmResults) {
        this.realmResults = realmResults;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ImageHolder(LayoutInflater.from(context).inflate(R.layout.row_gallery, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ImageHolder)
            try {
                Picasso.with(context)
                        .load(realmResults.get(position).getFeatured_image().getSource())
                        .fit()
                        .centerCrop()
                        .error(R.drawable.img_holder)
                        .into(((ImageHolder) holder).imageView);
            } catch (Exception e) {
                //Empty
            }
    }

    @Override
    public int getItemCount() {
        return realmResults == null ? 0 : realmResults.size();
    }

    class ImageHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.galleryImage)
        ImageView imageView;

        public ImageHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }

        @OnClick(R.id.galleryImage)
        public void onClickGalImg(View v) {
            if (listener != null)
                listener.onClick(v, getAdapterPosition());
        }
    }
}
