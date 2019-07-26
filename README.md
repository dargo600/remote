## Remote Control App for Android Devices ##
A remote control app for which uses the the Consumer IR Service
to transmit the proper code for various Devices.  On boot it creates a seperate
thread that first checks it's own database state and consults a
known remote REST server that populates the known database IR codes that have been
tested.  It then creates a group of objects with the configurations that are in use
 to avoid the need to have to consult the database other than at start up.

### Todo: ###
 - add support for other devices such as Roku
 - improve the display menu.  Provide a configuration Activity 
 - add espresso/junit tests
 - investigate doing this in swift for iphone

### Pronto Hex Format ###
According to http://www.hifi-remote.com/infrared/IR-PWM.shtml, one can extract
the frequency from pronto hex format as per #2.  The IR Transmit command will
then take the remaining pronto values after the second sequence code as per
the code. 
1. The first number is always a zero (0000) it indicates that the IR pattern
is raw data, which means it was learned.
2. The second number is the frequency of the IR carrier in terms of the
Pronto internal clock. The following formula where N represents the decimal
value of this hex number will give you the frequency of the carrier in

    Kilohertz:
    Frequency = 1000000/(N * .241246)

    A Sony remote will usually have a value for N of 103 (this shows as 67
    Hex).  Doing the arithmetic we have 

    Freq=1000000/(103*. 241246)= 40,244

    or approximately 40,000 cycles per second (well within a tolerance of
    40,000 +/- 10%)

3. The third number is the number of Burst Pairs in Burst Pair Sequence #1.
Each Burst pair consists of two 4 digit Hex numbers representing the On and
Off time of that burst (single binary Bit).
4. The fourth number is the number of Burst Pairs in Burst Pair Sequence #2.

