/*
 * This file is part of Metamail
 *
 * Copyright (C) 2012 Igalia, S.L.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.igalia.metamail.utils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

/**
 * Helper class for retrieving fields from an email
 * 
 * @author Diego Pino García <dpino@igalia.com>
 * 
 */
public class MailRecord {

	private static final RegExp emailRegExp = RegExp.create("<(.*)>"); 
	
	private MimeMessage msg;

	private Calendar sentDate;
	
	public static MailRecord create(Session s, InputStream input) throws MessagingException {
		return MailRecord.create(new MimeMessage(s, input));
	}
	
	public static MailRecord create(MimeMessage msg) {
		return new MailRecord(msg);
	}
	
	private MailRecord(MimeMessage msg) {
		this.msg = msg;		
	}

	public String getFrom() {
		try {
			Address[] addresses = msg.getFrom();
			if (addresses != null && addresses.length > 0) {
                return addresses[0].toString();
			}
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		return "";
	}

	public List<String> getTo() {
		List<String> result = new ArrayList<String>();
		try {				
			Address[] addresses = msg.getRecipients(Message.RecipientType.TO);
			if (addresses != null && addresses.length > 0) {
				for (Address address : addresses) {
					List<String> parts = emailRegExp.evaluate(address
							.toString());
					if (!parts.isEmpty()) {
						result.add(parts.get(0));
					} else {
						result.add(address.toString());
					}
				}
			}
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		return result;
	}
		
	public int getYear() {
		return getSentDate().get(Calendar.YEAR);
	}
	
	private Calendar getSentDate() {
		if (sentDate == null) {
			sentDate = Calendar.getInstance();
			try {
				sentDate.setTime(msg.getSentDate());
			} catch (MessagingException e) {
				e.printStackTrace();
			}
		}
		return sentDate;
	}
	
	public int getMonth() {
		return getSentDate().get(Calendar.MONTH);
		
	}
	
	public int getDayOfWeek() {
		return getSentDate().get(Calendar.DAY_OF_WEEK);
		
	}
	
	public int getHour() {
		return getSentDate().get(Calendar.HOUR_OF_DAY);		
	}		

	/**
	 * 
	 * @author Diego Pino García <dpino@igalia.com>
	 * 
	 *         Helper class for handling regular expressions
	 * 
	 */
	static class RegExp {

		private Pattern pattern;

		private RegExp(String regexp) {
			this.pattern = Pattern.compile(regexp);
		}

		public static RegExp create(String regexp) {
			return new RegExp(regexp);
		}

		public List<String> evaluate(String line) {
			List<String> result = new ArrayList<String>();

			int count = 1;
			Matcher matcher = pattern.matcher(line);
			while (matcher.find()) {
				result.add(matcher.group(count++));
			}
			return result;
		}
		
	}

	public String getMessageID() throws MessagingException {
		return msg.getMessageID();
	}

	public int getSize() throws MessagingException {
		return msg.getSize();
	}

	public String getSubject() throws MessagingException {
		return msg.getSubject();
	}
	
}
