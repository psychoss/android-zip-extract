package com.example.psycho.filemanager;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class MainActivity extends Activity {

    Button zipButton;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        zipButton = (Button) findViewById(R.id.zipButton);
        editText = (EditText) findViewById(R.id.editText);
        zipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listFilesForFolder(new File(editText.getText().toString()));
            }
        });
    }


    public static void unzip(String zipFile, String location) throws IOException {
        try {


            ZipInputStream zin = new ZipInputStream(new FileInputStream(zipFile));
            try {
                ZipEntry ze = null;
                while ((ze = zin.getNextEntry()) != null) {


                    String ext = extension(ze.getName());
                    if (ext.length() == 0) {
                        continue;
                    }
                    if (ext.equals("xhtml") || ext.equals("xml") || ext.equals("html") || ext.equals("htm")) {

                        String path = location + "/" + name(ze.getName());

                        path = uniqueName(path);

                        FileOutputStream fout = new FileOutputStream(path, false);
                        System.out.println("File" + path);
                        try {
                            for (int c = zin.read(); c != -1; c = zin.read()) {
                                fout.write(c);
                            }
                            fout.flush();
                            zin.closeEntry();
                        } finally {
                            fout.close();

                        }

                    }

                }
            } finally {
                zin.close();
            }
        } catch (Exception e) {
            System.out.println(e);

        }
    }

    public static String uniqueName(String pat) {
        String result = pat;
        File f = new File(pat);
        int count = 0;
        while (f.exists()) {
            count++;
            result = pat.replace(".", Integer.toString(count) + ".");
            f = new File(result);
        }
        return result;
    }

    public static String createByFileName(String fileName) {
        String extension = fileName;

        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(0, i);
        }
        extension = extension.replace(".", "");
        File target = new File(extension);
        if (!target.isDirectory()) {
            target.mkdirs();
        }
        return extension;
    }

    public static String name(String fileName) {
        String extension = fileName;

        int i = fileName.lastIndexOf('/');
        if (i > 0) {
            extension = fileName.substring(i + 1);
        }
        return extension;
    }

    public static String extension(String fileName) {
        String extension = "";

        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i + 1);
        }
        return extension;
    }

    public static void listFilesForFolder(final File folder) {
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry);
            } else {
                String absolute = fileEntry.getAbsolutePath();
                try {
                    unzip(absolute, createByFileName(absolute));
                } catch (Exception e) {
                }

            }
        }
    }


}
