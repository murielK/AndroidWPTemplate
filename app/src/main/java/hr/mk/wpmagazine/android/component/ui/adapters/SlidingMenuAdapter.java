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
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import hr.mk.wpmagazine.Utils.TypeFaceUtils;
import hr.mk.wpmagazine.android.component.R;

/**
 * Created by Mur0 on 3/30/2015.
 */
public class SlidingMenuAdapter extends RecyclerView.Adapter {

    private final Context context;
    private final List<MenuObject> listMenu;
    private SlidingMenuListener listener;

    public SlidingMenuAdapter(Context context, List<MenuObject> listMenu) {
        this.context = context;
        this.listMenu = listMenu;
    }

    public SlidingMenuAdapter(Context context, List<MenuObject> listMenu, SlidingMenuListener listener) {
        this.context = context;
        this.listMenu = listMenu;
        this.listener = listener;
    }

    public void setListener(SlidingMenuListener listener) {
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ViewType.separator:
                return new SeparatorHolder(LayoutInflater.from(context).inflate(R.layout.menu_separator, parent, false));
            case ViewType.headerTitle:
                return new HeaderHolder(LayoutInflater.from(context).inflate(R.layout.menu_title, parent, false));
            case ViewType.content:
                return new ContentHolder(LayoutInflater.from(context).inflate(R.layout.menu_content, parent, false));
            case ViewType.banner:
                return new BannerHolder(LayoutInflater.from(context).inflate(R.layout.menu_banner, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof HeaderHolder) {
            ((HeaderHolder) holder).textView.setText(listMenu.get(position).title.toUpperCase());

        } else if (holder instanceof ContentHolder) {
            ContentHolder contentHolder = (ContentHolder) holder;
            contentHolder.contentTitle.setText(listMenu.get(position).title);

            try {
                contentHolder.image.setImageDrawable(context.getResources().getDrawable(listMenu.get(position).drawableResID));
            } catch (Exception e) {
                //empty
            }

        } else if (holder instanceof BannerHolder) {
            ((BannerHolder) holder).imageView.setImageDrawable(context.getResources().getDrawable(listMenu.get(position).drawableResID));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return listMenu.get(position).viewType.getViewType();
    }

    @Override
    public int getItemCount() {
        return listMenu.size();
    }


    public enum ViewType {

        SEPARATOR(0), HEADER_TITLE(1), CONTENT(2), BANNER(3);
        private static final int separator = 0;
        private static final int headerTitle = 1;
        private static final int content = 2;
        private static final int banner = 3;
        private final int viewType;

        ViewType(int viewType) {
            this.viewType = viewType;
        }

        public int getViewType() {
            return viewType;
        }
    }

    public interface SlidingMenuListener {

        void onMenuSelected(MenuObject menuObject);
    }

    public static class MenuObject {

        public SlidingMenuAdapter.ViewType viewType;
        public String title;
        public int drawableResID;

        public MenuObject(SlidingMenuAdapter.ViewType viewType, String title, int resID) {
            this.viewType = viewType;
            this.title = title;
            this.drawableResID = resID;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof MenuObject)) return false;

            MenuObject that = (MenuObject) o;

            if (drawableResID != that.drawableResID) return false;
            if (!title.equals(that.title)) return false;
            return viewType == that.viewType;

        }

        @Override
        public int hashCode() {
            int result = viewType.hashCode();
            result = 31 * result + title.hashCode();
            result = 31 * result + drawableResID;
            return result;
        }
    }

    public static final class Builder {

        private final Context context;
        private final List<MenuObject> list;
        private SlidingMenuListener listener;

        public Builder(Context context) {
            this.context = context;
            list = new ArrayList<>();
        }

        public Builder addBanner(int drawableResID) {
            if (drawableResID <= 0)
                throw new IllegalStateException("invalid banner res ID");

            if (!list.isEmpty() && list.get(0).viewType == ViewType.BANNER)
                throw new IllegalStateException("Banner Already added");
            else {
                final MenuObject menu = new MenuObject(ViewType.BANNER, null, drawableResID);
                list.add(0, menu);
            }

            return this;
        }

        public Builder addSeparator() {
            list.add(new MenuObject(ViewType.SEPARATOR, null, 0));
            return this;
        }

        public Builder addHeader(String title) {
            if (title == null)
                throw new NullPointerException("No null title allowed");

            final MenuObject menu = new MenuObject(ViewType.HEADER_TITLE, title, 0);
            if (!list.contains(menu))
                list.add(menu);
            else
                throw new IllegalStateException("Already added");

            return this;
        }

        public Builder addContent(String title, int resID) {
            if (title == null)
                throw new NullPointerException("No null title allowed");

//            if (resID == 0 || resID < 0)
//                throw new IllegalStateException("Invalid drawableResID"); //to support content with no images
            final MenuObject menu = new MenuObject(ViewType.CONTENT, title, resID);
            if (!list.contains(menu))
                list.add(menu);
            else
                throw new IllegalStateException("Already added");

            return this;
        }

        public Builder addListner(SlidingMenuListener listener) {
            if (listener == null)
                throw new NullPointerException("You can add null listener");

            if (this.listener != null)
                throw new IllegalStateException("Listner already added");

            this.listener = listener;

            return this;
        }

        public SlidingMenuAdapter build() {

            final SlidingMenuAdapter slidingMenuAdapter = new SlidingMenuAdapter(context, list);
            if (listener != null)
                slidingMenuAdapter.setListener(listener);

            return slidingMenuAdapter;
        }
    }

    class SeparatorHolder extends RecyclerView.ViewHolder {

        public SeparatorHolder(View itemView) {
            super(itemView);
        }
    }

    class BannerHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.imageDrawer)
        ImageView imageView;

        public BannerHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    class HeaderHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.textViewHeader)
        TextView textView;

        public HeaderHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);

            final Typeface typeface = TypeFaceUtils.getTypeFace(context, "OpenSans-Regular.ttf");
            if (typeface != null)
                textView.setTypeface(typeface);
        }
    }

    class ContentHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.image)
        ImageView image;
        @InjectView(R.id.textContentTitle)
        TextView contentTitle;

        public ContentHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);

            final Typeface typeface = TypeFaceUtils.getTypeFace(context, "OpenSans-Regular.ttf");
            if (typeface != null)
                contentTitle.setTypeface(typeface);

        }

        @OnClick(R.id.layoutContent)
        public void onClickContentMenu(View v) {
            if (listener != null)
                listener.onMenuSelected(listMenu.get(getAdapterPosition()));
        }

    }

}
