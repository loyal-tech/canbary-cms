package com.adopt.apigw.modules.Reseller.mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Change;
import org.javers.core.diff.Diff;
import org.javers.core.diff.changetype.ValueChange;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class WifiUtils {

	static Javers javers = JaversBuilder.javers().build();


	final Logger log = LoggerFactory.getLogger(WifiUtils.class);

	private final static String LOCALHOST_IPV4 = "127.0.0.1";
	private final static String LOCALHOST_IPV6 = "0:0:0:0:0:0:0:1";

	public static String getUpdatedDiff(Object o1, Object o2) {
		String updated = "";
		try {
			Diff diff = javers.compare(o1, o2);
			if (diff.hasChanges()) {
				List<Change> changes = diff.getChanges();
				for (Change change : changes) {
					if (change instanceof ValueChange) {
						ValueChange valChange = (ValueChange) change;
						if (!(valChange.getPropertyName().equals("createdOn")
								|| valChange.getPropertyName().equals("lastModifiedOn")
								|| valChange.getPropertyName().equals("createdBy")
								|| valChange.getPropertyName().equals("lastModifiedBy"))) {
							updated = updated + "property: " + valChange.getPropertyName() + " from "
									+ valChange.getLeft() + " to " + valChange.getRight() + " ,";
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			return null;
		}
		return updated;
	}


	public static boolean isValidFormat(String format, String value) {
		Date date = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			date = sdf.parse(value);
			if (!value.equals(sdf.format(date))) {
				date = null;
			}
		} catch (ParseException ex) {
			return false;
		}
		return date != null;
	}

	public static boolean validateEmailAddress(String emailId)
	{
		try
		{
			String regex = "^(.+)@(.+)$";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(emailId);
			return matcher.matches();
		}
		catch (RuntimeException e)
		{
			throw new RuntimeException(e.getMessage());
		}
	}

	public boolean isJSONValid(String jsonStr) {
		try {
			new JSONObject(jsonStr);
		} catch (JSONException ex) {
			// e.g. in case JSONArray is valid as well...
			try {
				new JSONArray(jsonStr);
			} catch (JSONException ex1) {
				return false;
			}
		}
		return true;
	}

	public static boolean isValidIPAddress(String ip) {

		// Regex for digit from 0 to 255.
		String zeroTo255 = "(\\d{1,2}|(0|1)\\" + "d{2}|2[0-4]\\d|25[0-5])";

		// Regex for a digit from 0 to 255 and
		// followed by a dot, repeat 4 times.
		// this is the regex to validate an IP address.
		String regex = zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255;

		// Compile the ReGex
		Pattern p = Pattern.compile(regex);

		// If the IP address is empty
		// return false
		if (ip == null) {
			return false;
		}

		// Pattern class contains matcher() method
		// to find matching between given IP address
		// and regular expression.
		Matcher m = p.matcher(ip);

		// Return if the IP address
		// matched the ReGex
		return m.matches();
	}

	public static String getClientIp() throws SocketException {
		Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
		String ipAddress = null;
		for (NetworkInterface netint : Collections.list(nets)) {
			if (netint.isUp() && netint.getName().contains("wlan")) {
				for (InterfaceAddress ad : netint.getInterfaceAddresses()) {
					if (isValidIPAddress(ad.getAddress().toString().replace("/", ""))) {
						ipAddress = ad.getAddress().toString().replace("/", "");
					}
				}
			}
		}
		return ipAddress;
	}
}