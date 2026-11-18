package crypt;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Scanner;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Set;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class Crypt {

    public static void encode(InputStream input, OutputStream output, byte key) throws IOException {
        output.write(key);
        int nextByte;
        while ((nextByte = input.read()) != -1) {
            output.write(nextByte ^ key);
        }
    }

    public static void decode(InputStream input, OutputStream output) throws IOException {
        int key = input.read();
        if (key == -1) {
            throw new IOException("missing key");
        }
        int nextByte;
        while ((nextByte = input.read()) != -1) {
            output.write(nextByte ^ key);
        }
    }

    public static void encodeFile(String inFilename, String outFilename, byte key) throws IOException {
        InputStream input = null;
        OutputStream output = null;

        try {
            input = new FileInputStream(inFilename);
            output = new FileOutputStream(outFilename);
            encode(input, output, key);
        } finally {
            if (input != null) {
                input.close();
            }
            if (output != null) {
                output.close();
            }
        }
    }

    public static void decodeFile(String inFilename, String outFilename) throws IOException {
        InputStream input = null;
        OutputStream output = null;
        try {
            input = new FileInputStream(inFilename);
            output = new FileOutputStream(outFilename);
            decode(input, output);
        } finally {
            if (input != null) {
                input.close();
            }
            if (output != null) {
                output.close();
            }
        }
    }

    // buildVocabulary(filename)
    // read all the words in the file referenced by filename
    // make them unique, sort them alphabetically, and return a list
    public static List<String> buildVocabulary(String filename) throws IOException {
        Scanner input = null;
        try {
            input = new Scanner(new File(filename));
            input.useDelimiter("[ \r\n\t:\\-\\.,\"!?]+");
            Set<String> uniqueWords = new HashSet<String>();
            while (input.hasNext()) {
                uniqueWords.add(input.next().toLowerCase());
            }
            List<String> vocab = new ArrayList<String>(uniqueWords);
            Collections.sort(vocab);
            return vocab;
        } finally {
            if (input != null) {
                input.close();
            }
        }
    }
}
