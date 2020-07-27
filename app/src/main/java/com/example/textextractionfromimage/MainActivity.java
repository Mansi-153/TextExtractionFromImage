package com.example.textextractionfromimage;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.textextractionfromimage.R;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    Integer REQUESST_CAMERA=1, SELECT_FILE=0;
    Uri selectImageUri;
    private Button listenButton, b;
    ImageView ivImage;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int rotation = 0;
        ivImage = (ImageView) findViewById(R.id.imageView);
        MainActivity activity = (MainActivity) MainActivity.this;
        b=findViewById(R.id.button);
        listenButton=findViewById(R.id.button3);
        listenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage();
            }
        });


    }

    private void SelectImage(){
        final CharSequence[] items={"Camera","Gallery","Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Welcome");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if(items[i].equals("Camera")){
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUESST_CAMERA);

                }
                else if(items[i].equals("Gallery")){
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent.createChooser(intent, "Select File"),SELECT_FILE);

                }
                else if(items[i].equals("Cancel")){
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        Bitmap bmp = null;
        super.onActivityResult(requestCode, resultCode,data);
        if(resultCode== Activity.RESULT_OK){
            if(requestCode==REQUESST_CAMERA){
                Bundle bundle = data.getExtras();
                bmp = (Bitmap)bundle.get("data");
                ivImage.setImageBitmap(bmp);

            }else if(requestCode==SELECT_FILE){


                selectImageUri = data.getData();
                ivImage.setImageURI(selectImageUri);
                String a = selectImageUri.getPath();
                try {
                    bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectImageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(this, a, Toast.LENGTH_LONG).show();

            }
            try {
                extract(bmp);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
    public void extract(Bitmap bitmap) throws IOException {
        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        SparseArray<TextBlock> items = textRecognizer.detect(frame);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            TextBlock myItems = items.valueAt(i);
            stringBuilder.append(myItems.getValue());
            stringBuilder.append("\n");
        }
        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(stringBuilder);
        Log.d("meraString", String.valueOf(stringBuilder));

    }

}
