package com.trafficpol;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.IOException;
import java.util.ArrayList;

public class MyRecyclerViewVideosAdapter extends RecyclerView
        .Adapter<MyRecyclerViewVideosAdapter
        .VideoObjectHolder> {
    private static String LOG_TAG = "MyRecyclerViewAdapter";
    private ArrayList<VideoObject> mDataset;
    private Context context;

    private static MyClickListener myClickListener;

    public static class VideoObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        TextView status;
        TextView dateTime;
        TextView location;
        ImageButton play_button;
        ImageView thumbnailEvent;

        public VideoObjectHolder(View itemView) {
            super(itemView);
            status = (TextView) itemView.findViewById(R.id.status);
            play_button=(ImageButton) itemView.findViewById(R.id.play);
            location=(TextView)itemView.findViewById(R.id.location);
            dateTime = (TextView) itemView.findViewById(R.id.textView);
            thumbnailEvent=(ImageView) itemView.findViewById(R.id.thubnail);

        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);

        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;

    }

    public MyRecyclerViewVideosAdapter(Context context,ArrayList<VideoObject> myDataset) {
        this.context=context;
        mDataset = myDataset;
    }


    @Override
    public VideoObjectHolder onCreateViewHolder(ViewGroup parent,
                                                int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview, parent, false);

        VideoObjectHolder videoObjectHolder = new VideoObjectHolder(view);
        return videoObjectHolder;
    }

    @Override
    public void onBindViewHolder(VideoObjectHolder holder, final int position) {

        //holder.label.setText(mDataset.get(position).getStatusEvent());
        try {
            holder.thumbnailEvent.setImageBitmap(mDataset.get(position).getThumbnail());
            holder.dateTime.setText(mDataset.get(position).getTimeVideo() + ", "
                    + mDataset.get(position).getMonthName() + " "
                    + mDataset.get(position).getDayVideo() + ", "
                    + mDataset.get(position).getYearVideo());

            String coordinates=mDataset.get(position).getLocationVideo();

            /*
            Geocoder geo = new Geocoder(this.context, Locale.getDefault());
            //split coordinate
            // Get the position of the -/+ sign in location String, which indicates
            // the beginning of the longtitude.
            if(coordinates!=null){
                int index = coordinates.lastIndexOf('-');
                if (index == -1) {
                    index = coordinates.lastIndexOf('+');
                }
                double latitude = Double.parseDouble(coordinates.substring(0, index - 1));
                double longitude = Double.parseDouble(coordinates.substring(index));
                holder.location.setText(GPSTracker.getLocationName(geo, latitude, longitude));}*/
            //set Location

            holder.location.setText(mDataset.get(position).getLocationFromFileVideo());


            // holder.location.setText(mDataset.get(position).getLocationVideo());

            holder.thumbnailEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, CardEditVideoActivity.class);
                    intent.putExtra("position_video_to_edit", position);
                    context.startActivity(intent);
                }
            });
        holder.play_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, CardEditVideoActivity.class);
                        intent.putExtra("position_video_to_edit", position);
                        context.startActivity(intent);
                    }
                });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addItem(VideoObject videoObj, int index) {
        mDataset.add(index, videoObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface MyClickListener {
         void onItemClick(int position, View v);
    }
}