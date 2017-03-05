package app.num.barcodescannerproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.zxing.Result;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import me.dm7.barcodescanner.zxing.ZXingScannerView;


public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);

        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();
    }

    public void QrScanner(View view){

        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);

        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();         // Start camera

    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {

        Log.e("handler", rawResult.getText()); // Prints scan results
        Log.e("handler", rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode)
        String userId = "";
        Date createDate = null;
        try {
            JSONObject jsonObject = new JSONObject(rawResult.getText());
            userId = jsonObject.getString("userId");
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date dateUtil = format.parse(jsonObject.getString("createDate"));
            createDate = new Date(dateUtil.getTime());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        final HttpClient httpclient = new DefaultHttpClient();
        final HttpGet httpget= new HttpGet(Constants.HEROKU_URL + "users/profile?term=" + userId +
                "&createDate=" + createDate + "&ticketid=" + Utils.getRandomString());
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
}
