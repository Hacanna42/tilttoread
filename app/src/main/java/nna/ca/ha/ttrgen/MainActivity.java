package nna.ca.ha.ttrgen;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.imageView)
    ImageView img;
    @BindView(R.id.verEd)
    EditText verEd;
    @BindView(R.id.horEd)
    EditText horEd;
    @BindView(R.id.apply)
    Button apply;
    @BindView(R.id.save)
    Button save;
    @BindView(R.id.mess)
    SeekBar mess;

    private Rect r = new Rect();
    Canvas canvas;
    Paint paint;
    Bitmap bitmap;

    boolean temp = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        bitmap = Bitmap.createBitmap(1080, 1080, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);

        img.setImageBitmap(bitmap);

        paint = new Paint();
        paint.setColor(Color.BLACK);

        paint.setStrokeWidth(5f);
        paint.setTextSize(1000);
        paint.setTextScaleX(0.05f);
    }

    @OnClick(R.id.apply)
    public void apply() {
        requestPermission();
        temp = true;
        canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        drawCenter(canvas, paint, verEd.getText().toString());
        canvas.save();
        canvas.rotate(
                90, // degrees
                canvas.getWidth() / 2, // px, center x
                canvas.getHeight() / 2 // py, center y
        );
        drawCenter(canvas, paint, horEd.getText().toString());
        canvas.save();
        for (int j=1;j<=mess.getProgress();j++) {
            for (int i = 1; i < 25; i++) {
                int grid = (int) (Math.random() * 50) + 45;
                int grid2 = (int) (Math.random() * 50) + 45;
                canvas.drawLine(0, i * grid, 1080, i * grid2, paint);
                grid = (int) (Math.random() * 50) + 45;
                grid2 = (int) (Math.random() * 50) + 45;
                canvas.drawLine(i * grid, 0, i * grid2, 1080, paint);
            }
        }

//        for (int i = 1; i <= mess.getProgress(); i++) {
//            int x = (int)(Math.random() * 1080);
//            int x2 = (int)(Math.random() * 1080);
//            int y = (int)(Math.random() * 1080);
//            int y2 = (int)(Math.random() * 1080);
//            canvas.drawLine(x, y, x2, y2, paint);
//        }

        img.invalidate();
    }

    @OnClick(R.id.save)
    public void save() {
        if (temp) {
            try {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                int hour = cal.get(Calendar.HOUR);
                int minute = cal.get(Calendar.MINUTE);
                int second = cal.get(Calendar.SECOND);
                String fileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/TTR_"+year+month+day+hour+minute+second+".png";
                OutputStream stream = new FileOutputStream(fileName);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                stream.close();
                Toast.makeText(this, "사진이 갤러리에 저장되었습니다.", Toast. LENGTH_SHORT).show();
                scanner(fileName);
            } catch (FileNotFoundException e) {
                Toast.makeText(this, "이 기능을 사용하기 위해선 파일 액세스 권한을 허용해야 합니다!", Toast.LENGTH_LONG).show();
                requestPermission();
                e.printStackTrace();
            } catch (IOException e) {
                Toast.makeText(this, "알 수 없는 오류로 인해 사진을 저장하지 못했습니다.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else
            Toast.makeText(this, "글자 적용을 먼저 해주세요.", Toast.LENGTH_SHORT).show();
    }


    private void drawCenter(Canvas canvas, Paint paint, String text) {
        canvas.getClipBounds(r);
        int cHeight = r.height();
        int cWidth = r.width();
        paint.setTextAlign(Paint.Align.LEFT);
        paint.getTextBounds(text, 0, text.length(), r);
        float x = cWidth / 2f - r.width() / 2f - r.left;
        float y = cHeight / 2f + r.height() / 2f - r.bottom;
        canvas.drawText(text, x, y, paint);
    }

    private void scanner(String path) {

        MediaScannerConnection.scanFile(this,
                new String[] { path }, null,
                new MediaScannerConnection.OnScanCompletedListener() {

                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("TAG", "Finished scanning " + path);
                    }
                });
    }

    private void requestPermission() {
        Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED) {
            return;
        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE
        },22);
    }

}
