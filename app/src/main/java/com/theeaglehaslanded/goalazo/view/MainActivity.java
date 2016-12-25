package com.theeaglehaslanded.goalazo.view;

import android.Manifest;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubInterstitial;
import com.startapp.android.publish.SDKAdPreferences;
import com.startapp.android.publish.StartAppAd;
import com.startapp.android.publish.StartAppSDK;
import com.theeaglehaslanded.goalazo.R;
import com.theeaglehaslanded.goalazo.di.components.DaggerMainComponent;
import com.theeaglehaslanded.goalazo.di.components.MainComponent;
import com.theeaglehaslanded.goalazo.di.modules.MainModule;
import com.theeaglehaslanded.goalazo.presenters.MainPresenterImpl;
import com.theeaglehaslanded.goalazo.utils.Credentials;
import com.theeaglehaslanded.goalazo.utils.EndlessRecyclerOnScrollListener;
import com.theeaglehaslanded.goalazo.utils.NetworkState;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class MainActivity extends AppCompatActivity implements MainView {

    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    @Bind(R.id.refresh_bar)
    SmoothProgressBar smoothProgressBar;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.refresh_bar_bottom)
    SmoothProgressBar smoothProgressBarBottom;
    @Bind(R.id.reddit_error)
    TextView redditErrorTextView;
    @Inject
    SharedPreferences sharedPreferences;
    @Inject
    MainPresenterImpl mainPresenter;
    LinearLayoutManager layoutManager;
    FragmentManager fragmentManager;
    private MainComponent mComponent;
    InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        StartAppSDK.init(this, Credentials.STARTAPPID, new SDKAdPreferences()
//                .setAge(25)
//                .setGender(SDKAdPreferences.Gender.MALE), true);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            Window w = getWindow();
//            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        }
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(Credentials.admobID);
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                finish();
            }

        });

        requestNewInterstitial();
        mComponent = DaggerMainComponent.builder()
                .mainModule(new MainModule(this, this))
                .build();
        mComponent.inject(this);
        redditErrorTextView.setVisibility(View.GONE);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.app_name);
        checkForPermissions();
        fragmentManager = getFragmentManager();
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        smoothProgressBarBottom.setVisibility(View.GONE);
        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                smoothProgressBarBottom.setVisibility(View.VISIBLE);
                smoothProgressBarBottom.progressiveStart();
                int lastFirstVisiblePosition =
                        ((LinearLayoutManager) recyclerView.getLayoutManager())
                                .findFirstVisibleItemPosition();
                recyclerView.getLayoutManager().scrollToPosition(lastFirstVisiblePosition);
                if(NetworkState.isNetworkAvailable(getApplicationContext())) {
                    mainPresenter.loadMore();
                }else{
                    NetworkState.NetworkUnavailableToast(getApplicationContext());
                    hideRefreshLayout();
                }
            }
        });
        if(savedInstanceState!=null){
            mainPresenter.onRestoreInstanceState(savedInstanceState);
            hideRefreshLayout();
        }
        else {
            if(NetworkState.isNetworkAvailable(this)) {
                mainPresenter.initiateNetworkOperations(false);
            }else{
                NetworkState.NetworkUnavailableToast(this);
                hideRefreshLayout();
            }
        }
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice(Credentials.deviceId)
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    private void checkForPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
        }
    }


    @Override
    public void startRefresh() {
        smoothProgressBar.setVisibility(View.VISIBLE);
        showRefreshLayout();
        EndlessRecyclerOnScrollListener.previousTotal = 0;
        redditErrorTextView.setVisibility(View.GONE);
        mainPresenter.initiateNetworkOperations(true);
    }

    @Override
    public void showRefreshLayout() {
        smoothProgressBar.progressiveStart();
    }

    @Override
    public void hideRefreshLayout() {
        smoothProgressBar.setVisibility(View.GONE);
        smoothProgressBar.progressiveStop();
    }

    @Override
    public void setAdapterForRecyclerView(RecyclerView.Adapter adapter) {
        Log.d("MainActivity", "Adapter attached");
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void startVideoFragment(String Url) {
        if(NetworkState.isNetworkAvailable(this)) {
//            Bundle bundle = new Bundle();
//            bundle.putString("URL", Url);
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            VideoPlayerFragment videoPlayerFragment = new VideoPlayerFragment();
//            videoPlayerFragment.setArguments(bundle);
//            fragmentTransaction.addToBackStack(null);
//            fragmentTransaction.replace(R.id.activity_main, videoPlayerFragment);
//            fragmentTransaction.commit();
            Intent intent = new Intent(getBaseContext(), VideoActivity.class);
            intent.putExtra("URL", Url);
            startActivity(intent);
        }else{
            NetworkState.NetworkUnavailableToast(this);
        }
    }

    @Override
    public void stopProgressBarBottom() {
        smoothProgressBarBottom.progressiveStop();
        smoothProgressBarBottom.setVisibility(View.GONE);
    }

    @Override
    public void redditError() {
        redditErrorTextView.setVisibility(View.VISIBLE);
    }


    @Override
    public void onDestroy() {
//        mInterstitial.destroy();
        super.onDestroy();
        mainPresenter.onDestroy();
    }

    void refreshCheck(){
        if(NetworkState.isNetworkAvailable(this)) {
            startRefresh();
        }else{
            NetworkState.NetworkUnavailableToast(this);
            hideRefreshLayout();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mainPresenter.onSaveInstanceState(outState);
    }



    @Override
    public MainComponent getMainComponent() {
        return mComponent;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

//        if (id == R.id.action_settings) {
//            return true;
//        }

        if (id == R.id.check_for_updates){
            Uri uri = Uri.parse( "https://www.goalazo.xyz" );
            startActivity( new Intent(Intent.ACTION_VIEW, uri));
        }
        if(id == R.id.refresh){
            refreshCheck();
        }
        if (id == R.id.open_source_licenses) {
            LicenseDialog licenseDialog = new LicenseDialog();
            licenseDialog.show(this.getSupportFragmentManager(), "");
        }

        return super.onOptionsItemSelected(item);

    }



    @Override
    public void onBackPressed() {

        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            finish();
        }
//        if (mInterstitial.isReady()) {
//            mInterstitial.show();
//        }
//        if (fragmentManager.getBackStackEntryCount() != 0) {
//            StartAppAd.onBackPressed(this);
//            fragmentManager.popBackStack();
//        } else {
//            StartAppAd.onBackPressed(this);
//            Log.d("MainActivity", "Back pressed");
//            super.onBackPressed();
//        }
    }

}
