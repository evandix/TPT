package com.androidy.tpt.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.Scroller;
import android.widget.TextView;

import com.androidy.tpt.R;
import com.androidy.tpt.info.Tips;

/**
 * Created by christinajackey on 3/24/15.
 */
public class TipAdapter extends BaseAdapter {

    private Tips[] mTips;
    private Context mContext;

    public TipAdapter(Context context , Tips[] tipses) {
        mTips = tipses;
        mContext = context;

    }

    @Override
    public int getCount() {
        return mTips.length;
    }

    @Override
    public Object getItem(int position) {
         return mTips[mTips.length - position - 1];

    }


    @Override
    public long getItemId(int position) {
        return 0;   // we will not use
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null ) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.tip_list_item , null);
            holder = new ViewHolder();

            holder.linkImageButton = (ImageButton) convertView.findViewById(R.id.linkButton);
            holder.shareImageButton =  (ImageButton) convertView.findViewById(R.id.shareButton);
            holder.tipTextView = (TextView) convertView.findViewById(R.id.tipText);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Tips tip = mTips[position];

        final String link = tip.getLink();

        if (link.length() > 5) {

                holder.linkImageButton.setImageResource(R.drawable.whitelink);
                holder.linkImageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(link));
                        mContext.startActivity(intent);

                    }
                });
                // nothing happens
            } else {

        }


        String tipText = tip.getMessage();
        holder.tipTextView.setText(tipText);
        holder.tipTextView.setMovementMethod(new ScrollingMovementMethod());

        holder.shareImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String textToShare = tip.getMessage();
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, textToShare);

                mContext.startActivity(intent);

            }
        });

        return convertView;

    }

    public static class ViewHolder {
        ImageButton linkImageButton;
        ImageButton shareImageButton;
        TextView tipTextView;
    }
}
