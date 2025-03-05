package org.cipher.help;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Manipulate indices token with deltas of the indices
 */
public class IndexManipulatorProtobuf implements IndexManipulatorInterface {
    public String createIndicesToken(List<String> sourceList, List<String> searchList) {
        var byteArrayOutputStream = new ByteArrayOutputStream();
        // var gzip = new GZIPOutputStream(byteArrayOutputStream);
        var codedOutputStream = CodedOutputStream.newInstance(byteArrayOutputStream);

        var list = searchList.stream().map(bg -> sourceList.indexOf(bg))
                .filter(i -> i != -1)
                .sorted()
                .toList();

        try {
            codedOutputStream.writeRawByte(list.get(0));
            for (int i = 1; i < list.size(); i++) {
                codedOutputStream.writeInt32NoTag(list.get(i) - list.get(i - 1));
            }
            codedOutputStream.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
    }

    public List<String> decodeIndicesToken(List<String> sourceList, String indicesToken) {
        var byteArrayInputStream = Base64.getDecoder().wrap(new ByteArrayInputStream(indicesToken.getBytes()));
        var codedInputStream = CodedInputStream.newInstance(byteArrayInputStream);

        var list = new ArrayList<Integer>();

        try {
            while (!codedInputStream.isAtEnd()) {
                list.add(codedInputStream.readInt32());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        for (int i = 1; i < list.size(); i++) {
            list.set(i, list.get(i - 1) + list.get(i));
        }

        return list.stream().map(i -> sourceList.get(i)).toList();
    }
}
