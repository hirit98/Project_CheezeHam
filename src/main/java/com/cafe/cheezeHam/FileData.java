package com.cafe.cheezeHam;

import lombok.Getter;

@Getter
public class FileData {
    private final String encodedName;
    private final String decodedName;

    public FileData(String encodedName, String decodedName) {
        this.encodedName = encodedName;
        this.decodedName = decodedName;
    }
}