package thedorkknightrises.moviespop;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by samri_000 on 4/14/2016.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHolder> {
    public ArrayList<TrailerObj> trArray;
    Context context;
    String title;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View mView;

        public ViewHolder(View v) {
            super(v);
            mView = v;
        }

        TextView trText;
        ImageView trImage;
        ImageButton trShare;
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public TrailerAdapter(ArrayList<TrailerObj> rArrayList, Context c, String title) {
        trArray = rArrayList;
        context = c;
        this.title = title;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public TrailerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                        int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trailer_item, parent, false);

        ViewHolder vh = new ViewHolder(v);
        vh.trText = (TextView) v.findViewById(R.id.tr_name);
        vh.trImage = (ImageView) v.findViewById(R.id.tr_image);
        vh.trShare = (ImageButton) v.findViewById(R.id.share_trailer);

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        TrailerObj trailer = trArray.get(position);
        holder.trText.setText(trailer.trName);
        Glide.with(context).load("http://img.youtube.com/vi/" + trailer.getTrId() + "/0.jpg")
                .into(holder.trImage);
        holder.trImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TrailerObj t = trArray.get(position);
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://youtu.be/" + t.getTrId()));
                context.startActivity(i);
            }
        });
        holder.trShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TrailerObj t = trArray.get(position);
                Intent share = new Intent(Intent.ACTION_SEND);
                share.putExtra(Intent.EXTRA_TEXT, title + "\n" + t.getTrName() + "\nhttp://youtu.be/" + t.getTrId());
                share.setType("text/plain");
                context.startActivity(Intent.createChooser(share, "Share trailer link via"));
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (trArray == null) {
            return 0;
        } else return trArray.size();
    }
}