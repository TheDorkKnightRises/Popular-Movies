package thedorkknightrises.moviespop;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by samri_000 on 4/15/2016.
 */
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    public ArrayList<ReviewObj> rArray;
    Context context;

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

        TextView rText;
        TextView rAuth;
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ReviewAdapter(ArrayList<ReviewObj> rArrayList, Context c) {
        rArray = rArrayList;
        context = c;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_item, parent, false);

        ViewHolder vh = new ViewHolder(v);
        vh.rText = (TextView) v.findViewById(R.id.review_text);
        vh.rAuth = (TextView) v.findViewById(R.id.review_author);

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        ReviewObj review = rArray.get(position);
        holder.rText.setText(review.rText);
        holder.rAuth.setText(context.getString(R.string.author) + " " + review.rAuth);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (rArray == null) {
            return 0;
        } else return rArray.size();
    }
}