package com.example.shoppingwithfriends;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class FriendsListAdapter extends ArrayAdapter<String> {
    private final Context context;
  //  private final String users[];
    private final ArrayList<String> userslist;

  //  public FriendsListAdapter(Context context, String u[]) {
    //    super(context, R.layout.friends_list_item, new String[u.length]);
   //     this.context = context;
  //      this.users = u;
 //   }
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
        ivAddView.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_add_person));
        return rowView;
    }

    @Override
    public int getCount(){
        return userslist.size();
    }


}
