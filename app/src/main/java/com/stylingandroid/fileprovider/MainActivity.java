package com.stylingandroid.fileprovider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
    private static final String ASSET_NAME = "pdf-sample.pdf";
    public static final int BUFFER_SIZE = 1024;
    private File cacheFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button shareButton = (Button) findViewById(R.id.share);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share();
            }
        });
    }

    void share() {
        if (cacheFileDoesNotExist()) {
            createCacheFile();
        }
        Uri uri = FileProvider.getUriForFile(this, getPackageName(), cacheFile);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }

    private boolean cacheFileDoesNotExist() {
        if (cacheFile == null) {
            cacheFile = new File(getCacheDir(), ASSET_NAME);
        }
        return !cacheFile.exists();
    }

    private void createCacheFile() {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = getAssets().open(ASSET_NAME);
            outputStream = new FileOutputStream(cacheFile);
            byte[] buf = new byte[BUFFER_SIZE];
            int len;
            while ((len = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(inputStream);
            close(outputStream);
        }
    }

    private void close(@Nullable Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
