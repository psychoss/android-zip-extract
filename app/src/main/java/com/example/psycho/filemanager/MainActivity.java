package com.example.psycho.filemanager;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class MainActivity extends Activity {

    private static final char[] ILLEGAL_CHARACTERS = {'/', '\n', '\r', '\t', '\0', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':'};
    private static final String PARENT = "/storage/sdcard0/网页";
    private static final String ZIP = "/storage/sdcard0/zip";
    private static final String DEL = "/storage/sdcard0";

    Button zipButton;
    Button reButton;
    Button deButton;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        zipButton = (Button) findViewById(R.id.zipButton);
        reButton = (Button) findViewById(R.id.reButton);
        deButton = (Button) findViewById(R.id.deButton);
        editText = (EditText) findViewById(R.id.editText);
        zipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String f = editText.getText().toString();
                if (f.length() < 1) {
                    f = ZIP;
                }
                listFilesForFolder(new File(f));
            }
        });
        deButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String f = editText.getText().toString();
                if (f.length() < 1) {
                    f = DEL;
                }
                deleteEmpty(new File(f));
            }
        });
        reButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File folder = new File(PARENT);
                for (final File fileEntry : folder.listFiles()) {
                    if (fileEntry.isFile()) {

                        try {
                            renameFile(fileEntry);
                        } catch (Exception e) {
                        }
                    }
                }
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

    public static String readFile(String filename) {
        String content = null;
        File file = new File(filename); //for ex foo.txt
        FileReader reader = null;
        try {
            reader = new FileReader(file);
            char[] chars = new char[(int) file.length()];
            reader.read(chars);
            content = new String(chars);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {

                }
            }
        }
        return content;
    }

    public static String getTitle(String content) {
        Pattern p = Pattern.compile("<head>.*?<title>(.*?)</title>.*?</head>", Pattern.DOTALL);
        Matcher m = p.matcher(content);
        String title = "";
        while (m.find()) {
            title = m.group(1);
        }
        return title;
    }

    public static String getFileName(String title) {
        String fileName = PARENT + "/";

        for (char c : ILLEGAL_CHARACTERS) {
            title = title.replace(c, ' ');
        }
        fileName = fileName + title + ".mhtml";
        fileName = uniqueName(fileName);

        return fileName;
    }

    public static void renameFile(File file) {
        String target = file.getAbsolutePath();
        String content = readFile(target);
        Log.e("target=>", target);

        String title = getTitle(content);
        String f = getFileName(title);
        Log.e("f=>", f);
        file.renameTo(new File(f));

    }

    public static void deleteEmpty(File folder) {
        for (final File fileEntry : folder.listFiles()) {
            Log.e("f", fileEntry.getName());
            if (fileEntry.isDirectory()) {
                if (fileEntry.listFiles() == null) {
                    fileEntry.delete();
                } else if (fileEntry.listFiles().length < 1) {
                    fileEntry.delete();

                }

            }
        }
    }
}
