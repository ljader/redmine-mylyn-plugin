package net.sf.redmine_mylyn.internal.api.parser.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class DateAdapter extends XmlAdapter<String, Date> {

	@Override
	public String marshal(Date arg0) throws Exception {
		return null;
	}

	@Override
	public Date unmarshal(String arg0) throws Exception {
		try {
			SimpleDateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.UK);
			return df.parse(arg0);
		} catch (ParseException e) {
			; //can nothing do
		}
		return null;
	}

}
