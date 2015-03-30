package com.example.shoppingwithfriends;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

@SuppressWarnings("ALL")
public class UsersListAdapter extends ArrayAdapter<String> {
    private final Context context;
    //  private final String users[];
    private final ArrayList<String> userslist;

    //  public FriendsListAdapter(Context context, String u[]) {
    //    super(context, R.layout.friends_list_item, new String[u.length]);
    //     this.context = context;
    //      this.users = u;
    //   }
    public UsersListAdapter(Context context, ArrayList<String> u) {
        super(context, R.layout.friends_list_item, u);
        this.context = context;
        this.userslist = u;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("ViewHolder") View rowView = inflater.inflate(R.layout.users_list_item, parent, false);
        TextView tvUsername = (TextView) rowView.findViewById(R.id.USERSLISTITEM_TEXTIEW_ITEM);
        ImageView ivAddView = (ImageView) rowView.findViewById(R.id.USERSLISTITEM_IMAGEVIEW_ADDORVIEW);
        ivAddView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Click", "Image clicked: " + pos);
            }
        });
        tvUsername.setText(userslist.get(position));
        ivAddView.setVisibility(View.GONE);
        //ivAddView.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.view));
        return rowView;
    }

    @Override
    public int getCount(){
        return userslist.size();
    }


}
