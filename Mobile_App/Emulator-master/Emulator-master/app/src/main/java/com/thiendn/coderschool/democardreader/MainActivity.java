package com.thiendn.coderschool.democardreader;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.zxing.Result;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static com.thiendn.coderschool.democardreader.SettingDialog.*;


public class MainActivity extends AppCompatActivity implements LoginCardReader.LoginCallback, ZXingScannerView.ResultHandler{
    private String mURL;
    private ZXingScannerView mScannerView;
    public LoginCardReader mLoginCardReader;
    public static int READER_FLAGS =
            NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK;
    private String saveUrl = "URL";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mURL = Constants.BASE_URL;
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        setContentView(mScannerView);
        mScannerView.startCamera();
        mLoginCardReader = new LoginCardReader(this);
        enableReaderMode();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.setting:
                showSetting();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setmURL(String url){
        mURL = url;
    }

    private void showSetting() {
        android.app.FragmentManager fm = getFragmentManager();
        SettingDialog settingDialog = newInstance(MainActivity.this, new Listener() {
            @Override
            public void onSaveButtonClick(String url) {
                setmURL(url);
//                mURL = url;
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
                editor.putString(saveUrl, url);
                editor.apply();
            }
        });
        settingDialog.show(fm, "Setting");
    }

    @Override
    public void onAccountReceived(final String account) {
        String url = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString(saveUrl, "https://jwl-api-v0.herokuapp.com/");
        System.out.println("da toi day, Main activity Account is " + account);
//         This callback is run on a background thread, but updates to UI elements must be performed
//         on the UI thread.
        String userId = "";
        String key = "";
        try {
            JSONObject jsonObject = new JSONObject(account);
            userId = jsonObject.getString("userId");
            key = jsonObject.getString("key");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final HttpClient httpclient = new DefaultHttpClient();
//        final HttpGet httpget= new HttpGet("https://jwl-api-v0.herokuapp.com/users/profile?term=" + account +
//        final HttpGet httpget= new HttpGet(mURL + "users/profile?term=" + account +
//                "&createDate=" + new Date(Calendar.getInstance().getTimeInMillis()) +
//                "&ticketid=" + Utils.getRandomString());
        final HttpGet httpget = new HttpGet(url + "users/" + userId + "/checkin" + "?key=" + key);
        System.out.println("URI: " + httpget.getURI());

        final HttpResponse[] response = new HttpResponse[1];

        Thread executeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    response[0] = httpclient.execute(httpget);
                    String server_response = EntityUtils.toString(response[0].getEntity());
                    final JSONObject jsonObject = new JSONObject(server_response);
                    new Thread()
                    {
                        public void run()
                        {
                            MainActivity.this.runOnUiThread(new Runnable()
                            {
                                public void run()
                                {
                                    try {
//                                        tvHelloWorld.setText(jsonObject.getString("data"));
                                        Toast.makeText(getBaseContext(), jsonObject.getString("data"), Toast.LENGTH_LONG).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }.start();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        executeThread.start();

    }

    @Override
    public void onError() {
//        Toast.makeText(MainActivity.this, "Please touch again!", Toast.LENGTH_LONG).show();
        Thread errorThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "TOUCH AGAIN", Toast.LENGTH_SHORT).show();
            }
        });
        errorThread.start();
    }

    private void enableReaderMode() {
        NfcAdapter nfc = NfcAdapter.getDefaultAdapter(getBaseContext());
        if (nfc != null) {
            try {
                nfc.enableReaderMode(this, mLoginCardReader, READER_FLAGS, null);
            }catch (UnsupportedOperationException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void handleResult(Result rawResult) {
        String url = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString(saveUrl, "https://jwl-api-v0.herokuapp.com/");
        Log.e("handler", rawResult.getText()); // Prints scan results
        Log.e("handler", rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode)
        String userId = "";
        String key = "";
        try {
            JSONObject jsonObject = new JSONObject(rawResult.getText());
            userId = jsonObject.getString("userId");
            key = jsonObject.getString("key");
        } catch (JSONException e) {
            e.printStackTrace();
        }
            final HttpClient httpclient = new DefaultHttpClient();
            final HttpGet httpget = new HttpGet(url + "users/" + userId + "/checkin" + "?key=" + key);
            final HttpResponse[] response = new HttpResponse[1];
            System.out.println("URI " + httpget.getURI());
            Thread executeThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        response[0] = httpclient.execute(httpget);
                        String server_response = EntityUtils.toString(response[0].getEntity());
                        final JSONObject jsonObject = new JSONObject(server_response);
                        new Thread() {
                            public void run() {
                                MainActivity.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        try {
                                            // show the scanner result into dialog box.
                                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                            builder.setTitle("Scan Result");
                                            builder.setMessage(jsonObject.getString("data"));
                                            final AlertDialog alert1 = builder.create();
                                            alert1.show();
                                            //mScannerView.stopCamera();
                                            final Handler handler = new Handler();
                                            final Runnable runnable = new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (alert1.isShowing()) {
                                                        alert1.dismiss();
                                                    }
                                                }
                                            };
                                            alert1.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                                @Override
                                                public void onDismiss(DialogInterface dialogInterface) {
                                                    handler.removeCallbacks(runnable);
                                                }
                                            });
                                            handler.postDelayed(runnable, 3000);
                                            mScannerView.resumeCameraPreview(MainActivity.this);
                                            //tvHelloWorld.setText(jsonObject.getString("data"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        }.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            executeThread.start();

    }
    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    protected void onResume() {
        super.onResume();
        mScannerView.startCamera();
    }
}
