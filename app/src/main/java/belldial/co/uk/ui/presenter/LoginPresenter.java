package belldial.co.uk.ui.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.gson.JsonObject;
import java.util.HashMap;
import belldial.co.uk.api.ApiService;
import belldial.co.uk.api.RetroClient;
import belldial.co.uk.api.model.AddTokenResponse;
import belldial.co.uk.api.model.CheckNumberResponse;
import belldial.co.uk.api.model.GetDetailsResponse;
import belldial.co.uk.ui.base.BasePresenter;
import belldial.co.uk.ui.views.LoginView;
import belldial.co.uk.utils.Constant;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginPresenter extends BasePresenter<LoginView> {

    @Override
    public void resume() {}

    @Override
    public void pause() {}

    @Override
    public void destroy() {}

    public void checkNumber(String Number) {
        view.showLoader();
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authkey", "+<?=CPBw3|RmG=SMBo]H=[;b&X9U5r1rb*z]m1uI[%q4nXV;#4NGQ@D&RXiTtN8A");
        headers.put("Phonenumber", Number);

        ApiService api = RetroClient.getApiService();
        retrofit2.Call<CheckNumberResponse> call = api.checkNumber(headers);
        call.enqueue(
                new Callback<CheckNumberResponse>() {
                    @Override
                    public void onResponse(
                            retrofit2.Call<CheckNumberResponse> call,
                            Response<CheckNumberResponse> response) {

                        Log.d("LoginFragment", "onResponse: " + response.body().getStatus());
                        if (response.body().getStatus().equalsIgnoreCase("Success")) {
//                            view.hideLoader();
                            view.setVerification();
                        } else {
                            view.hideLoader();
                            view.numberNotRegisterd();
                        }
                    }

                    @Override
                    public void onFailure(
                            retrofit2.Call<CheckNumberResponse> call, Throwable throwable) {
                        view.hideLoader();
                        Log.e("LoginFragment", "Reason: " + throwable.getMessage());
                        Log.e("onFailure", "Reason: " + throwable.getMessage());
                    }
                });
    }

    public void getDetails(Activity activity, String Number) {
        view.showLoader();
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authkey", "+<?=CPBw3|RmG=SMBo]H=[;b&X9U5r1rb*z]m1uI[%q4nXV;#4NGQ@D&RXiTtN8A");
        headers.put("Phonenumber", Number);

        ApiService api = RetroClient.getApiService();
        retrofit2.Call<GetDetailsResponse> call = api.getDetails(headers);
        call.enqueue(
                new Callback<GetDetailsResponse>() {
                    @Override
                    public void onResponse(
                            retrofit2.Call<GetDetailsResponse> call,
                            Response<GetDetailsResponse> response) {
//                        view.hideLoader();
                        if (response.body().getStatus().equalsIgnoreCase("Success")) {
                            view.setDetails(response.body().getMessage());
                            SharedPreferences sharedPreferences =
                                    activity.getSharedPreferences(
                                            Constant.SHARED_PREF_APP, Context.MODE_PRIVATE);
                            sharedPreferences.edit().putString(
                                            Constant.ORDER_ID,
                                            response.body().getMessage().getOrderId())
                                    .apply();
                            sharedPreferences.edit().putString(
                                            Constant.PRODUCT_ID,
                                            response.body()
                                                    .getMessage()
                                                    .getProducts()
                                                    .get(0)
                                                    .getId())
                                    .apply();
                            view.hideLoader();
                        } else {
                            view.hideLoader();
                            view.detailsNotFound();
                        }
                    }

                    @Override
                    public void onFailure(
                            retrofit2.Call<GetDetailsResponse> call, Throwable throwable) {
                        view.hideLoader();
                        Log.e("onFailure", "Reason: " + throwable.getMessage());
                    }
                });
    }

    public void addpushtoken(String Number, String token) {
        // view.showLoader();
        /*HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("username", Number);
        headers.put("pushtoken", token);
        headers.put("platform", "android");*/

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("User", Number);
        headers.put("PnTok", token);
        headers.put("PnTtype", "android");

        ApiService api = RetroClient.getApiService();
        // retrofit2.Call<AddTokenResponse> call = api.addPushToken(headers);
        JsonObject data = new JsonObject();
        data.addProperty("User", Number);
        data.addProperty("PnTok", token);
        data.addProperty("PnTtype", "android");
        retrofit2.Call<AddTokenResponse> call = api.addBelldialToken(data);
        call.enqueue(
                new Callback<AddTokenResponse>() {
                    @Override
                    public void onResponse(
                            retrofit2.Call<AddTokenResponse> call,
                            Response<AddTokenResponse> response) {
                        // view.hideLoader();
                        /*  if (response.body().getStatus().equalsIgnoreCase("Success")) {
                            view.setDetails(response.body().getMessage());
                        } else {
                            view.detailsNotFound();
                        }*/
                        Log.e("onSuccess", "onSuccess: " + response.body().toString());
                    }

                    @Override
                    public void onFailure(
                            retrofit2.Call<AddTokenResponse> call, Throwable throwable) {
                        // view.hideLoader();
                        Log.e("onFailure", "Reason: " + throwable.getMessage());
                    }
                });
    }
}
