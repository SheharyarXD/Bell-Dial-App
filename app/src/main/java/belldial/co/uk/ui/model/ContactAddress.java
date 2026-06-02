package belldial.co.uk.ui.model;

import belldial.co.uk.contacts.LinphoneContact;

public class ContactAddress {
    public LinphoneContact contact;
    public String address;

    public ContactAddress(LinphoneContact c, String a) {
        this.contact = c;
        this.address = a;
    }
}
