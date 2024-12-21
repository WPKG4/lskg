package ovh.wpkg.lskg.server.types;

import lombok.Data;

public @Data class WPKGPayload {
    private final byte[] payload;

    public WPKGPayload(byte[] payload) {
        this.payload = payload;
    }

    public  byte[] getPayload() {
        return payload;
    }

    public String getPayloadAsString() {
        return new String(payload); // Konwersja na String, jeśli dane są tekstowe
    }

    @Override
    public String toString() {
        return "WPKGPayload{" +
                "payload=" + new String(payload) +
                '}';
    }
}
