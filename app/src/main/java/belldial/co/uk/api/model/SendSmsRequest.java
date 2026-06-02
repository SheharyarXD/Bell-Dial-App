package belldial.co.uk.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class SendSmsRequest implements Serializable {

    @SerializedName("CreditId")
    @Expose
    private String CreditId = "";

    @SerializedName("SenderNumber")
    @Expose
    private String senderNumber = "Adenike";

    @SerializedName("ReceiverNumber")
    @Expose
    private String receiverNumber = "7405046481";

    @SerializedName("UserId")
    @Expose
    private String userId = "376";

    @SerializedName("Message")
    @Expose
    private String message;

    public String getCreditId() {
        return CreditId;
    }

    public void setCreditId(String creditId) {
        CreditId = creditId;
    }

    public String getSenderNumber() {
        return senderNumber;
    }

    public void setSenderNumber(String senderNumber) {
        this.senderNumber = senderNumber;
    }

    public String getReceiverNumber() {
        return receiverNumber;
    }

    public void setReceiverNumber(String receiverNumber) {
        this.receiverNumber = receiverNumber;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
