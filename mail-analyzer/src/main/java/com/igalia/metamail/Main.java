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

package com.igalia.metamail;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;

import org.apache.commons.io.FileUtils;

import com.igalia.metamail.utils.MailRecord;


/**
 * 
 * @author Diego Pino García <dpino@igalia.com>
 *
 */
public class Main {
	
    public static void main( String[] args ) {
    	try {
        	String filename = "../enron-importer/data/maildir/lay-k/sent/1.";
        	byte[] body = FileUtils.readFileToByteArray(new File(filename));
    		InputStream input = new ByteArrayInputStream(body);
			Session s = Session.getDefaultInstance(new Properties());
        	        	
			MailRecord mail = MailRecord.create(s, input);
	        System.out.println( "To: " + mail.getTo());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}    	
    }
}
