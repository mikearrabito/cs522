package edu.stevens.cs522.chatserver.entities;

import androidx.room.TypeConverter;

import java.net.InetAddress;
import java.util.Date;

import edu.stevens.cs522.base.InetAddressUtils;

public class InetAddressConverter {
    @TypeConverter
    public static InetAddress fromAddress(String ipString) {
        return ipString == null ? null : InetAddressUtils.fromString(ipString);
    }

    @TypeConverter
    public static String addressToString(InetAddress address) {
        return address == null ? null : InetAddressUtils.toIpAddress(address);
    }
}
