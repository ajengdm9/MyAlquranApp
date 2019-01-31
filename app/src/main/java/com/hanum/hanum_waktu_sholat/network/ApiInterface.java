package com.hanum.hanum_waktu_sholat.network;

import com.hanum.hanum_waktu_sholat.model.Items;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;


public interface ApiInterface {

    @GET("{periode}/daily.json")
    Call<Items> getJadwalSholat(@Path("periode") String periode);
}
