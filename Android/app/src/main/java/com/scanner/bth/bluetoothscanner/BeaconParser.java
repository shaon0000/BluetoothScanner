package com.scanner.bth.bluetoothscanner;

/**
 * We use this to parse the advertisement ID and pull up the right data. This in conjunction with
 * some filter mechanism can be used to look for the right devices.
 *
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

        private String major;
        private String minor;
        private String beacon_prefix;
        private String proximity_uuid;
        private String tx;

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

        public String getTx() {
            return tx;
        }
    }


    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    // Convert bytes to a hex string
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    // Convert a subarray to a hex string
    public static String subBytesToHex(byte[] bytes, int start, int end) {
        int length = end - start + 1;
        byte[] sub_array = new byte[length];

        for (int i = 0; i < length; i++) {
            sub_array[i] = bytes[i + start];
        }

        return bytesToHex(sub_array);
    }

    public static BeaconData read(byte[] bytes) {
        int expected_size = 13 + 30 + 3;
        if (bytes.length < expected_size) {
            return null;
        }

        int[] prefix = {0, 8};
        int[] px_uuid = {9, 24};
        int[] major = {25, 26};
        int[] minor = {27, 28};

        // We need to do a quick verify here to make sure prefix makes sense. Not all devices
        // will properly fall into this mold. We can alternatively do a quick talk to make sure
        // this is in fact the device I'm looking for.

        return new BeaconData(subBytesToHex(bytes, prefix[0],prefix[1]),
                subBytesToHex(bytes, px_uuid[0], px_uuid[1]),
                String.valueOf(Integer.parseInt(subBytesToHex(bytes, major[0], major[1]), 16)),
                String.valueOf(Integer.parseInt(subBytesToHex(bytes, minor[0], minor[1]), 16)),
                "");
    }
}
