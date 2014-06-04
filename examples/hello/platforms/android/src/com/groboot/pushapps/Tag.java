package com.groboot.pushapps;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.groboot.pushapps.Constants.TYPES;

public class Tag {

	private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

	String Identifier;
	String Value;
	short TagType;

	public Tag(String identifier, int value) {
		this.Identifier = identifier;
		this.Value = String.valueOf(value);
		this.TagType = TYPES.NUMBER;
	}

	public Tag(String identifier, Date value) {
		this.Identifier = identifier;
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		this.Value = sdf.format(value);
		this.TagType = TYPES.DATE;
	}

	public Tag(String identifier, String value) {
		this.Identifier = identifier;
		this.Value = String.valueOf(value);
		this.TagType = TYPES.STRING;
	}

	public Tag(String identifier, boolean value) {
		this.Identifier = identifier;
		if (value) {
			this.Value = "1";
		} else {
			this.Value = "0";
		}
		this.TagType = TYPES.BOOLEAN;
	}
}
