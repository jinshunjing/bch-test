package org.jim.bch.addr;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class HdKeyServiceTest {

    @Test
    public void testReceiveAddress() {
        // jeans code dash demand sausage number nephew wrong chimney provide moment proof
        String xPubKey = "tpubDDijsRbYbPUAkGAQiG2i2xJnsTHZ7QzAm45rf6psLqN7dXctdgR21jiL6NXBHaabTinqFumYxdGDWxbBxwTY8ESU7qFYQuPWj7o72yajzVq";
        int network = 1;

        String addr = HdKeyService.deriveBchAddress(network, xPubKey, 0, 0);
        System.out.println(addr);
        // qrzc5x84nq7n58cgyt6waeg83wjwz3pj6c3u6nwcst
        // pubkey: 0353e693b02a6ca3da330235da70351bc9629ef0c08c943a22b0b3a48e439f11f2
        // prvKey: cR6vutpTAwjMe5gAnHzeqrkXpp829PukVS6WKK1hp3xDf4YwuDAr
        // pubKeyHash: c58a18f5983d3a1f0822f4eee5078ba4e14432d6
    }
}
