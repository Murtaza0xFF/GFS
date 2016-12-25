package com.theeaglehaslanded.goalazo.view;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.theeaglehaslanded.goalazo.R;
import com.theeaglehaslanded.goalazo.model.RedditJson;
import com.theeaglehaslanded.goalazo.network.request.DownloadStream;
import com.theeaglehaslanded.goalazo.utils.NetworkState;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Submissions extends RecyclerView.Adapter<Submissions.SubmissionViewHolder> {

    MainView mainView;
    private LayoutInflater layoutInflater;
    private Context context;
    private List<RedditJson> redditJsonList;
    private RedditJson current;
    private AdapterView.OnItemClickListener mOnItemClickListener;
    private int progress = 0;

    public Submissions(MainView mainView, Context context, List<RedditJson> redditJsonList) {
        this.context = context;
        this.redditJsonList = redditJsonList;
        layoutInflater = LayoutInflater.from(context);
        this.mainView = mainView;
    }

    @Override
    public SubmissionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.card_row, parent, false);
        return new SubmissionViewHolder(view, this);
    }


    @Override
    public void onBindViewHolder(final SubmissionViewHolder holder, final int position) {
        current = redditJsonList.get(position);
        holder.title.setText(current.getTitle().replace("&amp;", "&"));
        String Url = redditJsonList.get(position).getURL();
        String modVar = Url.substring(Url.lastIndexOf("/") + 1).trim();
        Url = Url.replace("/video/mp4/", "/image/");
        Url = Url + ".jpg";
        String thumbnailUrl = Url;
        try {
            Picasso.with(context).load(thumbnailUrl).error(R.mipmap.ic_placeholder).fit()
                    .into(holder.imageView);
        } catch (IllegalArgumentException e) {
            holder.imageView.setImageResource(R.mipmap.ic_placeholder);
        }
        holder.downloadIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterPopup(v, holder.getLayoutPosition(), position);
            }
        });
    }

    void startDownload(String URL, String title, int position){
        if(NetworkState.isNetworkAvailable(context)) {
            DownloadStream downloadStream = new DownloadStream(URL, title, position, this, context);
        }else{
            NetworkState.NetworkUnavailableToast(context);
        }
    }

    public void setDownloadBar(int value, int position){
    }


    @Override
    public int getItemCount() {
        return redditJsonList.size();
    }

    public void updateDataSet(ArrayList<RedditJson> redditJsonList){
        this.redditJsonList = redditJsonList;
        notifyDataSetChanged();
    }

    private void showFilterPopup(View v, final int layoutPosition, final int position) {
        PopupMenu popup = new PopupMenu(context, v);
        popup.getMenuInflater().inflate(R.menu.popup, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.download:
                        startDownload(redditJsonList.get(position).getURL(),
                                redditJsonList.get(position).getTitle(), layoutPosition);
                        return true;
                    case R.id.share:
                        Intent share = new Intent(Intent.ACTION_SEND);
                        share.setType("text/plain");
                        String url = redditJsonList.get(position).getURL();
                        String modVar = url.substring(url.lastIndexOf("/") + 1).trim();
                        String modURL = "https://streamable.com/" + modVar;
                        String extraText = redditJsonList.get(position).getTitle() +
                                ": " + modURL;
                        share.putExtra(Intent.EXTRA_TEXT, extraText);
                        context.startActivity(Intent.createChooser(share, "Share the GIF"));
                        return true;
                    default:
                        return false;
                }
            }
        });
        popup.show();
    }



    class SubmissionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            PopupMenu.OnMenuItemClickListener {

        @Bind(R.id.title_video)
        TextView title;
        @Bind(R.id.thumbnail)
        ImageView imageView;
        @Bind(R.id.download_card)
        CardView downloadCard;
        @Bind(R.id.download_icon)
        ImageView downloadIcon;
        private RecyclerView.Adapter adapter;

        public SubmissionViewHolder(View itemView, RecyclerView.Adapter adapter) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.adapter = adapter;
            itemView.findViewById(R.id.card).setOnClickListener(this);
            itemView.findViewById(R.id.download_card).setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = this.getAdapterPosition();
            if(v.getId() == R.id.card) {
                mainView.startVideoFragment(redditJsonList.get(position).getURL());
            }
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            return false;
        }
    }

}