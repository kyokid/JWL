package com.fpt.jwl.qrcodegen;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class MainActivity extends AppCompatActivity {

    EditText editText, editText1;
    Button button;
    ImageView imageView;
    String info2Qr;
    public static final String TAG = ";";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = (EditText) findViewById(R.id.editText);
        editText1 = (EditText) findViewById(R.id.editText1);
        button = (Button) findViewById(R.id.button);
        imageView = (ImageView) findViewById(R.id.image);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                info2Qr = editText.getText().toString().trim() + TAG + editText1.getText().toString().trim();
                System.out.println(info2Qr);
                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                try {
                    BitMatrix bitMatrix = multiFormatWriter.encode(info2Qr, BarcodeFormat.QR_CODE, 200, 200);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                    imageView.setImageBitmap(bitmap);
                }
                catch (WriterException  e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
