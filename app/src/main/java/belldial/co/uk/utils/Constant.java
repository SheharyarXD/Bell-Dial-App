package belldial.co.uk.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import belldial.co.uk.LinphoneManager;
import belldial.co.uk.R;
import belldial.co.uk.ui.model.CountryCode;
import org.linphone.core.Core;
import org.linphone.core.RegistrationState;
import org.linphone.mediastream.Log;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;

public class Constant {
    private static Context appContext;
    public static final String SHARED_PREF_APP = "belldail";
    public static final String IS_LOGIN = "isLogin";
    public static final String USER_ID = "user_id";
    public static final String ORDER_ID = "order_id";
    public static final String PRODUCT_ID = "product_id";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String DOMAIN = "domain";
    public static final String MOBILE = "mobile";

    public static final String MOVETODIAL = "moveToDial";
    public static final String MOVETODIALTRANSFER = "moveToDialTransfer";
    public static int TRANSFER = 0;

    public static HashMap<Integer, String> contryNameList = new HashMap<Integer, String>();
    public static List<CountryCode> cOuntryCOdeslist = new ArrayList<>();

    public static HashMap<String, String> contryCodeList = new HashMap<String, String>();

    // Call this ONCE in your Application class (or MainActivity onCreate)
    public static void init(Context context) {
        appContext = context.getApplicationContext();
    }
    public static HashMap<String, String> getcountryCode() {
        contryCodeList.put("AF", "93");
        contryCodeList.put("AL", "355");
        contryCodeList.put("DZ", "213");
        contryCodeList.put("AD", "376");
        contryCodeList.put("AO", "244");
        contryCodeList.put("AQ", "672");
        contryCodeList.put("AR", "54");
        contryCodeList.put("AM", "374");
        contryCodeList.put("AW", "297");
        contryCodeList.put("AU", "61");
        contryCodeList.put("AT", "43");
        contryCodeList.put("AZ", "994");
        contryCodeList.put("BH", "973");
        contryCodeList.put("BD", "880");
        contryCodeList.put("BY", "375");
        contryCodeList.put("BE", "32");
        contryCodeList.put("BZ", "501");
        contryCodeList.put("BJ", "229");
        contryCodeList.put("BT", "975");
        contryCodeList.put("BO", "591");
        contryCodeList.put("BA", "387");
        contryCodeList.put("BW", "267");
        contryCodeList.put("BR", "55");
        contryCodeList.put("BN", "673");
        contryCodeList.put("BG", "359");
        contryCodeList.put("BF", "226");
        contryCodeList.put("MM", "95");
        contryCodeList.put("BI", "257");
        contryCodeList.put("KH", "855");
        contryCodeList.put("CM", "237");
        contryCodeList.put("CA", "1");
        contryCodeList.put("CV", "238");
        contryCodeList.put("CF", "236");
        contryCodeList.put("TD", "235");
        contryCodeList.put("CL", "56");
        contryCodeList.put("CN", "61");
        contryCodeList.put("CC", "61");
        contryCodeList.put("CO", "57");
        contryCodeList.put("KM", "269");
        contryCodeList.put("CG", "242");
        contryCodeList.put("CD", "243");
        contryCodeList.put("CK", "375");
        contryCodeList.put("CR", "506");
        contryCodeList.put("HR", "385");
        contryCodeList.put("CU", "53");
        contryCodeList.put("CY", "357");
        contryCodeList.put("CZ", "420");
        contryCodeList.put("DK", "45");
        contryCodeList.put("DJ", "253");
        contryCodeList.put("TL", "670");
        contryCodeList.put("EC", "593");
        contryCodeList.put("EG", "20");
        contryCodeList.put("SV", "503");
        contryCodeList.put("GQ", "240");
        contryCodeList.put("ER", "291");
        contryCodeList.put("EE", "372");
        contryCodeList.put("ET", "251");
        contryCodeList.put("FK", "500");
        contryCodeList.put("FO", "298");
        contryCodeList.put("FJ", "679");
        contryCodeList.put("FI", "358");
        contryCodeList.put("FR", "33");
        contryCodeList.put("PF", "689");
        contryCodeList.put("GA", "241");
        contryCodeList.put("GM", "220");
        contryCodeList.put("GE", "995");
        contryCodeList.put("DE", "49");
        contryCodeList.put("GH", "233");
        contryCodeList.put("GI", "350");
        contryCodeList.put("GR", "30");
        contryCodeList.put("GL", "299");
        contryCodeList.put("GT", "502");
        contryCodeList.put("GN", "224");
        contryCodeList.put("GW", "245");
        contryCodeList.put("GY", "592");
        contryCodeList.put("HT", "509");
        contryCodeList.put("HN", "504");
        contryCodeList.put("HK", "852");
        contryCodeList.put("HU", "36");
        contryCodeList.put("IN", "91");
        contryCodeList.put("ID", "62");
        contryCodeList.put("IR", "98");
        contryCodeList.put("IQ", "964");
        contryCodeList.put("IE", "353");
        contryCodeList.put("IM", "44");
        contryCodeList.put("IL", "972");
        contryCodeList.put("IT", "39");
        contryCodeList.put("CI", "225");
        contryCodeList.put("JP", "81");
        contryCodeList.put("JO", "962");
        contryCodeList.put("KZ", "7");
        contryCodeList.put("KE", "254");
        contryCodeList.put("KI", "686");
        contryCodeList.put("KW", "965");
        contryCodeList.put("KG", "996");
        contryCodeList.put("LA", "856");
        contryCodeList.put("LV", "371");
        contryCodeList.put("LB", "961");
        contryCodeList.put("LS", "266");
        contryCodeList.put("LR", "231");
        contryCodeList.put("LY", "218");
        contryCodeList.put("LI", "423");
        contryCodeList.put("LT", "370");
        contryCodeList.put("LU", "352");
        contryCodeList.put("MO", "853");
        contryCodeList.put("MK", "389");
        contryCodeList.put("MG", "261");
        contryCodeList.put("MW", "265");
        contryCodeList.put("MY", "60");
        contryCodeList.put("MV", "960");
        contryCodeList.put("ML", "223");
        contryCodeList.put("MT", "356");
        contryCodeList.put("MH", "692");
        contryCodeList.put("MR", "222");
        contryCodeList.put("MU", "230");
        contryCodeList.put("YT", "262");
        contryCodeList.put("MX", "52");
        contryCodeList.put("FM", "691");
        contryCodeList.put("MD", "373");
        contryCodeList.put("MC", "377");
        contryCodeList.put("MN", "976");
        contryCodeList.put("ME", "382");
        contryCodeList.put("MA", "212");
        contryCodeList.put("MZ", "258");
        contryCodeList.put("NA", "264");
        contryCodeList.put("NR", "674");
        contryCodeList.put("NP", "977");
        contryCodeList.put("NL", "31");
        contryCodeList.put("AN", "599");
        contryCodeList.put("NC", "687");
        contryCodeList.put("NZ", "64");
        contryCodeList.put("NI", "505");
        contryCodeList.put("NE", "227");
        contryCodeList.put("NG", "234");
        contryCodeList.put("NU", "683");
        contryCodeList.put("KP", "850");
        contryCodeList.put("NO", "47");
        contryCodeList.put("OM", "968");
        contryCodeList.put("PK", "92");
        contryCodeList.put("PW", "680");
        contryCodeList.put("PA", "507");
        contryCodeList.put("PG", "675");
        contryCodeList.put("PY", "595");
        contryCodeList.put("PE", "51");
        contryCodeList.put("PH", "63");
        contryCodeList.put("PN", "870");
        contryCodeList.put("PL", "48");
        contryCodeList.put("PT", "351");
        contryCodeList.put("QA", "974");
        contryCodeList.put("RO", "40");
        contryCodeList.put("RU", "7");
        contryCodeList.put("RW", "250");
        contryCodeList.put("BL", "590");
        contryCodeList.put("WS", "685");
        contryCodeList.put("SM", "378");
        contryCodeList.put("ST", "239");
        contryCodeList.put("SA", "966");
        contryCodeList.put("SN", "221");
        contryCodeList.put("RS", "381");
        contryCodeList.put("SC", "248");
        contryCodeList.put("SL", "232");
        contryCodeList.put("SG", "65");
        contryCodeList.put("SK", "421");
        contryCodeList.put("SI", "386");
        contryCodeList.put("SB", "677");
        contryCodeList.put("SO", "252");
        contryCodeList.put("ZA", "27");
        contryCodeList.put("KR", "82");
        contryCodeList.put("ES", "34");
        contryCodeList.put("LK", "94");
        contryCodeList.put("SH", "290");
        contryCodeList.put("PM", "508");
        contryCodeList.put("SD", "249");
        contryCodeList.put("SR", "597");
        contryCodeList.put("SZ", "268");
        contryCodeList.put("SE", "46");
        contryCodeList.put("CH", "41");
        contryCodeList.put("SY", "963");
        contryCodeList.put("TW", "886");
        contryCodeList.put("TJ", "992");
        contryCodeList.put("TZ", "255");
        contryCodeList.put("TH", "66");
        contryCodeList.put("TG", "228");
        contryCodeList.put("TK", "690");
        contryCodeList.put("TO", "676");
        contryCodeList.put("TN", "216");
        contryCodeList.put("TR", "90");
        contryCodeList.put("TM", "993");
        contryCodeList.put("TV", "688");
        contryCodeList.put("AE", "971");
        contryCodeList.put("UG", "256");
        contryCodeList.put("GB", "44");
        contryCodeList.put("UA", "380");
        contryCodeList.put("UY", "598");
        contryCodeList.put("US", "1");
        contryCodeList.put("UZ", "998");
        contryCodeList.put("VU", "678");
        contryCodeList.put("VA", "39");
        contryCodeList.put("VE", "58");
        contryCodeList.put("VN", "84");
        contryCodeList.put("WF", "681");
        contryCodeList.put("YE", "967");
        contryCodeList.put("ZM", "260");
        contryCodeList.put("ZW", "263");

        return contryCodeList;
    }

