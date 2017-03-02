package com.trafficpol;

import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Emilia on 3/22/2016.
 */
public class ViewHolderStatus extends MyRecyclerViewVideosAdapter
        .VideoObjectHolder {
    TextView day_report;
    TextView status_report;
    TextView month_report;
    TextView feedback_received;
    CardView feedback_color;
    //ImageView thumbnailEvent;

    public ViewHolderStatus(View itemView) {
        super(itemView);
        day_report = (TextView) itemView.findViewById(R.id.day_report);
        month_report = (TextView) itemView.findViewById(R.id.month_report);
        feedback_received = (TextView) itemView.findViewById(R.id.feedback_received);
        status_report=(TextView) itemView.findViewById(R.id.status_report);
        feedback_color = (CardView) itemView.findViewById(R.id.card_view);
        //thumbnailEvent=(ImageView) itemView.findViewById(R.id.thubnail);


    }
}