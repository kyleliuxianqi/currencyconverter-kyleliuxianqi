package fr.isep.ii3510.currencyconverter_kyleliuxianqi;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class ApiFetcher {

    private static ApiFetcher INSTANCE = null;
    private final OkHttpClient client;
    private static final String BASE_URL = "https://api.frankfurter.app/";

    private ApiFetcher() {
        this.client = new OkHttpClient();
    }

    public static ApiFetcher getInstance() {
        if (INSTANCE == null)
        {
            INSTANCE = new ApiFetcher();
        }
        return INSTANCE;
    }

    public void getSymbols (Callback callback){
        Request request = new Request.Builder()
                .url(BASE_URL+"currencies")
                .build();
        System.out.println(request.url());
        Call call = this.client.newCall(request);
        call.enqueue(callback);
    }

    public void getConvert (String amount, String from, String to, Callback callback){
        Request request = new Request.Builder()
                .url(BASE_URL+"latest?amount="+amount+"&from="+from+"&to="+to)
                .build();
        System.out.println(request.url());
        Call call = this.client.newCall(request);
        call.enqueue(callback);
    }

}