    public static HashMap<Integer, String> getcountryNames() {

        contryNameList.put(7840, "ABKHAZIA");
        contryNameList.put(93, "  AFGHANISTAN");
        contryNameList.put(355, " ALBANIA");
        contryNameList.put(213, "ALGERIA");
        contryNameList.put(1684, "AMERICANSAMOA");
        contryNameList.put(376, "ANDORRA");
        contryNameList.put(244, "ANGOLA");
        contryNameList.put(1264, "ANGUILLA");
        contryNameList.put(672, "ANTARCTICA");
        contryNameList.put(1268, "ANTIGUAANDBARBUDA");
        contryNameList.put(54, "ARGENTINA");
        contryNameList.put(374, "ARMENIA");
        contryNameList.put(297, "ARUBA");
        contryNameList.put(61, "AUSTRALIA");
        contryNameList.put(43, "AUSTRIA");
        contryNameList.put(994, "AZERBAIJAN");
        contryNameList.put(1242, "BAHAMAS");
        contryNameList.put(973, "BAHRAIN");
        contryNameList.put(880, "BANGLADESH");
        contryNameList.put(1246, "BARBADOS");
        contryNameList.put(375, "BELARUS");
        contryNameList.put(32, "BELGIUM");
        contryNameList.put(501, "BELIZE");
        contryNameList.put(229, "BENIN");
        contryNameList.put(1441, "BERMUDA");
        contryNameList.put(975, "BHUTAN");
        contryNameList.put(591, "BOLIVIA");
        contryNameList.put(387, "BOSNIAANDHERZEGOVINA");
        contryNameList.put(267, "BOTSWANA");
        contryNameList.put(55, "BRAZIL");
        contryNameList.put(246, "BRITISHINDIANOCEANTERRITORY");
        contryNameList.put(1284, "BRITISHVIRGINISLANDS");
        contryNameList.put(673, "BRUNEI");
        contryNameList.put(359, "BULGARIA");
        contryNameList.put(226, "BURKINAFASO");
        contryNameList.put(257, "BURUNDI");
        contryNameList.put(855, "CAMBODIA");
        contryNameList.put(237, "CAMEROON");
        //        contryNameList.put(1, "CANADA");
        contryNameList.put(238, "CAPEVERDE");
        contryNameList.put(1345, "CAYMANISLANDS");
        contryNameList.put(236, "CENTRALAFRICANREPUBLIC");
        contryNameList.put(235, "CHAD");
        contryNameList.put(56, "CHILE");
        contryNameList.put(86, "CHINA");
        contryNameList.put(61, "CHRISTMASISLAND");
        contryNameList.put(61, "COCOSISLAND");
        contryNameList.put(57, "COLOMBIA");
        contryNameList.put(269, "COMOROS");
        contryNameList.put(682, "COOKISLANDS");
        contryNameList.put(506, "COSTARICA");
        contryNameList.put(385, "CROATIA");
        contryNameList.put(53, "CUBA");
        contryNameList.put(599, "CURACAO");
        contryNameList.put(357, "CYPRUS");
        contryNameList.put(420, "CZECHREPUBLIC");
        contryNameList.put(243, "DEMOCRATICREPUBLICOFCONGO");
        contryNameList.put(45, "DENMARK");
        contryNameList.put(253, "DJIBOUTI");
        contryNameList.put(1767, "DOMINICA");
        contryNameList.put(1809, "DOMINICANREPUBLIC");
        contryNameList.put(1829, "DOMINICANREPUBLIC");
        contryNameList.put(1849, "DOMINICANREPUBLIC");
        contryNameList.put(670, "EASTTIMOR");
        contryNameList.put(593, "ECUADOR");
        contryNameList.put(20, " EGYPT");
        contryNameList.put(503, "ELSALVADOR");
        contryNameList.put(240, "EQUATORIALGUINEA");
        contryNameList.put(291, "ERITREA");
        contryNameList.put(372, "ESTONIA");
        contryNameList.put(251, "ETHIOPIA");
        contryNameList.put(500, "FALKLANDISLANDS");
        contryNameList.put(298, "FAROEISLANDS");
        contryNameList.put(679, "FIJI");
        contryNameList.put(358, "FINLAND");
        contryNameList.put(33, " FRANCE");
        contryNameList.put(689, "FRENCHPOLYNESIA");
        contryNameList.put(241, "GABON");
        contryNameList.put(220, "GAMBIA");
        contryNameList.put(995, "GEORGIA");
        contryNameList.put(49, "GERMANY");
        contryNameList.put(233, "GHANA");
        contryNameList.put(350, "GIBRALTAR");
        contryNameList.put(30, "GREECE");
        contryNameList.put(299, "GREENLAND");
        contryNameList.put(1473, "GRENADA");
        contryNameList.put(1671, "GUAM");
        contryNameList.put(502, "GUATEMALA");
        contryNameList.put(441481, "GUERNSEY");
        contryNameList.put(224, "GUINEA");
        contryNameList.put(245, "GUINEABISSAU");
        contryNameList.put(592, "GUYANA");
        contryNameList.put(509, "HAITI");
        contryNameList.put(504, "HONDURAS");
        contryNameList.put(852, "HONGKONG");
        contryNameList.put(36, " HUNGARY");
        contryNameList.put(354, "ICELAND");
        contryNameList.put(91, " India");
        contryNameList.put(62, " INDONESIA");
        contryNameList.put(98, " IRAN");
        contryNameList.put(964, "IRAQ");
        contryNameList.put(353, "IRELAND");
        contryNameList.put(972, "ISRAEL");
        contryNameList.put(39, " ITALY");
        contryNameList.put(225, "IVORYCOAST");
        contryNameList.put(1876, "JAMAICA");
        contryNameList.put(81, " JAPAN");
        contryNameList.put(962, "JORDAN");
        contryNameList.put(7, "  KAZAKHSTAN");
        contryNameList.put(254, "KENYA");
        contryNameList.put(686, "KIRIBATI");
        contryNameList.put(383, "KOSOVO");
        contryNameList.put(965, "KWAIT");
        contryNameList.put(996, "KYRGYZSTAN");
        contryNameList.put(856, "LAOS");
        contryNameList.put(371, "LATVIA");
        contryNameList.put(961, "LEBANON");
        contryNameList.put(266, "LESOTHO");
        contryNameList.put(231, "LIBERIA");
        contryNameList.put(218, "LIBYA");
        contryNameList.put(423, "LIECHTENSTEIN");
        contryNameList.put(370, "LITHUANIA");
        contryNameList.put(352, "LUXEMBOURG");
        contryNameList.put(853, "MACAO");
        contryNameList.put(389, "REPUBLIC OF MACEDONIA");
        contryNameList.put(261, "MADAGASCAR");
        contryNameList.put(265, "MALAWI");
        contryNameList.put(60, "MALASYA");
        contryNameList.put(960, "MALDIVES");
        contryNameList.put(223, "MALI");
        contryNameList.put(356, "MALTA");
        contryNameList.put(692, "MARSHALLISLAND");
        contryNameList.put(222, "MAURITANIA");
        contryNameList.put(230, "MAURITIUS");
        contryNameList.put(262, "JERSEY");
        contryNameList.put(52, " MEXICO");
        contryNameList.put(691, "MICRONESIA");
        contryNameList.put(373, "MOLDOVA");
        contryNameList.put(377, "MONACO");
        contryNameList.put(976, "MONGOLIA");
        contryNameList.put(382, "MONTENEGRO");
        contryNameList.put(1664, "MONTSERRAT");
        contryNameList.put(212, "MOROCCO");
        contryNameList.put(258, "MOZAMBIQUE");
        contryNameList.put(95, "MYANMAR");
        contryNameList.put(264, "NAMIBIA");
        contryNameList.put(674, "NAURU");
        contryNameList.put(977, "NEPAL");
        contryNameList.put(31, " NETHERLANDS");
        contryNameList.put(599, "NETHERLANDS");
        contryNameList.put(687, "ICELAND");
        contryNameList.put(64, " NEW ZEALAND");
        contryNameList.put(505, "NICARAGUA");
        contryNameList.put(227, "NIGER");
        contryNameList.put(234, "NIGERIA");
        contryNameList.put(683, "NIUE");
        contryNameList.put(850, "NORTH KOREA");
        contryNameList.put(1670, "NORTH ERNMARIANASISLANDS");
        contryNameList.put(47, " NORWAY");
        contryNameList.put(968, "OMAN");
        contryNameList.put(92, "PAKISTAN");
        contryNameList.put(680, "PALAU");
        contryNameList.put(970, "PALESTINE");
        contryNameList.put(507, "PANAMA");
        contryNameList.put(675, "PAPUANEWGUINEA");
        contryNameList.put(595, "PARAGUAY");
        contryNameList.put(51, " PERU");
        contryNameList.put(63, " PHILIPPINES");
        contryNameList.put(64, " PITCAIRNISLANDS");
        contryNameList.put(48, " POLAND");
        contryNameList.put(351, "PORTUGAL");
        contryNameList.put(1939, "PUERTORICO");
        contryNameList.put(1787, "PUERTORICO");
        contryNameList.put(974, "QATAR");
        contryNameList.put(242, "REPUBLICOFTHECONGO");
        contryNameList.put(262, "NIUE");
        contryNameList.put(40, "ROMANIA");
        contryNameList.put(7, "  RUSSIA");
        contryNameList.put(250, "RWANDA");
        contryNameList.put(590, "STBARTS");
        contryNameList.put(290, "SAINT_HELENA");
        contryNameList.put(1869, "SAINTKITTSANDNEVIS");
        contryNameList.put(1758, "STLUCIA");
        contryNameList.put(1784, "STVINCENTANDTHEGRENADINES");
        contryNameList.put(685, "SAMOA");
        contryNameList.put(378, "SANMARINO");
        contryNameList.put(239, "SAOTOMEANDPRINCE");
        contryNameList.put(966, "SAUDIARABIA");
        contryNameList.put(221, "SENEGAL");
        contryNameList.put(381, "SERBIA");
        contryNameList.put(248, "SEYCHELLES");
        contryNameList.put(232, "SIERRALEONE");
        contryNameList.put(65, " SINGAPORE");
        contryNameList.put(1721, "SINTMAARTEN");
        contryNameList.put(421, "SLOVAKIA");
        contryNameList.put(386, "SLOVENIA");
        contryNameList.put(677, "SOLOMONISLANDS");
        contryNameList.put(252, "SOMALIA");
        contryNameList.put(27, "SOUTHAFRICA");
        contryNameList.put(82, "SOUTHKOREA");
        contryNameList.put(211, "SOUTHSUDAN");
        contryNameList.put(34, "  SPAIN");
        contryNameList.put(94, "SRILANKA");
        contryNameList.put(249, "SUDAN");
        contryNameList.put(597, "SURINAME");
        contryNameList.put(268, "SWAZILAND");
        contryNameList.put(46, " SWEDEN");
        contryNameList.put(41, " SWITZERLAND");
        contryNameList.put(963, "SYRIA");
        contryNameList.put(886, "TAIWAN");
        contryNameList.put(992, "TAJIKISTAN");
        contryNameList.put(255, "TANZANIA");
        contryNameList.put(66, " THAILAND");
        contryNameList.put(228, "TOGO");
        contryNameList.put(690, "TOKELAU");
        contryNameList.put(676, "TONGA");
        contryNameList.put(1868, "   TRINIDADANDTOBAGO");
        contryNameList.put(216, "TUNISIA");
        contryNameList.put(90, " TURKEY");
        contryNameList.put(993, "TURKMENISTAN");
        contryNameList.put(1649, "   TURKSANDCAICOS");
        contryNameList.put(688, "TUVALU");
        contryNameList.put(1340, " VIRGINISLANDS");
        contryNameList.put(256, "UGANDA");
        contryNameList.put(380, "UKRAINE");
        contryNameList.put(971, "UNITEDARABEMIRATES");
        contryNameList.put(44, " United Kingdom");
        contryNameList.put(1, "United States");
        contryNameList.put(598, "URUGUAY");
        contryNameList.put(998, "UZBEKISTN");
        contryNameList.put(678, "VANUATU");
        contryNameList.put(379, "VATICANCITY");
        contryNameList.put(58, " VENEZUELA");
        contryNameList.put(84, " VIETNAM");
        contryNameList.put(681, "TURKEY");
        contryNameList.put(212, "TURKEY");
        contryNameList.put(967, "YEMEN");
        contryNameList.put(260, "ZAMBIA");
        contryNameList.put(263, "ZIMBABWE");

        return contryNameList;
    }



public static int getStatusIconResource(RegistrationState state, boolean isDefaultAccount) {
        try {
            Core lc = LinphoneManager.getCore();

            if (lc != null && lc.getCallsNb() > 0) {
                return getNetworkSignalIcon(); // no context param needed
            }

            boolean defaultAccountConnected =
                    !isDefaultAccount
                            || (lc != null
                                    && lc.getDefaultProxyConfig() != null
                                    && lc.getDefaultProxyConfig().getState()
                                            == RegistrationState.Ok);

            if (state == RegistrationState.Ok && defaultAccountConnected) {
                return R.drawable.signal_high;
            } else if (state == RegistrationState.Progress) {
                return R.drawable.signal_low;
            } else {
                return R.drawable.signal_null;
            }

        } catch (Exception e) {
            Log.e(e);
        }
        return R.drawable.signal_null;
    }

