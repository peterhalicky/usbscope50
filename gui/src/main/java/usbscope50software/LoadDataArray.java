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

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import javax.swing.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.ui.Layer;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;
import org.jfree.chart.annotations.XYLineAnnotation;
import org.jfree.chart.annotations.XYTextAnnotation;

/**
 *
 * @author ana
 */
public class LoadDataArray extends Thread {//implements Runnable {

    NumberFormat FFTlabelFormatter = new DecimalFormat("#.###");
    double FFTLabelAdjust7_0 = 1;//adjusty FFT label for Setting index 7,6,5 and 4 to 0. as these are zoomed in function values
    public static boolean SoftwareStopped;
    public static float FreqStep;
    boolean runtimerbusy;
    boolean settimer = false;
    int pretrig_applicable;
    boolean runtimer_waiting;//might need to clear it in timebaseknob
    int runtimer_rollmodesamples = 0;//might need to clear it in timebaseknob
    boolean runtimer_triggered;
    int triggertest_counter;
    boolean runtimer_ignorethisdata;
    float HaltSamplePeriod;//at the moment only used in this class
    int nonactivity_counter;
    float[] runtimer_sampledata = new float[4097];
    float[] runtimer_sampledatamulti = new float[12288];//[3072 * 4];
    public static float[][] SampleData = new float[5][3072];
    double[][] SampleData_FFT = new double[5][3072];
    float runtimer_risdatasmoothed;
    boolean[] ChannelBWLimit = new boolean[6];
    static float[][] OffsetNull = new float[5][3];//5 for 4 channels; 0 not used and 3 for 3 diffenet volts settings
    double[] functionOffset_setInHW = new double[5];
    float CurrentPreTrigDepth;
//int tick;//used for demo mode
    int FreeRunTimeout = 20;//75;//6;//constant about 1.5 seconds
    private Timer RunTimer;
    private int maxGraphPointCount = 3000;           //number of data instances on the graph
    boolean SingleTrigger = false;
    //static boolean JavaRunning = false;// now defined in USBscope50_Main
    boolean Exiting = false;
    String status = "";
    int statusTracker = 0;//if this value  not changed and java is running, then something is wrong abort
    float runtimer_risbin;
    int[] runtimer_risbinlogint = new int[20];
    boolean just_entered_RIS = false;//when this is true clear SampleData array
    int iteration = 0;
    int RISModeGraphDivShift;

    public native void GetBufferBlocksMultiChan(int firstchan, int lastchan, int numblocks, float[] Data);

    private native void USBscope50Drvr_GetBufferIncremental(int channel, float[] Data, int numbytes, int numsofar);

    public native float USBscope50Drvr_GetBufferRIS(int channel, float[] Data, int numbytes);

    public native int GetAcquisitionState(int channel);

    public native void AcquisitionStart(int channel);

    public native void SetNormTrig(int channel, int normtrig);

    public native float GetSamplePeriod(int getcurrentperiod, float haltperiod);//called in usbscope50_main as well

    public native int GetTriggeredStatus(int channel);

    public native int USBscope50Drvr_GetSamplesSinceTrigger(int channel);

    public native int OpenPorts(int channel, int PortNo);

    public native boolean ScopeStackStatus(int channel);

    public native void USBscope50Drvr_SetDetectLine(int channel, int master, int State);

    public native void USBscope50Drvr_InitScope(int channel, int master);

    public native int GetRISmin(int channel);

    public native int GetRISmax(int channel);

    public native int GetTriggerDACOffset(int channel);

    public native float GetRawChannelOffset(int channel);

    public native float GetOffsetDACScale(int channel);

    public native void USBscope50Drvr_SetTrigMaster(int channel, int master);

    public native void ClosePorts();

    public native void USBscope50Drvr_SetTrigType(int channel, int trigtype);

    public native void USBscope50Drvr_SetBaseAdcClk(int channel, int clk);

    public native void SetDecimationRatio(int channel, int dr, int realDR);

    public native void USBscope50Drvr_AcquisitionEnd(int channel);

    //public native void TempInitSetting(int channel);//not used
    public native void USBscope50Drvr_SetRISMode(int channel, int State);

    public native void USBscope50Drvr_SetCalSource(int channel, int State); //0=off 1=on

    public native void USBscope50Drvr_SetUpFrontEnd(int channel, int gain, int dc, int gnd, int ris);

    public native void SetPreTrigDepth(int channel, float pct);

    public native void SetOffset(int channel, float offsetpct);

    public native void SetTrigThreshold(int channel, float threshpct, int gain, boolean comp);

    public native int USBscope50Drv_isAbort();

//--win specific
    public native int USBscopeDrvr_OpenDrvr();//loads silabs library

    public native int USBscope50Drvr_Enumerate(int RegKeyPath);

    public native int USBscope50Drvr_OpenAndReset(int channel);

    public native void USBscope50Drvr_SetLEDMode(int channel, int mode);//0=off;1=blinking;2=fast blinking;3=on

    public native String USBscope50Drvr_GetProductName(int channel);

    public native int USBscope50Drvr_GetControllerRev(int channel);

    public native String USBscope50Drvr_GetSerialNumber(int channel);

    public native int USBscope50Drvr_GetPortNumber(int channel);

    public native void USBscope50Drvr_FFT(double[] Data, long N, double dB, long window, int peakbin);

    public static void main(String[] args) {
    }

