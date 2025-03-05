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
 * Create large binary with same size as sourceList.
 * Keep indices in that binary by changing binary to 1 and rest to 0
 */
public class IndexManipulatorHelger implements IndexManipulatorInterface {

    public String createIndicesToken(List<String> sourceList, List<String> searchList) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BitOutputStream bitOutputStream = new BitOutputStream(byteArrayOutputStream, ByteOrder.LITTLE_ENDIAN);
        try {
            for (String kg : sourceList) {

                if (searchList.contains(kg)) {
                    bitOutputStream.writeBit(1);
                } else {
                    bitOutputStream.writeBit(0);
                }
            }
        } catch (EOFException e) {
            //Ignore
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        bitOutputStream.close();

        return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
    }

    public List<String> decodeIndicesToken(List<String> sourceList, String indicesToken) {
        var byteArrayInputStream = Base64.getDecoder().wrap(new ByteArrayInputStream(indicesToken.getBytes()));

        var bitInputStream = new BitInputStream(byteArrayInputStream, ByteOrder.LITTLE_ENDIAN);

        var list = new ArrayList<String>();
        var i = 0;
        try {
            while (true) {

                long r = bitInputStream.readBits(1);
                if (r == 1) {
                    list.add(sourceList.get(i));
                } else if (r == -1) {
                    break;
                }
                i++;

            }
        } catch (EOFException e) {
            //Ignore
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return list;
    }

}
