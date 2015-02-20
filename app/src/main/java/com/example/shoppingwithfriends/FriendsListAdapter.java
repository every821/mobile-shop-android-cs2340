package com.example.shoppingwithfriends;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;

import java.util.ArrayList;

public class FriendsListAdapter extends ArrayAdapter<String> implements SwipeLayout.SwipeListener {
    private final Context context;

  @Override
  public void onStartOpen(SwipeLayout swipeLayout) {

  }
          @Override
          public void onOpen(SwipeLayout swipeLayout) {

          }
          @Override
          public void onStartClose(SwipeLayout swipeLayout) {

          }
          @Override
          public void onClose(SwipeLayout swipeLayout) {

          }
          @Override
          public void onUpdate(SwipeLayout swipeLayout, int i, int i2) {

          }
          @Override
          public void onHandRelease(SwipeLayout swipeLayout, float v, float v2) {

          }
    private final ArrayList<String> userslist;

    public FriendsListAdapter(Context context, ArrayList<String> u) {
        super(context, R.layout.friends_list_item, u);
        this.context = context;
        this.userslist = u;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.friends_list_item, parent, false);
        TextView tvUsername = (TextView) rowView.findViewById(R.id.FRIENDSLISTITEM_TEXTIEW_ITEM);
        ImageView ivAddView = (ImageView) rowView.findViewById(R.id.FRIENDSLISTITEM_IMAGEVIEW_ADDORVIEW);
        ivAddView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Click", "Image clicked: " + pos);
            }
        });
        tvUsername.setText(userslist.get(position));
        ivAddView.setVisibility(View.GONE);
        return rowView;
    }

    @Override
    public int getCount(){
        return userslist.size();
    }
}
