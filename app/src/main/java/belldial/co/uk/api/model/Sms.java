package belldial.co.uk.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Sms implements Serializable {

    @SerializedName("customer_id")
    @Expose
    private String customerId;

    @SerializedName("credit_amount")
    @Expose
    private String creditAmount;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("msg_use")
    @Expose
    private String msgUse;

    @SerializedName("sender_number")
    @Expose
    private String senderNumber;

    @SerializedName("receiver_number")
    @Expose
    private String receiverNumber;

    @SerializedName("created_date")
    @Expose
    private String createdDate;

    @SerializedName("status")
    @Expose
    private String status = "sent";

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCreditAmount() {
        return creditAmount;
    }

    public void setCreditAmount(String creditAmount) {
        this.creditAmount = creditAmount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMsgUse() {
        return msgUse;
    }

    public void setMsgUse(String msgUse) {
        this.msgUse = msgUse;
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

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
