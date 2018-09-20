package org.jim.bch.addr;

/**
 * Address type
 *
 * @author author
 */
public enum CashAddrType {
    P2PKH((byte) 0), P2SH((byte) 8);

    private final byte versionByte;

    CashAddrType(byte versionByte) {
        this.versionByte = versionByte;
    }

    public byte getVersionByte() {
        return versionByte;
    }
}
