package com.theeaglehaslanded.goalazo.network.request;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.theeaglehaslanded.goalazo.R;
import com.theeaglehaslanded.goalazo.view.Submissions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DownloadStream {

    private final String VIDEO_FOLDER = "FootballGIFs";
    private String URL;
    private String title;
    private int position;
    private Submissions submissions;
    protected Context context;
    private File rootDirectory;
    private String modURL;
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    private int id;
    private int value;
    private long currentDownloadTicks;
    private long mDownloadTicks;
    private File filepath;


    public DownloadStream(String URL, String title, final int position,  final Submissions submissions, final Context context) {
        this.URL = URL;
        this.title = title;
        this.position = position;
        this.submissions = submissions;
        this.context = context;
//        String modVar = URL.substring(URL.lastIndexOf("/") + 1).trim();
//        modURL = "https://cdn.streamable.com/video/mp4/" + modVar + ".mp4";
        modURL = URL + ".mp4";
        filepath = Environment.getExternalStorageDirectory();
        rootDirectory = new File(filepath, VIDEO_FOLDER);
        if (!rootDirectory.exists()) {
            rootDirectory.mkdir();
        }
        showProgress();
        Toast.makeText(context, R.string.downloading_file_toast, Toast.LENGTH_LONG).show();
        DownloadObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(rootDirectory.getPath()));
//                        intent.setAction(android.content.Intent.ACTION_VIEW);
//                        File file = new File(rootDirectory.getPath());
                        intent.setDataAndType(Uri.parse(rootDirectory.getPath()), "video/*");
                        PendingIntent pIntent = PendingIntent.getActivity(context,
                                position, intent, 0);

                        mBuilder.setContentText("Download completed")
                                .setProgress(0, 0, false)
                                .setOngoing(false)
                                .setContentInfo("finished")
                                .setContentIntent(pIntent);
                        mNotifyManager.notify(id, mBuilder.build());
                        Toast.makeText(context, R.string.download_completed_toast, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(String s) {
                    }
                });
    }


    private Observable<String> DownloadObservable(){
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                java.net.URL url;
                int contentLength;
                int counter = 0;
                try {
                    url = new URL(modURL);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    contentLength = connection.getContentLength();
                    connection.connect();
                    for(int i = 0; i<title.length(); i++){
                        if(!Character.isLetter(title.charAt(i)) && !Character.isDigit(title.charAt(i))
                                && !Character.isWhitespace(title.charAt(i))){
                            title = title.replace(title.charAt(i), 'l');
                        }
                    }
                    File file = new File(rootDirectory, title + ".mp4");
//                    if(!file.exists()) {
//                        file.createNewFile();
//                    }
                    InputStream inputStream = connection.getInputStream();
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    mDownloadTicks = 0;
                    byte buffer[] = new byte[1024];
                    int byteCount = 0;
                    while ((byteCount = inputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, byteCount);
                        counter = byteCount + counter;
                        currentDownloadTicks = System.currentTimeMillis();
                        if (currentDownloadTicks > mDownloadTicks + 1500) {
                            value = (Math.abs(counter) * 100) / contentLength;
                            mBuilder.setProgress(100, value, false);
                            mBuilder.setContentInfo(String.valueOf(value) + "%");
                            mNotifyManager.notify(id, mBuilder.build());
                            mDownloadTicks = currentDownloadTicks;
                        }
                    }

                    fileOutputStream.close();
                    subscriber.onCompleted();
                } catch (IOException e) {
                    subscriber.onError(e);
                    e.printStackTrace();
                    Toast.makeText(context, R.string.download_failed
                            , Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    void showProgress(){
        id=position;
        mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setSummaryText(title);
            mBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.mipmap.ic_download_notification)
                    .setOngoing(true)
                    .setStyle(inboxStyle)
                    .setContentTitle(title);
    }

}
