package org.serdaroquai.me.misc;

import java.util.Arrays;
import java.util.Optional;

public enum Algorithm {

	X16R("x16r",""),
	X17("x17",""),
	PHI1612("phi",""),
	Lyra2REv2("lyra2v2","lyra2rev2"),
	NeoScrypt("neoscrypt","neoscrypt"),
	NIST5("nist5","nist5"),
	Tribus("tribus",""),
	Xevan("xevan",""),
	X11Gost("sib","x11gost"),
	Skein("skein",""),
	Equihash("","equihash"),
	MyriadGroestl("myr-gr",""),
	Skunk("skunk",""),
	X11("x11",""),
	Scrypt("scrypt",""),
	Qubit("qubit",""),
	BlakeCoin("blakecoin",""),
	Hmq1725("hmq1725",""),
	Yescrypt("yescrypt",""),
	Bitcore("bitcore",""),
	C11("c11",""),
	Groestl("groestl",""),
	Hsr("hsr",""),
	//Lbry("lbry","",7), //need to figure out how to parse a lbry blockheight so for now lets forget it
	Timetravel("timetravel",""),
	SHA3("keccak",""),
	Blake2s("blake2s",""),
	M7m("m7m","");
	
	String ahashpoolKey;
	String nicehashKey;
	
	private Algorithm(String ahashpoolKey, String nicehashKey) {
		this.ahashpoolKey = ahashpoolKey;
		this.nicehashKey = nicehashKey;
		
	}
	
	private String getNicehashKey() {
		return nicehashKey;
	}

	public String getAhashpoolKey() {
		return ahashpoolKey;
	}
	
	public static Optional<Algorithm> getByAhashpoolKey(String key) {
		return Arrays.stream(values())
				.filter(a -> !Util.isEmpty(a.getAhashpoolKey()))
				.filter(a -> a.getAhashpoolKey().equals(key))
				.findFirst();
	}
	
	public static Optional<Algorithm> getByNicehashKey(String key) {
		return Arrays.stream(values())
				.filter(a -> !Util.isEmpty(a.getNicehashKey()))
				.filter(a -> a.getNicehashKey().equals(key))
				.findFirst();
	}
	
}