    public synchronized void GetStatus(int intStatus) {
        String statusValue;
        switch (intStatus) {
            case 1:
                statusValue = "running...";
                break;
            case 2:
                statusValue = "stopped";
                break;
            case 3:
                statusValue = "armed...";
                break;
            case 4:
                statusValue = "triggered...";
                break;
            default:
                statusValue = "acquiring";
                break;
        }
        status = statusValue;
    }

    public void run() {

        if (settimer == false) {
            SetUpRunTimer();
            settimer = true;
        }
        synchronized (this) {
            if (Exiting) {
                RunTimer.setRepeats(false);
                RunTimer.stop();

            } else if (USBscope50_Main.JavaRunning) {
                RunTimer.setRepeats(true);
                RunTimer.restart();
                GetStatus(1);//1running;2stopped;/3acquiring           
            } else {
                GetStatus(2);//1running;2stopped;/3acquiring
                RunTimer.setRepeats(false);
                RunTimer.stop();
            }
        }

    }

    private double GetPointsPerDiv(int ArrayPointer) {
        double points = 100;
        /*if (USBscope50_Main.demoMode){
        return 100;
        }*/
        if (ArrayPointer == 0) {
            points = 4;
        } else if (ArrayPointer == 3) {
            points = 40;
        } else if ((ArrayPointer == 1) || (ArrayPointer == 5)) {
            points = 10;
        } else if ((ArrayPointer == 2) || (ArrayPointer == 6)) {
            points = 20;
        } else if (ArrayPointer == 7) {
            points = 50;
        } else {
            points = 100;
        }

        return points;
    }

