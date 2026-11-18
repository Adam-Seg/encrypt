package crypt;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.InputStream;
import java.util.List;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;

// Note Well: The testDecodeFile() must be run before the 
// testEncodeFile() so that the file story.txt will exist.

public class CryptTest {

    byte[] plain;
    byte[] coded;
    byte key;

    @BeforeEach
    public void setUp() {
        key = 0x67;
        plain = new byte[3];
        plain[0] = 0x5b;
        plain[1] = 0x0f;
        plain[2] = (byte) 0x8b;
        coded = new byte[4];
        coded[0] = key;
        coded[1] = 0x3c;
        coded[2] = 0x68;
        coded[3] = (byte) 0xec;
    }

    @AfterEach
    public void tearDown() {
        key = 0;
        plain = null;
        coded = null;
    }

    @Test
    public void testArrayEquals(){
        byte[] duplicate = plain.clone();
        assertFalse(plain == duplicate);
        assertTrue(areByteArraysEqual(plain, duplicate));
    }

    @Test
    public void testEncode() {
        try {
            InputStream input = new ByteArrayInputStream(plain);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            Crypt.encode(input, output, key);
            byte[] answer = output.toByteArray();
            assertTrue(areByteArraysEqual(coded, answer));
        } catch (IOException ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testDecode() {
        try {
            InputStream input = new ByteArrayInputStream(coded);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            Crypt.decode(input, output);
            byte[] answer = output.toByteArray();
            assertTrue(areByteArraysEqual(plain, answer));
        } catch (IOException ex) {
            fail(ex.getMessage());
        }
    }

    @Test
    public void testDecodeFile() {
        File inFile = new File("./coded");
        File outFile = new File("./story.txt");
        try {
            if (!inFile.exists()) {
                throw new Exception("file, coded, is missing");
            }
            if( outFile.exists() ){
                outFile.delete();
            }
            Crypt.decodeFile("coded", "story.txt");
            assertTrue(outFile.exists());
            assertEquals(inFile.length()-1, outFile.length());
        } catch(Exception ex){
            fail(ex.getMessage());
        }
    }

    @Test
    public void testEncodeFile(){
        File inFile = new File("./story.txt");
        File outFile = new File("./crypted");
        try{
            if (!inFile.exists()) {
                throw new Exception("file, story.txt, is missing");
            }
            if( outFile.exists() ){
                outFile.delete();
            }
            Crypt.encodeFile("story.txt", "crypted", (byte)115);
            assertTrue(areFilesEqual("coded", "crypted"));
        } catch(Exception ex){
            fail(ex.getMessage());
        }
    }

    @Test
    public void testVocabulary(){
        try {
            List<String> vocab = Crypt.buildVocabulary("story.txt");
            assertEquals(127, vocab.size());
            assertEquals("a", vocab.get(0));
            assertEquals("you", vocab.get(126));
        } catch(IOException ex){
            fail(ex.getMessage());
        }
    }

    public boolean areFilesEqual(String filename1, String filename2) throws IOException {
        InputStream input1 = null;
        InputStream input2 = null;
        try {
            input1 = new FileInputStream(filename1);
            input2 = new FileInputStream(filename2);
            int nextByte;
            while((nextByte = input1.read()) != -1){
                if(nextByte != input2.read()){
                    return false;
                }
            }
            return input2.read() == -1;
        } finally {
            if(input1 != null){
                input1.close();
            }
            if(input2 != null){
                input2.close();
            }
        }

    }

    private boolean areByteArraysEqual(byte[] array1, byte[] array2){
        if( array1.length != array2.length ) return false;
        for(int i = 0; i < array1.length; ++i){
            if(array1[i] != array2[i]) return false;
        }
        return true;
    }
}
