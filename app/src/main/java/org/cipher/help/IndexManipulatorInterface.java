package org.cipher.help;

import java.util.List;

public interface IndexManipulatorInterface {
    String createIndicesToken(List<String> sourceList, List<String> searchList);

    List<String> decodeIndicesToken(List<String> sourceList, String indicesToken);
}
