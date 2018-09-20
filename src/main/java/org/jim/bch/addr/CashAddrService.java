package org.jim.bch.addr;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * Decode/encode CashAddr address
 *
 * @author author
 */
public class CashAddrService {

    public static final String SEPARATOR = ":";

    public static final String MAIN_NET_PREFIX = "bitcoincash";

    public static final String TEST_NET_PREFIX = "bchtest";

    private static final BigInteger[] POLYMOD_GENERATORS = new BigInteger[] {
            new BigInteger("98f2bc8e61", 16),
            new BigInteger("79b76d99e2", 16),
            new BigInteger("f33e5fb3c4", 16),
            new BigInteger("ae2eabe2a8", 16),
            new BigInteger("1e4f43e470", 16)
    };

    private static final BigInteger POLYMOD_AND_CONSTANT =
            new BigInteger("07ffffffff", 16);

    public static String encodeCashAddressWithPrefix(CashNetwork network, byte[] hash, CashAddrType addressType) {
        // prefix
        String prefixString = getPrefixString(network);

        // address
        String cashAddress = encodeCashAddress(network, hash, addressType);

        return prefixString + SEPARATOR + cashAddress;
    }

    public static String encodeCashAddress(CashNetwork network, byte[] hash, CashAddrType addressType) {
        // prefix
        String prefixString = getPrefixString(network);
        byte[] prefixBytes = CashUtils.covertBytes(prefixString);

        // add address type
        byte[] payloadBytes = CashUtils.concatenateByteArrays(new byte[] { addressType.getVersionByte() }, hash);
        payloadBytes = CashUtils.convertBits(payloadBytes, 8, 5, false);

        // checksum
        byte[] allChecksumInput = CashUtils.concatenateByteArrays(
                CashUtils.concatenateByteArrays(
                        CashUtils.concatenateByteArrays(prefixBytes, new byte[] { 0 }),
                        payloadBytes),
                new byte[] { 0, 0, 0, 0, 0, 0, 0, 0 });
        byte[] checksumBytes = calculateChecksumBytesPolymod(allChecksumInput);
        checksumBytes = CashUtils.convertBits(checksumBytes, 8, 5, true);

        // address
        String cashAddress = CashBase32.encode(CashUtils.concatenateByteArrays(payloadBytes, checksumBytes));
        return cashAddress;
    }

    public static CashAddr decodeCashAddress(CashNetwork network, String cashAddress) {
        if (!isValidCashAddress(network, cashAddress)) {
            throw new RuntimeException("Invalid address: " + cashAddress);
        }

        CashAddr decoded = new CashAddr();

        // prefix
        String[] addressParts = cashAddress.split(SEPARATOR);
        if (addressParts.length == 2) {
            decoded.setPrefix(addressParts[0]);
            cashAddress = addressParts[1];
        }

        // address
        byte[] addressData = CashBase32.decode(cashAddress);
        addressData = Arrays.copyOfRange(addressData, 0, addressData.length - 8);
        addressData = CashUtils.convertBits(addressData, 5, 8, true);

        byte versionByte = addressData[0];
        byte[] hash = Arrays.copyOfRange(addressData, 1, addressData.length);

        decoded.setAddressType(getAddressTypeFromVersionByte(versionByte));
        decoded.setHash(hash);

        return decoded;
    }

    public static byte[] decodePubKeyHash(CashNetwork network, String cashAddress) {
        CashAddr decoded = decodeCashAddress(network, cashAddress);
        return decoded.getHash();
    }

    public static boolean isValidCashAddress(CashNetwork network, String cashAddress) {
        try {
            String prefix;
            if (cashAddress.contains(SEPARATOR)) {
                String[] split = cashAddress.split(SEPARATOR);
                prefix = split[0];
                cashAddress = split[1];

                if (MAIN_NET_PREFIX.equals(prefix) && !network.equals(CashNetwork.MAIN)) {
                    return false;
                }
                if (TEST_NET_PREFIX.equals(prefix) && !network.equals(CashNetwork.TEST)) {
                    return false;
                }
            } else {
                prefix = (network == CashNetwork.MAIN) ? MAIN_NET_PREFIX : TEST_NET_PREFIX;
            }

            if (!isSingleCase(cashAddress)) {
                return false;
            }

            cashAddress = cashAddress.toLowerCase();

            byte[] checksumData = CashUtils.concatenateByteArrays(
                    CashUtils.concatenateByteArrays(CashUtils.covertBytes(prefix), new byte[] { 0x00 }),
                    CashBase32.decode(cashAddress));

            byte[] calculateChecksumBytesPolymod = calculateChecksumBytesPolymod(checksumData);
            return new BigInteger(calculateChecksumBytesPolymod).compareTo(BigInteger.ZERO) == 0;
        } catch (RuntimeException re) {
            return false;
        }
    }

    private static boolean isSingleCase(String cashAddress) {
        if (cashAddress.equals(cashAddress.toLowerCase())) {
            return true;
        }
        if (cashAddress.equals(cashAddress.toUpperCase())) {
            return true;
        }
        return false;
    }

    private static byte[] calculateChecksumBytesPolymod(byte[] checksumInput) {
        BigInteger c = BigInteger.ONE;

        for (int i = 0; i < checksumInput.length; i++) {
            byte c0 = c.shiftRight(35).byteValue();
            c = c.and(POLYMOD_AND_CONSTANT).shiftLeft(5)
                    .xor(new BigInteger(String.format("%02x", checksumInput[i]), 16));

            if ((c0 & 0x01) != 0) {
                c = c.xor(POLYMOD_GENERATORS[0]);
            }
            if ((c0 & 0x02) != 0) {
                c = c.xor(POLYMOD_GENERATORS[1]);
            }
            if ((c0 & 0x04) != 0) {
                c = c.xor(POLYMOD_GENERATORS[2]);
            }
            if ((c0 & 0x08) != 0) {
                c = c.xor(POLYMOD_GENERATORS[3]);
            }
            if ((c0 & 0x10) != 0) {
                c = c.xor(POLYMOD_GENERATORS[4]);
            }
        }

        byte[] checksum = c.xor(BigInteger.ONE).toByteArray();
        if (checksum.length == 5) {
            return checksum;
        } else {
            byte[] newChecksumArray = new byte[5];

            System.arraycopy(checksum, Math.max(0, checksum.length - 5), newChecksumArray,
                    Math.max(0, 5 - checksum.length), Math.min(5, checksum.length));

            return newChecksumArray;
        }
    }

    private static String getPrefixString(CashNetwork network) {
        switch (network) {
            case MAIN:
                return MAIN_NET_PREFIX;
            case TEST:
                return TEST_NET_PREFIX;
            default:
                throw new RuntimeException("Unknown network");
        }
    }

    private static CashAddrType getAddressTypeFromVersionByte(byte versionByte) {
        for (CashAddrType addressType : CashAddrType.values()) {
            if (addressType.getVersionByte() == versionByte) {
                return addressType;
            }
        }
        throw new RuntimeException("Unknown version byte: " + versionByte);
    }
}
