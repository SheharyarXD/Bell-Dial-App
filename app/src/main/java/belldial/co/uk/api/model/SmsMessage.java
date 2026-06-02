package belldial.co.uk.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class SmsMessage {

    @SerializedName("remainingCredit")
    @Expose
    private String remainingCredit;

    @SerializedName("data")
    @Expose
    private List<MessagesResponse> contactList = new ArrayList<>();

    public String getRemainingCredit() {
        return remainingCredit;
    }

    public void setRemainingCredit(String remainingCredit) {
        this.remainingCredit = remainingCredit;
    }

    public List<MessagesResponse> getContactList() {
        return contactList;
    }

    public void setContactList(List<MessagesResponse> contactList) {
        this.contactList = contactList;
    }
}
