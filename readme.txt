Installation

1. send a /subscribe message to @YapsNotificationBot using TelegramWeb(easier to copy paste). It will return a token and an userId, note them down later to place in your properties file.
2. Create a base folder (D:\minerwatch) and copy yaps-client-0.1.0.jar
3. Copy the miner softwares you will use
4. Prepare an application.properties file in the same folder with your jar. You can find a sample file below
5. Don't forget to fill "token" and "userId" fields using step 1.
6. Make a windows shortcut to start yaps-client (C:\ProgramData\Oracle\Java\javapath\java.exe -jar "D:\minerwatch\yaps-client-0.1.0.jar"). Also don't forget to set the working directory to your root folder where yaps-client-0.1.0.jar sits. ("D:\minerwatch")
7. Copy this shortcut you have created to your windows start folder. (C:\Users\<username>\AppData\Roaming\Microsoft\Windows\Start Menu\Programs\Startup)


#-------------------------------------
#Sample application.properties file (should be in the same folder with your jar)
#-------------------------------------

# which port this client should run on
server.port=8090

# where the logs are to be stored
logging.file=logs/server.log

# if you want to use a custom yaps-server instance you can un-comment below and change the url
# otherwise leave it as it is
#remote.url=ws://yaps.serdarbaykan.com:8090/pokerNight

#token received from yaps telegram bot during subscription
token=123456789XYZ 

#user id received from yaps telegram bot during subscription
userId=1234

#id of your rig, name it as you please
id=Rig1

# First 25% threshold first 30 seconds, after 30 seconds 5%
threshold.0=0.25
threshold.30=0.05

# only strategy so far
strategy.name=highestCurrentEstimate
miner.enable=true

# initial delay before start mining after yaps starts
# useful to prevent drawing too much power during a restart
minerStart.initialDelay=60000

# your GPU hashrate benchmarks per algorithm in kiloHash
hashrateMap.X17=17770
hashrateMap.PHI1612=31650
hashrateMap.Lyra2REv2=67460
hashrateMap.NeoScrypt=1780
hashrateMap.Skein=869470
hashrateMap.Xevan=4960
hashrateMap.Tribus=93180
hashrateMap.X11Gost=19650
hashrateMap.NIST5=76590


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
