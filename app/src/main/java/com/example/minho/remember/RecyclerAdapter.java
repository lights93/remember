package com.example.minho.remember;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    Context context;
    List<Recycler_item> items;
    int item_layout;
    public RecyclerAdapter(Context context, List<Recycler_item> items, int item_layout) {
        this.context=context;
        this.items=items;
        this.item_layout=item_layout;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardview,null);
        return new ViewHolder(v);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Recycler_item item=items.get(position);
        String type = item.getType();
        holder.date.setText(item.getDate());


        if(type.equals("image")) {
            holder.image.setVisibility(View.VISIBLE);
            holder.time.setText(item.getTime());
            holder.image.setImageBitmap(item.getImage());
            holder.content.setVisibility(View.GONE);
            holder.cardview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, item.getContent(), Toast.LENGTH_SHORT).show();
                }

            });
        } else if(type.equals("text")) {
            holder.content.setVisibility(View.VISIBLE);
            holder.image.setVisibility(View.GONE);
            holder.time.setText(item.getTime());
            holder.content.setText(item.getContent());
        } else if(type.equals("voice")){
            holder.image.setVisibility(View.VISIBLE);
            holder.time.setText(item.getTime());
            Drawable drawable=context.getResources().getDrawable(R.drawable.play);
            holder.image.setBackground(drawable);
            holder.content.setVisibility(View.GONE);
            holder.cardview.setOnClickListener(new View.OnClickListener() {
                MediaPlayer player = new MediaPlayer();
                @Override
                public void onClick(View v) {
                    if(player!=null)
                    {
                        player.stop();
                        player.release();
                        player=null;
                    }

                    try{

                        player=new MediaPlayer();
                        player.setDataSource(item.getContent());

                        player.prepare();
                        player.start();

                    }catch(Exception e){
                        Log.e("sampleAudioRecorder", "Audio play failed", e);
                    }
                    Toast.makeText(context, item.getContent(), Toast.LENGTH_SHORT).show();
                }

            });
        }
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView content;
        TextView time;
        TextView date;
        CardView cardview;

        public ViewHolder(View itemView) {
            super(itemView);
            image=(ImageView)itemView.findViewById(R.id.cardview_image);
            content=(TextView)itemView.findViewById(R.id.cardview_content) ;
            time=(TextView)itemView.findViewById(R.id.cardview_time);
            date=(TextView)itemView.findViewById(R.id.cardview_date);
            cardview=(CardView)itemView.findViewById(R.id.cardview);
        }
    }
}
