package ovh.wpkg.lskg.server.types;

import lombok.Data;

public @Data class CommandOutput {
    private final String result; // Wynik komendy
    private final int statusCode; // Kod statusu (np. 0 = OK, 1 = ERROR)

    public CommandOutput(String result, int statusCode) {
        this.result = result;
        this.statusCode = statusCode;
    }

    // Getter dla wyniku
    public String getResult() {
        return result;
    }

    // Getter dla kodu statusu
    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public String toString() {
        return "CommandOutput{" +
                "result='" + result + '\'' +
                ", statusCode=" + statusCode +
                '}';
    }
}
