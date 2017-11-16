package com.example.jyn.filemanagerdemo;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity { // public class MainActivity extends Activity {

    public static String mFileName;
    private ListView lvFileControl;
    private Context mContext = this;

    private List<String> lItem = null;
    private List<String> lPath = null;
    private String mRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
    private TextView mPath;

    PermissionListener permissionListener;

    static int pdf_count;
    static int image_count;

    public static List<String> mFileNames = new ArrayList<String>();
    public static List<String> mFolderNames = new ArrayList<String>();

    String[] source = {
            mRoot + "/Download",
            mRoot + "/KakaoTalkDownload",
            mRoot + "/DCIM"
    };


    String[] format_pdf = {".pdf"};
    String[] format_img = {".jpg", ".png", ".bmp"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPath = findViewById(R.id.tvPath);
        lvFileControl = findViewById(R.id.lvFileControl);

        // 퍼미션 리스너(테드_ 라이브러리)
        permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
//                Toast.makeText(a_profile.this, "권한 허가", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
//                Toast.makeText(a_profile.this, "권한 거부", Toast.LENGTH_SHORT).show();
            }
        };

        // 퍼미션 체크
        permission_check();


        getDir(mRoot);

        lvFileControl.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                File file = new File(lPath.get(position));

                if (file.isDirectory()) {
                    if (file.canRead())
                        getDir(lPath.get(position));
                    else {
                        Toast.makeText(mContext, "No files in this folder.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    mFileName = file.getName();
                    Log.i("Test", "ext:" + mFileName.substring(mFileName.lastIndexOf('.') + 1, mFileName.length()));
                }
            }
        });


    }

    private void getDir(String dirPath) {
        mPath.setText("Location: " + dirPath);

        lItem = new ArrayList<String>();
        lPath = new ArrayList<String>();

        File f = new File(dirPath);
        File[] files = f.listFiles();

        if (!dirPath.equals(mRoot)) {
            //item.add(root); //to root.
            //path.add(root);
            lItem.add("../"); //to parent folder
            lPath.add(f.getParent());
        }

        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            lPath.add(file.getAbsolutePath());

            if (file.isDirectory())
                lItem.add(file.getName() + "/");
            else
                lItem.add(file.getName());
        }

        ArrayAdapter<String> fileList = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lItem);
        lvFileControl.setAdapter(fileList);
    }


    /**---------------------------------------------------------------------------
     메소드 ==> 퍼미션 체크
     ---------------------------------------------------------------------------*/
    public void permission_check() {
        // 퍼미션 확인(테드_ 라이브러리)
        new TedPermission(this)
                .setPermissionListener(permissionListener)
//                .setRationaleMessage("다음 작업을 허용하시겠습니까? 기기 사진, 미디어, 파일 액세스")
                .setDeniedMessage("[설정] > [권한] 에서 권한을 허용할 수 있습니다")
                .setGotoSettingButton(true)
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }


    public void get_PDF_clicked(View view) throws ExecutionException, InterruptedException {
        new Thread(new Runnable() {
            @Override
            public void run() {
//                format_search(mRoot, ".pdf");
                file_search(source, format_pdf);
            }
        }).start();
    }


    public void get_jpg_clicked(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
//                format_search(mRoot, ".pdf");
                file_search(source, format_img);
            }
        }).start();
    }

    public void file_count_clicked(View view) {
        Log.d("format_search_", "mFileNames.size: " + mFileNames.size());
        Log.d("format_search_", "mFolderNames.size: " + mFolderNames.size());
        Log.d("format_search_", "pdf_count: " + pdf_count);
        Log.d("format_search_", "image_count: " + image_count);

        mFileNames.clear();
        mFolderNames.clear();
        pdf_count = 0;
        image_count = 0;
    }


//    public void format_search(String source, String format) {
//        File files = new File(source);
//        File[] fileList = files.listFiles();
//
//        try {
//            if(fileList.length > 0) {
//                for(int i = 0 ; i < fileList.length ; i++) {
//                    File file = fileList[i];
//
//                    // 해당 파일이 맞는 포맷의 파일 파일이라면 list에 add
//                    if (file.getName().endsWith(format)) {
//                        mFileNames.add(file.getName());
//                        Log.d("format_search_", format +"_file_name: "+file.getName());
//
//                        if(format.equals(".pdf")) {
//                            pdf_count++;
//                        }
//                        else if(format.equals(".jpg") || format.equals(".png")) {
//                            image_count++;
//                        }
//
//                    }
//                    // 해당 파일이 폴더라면, 재귀적 방법으로 다시 탐색
//                    else if (file.isDirectory()) {
//                        format_search(file.getCanonicalPath(), format);
//                    }
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            Log.d("format_search_", "e: "+e.toString());
//        }
//    }


    public void file_search(String[] source, String[] format) {

        // source: 파일 찾을 path
        // path가 여러개임, 그래서 for문
        for(int j=0; j<source.length; j++) {
            File files = new File(source[j]);

            // 해당 path의 파일이 폴더일때만
            if(files.isDirectory()) {

                File[] fileList = files.listFiles();

                // 해당 폴더의 파일이 존재할때만
                if(fileList.length > 0) {

                    // 해당 폴더안에 있는 파일안에서
                    for(int i = 0 ; i<fileList.length ; i++) {
                        File file = fileList[i];

                        // 또 폴더가 있으면 다시 어레이리스트에 절대경로를 넣어서 저 밑 로직에서 다시 이용
                        if (file.isDirectory()) {
                            try {
                                mFolderNames.add(file.getCanonicalPath());
                                Log.d("format_search_", "folder_name: "+file.getCanonicalPath());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        // 파일이 내가 원하는 format을 가지고 있으면 어레이리스트에 파일이름 추가
                        // format이 여러개임, 그래서 for문
                        for(int k=0; k <format.length; k++) {
                            if(file.getName().endsWith(format[k])) {
                                mFileNames.add(file.getName());
                                Log.d("format_search_", format[k] +"_file_name: "+file.getName());

                                if(format[k].equals(".pdf")) {
                                    pdf_count++;
                                }
                                else if(format[k].equals(".jpg") || format[k].equals(".png") || format[k].equals(".bmp")) {
                                    image_count++;
                                }
                            }
                        }
                    }

                    // 아까 기본 path의 폴더안에 다시 폴더가 있는 경우, 새로운 어레이리스트에 절대경로를 넣어놨음
                    // 그걸 이용해서 다시 해당 폴더 안에 원하는 format의 파일이 있는지 확인하여, 어레이 리스트에 파일 이름 추가
                    for(int p=0; p<mFolderNames.size(); p++) {
                        File files_2 = new File(mFolderNames.get(p));

                        if(files_2.isDirectory() && !files_2.getName().equals(".thumbnails")) {

                            File[] fileList_2 = files_2.listFiles();

                            if(fileList_2.length > 0) {

                                for(int i = 0 ; i<fileList_2.length ; i++) {
                                    File file = fileList_2[i];

                                    for(int k=0; k <format.length; k++) {
                                        if(file.getName().endsWith(format[k])) {
                                            mFileNames.add(file.getName());
                                            Log.d("format_search_", format[k] +"_file_name: "+file.getName());

                                            if(format[k].equals(".pdf")) {
                                                pdf_count++;
                                            }
                                            else if(format[k].equals(".jpg") || format[k].equals(".png") || format[k].equals(".bmp")) {
                                                image_count++;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }


                }
            }
        }
    }
}
