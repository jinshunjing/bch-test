package org.jim.bch.addr;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Utils;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet2Params;

/**
 * HD Key
 *
 * @author author
 */
public class HdKeyService {

    public static final String XPUB = "xpub";

    public static String deriveBchAddress(int network, String xPubKey, int changeType, int sequence) {
        DeterministicKey dKey = deriveChildKey(xPubKey, changeType, sequence);
        byte[] pubKeyHash = dKey.getPubKeyHash();
        System.out.println(Utils.HEX.encode(pubKeyHash));

        CashNetwork cashNetwork = (0 == network) ? CashNetwork.MAIN : CashNetwork.TEST;
        return CashAddrService.encodeCashAddress(cashNetwork, pubKeyHash, CashAddrType.P2PKH);
    }

    public static String toBtcAddress(int network, String bchAddr) {
        CashNetwork cashNetwork = (0 == network) ? CashNetwork.MAIN : CashNetwork.TEST;
        NetworkParameters params = (0 == network) ? MainNetParams.get() : TestNet2Params.get();
        byte[] pubKeyHash = CashAddrService.decodePubKeyHash(cashNetwork, bchAddr);

        return new Address(params, pubKeyHash).toBase58();
    }

    private static DeterministicKey deriveChildKey(String xPubKey, int changeType, int sequence) {
        NetworkParameters params = (xPubKey.startsWith(XPUB)) ? MainNetParams.get() : TestNet2Params.get();
        DeterministicKey xPubMaster = DeterministicKey.deserializeB58(xPubKey, params);

        DeterministicKey key0 = HDKeyDerivation.deriveChildKey(xPubMaster, new ChildNumber(changeType, false));
        DeterministicKey dKey = HDKeyDerivation.deriveChildKey(key0, new ChildNumber(sequence, false));

        return dKey;
    }

}
