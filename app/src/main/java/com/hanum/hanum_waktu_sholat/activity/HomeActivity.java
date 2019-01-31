package com.hanum.hanum_waktu_sholat.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import com.hanum.hanum_waktu_sholat.R;
import com.hanum.hanum_waktu_sholat.activity.listsurah.QuranActivity;
import com.hanum.hanum_waktu_sholat.activity.notes.NotesActivity;
import com.hanum.hanum_waktu_sholat.model.Items;
import com.hanum.hanum_waktu_sholat.model.Jadwal;
import com.hanum.hanum_waktu_sholat.modelnote.Note;
import com.hanum.hanum_waktu_sholat.network.ApiClient;
import com.hanum.hanum_waktu_sholat.network.ApiInterface;
import com.hanum.hanum_waktu_sholat.service.AlarmNotifikasi;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {


    String zuhur, ashar, magrib, isya, subuh, tanggal;
    List<Jadwal> jadwal;

    Location location;
    String lokasi;
    ProgressDialog pd;
    @BindView(R.id.tv_tanngal)
    TextView tvTanngal;
    @BindView(R.id.tv_nama_daerah)
    TextView tvNamaDaerah;
    @BindView(R.id.textView2)
    TextView textView2;
    @BindView(R.id.txtSubuh)
    TextView txtSubuh;
    @BindView(R.id.img_subuh)
    ImageView imgSubuh;
    @BindView(R.id.textView4)
    TextView textView4;
    @BindView(R.id.txtDzuhur)
    TextView txtDzuhur;
    @BindView(R.id.img_zuhur)
    ImageView imgZuhur;
    @BindView(R.id.textView5)
    TextView textView5;
    @BindView(R.id.txtAshar)
    TextView txtAshar;
    @BindView(R.id.img_ashar)
    ImageView imgAshar;
    @BindView(R.id.textView6)
    TextView textView6;
    @BindView(R.id.txtMaghrib)
    TextView txtMaghrib;
    @BindView(R.id.img_magrhib)
    ImageView imgMagrhib;
    @BindView(R.id.textView7)
    TextView textView7;
    @BindView(R.id.txtIsya)
    TextView txtIsya;
    @BindView(R.id.img_isya)
    ImageView imgIsya;
    @BindView(R.id.img_arah_kabah)
    CircleImageView imgArahKabah;
    @BindView(R.id.img_al_quran)
    CircleImageView imgAlQuran;
    @BindView(R.id.img_catatan_ramadhan)
    CircleImageView imgCatatanRamadhan;
    @BindView(R.id.swipe_id)
    SwipeRefreshLayout swipeId;
    NestedScrollView idHomeActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.content_home);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        idHomeActivity = (NestedScrollView) findViewById(R.id.id_home_activity);

        //  requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        pd = new ProgressDialog(HomeActivity.this);
        pd.setTitle("Loading . . . ");
        pd.setMessage("Waiting . . .");
        pd.setCancelable(false);
        pd.show();

        actionLoad();
        refresh();

        stopService(new Intent(this,AlarmNotifikasi.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        stopService(new Intent(this,AlarmNotifikasi.class));
    }

    private void refresh() {
        swipeId.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeId.setRefreshing(true);
                actionLoad();
            }
        });

    }

    private void actionLoad() {

        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
        if (!EasyPermissions.hasPermissions(getApplicationContext(), perms)) {
            EasyPermissions.requestPermissions(HomeActivity.this, "Butuh Permission Location", 10, perms);
        } else {


            // GET CURRENT LOCATION
            FusedLocationProviderClient mFusedLocation = LocationServices.getFusedLocationProviderClient(this);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mFusedLocation.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        // Do it all with location
                        Log.d("My Current location", "Lat : " + location.getLatitude() + " Long : " + location.getLongitude());
                        // Display in Toast
                        Geocoder gcd3 = new Geocoder(getBaseContext(), Locale.getDefault());
                        List<Address> addresses;

                        try {
                            addresses = gcd3.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            if (addresses.size() > 0)

                            {
                                //while(locTextView.getText().toString()=="Location") {
                                Log.d("Cek lokasi", "1 :" + addresses.get(0).getLocality().toString());
                                lokasi = addresses.get(0).getLocality().toString();

                                if (lokasi != null) {
                                    Log.d("location", "locatin :" + lokasi);

                                    ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
                                    Call<Items> call = apiInterface.getJadwalSholat(lokasi);
                                    call.enqueue(new Callback<Items>() {
                                        @Override
                                        public void onResponse(Call<Items> call, Response<Items> response) {
                                            Log.d("Data ", " respon" + response.body().getItems());
                                            jadwal = response.body().getItems();
                                            Log.d("respon data ", "" + new Gson().toJson(jadwal));

                                            if (jadwal != null) {
                                                zuhur = jadwal.get(0).getZuhur();
                                                ashar = jadwal.get(0).getAshar();
                                                magrib = jadwal.get(0).getMaghrib();
                                                isya = jadwal.get(0).getIsya();
                                                subuh = jadwal.get(0).getFajar();
                                                tanggal = jadwal.get(0).getTanggal();
                                                Log.d("respon :", "" + zuhur);
                                                txtDzuhur.setText(zuhur);
                                                txtAshar.setText(ashar);
                                                txtMaghrib.setText(magrib);
                                                txtIsya.setText(isya);
                                                txtSubuh.setText(subuh);

                                                cekJamsolat(subuh,zuhur,ashar,magrib,isya);

                                                Log.d("", "lokasi" + lokasi);
                                                tvNamaDaerah.setText(lokasi);
                                                tvTanngal.setText(tanggal);
                                                swipeId.setRefreshing(false);
                                                pd.dismiss();
                                            } else {
                                                Toast.makeText(HomeActivity.this, "Error Network", Toast.LENGTH_SHORT).show();
                                                swipeId.setRefreshing(false);
                                            }
                                            //  loadData(jadwal);
                                        }

                                        @Override
                                        public void onFailure(Call<Items> call, Throwable t) {
                                            Log.d("Data ", "" + t.getMessage());
                                            swipeId.setRefreshing(false);
                                            pd.dismiss();
                                        }
                                    });

                                } /*else {

                Toast.makeText(this, "Swipe Layar Untuk Refresh", Toast.LENGTH_SHORT).show();
                swipeId.setRefreshing(false);
                pd.dismiss();

            }*/


                                // }
                            }

                        } catch (NullPointerException e) {
                            e.printStackTrace();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });


        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (requestCode == 10) {
            actionLoad();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

    @OnClick(R.id.img_subuh)
    public void onImgSubuhClicked() {
        Snackbar.make(idHomeActivity, R.string.coming_soon, Snackbar.LENGTH_LONG).show();
    }

    @OnClick(R.id.img_zuhur)
    public void onImgZuhurClicked() {

        Snackbar.make(idHomeActivity, R.string.coming_soon, Snackbar.LENGTH_LONG).show();
    }

    @OnClick(R.id.img_ashar)
    public void onImgAsharClicked() {

        Snackbar.make(idHomeActivity, R.string.coming_soon, Snackbar.LENGTH_LONG).show();
    }

    @OnClick(R.id.img_magrhib)
    public void onImgMagrhibClicked() {

        Snackbar.make(idHomeActivity, R.string.coming_soon, Snackbar.LENGTH_LONG).show();
    }

    @OnClick(R.id.img_isya)
    public void onImgIsyaClicked() {

        Snackbar.make(idHomeActivity, R.string.coming_soon, Snackbar.LENGTH_LONG).show();
    }

    @OnClick(R.id.img_arah_kabah)
    public void onImgArahKabahClicked() {
        startActivity(new Intent(HomeActivity.this, ArahKiblatActivity.class));
    }

    @OnClick(R.id.img_al_quran)
    public void onImgAlQuranClicked() {
        startActivity(new Intent(this, QuranActivity.class));
    }


    @OnClick(R.id.img_catatan_ramadhan)
    public void onImgCatatanRamadhanClicked() {
        startActivity(new Intent(this, NotesActivity.class));
    }

    private void cekJamsolat(String subuh, String zuhur, String ashar, String magrib, String isya){

        String format = "h:mm aa";
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat mdformat = new SimpleDateFormat(format);
        String strDate = mdformat.format(calendar.getTime());
        String coba = "10:45 pm"; //buat coba suara azan

        if (subuh != null && zuhur != null && ashar != null && magrib != null && isya != null){
            if (subuh.equals(strDate) || zuhur.equals(strDate) || ashar.equals(strDate) || magrib.equals(strDate) || isya.equals(strDate)){
                startService(new Intent(this,AlarmNotifikasi.class));
            }else {
                stopService(new Intent(this,AlarmNotifikasi.class));
            }
        }
    }
}
