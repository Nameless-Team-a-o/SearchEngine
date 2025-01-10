package com.nameless.storage_server.service.processor;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class ZipProcessor {
    public void processZipEntries(ZipInputStream zis, Consumer<ZipEntry> entryConsumer) throws IOException {
        ZipEntry zipEntry;
        while ((zipEntry = zis.getNextEntry()) != null) {
            entryConsumer.accept(zipEntry);
            zis.closeEntry();
        }
    }
}