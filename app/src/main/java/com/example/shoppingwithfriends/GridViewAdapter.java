package com.example.shoppingwithfriends;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daimajia.swipe.adapters.BaseSwipeAdapter;

import java.util.ArrayList;

@SuppressWarnings("ALL")
public class GridViewAdapter extends BaseSwipeAdapter {

    private Context mContext;
    private ArrayList<String> str;
    // --Commented out by Inspection (3/29/2015 8:13 PM):private int viewWithFocus = -1;

    public GridViewAdapter(Context mContext, ArrayList<String> str) {
        this.mContext = mContext;
        this.str = str;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.sample1;
    }

    //ATTENTION: Never bind listener or fill values in generateView.
    //           You have to do that in fillValues method.
    @SuppressLint("InflateParams")
    @Override
    public View generateView(int position, ViewGroup parent) {
        return LayoutInflater.from(mContext).inflate(R.layout.swipelayout, null);
    }

    @Override
    public void fillValues(final int position, View convertView) {
        TextView t = (TextView)convertView.findViewById(R.id.text1);
        TextView t2 = (TextView)convertView.findViewById(R.id.text2);
        t2.setText(str.get(position));
    }

    @Override
    public int getCount() {
        return str.size();
    }

    @Override
    public boolean isOpen(int position) {
        return super.isOpen(position);
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}


