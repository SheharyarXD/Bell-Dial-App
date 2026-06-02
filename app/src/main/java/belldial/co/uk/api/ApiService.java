package belldial.co.uk.api;

import com.google.gson.JsonObject;
import java.util.Map;
import belldial.co.uk.api.model.AddTokenResponse;
import belldial.co.uk.api.model.CheckNumberResponse;
import belldial.co.uk.api.model.GetDetailsResponse;
import belldial.co.uk.api.model.GetSmsDetailsResponse;
import belldial.co.uk.api.model.GetSmsHistoryResponse;
import belldial.co.uk.api.model.SendSmsRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {

    /*@GET("belldialstatus.php")
    Call<String> checkStatus();*/

    @GET("https://belldial.co.uk/API/CheckNumber.php")
    Call<CheckNumberResponse> checkNumber(@HeaderMap Map<String, String> headers);

    @GET("https://belldial.co.uk/API/GetDetails.php")
    Call<GetDetailsResponse> getDetails(@HeaderMap Map<String, String> headers);

    @Headers({
        "Content-Type: application/json; charset=utf-8",
        "Authorization: olDWkvHpLORVvNDVgINWRxaNCjhRDWfW"
    })
    @POST("https://belldial.co.uk/token/token.php")
    // Call<AddTokenResponse> addBelldialToken(@QueryMap Map<String, String> headers);
    Call<AddTokenResponse> addBelldialToken(@Body JsonObject jsonObject);

    @Headers({
        "Content-Type: application/json; charset=utf-8",
        "Authorization: olDWkvHpLORVvNDVgINWRxaNCjhRDWfW"
    })
    @HTTP(method = "DELETE", path = "https://belldial.co.uk/token/token.php", hasBody = true)
    Call<AddTokenResponse> deleteBelldialToken(@Body JsonObject jsonObject);

    @GET("https://belldial.co.uk/API/SMSHistory.php")
    Call<GetSmsHistoryResponse> getSmsHistory(@HeaderMap Map<String, String> headers);

    @POST("https://belldial.co.uk/API/SentSMS.php")
    Call<Void> sendSmsMessage(
            @HeaderMap Map<String, String> headers, @Body SendSmsRequest sendSmsRequest);

    @GET("https://belldial.co.uk/API/SmsDetails.php")
    Call<GetSmsDetailsResponse> getSmsDetails(@HeaderMap Map<String, String> headers);
}
