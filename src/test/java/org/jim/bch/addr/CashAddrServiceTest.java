package org.jim.bch.addr;

import org.bitcoinj.core.Utils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class CashAddrServiceTest {

    @Test
    public void testEncode() {
        CashNetwork network = CashNetwork.MAIN;
        CashAddrType type = CashAddrType.P2PKH;

        String pubKey = "02d8f9f55590fe34a0f8cc58d6c8834986427fec9999e8d0aaa96d06bd1d5ff865";
        byte[] pubKeyHash = Utils.sha256hash160(Utils.HEX.decode(pubKey));

        String addr = CashAddrService.encodeCashAddress(network, pubKeyHash, type);
        System.out.println(addr);
        //bitcoincash:qzwqtl8lhf08d9h0jh2dyvv7ayggvhf9qcs2gpfl6n
    }

}
