package org.cipher.help;

import com.helger.commons.io.stream.BitInputStream;
import com.helger.commons.io.stream.BitOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Manipulate indices token with deltas of the indices
 */
public class IndexManipulatorHelgerFixedBits implements IndexManipulatorInterface {

    public static final int A_NUMBER_OF_BITS = 13;

    public String createIndicesToken(List<String> sourceList, List<String> searchList) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BitOutputStream bitOutputStream = new BitOutputStream(byteArrayOutputStream, ByteOrder.LITTLE_ENDIAN);


        var list = searchList.stream().map(bg -> sourceList.indexOf(bg))
                .filter(i -> i != -1)
                .sorted()
                .toList();

        try {
            bitOutputStream.writeBits(list.get(0), A_NUMBER_OF_BITS);
            for (int i = 1; i < list.size(); i++) {
                bitOutputStream.writeBits(list.get(i) - list.get(i - 1), A_NUMBER_OF_BITS);
            }
            bitOutputStream.flush();
            bitOutputStream.close();
        }  catch (EOFException e) {
            //Ignore
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
    }

    public List<String> decodeIndicesToken(List<String> sourceList, String indicesToken) {
        var byteArrayInputStream = Base64.getDecoder().wrap(new ByteArrayInputStream(indicesToken.getBytes()));
        var bitInputStream = new BitInputStream(byteArrayInputStream, ByteOrder.LITTLE_ENDIAN);


        var list = new ArrayList<Integer>();

        try {
            while (true) {
                var r = bitInputStream.readBits(A_NUMBER_OF_BITS);
                if (r == -1) {
                    break;
                }
                list.add(r);
            }
        } catch (EOFException e) {
            //Ignore
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        for (int i = 1; i < list.size(); i++) {
            list.set(i, list.get(i - 1) + list.get(i));
        }

        return list.stream().map(i -> sourceList.get(i)).toList();
    }
}
