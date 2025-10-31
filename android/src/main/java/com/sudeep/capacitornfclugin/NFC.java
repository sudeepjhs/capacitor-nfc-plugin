package com.sudeep.capacitornfclugin;

import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.content.Context;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.IsoDep;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.Parcelable;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import com.getcapacitor.Logger;
import com.getcapacitor.PluginCall;

public class NFC {

    private NfcAdapter nfcAdapter;
    private Activity activity;
    private PendingIntent pendingIntent;
    private IntentFilter[] intentFiltersArray;
    private String[][] techListsArray;

    public NFC(Activity activity) {
        this.activity = activity;
        this.nfcAdapter = NfcAdapter.getDefaultAdapter(activity);
        this.pendingIntent = PendingIntent.getActivity(
            activity, 0, new Intent(activity, activity.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_MUTABLE);
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndef.addDataType("*/*");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }
        intentFiltersArray = new IntentFilter[]{ndef};
        techListsArray = new String[][]{new String[]{NfcF.class.getName()}};
    }

    public String echo(String value) {
        Logger.info("Echo", value);
        return value;
    }

    public boolean isAvailable() {
        return nfcAdapter != null && nfcAdapter.isEnabled();
    }

    public void enableForegroundDispatch() {
        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(activity, pendingIntent, intentFiltersArray, techListsArray);
        }
    }

    public void disableForegroundDispatch() {
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(activity);
        }
    }

    public String readTag(Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag == null) {
            return null;
        }

        Ndef ndef = Ndef.get(tag);
        if (ndef == null) {
            return null;
        }

        NdefMessage ndefMessage = ndef.getCachedNdefMessage();
        if (ndefMessage == null) {
            return null;
        }

        NdefRecord[] records = ndefMessage.getRecords();
        for (NdefRecord ndefRecord : records) {
            if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                try {
                    byte[] payload = ndefRecord.getPayload();
                    String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
                    int languageCodeLength = payload[0] & 0063;
                    return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
                } catch (Exception e) {
                    Logger.error("Error reading NDEF record", e);
                }
            }
        }
        return null;
    }

    public boolean writeTag(Intent intent, String data) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag == null) {
            return false;
        }

        NdefMessage ndefMessage = createNdefMessage(data);
        if (ndefMessage == null) {
            return false;
        }

        try {
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();
                if (!ndef.isWritable()) {
                    return false;
                }
                if (ndef.getMaxSize() < ndefMessage.getByteArrayLength()) {
                    return false;
                }
                ndef.writeNdefMessage(ndefMessage);
                return true;
            } else {
                NdefFormatable format = NdefFormatable.get(tag);
                if (format != null) {
                    try {
                        format.connect();
                        format.format(ndefMessage);
                        return true;
                    } catch (IOException e) {
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            Logger.error("Error writing to tag", e);
        }
        return false;
    }

    private NdefMessage createNdefMessage(String text) {
        try {
            byte[] textBytes = text.getBytes();
            byte[] textPayload = new byte[textBytes.length + 3];
            textPayload[0] = 0x02; // Status byte: UTF-8 encoding, no language code
            textPayload[1] = 0x65; // Language code length (en)
            textPayload[2] = 0x6E; // Language code (en)
            System.arraycopy(textBytes, 0, textPayload, 3, textBytes.length);
            NdefRecord textRecord = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], textPayload);
            return new NdefMessage(textRecord);
        } catch (Exception e) {
            Logger.error("Error creating NDEF message", e);
        }
        return null;
    }

    public String getTagId(Tag tag) {
        byte[] id = tag.getId();
        if (id != null) {
            StringBuilder sb = new StringBuilder();
            for (byte b : id) {
                sb.append(String.format("%02X", b));
            }
            return sb.toString();
        }
        return null;
    }
}
