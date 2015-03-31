package com.example.shoppingwithfriends;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


@SuppressWarnings("ALL")
public class PostSale extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    static final int REQUEST_RESOLVE_ERROR = 1001;
    boolean mResolvingError = false;
    static String username, password; // --Commented out by Inspection (3/29/2015 8:13 PM):password;
    static Context mContext;
    GoogleApiClient mGoogleApiClient;
    LatLng myLocation;
    Location mLastLocation;
    static EditText etItemName, etLocation, etPrice;
    static CheckBox cbGPS, cbIncludePhoto;
    Button btPost, btCancel;
    ImageView ivItemImage;
    LinearLayout llButtonsLayout;
    String enteredAddress = "";
    Bitmap bmp;
    static File photo;
    String imageFilename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_sale);
        mContext = getApplicationContext();

        Bundle extras = getIntent().getExtras();
        username = extras.getString("username");
        password = extras.getString("password");

        etItemName = (EditText) findViewById(R.id.POSTSALE_EDITTEXT_ITEMNAME);
        etLocation = (EditText) findViewById(R.id.POSTSALE_EDITTEXT_LOCATION);
        etPrice = (EditText) findViewById(R.id.POSTSALE_EDITTEXT_PRICE);
        btPost = (Button) findViewById(R.id.POSTSALE_BUTTON_POST);
        btCancel = (Button) findViewById(R.id.POSTSALE_BUTTON_CANCEL);
        ivItemImage = (ImageView) findViewById(R.id.POSTSALE_IMAGEVIEW_ITEMIMAGE);
        llButtonsLayout = (LinearLayout) findViewById(R.id.POSTSALE_LINEARLAYOUT_BUTTONSLAYOUT);

        etItemName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                InputMethodManager imm = (InputMethodManager) getBaseContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return true;
            }
        });

        etLocation.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                InputMethodManager imm = (InputMethodManager) getBaseContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return true;
            }
        });

        etPrice.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                InputMethodManager imm = (InputMethodManager) getBaseContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return true;
            }
        });

        btPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String item = etItemName.getText().toString();
                String location = etLocation.getText().toString();
                String price = etPrice.getText().toString();
                new PostSaleTask(username, item, location, price).execute();
            }
        });

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        cbGPS = (CheckBox) findViewById(R.id.POSTSALE_CHECKBOX_GPS);
        cbIncludePhoto= (CheckBox) findViewById(R.id.POSTSALE_CHECKBOX_INCLUDEPHOTO);
        cbIncludePhoto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) { // include photo
                    ivItemImage.setVisibility(View.VISIBLE);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)llButtonsLayout.getLayoutParams();
                    params.removeRule(RelativeLayout.BELOW);
                    params.addRule(RelativeLayout.BELOW, R.id.POSTSALE_IMAGEVIEW_ITEMIMAGE);
                } else { // don't include photo
                    ivItemImage.setVisibility(View.GONE);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)llButtonsLayout.getLayoutParams();
                    params.removeRule(RelativeLayout.BELOW);
                    params.addRule(RelativeLayout.BELOW, R.id.POSTSALE_CHECKBOX_INCLUDEPHOTO);
                }
            }
        });

        ivItemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialogClass cdd = new CustomDialogClass(PostSale.this, R.style.DialogSlideAnim);
                cdd.show();
            }
        });

        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        Log.i("Map", "Created Api Client");
        mGoogleApiClient.connect();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        //noinspection StatementWithEmptyBody
        if (resultCode == RESULT_OK) {
            int width, height, newWidth, newHeight;
            float scaleWidth, scaleHeight;
            Matrix matrix;
            String[] fs;
            String newFilePath;
            switch (requestCode) {
                case 0: // choose photo
                    Uri selectedImage = imageReturnedIntent.getData();
                    String[] filePathColumn = { MediaStore.Images.Media.DATA };
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null,
                            null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();
                    bmp = BitmapFactory.decodeFile(filePath);
                    try {
                        int o = resolveBitmapOrientation(new File(filePath));
                        bmp = applyOrientation(bmp, o);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    photo = new File(filePath);
                    width = bmp.getWidth();
                    height = bmp.getHeight();
                    newWidth = 200;
                    newHeight = 200;

                    scaleWidth = ((float) newWidth) / width;
                    scaleHeight = ((float) newHeight) / height;

                    matrix = new Matrix();
                    matrix.postScale(scaleWidth, scaleHeight);
                    matrix.postRotate(0);

                    bmp = Bitmap.createBitmap(bmp, 0, 0, width, height, matrix, true);
                    fs = filePath.split(".jpg");
                    newFilePath = fs[0] + "_compressed.jpg";
                    System.out.println(newFilePath);
                    try {
                        FileOutputStream out = new FileOutputStream(newFilePath);
                        photo = new File(newFilePath);
                        bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        out.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ivItemImage.setImageBitmap(bmp);
                    ivItemImage.requestLayout();
                    break;
                case 1: // take photo
                    //Bundle extras = imageReturnedIntent.getExtras();
                    //bmp = (Bitmap) extras.get("data");
                    try {
                        photo = new File(imageFilename);
                        bmp = BitmapFactory.decodeFile(imageFilename);
                        int o = resolveBitmapOrientation(new File(imageFilename));
                        bmp = applyOrientation(bmp, o);
                        width = bmp.getWidth();
                        height = bmp.getHeight();
                        newWidth = 200;
                        newHeight = 200;

                        scaleWidth = ((float) newWidth) / width;
                        scaleHeight = ((float) newHeight) / height;

                        matrix = new Matrix();
                        matrix.postScale(scaleWidth, scaleHeight);
                        matrix.postRotate(0);

                        bmp = Bitmap.createBitmap(bmp, 0, 0, width, height, matrix, true);
                        fs = photo.getAbsolutePath().split(".jpg");
                        newFilePath = fs[0] + "_compressed.jpg";
                        try {
                            FileOutputStream out = new FileOutputStream(newFilePath);
                            photo = new File(newFilePath);
                            bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
                            out.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        ivItemImage.setImageBitmap(bmp);
                        ivItemImage.requestLayout();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    ivItemImage.setImageBitmap(bmp);
                    ivItemImage.requestLayout();
                    break;
            }

        } else {
            //Toast.makeText(getApplicationContext(), "Could not change profile picture",
            //        Toast.LENGTH_LONG).show();;
        }
    }

    private int resolveBitmapOrientation(File file) throws IOException {
        ExifInterface exif = null;
        exif = new ExifInterface(file.getAbsolutePath());

        return exif
                .getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
    }

    private Bitmap applyOrientation(Bitmap bitmap, int orientation) {
        int rotate = 0;
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotate = 270;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotate = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotate = 90;
                break;
            default:
                return bitmap;
        }
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix mtx = new Matrix();
        mtx.postRotate(rotate);
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }


    /**
     * Handles the work done when connected to Google Places API server
     * @param connectionHint details about the connection
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            myLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        } else {
            Log.e("Map", "My location is null");
        }
        cbGPS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) { // replace etLocation with GPS location
                    etLocation.setHint("Loading...");
                    new GetLocationTask(mLastLocation).execute(mContext);
                } else { // refill etLocation with entered location
                    etLocation.setText(enteredAddress);
                }
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("OnConnectionSuspended", "How'd we get here!?");
    }

    /**
     *
     * @param result The error from the failed connection
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (mResolvingError) {
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                mGoogleApiClient.connect();
            }
        } else {
            Log.e("Map", "Can't connect");
            mResolvingError = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_post_sale, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    public void onPostSaleReturn() {
        finish();
    }

    private class GetLocationTask extends AsyncTask <Context, Void, Boolean>{
        String location;
        String address;
        // --Commented out by Inspection (3/29/2015 8:13 PM):Context mContext;
        public GetLocationTask(Location loc) {
            this.location = loc.getLatitude() + ", " + loc.getLongitude();
        }
        /**
         * @param params The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected Boolean doInBackground(Context... params) {
            mContext = params[0];
            HttpURLConnection conn = null;
            URL url = null;
            int response = 400;
            String query = String.format("location=%s", location);
            String str = "";
            try {
                url = new URL("http://ythogh.com/shopwf/scripts/json_request.php");
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
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((inputLine = in.readLine())!= null) {
                    str = str + inputLine;
                    System.out.println(str);
                }
                conn.disconnect();
                in.close();
                out.close();

                JSONObject marr = new JSONObject(str);
                JSONArray arr = marr.getJSONArray("results");
                JSONObject obj = arr.getJSONObject(0);
                address = obj.getString("vicinity");
                Log.e("Address", address);
                return true;
            } catch (Exception e) {
                conn.disconnect();
                Log.e("Exception", "Exception");
                e.printStackTrace();
                return false;
            }
        }

        protected void onPostExecute(Boolean result) {
            enteredAddress = etLocation.getText().toString();
            etLocation.setText(address);
            etLocation.setHint("Location");
            System.out.println(myLocation.latitude +", " + myLocation.longitude);
        }
    }

    public static class UploadImageTask extends AsyncTask<String, Void, Boolean> {
        File photo;
        HttpURLConnection conn;

        public UploadImageTask(File file) {
            this.photo = file;
        }

        @Override
        protected Boolean doInBackground(String... itemname) {
            String iname = itemname[0];
            return uploadPhoto(iname);
        }

        @Override
        protected void onPostExecute(Boolean result) {

        }

        private boolean uploadPhoto(String itemname) {
            String urlString = "http://ythogh.com/shopwf/scripts/photos/upload_photo.php";
            String Tag = "UPLOAD";
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            String item = etItemName.getText().toString().trim().replace(" ","").replace(".","");
            String location = etLocation.getText().toString().trim().replace(" ","").replace(".","");
            String price = etPrice.getText().toString().trim().replace(" ","").replace(".","");
            String uploadFileName = String.format("%s_%s_%s_%s", username.replace(" ","").replace(".",""), item, location, price);
            try {
                FileInputStream fileInputStream = new FileInputStream(this.photo);
                URL url = new URL(urlString);
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Content-Type",
                        "multipart/form-data;boundary=" + boundary);
                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: post-data; name=uploaded_file;filename="
                        + uploadFileName + "" + lineEnd);
                System.out.println(uploadFileName);
                dos.writeBytes(lineEnd);
                Log.e(Tag, "Headers are written");

                int bytesAvailable = fileInputStream.available();
                int maxBufferSize = 1000;
                byte[] buffer = new byte[bytesAvailable];
                int bytesRead = fileInputStream.read(buffer, 0, bytesAvailable);
                while (bytesRead > 0) {
                    dos.write(buffer, 0, bytesAvailable);
                    bytesAvailable = fileInputStream.available();
                    bytesAvailable = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bytesAvailable);
                }
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                Log.e(Tag, "File is written");
                fileInputStream.close();
                dos.flush();
                dos.close();
            } catch (Exception ex) {
                Log.e(Tag, "error: " + ex.getMessage(), ex);
            }
            try {
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn
                        .getInputStream()));
                String line;
                boolean b = false;
                while ((line = rd.readLine()) != null) {
                    Log.e("Dialoge Box", "Message: " + line);
                    b = line.equals("uploaded");
                }
                rd.close();
                return b;
            } catch (IOException ioex) {
                Log.e("MediaPlayer", "error: " + ioex.getMessage(), ioex);
                return false;
            }
        }
    }

    private class CustomDialogClass extends Dialog implements View.OnClickListener{

        public Activity c;
        // --Commented out by Inspection (3/29/2015 8:13 PM):public Context mContext;
        // --Commented out by Inspection (3/29/2015 8:13 PM):public Dialog d;
        public Button btTake, btChoose, btCancel;

// --Commented out by Inspection START (3/29/2015 8:13 PM):
//        public CustomDialogClass(Activity a) {
//            super(a);
//            this.c = a;
//        }
// --Commented out by Inspection STOP (3/29/2015 8:13 PM)

// --Commented out by Inspection START (3/29/2015 8:13 PM):
//        public CustomDialogClass(Context context, int theme) {
//            super(context, theme);
//            this.mContext = context;
//        }
// --Commented out by Inspection STOP (3/29/2015 8:13 PM)

        public CustomDialogClass(Activity context, int theme) {
            super(context, theme);
            this.c = context;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.custom_dialog_post);
            btTake = (Button) findViewById(R.id.CUSTOMDIALOGPOST_BUTTON_TAKEPHOTO);
            btChoose = (Button) findViewById(R.id.CUSTOMDIALOGPOST_BUTTON_CHOOSEPHOTO);
            btCancel = (Button) findViewById(R.id.CUSTOMDIALOGPOST_BUTTON_CANCEL);
            btTake.setOnClickListener(this);
            btChoose.setOnClickListener(this);
            btCancel.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.CUSTOMDIALOGPOST_BUTTON_TAKEPHOTO:
                    try {
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (takePictureIntent.resolveActivity(c.getPackageManager()) != null) {
                            imageFilename = "JPEG_" + "itemPic" + "_";
                            File storageDir = Environment.getExternalStoragePublicDirectory(
                                    Environment.DIRECTORY_PICTURES);
                            File image = File.createTempFile(imageFilename, ".jpg", storageDir);
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(image));
                            imageFilename = image.getAbsolutePath();
                            c.startActivityForResult(takePictureIntent, 1);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.CUSTOMDIALOGPOST_BUTTON_CHOOSEPHOTO:
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    c.startActivityForResult(photoPickerIntent, 0);
                    break;
                case R.id.CUSTOMDIALOGPOST_BUTTON_CANCEL:
                    break;
                default:
                    break;
            }
            dismiss();
        }
    }

    public static class PostSaleTask extends AsyncTask<Context, Void, Boolean> {

        String username, item, location, price;
        Context mContext;

        public PostSaleTask(String username, String item, String location, String price) {
            this.username = username;
            this.item = item;
            this.location = location;
            this.price = price;
        }

        @Override
        protected Boolean doInBackground(Context... params) {
                HttpURLConnection conn = null;
                if (params != null && params.length > 0) {
                    mContext = params[0];
                }
                URL url = null;
                if (item.trim().length() == 0) {
                    return false;
                }
                if (location.trim().length() == 0) {
                    return false;
                }
                if (price.trim().length() == 0) {
                    return false;
                }
                if (username.trim().length() == 0) {
                    return false;
                }
                String query = String.format("username=%s&item=%s&location=%s&price=%s", username, item, location, price);
                try {
                    url = new URL("http://ythogh.com/shopwf/scripts/post_sale.php");
                    System.out.println(url.toString() + "?" + query);
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
                    System.out.println(conn.getResponseCode());
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String inputLine = "";
                    while ((inputLine = in.readLine()) != null) {
                        System.out.println("Result: " + inputLine);
                    }
                    System.out.println(conn.getResponseMessage());
                    conn.disconnect();
                    out.close();
                    System.out.println("POSTED");
                    return true;
                } catch (Exception e) {
                    System.out.println("PROBLEM");
                    conn.disconnect();
                    e.printStackTrace();
                    return false;
                }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                if (cbIncludePhoto.isChecked() && photo != null) {
                    System.out.println("True, true");
                    new UploadImageTask(photo).execute(this.item);
                } else {
                    System.out.println("True, False");

                }
            } else {
                System.out.println("False");
                Toast.makeText(mContext, "Problem posting this sale :(", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
