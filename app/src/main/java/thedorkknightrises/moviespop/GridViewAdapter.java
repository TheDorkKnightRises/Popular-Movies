package thedorkknightrises.moviespop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by samri_000 on 3/19/2016.
 */
public class GridViewAdapter extends ArrayAdapter<MovieObj>{
    private Context context;
    private ArrayList<MovieObj> movies= new ArrayList<>();

    public GridViewAdapter(Context context, ArrayList<MovieObj> mArrayList) {
        super(context, R.layout.grid_item);
        this.context=context;
        this.movies=mArrayList;
    }

    @Override
    public MovieObj getItem(int position) {
        return movies.get(position);
    }

    @Override
    public int getCount() {
        if (movies == null) {
            return 0;
        } else return movies.size();
    }

    @Override
    public int getPosition(MovieObj item) {
        return movies.indexOf(item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = LayoutInflater.from(context);
        if (convertView == null) {
            holder = new ViewHolder();
            if (MainActivity.getmDualPane() && MainActivity.getWidthFlag() || ((GridView) parent).getNumColumns() == 1) {
                convertView = inflater.inflate(R.layout.list_item, null);
                holder.title = (TextView) convertView.findViewById(R.id.list_title);
                holder.year = (TextView) convertView.findViewById(R.id.list_year);
            } else convertView = inflater.inflate(R.layout.grid_item, null);
            holder.posterView = (ImageView) convertView.findViewById(R.id.grid_image);
            holder.posterView.setMinimumHeight(holder.posterView.getWidth()*3/2);
            convertView.setTag(holder);
        } else holder = (ViewHolder) convertView.getTag();

        MovieObj movie = movies.get(position);
        Glide.with(context).load(movie.getPosterUrl())
                .placeholder(R.drawable.ic_photo_white_24px)
                .error(R.drawable.ic_photo_white_24px)
                .crossFade(500)
                .into(holder.posterView);
        if (MainActivity.getmDualPane() && MainActivity.getWidthFlag() || ((GridView) parent).getNumColumns() == 1) {
            holder.title.setText(movie.getTitle());
            if (!movie.getYear().equals(""))
            holder.year.setText(movie.getYear().substring(8));
        }
        return convertView;
    }

    private static class ViewHolder {
        ImageView posterView;
        TextView title;
        TextView year;
    }
}
