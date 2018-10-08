package com.andreapetreti.subspedia.common;

import com.andreapetreti.subspedia.model.Serie;
import com.andreapetreti.subspedia.model.SerieTranslating;
import com.andreapetreti.subspedia.model.Subtitle;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SubspediaService {

    @GET("elenco_serie")
    Call<List<Serie>> getAllSeries();

    @GET("serie_traduzione")
    Call<List<SerieTranslating>> getAllTranslatingSeries();

    @GET("sottotitoli_serie")
    Call<List<Subtitle>> getSubtitlesOf(@Query("serie") int idSerie);

    @GET("ultimi_sottotitoli")
    Call<List<Subtitle>> getLastSubtitles();


    public abstract class Provider {

        private static final  String BASE_URL = "https://www.subspedia.tv/API/";

        private static SubspediaService instance;

        public static SubspediaService getInstance() {
            if(instance == null) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                instance = retrofit.create(SubspediaService.class);
            }
            return instance;
        }
    }
}
