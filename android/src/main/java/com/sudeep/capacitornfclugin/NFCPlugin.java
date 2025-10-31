package com.sudeep.capacitornfclugin;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import android.content.Intent;
import android.nfc.Tag;
import android.app.Activity;

@CapacitorPlugin(name = "NFC")
public class NFCPlugin extends Plugin {

    private NFC implementation;

    @Override
    public void load() {
        super.load();
        implementation = new NFC(getActivity());
    }

    @PluginMethod
    public void echo(PluginCall call) {
        String value = call.getString("value");

        JSObject ret = new JSObject();
        ret.put("value", implementation.echo(value));
        call.resolve(ret);
    }

    @PluginMethod
    public void isAvailable(PluginCall call) {
        boolean available = implementation.isAvailable();
        JSObject ret = new JSObject();
        ret.put("available", available);
        call.resolve(ret);
    }

    @PluginMethod
    public void scanTag(PluginCall call) {
        // This method will be called when NFC tag is discovered
        // The actual scanning is handled by onNewIntent
        // For now, just enable foreground dispatch
        implementation.enableForegroundDispatch();
        call.resolve();
    }

    @PluginMethod
    public void writeTag(PluginCall call) {
        String data = call.getString("data");
        if (data == null) {
            call.reject("Data is required");
            return;
        }
        // Writing will be handled when tag is discovered
        implementation.enableForegroundDispatch();
        call.resolve();
    }

    @Override
    protected void handleOnNewIntent(Intent intent) {
        super.handleOnNewIntent(intent);
        String action = intent.getAction();
        if (android.nfc.NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action) ||
            android.nfc.NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
            Tag tag = intent.getParcelableExtra(android.nfc.NfcAdapter.EXTRA_TAG);
            if (tag != null) {
                String tagId = implementation.getTagId(tag);
                String data = implementation.readTag(intent);
                if (data != null) {
                    JSObject ret = new JSObject();
                    ret.put("tagId", tagId);
                    ret.put("data", data);
                    notifyListeners("nfcTagScanned", ret);
                }
            }
        }
    }

    @Override
    protected void handleOnResume() {
        super.handleOnResume();
        implementation.enableForegroundDispatch();
    }

    @Override
    protected void handleOnPause() {
        super.handleOnPause();
        implementation.disableForegroundDispatch();
    }
}
