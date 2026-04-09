public class CalculateCRC {

    public String calcular(String payload) {
        int polynomial = 0x1021;
        int crc = 0xFFFF;

        byte[] bytes = payload.getBytes(java.nio.charset.StandardCharsets.UTF_8);

        for (byte b : bytes) {
            crc ^= (b & 0xFF) << 8;

            for (int i = 0; i < 8; i++) {
                if ((crc & 0x8000) != 0) {
                    crc = (crc << 1) ^ polynomial;
                } else {
                    crc = crc << 1;
                }

                crc &= 0xFFFF;
            }
        }

        return String.format("%04X", crc);
    }

}