    private void SetUpRunTimer() {
        //int timerDuration;
        ActionListener RunTimerListener = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (USBscope50_Main.demoMode) {
                    PlotNow();

                    if (SingleTrigger) {
                        RunTimer.setRepeats(false);
                        RunTimer.stop();
                        USBscope50_Main.JavaRunning = false;
                        GetStatus(2);//1running;2stopped;/3acquiring
                    }
                    if (!USBscope50_Main.JavaRunning) {
                        RunTimer.setRepeats(false);
                        RunTimer.stop();
                        GetStatus(2);
                    }
                } else {
                    Update();
                }

                statusTracker++;
                if (statusTracker > 10000) {
                    statusTracker = 0;
                }

            }
        };

        /*if (USBscope50_Main.demoMode){
        timerDuration = 250;
        }else{
        timerDuration = 100;
        }*/
        RunTimer = new Timer(100, RunTimerListener);
        RunTimer.setRepeats(true);
    }

    public void DisplayData() {
        int i;
        double PointsPerDiv = 100.0;
        double[] adcAdjust = new double[USBscope50_Main.numScopesFound + 1];
        int[] mult = new int[5];
        float[] tempSoftwareOffset = new float[5];


        for (i = 1; i < 5; i++) {
            mult[i] = 1;
            if (USBscope50_Main.Probe[i] == 2) {
                mult[i] = 10;
            }
        }
        for (i = 1; i <= USBscope50_Main.numScopesFound; i++) {
            adcAdjust[i] = (0.30000 * Math.pow(10.0, USBscope50_Main.VoltageGain[i])) / 127.00000;
            adcAdjust[i] = adcAdjust[i] * mult[i] * USBscope50_Main.INV[i];
            //SoftwareOffset[i] = (float) ((USBscope50_Main.functionOffset[i]*5*USBscope50_Main.YDiv[i]/10000)* mult[i]);
            tempSoftwareOffset[i] = (float) (USBscope50_Main.SoftwareOffset[i] * mult[i]);
        }

        for (i = -1500; i < (1499); i++) {

            USBscope50_Main.seriesCh1.add(i, (SampleData[1][i + 1500] * adcAdjust[1] + tempSoftwareOffset[1]), false);

            if (USBscope50_Main.numScopesFound > 1) {
                USBscope50_Main.seriesCh2.add(i, (SampleData[2][i + 1500] * adcAdjust[2] + tempSoftwareOffset[2]), false);
            }
            if (USBscope50_Main.numScopesFound > 2) {
                USBscope50_Main.seriesCh3.add(i, (SampleData[3][i + 1500] * adcAdjust[3] + tempSoftwareOffset[3]), false);
            }
            if (USBscope50_Main.numScopesFound > 3) {
                USBscope50_Main.seriesCh4.add(i, (SampleData[4][i + 1500] * adcAdjust[4] + tempSoftwareOffset[4]), false);
            }
        }
        USBscope50_Main.seriesCh1.add(i, (SampleData[1][i + 1500] * adcAdjust[1] + tempSoftwareOffset[1]), true);
        if (USBscope50_Main.numScopesFound > 1) {
            USBscope50_Main.seriesCh2.add(i, (SampleData[2][i + 1500] * adcAdjust[2] + tempSoftwareOffset[2]), true);
        }
        if (USBscope50_Main.numScopesFound > 2) {
            USBscope50_Main.seriesCh3.add(i, (SampleData[3][i + 1500] * adcAdjust[3] + tempSoftwareOffset[3]), true);
        }
        if (USBscope50_Main.numScopesFound > 3) {
            USBscope50_Main.seriesCh4.add(i, (SampleData[4][i + 1500] * adcAdjust[4] + tempSoftwareOffset[4]), true);
        }

        USBscope50_Main.trigPosClone_loaded[1] = false;
        USBscope50_Main.trigPosClone_loaded[2] = false;
        USBscope50_Main.trigPosClone_loaded[3] = false;
        USBscope50_Main.trigPosClone_loaded[4] = false;

        PointsPerDiv = GetPointsPerDiv(USBscope50_Main.intTimeBaseArrayPointer);

        if (PointsPerDiv != USBscope50_Main.lastPointsPerDiv) {
            USBscope50_Main.chartPlot.clearDomainMarkers();

            FFTLabelAdjust7_0 = 100 / PointsPerDiv;
            RISModeGraphDivShift = 0;


            USBscope50_Main.lastPointsPerDiv = PointsPerDiv;
            USBscope50_Main.axisX.setTickUnit(new NumberTickUnit(PointsPerDiv), true, true);
            if (USBscope50_Main.intTimeBaseArrayPointer < 4) {
                USBscope50_Main.axisX.setRange((-maxGraphPointCount / 2) + 20, 20 + ((30 * PointsPerDiv) - (maxGraphPointCount / 2)));
                RISModeGraphDivShift = 20;
            //USBscope50_Main.chartPlot.
            //this 20 is just so that the first division appears as the full division. This is a feature as the 0 on the graph is in the centre
            //USBscope50_Main.axisX.setRange((-maxGraphPointCount / 2), ((30 * PointsPerDiv) - (maxGraphPointCount / 2)));
            } else {// intTimeBaseArrayPointer = 5,6,7
                USBscope50_Main.axisX.setRange(-maxGraphPointCount / 2, ((30 * PointsPerDiv) - (maxGraphPointCount / 2)));
            }
            //redraw vertical markers

            UpdateVerticalMarkers();
        }
    }

    public void DisplayDataDemo(boolean incrementIteration) {
        int i, j = 0;
        int[] shift = new int[4];// {200,60,30,10];// = 200,60,30,10;
        int tempOffsetvalue;

        int[] mult = new int[5];

        for (i = 1; i < 5; i++) {
            mult[i] = 1;
            if (USBscope50_Main.Probe[i] == 2) {
                mult[i] = 10;
            }
        }
        shift[0] = 150;
        shift[1] = 40;
        shift[2] = 100;
        shift[3] = 10;

        if (incrementIteration) {
            iteration++;
        }

        if (iteration > 32000) {
            iteration = 0;
        }

        tempOffsetvalue = ((iteration * shift[0]) % 3000);
        j = 0;
        for (i = -1500; i < 1499 - tempOffsetvalue; i++, j++) {
            //USBscope50_Main.seriesCh1.add(i, (SampleData[1][tempOffsetvalue + j] * mult[1] * USBscope50_Main.INV[1]), false);
            USBscope50_Main.seriesCh1.add(i, (SampleData[1][tempOffsetvalue + j] * mult[1]), false);
        }
        j = 0;
        for (i = 1499 - tempOffsetvalue; i < 1499; i++, j++) {
            USBscope50_Main.seriesCh1.add(i, (SampleData[1][j] * mult[1]), false);
        }
        USBscope50_Main.seriesCh1.add(i, (SampleData[1][j] * mult[1]), true);


        //channel 2 stuff
        if (USBscope50_Main.numScopesFound > 1) {
            tempOffsetvalue = ((iteration * shift[1]) % 3000);
            j = 0;
            for (i = -1500; i < 1499 - tempOffsetvalue; i++, j++) {
                USBscope50_Main.seriesCh2.add(i, (SampleData[2][tempOffsetvalue + j] * mult[2]), false);
            }

            j = 0;
            for (i = 1499 - tempOffsetvalue; i < 1499; i++, j++) {
                USBscope50_Main.seriesCh2.add(i, (SampleData[2][j] * mult[2]), false);
            }
            USBscope50_Main.seriesCh2.add(i, (SampleData[2][j] * mult[2]), true);
        }

//channel 3 stuff
        if (USBscope50_Main.numScopesFound > 2) {
            tempOffsetvalue = ((iteration * shift[2]) % 3000);
            j = 0;
            for (i = -1500; i < 1499 - tempOffsetvalue; i++, j++) {
                USBscope50_Main.seriesCh3.add(i, (SampleData[3][tempOffsetvalue + j] * mult[3]), false);
            }

            j = 0;
            for (i = 1499 - tempOffsetvalue; i < 1499; i++, j++) {
                USBscope50_Main.seriesCh3.add(i, (SampleData[3][j] * mult[3]), false);
            }
            USBscope50_Main.seriesCh3.add(i, (SampleData[3][j] * mult[3]), true);
        }

        //channel 4 stuff
        if (USBscope50_Main.numScopesFound > 3) {
            tempOffsetvalue = ((iteration * shift[3]) % 3000);
            j = 0;
            for (i = -1500; i < 1499 - tempOffsetvalue; i++, j++) {
                USBscope50_Main.seriesCh4.add(i, (SampleData[4][tempOffsetvalue + j] * mult[4]), false);
            }

            j = 0;
            for (i = 1499 - tempOffsetvalue; i < 1499; i++, j++) {
                USBscope50_Main.seriesCh4.add(i, (SampleData[4][j] * mult[4]), false);
            }
            USBscope50_Main.seriesCh4.add(i, (SampleData[4][j] * mult[4]), true);
        }
    }

    public void CalculateFreqStep() {
        FreqStep = (0.500f / GetSamplePeriod((USBscope50_Main.JavaRunning ? 1 : 0), (float) USBscope50_Main.SignalSamplePeriod / 1.000f)) / (2048.000f / 2.000f);
        FreqStep = (float) (FreqStep / FFTLabelAdjust7_0);

        if ((FreqStep) >= (1000000 / 100)) {
            USBscope50_Main.lbFDivAllChs.setText("FFT: " + FFTlabelFormatter.format(FreqStep * 100 / 1000000) + " MHz/div");//100 points per division
        } else if ((FreqStep) >= (1000 / 100)) {
            USBscope50_Main.lbFDivAllChs.setText("FFT: " + FFTlabelFormatter.format(FreqStep * 100 / 1000) + " KHz/div");
        } else if ((FreqStep) > (1 / 100)) {
            USBscope50_Main.lbFDivAllChs.setText("FFT: " + FFTlabelFormatter.format(FreqStep * 100) + " Hz/div");
        } else {
            USBscope50_Main.lbFDivAllChs.setText("FFT: " + FFTlabelFormatter.format(FreqStep * 100 * 1000) + " mHz/div");
        }
    }

    public void DisplayFFTData() {
        int i, j;
        long N = 2048;
        double dB;
        int peakbin = 1;
        double plotscale;
        double plotoffset;
        double VperDivHWSetting;


        if (USBscope50_Main.dblFFTPlotType == 0) { //0 is dB;
            dB = 0.5;
            plotscale = 1;
            plotoffset = 0;//-5.95;//it should be -6;//(30-6);
        } else {//1- Linear
            dB = 0;
            plotscale = 10 * 128 * 2 / 127;
            plotoffset = 0;
        }

        for (i = 1; i <= USBscope50_Main.numScopesFound; i++) {
            for (j = 0; j < 2048; j++) {
                SampleData_FFT[i][j] = SampleData[i][j] / 255;
            }
        }

        //System.out.println("VperDivHWSetting is " + VperDivHWSetting);
        for (j = 1; j <= USBscope50_Main.numScopesFound; j++) {
            USBscope50Drvr_FFT(SampleData_FFT[j], N, dB, USBscope50_Main.lngFFTWindowType, peakbin);

            if (USBscope50_Main.demoMode && USBscope50_Main.dblFFTPlotType == 1) {
                VperDivHWSetting = 10;
            } else if (USBscope50_Main.dblFFTPlotType == 0) {//0 is dB
                VperDivHWSetting = 1;
            } else if (USBscope50_Main.VoltageGain[j] == 0) {//0.03V/div
                VperDivHWSetting = 0.03;
            } else if (USBscope50_Main.VoltageGain[j] == 1) {//0.3V/div
                VperDivHWSetting = 0.3;
            } else {//3V/div
                VperDivHWSetting = 3;
            }


            for (i = 0; i < (2048 / 2); i++) {
                if (SampleData_FFT[j][i] < -58) {
                    SampleData_FFT[j][i] = -59.5;
                }

                SampleData_FFT[j][i] = (SampleData_FFT[j][i] * VperDivHWSetting * plotscale) + plotoffset;// * 10 *128*2/127);//*plotscale)+plotoffset;
            }
        }
//RISModeGraphDivShift=0;
        for (i = -1500; i < (-476); i++) {//as there is only 1024 points to display for FFT;-1500+1024~480
            //j=i;//;//=i+(2*(i+1500));
            USBscope50_Main.seriesCh1_FFT.add(i + RISModeGraphDivShift, (SampleData_FFT[1][i + 1500]), false);
            if (USBscope50_Main.numScopesFound > 1) {
                USBscope50_Main.seriesCh2_FFT.add(i + RISModeGraphDivShift, (SampleData_FFT[2][i + 1500]), false);

            }
            if (USBscope50_Main.numScopesFound > 2) {
                USBscope50_Main.seriesCh3_FFT.add(i + RISModeGraphDivShift, (SampleData_FFT[3][i + 1500]), false);
            }
            if (USBscope50_Main.numScopesFound > 3) {
                USBscope50_Main.seriesCh4_FFT.add(i + RISModeGraphDivShift, (SampleData_FFT[4][i + 1500]), false);
            }
        }
        USBscope50_Main.seriesCh1_FFT.add(i + RISModeGraphDivShift, (SampleData_FFT[1][i + 1500]), true);
        if (USBscope50_Main.numScopesFound > 1) {
            USBscope50_Main.seriesCh2_FFT.add(i + RISModeGraphDivShift, (SampleData_FFT[2][i + 1500]), true);
        }
        if (USBscope50_Main.numScopesFound > 2) {
            USBscope50_Main.seriesCh3_FFT.add(i + RISModeGraphDivShift, (SampleData_FFT[3][i + 1500]), true);
        }
        if (USBscope50_Main.numScopesFound > 3) {
            USBscope50_Main.seriesCh4_FFT.add(i + RISModeGraphDivShift, (SampleData_FFT[4][i + 1500]), true);
        }
    }

    private void PlotNow() {
        //clear all markers
        USBscope50_Main.seriesCh1.clear();
        USBscope50_Main.seriesCh2.clear();
        USBscope50_Main.seriesCh3.clear();
        USBscope50_Main.seriesCh4.clear();

        if (USBscope50_Main.demoMode) {
            DisplayDataDemo(USBscope50_Main.JavaRunning);
        } else {
            DisplayData();
        }

        if (USBscope50_Main.FFT_Ch1_4 && !USBscope50_Main.demoMode) {
            USBscope50_Main.seriesCh1_FFT.clear();
            USBscope50_Main.seriesCh2_FFT.clear();
            USBscope50_Main.seriesCh3_FFT.clear();
            USBscope50_Main.seriesCh4_FFT.clear();
            DisplayFFTData();
            CalculateFreqStep();
        }


    }

    private void Update() {

        int chanct, runtimer_sweeps = 1;
        float sample1, sample2;
        double dbOffset;

        //if (USBscope50_Main.demoMode && DemoStop){
        if (SoftwareStopped) {
            RunTimer.stop();
            RunTimer.setRepeats(false);
            runtimer_waiting = false;
            runtimer_triggered = true;
            GetStatus(2);
            return;
        }
        if (runtimerbusy) {
            return;
        }
        runtimerbusy = true;

        if ((USBscope50_Main.UseRISmode) != 0) {//RIS mode = true
            runtimer_sweeps = 5;
            if (!just_entered_RIS) {
                for (int i = 0; i < SampleData.length; i++) {
                    for (int j = 0; j < SampleData[0].length; j++) {
                        SampleData[i][j] = 0;
                    }
                }
                USBscope50Drvr_SetRISMode(1, 1);
                USBscope50Drvr_SetRISMode(2, 1);
                USBscope50Drvr_SetRISMode(3, 1);
                USBscope50Drvr_SetRISMode(4, 1);
                just_entered_RIS = true;
            //USBscope50_Main.CalculateFreqStep();

            }

        } else {
            if (just_entered_RIS) {
                runtimer_sweeps = 1;
                USBscope50Drvr_SetRISMode(1, 0);
                USBscope50Drvr_SetRISMode(2, 0);
                USBscope50Drvr_SetRISMode(3, 0);
                USBscope50Drvr_SetRISMode(4, 0);
                just_entered_RIS = false;
            }
        }


        if ((GetAcquisitionState(USBscope50_Main.MasterChannel) == 0) || USBscope50_Main.demoMode) {//not acquiring
            /*double VperDivHWSetting;
            for (int i = 1; i <= 4; i++) {
            if (functionOffset_setInHW[i] != USBscope50_Main.functionOffset[i]) {
            if (USBscope50_Main.VoltageGain[i] == 0) {//0.03V/div
            VperDivHWSetting = 0.03;
            } else if (USBscope50_Main.VoltageGain[i] == 1) {//0.3V/div
            VperDivHWSetting = 0.3;
            } else {//3V/div
            VperDivHWSetting = 3;
            }
            dbOffset = USBscope50_Main.functionOffset[i] / 100;
            dbOffset = dbOffset * USBscope50_Main.YDiv[i] * 5 / 100;
            dbOffset = dbOffset / ((VperDivHWSetting * 10) / 100);
            SetOffset(i, (float) (dbOffset));

            functionOffset_setInHW[i] = USBscope50_Main.functionOffset[i];
            }
            }*/

            if (CurrentPreTrigDepth != USBscope50_Main.PreTrigDepth) {
                for (int i = 1; i <= 4; i++) {
                    SetPreTrigDepth(i, USBscope50_Main.PreTrigDepth);
                }
                CurrentPreTrigDepth = USBscope50_Main.PreTrigDepth;
            }

            for (int sweep = 0; sweep < runtimer_sweeps; sweep++) {  //ris mode uses multiple sweeps
                if ((!runtimer_waiting) || USBscope50_Main.demoMode) {

                    GetStatus(3);//armed

                    if (USBscope50_Main.UseRISmode == 0) {
                        for (chanct = 1; chanct <= USBscope50_Main.numScopesFound; chanct++) {
                            SetNormTrig(chanct, USBscope50_Main.triggermode);
                        }
                    }
                    for (chanct = 1; chanct <= USBscope50_Main.numScopesFound; chanct++) {
                        if ((USBscope50_Main.ChannelOn[chanct]) && (chanct != USBscope50_Main.TriggerChannel)) {
                            AcquisitionStart(chanct);
                        }
                    }
                    AcquisitionStart(USBscope50_Main.TriggerChannel);
                    //System.out.println("AcquisitionStart(USBscope50_Main.TriggerChannel)");
                    runtimer_rollmodesamples = 0;
                    runtimer_waiting = true;
                    runtimer_triggered = false;

                    if (USBscope50_Main.UseRollmode) {
                        int timeout = 0;
                        while ((GetAcquisitionState(USBscope50_Main.MasterChannel) == 0) && (timeout < 1000)) {
                            timeout++;
                        }
                    }
                }//if ((!runtimer_waiting) || USBscope50_Main.demoMode){


                try {
                    Thread.sleep(1);
                } catch (InterruptedException ex) {
                    Logger.getLogger(USBscope50_Main.class.getName()).log(Level.SEVERE, null, ex);
                }

                //System.out.println("GetAcquisitionState(USBscope50_Main.MasterChannel");
                if (((GetAcquisitionState(USBscope50_Main.MasterChannel) == 0) && (!runtimer_ignorethisdata)) || USBscope50_Main.demoMode) {
                    GetStatus(4);//triggered
                    runtimer_waiting = false;
                    nonactivity_counter = 0;

                    runtimer_triggered = true;
                    for (chanct = 1; chanct <= USBscope50_Main.numScopesFound; chanct++) {
                        if (USBscope50_Main.ChannelOn[chanct]) {//Channel on has nothing to do with function on Channel present would be much better to use
                            USBscope50Drvr_AcquisitionEnd(chanct);
                        }
                    }
                    //System.out.println("USBscope50Drvr_AcquisitionEnd");

                    if ((USBscope50_Main.UseRISmode) == 1) {

                        for (chanct = 1; chanct <= USBscope50_Main.numScopesFound; chanct++) {
                            if (USBscope50_Main.ChannelOn[chanct]) {
                                runtimer_risbin = USBscope50Drvr_GetBufferRIS(chanct, runtimer_sampledata, 150);
                            //System.out.println("bin: " + runtimer_risbin);
                            }

                            //inc=0;
                            for (int ct = 0; ct < 150; ct++) {
                                runtimer_sampledata[ct] = ((runtimer_sampledata[ct] + runtimer_sampledata[ct + 1]) / 2) - OffsetNull[chanct][USBscope50_Main.VoltageGain[chanct]];
                            //runtimer_sampledata[ct] = runtimer_sampledata[ct];// - OffsetNull[chanct][USBscope50_Main.VoltageGain[chanct]];
                            //runtimer_sampledata[ct] = inc*0.1f;
                            //inc++;
                            }


                            int runtimer_risbinquant = (int) runtimer_risbin;

                            // 'vb fix for signals over 50MHz
                            // 'log which "bins" have been hit
                            if (runtimer_risbinquant >= 0 && runtimer_risbinquant < 20) {
                                runtimer_risbinlogint[runtimer_risbinquant] = runtimer_risbinlogint[runtimer_risbinquant] + 1;// 'keep a count of the bins
                            }

                            float runtimer_risbinfrac = Math.abs(runtimer_risbin - runtimer_risbinquant); //'doesn't really need abs but just in case

                            //'runtimer_reject = (runtimer_risbinfrac > 0.6) Or (runtimer_risbinfrac < 0.4)

                            for (int ct = 0; ct < 150; ct++) {                                //'because the bins get quantized, look at the frac part to see how much to weight the previous
                                //'bin sample in the avg with the new bin sample.  Near the middle (frac=0.5), use the new sample raw.
                                //'near the ends of the bin, favour the old data 100%
                                runtimer_risdatasmoothed = (float) (Math.abs(2 * (runtimer_risbinfrac - 0.5)) * (SampleData[chanct][(ct * 20) + runtimer_risbinquant]) + (1 - Math.abs(2 * (runtimer_risbinfrac - 0.5))) * runtimer_sampledata[ct]);
                                SampleData[chanct][(ct * 20) + runtimer_risbinquant] = runtimer_risdatasmoothed;//offset already removed above - OffsetNull[chanct][USBscope50_Main.VoltageGain[chanct]];

                            }
                        }

                    } else {
                        //this call recovers all scope channel data into one large linear buffer 
                        //System.out.println("GetBufferBlocksMultiChan B4");
                        //System.out.println(USBscope50Drv_isAbort());
                        GetBufferBlocksMultiChan(1, USBscope50_Main.numScopesFound, 6, runtimer_sampledatamulti);
                        /*try {
                        Thread.sleep(50);
                        } catch (InterruptedException ex) {
                        Logger.getLogger(LoadDataArray.class.getName()).log(Level.SEVERE, null, ex);
                        }*/
                        //System.out.println("GetBufferBlocksMultiChan A8");
                        //System.out.println(USBscope50Drv_isAbort());

                        for (chanct = 1; chanct <= USBscope50_Main.numScopesFound; chanct++) {
                            if (USBscope50_Main.ChannelOn[chanct] && !USBscope50_Main.demoMode) {
                                for (int ct = 0; ct < USBscope50_Main.SampleDepth; ct++) {
                                    sample1 = runtimer_sampledatamulti[((chanct - 1) * 6 * 512) + ct];
                                    sample2 = runtimer_sampledatamulti[(((chanct - 1) * 6 * 512) + ct + 1)];

                                    SampleData[chanct][ct] = ((sample1 + sample2) / 2) - OffsetNull[chanct][USBscope50_Main.VoltageGain[chanct]];

                                //  if (ChannelBWLimit[chanct] && (ct < USBscope50_Main.SampleDepth)) {
                                //      SampleData[chanct][ct] = (sample1 + sample2) / 2;
                                //  }
                                //now apply any offset null to correct the raw adc code
                                //SampleData[chanct][ct] = SampleData[chanct][ct];// - OffsetNull[chanct][USBscope50_Main.VoltageGain[chanct]];//* relace this zero with a value

                                }
                            }
                        }

                    }
                //*domaths
                //DoMaths();
                } else {
                }
            }//for sweeps


            if ((!runtimer_waiting) && (!runtimer_ignorethisdata)) {
                // System.out.println("if not runtimer waiting");
                GetStatus(1);//running
                if (USBscope50_Main.JavaRunning) {
                    PlotNow();
                }

                if (SingleTrigger) {
                    if (USBscope50_Main.nulling) {
                        USBscope50_Main.nulling = false;
                    }
                    RunTimer.setRepeats(false);
                    RunTimer.stop();

                    USBscope50_Main.JavaRunning = false;
                    GetStatus(2);//1running;2stopped;/3acquiring
                } else {
                    RunTimer.setRepeats(true);
                }
            }
        }//if ((GetAcquisitionState(USBscope50_Main.MasterChannel) == 0) || USBscope50_Main.demoMode){


        if (runtimer_waiting) {

            if ((GetTriggeredStatus(USBscope50_Main.MasterChannel) != 0) || runtimer_triggered) {
                runtimer_triggered = true; //acquiring...

            } //else {
            //nonactivity_counter++;
            //if (nonactivity_counter >= FreeRunTimeout) {
            //    nonactivity_counter = FreeRunTimeout;
            //    selftriggered = false;//don't know what this is for???
            //    USBscope50_Main.triggermode = 0;
            //    for (chanct = 1; chanct <= USBscope50_Main.numScopesFound; chanct++) {
            //        USBscope50Drvr_AcquisitionEnd(chanct);
            //    }
            // }
            //}
            if (USBscope50_Main.UseRollmode) {
                int delta;
                delta = USBscope50Drvr_GetSamplesSinceTrigger(1);//channel 1              

                delta = delta - runtimer_rollmodesamples;

                if (delta > 10) {//10
                    for (chanct = 1; chanct <= USBscope50_Main.numScopesFound; chanct++) {

                        // if (ChannelPresent[chanct]){// && ChannelOn(chanct)){//channelon is static, channelpresentis not
                        if (USBscope50_Main.ChannelOn[chanct]) {
                            USBscope50Drvr_GetBufferIncremental(chanct, runtimer_sampledata, delta, runtimer_rollmodesamples);  //'driver now add's 1 to the runningtotal and loads it to the rdp
                            int ct = 0;
                            for (ct = runtimer_rollmodesamples; ct < (runtimer_rollmodesamples + delta - 1); ct++) {


                                SampleData[chanct][ct] = ((runtimer_sampledata[ct] + runtimer_sampledata[ct + 1]) / 2) - OffsetNull[chanct][USBscope50_Main.VoltageGain[chanct]];

                            //*  if (ChannelBWLimit(chanct) && (ct <= maxGraphPointCount - 2)) {
                            //*      SampleData[chanct][ct] = (runtimer_sampledata[ct] + runtimer_sampledata[ct + 1]) / 2;
                            //*  }
                            // 'now apply any offset null to correct the raw ADC codes
                            //SampleData[chanct][ct] = SampleData[chanct][ct];// - OffsetNull[chanct][0];
                            //   System.out.println("Sample data index " + ct + " written to");
                            }
                            SampleData[chanct][ct] = ((runtimer_sampledata[ct] + runtimer_sampledata[ct - 1]) / 2) - OffsetNull[chanct][USBscope50_Main.VoltageGain[chanct]];
                        }
                    }
                    PlotNow();
                    runtimer_rollmodesamples = runtimer_rollmodesamples + delta;
                    //        System.out.println("plot runtimer_rollmodesamples " + runtimer_rollmodesamples);
                    if (!USBscope50_Main.JavaRunning) {
                        RunTimer.setRepeats(false);
                        RunTimer.stop();
                        USBscope50Drvr_AcquisitionEnd(1);
                        USBscope50Drvr_AcquisitionEnd(2);
                        USBscope50Drvr_AcquisitionEnd(3);
                        USBscope50Drvr_AcquisitionEnd(4);
                    //System.out.println("USBscope50Drvr_AcquisitionEnd(4)");
                    }

                }
            }

        }//if(runtimer_waiting){
        runtimerbusy = false;

        if (RunTimer.isRepeats()) {
            RunTimer.restart();
        }

        if (USBscope50_Main.demoMode && !SingleTrigger && !USBscope50_Main.JavaRunning) {
            RunTimer.stop();
        }
    }

    public static void loadJarLibrary(String name) throws IOException {
        try (InputStream in = ClassLoader.getSystemResourceAsStream(name)) {
            if (in==null)
                throw new IOException("Can't find "+name+" resource");
            File temp = File.createTempFile(name, "");

            try (FileOutputStream fos = new FileOutputStream(temp)) {
                byte[] buffer = new byte[10240];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    fos.write(buffer, 0, read);
                }
            }
            System.load(temp.getAbsolutePath());
        }
    }

    static {
        try {
            if (USBFamily_Main.OS.equalsIgnoreCase("Linux")) {
                loadJarLibrary(USBscope50_Main.productID + "Drvr.so");
            } else if (USBFamily_Main.OS.equalsIgnoreCase("Windows")) {
                loadJarLibrary(USBscope50_Main.productID + "Drvr_W" + System.getProperty("sun.arch.data.model") + ".dll");
            }
        } catch (UnsatisfiedLinkError | IOException err) {
            USBscope50_Main.abort = 10;
            err.getMessage();
        }
    }

    private void UpdateVerticalMarkers() {
        double chartX;
        int loop;
        int indexTimeUnits;
        String TimeMarker;
        double atTime;
        double adjust = 1;
        double PointsPerDiv = GetPointsPerDiv(USBscope50_Main.intTimeBaseArrayPointer);
        String TimeUnit;
        double dt = 0;
        int dtUnit;

        for (loop = 0; loop < USBscope50_Main.NoDomainMarkersOnChart; loop++) {
            Point2D p = USBscope50_Main.chartPanel.translateScreenToJava2D(new Point((int) USBscope50_Main.VerticalMarkerMouseClickLocation[loop][0], USBscope50_Main.VerticalMarkerMouseClickLocation[loop][1]));
            ChartRenderingInfo info = USBscope50_Main.chartPanel.getChartRenderingInfo();
            Rectangle2D dataArea = info.getPlotInfo().getDataArea();
            ValueAxis domainAxis = USBscope50_Main.chartPlot.getDomainAxis();
            RectangleEdge domainAxisEdge = USBscope50_Main.chartPlot.getDomainAxisEdge();
            chartX = domainAxis.java2DToValue(p.getX(), dataArea, domainAxisEdge);
            Marker domainMarker = new ValueMarker(chartX);
            USBscope50_Main.VerticalMarkerLocation[loop] = chartX;
            domainMarker.setPaint(Color.black);
            adjust = 1;
            if (USBscope50_Main.intTimeBaseArrayPointer < 7) {
                indexTimeUnits = 0;
                adjust = 0.001;
            } else if (USBscope50_Main.intTimeBaseArrayPointer < 16) {
                indexTimeUnits = 1;
            } else if (USBscope50_Main.intTimeBaseArrayPointer < 25) {
                indexTimeUnits = 2;
                adjust = 1000;
            } else {
                indexTimeUnits = 3;
                adjust = 1000000;
            }

            if (USBscope50_Main.intTimeBaseArrayPointer < 4) {//this is as i have moved point by 20 as 0 is in the middle of the x axis and to get div lines i had to move by 20
                //adjust = adjust * VerticalMarkerAdjust;
                chartX = chartX - 20;
            }
            adjust = adjust / (100 / PointsPerDiv);
            atTime = (chartX + 1500) * 10 / adjust * (Double.parseDouble(USBscope50_Main.TimeBaseSettings[USBscope50_Main.intTimeBaseArrayPointer][3]));

            if (loop > 0) {
                dt = atTime - dt;
            } else {
                dt = atTime;
            }
            //USBscope50_Main.verticalMarkersAtTime[loop+1] = atTime;
            //USBscope50_Main.verticalMarkersTimeUnitIndex[loop+1]= indexTimeUnits;

            if ((atTime > 1000) && (indexTimeUnits < USBscope50_Main.TimeUnits.length - 1)) {
                indexTimeUnits++;
                atTime = atTime / 1000;
            }



            TimeMarker = USBscope50_Main.df.format(atTime);//(chartX + 1500)*10*(Double.parseDouble(TimeBaseSettings[intTimeBaseArrayPointer][3])));
            TimeUnit = USBscope50_Main.TimeUnits[indexTimeUnits];
            domainMarker.setLabel("@" + TimeMarker + TimeUnit);

            if ((chartX + 1500) > ((USBscope50_Main.axisX.getUpperBound() + 1500) * 3 / 4)) {
                domainMarker.setLabelAnchor(RectangleAnchor.TOP);
                domainMarker.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
                if (loop >= 1 && (Math.abs(USBscope50_Main.VerticalMarkerLocation[1] - USBscope50_Main.VerticalMarkerLocation[0]) < 100)) {
                    domainMarker.setLabelAnchor(RectangleAnchor.BOTTOM);
                    domainMarker.setLabelTextAnchor(TextAnchor.BOTTOM_RIGHT);
                }
            } else {
                domainMarker.setLabelAnchor(RectangleAnchor.TOP);
                domainMarker.setLabelTextAnchor(TextAnchor.TOP_LEFT);
                if (loop >= 1 && (Math.abs(USBscope50_Main.VerticalMarkerLocation[1] - USBscope50_Main.VerticalMarkerLocation[0]) < 100)) {

                    //domainMarker.setLabelAnchor(RectangleAnchor.BOTTOM);
                    domainMarker.setLabelTextAnchor(TextAnchor.BOTTOM_LEFT);
                    domainMarker.setLabelOffset(USBscope50_Main.rectOffset);
                }
            }
            USBscope50_Main.chartPlot.addDomainMarker(loop, domainMarker, Layer.FOREGROUND);

            if (loop == 1) {
                //dt = (Math.abs(USBscope50_Main.verticalMarkersAtTime[2] - USBscope50_Main.verticalMarkersAtTime[1]));
                dtUnit = USBscope50_Main.verticalMarkersTimeUnitIndex[loop];
                if (dt > 1000 && dtUnit < 3) {
                    dt = dt / 1000;
                    dtUnit++;
                }

                int mult = 1;
                if (USBscope50_Main.Probe[1] == 2) {
                    mult = 10;
                }

                if (USBscope50_Main.DeltaShowing) {
                    USBscope50_Main.chartPlot.removeAnnotation(USBscope50_Main.Delta);
                    USBscope50_Main.chartPlot.removeAnnotation(USBscope50_Main.DeltaText);
                }
                double smallerX = (USBscope50_Main.VerticalMarkerLocation[0] < USBscope50_Main.VerticalMarkerLocation[1] ? USBscope50_Main.VerticalMarkerLocation[0] : USBscope50_Main.VerticalMarkerLocation[1]);
                Font font = new Font("SansSerif", Font.PLAIN, 9);
                USBscope50_Main.Delta = new XYLineAnnotation(USBscope50_Main.VerticalMarkerLocation[0], USBscope50_Main.YDiv[1] * mult * 4.0, USBscope50_Main.VerticalMarkerLocation[1], USBscope50_Main.YDiv[1] * mult * 4.0);
                USBscope50_Main.DeltaText = new XYTextAnnotation("dt=" + USBscope50_Main.df.format(dt) + USBscope50_Main.TimeUnits[dtUnit], Math.abs(USBscope50_Main.VerticalMarkerLocation[0] - USBscope50_Main.VerticalMarkerLocation[1]) / 2 + smallerX, (USBscope50_Main.YDiv[1]*mult * 4.0) + (USBscope50_Main.YDiv[1]*mult   / 3));
                USBscope50_Main.DeltaText.setFont(font);
                USBscope50_Main.chartPlot.addAnnotation(USBscope50_Main.Delta);
                USBscope50_Main.chartPlot.addAnnotation(USBscope50_Main.DeltaText);
                USBscope50_Main.DeltaShowing = true;

            }
        }

    }
   
}



