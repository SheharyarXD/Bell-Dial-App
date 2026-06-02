package belldial.co.uk.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SmsDetails {

    @SerializedName("userId")
    @Expose
    private String userId;

    @SerializedName("creditId")
    @Expose
    private String creditId;

    @SerializedName("send")
    @Expose
    private List<SendModel> sendList;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCreditId() {
        return creditId;
    }

    public void setCreditId(String creditId) {
        this.creditId = creditId;
    }

    public List<SendModel> getSendList() {
        return sendList;
    }

    public void setSendList(List<SendModel> sendList) {
        this.sendList = sendList;
    }
}
