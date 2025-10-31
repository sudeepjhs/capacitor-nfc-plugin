package com.sudeep.capacitornfclugin;

import com.getcapacitor.Logger;

public class NFC {

    public String echo(String value) {
        Logger.info("Echo", value);
        return value;
    }
}
