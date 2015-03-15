package com.scanner.bth.bluetoothscanner;

/**
 * Created by shaon0000 on 2015-03-07.
 */
public class BeaconParser {


    public static class BeaconData {

        public BeaconData(String beacon_prefix, String proximity_uuid, String major, String minor, String tx) {
            this.beacon_prefix = beacon_prefix;
            this.proximity_uuid = proximity_uuid;
            this.major = major;
            this.minor = minor;
            this.tx = tx;

        }

        String major;
        String minor;
        String beacon_prefix;
        String proximity_uuid;
        String tx;

        public String getMajor() {
            return major;
        }

        public String getMinor() {
            return minor;
        }

        public String getBeacon_prefix() {
            return beacon_prefix;
        }

        public String getProximity_uuid() {
            return proximity_uuid;
        }
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static String subBytesToHex(byte[] bytes, int start, int end) {
        int length = end - start;
        byte[] sub_array = new byte[length];

        for (int i = 0; i < length; i++) {
            sub_array[i] = bytes[i];
        }

        return bytesToHex(sub_array);
    }

    public static BeaconData read(byte[] bytes) {
        int expected_size = 13 + 30 + 3;
        if (bytes.length < expected_size) {
            return null;
        }

        int[] prefix = {13, 9};
        int[] px_uuid = {prefix[0] + prefix[1], 16};
        int[] major = {px_uuid[0] + px_uuid[1], 2};
        int[] minor = {major[0] + major[1], 2};

        // We need to do a quick verify here to make sure prefix makes sense.

        return new BeaconData(subBytesToHex(bytes, prefix[0], prefix[0] + prefix[1]),
                subBytesToHex(bytes, px_uuid[0], px_uuid[0] + px_uuid[1]),
                subBytesToHex(bytes, major[0], major[0] + major[1]),
                subBytesToHex(bytes, minor[0], minor[0] + minor[1]),
                "");
    }
}
