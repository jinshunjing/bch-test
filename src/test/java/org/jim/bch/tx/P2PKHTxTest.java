package org.jim.bch.tx;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class P2PKHTxTest {

    @Test
    public void testCreateTx() {
        int network = 1;

        String txid = "cbaa4b3c0292f60def4cf325d84b10181a0ef7227c87a76b8089f5a70c5a91ac";
        long vin = 0;

        String toAddr = "qpka2p34tle06m02wc3qxfvw0df5zyk7u5ghxwzk85";
        long amount = 9999707;

        String changeAddr = "qrlgec983jftqql62wuarz8u6ke6q9cwmsvnnsarkq";
        long change = 100000000;

        String rawTx = P2PKHTx.createRawTx(network, txid, vin, toAddr, amount, changeAddr, change);
        System.out.println(rawTx);
        // 0100000001ac915a0ca7f589806ba7877c22f70e1a18104bd825f34cef0df692023c4baacb0000000000ffffffff025b959800000000001976a9146dd506355ff2fd6dea762203258e7b534112dee588ac00e1f505000000001976a914fe8ce0a78c92b003fa53b9d188fcd5b3a0170edc88ac00000000
    }

    @Test
    public void testSignTxInput() {
        int network = 1;

        String outpoint = "ac915a0ca7f589806ba7877c22f70e1a18104bd825f34cef0df692023c4baacb00000000";
        String amount = "80778e0600000000";

        String redeemScript = "1976a914c58a18f5983d3a1f0822f4eee5078ba4e14432d688ac";

        String outputs = "5b959800000000001976a9146dd506355ff2fd6dea762203258e7b534112dee588ac" +
                         "00e1f505000000001976a914fe8ce0a78c92b003fa53b9d188fcd5b3a0170edc88ac";

        String prvKey = "cR6vutpTAwjMe5gAnHzeqrkXpp829PukVS6WKK1hp3xDf4YwuDAr";

        String hash = P2PKHTx.hashForSignature(outpoint, amount, redeemScript, outputs);

        String sig = P2PKHTx.signTxInput(network, hash, prvKey);

        System.out.println(sig);
        // 3045022100a531adaa66ceebadabd9d60fcaa9800260c5141e8e40a2e1e19ec432ff8f31e102202fe0a68013b786e65a13f40738c078ee8dc4a0b746749d49fc717d10528c7f38
    }

    @Test
    public void testSignRawTx() {
        // txid:a1c7f615ac069060da11cb1c56055f7fbabaafb24687657f3dad872ada60e5b1
        int network = 1;
        String rawTx = "0100000001ac915a0ca7f589806ba7877c22f70e1a18104bd825f34cef0df692023c4baacb0000000000ffffffff025b959800000000001976a9146dd506355ff2fd6dea762203258e7b534112dee588ac00e1f505000000001976a914fe8ce0a78c92b003fa53b9d188fcd5b3a0170edc88ac00000000";
        String signature = "3045022100a531adaa66ceebadabd9d60fcaa9800260c5141e8e40a2e1e19ec432ff8f31e102202fe0a68013b786e65a13f40738c078ee8dc4a0b746749d49fc717d10528c7f38";
        String pubKey = "0353e693b02a6ca3da330235da70351bc9629ef0c08c943a22b0b3a48e439f11f2";

        rawTx = P2PKHTx.signRawTx(network, rawTx, 0, signature, pubKey);
        System.out.println(rawTx);
        // 0100000001ac915a0ca7f589806ba7877c22f70e1a18104bd825f34cef0df692023c4baacb000000006b483045022100a531adaa66ceebadabd9d60fcaa9800260c5141e8e40a2e1e19ec432ff8f31e102202fe0a68013b786e65a13f40738c078ee8dc4a0b746749d49fc717d10528c7f3801210353e693b02a6ca3da330235da70351bc9629ef0c08c943a22b0b3a48e439f11f2ffffffff025b959800000000001976a9146dd506355ff2fd6dea762203258e7b534112dee588ac00e1f505000000001976a914fe8ce0a78c92b003fa53b9d188fcd5b3a0170edc88ac00000000
    }

}
