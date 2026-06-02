package belldial.co.uk.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetSmsDetailsResponse {

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("message")
    @Expose
    private SmsDetails smsDetails;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public SmsDetails getSmsDetails() {
        return smsDetails;
    }

    public void setSmsDetails(SmsDetails smsDetails) {
        this.smsDetails = smsDetails;
    }
}
