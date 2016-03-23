package nz.co.mikesimpson.robobrain.UDP;

import java.net.InetAddress;

import nz.co.mikesimpson.robobrain.UDP.UDPCallBackActivity;

/**
 * Created by Mike Simpson on 21/03/2016.
 */
public abstract class UDPMachine {

    public abstract void sendData(String data);
    public abstract void close();

}
