package com.example.shoppingwithfriends;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;


@SuppressWarnings("ALL")
public class BrowseSales extends ActionBarActivity {

    static ArrayList<SaleItem> allSales;
    static ArrayList<SaleItem> filteredSales;
    CustomListAdapter adapter;
    GridView lvBrowseSales;
    String username, password;
    Double initialMin = 0.00, initialMax = 100.00;
    String mQuery = "";

    /**
     * Dispatch onPause() to fragments.
     */
    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * Call this when your activity is done and should be closed.  The
     * ActivityResult is propagated back to whoever launched you via
     * onActivityResult().
     */
    @Override
    public void finish() {
        new UpdatePriceThresholdTask(username).execute(initialMin, initialMax);
        System.out.println("Finish!");
        super.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_sales);
        Bundle extras = getIntent().getExtras();
        username = extras.getString("username");
        lvBrowseSales = (GridView) findViewById(R.id.BROWSESALES_LISTVIEW_DISPLAYLIST);
        allSales = new ArrayList<>();
        if (extras.containsKey("min") && extras.containsKey("max")) {
            initialMin = extras.getDouble("min");
            initialMax = extras.getDouble("max");
            allSales = MainActivity.arr;
            System.out.println("Size: " + allSales.size());
            for (int i = 0; i <  MainActivity.arr.size(); i++) {
                System.out.println(i);
                new DownloadImageTask(allSales.get(i), i).execute();
            }
        } else {
            new GetAllSalesTask(username).execute();
        }
    }

    private void onGetAllSalesReturn() {
        adapter = new CustomListAdapter(getApplicationContext(), allSales);
        lvBrowseSales.setAdapter(adapter);
        lvBrowseSales.requestLayout();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_browse_sales, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_filter) {
            CustomDialogClass cdd = new CustomDialogClass(BrowseSales.this, R.style.DialogSlideAnim);
            cdd.show();
            return true;
        } else if (id == R.id.action_update) {
            allSales = new ArrayList<>();
            new GetAllSalesTask(username).execute();
        }

        return super.onOptionsItemSelected(item);
    }


    private class UpdatePriceThresholdTask extends AsyncTask<Double, Void, Boolean> {

        String username;

        public UpdatePriceThresholdTask(String username) {
            this.username = username;
        }

        /**
         * @param params passes in getApplicationContext() from calling Activity
         * @return Result code from php request
         */
        @Override
        protected Boolean doInBackground(Double... params) {
            double low = params[0];
            double high = params[1];
            HttpURLConnection conn = null;
            URL url = null;
            int response = 400;
            DecimalFormat df = new DecimalFormat("###.00");
            String mLow = df.format(low);
            String mHigh = df.format(high);
            String query = String.format("username=%s&low=%s&high=%s", username, mLow, mHigh);
            System.out.println(query);
            try {
                url = new URL("http://ythogh.com/shopwf/scripts/update_price_threshold.php");
                String agent = "Applet";
                String type = "application/x-www-form-urlencoded";
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("User-Agent", agent);
                conn.setRequestProperty("Content-Type", type);
                conn.setRequestProperty("Content-Length", "" + query.length());
                OutputStream out = conn.getOutputStream();
                out.write(query.getBytes());
                response = conn.getResponseCode();
                System.out.println(response);
                String inputLine = "";
                conn.disconnect();
                out.close();
                System.out.println("Done");
                return true;
            } catch (Exception e) {
                conn.disconnect();
                e.printStackTrace();
                return false;
            }
        }

        /**
         * Updates friends-list with returned friends' names
         * @param result Result code from php request
         */
        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
        }
    }

    private class CustomListAdapter extends ArrayAdapter<String> {
        private final Context context;
        private final ArrayList<SaleItem> arr;

        public CustomListAdapter(Context context, ArrayList<SaleItem> arr) {
            super(context, R.layout.sale_item, new ArrayList<String>(arr.size()));
            this.context = context;
            this.arr = arr;
        }

        @Override
        public View getView(final int position, View rowView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final SetupViewHolder holder;
            if (rowView == null) {
                rowView = inflater.inflate(R.layout.sale_item, parent, false);
                holder = new SetupViewHolder();
                holder.tvItem = (TextView) rowView.findViewById(R.id.SALEITEM_TEXTVIEW_ITEMNAME);
                holder.tvPrice = (TextView) rowView.findViewById(R.id.SALEITEM_TEXTVIEW_PRICE);
                holder.iv = (ImageView) rowView.findViewById(R.id.SALEITEM_IMAGEVIEW_ITEMIMAGE);
                rowView.setTag(holder);
            } else {
                holder = (SetupViewHolder) rowView.getTag();
            }
            holder.tvItem.setText(arr.get(position).getItem());
            holder.tvPrice.setText(arr.get(position).getPrice());
            if (arr.get(position).getBitmap() != null) {
                System.out.println("Set bitmap");
                holder.iv.setImageBitmap(arr.get(position).getBitmap());
            } else {
                holder.iv.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.common_signin_btn_text_light));
                System.out.println("Bitmap null");
            }
            //final View rView = rowView;
            return rowView;
        }

        @Override
        public int getCount(){
            return arr.size();
        }
    }

    class SetupViewHolder {
        ImageView iv;
        TextView tvItem, tvPrice;
    }

    private class GetAllSalesTask extends AsyncTask<Void, Void, Boolean> {

        String username;

        public GetAllSalesTask(String username) {
            this.username = username;
        }

        /**
         * @param params passes in getApplicationContext() from calling Activity
         * @return Result code from php request
         */
        @Override
        protected Boolean doInBackground(Void... params) {

            HttpURLConnection conn = null;
            URL url = null;
            int response = 400;
            String query = String.format("username=%s", username);
            try {
                url = new URL("http://ythogh.com/shopwf/scripts/get_sales.php");
                String agent = "Applet";
                String type = "application/x-www-form-urlencoded";
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("User-Agent", agent);
                conn.setRequestProperty("Content-Type", type);
                conn.setRequestProperty("Content-Length", "" + query.length());
                OutputStream out = conn.getOutputStream();
                out.write(query.getBytes());
                response = conn.getResponseCode();
                System.out.println(response);
                String inputLine = "";
                String result = "";
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((inputLine = in.readLine())!= null) {
                    result += inputLine;
                    Log.e("Sale", inputLine);
                }
                JSONArray jArr = new JSONArray(result);
                JSONObject jObj;
                SaleItem mItem;
                for (int i = 0; i < jArr.length(); i++) {
                    jObj = jArr.getJSONObject(i);
                    mItem = new SaleItem(jObj.getString("username"), jObj.getString("item"),
                            jObj.getString("location"), jObj.getString("price"));
                        allSales.add(mItem);
                        new DownloadImageTask(mItem, allSales.size() - 1).execute();

                }
                conn.disconnect();
                out.close();
                return true;
            } catch (Exception e) {
                conn.disconnect();
                e.printStackTrace();
                return false;
            }
        }

        /**
         * Updates friends-list with returned friends' names
         * @param result Result code from php request
         */
        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
        }
    }

    private class DownloadImageTask extends AsyncTask<Void, Void, Bitmap> {
        // --Commented out by Inspection (3/29/2015 8:12 PM):ImageView bmp;

        final String urlPhotoBase = "http://www.ythogh.com/shopwf/scripts/photos/";
        SaleItem mItem;
        int position;

        public DownloadImageTask(SaleItem mItem, int position) {
            this.mItem = mItem;
            this.position = position;
        }

        @Override
        protected Bitmap doInBackground(Void... nada) {
            Bitmap bmp = null;
            String username = mItem.getUsername().trim().replace(" ","").replace(".","");
            String item = mItem.getItem().trim().replace(" ","").replace(".","");
            String location = mItem.getLocation().trim().replace(" ", "").replace(".","");
            String price = mItem.getPrice().trim().replace(" ", "").replace(".","");
            String url  = String.format("%s%s_%s_%s_%s.jpg", urlPhotoBase, username.replace(" ","").replace(".",""), item, location, price);
            System.out.println("photo: " +url);
            try {
                InputStream in = new java.net.URL(url).openStream();
                bmp = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bmp;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                System.out.println("Added image: " + position);
                allSales.get(position).setBitmap(result);
            }
            System.out.println("Returning");
            onGetAllSalesReturn();
        }
    }

    private class CustomDialogClass extends Dialog implements View.OnClickListener {

        // --Commented out by Inspection (3/29/2015 8:12 PM):public Activity c;
        // --Commented out by Inspection (3/29/2015 8:12 PM):public Context mContext;
        // --Commented out by Inspection (3/29/2015 8:12 PM):public Button btTake, btChoose, btCancel;
        // --Commented out by Inspection (3/29/2015 8:12 PM):public SeekBar sbPrice;

        public CustomDialogClass(Activity context, int theme) {
            super(context, theme);
            //this.c = context;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.custom_dialog_browse_filter);

            final RangeSeekBar<Double> seekBar = new RangeSeekBar<>(0.00, 100.00, getApplicationContext());
            seekBar.setSelectedMinValue(initialMin);
            seekBar.setSelectedMaxValue(initialMax);
            seekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Double>() {
                @Override
                public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Double minValue, Double maxValue) {
                    // handle changed range values
                    Log.i("tag", "User selected new range values: MIN=" + minValue + ", MAX=" + maxValue);
                }
            });
            seekBar.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        System.out.println(seekBar.getSelectedMinValue() + ", " + seekBar.getSelectedMaxValue());

                        filteredSales = new SearchSales(allSales).execute(seekBar.getSelectedMinValue(), seekBar.getSelectedMaxValue());
                        adapter = new CustomListAdapter(getApplicationContext(), filteredSales);
                        lvBrowseSales.setAdapter(adapter);
                        lvBrowseSales.requestLayout();
                        System.out.println(allSales.size() + ", " + filteredSales.size());
                    }
                    return false;
                }
            });
            // add RangeSeekBar to pre-defined layout
            ViewGroup layout = (ViewGroup) findViewById(R.id.seekbar_placeholder);
            layout.addView(seekBar);

            final EditText etSearch = (EditText) findViewById(R.id.CUSTOMDIALOGBROWSEFILTER_EDITTEXT_SEARCH);
            etSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    filteredSales = new SearchSales(allSales).execute(s.toString().trim());
                    adapter = new CustomListAdapter(getApplicationContext(), filteredSales);
                    lvBrowseSales.setAdapter(adapter);
                    lvBrowseSales.requestLayout();
                }
            });
            etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    InputMethodManager imm = (InputMethodManager) getBaseContext().getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    return true;
                }
            });

        }

        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                default:
                    break;
            }
            dismiss();
        }
    }

    private class SearchSales {

        ArrayList<SaleItem> arr;

        public SearchSales(ArrayList<SaleItem> arr){
            this.arr = arr;
        }

        public ArrayList<SaleItem> execute(String query) {
            mQuery = query;
            return execute(mQuery, initialMin, initialMax);
        }

        public ArrayList<SaleItem> execute(String query, Double min, Double max) {
            ArrayList<SaleItem> mArr = new ArrayList<>();
            try {
                for (int i = 0; i < arr.size(); i++) {
                    if ((Double.parseDouble(arr.get(i).getPrice()) >= min) && (Double.parseDouble(arr.get(i).getPrice()) <= max) && (arr.get(i).getItem().contains(query))) {
                        mArr.add(arr.get(i));
                    }
                }
                System.out.println(mArr.size());
                return mArr;
            } catch (Exception e) {
                e.printStackTrace();
                return arr;
            }
        }

        public ArrayList<SaleItem> execute(Double min, Double max) {
            initialMin = min;
            initialMax = max;
            return execute(mQuery, initialMin, initialMax);
        }

    }
}
