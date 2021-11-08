package fr.isep.ii3510.currencyconverter_kyleliuxianqi;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {

    private EditText et_EnterAmount;
    private TextView tv_CovertResult;

    private final ArrayList<String> symbolsList = new ArrayList<>();

    private String currencyFrom = "";
    private String currencyTo = "";
    private ArrayAdapter<String> adapterFrom, adapterTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        initSpinnerItems();

    }

    private void init(){
        et_EnterAmount = (EditText) findViewById(R.id.et_EnterAmount);
        //et_EnterAmount.setLayerType(View.LAYER_TYPE_HARDWARE,null);
        Spinner sp_From = (Spinner) findViewById(R.id.sp_From);
        Spinner sp_To = (Spinner) findViewById(R.id.sp_To);
        tv_CovertResult = (TextView) findViewById(R.id.tv_ConvertResult);

        adapterFrom = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, symbolsList);
        adapterFrom.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_From.setAdapter(adapterFrom);
        sp_From.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currencyFrom = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        adapterTo = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, symbolsList);
        adapterTo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_To.setAdapter(adapterTo);
        sp_To.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currencyTo = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void initSpinnerItems(){
        ApiFetcher.getInstance().getSymbols(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try(ResponseBody responseBody = response.body()){
                    assert responseBody != null;
                    String symbolsJson = responseBody.string();
                    System.out.println(symbolsJson);
                    Gson gson = new Gson();
                    HashMap<String,String> map = gson.fromJson(symbolsJson, HashMap.class);
                    symbolsList.addAll(map.keySet());
                    final ArrayList<String> list = new ArrayList<>(symbolsList);
                    runOnUiThread(() -> {
                        adapterFrom.clear();
                        adapterFrom.addAll(list);
                        adapterFrom.notifyDataSetChanged();
                        adapterTo.clear();
                        adapterTo.addAll(list);
                        adapterTo.notifyDataSetChanged();
                    });
                }
            }
        });
    }

    public void onConvert(View view){
        String amount = et_EnterAmount.getText().toString();
        ApiFetcher.getInstance().getConvert(amount, currencyFrom, currencyTo,new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try (ResponseBody responseBody = response.body()){
                    assert responseBody != null;
                    String resultJson = responseBody.string();
                    System.out.println(resultJson);
                    final String strResult = new JSONObject(resultJson).getJSONObject("rates").getString(currencyTo);
                    System.out.println(strResult);
                    runOnUiThread(() -> tv_CovertResult.setText(strResult));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}