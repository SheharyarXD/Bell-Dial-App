package belldial.co.uk.ui.fragments;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import belldial.co.uk.LinphoneManager;
import belldial.co.uk.ui.base.BaseFragment;
import belldial.co.uk.ui.presenter.SettingsPresenter;
import belldial.co.uk.ui.views.SettingsView;
import belldial.co.uk.settings.LinphonePreferences;
import butterknife.BindView;
import butterknife.OnClick;
import java.util.ArrayList;
import java.util.List;

import belldial.co.uk.R;
import belldial.co.uk.ui.activity.HomeActivity;

import org.linphone.core.AVPFMode;
import org.linphone.core.Account;
import org.linphone.core.AccountParams;
import org.linphone.core.Address;
import org.linphone.core.AuthInfo;
import org.linphone.core.Core;
import org.linphone.core.Factory;
import org.linphone.core.NatPolicy;
import org.linphone.core.TransportType;
import org.linphone.core.tools.Log;

/** A simple {@link Fragment} subclass. */
public class AcoountFragment extends BaseFragment<SettingsPresenter, SettingsView>
        implements SettingsView {

    @BindView(R.id.toolbar_back_title_layout)
    Toolbar toolbar;

    @BindView(R.id.imageViewBack)
    ImageView imageViewBack;

    @BindView(R.id.toolBarTitle)
    AppCompatTextView toolBarTitle;

    @BindView(R.id.imageViewSwitchOff)
    ImageView imageViewSwitchOff;

    @BindView(R.id.layoutAccount)
    LinearLayout layoutAccount;

    @BindView(R.id.et_username)
    AppCompatEditText etUsername;

    @BindView(R.id.et_displayname)
    AppCompatEditText etDisplayname;

    @BindView(R.id.et_userid)
    AppCompatEditText etUserid;

    @BindView(R.id.et_password)
    AppCompatEditText etPassword;

    @BindView(R.id.et_domain)
    AppCompatEditText etDomain;

    @BindView(R.id.et_proxy)
    AppCompatEditText etProxy;

    @BindView(R.id.et_trannsport)
    AppCompatTextView etTrannsport;

    @BindView(R.id.et_stun)
    AppCompatEditText etStun;

    @BindView(R.id.switchICE)
    SwitchCompat switchICE;

    @BindView(R.id.switchOutboundProxy)
    SwitchCompat switchOutboundProxy;

    @BindView(R.id.switchAVPF)
    SwitchCompat switchAVPF;

    @BindView(R.id.et_expire)
    AppCompatEditText etExpire;

    @BindView(R.id.et_country)
    AppCompatEditText etCountry;

    @BindView(R.id.switchPushNotification)
    SwitchCompat switchPushNotification;

    private int mAccountIndex = 0;
    private Account mAccount;
    private AuthInfo mAuthInfo;
    private boolean mIsNewlyCreatedAccount;

    @Override
    protected int createLayout() {
        return R.layout.fragment_account;
    }

    @Override
    protected void setPresenter() {
        presenter = new SettingsPresenter();
    }

    @Override
    protected SettingsView createView() {
        return this;
    }

    @Override
    protected void bindData() {
        imageViewSwitchOff.setVisibility(View.GONE);
        toolBarTitle.setText("Account");
        ((HomeActivity) getActivity()).hideTabBar(true);
        mAccount = null;
        Core core = LinphoneManager.getCore();
        if (mAccountIndex >= 0 && core != null) {
            Account[] accounts = core.getAccountList();
            if (accounts.length > mAccountIndex) {
                mAccount = accounts[mAccountIndex];
                mIsNewlyCreatedAccount = false;
            } else {
                Log.e("[Account Settings] Account not found !");
            }
        }
        etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        initTransportList();
        etUsername.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(
                            CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (editable.toString().isEmpty()) {
                            return;
                        }

                        if (mAuthInfo != null) {
                            mAuthInfo.setUsername(editable.toString());
                        } else {
                            Log.e("[Account Settings] No auth info !");
                        }

                        if (mAccount != null) {
                            AccountParams params = mAccount.getParams().clone();
                            Address identity = params.getIdentityAddress();
                            if (identity != null) {
                                identity.setUsername(editable.toString());
                            }
                            params.setIdentityAddress(identity);
                            mAccount.setParams(params);
                        } else {
                            Log.e("[Account Settings] No account !");
                        }
                    }
                });
        etDomain.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(
                            CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (editable.toString().isEmpty()) {
                            return;
                        }
                        if (editable.toString().contains(":")) {
                            Log.e(
                                    "[Account Settings] Do not specify port information inside domain field !");
                            return;
                        }

                        if (mAuthInfo != null) {
                            mAuthInfo.setDomain(editable.toString());
                        } else {
                            Log.e("[Account Settings] No auth info !");
                        }

                        if (mAccount != null) {
                            AccountParams params = mAccount.getParams().clone();
                            Address identity = params.getIdentityAddress();
                            if (identity != null) {
                                identity.setDomain(editable.toString());
                            }
                            params.setIdentityAddress(identity);
                            mAccount.setParams(params);
                        } else {
                            Log.e("[Account Settings] No account !");
                        }
                    }
                });
        etUserid.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(
                            CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (mAuthInfo != null) {
                            mAuthInfo.setUserid(editable.toString());

                            Core core = LinphoneManager.getCore();
                            if (core != null) {
                                core.refreshRegisters();
                            }
                        } else {
                            Log.e("[Account Settings] No auth info !");
                        }
                    }
                });
        etPassword.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(
                            CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (mAuthInfo != null) {
                            mAuthInfo.setHa1(null);
                            mAuthInfo.setPassword(editable.toString());
                            Core core = LinphoneManager.getCore();
                            if (core != null) {
                                core.addAuthInfo(mAuthInfo);
                                core.refreshRegisters();
                            }
                        } else {
                            Log.e("[Account Settings] No auth info !");
                        }
                    }
                });
        etDisplayname.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(
                            CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (mAccount != null) {
                            AccountParams params = mAccount.getParams().clone();
                            Address identity = params.getIdentityAddress();
                            if (identity != null) {
                                identity.setDisplayName(editable.toString());
                            }
                            params.setIdentityAddress(identity);
                            mAccount.setParams(params);
                        } else {
                            Log.e("[Account Settings] No account !");
                        }
                    }
                });
        etProxy.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(
                            CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (mAccount != null) {
                            AccountParams params = mAccount.getParams().clone();
                            Address proxy = Factory.instance().createAddress(editable.toString());
                            if (proxy != null) {
                                params.setServerAddr(proxy.asString());
                                if (switchOutboundProxy.isChecked()) {
                                    params.setRoutesAddresses(new Address[] {proxy});
                                }
                                etTrannsport.setText(proxy.getTransport().toString());
                            }
                            mAccount.setParams(params);
                        } else {
                            Log.e("[Account Settings] No account !");
                        }
                    }
                });

        etStun.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(
                            CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (mAccount != null) {
                            AccountParams params = mAccount.getParams().clone();
                            NatPolicy natPolicy = params.getNatPolicy();
                            if (natPolicy == null) {
                                Core core = LinphoneManager.getCore();
                                if (core != null) {
                                    natPolicy = core.createNatPolicy();
                                    params.setNatPolicy(natPolicy);
                                }
                            }
                            if (natPolicy != null) {
                                natPolicy.setStunServer(editable.toString());
                            }
                            if (editable.toString() == null || editable.toString().isEmpty()) {
                                switchICE.setChecked(false);
                            }
                            switchICE.setEnabled(
                                    editable.toString() != null && !editable.toString().isEmpty());
                            mAccount.setParams(params);
                        } else {
                            Log.e("[Account Settings] No account !");
                        }
                    }
                });
        etExpire.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(
                            CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (mAccount != null) {
                            AccountParams params = mAccount.getParams().clone();
                            try {
                                params.setExpires(Integer.parseInt(editable.toString()));
                            } catch (NumberFormatException nfe) {
                                Log.e(nfe);
                            }
                            mAccount.setParams(params);
                        } else {
                            Log.e("[Account Settings] No account !");
                        }
                    }
                });
        etCountry.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(
                            CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (mAccount != null) {
                            AccountParams params = mAccount.getParams().clone();
                            params.setInternationalPrefix(editable.toString());
                            mAccount.setParams(params);
                        } else {
                            Log.e("[Account Settings] No account !");
                        }
                    }
                });
        switchICE.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (mAccount != null) {
                            AccountParams params = mAccount.getParams().clone();

                            NatPolicy natPolicy = params.getNatPolicy();
                            if (natPolicy == null) {
                                Core core = LinphoneManager.getCore();
                                if (core != null) {
                                    natPolicy = core.createNatPolicy();
                                    params.setNatPolicy(natPolicy);
                                }
                            }

                            if (natPolicy != null) {
                                natPolicy.setIceEnabled(b);
                                if (b) natPolicy.setStunEnabled(true);
                            }
                            mAccount.setParams(params);
                        } else {
                            Log.e("[Account Settings] No account !");
                        }
                    }
                });
        switchAVPF.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (mAccount != null) {
                            AccountParams params = mAccount.getParams().clone();
                            params.setAvpfMode(b ? AVPFMode.Enabled : AVPFMode.Disabled);
                            // mAvpfInterval.setEnabled(params.avpfEnabled());
                            mAccount.setParams(params);
                        } else {
                            Log.e("[Account Settings] No account !");
                        }
                    }
                });
        switchOutboundProxy.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (mAccount != null) {
                            AccountParams params = mAccount.getParams().clone();
                            if (b) {
                                Address proxy = Factory.instance().createAddress(etProxy.getText().toString());
                                if (proxy != null) {
                                    params.setRoutesAddresses(new Address[] {proxy});
                                }
                            } else {
                                params.setRoutesAddresses(null);
                            }
                            mAccount.setParams(params);
                        } else {
                            Log.e("[Account Settings] No account !");
                        }
                    }
                });
        switchPushNotification.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (mAccount != null) {
                            LinphonePreferences.instance().setPushNotificationEnabled(b);
//                            AccountParams params = mAccount.getParams().clone();
//                            params.setPushNotificationAllowed(b);
//                            mAccount.setParams(params);
                        } else {
                            Log.e("[Account Settings] No account !");
                        }

                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        updateValues();
    }

    private void updateValues() {
        Core core = LinphoneManager.getCore();
        if (core == null) return;

        // Create an account if there is none
        if (mAccount == null) {
            // Ensure the default configuration is loaded first
            String defaultConfig = LinphonePreferences.instance().getDefaultDynamicConfigFile();
            core.loadConfigFromXml(defaultConfig);
            AccountParams params = core.createAccountParams();
            mAccount = core.createAccount(params);
            mAuthInfo = Factory.instance().createAuthInfo(null, null, null, null, null, null);
            mIsNewlyCreatedAccount = true;
        }

        if (mAccount != null) {
            AccountParams params = mAccount.getParams();
            Address identityAddress = params.getIdentityAddress();
            mAuthInfo = mAccount.findAuthInfo();

            NatPolicy natPolicy = params.getNatPolicy();
            if (natPolicy == null) {
                natPolicy = core.createNatPolicy();
                // We need to set it to params if we want it to persist, but params here is just a reference/copy?
                // AccountParams getters usually return the object.
                // However, if it was null, we created a new one. We need to set it.
                // But params instance is from mAccount.getParams().
                // If we want to modify mAccount, we should clone, modify, setParams.
                // But here we are just reading values to populate UI!
                // So if natPolicy is null, we create one just to avoid NPE when reading values?
                // Or do we expect it to be populated?
                // If it is null in params, we might want to show default values.
            }
            // If natPolicy is still null (e.g. wasn't set), we just use empty defaults.

            if (mAuthInfo != null) {
                etUserid.setText(mAuthInfo.getUserid());
                // If password is hashed we can't display it
                etPassword.setText(mAuthInfo.getPassword());
            }

            if (identityAddress != null) {
                etUsername.setText(identityAddress.getUsername());
                etDomain.setText(identityAddress.getDomain());
                etDisplayname.setText(identityAddress.getDisplayName());
            }

            etProxy.setText(params.getServerAddr());

            if (natPolicy != null) {
                etStun.setText(natPolicy.getStunServer());
                switchICE.setChecked(natPolicy.isIceEnabled());
            }

            etExpire.setText(String.valueOf(params.getExpires()));

            etCountry.setText(params.getInternationalPrefix());

            // mAvpfInterval.setValue(params.getAvpfRrInterval());
            switchAVPF.setChecked(params.getAvpfMode() == AVPFMode.Enabled);

            /*  mDisable.setChecked(!params.registerEnabled());

            mUseAsDefault.setChecked(mAccount.equals(core.getDefaultAccount()));
            mUseAsDefault.setEnabled(!mUseAsDefault.isChecked());*/

            Address[] routes = params.getRoutesAddresses();
            switchOutboundProxy.setChecked(routes != null && routes.length > 0);

            /* mIce.setEnabled(
                    natPolicy.getStunServer() != null && !natPolicy.getStunServer().isEmpty());

            mAvpf.setChecked(params.avpfEnabled());*/

            // mReplacePlusBy00.setChecked(params.getDialEscapePlus());

            switchPushNotification.setChecked(LinphonePreferences.instance().isPushNotificationEnabled());

            String serverAddr = params.getServerAddr();
            if (serverAddr != null) {
                Address proxy = Factory.instance().createAddress(serverAddr);
                if (proxy != null) {
                    etTrannsport.setText(proxy.getTransport().toString().toUpperCase());
                }
            }
        }
    }

    private void initTransportList() {
        List<String> entries = new ArrayList<>();
        List<String> values = new ArrayList<>();

        entries.add(getString(R.string.pref_transport_udp));
        values.add(String.valueOf(TransportType.Udp.toInt()));
        entries.add(getString(R.string.pref_transport_tcp));
        values.add(String.valueOf(TransportType.Tcp.toInt()));

        if (!getResources().getBoolean(R.bool.disable_all_security_features_for_markets)) {
            entries.add(getString(R.string.pref_transport_tls));
            values.add(String.valueOf(TransportType.Tls.toInt()));
        }

        //        mTransport.setItems(entries, values);
    }

    @OnClick(R.id.imageViewBack)
    public void onViewClicked() {
        getActivity().onBackPressed();
    }
}
