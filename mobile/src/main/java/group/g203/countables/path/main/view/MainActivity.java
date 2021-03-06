package group.g203.countables.path.main.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import butterknife.Bind;
import butterknife.ButterKnife;
import group.g203.countables.R;
import group.g203.countables.base.manager.BasePresenterManager;
import group.g203.countables.base.presenter.BasePresenter;
import group.g203.countables.custom_view.loading_view.LoadingAspect;
import group.g203.countables.path.main.presenter.MainPresenter;

public class MainActivity extends AppCompatActivity implements MainView, DataApi.DataListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    @Bind(R.id.loading_aspect)
    LoadingAspect mLoadingAspect;
    @Bind(R.id.rvCountables)
    public RecyclerView mCountablesRv;
    @Bind(R.id.etCountable)
    public EditText mCountableField;
    @Bind(R.id.ivAddCountable)
    public ImageView mAddCountable;
    @Bind(R.id.clSnack)
    public CoordinatorLayout clSnack;
    public View mView;
    public GoogleApiClient mClient;
    public Node mNode;
    MainPresenter mPresenter;
    Bundle mStateBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStateBundle = savedInstanceState;
    }

    protected void onNewIntent(Intent intent) {
        this.setIntent(intent);
        MainPresenter presenter = (BasePresenterManager.getInstance().getMainPresenter());
        if (presenter != null) {
            setPresenter(presenter);
        } else {
            setPresenter(new MainPresenter());
        }
        if (intent != null && !TextUtils.isEmpty(intent.getAction()) && intent.getAction().contains(getString(R.string.wear_connection))) {
            mPresenter.displayToast(intent.getAction());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_sort) {
            displaySortDialog();
        } else if (id == R.id.action_info) {
            displayInfoDialog();
        } else if (id == R.id.action_credits) {
            displayCreditsDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.activity_main, null, false);
        setContentView(mView);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        setPresenterFromState(mStateBundle);
        if (mClient != null && !mClient.isConnected() && !mClient.isConnecting()) {
            mClient.connect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setEmptyParams();
        handleContentDisplay();
        mPresenter.onGoogleApiConnected();
    }

    @Override
    protected void onPause() {
        super.onPause();
        BasePresenterManager.getInstance().savePresenter(mPresenter);
        if (mClient != null) {
            Wearable.DataApi.removeListener(mClient, this);
        }
    }

    @Override
    public void handleContentDisplay() {
        mPresenter.handleInitialContentDisplay();
    }

    @Override
    public void displayInfoDialog() {
        mPresenter.displayInfoDialog(getSupportFragmentManager());
    }

    @Override
    public void displaySortDialog() {
        mPresenter.displaySortDialog(getSupportFragmentManager());
    }

    @Override
    public void displayCreditsDialog() {
        mPresenter.displayCreditsDialog(getSupportFragmentManager());
    }

    void setPresenterFromState(Bundle savedState) {
        if (savedState != null) {
            setPresenter(BasePresenterManager.getInstance().getPresenter(savedState));
        } else {
            setPresenter(new MainPresenter());
            Intent intent = getIntent();
            if (intent != null && !TextUtils.isEmpty(intent.getAction()) && intent.getAction().contains(getString(R.string.wear_connection))) {
                mPresenter.displayToast(intent.getAction());
            }
        }
    }

    @Override
    public <P extends BasePresenter> void setPresenter(P presenter) {
        if (presenter instanceof MainPresenter) {
            mPresenter = (MainPresenter) presenter;
            mPresenter.bindViews(this, mLoadingAspect);
        } else {
            mPresenter.displaySnackbarMessage(getString(R.string.general_error));
        }
    }

    @Override
    public MainPresenter getPresenter() {
        return mPresenter;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        BasePresenterManager.getInstance().savePresenter(mPresenter, outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
    }


    public void setEmptyParams() {
        mPresenter.setEmptyIcon(R.mipmap.ic_empty_file);
        mPresenter.setEmptyMessage(getString(R.string.no_countables));
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mPresenter.onGoogleApiConnected();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        mPresenter.dataChanged(dataEvents);
    }
}
