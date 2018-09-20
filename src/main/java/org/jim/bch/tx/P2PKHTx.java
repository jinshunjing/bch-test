package org.jim.bch.tx;

import org.bitcoinj.core.*;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet2Params;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.jim.bch.addr.HdKeyService;

/**
 * P2PKH transaction
 *
 * @author author
 */
public class P2PKHTx {

    /**
     * Create a transaction: 1 vin, 2 vouts
     *
     * @param network
     * @param vinTxid
     * @param vinIndex
     * @param voutAddr
     * @param amount
     * @param changeAddr
     * @param change
     * @return
     */
    public static String createRawTx(int network,
                                     String vinTxid, long vinIndex,
                                     String voutAddr, long amount,
                                     String changeAddr, long change) {
        NetworkParameters networkParam = (0 == network) ? MainNetParams.get() : TestNet2Params.get();
        Transaction transaction = new Transaction(networkParam);

        TransactionOutPoint outPoint = new TransactionOutPoint(networkParam, vinIndex, Sha256Hash.wrap(Utils.HEX.decode(vinTxid)));
        TransactionInput vin = new TransactionInput(networkParam, transaction, new byte[0], outPoint);
        transaction.addInput(vin);

        // covert from CashAddr to Legacy Address
        voutAddr = HdKeyService.toBtcAddress(network, voutAddr);
        changeAddr = HdKeyService.toBtcAddress(network, changeAddr);

        transaction.addOutput(Coin.valueOf(amount), Address.fromBase58(networkParam, voutAddr));
        transaction.addOutput(Coin.valueOf(change), Address.fromBase58(networkParam, changeAddr));

        return Utils.HEX.encode(transaction.bitcoinSerialize());
    }

    /**
     * Calculate the hash for signature
     * Reference: https://github.com/bitcoin/bips/blob/master/bip-0143.mediawiki
     *
     * @param outpoint
     * @param amount
     * @param redeemScript
     * @param outputs
     * @return
     */
    public static String hashForSignature(String outpoint, String amount, String redeemScript,
                                          String outputs) {
        StringBuilder buffer = new StringBuilder();

        // version
        String nVersion = "01000000";
        buffer.append(nVersion);
        System.out.println("nVersion: " + nVersion);

        // hashPrevouts
        Sha256Hash hashPrevouts = Sha256Hash.twiceOf(Utils.HEX.decode(outpoint));
        buffer.append(hashPrevouts.toString());
        System.out.println("hashPrevouts: " + hashPrevouts.toString());

        // hashSequence
        String nSequence = "ffffffff";
        Sha256Hash hashSequence = Sha256Hash.twiceOf(Utils.HEX.decode(nSequence));
        buffer.append(hashSequence.toString());
        System.out.println("hashSequence: " + hashSequence.toString());

        // outpoint
        buffer.append(outpoint);
        System.out.println("outpoint: " + outpoint);

        // scriptCode
        buffer.append(redeemScript);
        System.out.println("scriptCode: " + redeemScript);

        // amount
        buffer.append(amount);
        System.out.println("amount: " + amount);

        // nSequence
        buffer.append(nSequence);
        System.out.println("nSequence: " + nSequence);

        // hashOutputs
        Sha256Hash hashOutputs = Sha256Hash.twiceOf(Utils.HEX.decode(outputs));
        buffer.append(hashOutputs.toString());
        System.out.println("hashOutputs: " + hashOutputs.toString());

        // nLockTime
        String nLockTime = "00000000";
        buffer.append(nLockTime);
        System.out.println("nLockTime: " + nLockTime);

        // FIXME: signHash is 41, not 01
        String signHash = "41000000";
        buffer.append(signHash);
        System.out.println("signHash: " + signHash);

        Sha256Hash hash = Sha256Hash.twiceOf(Utils.HEX.decode(buffer.toString()));
        return hash.toString();
    }

    /**
     * Calculate the signature
     *
     * @param network
     * @param hashHex
     * @param prvKey
     * @return
     */
    public static String signTxInput(int network, String hashHex, String prvKey) {
        NetworkParameters networkParam = (0 == network) ? MainNetParams.get() : TestNet2Params.get();
        Sha256Hash hash = Sha256Hash.wrap(hashHex);

        ECKey ecKey = DumpedPrivateKey.fromBase58(networkParam, prvKey).getKey();
        ECKey.ECDSASignature signature = ecKey.sign(hash);
        return Utils.HEX.encode(signature.encodeToDER());
    }

    /**
     * Add signature into the transaction
     *
     * @param network
     * @param rawTx
     * @param vin
     * @param signature
     * @param pubKey
     * @return
     */
    public static String signRawTx(int network, String rawTx, int vin, String signature, String pubKey) {
        NetworkParameters networkParam = (0 == network) ? MainNetParams.get() : TestNet2Params.get();
        Transaction tx = new Transaction(networkParam, Utils.HEX.decode(rawTx));

        ECKey.ECDSASignature sig = TransactionSignature.decodeFromDER(Utils.HEX.decode(signature));
        ECKey ecKey = ECKey.fromPublicOnly(Utils.HEX.decode(pubKey));
        Script script = ScriptBuilder.createInputScript(new TransactionSignature(sig, Transaction.SigHash.ALL, false), ecKey);
        tx.getInput(vin).setScriptSig(script);
        // FIXME: SignHash is 41, not 01

        return Utils.HEX.encode(tx.bitcoinSerialize());
    }

}
