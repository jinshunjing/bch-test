package org.jim.bch.addr;

import lombok.Data;

/**
 * Bitcoin Cash CashAddr Address
 *
 * @author author
 */
@Data
public class CashAddr {

    private String prefix;

    private CashAddrType addressType;

    private byte[] hash;

}
