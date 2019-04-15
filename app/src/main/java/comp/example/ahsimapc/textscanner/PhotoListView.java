package comp.example.ahsimapc.textscanner;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class PhotoListView extends AppCompatActivity {

public static final String TAG="path";



    RecyclerView recyclerView;
    List<File> listk=new ArrayList<File>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_list_view);
        File file=getFilesDir();
        File[] files=file.listFiles();

        for(File file1:files)
        {

            if(file1.getName().contains("jpg"))
            {
                listk.add(file1);
            }
        }
        if(listk.size()==0)
        {
            recyclerView.setVisibility(View.INVISIBLE);

        }
        recyclerView=(RecyclerView)findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new PhotoAdapter(listk));
        Log.i("hello",files.length+"");

    }


    class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {

        List<File> files;
        PhotoAdapter(List<File> list)
        {files=list;

        }


        @NonNull
        @Override
        public PhotoHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

            LayoutInflater inflater=LayoutInflater.from(viewGroup.getContext());
            View v=inflater.inflate(R.layout.list_item_view,viewGroup,false);

            return new PhotoHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull PhotoHolder photoHolder, int i) {
            File file=files.get(i);

            photoHolder.onBind(file);

        }

        @Override
        public int getItemCount() {

         //   Log.d("hello",files.length+"");
            return files.size();
        }
    }


    class PhotoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
       ImageView imageView;
       TextView textView;
       String filepath;



        public PhotoHolder(@NonNull View itemView) {
            super(itemView);
            textView=(TextView)itemView.findViewById(R.id.list_item_textview);
            imageView=(ImageView)itemView.findViewById(R.id.list_item_imageview);
            itemView.setOnClickListener(this);




        }
        public void onBind(File file )
        {   filepath=file.getPath();

         String    filename=file.getName();
            Point point=new Point();
            getWindowManager().getDefaultDisplay().getSize(point);
        Bitmap bitmap=  ScaleDownImage(file.getPath(),point.x,point.y);
            imageView.setImageBitmap(bitmap);
            textView.setText(filename);



        }



        private Bitmap ScaleDownImage(String path,int destwidth,int destHeight)
        {  BitmapFactory.Options    options=new BitmapFactory.Options();
        options.inJustDecodeBounds=true;
        BitmapFactory.decodeFile(path,options);
        float srcwidth=options.outWidth;
        float srcheight=options.outHeight;
        int insampleSize=1;
        if(srcheight>destHeight||srcwidth>destHeight)
        {
            float heightScale=srcheight/destHeight;
            float widthScale=srcwidth/destwidth;


            insampleSize=Math.round(heightScale>widthScale?widthScale:heightScale);
        }
        options=new BitmapFactory.Options();
        options.inSampleSize=insampleSize;
        return BitmapFactory.decodeFile(path,options);

        }

        @Override
        public void onClick(View view) {

            Intent i=new Intent();
            i.putExtra(TAG,filepath);
            setResult(RESULT_OK,i);
            onBackPressed();

        }
    }
}