    public static int getNetworkSignalIcon() {
    try {
        if (appContext == null) return R.drawable.signal_high; // fail safe → don't show red

        ConnectivityManager cm = (ConnectivityManager)
                appContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm == null) return R.drawable.signal_high;

        android.net.Network network = cm.getActiveNetwork();
        if (network == null) return R.drawable.signal_null; // truly no network

        NetworkCapabilities caps = cm.getNetworkCapabilities(network);
        if (caps == null) return R.drawable.signal_null;

        // Check if ANY transport is available (WiFi, cellular, ethernet)
        boolean hasTransport =
                caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                || caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                || caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET);

        if (!hasTransport) return R.drawable.signal_null; // no connection at all → red

        // Has transport → check quality
        int downKbps = caps.getLinkDownstreamBandwidthKbps();

        if (downKbps == 0) {
            // Bandwidth unknown but transport exists → assume ok during call
            // Don't punish user with red when we just can't measure
            return R.drawable.signal_high;
        } else if (downKbps >= 500) {
            return R.drawable.signal_high;
        } else if (downKbps >= 100) {
            return R.drawable.signal_low;
        } else {
            return R.drawable.signal_null; // genuinely bad bandwidth → red
        }

    } catch (Exception e) {
        Log.e(e);
        return R.drawable.signal_high; // on any error, fail safe → don't scare user
    }
}


    public static List<CountryCode> getCountryCodeList() {
        cOuntryCOdeslist.clear();

        cOuntryCOdeslist.add(new CountryCode("United States", R.drawable.united_states, "+1"));
        cOuntryCOdeslist.add(new CountryCode("Canada", R.drawable.canada, "+1"));
        cOuntryCOdeslist.add(new CountryCode("United Kingdom", R.drawable.unitedkingdom, "+44"));
        cOuntryCOdeslist.add(new CountryCode("Abkhazia", R.drawable.abkhazia, "+7840"));
        cOuntryCOdeslist.add(new CountryCode("Afghanistan", R.drawable.afghanistan, "+93"));
        cOuntryCOdeslist.add(new CountryCode("Albania", R.drawable.albania, "+355"));
        cOuntryCOdeslist.add(new CountryCode("Algeria", R.drawable.algeria, "+213"));
        cOuntryCOdeslist.add(new CountryCode("American Samoa", R.drawable.americansamoa, "+1684"));
        cOuntryCOdeslist.add(new CountryCode("Andorra", R.drawable.andorra, "+376"));
        cOuntryCOdeslist.add(new CountryCode("Angola", R.drawable.angola, "+244"));
        cOuntryCOdeslist.add(new CountryCode("Anguilla", R.drawable.anguilla, "+1264"));
        cOuntryCOdeslist.add(
                new CountryCode("Antigua and Barbuda", R.drawable.antiguaandbarbuda, "+1268"));
        cOuntryCOdeslist.add(new CountryCode("Argentina", R.drawable.argentina, "+54"));
        cOuntryCOdeslist.add(new CountryCode("Armenia", R.drawable.armenia, "+374"));
        cOuntryCOdeslist.add(new CountryCode("Aruba", R.drawable.aruba, "+297"));
        cOuntryCOdeslist.add(new CountryCode("Australia", R.drawable.australia, "+61"));
        cOuntryCOdeslist.add(new CountryCode("Austria", R.drawable.austria, "+43"));
        cOuntryCOdeslist.add(new CountryCode("Azerbaijan", R.drawable.azerbaijan, "+944"));
        cOuntryCOdeslist.add(new CountryCode("Bahamas", R.drawable.bahamas, "+1224"));
        cOuntryCOdeslist.add(new CountryCode("Bahrain", R.drawable.bahrain, "+973"));
        cOuntryCOdeslist.add(new CountryCode("Bangladesh", R.drawable.bangladesh, "+880"));
        cOuntryCOdeslist.add(new CountryCode("Barbados", R.drawable.barbados, "+1246"));
        cOuntryCOdeslist.add(new CountryCode("Belarus", R.drawable.belarus, "+375"));
        cOuntryCOdeslist.add(new CountryCode("Belgium", R.drawable.belgium, "+32"));
        cOuntryCOdeslist.add(new CountryCode("Belize", R.drawable.belize, "+501"));
        cOuntryCOdeslist.add(new CountryCode("Benin", R.drawable.benin, "+229"));
        cOuntryCOdeslist.add(new CountryCode("Bermuda", R.drawable.bermuda, "+1441"));
        cOuntryCOdeslist.add(new CountryCode("Bhutan", R.drawable.bhutan, "+975"));
        cOuntryCOdeslist.add(new CountryCode("Bolivia", R.drawable.bolivia, "+591"));
        cOuntryCOdeslist.add(
                new CountryCode("Bosnia and Herzegovina", R.drawable.bosniaandherzegovina, "+387"));
        cOuntryCOdeslist.add(new CountryCode("Botswana", R.drawable.botswana, "+267"));
        cOuntryCOdeslist.add(new CountryCode("Brazil", R.drawable.brazil, "+55"));
        cOuntryCOdeslist.add(
                new CountryCode(
                        "British Indian Ocean Territory",
                        R.drawable.britishindianoceanterritory,
                        "+246"));
        cOuntryCOdeslist.add(
                new CountryCode(
                        "British Virgin Islands", R.drawable.britishvirginislands, "+1284"));
        cOuntryCOdeslist.add(new CountryCode("Brunei", R.drawable.brunei, "+673"));
        cOuntryCOdeslist.add(new CountryCode("Bulgaria", R.drawable.bulgaria, "+359"));
        cOuntryCOdeslist.add(new CountryCode("Burkina Faso", R.drawable.burkinafaso, "+226"));
        cOuntryCOdeslist.add(new CountryCode("Burundi", R.drawable.burundi, "+257"));
        cOuntryCOdeslist.add(new CountryCode("Cambodia", R.drawable.cambodia, "+855"));
        cOuntryCOdeslist.add(new CountryCode("Cameroon", R.drawable.cameroon, "+237"));
        cOuntryCOdeslist.add(new CountryCode("Cape Verde", R.drawable.capeverde, "+238"));
        cOuntryCOdeslist.add(new CountryCode("Cayman Islands ", R.drawable.caymanislands, "+1345"));
        cOuntryCOdeslist.add(
                new CountryCode(
                        "Central African Republic", R.drawable.centralafricanrepublic, "+236"));
        cOuntryCOdeslist.add(new CountryCode("Chad", R.drawable.chad, "+235"));
        cOuntryCOdeslist.add(new CountryCode("Chile", R.drawable.chile, "+56"));
        cOuntryCOdeslist.add(new CountryCode("China", R.drawable.china, "+86"));
        cOuntryCOdeslist.add(
                new CountryCode("Christmas Island", R.drawable.christmasisland, "+61"));
        cOuntryCOdeslist.add(new CountryCode("Cocos Islands", R.drawable.cocosisland, "+61"));
        cOuntryCOdeslist.add(new CountryCode("Colombia", R.drawable.colombia, "+57"));
        cOuntryCOdeslist.add(new CountryCode("Comoros", R.drawable.comoros, "+269"));
        cOuntryCOdeslist.add(new CountryCode("Cook Islands", R.drawable.cookislands, "+682"));
        cOuntryCOdeslist.add(new CountryCode("Costa Rica", R.drawable.costarica, "+506"));
        cOuntryCOdeslist.add(new CountryCode("Croatia", R.drawable.croatia, "+385"));
        cOuntryCOdeslist.add(new CountryCode("Cuba", R.drawable.cuba, "+53"));
        cOuntryCOdeslist.add(new CountryCode("Curacao", R.drawable.curacao, "+599"));
        cOuntryCOdeslist.add(new CountryCode("Cyprus", R.drawable.cyprus, "+357"));
        cOuntryCOdeslist.add(new CountryCode("Czech Republic ", R.drawable.czechrepublic, "+420"));
        cOuntryCOdeslist.add(
                new CountryCode(
                        "Democratic Republic of the Congo",
                        R.drawable.democraticrepublicofcongo,
                        "+243"));
        cOuntryCOdeslist.add(new CountryCode("Denmark", R.drawable.denmark, "+45"));
        cOuntryCOdeslist.add(new CountryCode("Djibouti", R.drawable.djibouti, "+253"));
        cOuntryCOdeslist.add(new CountryCode("Dominica", R.drawable.dominica, "+1767"));
        cOuntryCOdeslist.add(
                new CountryCode("Dominican Republic", R.drawable.dominicanrepublic, "+1809"));
        cOuntryCOdeslist.add(new CountryCode("East Timor", R.drawable.easttimor, "+670"));
        cOuntryCOdeslist.add(new CountryCode("Ecuador", R.drawable.ecuador, "+593"));
        cOuntryCOdeslist.add(new CountryCode("Egypt", R.drawable.egypt, "+20"));
        cOuntryCOdeslist.add(new CountryCode("El Salvador ", R.drawable.elsalvador, "+503"));
        cOuntryCOdeslist.add(
                new CountryCode("Equatorial Guine", R.drawable.equatorialguinea, "+240"));
        cOuntryCOdeslist.add(new CountryCode("Eritrea", R.drawable.eritrea, "+291"));
        cOuntryCOdeslist.add(new CountryCode("Estonia", R.drawable.estonia, "+372"));
        cOuntryCOdeslist.add(new CountryCode("Ethiopia", R.drawable.ethiopia, "+251"));
        cOuntryCOdeslist.add(
                new CountryCode("Falkland Islands", R.drawable.falklandislands, "+500"));
        cOuntryCOdeslist.add(new CountryCode("Faroe Islands", R.drawable.faroeislands, "+298"));
        cOuntryCOdeslist.add(new CountryCode("Fiji", R.drawable.fiji, "+679"));
        cOuntryCOdeslist.add(new CountryCode("Finland", R.drawable.finland, "+358"));
        cOuntryCOdeslist.add(new CountryCode("France", R.drawable.france, "+33"));
        cOuntryCOdeslist.add(
                new CountryCode("French Polynesia", R.drawable.frenchpolynesia, "+689"));
        cOuntryCOdeslist.add(new CountryCode("Gabon", R.drawable.gabon, "+241"));
        cOuntryCOdeslist.add(new CountryCode("Gambia", R.drawable.gambia, "+220"));
        cOuntryCOdeslist.add(new CountryCode("Georgia", R.drawable.georgia, "+995"));
        cOuntryCOdeslist.add(new CountryCode("Germany", R.drawable.germany, "+49"));
        cOuntryCOdeslist.add(new CountryCode("Ghana", R.drawable.ghana, "+233"));
        cOuntryCOdeslist.add(new CountryCode("Gibraltar", R.drawable.gibraltar, "+350"));
        cOuntryCOdeslist.add(new CountryCode("Greece", R.drawable.greece, "+30"));
        cOuntryCOdeslist.add(new CountryCode("Greenland", R.drawable.greenland, "+299"));
        cOuntryCOdeslist.add(new CountryCode("Grenada ", R.drawable.grenada, "+1473"));
        cOuntryCOdeslist.add(new CountryCode("Guam", R.drawable.guam, "+1671"));
        cOuntryCOdeslist.add(new CountryCode("Guatemala", R.drawable.guatemala, "+502"));
        cOuntryCOdeslist.add(new CountryCode("Guernsey", R.drawable.guernsey, "+441481"));
        cOuntryCOdeslist.add(new CountryCode("Guinea", R.drawable.guinea, "+224"));
        cOuntryCOdeslist.add(new CountryCode("Guinea-Bissau", R.drawable.guineabissau, "+245"));
        cOuntryCOdeslist.add(new CountryCode("Haiti", R.drawable.haiti, "+509"));
        cOuntryCOdeslist.add(new CountryCode("Honduras", R.drawable.honduras, "+504"));
        cOuntryCOdeslist.add(new CountryCode("Hong Kong", R.drawable.hongkong, "+852"));
        cOuntryCOdeslist.add(new CountryCode("Hungary", R.drawable.hungary, "+36"));
        cOuntryCOdeslist.add(new CountryCode("Iceland", R.drawable.iceland, "+354"));
        cOuntryCOdeslist.add(new CountryCode("India", R.drawable.india, "+91"));
        cOuntryCOdeslist.add(new CountryCode("Indonesia", R.drawable.indonesia, "+62"));
        cOuntryCOdeslist.add(new CountryCode("Iran", R.drawable.iran, "+98"));
        cOuntryCOdeslist.add(new CountryCode("Iraq", R.drawable.iraq, "+964"));
        cOuntryCOdeslist.add(new CountryCode("Ireland", R.drawable.ireland, "+353"));
        cOuntryCOdeslist.add(new CountryCode("Isle of Man", R.drawable.isleofman, "+441624"));
        cOuntryCOdeslist.add(new CountryCode("Israel", R.drawable.israel, "+972"));
        cOuntryCOdeslist.add(new CountryCode("Italy", R.drawable.italy, "+39"));
        cOuntryCOdeslist.add(new CountryCode("Ivory Coast", R.drawable.ivorycoast, "+225"));
        cOuntryCOdeslist.add(new CountryCode("Jamaica", R.drawable.jamaica, "+1876"));
        cOuntryCOdeslist.add(new CountryCode("Japan", R.drawable.japan, "+81"));
        cOuntryCOdeslist.add(new CountryCode("Jersey", R.drawable.jersey, "+441534"));
        cOuntryCOdeslist.add(new CountryCode("Jordan", R.drawable.jordan, "+962"));
        cOuntryCOdeslist.add(new CountryCode("Kazakhstan", R.drawable.kazakhstan, "+7"));
        cOuntryCOdeslist.add(new CountryCode("Kenya", R.drawable.kenya, "+254"));
        cOuntryCOdeslist.add(new CountryCode("Kiribati", R.drawable.kiribati, "+686"));
        cOuntryCOdeslist.add(new CountryCode("Kosovo", R.drawable.kosovo, "+383"));
        cOuntryCOdeslist.add(new CountryCode("Kuwait", R.drawable.kwait, "+965"));
        cOuntryCOdeslist.add(new CountryCode("Kyrgyzstan", R.drawable.kyrgyzstan, "+996"));
        cOuntryCOdeslist.add(new CountryCode("Laos", R.drawable.laos, "+856"));
        cOuntryCOdeslist.add(new CountryCode("Latvia", R.drawable.latvia, "+371"));
        cOuntryCOdeslist.add(new CountryCode("Lebanon", R.drawable.lebanon, "+961"));

        cOuntryCOdeslist.add(new CountryCode("Lesotho", R.drawable.lesotho, "+266"));
        cOuntryCOdeslist.add(new CountryCode("Liberia", R.drawable.liberia, "+231"));
        cOuntryCOdeslist.add(new CountryCode("Libya", R.drawable.libya, "+218"));
        cOuntryCOdeslist.add(new CountryCode("Liechtenstein", R.drawable.liechtenstein, "+423"));
        cOuntryCOdeslist.add(new CountryCode("Lithuania", R.drawable.lithuania, "+370"));
        cOuntryCOdeslist.add(new CountryCode("Luxembourg", R.drawable.luxembourg, "+352"));
        cOuntryCOdeslist.add(new CountryCode("Macau", R.drawable.macao, "+853"));
        cOuntryCOdeslist.add(new CountryCode("Macedonia", R.drawable.republicofmacedonia, "+389"));
        cOuntryCOdeslist.add(new CountryCode("Madagascar", R.drawable.madagascar, "+261"));
        cOuntryCOdeslist.add(new CountryCode("Malawi", R.drawable.malawi, "+265"));
        cOuntryCOdeslist.add(new CountryCode("Malaysia", R.drawable.malasya, "+60"));
        cOuntryCOdeslist.add(new CountryCode("Maldives", R.drawable.maldives, "+960"));
        cOuntryCOdeslist.add(new CountryCode("Mali", R.drawable.mali, "+223"));
        cOuntryCOdeslist.add(new CountryCode("Malta", R.drawable.malta, "+356"));
        cOuntryCOdeslist.add(
                new CountryCode("Marshall Islands", R.drawable.marshallisland, "+692"));
        cOuntryCOdeslist.add(new CountryCode("Mauritania", R.drawable.mauritania, "+222"));
        cOuntryCOdeslist.add(new CountryCode("Mauritius", R.drawable.mauritius, "+230"));
        cOuntryCOdeslist.add(new CountryCode("Mayotte", R.drawable.jersey, "+262"));
        cOuntryCOdeslist.add(new CountryCode("Mexico", R.drawable.mexico, "+52"));
        cOuntryCOdeslist.add(new CountryCode("Micronesia", R.drawable.micronesia, "+691"));
        cOuntryCOdeslist.add(new CountryCode("Moldova", R.drawable.moldova, "+373"));
        cOuntryCOdeslist.add(new CountryCode("Monaco", R.drawable.monaco, "+377"));
        cOuntryCOdeslist.add(new CountryCode("Mongolia", R.drawable.mongolia, "+976"));
        cOuntryCOdeslist.add(new CountryCode("Montenegro", R.drawable.montenegro, "+382"));
        cOuntryCOdeslist.add(new CountryCode("Montserrat", R.drawable.montserrat, "+1664"));
        cOuntryCOdeslist.add(new CountryCode("Morocco", R.drawable.morocco, "+212"));
        cOuntryCOdeslist.add(new CountryCode("Mozambique", R.drawable.mozambique, "+258"));
        cOuntryCOdeslist.add(new CountryCode("Myanmar", R.drawable.myanmar, "+95"));
        cOuntryCOdeslist.add(new CountryCode("Namibia", R.drawable.namibia, "+264"));
        cOuntryCOdeslist.add(new CountryCode("Nauru", R.drawable.nauru, "+674"));
        cOuntryCOdeslist.add(new CountryCode("Nepal", R.drawable.nepal, "+977"));
        cOuntryCOdeslist.add(new CountryCode("Netherlands", R.drawable.netherlands, "+31"));
        cOuntryCOdeslist.add(
                new CountryCode("Netherlands Antilles", R.drawable.netherlands, "+599"));
        cOuntryCOdeslist.add(new CountryCode("New Caledonia", R.drawable.iceland, "+687"));
        cOuntryCOdeslist.add(new CountryCode("New Zealand", R.drawable.newzealand, "+64"));
        cOuntryCOdeslist.add(new CountryCode("Nicaragua", R.drawable.nicaragua, "+505"));

        cOuntryCOdeslist.add(new CountryCode("Niger", R.drawable.niger, "+227"));
        cOuntryCOdeslist.add(new CountryCode("Nigeria", R.drawable.nigeria, "+234"));
        cOuntryCOdeslist.add(new CountryCode("Niue", R.drawable.niue, "+683"));
        cOuntryCOdeslist.add(new CountryCode("North Korea ", R.drawable.northkorea, "+850"));
        cOuntryCOdeslist.add(
                new CountryCode(
                        "Northern Mariana Islands", R.drawable.northernmarianasislands, "+1670"));
        cOuntryCOdeslist.add(new CountryCode("Norway", R.drawable.norway, "+47"));
        cOuntryCOdeslist.add(new CountryCode("Oman", R.drawable.oman, "+968"));
        cOuntryCOdeslist.add(new CountryCode("Pakistan", R.drawable.pakistan, "+92"));
        cOuntryCOdeslist.add(new CountryCode("Palau", R.drawable.palau, "+680"));
        cOuntryCOdeslist.add(new CountryCode("Palestine", R.drawable.palestine, "+970"));
        cOuntryCOdeslist.add(new CountryCode("Panama", R.drawable.panama, "+507"));
        cOuntryCOdeslist.add(
                new CountryCode("Papua New Guinea", R.drawable.papuanewguinea, "+675"));
        cOuntryCOdeslist.add(new CountryCode("Paraguay", R.drawable.paraguay, "+595"));
        cOuntryCOdeslist.add(new CountryCode("Peru", R.drawable.peru, "+51"));
        cOuntryCOdeslist.add(new CountryCode("Philippines", R.drawable.philippines, "+63"));
        cOuntryCOdeslist.add(new CountryCode("Pitcairn", R.drawable.pitcairnislands, "+64"));
        cOuntryCOdeslist.add(new CountryCode("Poland", R.drawable.poland, "+48"));
        cOuntryCOdeslist.add(new CountryCode("Portugal", R.drawable.portugal, "+351"));
        cOuntryCOdeslist.add(new CountryCode("Puerto Rico", R.drawable.puertorico, "+1787"));
        // cOuntryCOdeslist.add(new COuntryCOde("Qatar", R.drawable.qatar, "+974"));

        cOuntryCOdeslist.add(
                new CountryCode("Republic of the Congo", R.drawable.republicofthecongo, "+242"));
        cOuntryCOdeslist.add(new CountryCode("Reunion", R.drawable.niue, "+262"));
        cOuntryCOdeslist.add(new CountryCode("Romania", R.drawable.romania, "+40"));
        cOuntryCOdeslist.add(new CountryCode("Russia", R.drawable.russia, "+7"));
        cOuntryCOdeslist.add(new CountryCode("Rwanda", R.drawable.rwanda, "+250"));
        cOuntryCOdeslist.add(new CountryCode("Saint Barthelemy", R.drawable.stbarts, "+590"));
        // cOuntryCOdeslist.add(new COuntryCOde("Saint Helena", R.drawable.saint_helena, "+290"));
        cOuntryCOdeslist.add(
                new CountryCode("Saint Kitts and Nevis", R.drawable.saintkittsandnevis, "+1869"));
        cOuntryCOdeslist.add(new CountryCode("Saint Lucia", R.drawable.stlucia, "+1758"));
        cOuntryCOdeslist.add(new CountryCode("Samoa", R.drawable.samoa, "+685"));
        cOuntryCOdeslist.add(new CountryCode("San Marino", R.drawable.sanmarino, "+378"));
        cOuntryCOdeslist.add(new CountryCode("Saudi Arabia", R.drawable.saudiarabia, "+966"));
        cOuntryCOdeslist.add(new CountryCode("Senegal", R.drawable.senegal, "+221"));
        cOuntryCOdeslist.add(new CountryCode("Serbia", R.drawable.serbia, "+381"));
        cOuntryCOdeslist.add(new CountryCode("Seychelles", R.drawable.seychelles, "+248"));
        cOuntryCOdeslist.add(new CountryCode("Sierra Leone", R.drawable.sierraleone, "+232"));
        cOuntryCOdeslist.add(new CountryCode("Singapore", R.drawable.singapore, "+65"));
        cOuntryCOdeslist.add(new CountryCode("Sint Maarten", R.drawable.sintmaarten, "+1721"));
        cOuntryCOdeslist.add(new CountryCode("Slovakia", R.drawable.slovakia, "+421"));
        cOuntryCOdeslist.add(new CountryCode("Slovenia", R.drawable.slovenia, "+386"));
        cOuntryCOdeslist.add(new CountryCode("Solomon Islands", R.drawable.solomonislands, "+677"));
        cOuntryCOdeslist.add(new CountryCode("Somalia", R.drawable.somalia, "+252"));
        cOuntryCOdeslist.add(new CountryCode("South Africa", R.drawable.southafrica, "+27"));
        cOuntryCOdeslist.add(new CountryCode("South Korea", R.drawable.southkorea, "+82"));
        cOuntryCOdeslist.add(new CountryCode("South Sudan", R.drawable.southsudan, "+211"));
        cOuntryCOdeslist.add(new CountryCode("Spain", R.drawable.spain, "+34"));
        cOuntryCOdeslist.add(new CountryCode("Sri Lanka", R.drawable.srilanka, "+94"));
        cOuntryCOdeslist.add(new CountryCode("Sudan", R.drawable.sudan, "+249"));
        cOuntryCOdeslist.add(new CountryCode("Suriname", R.drawable.suriname, "+597"));
        cOuntryCOdeslist.add(new CountryCode("Swaziland", R.drawable.swaziland, "+268"));
        cOuntryCOdeslist.add(new CountryCode("Sweden", R.drawable.sweden, "+46"));

        cOuntryCOdeslist.add(new CountryCode("Switzerland", R.drawable.switzerland, "+41"));
        cOuntryCOdeslist.add(new CountryCode("Syria", R.drawable.syria, "+963"));
        cOuntryCOdeslist.add(new CountryCode("Taiwan", R.drawable.taiwan, "+886"));
        cOuntryCOdeslist.add(new CountryCode("Tajikistan", R.drawable.tajikistan, "+992"));
        cOuntryCOdeslist.add(new CountryCode("Tanzania", R.drawable.tanzania, "+255"));
        cOuntryCOdeslist.add(new CountryCode("Thailand", R.drawable.thailand, "+66"));
        cOuntryCOdeslist.add(new CountryCode("Togo", R.drawable.togo, "+228"));
        cOuntryCOdeslist.add(new CountryCode("Tokelau", R.drawable.tokelau, "+690"));
        cOuntryCOdeslist.add(new CountryCode("Tonga", R.drawable.tonga, "+676"));
        cOuntryCOdeslist.add(
                new CountryCode("Trinidad and Tobago", R.drawable.trinidadandtobago, "+1868"));
        cOuntryCOdeslist.add(new CountryCode("Tunisia", R.drawable.tunisia, "+216"));
        cOuntryCOdeslist.add(new CountryCode("Turkey", R.drawable.turkey, "+90"));
        cOuntryCOdeslist.add(new CountryCode("Turkmenistan", R.drawable.turkmenistan, "+993"));
        cOuntryCOdeslist.add(
                new CountryCode("Turks and Caicos", R.drawable.turksandcaicos, "+1649"));
        cOuntryCOdeslist.add(new CountryCode("U.S. Virgin Island", R.drawable.turkey, "+1340"));
        cOuntryCOdeslist.add(new CountryCode("Uganda", R.drawable.uganda, "+256"));
        cOuntryCOdeslist.add(new CountryCode("Ukraine", R.drawable.ukraine, "+380"));

        cOuntryCOdeslist.add(new CountryCode("United Arab Emirat", R.drawable.syria, "+971"));

        // cOuntryCOdeslist.add(new COuntryCOde("United States", R.drawable.united_states, "+1"));
        cOuntryCOdeslist.add(new CountryCode("Uruguay", R.drawable.uruguay, "+598"));
        cOuntryCOdeslist.add(new CountryCode("Uzbekistan", R.drawable.uzbekistn, "+998"));
        cOuntryCOdeslist.add(new CountryCode("Vanuatu", R.drawable.vanuatu, "+678"));
        cOuntryCOdeslist.add(new CountryCode("Vatican", R.drawable.vaticancity, "+379"));
        cOuntryCOdeslist.add(new CountryCode("Venezuela", R.drawable.venezuela, "+58"));
        cOuntryCOdeslist.add(new CountryCode("Vietnam", R.drawable.vietnam, "+84"));
        cOuntryCOdeslist.add(new CountryCode("Wallis and Futuna", R.drawable.turkey, "+681"));
        cOuntryCOdeslist.add(new CountryCode("Western Sahara", R.drawable.turkey, "+212"));
        cOuntryCOdeslist.add(new CountryCode("Yemen", R.drawable.yemen, "+967"));
        cOuntryCOdeslist.add(new CountryCode("Zambia", R.drawable.zambia, "+260"));
        cOuntryCOdeslist.add(new CountryCode("Zimbabwe", R.drawable.zimbabwe, "+263"));

        return cOuntryCOdeslist;
    }
}
