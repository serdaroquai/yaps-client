#Sample application.properties file (should be in the same folder with your jar)
#-------------------------------------

# which port this client should run on
server.port=8090

# where the logs are to be stored
logging.file=logs/server.log

#token received from yaps telegram bot during subscription
token=123456789 

#id of your rig
id=Rig1

# First 25% threshold first 30 seconds, after 30 seconds 5%
threshold.0=0.25
threshold.30=0.05

# only strategy so far
strategy.name=highestCurrentEstimate
miner.enable=true

# initial delay before starting miners
# useful to prevent drawing too much power during restart
minerStart.initialDelay=60000

# your GPU hashrate benchmarks per algorithm in kiloHash
hashrateMap.X17=17770
hashrateMap.PHI1612=31650
hashrateMap.Lyra2REv2=67460
hashrateMap.NeoScrypt=1780
hashrateMap.Skein=869470
hashrateMap.Xevan=4960
#hashrateMap.Tribus=93180
#hashrateMap.X11Gost=19650
#hashrateMap.NIST5=76590


# configuration of your miner softwares
minerMap.X17.algo=X17
minerMap.X17.path=D:\\minerwatch\\miner\\palginmod_1.1_x64_X17\\ccminer.exe -a x17 -o stratum+tcp://x17.mine.ahashpool.com:3737 -u 1Ai9ThRgaUL5SY1URjL8Kk6oA9ykN1aXxr -p c=BTC,ID=Flagship
minerMap.X17.kill=ccminer.exe

minerMap.PHI1612.algo=PHI1612
minerMap.PHI1612.path=D:\\minerwatch\\miner\\ccminer-phi-1.0_PHI1612\\ccminer.exe -a phi -o stratum+tcp://phi.mine.ahashpool.com:8333 -u 1Ai9ThRgaUL5SY1URjL8Kk6oA9ykN1aXxr -p c=BTC,ID=Flagship
minerMap.PHI1612.kill=ccminer.exe

minerMap.Lyra2REv2.algo=Lyra2REv2
minerMap.Lyra2REv2.path=D:\\minerwatch\\miner\\excavator_Lyra2REv2\\excavator.exe -c D:\\minerwatch\\miner\\excavator_Lyra2REv2\\awesome.json
minerMap.Lyra2REv2.kill=excavator.exe

minerMap.NeoScrypt.algo=NeoScrypt
minerMap.NeoScrypt.path=D:\\minerwatch\\miner\\excavator_NeoScrypt\\excavator.exe -c D:\\minerwatch\\miner\\excavator_NeoScrypt\\awesome.json
minerMap.NeoScrypt.kill=excavator.exe

#minerMap.Xevan.algo=Xevan
#minerMap.Xevan.path=D:\\minerwatch\\miner\\ccminer-Xevan\\ccminer.exe -a xevan -o stratum+tcp://xevan.mine.ahashpool.com:3739 -u 1Ai9ThRgaUL5SY1URjL8Kk6oA9ykN1aXxr -p c=BTC,ID=Flagship
#minerMap.Xevan.kill=ccminer.exe

#minerMap.X11Gost.algo=X11Gost
#minerMap.X11Gost.path=D:\\minerwatch\\miner\\palginmod_1.1_x64_X11Gost\\ccminer.exe -a sib -o stratum+tcp://sib.mine.ahashpool.com:5033 -u 1Ai9ThRgaUL5SY1URjL8Kk6oA9ykN1aXxr -p c=BTC,ID=Flagship
#minerMap.X11Gost.kill=ccminer.exe

#minerMap.Tribus.algo=Tribus
#minerMap.Tribus.path=D:\\minerwatch\\miner\\ccminer-x64-2.2.4_Tribus\\ccminer-x64.exe -a tribus -o stratum+tcp://tribus.mine.ahashpool.com:8533 -u 1Ai9ThRgaUL5SY1URjL8Kk6oA9ykN1aXxr -p c=BTC,ID=Flagship
#minerMap.Tribus.kill=ccminer-x64.exe

#minerMap.NIST5.algo=NIST5
#minerMap.NIST5.path=D:\\minerwatch\\miner\\palginmod_1.1_x64_NIST5\\ccminer.exe -a nist5 -o stratum+tcp://nist5.mine.ahashpool.com:3833 -u 1Ai9ThRgaUL5SY1URjL8Kk6oA9ykN1aXxr -p c=BTC,ID=Flagship
#minerMap.NIST5.kill=ccminer.exe

minerMap.Skein.algo=Skein
minerMap.Skein.path=D:\\minerwatch\\miner\\ccminer8.14-KlausT_Skein\\ccminer.exe -a skein -o stratum+tcp://skein.mine.ahashpool.com:4933 -u 1Ai9ThRgaUL5SY1URjL8Kk6oA9ykN1aXxr -p c=BTC,ID=Flagship
minerMap.Skein.kill=ccminer.exe


