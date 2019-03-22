/*
 * This file is part of USBscope50 Software.
 *
 *   USBscope50 Software is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   any later version.
 *
 *   USBscope50 Software is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   A copy of the GNU General Public License should be included
 *   along with USBscope50 Software.  If not, see <http://www.gnu.org/licenses/>.
 *
 * 
 */
package usbscope50software;

import java.io.BufferedReader;
import java.io.File;

import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class USBFamily_Main {

    SplashScreen screen;
    String[][] gUSBTandMDevices;
    public static String OS;
    public static boolean Vista;

    public USBFamily_Main() {
        boolean Done = false;
        String Msg = "";
        

        splashScreenInit(); // initialize the splash screen

        long timeNow = GetTimeNow();
        while (!(Done = Delay(timeNow, 5))) {   //return in 5 seconds from now

        }

        for (int i = 0; i <= 100; i++) {
            for (long j = 0; j < 50000; ++j) {
                String poop = " " + (j + i);
            }
            screen.setProgress(i);           // progress bar with no message

        }

        screen.removeAll();
        screen.dispose();
        splashScreenDestruct();

        OS = CheckOperatingSystem();
        if (OS.equalsIgnoreCase("Windows")) {
        } else {//Linux

            File c_library = new File("/usr/lib/" + USBscope50_Main.productID + "Drvr.so");
            //File c_library = new File("/home/ana/Desktop/" + USBscope50_Main.productID + "Drvr.so");
            if (c_library.exists() && c_library.canRead()) {
            } else {
                c_library = null;
                Msg = "\n" + USBscope50_Main.productID + " Software will terminate now.\n\n" +
                        "Unable to open " + USBscope50_Main.productID + "Drvr.so\n\n" +
                        "Please insure that this file is in /usr/lib/ folder\n" +
                        "and that you have read access to this location.\n\n" +
                        USBscope50_Main.companyID;
                JOptionPane.showMessageDialog(screen, Msg, "Fatal Error", JOptionPane.ERROR_MESSAGE);
                System.exit(0);


            }
        }


        if (OS.equalsIgnoreCase("Linux")) {
            
            String Device_found = USBDeviceScan();  //reads virtual file product;determines what form to load

            if (Device_found.equalsIgnoreCase("USBwave12")) {
            } else if (Device_found.equalsIgnoreCase("USBpulse100")) {
            } else if (Device_found.equalsIgnoreCase("USBcount50")) {
            } else if (Device_found.equalsIgnoreCase("USBscope50")) {//i think this one might have to stay usbscope50 and not akip
                //   new USBscope50_Main().setVisible(true);
            } else {
                //demo mode//MUST do this bit 2!!!
                Msg = "\n" + USBscope50_Main.companyID + " Software will run in Demo Mode\n" +
                        "as no USB Test and Measurement devices were detected.\n\n" +
                        "If such a device is plugged in, but not recognized by this software,\n" +
                        "it is most likely that the " + USBscope50_Main.companyID + " ids\n" +
                        "are missing from the SiLabs driver in the kernel.\n\n" +
                        "For more information either see " + USBscope50_Main.companyID + " software Help Pages,\n" +
                        "or refer to www.elandigitalsystems.com.\n\n" +
                        "Thank you for using " + USBscope50_Main.companyID + " Software.\n\n" +
                        USBscope50_Main.companyID;
                JOptionPane.showMessageDialog(screen, Msg, USBscope50_Main.companyID + " - Demo Mode", JOptionPane.INFORMATION_MESSAGE);
            }

        }
    
        new USBscope50_Main().setVisible(true);
        
    }

    private String CheckOperatingSystem() {
        
        try {
            File ProfFileFolder = new File(System.getenv("ProgramFiles"));
            if (ProfFileFolder.exists() && ProfFileFolder.isDirectory()) {
                //confiintln(System.getProperty("os.name"));
                /*if ((System.getProperty("os.name").contains("Vista")) || (System.getProperty("os.name").contains("Windows 7"))){
                    Vista = true;
                }else{
                    Vista = false;
                }*/
                Vista = true; // hardcode for Windows 7; WinXP is quite out-of-fashion
                return "Windows";
            }
        } catch (Exception e) {
        }
        return "Linux";        
    }

    private boolean Delay(long timeNow, int i) {
        long timeNowRunning = GetTimeNow();

        while (timeNowRunning < (timeNow + (i * 1000))) {
            timeNowRunning = GetTimeNow();
        }
        return true;
    }

    private int EnumUSB_SerialDevices() {
        /* Kernel creates virtual files in /sys/bus/usb-serial/devices when drivers loaded.
         * Scan this location to see how many USB test & measurements devices attached.
         * Scan /sys/bus/usb/drivers/cp2101 to see how many are using Silabs driver
         * Load global array gUSBTandMDevices. gUSBTandMDevices[0][1-3];[1][ttyUSB0];
         * return number of silab devices.
         */

        String sysDevicesLocation = "/sys/bus/usb-serial/devices";
        File sysDevicesFolder = new File(sysDevicesLocation);
        String sysDriversLocation = "/sys/bus/usb/drivers/cp2101";
        File sysDriverFolder = new File(sysDriversLocation);
        String[] USB_SerialArray = null;        //list of all files from sysDevicesLocation e.g. ttyUSB0, ttyUSB1, etc.

        String[] USB_Serialcp2101Array = null;  //list of all files in usb/drivers/cp2101 folder e.g. wanted 1-3:1.0, 
        //as well as unwanted module, bind, unbind

        String[] USB_Serialcp2101Arraychecked = null;        //filtered USB_Serialcp2101Array;only wanted files e.g. 1-3

        int noUSB_SerialDevices = 0;            //number of ttyUSB folders in sysDevicesLocation, i.e. no of usb-serial devices

        int noCP2101Devices = 0;                //number of virtual device folders in sysDriversLocation (format N-N:N.N)

        int index;//temp looping variable

        //step1: find number of USB_Serial devices attached /sys/bus/usb-serial/devices; folder names ttyUSB
        if (sysDevicesFolder.exists() && sysDevicesFolder.isDirectory()) {
            USB_SerialArray = sysDevicesFolder.list();

            for (index = 0; index < USB_SerialArray.length; index++) {
                String USB_SerialDeviceFolder = sysDevicesLocation + "/" + USB_SerialArray[index];
                if (USB_SerialArray[index].toLowerCase().contains("ttyusb") && (new File(USB_SerialDeviceFolder).isDirectory())) {
                    noUSB_SerialDevices++;
                }
            }
        }

        if (noUSB_SerialDevices == 0) {
            return 0;
        }


        //step2:find number of cp2101 devices attached in /sys/bus/usb/drivers/cp2101; folder names: 1-3:1.0;2-1:1.0,etc       
        if (sysDriverFolder.exists() && sysDriverFolder.isDirectory()) {
            USB_Serialcp2101Array = sysDriverFolder.list();                 //get all the files

            USB_Serialcp2101Arraychecked = new String[USB_Serialcp2101Array.length];
            for (index = 0; index < USB_Serialcp2101Array.length; index++) {
                String USB_SerialCP2101Folder = sysDriversLocation + "/" + USB_Serialcp2101Array[index];
                if (USB_Serialcp2101Array[index].contains(":") && (new File(USB_SerialCP2101Folder).isDirectory())) {//check if this is the one we want

                    USB_Serialcp2101Arraychecked[noCP2101Devices] = USB_Serialcp2101Array[index];
                    noCP2101Devices++;
                }
            }
        }

        String[][] USBTandMDevices = new String[2][noCP2101Devices];    //lets organize all the info we have collected up to now

        for (index = 0; index < noCP2101Devices; index++) {
            USBTandMDevices[0][index] = USB_Serialcp2101Arraychecked[index].substring(0, USB_Serialcp2101Arraychecked[index].indexOf(":"));
            USBTandMDevices[1][index] = getTTYIndex(sysDriversLocation, USB_Serialcp2101Arraychecked[index]);
        //USBTandMDevices[0] will store info like 1-3
        //USBTandMDevices[1] will store info like port number like ttyUSB0
        }

        gUSBTandMDevices = new String[2][noCP2101Devices];              //lets have info availabe globally

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < noCP2101Devices; j++) {
                gUSBTandMDevices[i][j] = USBTandMDevices[i][j];
            }
        }
        return noCP2101Devices;                     //number of usb-serial devices with silab chip

    }

    private String GetAttachedDeviceProductString(int noUSB_SerialDevices) {
        /* returns USB device product string e.g. USBscope50 or USBwave12, 
         * string read from device properties product file         
         * on error returns "" empty string
         * 
         * Although gUSBTandMDevices array has been loaded with all USB devices using cp2101 driver,
         * we only look at the first array item and display the software for this device
         */

        String product = "";

        String USBDevicePropertiesLocation = "/sys/bus/usb/devices/" + gUSBTandMDevices[0][0];//returns e.g. 1-3

        File USBDevicePropertyFolder = new File(USBDevicePropertiesLocation);

        if (USBDevicePropertyFolder.exists() && USBDevicePropertyFolder.isDirectory()) {
            File productFile = new File(USBDevicePropertiesLocation + "/product");
            if (productFile.exists() && productFile.isFile() && productFile.canRead()) {
                BufferedReader productReader = null;
                try {
                    productReader = new BufferedReader(new FileReader(productFile));
                    product = productReader.readLine(); //expected to read USBscope50 or USBcount50, etc.

                    productReader.close();
                } catch (IOException ex) {
                    Logger.getLogger(USBFamily_Main.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        productReader.close();
                    } catch (IOException ex) {
                        Logger.getLogger(USBFamily_Main.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }

        return product;
    }

    private long GetTimeNow() {
        Date now = new Date();
        return now.getTime();
    }

    private String USBDeviceScan() {
        /* Returns a string of the fist Elan USB test & measurement device it detects
         * Return "Demo Mode" if zero devices detected        
         */

        int noUSB_SerialDevices = EnumUSB_SerialDevices();  //number of devices using Silabs driver 

        if (noUSB_SerialDevices > 0) {
            String ProductID = GetAttachedDeviceProductString(noUSB_SerialDevices);
            return ProductID;//e.g. USBscope50

        }

        return "Demo Mode";
    }

    private String getTTYIndex(String sysDriversLocation, String DriverCode) {
        String FolderPath = sysDriversLocation + "/" + DriverCode;
        File cpDriverDeviceFolder = new File(FolderPath);

        if (cpDriverDeviceFolder.exists() && cpDriverDeviceFolder.isDirectory()) {
            String[] cpDriverDeviceSubfolders = cpDriverDeviceFolder.list();
            for (int i = 0; i < cpDriverDeviceSubfolders.length; i++) {
                if (cpDriverDeviceSubfolders[i].toLowerCase().startsWith("ttyusb") && (new File(FolderPath).isDirectory())) {
                    return cpDriverDeviceSubfolders[i];
                }
            }
        }
        return "NotElanUSBDevice";
    }

    private void splashScreenDestruct() {
        screen.setScreenVisible(false);
        screen = null;
    }

    private void splashScreenInit() {
        
        ImageIcon myImage;
        if (USBscope50_Main.companyID.equals("PRIST")){
            myImage = new ImageIcon(getClass().getResource("/usbscope50software/Images/AKIP-logo.jpg"));
        }else{
            myImage = new ImageIcon(getClass().getResource("/usbscope50software/Images/elan-logo_cheetah.jpg"));
        }
        screen = new SplashScreen(myImage);
        screen.setSize(340, 250);
        screen.setLocationRelativeTo(null); //place in the centre of the screen

        screen.setProgressMax(100);         //progress bar max value

        screen.setScreenVisible(true);       
    }

    public static void main(String[] args) {
        try {
            //UIManager.setLookAndFeel(new SyntheticaBlackMoonLookAndFeel());// sets Look & Feel globally

        } catch (Exception e) {
            e.printStackTrace();
        }

        new USBFamily_Main();
    }
   
}
