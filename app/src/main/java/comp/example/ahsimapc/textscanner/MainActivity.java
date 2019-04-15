package comp.example.ahsimapc.textscanner;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_PHOTO_ATTACH=2;


    private TextView textView;
    private ImageView imageview;
    private Button snap;
    private  Bitmap bitmap;
    private Button detect;
    PhotoLab mPhotoLab;
    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView=(TextView)findViewById(R.id.textview);
        imageview=(ImageView)findViewById(R.id.imageview);
        snap=(Button)findViewById(R.id.snap_button);
        detect=(Button)findViewById(R.id.detect_button);
        mPhotoLab=PhotoLab.getPhotoLabInstance(this);
        snap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickImage();

            }
        });


        detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detectImage();
            }
        });


    }



    private void clickImage()
    {
      Intent i=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

      if(i.resolveActivity(getPackageManager())!=null)
      {    Photo photo=new Photo();
          file= mPhotoLab.getFile(photo);

         if(file==null)
         {
             return;
         }
          Uri uri=FileProvider.getUriForFile(this,"com.example.ahsimapc.textscanner.fileprovider",file);
          i.putExtra(MediaStore.EXTRA_OUTPUT,uri);

          List<ResolveInfo> activities=getPackageManager().queryIntentActivities(i,PackageManager.MATCH_DEFAULT_ONLY);


              for(ResolveInfo resolveInfo:activities)
              {

                  grantUriPermission(resolveInfo.activityInfo.packageName,uri,i.FLAG_GRANT_WRITE_URI_PERMISSION);
              }


          startActivityForResult(i,REQUEST_IMAGE_CAPTURE);
      }



    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_IMAGE_CAPTURE&&resultCode== Activity.RESULT_OK)
        {
            String path=file.getPath();
           bitmap= BitmapFactory.decodeFile(path);
           imageview.setImageBitmap(bitmap);

        }
      else if(requestCode==REQUEST_PHOTO_ATTACH&&resultCode==Activity.RESULT_OK)
        {

            String path=data.getStringExtra(PhotoListView.TAG);
            bitmap=BitmapFactory.decodeFile(path);
            imageview.setImageBitmap(bitmap);
        }

    }

    private void detectImage()
    {

        FirebaseVisionImage firebaseVisionImage=FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionTextDetector detector=FirebaseVision.getInstance().getVisionTextDetector();

        detector.detectInImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                getText(firebaseVisionText);

            }
        })

                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {

                        Toast.makeText(getApplicationContext(),"Unable to detect",Toast.LENGTH_SHORT).show();

                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       getMenuInflater().inflate(R.menu.main_activity,menu);
       return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if(item.getItemId()==R.id.explore_buuton)
        {


            Intent i=new Intent(this,PhotoListView.class);
            startActivityForResult(i,REQUEST_PHOTO_ATTACH);
        }
            return true;
    }

    private void getText(FirebaseVisionText text)
    {
        List<FirebaseVisionText.Block> blocks=text.getBlocks();
        Log.i("Hello",""+blocks.size());
        if(blocks.size()==0)
        {
            Toast.makeText(this, "No Text", Toast.LENGTH_SHORT).show();
            return;
        }
        textView.setText("");

        for(FirebaseVisionText.Block block:blocks)
        {

              textView.setTextSize(24);

            String s=block.getText();
            Log.i("Hello",s+"\n");
            
            
            textView.append(s+"\n");
            
        }



    }
}
