package com.deverdie.checknetworkstatus.Class;

import com.deverdie.checknetworkstatus.UnicodeBOMInputStream;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;

public class TextUtil {

    public static InputStream readTextFilePath(String path) throws FileNotFoundException {
        File myFile = new File(path);
        FileInputStream fIn = new FileInputStream(myFile);
        return new FileInputStream(myFile);
    }

    public static BufferedReader resetEncoding(InputStream inputStream) throws IOException {
        UnicodeBOMInputStream ubis = new UnicodeBOMInputStream(inputStream);
        InputStreamReader isr = new InputStreamReader(ubis, ubis.getBOM().toString());
        return new BufferedReader(isr);
    }

    public static int bufferedReaderSize(BufferedReader bufferedReader) throws IOException {
        String line;
        int c = 0;
        while ((line = bufferedReader.readLine()) != null) {
            ++c;
        }
        return c;
    }

    public static InputStream readTextFileSMB(String strSMBUrl, String strSMBUser, String strSMBPass, String fileName) {
        try {
            SmbFile smbFile;
            if (strSMBUser.trim().equals("")) {
                smbFile = new SmbFile(strSMBUrl + fileName);
            } else {
                NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null, strSMBUser, strSMBPass);
                smbFile = new SmbFile(strSMBUrl + fileName, auth);
            }
            return new SmbFileInputStream(smbFile);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
