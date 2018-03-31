package org.serdaroquai.me.misc;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.apache.commons.lang3.StringUtils;
import org.serdaroquai.me.Config.StratumConnection;


public class Util {
	
	private static final CharSequence zeroes = "000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
	private static final String genesisBlockDiff = "00ffff0000000000000000000000000000000000000000000000000000";
	
	public static BigDecimal diffToInteger(String diffEncoded, Algorithm algo) {
		//TODO for now skip algos with multipliers
		    	
    	//note 2 chars in string is 1 byte
    	int numberOfBytes = Integer.valueOf(diffEncoded.substring(0,2), 16);
    	int prefixLength = diffEncoded.length() - 2;
    	int numberOfZeroes= (numberOfBytes * 2) - prefixLength;
    	
    	String diffDecoded = new StringBuilder(numberOfBytes*2).append(diffEncoded.substring(2)).append(zeroes,0,numberOfZeroes).toString();
    	
    	return new BigDecimal(new BigInteger(genesisBlockDiff, 16)).divide(new BigDecimal(new BigInteger(diffDecoded, 16)), 3, RoundingMode.HALF_UP);
	}
	
	public static String cascade(String algo, String tag) {
		return String.format("%s-%s", algo,tag);
	}
	
	public static String getAddressOf(StratumConnection conn) {
		return String.format("%s:%s", conn.getHost(), conn.getPort());
	}
	
	public static int getBlockHeight(String coinbase1) {
		/*
		 * coinbase1 = 020000004a24a05a010000000000000000000000000000000000000000000000000000000000000000ffffffff180304871d044c24a05a08
		 * after ffffff is 18 (script length)
		 * 03 block height byte length ( no need to parse this for the next 150 years for 2^23-1 blocks
		 * 04871d block height in little endian
		 * note that we will reverse 04871d00 (since java uses 4 bytes for Integer)
		 */
		String coinbase = coinbase1.split("ffffffff")[1]; // 180304871d044c24a05a08
		int byteCount = Integer.valueOf(coinbase.substring(2,4),16); //usually 03, for some low block numbers 02
		String heightLittleEndian = coinbase.substring(4, 4 + (byteCount*2)); // (00) 04 87 1d 
		return swapEndian(Integer.valueOf(heightLittleEndian,16), byteCount); // 00 1d 87 04
	}
	
	public static int swapEndian(int i, int byteCount) { 
		// converts (00) 04 87 1d first to 1d 87 04 00 then to 00 1d 87 04
		return ((i<<24) + ((i<<8)&0x00FF0000) + ((i>>8)&0x0000FF00) + (i>>>24)) >> (8 * (4 - byteCount));
	}
	
	public static boolean isEmpty(String string) {
		return StringUtils.isEmpty(string);
	}
}
