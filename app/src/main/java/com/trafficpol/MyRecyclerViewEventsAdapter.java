package com.trafficpol;

import android.content.Context;
import android.content.Intent;
import android.location.Geocoder;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class MyRecyclerViewEventsAdapter extends RecyclerView
        .Adapter<MyRecyclerViewEventsAdapter
        .EventObjectHolder> {
    private static String LOG_TAG = "MyRecyclerViewAdapter";
    private ArrayList<EventObject> mDataset;
    private Context context;
    private String type;
    private ArrayList<Integer> positions;

    private static MyClickListener myClickListener;

    public static class EventObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        TextView label;
        TextView dateTime;
        //LinearLayout container_upper;
        CardView container_upper;
        LinearLayout container_down;

        TextView location;
        TextView status;
        ImageView thumbnailEvent;
        ImageButton play_video;

        public EventObjectHolder(View itemView) {
            super(itemView);
            //   label = (TextView) itemView.findViewById(R.id.textView2);
            status=(TextView)itemView.findViewById(R.id.status);
            location=(TextView)itemView.findViewById(R.id.location);
            dateTime = (TextView) itemView.findViewById(R.id.textView);
            thumbnailEvent=(ImageView) itemView.findViewById(R.id.thubnail);
            play_video=(ImageButton) itemView.findViewById(R.id.play);
            container_upper=(CardView) itemView.findViewById(R.id.card_view);

            container_down=(LinearLayout)itemView.findViewById(R.id.container_down);
            Log.i(LOG_TAG, "Adding Listener");
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;

    }

    public MyRecyclerViewEventsAdapter(Context context, ArrayList<EventObject> myDataset,ArrayList<Integer> myPositions, String type) {
        this.context=context;
        mDataset = myDataset;
        this.type=type;
        this.positions=myPositions;

    }

    @Override
    public EventObjectHolder onCreateViewHolder(ViewGroup parent,
                                                int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview, parent, false);

        EventObjectHolder eventObjectHolder = new EventObjectHolder(view);
        return eventObjectHolder;
    }

    @Override
    public void onBindViewHolder(EventObjectHolder holder, final int position) {
        try {
            holder.thumbnailEvent.setImageBitmap(mDataset.get(position).getThumbnail());
            holder.dateTime.setText(mDataset.get(position).getTimeEvent() + ", "
                    + mDataset.get(position).getDayEvent() + " "
                    + mDataset.get(position).getMonthName() + " "
                    + mDataset.get(position).getYearEvent());
            switch(type){
                case "UNREPORTED":{
                    holder.thumbnailEvent.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, CardEditUnreportedEventActivity.class);
                            intent.putExtra("position_video_to_edit", positions.get(position));
                           // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            context.startActivity(intent);
                        }
                    });
                    holder.play_video.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, CardEditUnreportedEventActivity.class);
                            intent.putExtra("position_video_to_edit", positions.get(position));
                            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            context.startActivity(intent);
                        }
                    });
                    break;
                }
                case "REPORTED":{
                    holder.thumbnailEvent.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, CardSeeEventsActivity.class);
                            intent.putExtra("position_video_to_edit", positions.get(position));
                            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            context.startActivity(intent);
                        }
                    });
                    holder.play_video.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, CardSeeEventsActivity.class);
                            intent.putExtra("position_video_to_edit", positions.get(position));
                            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            context.startActivity(intent);
                        }
                    });
                    break;
                }
                case "unavailable":{
                    holder.thumbnailEvent.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, CardSeeEventsActivity.class);
                            intent.putExtra("position_video_to_edit", positions.get(position));
                            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            context.startActivity(intent);
                        }
                    });
                    holder.play_video.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, CardSeeEventsActivity.class);
                            intent.putExtra("position_video_to_edit", positions.get(position));
                            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            context.startActivity(intent);
                        }
                    });
                    break;
                }
            }


            Geocoder geo = new Geocoder(this.context, Locale.getDefault());
            holder.location.setText(mDataset.get(position).getLocationEvent());

        } catch (IOException e1) {
            e1.printStackTrace();
        }
        holder.status.setText(mDataset.get(position).getStatusEvent());
        switch(mDataset.get(position).getStatusEvent()){
            case "UNREPORTED": {
                holder.container_upper.setBackgroundColor(context.getResources().getColor(R.color.unreported_red_upper));
                holder.container_down.setBackgroundColor(context.getResources().getColor(R.color.unreported_red_down));
                break;
            }
            case "REPORTED": {
                holder.container_upper.setBackgroundColor(context.getResources().getColor(R.color.approved_green_upper));
                holder.container_down.setBackgroundColor(context.getResources().getColor(R.color.approvedi_green_down));
                break;
            }

            case "in progress": {
                holder.container_upper.setBackgroundColor(context.getResources().getColor(R.color.darkorange));
                holder.container_down.setBackgroundColor(context.getResources().getColor(R.color.orange));
                break;
            }
            case "rejected": {
                holder.container_upper.setBackgroundColor(context.getResources().getColor(R.color.rejected_grey_upper));
                holder.container_down.setBackgroundColor(context.getResources().getColor(R.color.rejected_grey_down));
                break;
            }
            case "accepted": {
                holder.container_upper.setBackgroundColor(context.getResources().getColor(R.color.approved_green_upper));
                holder.container_down.setBackgroundColor(context.getResources().getColor(R.color.approvedi_green_down));
                break;
            }
            default:{
                holder.container_upper.setBackgroundColor(context.getResources().getColor(R.color.darkBlue));
                holder.container_down.setBackgroundColor(context.getResources().getColor(R.color.lightBlue));
                break;
            }
        }
    }

    public void addItem(EventObject eventObj, int index) {
        mDataset.add(index, eventObj);
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

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    // Insert a new item to the RecyclerView on a predefined position
    public void insert(int position, EventObject event) {
        mDataset.add(position, event);
        notifyItemInserted(position);
    }

    // Remove a RecyclerView item containing a specified Data object
    public void remove(EventObject event) {
        int position = mDataset.indexOf(event);
        mDataset.remove(position);
        notifyItemRemoved(position);
    }
}