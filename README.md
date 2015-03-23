#FFO Assistant

This tool is for reconnection between host and client without losing the current game process.

##require java 1.8 [download](http://www.oracle.com/technetwork/java/javase/downloads/index.html)

##Before game starts:
Action on host side:

###step 1
Turn on FFO Assistant with following setting

"local port" = FFO port (12700 by default)

"remote ip" =         (keep it blank)

"remote port" = xxxxx (any value)

Then press the host button and keep FFO Assistant on

###step 2
Turn on FFO and host as usual.

Action on client side:

###step 1
Turn on FFO Assistant with following settings:

"local port" = FFO port (12700 by default, need to be same as the local port of host)

"remote ip" = host ip 

"remote port" = xxxxx (any value, need to be same as the remote port of host)

Then press the client button and keep FFO Assistant on

###step 2
Turn on FFO with following settings: 

"TO HOST" = 127.0.0.1 (or keep it blank)

"PORT" = FFO port (12700 by default, need to be same as the local port of host)

Then press the "NETWORK MODE-CLIENT" button to start.



##Once disconnection occurs:
###step 1 
host: press "reconnect" button of FFO Assistant(FFO Assistant will keep freezing until client reconnect. if you want to terminate it, use windows task manager(taskmgr.exe))

###step 2
client: press "reconnect" button of FFO Assistant and the connection should be fixed.

###THE ORDER MATTERS!!! Client has to press the button after Host does, otherwise it would not work！


###If Japanese version of this readme is required, please let me know. Thank you!
