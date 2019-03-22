
//
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

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.awt.Font;

import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.swing.event.ChangeEvent;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.DefaultFontMapper;
import com.lowagie.text.pdf.FontMapper;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;

//import java.awt.Desktop;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;

import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.Timer;
import javax.swing.ImageIcon;
import javax.swing.SpinnerNumberModel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.axis.AxisLocation;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;
import org.jfree.chart.entity.XYItemEntity;//CategoryItemEntity;

import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.ChangeListener;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.Range;
import org.jfree.ui.Layer;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.annotations.XYLineAnnotation;
import org.jfree.chart.annotations.XYImageAnnotation;
import org.jfree.ui.RectangleInsets;

/**
 *
 * @author  ana.orec-archer
 * USBscope50 previous versions done in VB
 * limitation being that it could only be used on the Microsoft OS
 * Software re-written using Java and all platforms targeted
 *
 * Estimated release date January 2008
 *
 * Copyright © 2008 Elan Digital Systems Ltd.
 *
 */
public class USBscope50_Main extends javax.swing.JFrame implements ChartMouseListener {

    //private TranslatingXYDataset dataset;
    //leave one product ID commented: either Elan or PRIST
    //version is teh same for both
    static String productID = "USBscope50";
    static String companyID = "Elan Digital Systems";
    static String version = "1.17";

    /*static String productID = "AKIP-4101";
    static String companyID = "PRIST";
     */

    //NumberFormat FFTlabelFormatter = new DecimalFormat("#.###");
    static boolean JavaRunning;
    LoadDataArray t = new LoadDataArray();
    NumberFormat FFTlabelFormatter = new DecimalFormat("#.###");
    public static final long SampleDepth = 3000; //the number of points to take notice of
    //SWRAMSIZE in c library MUST have the same value
    public static final int MaxScope = 5; //Allowed up to 4 USBscopes in the  stack, not more (use 5 for array size [1]->[4],[0] not used)
    static int MasterChannel = 0; //master system clock genereted by this channel; 0 not used, use 1-4 - master channels not yet set;
    //different to TriggerMaster
    public static int numIntsFound; //number of cp2101 devices attached
    int cpynumIntsFound;
    public static int numScopesFound = 0; //out of cp2101 devices found, how many are USBscope50s
    String[][] gUSBTandMDevices; //Array of all USB devices detected com number 1-3,ttyUSB
    String[][] gUSBscope50Devices; //Array of all USBscope50 devices detected com number 1-3,ttyUSB
    public static boolean demoMode;
    public static double dblFFTPlotType = 0;
    public static long lngFFTWindowType = 0;
    boolean ImportedData = false;
    boolean[] ChannelPresent = new boolean[MaxScope]; //+MathChannel- not sure if i will need this
    static boolean[] ChannelOn;
    static int SlitPaneEffect = 269 - 70;// used to detect when tabbs X is being clicked on in;70 is tab extension width with title and X
    //String Msg; //Text display in the message window
    String[] IDMsg;
    String NullMsg;
    String[] ScopeProductNames;
    String[] ScopeSerialNumbers;
    int[] ScopePortNumbers;
    int[] ScopeHWRevs;
    int arraySize;
    //int[] ScopeControllerRevs;
    int[] rismax;
    int[] rismin;
    int[] triggerDACoffset;
    float[] rawchanneloffset;
    float[] offsetDACscale;
    public static XYSeries seriesCh1; //XYSeries data type used on this XYPlot graph used
    public static XYSeries seriesCh2; //XYSeries data type used on this XYPlot graph used
    public static XYSeries seriesCh3; //XYSeries data type used on this XYPlot graph used
    public static XYSeries seriesCh4; //XYSeries data type used on this XYPlot graph used
    public static XYSeries seriesCh1_FFT; //XYSeries data type used on this XYPlot graph used
    public static XYSeries seriesCh2_FFT; //XYSeries data type used on this XYPlot graph used
    public static XYSeries seriesCh3_FFT; //XYSeries data type used on this XYPlot graph used
    public static XYSeries seriesCh4_FFT; //XYSeries data type used on this XYPlot graph used
    public static double[] YDiv = new double[MaxScope]; //volts/div value used to set range for up to 4 channels
    boolean demoOffsetDoNoUpdate = false;//public XYSeries series_cloneCh1;
    public XYSeries trigPosSeries_cloneCh1;
    public XYSeries trigPosSeries_cloneCh2;
    public XYSeries trigPosSeries_cloneCh3;
    public XYSeries trigPosSeries_cloneCh4;
    public static boolean[] trigPosClone_loaded = new boolean[MaxScope];
    public static boolean OffsetClone_loaded = false;
    public XYSeries Offset_clone;
    //static float ChannelOffset = 0;
    double LastChannelOffset;
    float[] ClonedData = new float[3000];
    public static ChartPanel chartPanel; //Graph container (custom- comes with JFreeChart)
    public static XYPlot chartPlot; //XYPlot graph used
    public static NumberAxis axisX = new NumberAxis(); //neccessary to manipulate Graph axis ch1
    public static NumberAxis[] axisY = new NumberAxis[MaxScope];
    public static NumberAxis[] axisY_FFT = new NumberAxis[MaxScope];
    public static boolean FFT_Ch1_4 = false;
    CombinedDomainXYPlot plot;// = new CombinedDomainXYPlot(axisX);
    XYPlot FFTplot;
    public static double[] functionOffset = new double[MaxScope];
    static int TriggerChannel = 1;
    static int triggermode;
    int currentTrigType;
    int intPreTrigDepth;
    static float PreTrigDepth;//percentage value set in hardware for pretrigger depth
    int ScopeReadAtSlider = 1; //same as the initial triggerpositionslider value, which is set in properties
    int LastTrigerPos; //remember previous slider value
    static double SignalSamplePeriod = 0.0;
    static boolean UseRollmode;
    //boolean runtimer_ignorethisdata;
    static int Run = 1;
    static int abort;    //abort 1,2,3 or 4 = abort on that channel; if 10 USBscope50Drvr.dll missing
    //float[][] SampleData = new float[6][3072];
    //boolean[] ChannelBWLimit = new boolean[6];
    //float[][] OffsetNull = new float[4][3];
    //int runtimer_ratect;
    //int tick;
    boolean once; //timers created once
    boolean StupidNetbeans; //splitpane divider locations is giving me headache!!!
    boolean FirstStopped = false;
    boolean SetInialGUISettings = false;    //int FreeRunTimeout = 6;//constant about 1.5 seconds
    public static DecimalFormat df = new DecimalFormat("0.000"); //zero shows as digit, # would not display 0;
    String DATE_FORMAT_NOW = "dd-MM-yyyy HH:mm:ss"; //formats double to 2 decimal places, e.g. display value on the marker of the point where mouse clicked
    JFreeChart JFchart;
    boolean BusyUpdating = false;
    HelpSet hs;
    HelpBroker hb;
    int IsChannelMaster;
    boolean runtimer = false;
    int intTriggerModeFree = 0; // used in settrigmode
    int intTriggerModeNormal = 1;
    int intRisingEdge = 3; //4 trig type settings; used in settrigtype
    int intFallingEdge = 2;
    int intGreaterThan = 1;
    int intLessThan = 0;
    static String[][] TimeBaseSettings = new String[28][6]; //time per division options stored int his array
    //1st column contains text displayed on the screen, 2nd value to pass to SetBaseAdcClk;3rd decimationRatio
    //4th value used for time base adjustments when arrows clicked to follow time/div via zoom function
    String[][] VoltsDivSettings = new String[12][3]; //12 diff. settings; column 1 text displayed on the screen, column 2 value in mV, column 3 when probe x10 this value displayed on the screen
    static int[] Probe = new int[MaxScope];
    int CurrentAdcClockSetting = 0;
    int CurrentDecimationRatio;
    static int intTimeBaseArrayPointer = 10; //point to TimeBaseSettings[10];default settings
    static int[] intVoltsperDivPointer = new int[MaxScope];
    int CurrentVoltsDivSetting;
    boolean[] ChannelCalSourceOn = new boolean[MaxScope];
    double TimeBaseRatio = 0;
    double OverallZoomRatio = 1;
    double CurrentTimeBase = 0;
    boolean RefreshGraph = false;
    static int[] VoltageGain = new int[MaxScope];//VoltageGain[ch]= 0 for 0.03V/div, 1 for 0.3V/div & 2 for 3V/div
    int[] Dc_option = new int[MaxScope]; //true as set in tempinit!!!change so it is not set in tempinit, but ac as default
    int[] gnd_option = new int[MaxScope];
    static int[] INV = new int[MaxScope];
    static int UseRISmode;//it was boolean in vb code, but setupfront end wants int
    private float Threshold;
    private float initialThreshold;
    int initialPretrigger = 0;
    private Timer StatusTimer;
    private Timer NullTimer;
    int intNullTimerCommandID = 0;
    int intNullChannelID = 0;
    int Nulltriggermode;
    boolean NullJavaRunning;
    int NullcurrentTrigType;
    //int[] CurrentHardwareVSetting = new int[MaxScope];
    int oldStatus;
    public static boolean TutorialDemoDisplayed = false;
    public static boolean TutorialSaveGraphDisplayed;
    String[] PathToDemoFile = new String[5];
    int hDrvr;//silab driver handlerer
    public static Color ColorCh1 = new Color(255, 200, 0);    //Ch1 default orangeColor
    public static Color ColorCh2 = Color.GREEN;               //Ch2 default green
    public static Color ColorCh3 = new Color(100, 205, 255);//Color.BLUE;                //Ch3 default blue
    public static Color ColorCh4 = Color.RED;                 //Ch4 default red
    Color newColorCh1 = new Color(255, 200, 0);    //Ch1 default orange
    Color newColorCh2 = Color.GREEN;               //Ch2 default green
    Color newColorCh3 = new Color(100, 205, 255);//Color.BLUE;
    Color newColorCh4 = Color.RED;
    public Color DefaultColorCh1 = new Color(255, 200, 0);    //Ch1 default orange
    public Color DefaultColorCh2 = Color.GREEN;               //Ch2 default green
    public Color DefaultColorCh3 = new Color(100, 205, 255);//Color.BLUE;                //Ch3 default blue
    public Color DefaultColorCh4 = Color.RED;                 //Ch4 default red
    public Color ChartBackgroundColor;
    public static final Color ChartBackgroundDefaultColor = new Color(230, 230, 230);
    public Color newChartBackgroundColor;
    public Color DomainGridColor;
    public static final Color DefaultDomainGridColor = Color.lightGray;
    public Color newDomainGridColor;
    public Color RangeGridColor;
    public static final Color DefaultRangeGridColor = Color.lightGray;
    public Color newRangeGridColor;
    public float DomainStroke;
    public static final float DefaultDomainStroke = (float) 0.0;
    public float newDomainStroke;
    public float RangeStroke;
    public static final float DefaultRangeStroke = (float) 0.0;
    public float newRangeStroke;
    public float FunctionStroke;
    public static final float DefaultFunctionStroke = (float) 0.5;
    public float newFunctionStroke;
    /*private XYItemRenderer renderer = new XYLineAndShapeRenderer();//DO NOT USE XYLineAndShapeRenderer
    private XYItemRenderer renderer2 = new XYLineAndShapeRenderer();//IT PUTS WEIRD SQUARES ON EVERY DATA POINT AND
    private XYItemRenderer renderer3 = new XYLineAndShapeRenderer();//IT IS SO NOTICEABLY SLOWER. UNACCEPTABLY SLOWER
    private XYItemRenderer renderer4 = new XYLineAndShapeRenderer();    //Config file settings read and stored in these variables
     */
    private XYItemRenderer renderer = new StandardXYItemRenderer();
    private XYItemRenderer renderer2 = new StandardXYItemRenderer();
    private XYItemRenderer renderer3 = new StandardXYItemRenderer();
    private XYItemRenderer renderer4 = new StandardXYItemRenderer();
    private XYItemRenderer renderer_FFT = new StandardXYItemRenderer();
    private XYItemRenderer renderer2_FFT = new StandardXYItemRenderer();
    private XYItemRenderer renderer3_FFT = new StandardXYItemRenderer();
    private XYItemRenderer renderer4_FFT = new StandardXYItemRenderer();
    int FormSizeX;
    int FormSizeX_default = 1200;
    int FormSizeY;
    int FormSizeY_default = 600;
    int FormLocationX;
    int FormLocationX_default = -1;//ensures it is placed in the centre of the screen
    int FormLocationY;
    int FormLocationY_default = -1;//ensures it is placed in the centre of the screen
    int DividerLocation;
    int DividerLocation_default = 700;
    String WinExtState = "";
    static double lastPointsPerDiv;
    static boolean ChartZoomed;
    private XYDataset datasetCh1;
    private XYDataset datasetCh2;
    private XYDataset datasetCh3;
    private XYDataset datasetCh4;
    private XYDataset datasetCh1_FFT;
    private XYDataset datasetCh2_FFT;
    private XYDataset datasetCh3_FFT;
    private XYDataset datasetCh4_FFT;
    int MouseOverCh;
    int offsetCh;
    boolean ignoreMouseMove = false;
    boolean[] FFT_Channel = new boolean[MaxScope];//5
    static boolean ThresholdMarkerOn;
    static double ThresholdMarkerValue;
    int maxNumRangeMarkers = 2; //not counting threshold marker
    int maxNumNDomainMarkers = 2;
    static int NoRangeMarkersOnChart = 0;
    static int NoDomainMarkersOnChart = 0;
    int tempNoDomainMarkersOnChart = 0;
    int tempNoRangeMarkersOnChart = 0;
    String TimeUnit;
    String VoltageUnit;
    static String[] TimeUnits = new String[4];
    static String[] VoltageUnits = new String[2];
    static double[] VerticalMarkerLocation = new double[2];
    static double[] HorizontalMarkerLocation = new double[2];//HorizontalMarkerLocation[2] is always the threshold level
    boolean DomainMarker1Selected = false;
    boolean DomainMarker2Selected = false;
    String[] HorizontalMarkerLabelArray = new String[2];
    static int[][] VerticalMarkerMouseClickLocation = new int[2][2];//stores X,Y for markers in position 0 and 1
    static int[][] HorizontalMarkerMouseClickLocation = new int[2][2];
    static double VerticalMarkerAdjust = 1.00;
    static int OffsetNull[][] = new int[MaxScope][2];//used to correct offset, stored as adc code (not volts)
    static boolean nulling = false;
    XYTextAnnotation ZeroPointCh1 = null;
    XYTextAnnotation ZeroPointCh2 = null;
    XYTextAnnotation ZeroPointCh3 = null;
    XYTextAnnotation ZeroPointCh4 = null;
    static XYLineAnnotation Delta = null;
    static XYTextAnnotation DeltaText = null;
    XYImageAnnotation testImageAnnotation = null;
    double ZeroPointCh1Value = 0;
    double ZeroPointCh2Value = 0;
    double ZeroPointCh3Value = 0;
    double ZeroPointCh4Value = 0;
    static double[] SoftwareOffset = new double[MaxScope];
    boolean SoftwareLoaded = false;//i keep calling all these rout9ines before the window is loaded, so here is a flag to say all components on the screen
    int countStatusTimerLoops = 0; //will set SoftwareLoaded true after 10 status timer updates
    static RectangleInsets rectOffset = new RectangleInsets(40, 0, 0, 0);
    static RectangleInsets rectOffset_horiz = new RectangleInsets(0, 0, 0, 240);
    static RectangleInsets rectOffset_horiz_down_left = new RectangleInsets(0, 0, 20, 240);
    static RectangleInsets rectOffset_horiz_down = new RectangleInsets(0, 0, 20, 0);
    int AllowedDistanceBetweenMarkers = 450;
    static double[] verticalMarkersAtTime = new double[3];
    static int[] verticalMarkersTimeUnitIndex = new int[3];
    static boolean DeltaShowing = false;

    public USBscope50_Main() {


        if (companyID.equals("PRIST")) {
            this.setIconImage(AKIPLogo.getImage());
        } else {
            this.setIconImage(ElanLogo.getImage());
        }

        BorderLayout borderLayout1 = new BorderLayout(); //layout to place graph in the centre on the JSplitPane


        initComponents(); //NetBeans generated GUI components code
        ClearOffsetCh1.setVisible(false);//21.07.2009. Bart decided he didn't want these
        ClearOffsetCh2.setVisible(false);
        ClearOffsetCh3.setVisible(false);
        ClearOffsetCh4.setVisible(false);

        NullMsg = "\nNull feature will eliminate small channel offset that are naturally present in various Volts/div ranges.\n" +
                "\nSoftware will scan through the Volts/div ranges and save the null offset adjustments.\n" +
                "This process could take up to 10 seconds. The message will pop up when the process has completed.\n" +
                "You only ever need to do this once per scope.\n\n" +
                "Ground the probe tip now and press OK to continue.\n" +
                "Or press cancel to abort Null Function.";



        TimeUnits[0] = "ns";
        TimeUnits[1] = "us";
        TimeUnits[2] = "ms";
        TimeUnits[3] = "s";

        VoltageUnits[0] = "mV";
        VoltageUnits[1] = "V";
        intVoltsperDivPointer[1] = 4;//default is 50mV per division if nothing in config file
        intVoltsperDivPointer[2] = 4;
        intVoltsperDivPointer[3] = 4;
        intVoltsperDivPointer[4] = 4;

        INV[1] = 1;//values accepted 1 and -1; if 0 then in display data all points will be 0
        INV[2] = 1;
        INV[3] = 1;
        INV[4] = 1;

        Probe[1] = 1;
        Probe[2] = 1;
        Probe[3] = 1;
        Probe[4] = 1;

        ReadConfigFile();


        lbVDivCh1.setBackground(ColorCh1);
        lbVDivCh2.setBackground(ColorCh2);
        lbVDivCh3.setBackground(ColorCh3);
        lbVDivCh4.setBackground(ColorCh4);

        if (WinExtState.length() > 0) {
            if (WinExtState.equalsIgnoreCase("MIN")) {
                this.setExtendedState(USBscope50_Main.ICONIFIED);
            } else if (WinExtState.equalsIgnoreCase("MAX")) {
                this.setExtendedState(USBscope50_Main.MAXIMIZED_BOTH);
            }
        } else {
            this.setSize(FormSizeX, FormSizeY);
        }


        if ((FormLocationX == -1) || (FormLocationY == -1)) {//N.B. set location after windows size
            setLocationRelativeTo(null); //put form on the centre of the screen
        } else {
            this.setLocation(FormLocationX, FormLocationY);
        }


        // JFchart = createChart();
        JFchart = createCombinedChart();

        //JFchart.addSubtitle(new TextTitle("    Ch1 "));// + VoltsDivSettings[SettingIndex][0]));
        seriesCh1.setMaximumItemCount(maxGraphPointCount); //max number of data values on the graph
        seriesCh2.setMaximumItemCount(maxGraphPointCount); //max number of data values on the graph
        seriesCh3.setMaximumItemCount(maxGraphPointCount); //max number of data values on the graph
        seriesCh4.setMaximumItemCount(maxGraphPointCount); //max number of data values on the graph

        ONButtonCh1.setSelected(true);
        SetButtonState(1, ONButtonCh1, ONButtonCh1.isSelected());
        ONButtonCh2.setSelected(true);
        SetButtonState(2, ONButtonCh2, ONButtonCh2.isSelected());
        ONButtonCh3.setSelected(true);
        SetButtonState(3, ONButtonCh3, ONButtonCh3.isSelected());
        ONButtonCh4.setSelected(true);
        SetButtonState(4, ONButtonCh4, ONButtonCh4.isSelected());

        chartPanel = new ChartPanelModified(JFchart, true); //place JFreeChart on the ChartPanel

        chartPanel.setFocusable(true);
        chartPanel.addChartMouseListener(this);



        chartPanel.addKeyListener(new java.awt.event.KeyAdapter() {

            @Override
            public void keyTyped(KeyEvent arg0) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                int largeStep = 2000;//large step
                int smallStep = 100;
                if (offsetCh == 0) {
                    return;//no channel selected to offset
                }

                double OffsetSliderValue = functionOffset[offsetCh];//SliderOffset.getValue();

                if (e.getKeyCode() == 38) {
                    if (e.isControlDown()) {
                        OffsetSliderValue = OffsetSliderValue + largeStep;
                    } else {
                        OffsetSliderValue = OffsetSliderValue + smallStep;
                    }

                    if (OffsetSliderValue > 10000) {
                        OffsetSliderValue = 10000;
                    }
                } else if (e.getKeyCode() == 40) {
                    if (e.isControlDown()) {
                        OffsetSliderValue = OffsetSliderValue - largeStep;
                    } else {
                        OffsetSliderValue = OffsetSliderValue - smallStep;
                    }
                    if (OffsetSliderValue < -10000) {
                        OffsetSliderValue = -10000;
                    }
                }
                switch (offsetCh) {
                    case 1:
                        OffsetSliderCh1.setValue((int) OffsetSliderValue);
                        break;
                    case 2:
                        OffsetSliderCh2.setValue((int) OffsetSliderValue);
                        break;
                    case 3:
                        OffsetSliderCh3.setValue((int) OffsetSliderValue);
                        break;
                    case 4:
                        OffsetSliderCh4.setValue((int) OffsetSliderValue);
                        break;
                    default:
                        break;
                }

            //SliderOffset.setValue((int) OffsetSliderValue);
            }

            @Override
            public void keyReleased(KeyEvent arg0) {
            }
        });

        chartPanel.addFocusListener(new java.awt.event.FocusAdapter() {

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                //MnuSelectNone.setSelected(true);
                //RadioButOffsetNone.setSelected(true);//will not do this as moving slider will select and offset selected channel
                //OffsetSelectNone();
            }
        });
        chartPanel.setIgnoreRepaint(true);

        JFchart.setBorderPaint(Color.BLACK);

        //chartPlot = JFchart.getXYPlot(); //need this lot when redrawing graph
        //not JFchart but subplot... to do with nitroducing combined graph for FFT. since 1.0.1

        axisX.setUpperMargin(0); //otherwise there is a bit of graph  displayed after 3000th point
        axisX.setLowerMargin(0);
        chartPlot.setRangeGridlinesVisible(true);
        chartPlot.setRangeGridlinePaint(RangeGridColor);
        chartPlot.setDomainGridlinePaint(DomainGridColor);

        FFTplot.setRangeGridlinesVisible(true);
        FFTplot.setRangeGridlinePaint(RangeGridColor);
        FFTplot.setDomainGridlinePaint(DomainGridColor);

        //chartPlot.setRangeGridlineStroke(new BasicStroke(RangeStroke, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, RangeStroke, new float[]{2.5f, 1.0f}, 0.0f));
        //chartPlot.setDomainGridlineStroke(new BasicStroke(DomainStroke, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, DomainStroke, new float[]{1.0f, 2.5f}, 0.0f));
        chartPlot.setRangeGridlineStroke(new BasicStroke(RangeStroke));
        chartPlot.setDomainGridlineStroke(new BasicStroke(DomainStroke));

        FFTplot.setRangeGridlineStroke(new BasicStroke(RangeStroke));
        FFTplot.setDomainGridlineStroke(new BasicStroke(DomainStroke));

        if (RangeStroke == DomainStroke) {
            chkModifyBoth.setSelected(true);//should I read this value in config file too?
        } else {
            chkModifyBoth.setSelected(false);//should I read this value in config file too?
        }
        //chartPlot.setRangeGridlinePaint(Color.GRAY);
        //chartPlot.setDomainGridlinePaint(Color.GRAY);
        //chartPlot.setRangeGridlineStroke(new BasicStroke(1.0F));
        //chartPlot.setDomainGridlineStroke(new BasicStroke(1.0F));
        //axisX.setTickLabelsVisible(true);
        //axisY.setTickLabelsVisible(true);
        axisX.setTickLabelsVisible(false);
        axisY[1].setTickLabelsVisible(false);

        axisX.setTickMarkOutsideLength(0f);
        axisY[1].setTickMarkOutsideLength(0.0f);

        axisX.setTickMarkInsideLength(3.0f);
        axisY[1].setTickMarkInsideLength(3.0f);

        /* set it to what ever you like*/
        //chartPlot.setBackgroundPaint(Color.WHITE);//(Color.DARK_GRAY);
        //chart.setBackgroundPaint(MenuBar.getBackground());
        //ChartBackgroundColor = ChartBackgroundDefaultColor;
        chartPlot.setBackgroundPaint(ChartBackgroundColor);//(new Color(230, 230, 230)); //(Color.DARK_GRAY);
        FFTplot.setBackgroundPaint(ChartBackgroundColor);

        JFchart.setBackgroundPaint(new Color(250, 250, 250));
        PanelforChart.setLayout(borderLayout1);
        borderLayout1.setHgap(0);
        borderLayout1.setVgap(0);
        PanelforChart.add(chartPanel, BorderLayout.CENTER);

        ModifyChartPopupMenu();

        lbVDivCh1.setVisible(false);
        lbVDivCh2.setVisible(false);
        lbVDivCh3.setVisible(false);
        lbVDivCh4.setVisible(false);

        JFchart.getPlot().setOutlinePaint(null);
        JFchart.setTitle("");


        RightSplitPane.addTab("Ch 1   ", ScrollOptionsCh1);//Demo mode or 4 channels
        RightSplitPane.addTab("Ch 2   ", ScrollOptionsCh2);
        RightSplitPane.addTab("Ch 3   ", ScrollOptionsCh3);
        RightSplitPane.addTab("Ch 4   ", ScrollOptionsCh4);

        MnuCh1.setSelected(true);
        MnuCh2.setSelected(true);
        MnuCh3.setSelected(true);
        MnuCh4.setSelected(true);
        RadioButTrigOnChan1.setEnabled(true);
        RadioButTrigOnChan2.setEnabled(true);
        RadioButTrigOnChan3.setEnabled(true);
        RadioButTrigOnChan4.setEnabled(true);

        OrganizeTabs("Ch 1");//adds tooltips to all the tabs and puts the tabs in the right order

        VoltsDivSettingsReset();
        for (int ch = 1; ch <= 4; ch++) {//do this always, run or demo mode to get divisions right
            VoltsperDivChangedEvent(4, ch);// ch=1 for channel1, ch=2 for channel 2, etc.
        }
        TimeBaseSettingsReset();
        CurrentAdcClockSetting = 50;


        ((JSpinner.DefaultEditor) XAxisZoomValue.getEditor()).getTextField().addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent e) {
                String currValue = ((JSpinner.DefaultEditor) XAxisZoomValue.getEditor()).getTextField().getText();
                ((JSpinner.DefaultEditor) XAxisZoomValue.getEditor()).getTextField().setText(currValue);
                int LenofTxt = ((JSpinner.DefaultEditor) XAxisZoomValue.getEditor()).getTextField().getText().length();
                ((JSpinner.DefaultEditor) XAxisZoomValue.getEditor()).getTextField().setSelectionStart(0);
                ((JSpinner.DefaultEditor) XAxisZoomValue.getEditor()).getTextField().setSelectionEnd(LenofTxt);
            }
        });


        ((JSpinner.DefaultEditor) YAxisZoomValue.getEditor()).getTextField().addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent e) {
                String currValue = ((JSpinner.DefaultEditor) YAxisZoomValue.getEditor()).getTextField().getText();
                ((JSpinner.DefaultEditor) YAxisZoomValue.getEditor()).getTextField().setText(currValue);
                int LenofTxt = ((JSpinner.DefaultEditor) YAxisZoomValue.getEditor()).getTextField().getText().length();
                ((JSpinner.DefaultEditor) YAxisZoomValue.getEditor()).getTextField().setSelectionStart(0);
                ((JSpinner.DefaultEditor) YAxisZoomValue.getEditor()).getTextField().setSelectionEnd(LenofTxt);
            }
        });



        try {
            URL hsURL = HelpSet.findHelpSet(null, "HelpSet.hs");
            hs = new HelpSet(null, hsURL);
            hb = hs.createHelpBroker();
        //new CSH.DisplayHelpFromSource(hb);

        } catch (Exception ee) {
            // Print info to the console if there is an exception
            System.out.println("HelpSet " + ee.getMessage());
        }
    // Create a HelpBroker object for manipulating the help set
    }

    private void CalculateFFTonCurrentSignals() {
        if (USBscope50_Main.FFT_Ch1_4) {
            USBscope50_Main.seriesCh1_FFT.clear();
            USBscope50_Main.seriesCh2_FFT.clear();
            USBscope50_Main.seriesCh3_FFT.clear();
            USBscope50_Main.seriesCh4_FFT.clear();
            t.DisplayFFTData();

        }
    }

    private void ChColorCancelActionPerformed() {
        newColorCh1 = ColorCh1;
        newColorCh2 = ColorCh2;
        newColorCh3 = ColorCh3;
        newColorCh4 = ColorCh4;

        renderer.setSeriesPaint(0, ColorCh1);
        renderer2.setSeriesPaint(0, ColorCh2);
        renderer3.setSeriesPaint(0, ColorCh3);
        renderer4.setSeriesPaint(0, ColorCh4);

        renderer_FFT.setSeriesPaint(0, ColorCh1);
        renderer2_FFT.setSeriesPaint(0, ColorCh2);
        renderer3_FFT.setSeriesPaint(0, ColorCh3);
        renderer4_FFT.setSeriesPaint(0, ColorCh4);

        axisY[1].setTickLabelPaint(ColorCh1);
        axisY[2].setTickLabelPaint(ColorCh2);
        axisY[3].setTickLabelPaint(ColorCh3);
        axisY[4].setTickLabelPaint(ColorCh4);

        axisY_FFT[1].setTickLabelPaint(ColorCh1);
        axisY_FFT[2].setTickLabelPaint(ColorCh2);
        axisY_FFT[3].setTickLabelPaint(ColorCh3);
        axisY_FFT[4].setTickLabelPaint(ColorCh4);

        ChannelColorsDialog.setVisible(false);
    }

    private void FFTFunctionSelected(boolean on_off_state) {
        //boolean FFT_Ch1_4;// made ot global

        if (on_off_state) {//on
            //CalculateFreqStep();


            if (ChannelOn[1] && CheckTabTitles("Ch 1")) {
                FFT_Channel[1] = true;
                FFTButtonCh1.setSelected(true);
                SetButtonState(1, FFTButtonCh1, true);
                renderer_FFT.setSeriesVisible(0, true);
            }
            if (ChannelOn[2] && CheckTabTitles("Ch 2")) {
                FFT_Channel[2] = true;
                FFTButtonCh2.setSelected(true);
                SetButtonState(2, FFTButtonCh2, true);
                renderer2_FFT.setSeriesVisible(0, true);
            }
            if (ChannelOn[3] && CheckTabTitles("Ch 3")) {
                FFT_Channel[3] = true;
                FFTButtonCh3.setSelected(true);
                SetButtonState(3, FFTButtonCh3, true);
                renderer3_FFT.setSeriesVisible(0, true);
            }
            if (ChannelOn[4] && CheckTabTitles("Ch 4")) {
                FFT_Channel[4] = true;
                FFTButtonCh4.setSelected(true);
                SetButtonState(4, FFTButtonCh4, true);
                renderer4_FFT.setSeriesVisible(0, true);
            }

            if (demoMode) {
                FFTButtonCh1.setSelected(true);
                FFTButtonCh2.setSelected(true);
                FFTButtonCh3.setSelected(true);
                FFTButtonCh4.setSelected(true);
                SetButtonState(1, FFTButtonCh1, true);
                SetButtonState(2, FFTButtonCh2, true);
                SetButtonState(3, FFTButtonCh3, true);
                SetButtonState(4, FFTButtonCh4, true);

                FFT_Channel[1] = true;
                FFT_Channel[2] = true;
                FFT_Channel[3] = true;
                FFT_Channel[4] = true;
                renderer_FFT.setSeriesVisible(0, true);
                renderer2_FFT.setSeriesVisible(0, true);
                renderer3_FFT.setSeriesVisible(0, true);
                renderer4_FFT.setSeriesVisible(0, true);
            }

            FFT_Ch1_4 = FFT_Channel[1] || FFT_Channel[2] || FFT_Channel[3] || FFT_Channel[4];
            MnuShowYAxisLabelsActionPerformed();

            if ((LoadDataArray.SoftwareStopped && FFT_Ch1_4) || demoMode) {
                CalculateFFTonCurrentSignals();
            }

            if (plot.getSubplots().contains(FFTplot)) {//FFT plot already on
            } else if (FFT_Ch1_4) {
                plot.add(FFTplot, 1);
            }
            USBscope50_Main.lbFDivAllChs.setVisible(true);


        } else {//off

            //lbFDivAllChs.setText("");
            lbFDivAllChs.setVisible(false);

            FFTButtonCh1.setSelected(false);
            FFTButtonCh2.setSelected(false);
            FFTButtonCh3.setSelected(false);
            FFTButtonCh4.setSelected(false);

            SetButtonState(1, FFTButtonCh1, false);
            SetButtonState(2, FFTButtonCh2, false);
            SetButtonState(3, FFTButtonCh3, false);
            SetButtonState(4, FFTButtonCh4, false);

            FFT_Channel[1] = false;
            FFT_Channel[2] = false;
            FFT_Channel[3] = false;
            FFT_Channel[4] = false;

            //labels axis false

            renderer_FFT.setSeriesVisible(0, false);
            renderer2_FFT.setSeriesVisible(0, false);
            renderer3_FFT.setSeriesVisible(0, false);
            renderer4_FFT.setSeriesVisible(0, false);

            MnuShowYAxisLabelsActionPerformed();
            if (plot.getSubplots().contains(FFTplot)) {
                plot.remove(FFTplot);
            }

            if (!(plot.getSubplots().contains(chartPlot))) {
                plot.add(chartPlot);
            }
        }
        validate();
    }

    private void InitialCouplingValues() {
        //check gnd_option values as read form config file
        if (gnd_option[1] > 0) {
            OpGNDCh1.setSelected(true);
            CouplingActionPerformed(1, 0, 1, false);//1=channel1,Dc_option[1]=0,gnd_option[1]=1
        }

        if (gnd_option[2] > 0) {
            OpGNDCh2.setSelected(true);
            CouplingActionPerformed(2, 0, 1, false);//2=channel2,Dc_option[2]=0,gnd_option[1]=1
        }

        if (gnd_option[3] > 0) {
            OpGNDCh3.setSelected(true);
            CouplingActionPerformed(3, 0, 1, false);//3=channel3,Dc_option[3]=0,gnd_option[1]=1
        }

        if (gnd_option[4] > 0) {
            OpGNDCh4.setSelected(true);
            CouplingActionPerformed(4, 0, 1, false);//4=channel4,Dc_option[4]=0,gnd_option[1]=1
        }

        if (Dc_option[1] > 0) {
            OpDCCouplingCh1.setSelected(true);
            CouplingActionPerformed(1, 1, 0, false);//1=channel1,Dc_option[1]=0,gnd_option[1]=1
        }

        if (Dc_option[2] > 0) {
            OpDCCouplingCh2.setSelected(true);
            CouplingActionPerformed(2, 1, 0, false);//2=channel2,Dc_option[2]=0,gnd_option[2]=1
        }

        if (Dc_option[3] > 0) {
            OpDCCouplingCh3.setSelected(true);
            CouplingActionPerformed(3, 1, 0, false);//3=channel3,Dc_option[3]=0,gnd_option[3]=1
        }

        if (Dc_option[4] > 0) {
            OpDCCouplingCh4.setSelected(true);
            CouplingActionPerformed(4, 1, 0, false);//4=channel4,Dc_option[4]=0,gnd_option[4]=1
        }

        if (Probe[1] == 2) {
            Opx10ProbeCh1.setSelected(true);
            ProbeOptionActionPerformed(1, 2);//channel, probe setting 1 for x1, 2 for x10
        }

        if (Probe[2] == 2) {
            Opx10ProbeCh2.setSelected(true);
            ProbeOptionActionPerformed(2, 2);//channel, probe setting 1 for x1, 2 for x10
        }

        if (Probe[3] == 2) {
            Opx10ProbeCh3.setSelected(true);
            ProbeOptionActionPerformed(3, 2);//channel, probe setting 1 for x1, 2 for x10
        }

        if (Probe[4] == 2) {
            Opx10ProbeCh4.setSelected(true);
            ProbeOptionActionPerformed(4, 2);//channel, probe setting 1 for x1, 2 for x10
        }
    }

    private void InitialTriggerSettings() {

        //SetTriggerMode
        if (triggermode == intTriggerModeNormal) {//1
            if (currentTrigType == intLessThan) {
                TriggerModeLessThan.setSelected(true);
                lbTriggerStatus.setIcon(triggerLessThan);
            } else if (currentTrigType == intGreaterThan) {
                TriggerModeGreaterThan.setSelected(true);
                lbTriggerStatus.setIcon(triggerMoreThan);
            } else if (currentTrigType == intFallingEdge) {
                TriggerModeFallingEdge.setSelected(true);
                lbTriggerStatus.setIcon(triggerFalling);
            } else if (currentTrigType == intRisingEdge) {
                TriggerModeRisingEdge.setSelected(true);
                lbTriggerStatus.setIcon(triggerRising);
            }

            /*TriggerModeRisingEdgeSelected();
            SetTriggerMode(intTriggerModeNormal);
            SetTrigType(intRisingEdge);
            triggermode = 1;*/


            //ThresholdMarkerOn = true;
            SetThreasholdStatus(true);
            //SetTriggerDelayControlls(true);
            SetTriggerDelayControlls(false); //it should be true- not implemented yet
            SetTriggerMode(intTriggerModeNormal);
            SetTrigType(currentTrigType);

        } else if (triggermode == intTriggerModeFree) {//0
            TriggerModeFree.setSelected(true);
            TriggerModeFreeSelected();
            SetTriggerMode(intTriggerModeFree);
        }


        switch (TriggerChannel) {
            case 1:
                RadioButTrigOnChan1.setSelected(true);
                break;
            case 2:
                if (RadioButTrigOnChan2.isEnabled()) {
                    RadioButTrigOnChan2.setSelected(true);
                } else {
                    TriggerChannel = 1;
                    RadioButTrigOnChan1.setSelected(true);
                }
                break;
            case 3:
                if (RadioButTrigOnChan3.isEnabled()) {
                    RadioButTrigOnChan3.setSelected(true);
                } else {
                    TriggerChannel = 1;
                    RadioButTrigOnChan1.setSelected(true);
                }
                break;
            case 4:
                if (RadioButTrigOnChan3.isEnabled()) {
                    RadioButTrigOnChan4.setSelected(true);
                } else {
                    TriggerChannel = 1;
                    RadioButTrigOnChan1.setSelected(true);
                }
                break;
            default:
                return;
        }
        MakeThisTrigMaster(TriggerChannel);
    }

    private void InitialChSettings() {

        if (INV[1] == -1) {
            INVButtonCh1.setSelected(true);
            SetButtonState(1, INVButtonCh1, INVButtonCh1.isSelected());
            PlotINVData(1, -1);
        }

        if (INV[2] == -1) {
            INVButtonCh2.setSelected(true);
            SetButtonState(2, INVButtonCh2, INVButtonCh2.isSelected());
            PlotINVData(2, -1);

        }


        if (INV[3] == -1) {
            INVButtonCh3.setSelected(true);
            SetButtonState(3, INVButtonCh3, INVButtonCh3.isSelected());
            PlotINVData(3, -1);
        }

        if (INV[4] == -1) {
            INVButtonCh4.setSelected(true);
            SetButtonState(4, INVButtonCh4, INVButtonCh4.isSelected());
            PlotINVData(4, -1);
        }

    }

    private void MnuLinearFFTPlotActionPerformed() {


        if (MnuLinearFFTPlot.isSelected()) {
            dblFFTPlotType = 1;
            axisY_FFT[1].setRange(0.0, YDiv[1] * 5);
            axisY_FFT[1].setTickUnit(new NumberTickUnit(YDiv[1]), true, true);
            axisY_FFT[2].setRange(0.0, YDiv[2] * 5);
            axisY_FFT[2].setTickUnit(new NumberTickUnit(YDiv[2]), true, true);
            axisY_FFT[3].setRange(0.0, YDiv[3] * 5);
            axisY_FFT[3].setTickUnit(new NumberTickUnit(YDiv[3]), true, true);
            axisY_FFT[4].setRange(0.0, YDiv[4] * 5);
            axisY_FFT[4].setTickUnit(new NumberTickUnit(YDiv[4]), true, true);

            CalculateFFTonCurrentSignals();
        }
    }

    private void MnuShowYAxisLabelsActionPerformed() {
        int ch;
        for (ch = 1; ch <= 4; ch++) {
            axisY[ch].setTickLabelsVisible(MnuShowYAxisLabels.isSelected() && (ChannelOn[ch] || demoMode));
            axisY_FFT[ch].setTickLabelsVisible(MnuShowYAxisLabels.isSelected() && (ChannelOn[ch] && FFT_Channel[ch] || demoMode));
        }
    }

    private void MnudBVFFTPlotActionPerformed() {


        if (MnudBVFFTPlot.isSelected()) {
            dblFFTPlotType = 0;
            Range FFTRange = new Range(-60.0, 10.0);//At this point YDiv is 0 ;//5*YDiv[1]);
            axisY_FFT[1].setRange(FFTRange, true, true);
            axisY_FFT[2].setRange(FFTRange, true, true);
            axisY_FFT[3].setRange(FFTRange, true, true);
            axisY_FFT[4].setRange(FFTRange, true, true);

            axisY_FFT[1].setTickUnit(new NumberTickUnit(10), true, true);
            axisY_FFT[2].setTickUnit(new NumberTickUnit(10), true, true);
            axisY_FFT[3].setTickUnit(new NumberTickUnit(10), true, true);
            axisY_FFT[4].setTickUnit(new NumberTickUnit(10), true, true);
            CalculateFFTonCurrentSignals();
        }
    }

    private void ProbeOptionActionPerformed(int channel, int ProbeSetting) {
        Probe[channel] = ProbeSetting;
        UpdateVDivLabels(intVoltsperDivPointer[channel], channel);

        VoltsperDivChangedEvent(intVoltsperDivPointer[channel], channel);//1 = channel 1
        seriesCh1.clear();
        seriesCh2.clear();
        seriesCh3.clear();
        seriesCh4.clear();
        if (demoMode) {
            t.DisplayDataDemo(JavaRunning);
        } else {
            t.DisplayData();
        }

        SliderThreshold.setValue(SliderThreshold.getValue() + 1);
        SliderThreshold.setValue(SliderThreshold.getValue() - 1);

        //clear zero marker for this channel
        //this gets complicated as all annotations are fixed on the range of channel 1
        if (channel == 1 && chartPlot.getAnnotations().contains(ZeroPointCh1)) {
            //chartPlot.removeAnnotation(ZeroPointCh1);
            if (chartPlot.getAnnotations().contains(ZeroPointCh2)) {
                chartPlot.removeAnnotation(ZeroPointCh2);
            }
            if (chartPlot.getAnnotations().contains(ZeroPointCh3)) {
                chartPlot.removeAnnotation(ZeroPointCh3);
            }
            if (chartPlot.getAnnotations().contains(ZeroPointCh4)) {
                chartPlot.removeAnnotation(ZeroPointCh4);
            }

            if (Probe[1] == 2) {
                ZeroPointCh1Value = ZeroPointCh1Value * 10;
                ZeroPointCh2Value = ZeroPointCh2Value * 10;
                ZeroPointCh3Value = ZeroPointCh3Value * 10;
                ZeroPointCh4Value = ZeroPointCh4Value * 10;

            } else {
                ZeroPointCh1Value = ZeroPointCh1Value / 10;
                ZeroPointCh2Value = ZeroPointCh2Value / 10;
                ZeroPointCh3Value = ZeroPointCh3Value / 10;
                ZeroPointCh4Value = ZeroPointCh4Value / 10;
            }
            drawZeroPointChMarkers(0, false, CheckTabTitles("Ch 2"), CheckTabTitles("Ch 3"), CheckTabTitles("Ch 4"));
            String TempdeltaText = "";
            if (DeltaShowing) {
                TempdeltaText = DeltaText.getText();
                chartPlot.removeAnnotation(Delta);
                chartPlot.removeAnnotation(DeltaText);


                int mult = 1;
                if (Probe[1] == 2) {
                    mult = 10;
                }

                double smallerX = (VerticalMarkerLocation[0] < VerticalMarkerLocation[1] ? VerticalMarkerLocation[0] : VerticalMarkerLocation[1]);
                Font font = new Font("SansSerif", Font.PLAIN, 9);
                Delta = new XYLineAnnotation(VerticalMarkerLocation[0], YDiv[1] * mult * 4.0, VerticalMarkerLocation[1], YDiv[1] * mult * 4.0);
                DeltaText = new XYTextAnnotation(TempdeltaText, Math.abs(VerticalMarkerLocation[0] - VerticalMarkerLocation[1]) / 2 + smallerX, (YDiv[1] * mult * 4.0) + (YDiv[1] * mult / 3));
                DeltaText.setFont(font);
                chartPlot.addAnnotation(Delta);
                chartPlot.addAnnotation(DeltaText);
                DeltaShowing = true;
            }

        }
    }

    private boolean ReadMarkersXYLocation(String TxtFileLine, int chartXY, int markerNum) {//1 for horizontal(chartY) value, 1 for marker 1
        String tempString;
        int equal = TxtFileLine.indexOf("=");
        int semicolumn = TxtFileLine.indexOf(";");
        int comma = TxtFileLine.indexOf(",");//there shouldn't be comma in SplitDivider line

        if ((equal < 0) || (semicolumn < 0 || (equal > semicolumn) || comma >= 0)) {
            return true;//abort
        }
        tempString = TxtFileLine.substring(equal + 1, semicolumn).trim();//tempString e.g. "539" or "null"
        try {
            if (tempString.equalsIgnoreCase("null")) {
                //DividerLocation = Integer.parseInt(tempString);
            } else {
                if (chartXY == 1) {//horizontal
                    switch (markerNum) {
                        case 1:
                            HorizontalMarkerLocation[0] = Double.parseDouble(tempString);
                            break;
                        case 2:
                            HorizontalMarkerLocation[1] = Double.parseDouble(tempString);
                            break;
                        default:
                            break;
                    }
                } else {
                    switch (markerNum) {
                        case 1:
                            VerticalMarkerLocation[0] = Double.parseDouble(tempString);
                            break;
                        case 2:
                            VerticalMarkerLocation[1] = Double.parseDouble(tempString);
                            break;
                        default:
                            break;
                    }
                }
            }
        } catch (NumberFormatException ex) {
            System.out.println("Error encountered while reading chartXY from config file\n" + ex);
            return true;//abort
        }
        return false;
    }

    private boolean ReadMnuAddMarker(String TxtFileLine, int HorizontalVertical) {//1 for horizontal, 2 for vertical
        String tempString;
        int equal = TxtFileLine.indexOf("=");
        int semicolumn = TxtFileLine.indexOf(";");


        if ((equal < 0) || (semicolumn < 0 || (equal > semicolumn))) {
            return true;//abort
        }

        tempString = TxtFileLine.substring(equal + 1, semicolumn).trim();
        equal = Integer.parseInt(tempString);

        if (equal >= 0 && equal < 2) {//false or true
            if (HorizontalVertical == 1) {
                MnuAddHorizontalMarker.setSelected(equal != 0);
            } else if (HorizontalVertical == 2) {
                MnuAddVerticalMarker.setSelected(equal != 0);
            }
        } else {
            return true;
        }

        return false;
    }

    private boolean ReadAxisSetting(String TxtFileLine, int XorY) {//zero for X axis and 1 for Y axis
        String tempString;
        int equal = TxtFileLine.indexOf("=");
        int semicolumn = TxtFileLine.indexOf(";");


        if ((equal < 0) || (semicolumn < 0 || (equal > semicolumn))) {
            return true;//abort
        }

        tempString = TxtFileLine.substring(equal + 1, semicolumn).trim();
        equal = Integer.parseInt(tempString);

        if (equal >= 0 && equal < 2) {//false or true
            if (XorY > 0) {//Y
                MnuShowYAxisLabels.setSelected(equal != 0);
            } //else {//X
        //  MnuShowXAxisLabels.setSelected(equal != 0);
        //}
        } else {
            return true;
        }

        return false;
    }

    private boolean ReadOffset(String TxtFileLine) {
        String tempString;
        int equal = TxtFileLine.indexOf("=");
        int semicolumn = TxtFileLine.indexOf(";");
        int comma;

        String tempOffsetSetting = "0.0";//default false

        if ((equal < 0) || (semicolumn < 0 || (equal > semicolumn))) {
            return true;//abort
        }
        tempString = TxtFileLine.substring(equal + 1, semicolumn).trim();
        comma = tempString.indexOf(",");

        if (comma > 0) {
            tempOffsetSetting = tempString.substring(0, comma).trim();
        } else {
            return true;//abort something wrong in this line
        }


        if (Double.parseDouble(tempOffsetSetting) >= -100.0 && Double.parseDouble(tempOffsetSetting) <= 100) {
            functionOffset[1] = Double.parseDouble(tempOffsetSetting) * 100.0;
        }

        tempString = tempString.substring(comma + 1).trim();
        comma = tempString.indexOf(",");

        if (comma > 0) {
            tempOffsetSetting = tempString.substring(0, comma).trim();
        } else {
            return true;
        }

        if (Double.parseDouble(tempOffsetSetting) >= -100.0 && Double.parseDouble(tempOffsetSetting) <= 100) {
            functionOffset[2] = Double.parseDouble(tempOffsetSetting) * 100.0;
        }

        tempString = tempString.substring(comma + 1).trim();
        comma = tempString.indexOf(",");

        if (comma > 0) {
            tempOffsetSetting = tempString.substring(0, comma).trim();
        } else {
            return true;
        }

        if (Double.parseDouble(tempOffsetSetting) >= -100.0 && Double.parseDouble(tempOffsetSetting) <= 100) {
            functionOffset[3] = Double.parseDouble(tempOffsetSetting) * 100.0;
        }

        tempString = tempString.substring(comma + 1).trim();
        if (Double.parseDouble(tempOffsetSetting) >= -100.0 && Double.parseDouble(tempOffsetSetting) <= 100) {
            functionOffset[4] = Double.parseDouble(tempString) * 100.0;
        }

        return false;
    }

    private boolean ReadFFTSettings(String TxtFileLine) {
        String tempString;
        int equal = TxtFileLine.indexOf("=");
        int semicolumn = TxtFileLine.indexOf(";");
        int comma;
        String tempFFTSetting = "0";//default false
        if ((equal < 0) || (semicolumn < 0 || (equal > semicolumn))) {
            return true;//abort
        }
        tempString = TxtFileLine.substring(equal + 1, semicolumn).trim();//0, 1, 0, 0
        comma = tempString.indexOf(",");

        if (comma > 0) {
            tempFFTSetting = tempString.substring(0, comma).trim();
        } else {
            return true;//abort something wrong in this line
        }

        if (Integer.parseInt(tempFFTSetting) > 0) {
            FFT_Channel[1] = true;//channel 1
        } else {
            FFT_Channel[1] = false;
        }

        tempString = tempString.substring(comma + 1).trim();
        comma = tempString.indexOf(",");

        if (comma > 0) {
            tempFFTSetting = tempString.substring(0, comma).trim();
        } else {
            return true;
        }

        if (Integer.parseInt(tempFFTSetting) > 0) {
            FFT_Channel[2] = true;//channel 2
        } else {
            FFT_Channel[2] = false;
        }

        tempString = tempString.substring(comma + 1).trim();
        comma = tempString.indexOf(",");

        if (comma > 0) {
            tempFFTSetting = tempString.substring(0, comma).trim();
        } else {
            return true;
        }

        if (Integer.parseInt(tempFFTSetting) > 0) {
            FFT_Channel[3] = true;//channel 3
        } else {
            FFT_Channel[3] = false;
        }

        tempString = tempString.substring(comma + 1).trim();
        if ((tempString.length() > 0) && (Integer.parseInt(tempString) > 0)) {
            FFT_Channel[4] = true;//channel 4
        } else {
            FFT_Channel[4] = false;
        }

        return false;
    }

    private boolean ReadFFTPlotType(String TxtFileLine) {
        String tempString;
        int equal = TxtFileLine.indexOf("=");
        int semicolumn = TxtFileLine.indexOf(";");


        if ((equal < 0) || (semicolumn < 0 || (equal > semicolumn))) {
            return true;//abort
        }

        tempString = TxtFileLine.substring(equal + 1, semicolumn).trim();//4

        if ((Double.parseDouble(tempString) >= 0) && (Double.parseDouble(tempString) < 2)) {
            dblFFTPlotType = Double.parseDouble(tempString);
            if (dblFFTPlotType < 1.0) {//0
                MnudBVFFTPlot.setSelected(true);
            } else {
                MnuLinearFFTPlot.setSelected(true);
            }
        } else {
            return true;
        }

        return false;
    }

    private boolean ReadFFTWindow(String TxtFileLine) {
        String tempString;
        int equal = TxtFileLine.indexOf("=");
        int semicolumn = TxtFileLine.indexOf(";");


        if ((equal < 0) || (semicolumn < 0 || (equal > semicolumn))) {
            return true;//abort
        }

        tempString = TxtFileLine.substring(equal + 1, semicolumn).trim();//4

        if ((Long.parseLong(tempString) >= 0) && (Long.parseLong(tempString) < 5)) {
            lngFFTWindowType = Long.parseLong(tempString);
            if (lngFFTWindowType < 1.0) {//0
                MnuRectangular.setSelected(true);
            } else if (lngFFTWindowType < 2.0) {
                MnuHanning.setSelected(true);
            } else if (lngFFTWindowType < 3.0) {
                MnuHamming.setSelected(true);
            } else if (lngFFTWindowType < 4.0) {
                MnuTriangular.setSelected(true);
            } else {
                MnuWelch.setSelected(true);
            }
        } else {
            return true;
        }

        return false;
    }

    private void ReadNullOffsetSettings() {
        String NullOffsetFileName;
        String path = null;
        File NullOffsetFile;
        String TxtFileLine = null;
        FileReader fr = null;


        if (USBFamily_Main.OS.equalsIgnoreCase("Windows")) {
            if (USBFamily_Main.Vista) {
                path = (System.getProperty("user.home") + "/AppData/" + companyID + "/" + productID + " Java Software/");
            } else {
                path = (System.getenv("ProgramFiles") + "/" + companyID + "/" + productID + " Java Software/");
            }
        } else if (USBFamily_Main.OS.equalsIgnoreCase("Linux")) {
            path = System.getProperty("user.home") + "/.";
        }

        if (demoMode) {
        } else {
            for (int loop = 1; loop <= numScopesFound; loop++) {

                NullOffsetFileName = ScopeSerialNumbers[loop] + "_NullOffset.txt";
                NullOffsetFile = new File(path + NullOffsetFileName);
                if (NullOffsetFile.exists() && NullOffsetFile.canRead() && NullOffsetFile.isFile()) {

                    try {
                        fr = new FileReader(NullOffsetFile);
                        BufferedReader br = new BufferedReader(fr);
                        while ((TxtFileLine = br.readLine()) != null) {
                            if (TxtFileLine.length() <= 0 || TxtFileLine.startsWith("//")) { //empty line and comments ignored
                            } else {
                                try {
                                    if (TxtFileLine.startsWith("OffsetNull[0] = ")) {
                                        LoadDataArray.OffsetNull[loop][0] = Float.parseFloat(TxtFileLine.substring(16).trim());

                                    } else if (TxtFileLine.startsWith("OffsetNull[1] = ")) {
                                        LoadDataArray.OffsetNull[loop][1] = Float.parseFloat(TxtFileLine.substring(16).trim());

                                    } else if (TxtFileLine.startsWith("OffsetNull[2] = ")) {
                                        LoadDataArray.OffsetNull[loop][2] = Float.parseFloat(TxtFileLine.substring(16).trim());

                                    }
                                } catch (Exception ex) {
                                    //System.out.println("ex: " + ex);
                                    //System.out.println(""+ LoadDataArray.OffsetNull[loop][0]);//keeps defualt 0 value
                                    //System.out.println(""+ LoadDataArray.OffsetNull[loop][1]);
                                    //System.out.println(""+ LoadDataArray.OffsetNull[loop][2]);
                                }
                            }
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(USBscope50_Main.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        try {
                            fr.close();
                        } catch (IOException ex) {
                            Logger.getLogger(USBscope50_Main.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                }

            }
        }
    }

    private void SetFFTSubPlot(int intChannel, boolean ONstate) {

        //boolean FFT_Ch1_4;
        FFT_Channel[intChannel] = ONstate;
        FFT_Ch1_4 = FFT_Channel[1] || FFT_Channel[2] || FFT_Channel[3] || FFT_Channel[4];



        if (FFT_Ch1_4 && !plot.getSubplots().contains(FFTplot)) {
            CalculateFFTonCurrentSignals();
            plot.add(FFTplot);
        }


        if (!FFT_Ch1_4 && plot.getSubplots().contains(FFTplot)) {
            plot.remove(FFTplot);
        }

    }

    private void SortOutInitialFFTsettings() {

        FFT_Ch1_4 = FFT_Channel[1] || FFT_Channel[2] || FFT_Channel[3] || FFT_Channel[4];


        if (FFT_Channel[1]) {
            FFTButtonCh1.setSelected(true);
            SetButtonState(1, FFTButtonCh1, true);
            renderer_FFT.setSeriesVisible(0, true);
        }

        if (FFT_Channel[2]) {

            FFTButtonCh2.setSelected(true);
            SetButtonState(2, FFTButtonCh2, true);
            renderer2_FFT.setSeriesVisible(0, true);
        }
        if (FFT_Channel[3]) {

            FFTButtonCh3.setSelected(true);
            SetButtonState(3, FFTButtonCh3, true);
            renderer3_FFT.setSeriesVisible(0, true);
        }
        if (FFT_Channel[4]) {

            FFTButtonCh4.setSelected(true);
            SetButtonState(4, FFTButtonCh4, true);
            renderer4_FFT.setSeriesVisible(0, true);
        }


        if (LoadDataArray.SoftwareStopped && FFT_Ch1_4) {
            CalculateFFTonCurrentSignals();
        }

        if (plot.getSubplots().contains(FFTplot)) {//FFT plot already on
            } else if (FFT_Ch1_4) {
            plot.add(FFTplot, 1);
        }



    }

    private void UpdateFFTgraph(long lngFFTWindowType) {
        CalculateFFTonCurrentSignals();
    }

    private void UpdateFFTlabel(double FFTLabelAdjust) {
        if ((FFTLabelAdjust) >= (1000000 / 100)) {
            //lbFDivAllChs.setText("FFT: " + (FreqStep*10/1000000.000) + " MHz/div");//normally you would get 100 point per division
            lbFDivAllChs.setText("FFT: " + FFTlabelFormatter.format(FFTLabelAdjust * 100 / 1000000) + " MHz/div");//100 points per division
        } else if ((FFTLabelAdjust) >= (1000 / 100)) {
            //lbFDivAllChs.setText("FFT: " + (FreqStep*10/1000.000) + " KHz/div");
            lbFDivAllChs.setText("FFT: " + FFTlabelFormatter.format(FFTLabelAdjust * 100 / 1000) + " KHz/div");
        } else if ((FFTLabelAdjust) > (1 / 100)) {
            lbFDivAllChs.setText("FFT: " + FFTlabelFormatter.format(FFTLabelAdjust * 100) + " Hz/div");
        //lbFDivAllChs.setText("FFT: " + (FreqStep*10/1.000) + " Hz/div");//normally you would get 100 point per division
        //with fft as there i write every third point i have 10 points per division (*10)
        } else {// if((FreqStep*10/1000)<=1){
            lbFDivAllChs.setText("FFT: " + FFTlabelFormatter.format(FFTLabelAdjust * 100 * 1000) + " mHz/div");
        }
    }

    private void UpdateGUIColors() {
        ArrowsPanelLineCh1.setBorder(javax.swing.BorderFactory.createLineBorder(ColorCh1));
        ArrowsPanelLineCh2.setBorder(javax.swing.BorderFactory.createLineBorder(ColorCh2));
        ArrowsPanelLineCh3.setBorder(javax.swing.BorderFactory.createLineBorder(ColorCh3));
        ArrowsPanelLineCh4.setBorder(javax.swing.BorderFactory.createLineBorder(ColorCh4));
        SetButtonState(1, ONButtonCh1, ONButtonCh1.isSelected());
        SetButtonState(2, ONButtonCh2, ONButtonCh2.isSelected());
        SetButtonState(3, ONButtonCh3, ONButtonCh3.isSelected());
        SetButtonState(4, ONButtonCh4, ONButtonCh4.isSelected());
        SetButtonState(1, INVButtonCh1, INVButtonCh1.isSelected());
        SetButtonState(2, INVButtonCh2, INVButtonCh2.isSelected());
        SetButtonState(3, INVButtonCh3, INVButtonCh3.isSelected());
        SetButtonState(4, INVButtonCh4, INVButtonCh4.isSelected());
        SetButtonState(1, COMPButtonCh1, COMPButtonCh1.isSelected());
        SetButtonState(2, COMPButtonCh2, COMPButtonCh2.isSelected());
        SetButtonState(3, COMPButtonCh3, COMPButtonCh3.isSelected());
        SetButtonState(4, COMPButtonCh4, COMPButtonCh4.isSelected());
        SetButtonState(1, FFTButtonCh1, FFTButtonCh1.isSelected());
        SetButtonState(2, FFTButtonCh2, FFTButtonCh2.isSelected());
        SetButtonState(3, FFTButtonCh3, FFTButtonCh3.isSelected());
        SetButtonState(4, FFTButtonCh4, FFTButtonCh4.isSelected());

        lbVDivCh1.setBackground(ColorCh1);
        lbVDivCh2.setBackground(ColorCh2);
        lbVDivCh3.setBackground(ColorCh3);
        lbVDivCh4.setBackground(ColorCh4);
        updateZeroMarkers();
    }

    private void ChColorOKActionPerformed() {
        ColorCh1 = newColorCh1;
        ColorCh2 = newColorCh2;
        ColorCh3 = newColorCh3;
        ColorCh4 = newColorCh4;

        UpdateGUIColors();
        ChColorCancelActionPerformed();
    }

    private void SetChColorDefaultValues() {
        newColorCh1 = DefaultColorCh1;//new Color(255, 200, 0);    //Ch1 default orange
        newColorCh2 = DefaultColorCh2;//Color.GREEN;
        newColorCh3 = DefaultColorCh3;//Color.BLUE;
        newColorCh4 = DefaultColorCh4;//Color.RED;

        renderer.setSeriesPaint(0, newColorCh1);
        renderer2.setSeriesPaint(0, newColorCh2);
        renderer3.setSeriesPaint(0, newColorCh3);
        renderer4.setSeriesPaint(0, newColorCh4);

        renderer_FFT.setSeriesPaint(0, newColorCh1);
        renderer2_FFT.setSeriesPaint(0, newColorCh2);
        renderer3_FFT.setSeriesPaint(0, newColorCh3);
        renderer4_FFT.setSeriesPaint(0, newColorCh4);

        axisY[1].setTickLabelPaint(newColorCh1);
        axisY[2].setTickLabelPaint(newColorCh2);
        axisY[3].setTickLabelPaint(newColorCh3);
        axisY[4].setTickLabelPaint(newColorCh4);

        axisY_FFT[1].setTickLabelPaint(newColorCh1);
        axisY_FFT[2].setTickLabelPaint(newColorCh2);
        axisY_FFT[3].setTickLabelPaint(newColorCh3);
        axisY_FFT[4].setTickLabelPaint(newColorCh4);


    }

    private void PlotOffsetData(int offsetCh, double OffsetSliderVal) {

        int mult = 1;

        if (Probe[offsetCh] == 2) {
            mult = 10;
        }

        double step = mult * OffsetSliderVal * YDiv[offsetCh] * 5 / 10000;// * INV[offsetCh];

        switch (offsetCh) {
            case 1:
                if (seriesCh1.isEmpty()) {
                    break;
                }

                for (int i = 0; i < 3000; i++) {
                    //for (int i = 0; i < 2500; i++) {
                    seriesCh1.updateByIndex(i, (seriesCh1.getDataItem(i).getYValue() + step));
                }
                break;
            case 2:
                if (seriesCh2.isEmpty()) {
                    break;
                }
                for (int i = 0; i < 3000; i++) {
                    //for (int i = 0; i < 2500; i++) {
                    seriesCh2.updateByIndex(i, (seriesCh2.getDataItem(i).getYValue() + step));
                }
                break;
            case 3:
                if (seriesCh3.isEmpty()) {
                    break;
                }
                for (int i = 0; i < 3000; i++) {
                    //for (int i = 0; i < 2500; i++) {
                    seriesCh3.updateByIndex(i, (seriesCh3.getDataItem(i).getYValue() + step));
                }
                break;
            case 4:
                if (seriesCh4.isEmpty()) {
                    break;
                }
                for (int i = 0; i < 3000; i++) {
                    //for (int i = 0; i < 2500; i++) {
                    seriesCh4.updateByIndex(i, (seriesCh4.getDataItem(i).getYValue() + step));
                }
                break;

            default:
                //System.out.println("DIDN'T WANT THIS! default");
                break;
        }
    }

    private void PlotOffsetDataDemo(int offsetChannel, double OffsetSliderValue) {
        double step = OffsetSliderValue * YDiv[offsetChannel] * 5 / 10000;// * INV[offsetChannel];

        int i;
        switch (offsetChannel) {
            case 1:
                if (seriesCh1.isEmpty()) {
                    break;
                }

                for (i = 0; i < 3000; i++) {
                    LoadDataArray.SampleData[1][i] = (float) (LoadDataArray.SampleData[1][i] + step);
                }
                break;
            case 2:
                if (seriesCh2.isEmpty()) {
                    break;
                }
                for (i = 0; i < 3000; i++) {
                    LoadDataArray.SampleData[2][i] = (float) (LoadDataArray.SampleData[2][i] + step);
                }
                break;
            case 3:
                if (seriesCh3.isEmpty()) {
                    break;
                }
                for (i = 0; i < 3000; i++) {
                    LoadDataArray.SampleData[3][i] = (float) (LoadDataArray.SampleData[3][i] + step);
                }
                break;
            case 4:
                if (seriesCh4.isEmpty()) {
                    break;
                }
                for (i = 0; i < 3000; i++) {
                    LoadDataArray.SampleData[4][i] = (float) (LoadDataArray.SampleData[4][i] + step);
                }
                break;
            default:
                //System.out.println("We should never get here");
                break;
        }
    }

    private void PlotINVData(int offsetChannel, int INVValue) {
        int n = 0;
        if (USBFamily_Main.OS.equalsIgnoreCase("Linux") && demoMode) {
            n = 2;//???
        }

        USBscope50_Main.seriesCh1.clear();
        USBscope50_Main.seriesCh2.clear();
        USBscope50_Main.seriesCh3.clear();
        USBscope50_Main.seriesCh4.clear();
        if (demoMode) {

            double step = functionOffset[offsetChannel] * YDiv[offsetChannel] * 5 / 10000;

            for (int i = 0; i < 3000; i++) {

                LoadDataArray.SampleData[offsetChannel][i] = (float) (LoadDataArray.SampleData[offsetChannel][i] - step);
                LoadDataArray.SampleData[offsetChannel][i] = -LoadDataArray.SampleData[offsetChannel][i];
                LoadDataArray.SampleData[offsetChannel][i] = (float) (LoadDataArray.SampleData[offsetChannel][i] + step);
            }

            t.DisplayDataDemo(false);
        } else {
            t.DisplayData();
        }

    }

    @Override
    public void chartMouseClicked(ChartMouseEvent arg0) {
        /* graph mouse listener added
         * when user clicks on the graph display a marker at that location(if MnuAddMarkers options selected)
         */

        chartX = 0; //reset to zero before this value read
        chartY = 0; // cannot have function return two values, so cheat and make them global
        String HorizontalMarkerLabel = "";
        int whichVUnit = 0;


        //OffsetSliderValue[1] = OffsetSliderCh1.getValue();
        //OffsetSliderValue[2] = OffsetSliderCh2.getValue();
        //OffsetSliderValue[3] = OffsetSliderCh3.getValue();
        //OffsetSliderValue[4] = OffsetSliderCh4.getValue();



        if (MnuAddHorizontalMarker.isSelected() || MnuAddVerticalMarker.isSelected()) {

            for (int i = 1; i <= numScopesFound; i++) {
                if ((ChannelOn[i] || demoMode) && CheckTabTitles("Ch " + i)) {
                    /*if (intVoltsperDivPointer[i] < 8) {
                    whichVUnit = 0;
                    } else {
                    whichVUnit = 1;
                    }*/
                    whichVUnit = 1;
                    GetGraphValueOfMouseClickedPoint(arg0.getTrigger().getX(), arg0.getTrigger().getY(), i - 1);
                    HorizontalMarkerLabel = HorizontalMarkerLabel + "Ch" + i + "=" + df.format(chartY - (functionOffset[i] * 5 * YDiv[i] / 10000)) + "" + VoltageUnits[whichVUnit] + ";";
                }
            }

            //sets chartX & chartY to point value where mouse clicked on the chart
            GetGraphValueOfMouseClickedPoint(arg0.getTrigger().getX(), arg0.getTrigger().getY(), 0);



            if (MnuAddHorizontalMarker.isSelected() && (NoRangeMarkersOnChart < 2)) {

                NoRangeMarkersOnChart++;
                HorizontalMarkerLocation[NoRangeMarkersOnChart - 1] = chartY;
                HorizontalMarkerMouseClickLocation[NoRangeMarkersOnChart - 1][0] = arg0.getTrigger().getX();
                HorizontalMarkerMouseClickLocation[NoRangeMarkersOnChart - 1][1] = arg0.getTrigger().getY();
                HorizontalMarkerLabelArray[NoRangeMarkersOnChart - 1] = HorizontalMarkerLabel;
                AddHorizontalMarker(chartY, false, HorizontalMarkerLabel, NoRangeMarkersOnChart);
            }


            if (MnuAddVerticalMarker.isSelected() && NoDomainMarkersOnChart < 2) {
                NoDomainMarkersOnChart++;
                VerticalMarkerLocation[NoDomainMarkersOnChart - 1] = chartX;//indexes 0 an 1 used
                VerticalMarkerMouseClickLocation[NoDomainMarkersOnChart - 1][0] = arg0.getTrigger().getX();
                VerticalMarkerMouseClickLocation[NoDomainMarkersOnChart - 1][1] = arg0.getTrigger().getY();
                AddVerticalMarker(NoDomainMarkersOnChart, false);
            //AddVerticalMarker(chartX, false, NoDomainMarkersOnChart);

            }
        }

        if (MouseOverCh != 0) {
            offsetCh = MouseOverCh;
        /*switch (offsetCh) {
        case 1:
        //RadioButOffsetChan1.setSelected(true);
        //OffsetSelectActionperformed(1, true);
        break;
        case 2:
        //RadioButOffsetChan2.setSelected(true);
        //OffsetSelectActionperformed(2, true);
        break;
        case 3:
        //RadioButOffsetChan3.setSelected(true);
        //OffsetSelectActionperformed(3, true);
        break;
        case 4:
        //RadioButOffsetChan4.setSelected(true);
        //OffsetSelectActionperformed(4, true);
        break;
        default:
        break;
        }*/
        //ignoreMouseMove = true;
        }
    }

    public void updateMarkers(boolean UpdateHorizontal, boolean UpdateVertical) {
        int loop = 0;
        int whichVUnit = 0;
        String HorizontalMarkerLabel = "";

        if (UpdateVertical && NoDomainMarkersOnChart > 0) {

            if (JavaRunning || demoMode) {
                if (DeltaShowing) {
                    chartPlot.removeAnnotation(Delta);
                    chartPlot.removeAnnotation(DeltaText);
                    DeltaShowing = false;
                }
                for (loop = 0; loop < NoDomainMarkersOnChart; loop++) {//vertical markers
                    chartPlot.clearDomainMarkers(loop);//clear and redraw


                    GetGraphValueOfMouseClickedPoint(VerticalMarkerMouseClickLocation[loop][0], VerticalMarkerMouseClickLocation[loop][1], 0);
                    VerticalMarkerLocation[loop] = chartX;
                    AddVerticalMarker(loop + 1, false);
                }
            } else {
                if (DeltaShowing) {
                    chartPlot.removeAnnotation(Delta);
                    chartPlot.removeAnnotation(DeltaText);
                    DeltaShowing = false;
                }
                for (loop = 0; loop < NoDomainMarkersOnChart; loop++) {//vertical markers
                    chartPlot.clearDomainMarkers(loop);//clear and redraw

                    GetGraphValueOfMouseClickedPoint(VerticalMarkerMouseClickLocation[loop][0], VerticalMarkerMouseClickLocation[loop][1], 0);
                    VerticalMarkerLocation[loop] = chartX;
                    AddVerticalMarkerJavaNotRunning(loop + 1, false);
                }
            }
        }

        if (UpdateHorizontal && NoRangeMarkersOnChart > 0) {
            //chartPlot.clearRangeMarkers();//clear and redraw this one clear horizontal threshold marker at the start
            for (loop = 0; loop < NoRangeMarkersOnChart; loop++) {//horizontal markers
                HorizontalMarkerLabel = "";


                for (int i = 1; i <= numScopesFound; i++) {
                    if ((ChannelOn[i] || demoMode) && CheckTabTitles("Ch " + i)) {

                        GetGraphValueOfMouseClickedPoint(HorizontalMarkerMouseClickLocation[loop][0], HorizontalMarkerMouseClickLocation[loop][1], i - 1);
                        /*if (intVoltsperDivPointer[i] < 8) {
                        whichVUnit = 0;
                        //if (chartY-(functionOffset[i]*5*YDiv[i]/10000)){
                        //   whichVUnit = 1;
                        //}
                        } else {
                        whichVUnit = 1;
                        }*/
                        whichVUnit = 1;
                        HorizontalMarkerLabel = HorizontalMarkerLabel + "Ch" + i + "=" + df.format(chartY - (functionOffset[i] * 5 * YDiv[i] / 10000)) + "" + VoltageUnits[whichVUnit] + ";";
                        HorizontalMarkerLabelArray[loop] = HorizontalMarkerLabel;
                    }
                }

                GetGraphValueOfMouseClickedPoint(HorizontalMarkerMouseClickLocation[loop][0], HorizontalMarkerMouseClickLocation[loop][1], 0);
                AddHorizontalMarker(chartY, false, HorizontalMarkerLabelArray[loop], loop + 1);

            }
        }
    }

    @Override
    public void chartMouseMoved(ChartMouseEvent event) {

        float highlighter = getHighlightValue();
        final ChartEntity entity = event.getEntity();

        int mouseX = event.getTrigger().getX();
        int mouseY = event.getTrigger().getY();

        Point2D p = chartPanel.translateScreenToJava2D(new Point(mouseX, mouseY));
        Rectangle2D plotArea = chartPanel.getScreenDataArea();
        ValueAxis domainAxis = chartPlot.getDomainAxis();
        RectangleEdge domainAxisEdge = chartPlot.getDomainAxisEdge();
        ValueAxis rangeAxis = chartPlot.getRangeAxis();
        RectangleEdge rangeAxisEdge = chartPlot.getRangeAxisEdge();
        this.repaint();

        double chartXi = domainAxis.java2DToValue(p.getX(), plotArea, domainAxisEdge);
        double chartYi = rangeAxis.java2DToValue(p.getY(), plotArea, rangeAxisEdge);

        chartX = chartXi;
        chartY = chartYi;

        int gap = 10;

        if ((event.getTrigger().getX() < VerticalMarkerMouseClickLocation[0][0] + gap) && (event.getTrigger().getX() > VerticalMarkerMouseClickLocation[0][0] - gap) && (NoDomainMarkersOnChart > 0)) {
            chartPanel.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.E_RESIZE_CURSOR));
        } else if ((event.getTrigger().getX() < VerticalMarkerMouseClickLocation[1][0] + gap) && (event.getTrigger().getX() > VerticalMarkerMouseClickLocation[1][0] - gap) && (NoDomainMarkersOnChart > 1)) {
            chartPanel.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.W_RESIZE_CURSOR));
        } else if ((event.getTrigger().getY() < HorizontalMarkerMouseClickLocation[0][1] + gap) && (event.getTrigger().getY() > HorizontalMarkerMouseClickLocation[0][1] - gap) && (NoRangeMarkersOnChart > 0)) {
            chartPanel.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.N_RESIZE_CURSOR));
        } else if ((event.getTrigger().getY() < HorizontalMarkerMouseClickLocation[1][1] + gap) && (event.getTrigger().getY() > HorizontalMarkerMouseClickLocation[1][1] - gap) && (NoRangeMarkersOnChart > 1)) {
            chartPanel.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.S_RESIZE_CURSOR));
        } else if (entity instanceof XYItemEntity) {//XYPlot){//ChartEntity){//XYDataset){//XYItemEntity) {

            //if (ignoreMouseMove) {
            //    return;
            //}
            /*
            chartPanel.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));

            XYItemEntity ent = (XYItemEntity) entity;

            renderer.setSeriesStroke(0, new BasicStroke(FunctionStroke));
            renderer2.setSeriesStroke(0, new BasicStroke(FunctionStroke));
            renderer3.setSeriesStroke(0, new BasicStroke(FunctionStroke));
            renderer4.setSeriesStroke(0, new BasicStroke(FunctionStroke));

            //rendererFFT ??? i don't think I need to do mouse over for fft graphs

            if (ent.getDataset().equals(datasetCh1)) {
            renderer.setSeriesStroke(0, new BasicStroke(FunctionStroke * highlighter));
            MouseOverCh = 1;
            } else if (ent.getDataset().equals(datasetCh2)) {
            renderer2.setSeriesStroke(0, new BasicStroke(FunctionStroke * highlighter));
            MouseOverCh = 2;
            } else if (ent.getDataset().equals(datasetCh3)) {
            renderer3.setSeriesStroke(0, new BasicStroke(FunctionStroke * highlighter));
            MouseOverCh = 3;
            } else if (ent.getDataset().equals(datasetCh4)) {
            renderer4.setSeriesStroke(0, new BasicStroke(FunctionStroke * highlighter));
            MouseOverCh = 4;
            }
             */
        } else {

            chartPanel.setCursor(null);
            //if (ignoreMouseMove) {
            //    return;
            // }
            renderer.setSeriesStroke(0, new BasicStroke(FunctionStroke));
            renderer2.setSeriesStroke(0, new BasicStroke(FunctionStroke));
            renderer3.setSeriesStroke(0, new BasicStroke(FunctionStroke));
            renderer4.setSeriesStroke(0, new BasicStroke(FunctionStroke));

            //rendererFFT ??? i don't think I need to do mouse over for fft graphs
            MouseOverCh = 0;
        }


    }

    private static void writeChartAsPDF(OutputStream out, JFreeChart chart, int width, int height, FontMapper mapper) {
        // Save graph in pdf format.

        Rectangle pagesize = new Rectangle(width, height);
        Document document = new Document(pagesize, 50, 50, 50, 50);
        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);
            document.addTitle("This is my title");
            document.addAuthor("JFreeChart");
            document.addSubject("Demonstration");
            document.open();
            PdfContentByte cb = writer.getDirectContent();

            PdfTemplate tp = cb.createTemplate(width, height);

            Graphics2D g2 = tp.createGraphics(width, height, mapper);
            Rectangle2D r2D = new Rectangle2D.Double(0, 0, width, height);
            chart.draw(g2, r2D);
            g2.dispose();
            cb.addTemplate(tp, 0, 0);

        /*    BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
        cb.beginText();
        cb.setFontAndSize(bf, 12);
        cb.setTextMatrix(0, height-12);
        cb.showText("Can you see me?");
        cb.endText();


        cb.beginText();
        cb.setFontAndSize(bf, 12);
        cb.setTextMatrix(0, height-24);
        cb.showText("2 see me?");
        cb.endText();

        cb.stroke();
         */

        // } catch (IOException ex) {
        //     Logger.getLogger(USBscope50_Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DocumentException de) {
            System.err.println(de.getMessage());
        }
        document.close();
    }

    //public static JFreeChart createChart(XYDataset dataset) {//I think it is better if not static?!
    public JFreeChart createCombinedChart() {

        //create subplot one which I refer to as chartPlot
        axisY[1] = new NumberAxis();
        axisY[2] = new NumberAxis();
        axisY[3] = new NumberAxis();
        axisY[4] = new NumberAxis();
        axisY[1].setTickLabelsVisible(false);
        axisY[2].setTickLabelsVisible(false);
        axisY[3].setTickLabelsVisible(false);
        axisY[4].setTickLabelsVisible(false);

        datasetCh1 = createDataset(1);//1 is for channel 1
        chartPlot = new XYPlot(datasetCh1, null, axisY[1], renderer);//range 1
        chartPlot.setDomainPannable(true);
        chartPlot.setRangePannable(true);
        //chartPlot.setDomainPannable(false);
        //chartPlot.setRangePannable(false);

        //add secondary axis
        datasetCh2 = createDataset(2);
        chartPlot.setDataset(1, datasetCh2);
        chartPlot.setRangeAxis(1, axisY[2]);
        chartPlot.setRangeAxisLocation(1, AxisLocation.BOTTOM_OR_LEFT);
        chartPlot.setRenderer(1, renderer2);
        chartPlot.mapDatasetToRangeAxis(1, 1);

        //add the third axis
        datasetCh3 = createDataset(3);
        chartPlot.setDataset(2, datasetCh3);
        chartPlot.setRangeAxis(2, axisY[3]);
        chartPlot.setRangeAxisLocation(2, AxisLocation.BOTTOM_OR_LEFT);
        chartPlot.setRenderer(2, renderer3);
        chartPlot.mapDatasetToRangeAxis(2, 2);

        //add the fourth axis for channel 4
        datasetCh4 = createDataset(4);
        chartPlot.setDataset(3, datasetCh4);
        chartPlot.setRangeAxis(3, axisY[4]);
        chartPlot.setRangeAxisLocation(3, AxisLocation.BOTTOM_OR_LEFT);
        chartPlot.setRenderer(3, renderer4);
        chartPlot.mapDatasetToRangeAxis(3, 3);

        plot = new CombinedDomainXYPlot(axisX);



        renderer.setSeriesPaint(0, ColorCh1);//series numbering starts form 0, hence series 0 == channel 1
        renderer.setSeriesStroke(0, new BasicStroke(FunctionStroke));

        renderer2.setSeriesPaint(0, ColorCh2);
        renderer2.setSeriesStroke(0, new BasicStroke(FunctionStroke));

        renderer3.setSeriesPaint(0, ColorCh3);
        renderer3.setSeriesStroke(0, new BasicStroke(FunctionStroke));

        renderer4.setSeriesPaint(0, ColorCh4);
        renderer4.setSeriesStroke(0, new BasicStroke(FunctionStroke));

        plot.setGap(10.0);//gap between function and FFT graph
        plot.add(chartPlot, 1);

        axisY_FFT[1] = new NumberAxis();
        axisY_FFT[2] = new NumberAxis();
        axisY_FFT[3] = new NumberAxis();
        axisY_FFT[4] = new NumberAxis();
        axisY_FFT[1].setTickLabelsVisible(false);//ANA UNCOMMENT THIS??? ***
        axisY_FFT[2].setTickLabelsVisible(false);
        axisY_FFT[3].setTickLabelsVisible(false);
        axisY_FFT[4].setTickLabelsVisible(false);

        Range FFTRange = new Range(0.0, 10.0);//At this point YDiv is 0 ;//5*YDiv[1]);
        axisY_FFT[1].setRange(FFTRange, true, true);
        axisY_FFT[2].setRange(FFTRange, true, true);
        axisY_FFT[3].setRange(FFTRange, true, true);
        axisY_FFT[4].setRange(FFTRange, true, true);

        datasetCh1_FFT = createDataset_FFT(1);
        FFTplot = new XYPlot(datasetCh1_FFT, null, axisY_FFT[1], renderer_FFT);//range 1
        FFTplot.setDomainPannable(true);
        FFTplot.setRangePannable(true);


        //add secondary axis
        datasetCh2_FFT = createDataset_FFT(2);
        FFTplot.setDataset(1, datasetCh2_FFT);
        FFTplot.setRangeAxis(1, axisY_FFT[2]);
        FFTplot.setRangeAxisLocation(1, AxisLocation.BOTTOM_OR_LEFT);
        FFTplot.setRenderer(1, renderer2_FFT);
        FFTplot.mapDatasetToRangeAxis(1, 1);

        //add the third axis
        datasetCh3_FFT = createDataset_FFT(3);
        FFTplot.setDataset(2, datasetCh3_FFT);
        FFTplot.setRangeAxis(2, axisY_FFT[3]);
        FFTplot.setRangeAxisLocation(2, AxisLocation.BOTTOM_OR_LEFT);
        FFTplot.setRenderer(2, renderer3_FFT);
        FFTplot.mapDatasetToRangeAxis(2, 2);

        //add the fourth axis for channel 4
        datasetCh4_FFT = createDataset_FFT(4);
        FFTplot.setDataset(3, datasetCh4_FFT);
        FFTplot.setRangeAxis(3, axisY_FFT[4]);
        FFTplot.setRangeAxisLocation(3, AxisLocation.BOTTOM_OR_LEFT);
        FFTplot.setRenderer(3, renderer4_FFT);
        FFTplot.mapDatasetToRangeAxis(3, 3);

        renderer_FFT.setSeriesPaint(0, ColorCh1);//series numbering starts form 0, hence series 0 == channel 1
        renderer_FFT.setSeriesStroke(0, new BasicStroke(FunctionStroke));


        renderer2_FFT.setSeriesPaint(0, ColorCh2);
        renderer2_FFT.setSeriesStroke(0, new BasicStroke(FunctionStroke));

        renderer3_FFT.setSeriesPaint(0, ColorCh3);
        renderer3_FFT.setSeriesStroke(0, new BasicStroke(FunctionStroke));

        renderer4_FFT.setSeriesPaint(0, ColorCh4);
        renderer4_FFT.setSeriesStroke(0, new BasicStroke(FunctionStroke));


        renderer_FFT.setSeriesVisible(0, false);
        renderer2_FFT.setSeriesVisible(0, false);
        renderer3_FFT.setSeriesVisible(0, false);
        renderer4_FFT.setSeriesVisible(0, false);


        JFreeChart chart = new JFreeChart("", JFreeChart.DEFAULT_TITLE_FONT, plot, false);//last property false is to show symbols below the graph

        return chart;
    }

    private void AddVerticalMarker(int position, boolean Highlighted) {


        int indexTimeUnits;
        String TimeMarker;
        double atTime;
        double adjust = 1;
        double PointsPerDiv = GetPointsPerDiv(intTimeBaseArrayPointer);
        //double VerticalAdjustRunning = lastPointsPerDiv/PointsPerDiv;

        //get chartX
        GetGraphValueOfMouseClickedPoint(VerticalMarkerMouseClickLocation[position - 1][0], VerticalMarkerMouseClickLocation[position - 1][1], 0);
        Marker domainMarker = new ValueMarker(chartX);
        domainMarker.setPaint(Color.black);

        if (intTimeBaseArrayPointer < 7) {
            indexTimeUnits = 0;
            adjust = 0.001;
        } else if (intTimeBaseArrayPointer < 16) {
            indexTimeUnits = 1;
        } else if (intTimeBaseArrayPointer < 25) {
            indexTimeUnits = 2;
            adjust = 1000;
        } else {
            indexTimeUnits = 3;
            adjust = 1000000;
        }

        if (demoMode && !JavaRunning) {
            if (lastPointsPerDiv == 40) {//this is as i have moved point by 20 as 0 is in the middle of the x axis and to get div lines i had to move by 20
                chartX = chartX - 20;
            }
            adjust = adjust * VerticalMarkerAdjust;



        } else if (intTimeBaseArrayPointer < 5 && USBscope50_Main.demoMode) {
            //adjust = adjust;//Ris mode already adjuster
        } else if (intTimeBaseArrayPointer < 8 && USBscope50_Main.demoMode) {
        } else if (JavaRunning) {
            adjust = adjust / (100 / PointsPerDiv);
        }


        atTime = (chartX + 1500) * 10 / adjust * (Double.parseDouble(TimeBaseSettings[intTimeBaseArrayPointer][3]));

        verticalMarkersAtTime[position] = atTime;
        verticalMarkersTimeUnitIndex[position] = indexTimeUnits;

        if ((atTime > 1000) && (indexTimeUnits < TimeUnits.length - 1)) {
            indexTimeUnits++;
            atTime = atTime / 1000;
        }

        TimeMarker = df.format(atTime);//(chartX + 1500)*10*(Double.parseDouble(TimeBaseSettings[intTimeBaseArrayPointer][3])));
        TimeUnit = TimeUnits[indexTimeUnits];
        domainMarker.setLabel("@" + TimeMarker + TimeUnit);


        //if (chartX > ((PointsPerDiv * 30) * 3 / 4) - 1500) {
        if ((chartX + 1500) > ((USBscope50_Main.axisX.getUpperBound() + 1500) * 3 / 4)) {
            domainMarker.setLabelAnchor(RectangleAnchor.TOP);
            domainMarker.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
            if (position > 1 && (Math.abs(VerticalMarkerLocation[1] - VerticalMarkerLocation[0]) < AllowedDistanceBetweenMarkers)) {
                //domainMarker.setLabelAnchor(RectangleAnchor.BOTTOM);//use offset inste3ad of bottom
                domainMarker.setLabelOffset(rectOffset);
            }
        } else {
            domainMarker.setLabelAnchor(RectangleAnchor.TOP);
            domainMarker.setLabelTextAnchor(TextAnchor.TOP_LEFT);
            if (position > 1 && (Math.abs(VerticalMarkerLocation[1] - VerticalMarkerLocation[0]) < AllowedDistanceBetweenMarkers)) {
                //System.out.println("The time difference between two vertical markers is: " + (Math.abs(VerticalMarkerLocation[1] - VerticalMarkerLocation[0])));

                //domainMarker.setLabelAnchor(RectangleAnchor.BOTTOM);//use offset instead of bottom
                domainMarker.setLabelTextAnchor(TextAnchor.BOTTOM_LEFT);
                domainMarker.setLabelOffset(rectOffset);
            }
        }

        chartPlot.addDomainMarker(position - 1, domainMarker, Layer.FOREGROUND);

        double dt = (Math.abs(verticalMarkersAtTime[2] - verticalMarkersAtTime[1]));
        int dtUnit = verticalMarkersTimeUnitIndex[position];
        if (dt > 1000 && dtUnit < 3) {
            dt = dt / 1000;
            dtUnit++;
        }
        if (position == 2) {
            if (DeltaShowing) {
                chartPlot.removeAnnotation(Delta);
                chartPlot.removeAnnotation(DeltaText);
            }

            int mult = 1;
            if (Probe[1] == 2) {
                mult = 10;
            }
            double smallerX = (VerticalMarkerLocation[0] < VerticalMarkerLocation[1] ? VerticalMarkerLocation[0] : VerticalMarkerLocation[1]);
            Font font = new Font("SansSerif", Font.PLAIN, 9);
            Delta = new XYLineAnnotation(VerticalMarkerLocation[0], YDiv[1] * mult * 4.0, VerticalMarkerLocation[1], YDiv[1] * mult * 4.0);
            DeltaText = new XYTextAnnotation("dt=" + df.format(dt) + TimeUnits[dtUnit], Math.abs(VerticalMarkerLocation[0] - VerticalMarkerLocation[1]) / 2 + smallerX, (YDiv[1] * mult * 4.0) + (YDiv[1] * mult / 3));
            DeltaText.setFont(font);
            chartPlot.addAnnotation(Delta);
            chartPlot.addAnnotation(DeltaText);
            DeltaShowing = true;

        //System.out.println("2:-dt="+df.format(dt)+TimeUnits[dtUnit]);
        }

    }

    private void AddVerticalMarkerJavaNotRunning(int position, boolean Highlighted) {


        int indexTimeUnits;
        String TimeMarker;
        double atTime;
        double adjust = 1;
        double PointsPerDiv = GetPointsPerDiv(intTimeBaseArrayPointer);
        //double VerticalAdjustRunning = lastPointsPerDiv/PointsPerDiv;

        //get chartX
        GetGraphValueOfMouseClickedPoint(VerticalMarkerMouseClickLocation[position - 1][0], VerticalMarkerMouseClickLocation[position - 1][1], 0);



        Marker domainMarker = new ValueMarker(chartX);

        domainMarker.setPaint(Color.black);

        if (intTimeBaseArrayPointer < 7) {
            indexTimeUnits = 0;
            adjust = 0.001;
        } else if (intTimeBaseArrayPointer < 16) {
            indexTimeUnits = 1;
        } else if (intTimeBaseArrayPointer < 25) {
            indexTimeUnits = 2;
            adjust = 1000;
        } else {
            indexTimeUnits = 3;
            adjust = 1000000;
        }




        if (JavaRunning && !demoMode) {
            adjust = adjust / (100 / PointsPerDiv);
        } else {


            if (lastPointsPerDiv == 40) {//this is as i have moved point by 20 as 0 is in the middle of the x axis and to get div lines i had to move by 20
                adjust = adjust * VerticalMarkerAdjust;
                chartX = chartX - 20;

            } else {
                adjust = adjust * VerticalMarkerAdjust;
            }
        }

        atTime = (chartX + 1500) * 10 / adjust * (Double.parseDouble(TimeBaseSettings[intTimeBaseArrayPointer][3]));
        verticalMarkersAtTime[position] = atTime;
        verticalMarkersTimeUnitIndex[position] = indexTimeUnits;
        if ((atTime > 1000) && (indexTimeUnits < TimeUnits.length - 1)) {
            indexTimeUnits++;
            atTime = atTime / 1000;
        }

        TimeMarker = df.format(atTime);//(chartX + 1500)*10*(Double.parseDouble(TimeBaseSettings[intTimeBaseArrayPointer][3])));
        TimeUnit = TimeUnits[indexTimeUnits];
        domainMarker.setLabel("@" + TimeMarker + TimeUnit);

        if ((chartX + 1500) > ((axisX.getUpperBound() + 1500) * 3 / 4)) {//((PointsPerDiv * 30) * 3 / 4) - 1500) {
            domainMarker.setLabelAnchor(RectangleAnchor.TOP);
            domainMarker.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
            if (position > 1 && (Math.abs(VerticalMarkerLocation[1] - VerticalMarkerLocation[0]) < AllowedDistanceBetweenMarkers)) {
                //domainMarker.setLabelAnchor(RectangleAnchor.BOTTOM);//use offset inste3ad of bottom
                domainMarker.setLabelTextAnchor(TextAnchor.BOTTOM_RIGHT);
                domainMarker.setLabelOffset(rectOffset);
            }
        } else {
            domainMarker.setLabelAnchor(RectangleAnchor.TOP);
            domainMarker.setLabelTextAnchor(TextAnchor.TOP_LEFT);
            if (position > 1 && (Math.abs(VerticalMarkerLocation[1] - VerticalMarkerLocation[0]) < AllowedDistanceBetweenMarkers)) {
                //System.out.println("The time difference between two vertical markers is: " + (Math.abs(VerticalMarkerLocation[1] - VerticalMarkerLocation[0])));

                //domainMarker.setLabelAnchor(RectangleAnchor.BOTTOM);//use offset inste3ad of bottom
                domainMarker.setLabelTextAnchor(TextAnchor.BOTTOM_LEFT);
                domainMarker.setLabelOffset(rectOffset);
            }
        }

        chartPlot.addDomainMarker(position - 1, domainMarker, Layer.FOREGROUND);

        double dt = (Math.abs(verticalMarkersAtTime[2] - verticalMarkersAtTime[1]));
        int dtUnit = verticalMarkersTimeUnitIndex[position];
        if (dt > 1000 && dtUnit < 3) {
            dt = dt / 1000;
            dtUnit++;
        }
        if (position == 2) {
            if (DeltaShowing) {
                chartPlot.removeAnnotation(Delta);
                chartPlot.removeAnnotation(DeltaText);
            }

            int mult = 1;
            if (Probe[1] == 2) {
                mult = 10;
            }
            double smallerX = (VerticalMarkerLocation[0] < VerticalMarkerLocation[1] ? VerticalMarkerLocation[0] : VerticalMarkerLocation[1]);

            Font font = new Font("SansSerif", Font.PLAIN, 9);
            Delta = new XYLineAnnotation(VerticalMarkerLocation[0], YDiv[1] * mult * 4.0, VerticalMarkerLocation[1], YDiv[1] * mult * 4.0);
            DeltaText = new XYTextAnnotation("dt=" + df.format(dt) + TimeUnits[dtUnit], Math.abs(VerticalMarkerLocation[0] - VerticalMarkerLocation[1]) / 2 + smallerX, (YDiv[1] * mult * 4.0) + (YDiv[1] * mult / 3));
            DeltaText.setFont(font);
            chartPlot.addAnnotation(Delta);
            chartPlot.addAnnotation(DeltaText);
            DeltaShowing = true;
        //System.out.println("1:-dt="+ df.format(dt)+TimeUnits[dtUnit]);
        }


    }

    private void AddHorizontalMarker(double chartY, boolean Highlighted, String label, int position) {//position is 1 or 2
        int indexVoltsUnits;
        String VoltsMarker;
        double atVoltage;

        Marker rangeMarker = new ValueMarker(chartY);
        rangeMarker.setPaint(Color.black);

        if (intVoltsperDivPointer[1] < 8) {
            indexVoltsUnits = 0;
        } else {
            indexVoltsUnits = 1;
        }
        atVoltage = chartY;

        VoltsMarker = df.format(atVoltage);//(chartX + 1500)*10*(Double.parseDouble(TimeBaseSettings[intTimeBaseArrayPointer][3])));
        VoltageUnit = VoltageUnits[indexVoltsUnits];


        if (Highlighted) {
            rangeMarker.setLabelFont(new Font("SansSerif", Font.BOLD, 12));//default is Font.Plain, size 9
        }
        rangeMarker.setLabel(label);

        rangeMarker.setLabelPaint(Color.black);


        //what if at the top then I write it below the line
        //and what if close then the other one is writen above
        if (chartY > (axisY[1].getUpperBound() / 5 * 4)) {
            rangeMarker.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
            rangeMarker.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
            rangeMarker.setLabelOffset(rectOffset_horiz_down);
            if ((position > 1) && (Math.abs(HorizontalMarkerLocation[1] - HorizontalMarkerLocation[0]) < (axisY[1].getUpperBound() / 5))) {
                //rangeMarker.setLabelAnchor(RectangleAnchor.BOTTOM_LEFT);
                //rangeMarker.setLabelTextAnchor(TextAnchor.TOP_LEFT);
                //rangeMarker.setLabelOffset(rectOffset_horiz);
                rangeMarker.setLabelOffset(rectOffset_horiz_down_left);
            }
        } else {
            rangeMarker.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
            rangeMarker.setLabelTextAnchor(TextAnchor.BOTTOM_RIGHT);
            if ((position > 1) && (Math.abs(HorizontalMarkerLocation[1] - HorizontalMarkerLocation[0]) < (axisY[1].getUpperBound() / 5))) {
                //rangeMarker.setLabelAnchor(RectangleAnchor.BOTTOM_LEFT);
                //rangeMarker.setLabelTextAnchor(TextAnchor.TOP_LEFT);
                rangeMarker.setLabelOffset(rectOffset_horiz);
            }
        }

        chartPlot.addRangeMarker(rangeMarker);
    }

    private void ChartOptionsCancelActionPerformed() {
        chartPlot.setRangeGridlinePaint(RangeGridColor);
        chartPlot.setDomainGridlinePaint(DomainGridColor);

        FFTplot.setRangeGridlinePaint(RangeGridColor);
        FFTplot.setDomainGridlinePaint(DomainGridColor);

        chartPlot.setRangeGridlineStroke(new BasicStroke(RangeStroke));
        chartPlot.setDomainGridlineStroke(new BasicStroke(DomainStroke));

        FFTplot.setRangeGridlineStroke(new BasicStroke(RangeStroke));
        FFTplot.setDomainGridlineStroke(new BasicStroke(DomainStroke));

        renderer.setSeriesStroke(0, new BasicStroke(FunctionStroke));
        renderer2.setSeriesStroke(0, new BasicStroke(FunctionStroke));
        renderer3.setSeriesStroke(0, new BasicStroke(FunctionStroke));
        renderer4.setSeriesStroke(0, new BasicStroke(FunctionStroke));
        renderer_FFT.setSeriesStroke(0, new BasicStroke(FunctionStroke));

        chartPlot.setBackgroundPaint(ChartBackgroundColor);//(new Color(230, 230, 230)); //(Color.DARK_GRAY);

        FFTplot.setBackgroundPaint(ChartBackgroundColor);

        ChartOptionsDialog.setVisible(false);
    }

    private void ChartOptionsOKActionPerformed() {
        RangeGridColor = newRangeGridColor;
        DomainGridColor = newDomainGridColor;
        RangeStroke = newRangeStroke;
        DomainStroke = newDomainStroke;

        FunctionStroke = newFunctionStroke;

        ChartBackgroundColor = newChartBackgroundColor;

        ChartOptionsCancelActionPerformed();
    }

    private String CheckFileNameforTxt(String FileName, String FileExtensionFilter) {

        String CheckedFileName = "";
        String FileExtension = ".txt";


        if (FileExtensionFilter.toUpperCase().contains("TXT")) {
            FileExtension = ".txt";
        }

        if (FileName.contains(".")) {
            int loc = FileName.indexOf(".");
            CheckedFileName = FileName.substring(0, loc) + FileExtension;
            return CheckedFileName;
        } else {
            return FileName + FileExtension;
        }
    }

    private boolean CheckScopesStackStatus(int NumScopesToCheck) {

        boolean returnVal = (NumScopesToCheck == numScopesFound);
        if (!demoMode) {
            for (int i = 1; i <= numScopesFound; i++) {
                if (returnVal || (i != MasterChannel)) {
                    if ((t.ScopeStackStatus(i)) != returnVal) {
                        return !returnVal;
                    }
                }
            }
        }
        return returnVal;
    }

    private int CheckTabIndex(String TabTitle) {
        // Returns tab index if tab with the string TabTitle is already displayed, otherwise returns -1
        for (int i = 0; i < RightSplitPane.getTabCount(); i++) {//tab indexes start from 0
            if (RightSplitPane.getTitleAt(i).contains(TabTitle)) {
                return i;
            }
        }
        return -1;
    }

    public static boolean CheckTabTitles(String TabTitle) {
        // Returns true if Tab already visible on the screen, otherwise return false.
        for (int i = 0; i < RightSplitPane.getTabCount(); i++) {//tab indexes start from 0
            if (RightSplitPane.getTitleAt(i).contains(TabTitle)) {
                return true;
            }
        }
        return false;
    }

    private void ClearDomainMarkers() {
        // clears vertical markers
        chartPlot.clearDomainMarkers();
        NoDomainMarkersOnChart = 0;
        if (DeltaShowing) {
            chartPlot.removeAnnotation(Delta);
            chartPlot.removeAnnotation(DeltaText);
            DeltaShowing = false;
        }
    }

    private void ClearRangeMarkers() {
        // clears horizontal markers
        //chartPlot.clearRangeMarkers(0);
        //chartPlot.clearRangeMarkers(1);
        //chartPlot.clearRangeMarkers(HorizRangeMarkerID[0]);
        //chartPlot.clearRangeMarkers(HorizRangeMarkerID[1]);

        chartPlot.clearRangeMarkers();
        NoRangeMarkersOnChart = 0;
        SliderThreshold.setValue(SliderThreshold.getValue() + 1);
        SliderThreshold.setValue(SliderThreshold.getValue() - 1);

    /*if (ThresholdMarkerOn){
    Marker rangeMarker = new ValueMarker(SliderThreshold.getValue() * 5 * YDiv[1] / 100.000);

    rangeMarker.setPaint(Color.DARK_GRAY);
    rangeMarker.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, new float[]{10.0f, 10.0f}, 0.0f));

    chartPlot.addRangeMarker(rangeMarker);
    }*/
    }

    private void ClearRunFlags() {
        //OffsetSlider.setValue(0);//used to reset offset slider before setoffset  implementation
        //LastRunChannelOffset = ChannelOffset;
        ScopeReadAtSlider = TriggerPositionSlider.getValue();
        OverallZoomRatio = 1;
    }

    private void CouplingActionPerformed(int intChannel, int intDc_option, int intgnd_option, boolean ApplyToAll) {
        if (ApplyToAll) {
            OpACCouplingCh1.setSelected((intDc_option != 1));
            OpDCCouplingCh1.setSelected((intDc_option != 0));
            OpGNDCh1.setSelected((intgnd_option != 0));

            OpACCouplingCh2.setSelected((intDc_option != 1));
            OpDCCouplingCh2.setSelected((intDc_option != 0));
            OpGNDCh2.setSelected((intgnd_option != 0));

            OpACCouplingCh3.setSelected((intDc_option != 1));
            OpDCCouplingCh3.setSelected((intDc_option != 0));
            OpGNDCh3.setSelected((intgnd_option != 0));

            OpACCouplingCh4.setSelected((intDc_option != 1));
            OpDCCouplingCh4.setSelected((intDc_option != 0));
            OpGNDCh4.setSelected((intgnd_option != 0));

            for (int i = 1; i <= 4; i++) {
                Dc_option[i] = intDc_option;
                gnd_option[i] = intgnd_option;
                t.USBscope50Drvr_SetUpFrontEnd(i, VoltageGain[i], Dc_option[i], gnd_option[i], UseRISmode);
            }

        } else {
            Dc_option[intChannel] = intDc_option;
            gnd_option[intChannel] = intgnd_option;
            t.USBscope50Drvr_SetUpFrontEnd(intChannel, VoltageGain[intChannel], Dc_option[intChannel], gnd_option[intChannel], UseRISmode);
        }
    }

    private void DisplayDemoTutorial() {
        //if (TutorialSaveGraphDisplayed) {
        //   return;
        //}
        new TutorialDemo().setVisible(true);
        TutorialSaveGraphDisplayed = true;

    }

    private void DisplayTutorial() {
        //display a pop up window with 10 steps how to use the software

        //if (TutorialDemoDisplayed == true) {
        //    return;
        //}
        new Tutorial().setVisible(true);
        TutorialDemoDisplayed = true;

    }

    /*    private void DisplayZoomPlot(double newLowerBound, double dbRatio) {
    int i;
    try {
    if (clone_loaded[1] == false) {//clone_loaded[1] for channel 1
    if (seriesCh1.getItemCount() == 0) {
    for (i = 0; i < (maxGraphPointCount - 2); i++) {
    series_cloneCh1.add(i, 0);
    }
    } else {
    series_cloneCh1 = (XYSeries) seriesCh1.createCopy(0, maxGraphPointCount - 2); ////does this need to be -1500
    }
    clone_loaded[1] = true;
    }

    if (clone_loaded[2] == false) {
    if (seriesCh2.getItemCount() == 0) {
    for (i = 0; i < (maxGraphPointCount - 2); i++) {
    series_cloneCh2.add(i, 0);
    }
    } else {
    series_cloneCh2 = (XYSeries) seriesCh2.createCopy(0, maxGraphPointCount - 2); ////does this need to be -1500
    }
    clone_loaded[2] = true;
    }

    if (!clone_loaded[3]) {
    if (seriesCh3.getItemCount() == 0) {
    series_cloneCh3 = (XYSeries) series_cloneCh1.clone();//otherwise crashed; don't know hoe to initialize clone
    for (i = 0; i < (maxGraphPointCount - 2); i++) {
    series_cloneCh3.add(i, 0);
    }
    } else {
    series_cloneCh3 = (XYSeries) seriesCh3.createCopy(0, maxGraphPointCount - 2); ////does this need to be -1500
    }
    clone_loaded[3] = true;
    }

    if (!clone_loaded[4]) {
    if (seriesCh4.getItemCount() == 0) {
    series_cloneCh4 = (XYSeries) series_cloneCh1.clone();//otherwise crashed; don't know hoe to initialize clone
    for (i = 0; i < (maxGraphPointCount - 2); i++) {
    series_cloneCh4.add(i, 0);
    }
    } else {
    series_cloneCh4 = (XYSeries) seriesCh4.createCopy(0, maxGraphPointCount - 2); ////does this need to be -1500
    }
    clone_loaded[4] = true;
    }
    } catch (CloneNotSupportedException ex) {
    Logger.getLogger(USBscope50_Main.class.getName()).log(Level.SEVERE, null, ex);
    }

    seriesCh1.clear();//this was clearing my demo functions
    seriesCh2.clear();
    seriesCh3.clear();
    seriesCh4.clear();

    for (i = 0; i < (maxGraphPointCount - 2); i++) {
    USBscope50_Main.seriesCh1.add(newLowerBound + (i * dbRatio), series_cloneCh1.getY(i), false);
    USBscope50_Main.seriesCh2.add(newLowerBound + (i * dbRatio), series_cloneCh2.getY(i), false);
    USBscope50_Main.seriesCh3.add(newLowerBound + (i * dbRatio), series_cloneCh3.getY(i), false);
    USBscope50_Main.seriesCh4.add(newLowerBound + (i * dbRatio), series_cloneCh4.getY(i), false);
    }
    USBscope50_Main.seriesCh1.add(newLowerBound + (i * dbRatio), series_cloneCh1.getY(i), true);
    USBscope50_Main.seriesCh2.add(newLowerBound + (i * dbRatio), series_cloneCh2.getY(i), true);
    USBscope50_Main.seriesCh3.add(newLowerBound + (i * dbRatio), series_cloneCh3.getY(i), true);
    USBscope50_Main.seriesCh4.add(newLowerBound + (i * dbRatio), series_cloneCh4.getY(i), true);

    //axisX.setRange(-maxGraphPointCount/2,maxGraphPointCount/2);
    }*/
    private void DomainSpinnerActionPerformed() {
        if (chkModifyBoth.isSelected()) {
            RangeSpinner.setValue(DomainSpinner.getValue());
        }
        newDomainStroke = Float.valueOf(DomainSpinner.getValue().toString());
        chartPlot.setDomainGridlineStroke(new BasicStroke(newDomainStroke));
        FFTplot.setDomainGridlineStroke(new BasicStroke(newDomainStroke));
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
        String[] USB_SerialArray = null; //list of all files from sysDevicesLocation e.g. ttyUSB0, ttyUSB1, etc.

        String[] USB_Serialcp2101Array = null; //list of all files in usb/drivers/cp2101 folder e.g. wanted 1-3:1.0,
        //as well as unwanted module, bind, unbind

        String[] USB_Serialcp2101Arraychecked = null; //filtered USB_Serialcp2101Array;only wanted files e.g. 1-3

        int noUSB_SerialDevices = 0; //number of ttyUSB folders in sysDevicesLocation, i.e. no of usb-serial devices

        int noCP2101Devices = 0; //number of virtual device folders in sysDriversLocation (format N-N:N.N)

        int index; //temp looping variable
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
            USB_Serialcp2101Array = sysDriverFolder.list(); //get all the files

            USB_Serialcp2101Arraychecked = new String[USB_Serialcp2101Array.length];
            for (index = 0; index < USB_Serialcp2101Array.length; index++) {
                String USB_SerialCP2101Folder = sysDriversLocation + "/" + USB_Serialcp2101Array[index];
                if (USB_Serialcp2101Array[index].contains(":") && (new File(USB_SerialCP2101Folder).isDirectory())) {
                    //check if this is the one we want
                    noCP2101Devices++;
                    USB_Serialcp2101Arraychecked[noCP2101Devices] = USB_Serialcp2101Array[index];
                }
            }
        }

        String[][] USBTandMDevices = new String[2][noCP2101Devices + 1]; //lets organize all the info we have collected up to now

        for (index = 1; index <= noCP2101Devices; index++) {
            USBTandMDevices[0][index] = USB_Serialcp2101Arraychecked[index].substring(0, USB_Serialcp2101Arraychecked[index].indexOf(":"));
            USBTandMDevices[1][index] = getTTYIndex(sysDriversLocation, USB_Serialcp2101Arraychecked[index]);
        //USBTandMDevices[0] will store info like 1-3
        //USBTandMDevices[1] will store info like port number like ttyUSB0
        }


        gUSBTandMDevices = new String[3][noCP2101Devices + 1]; //lets have info availabe globally

        for (int i = 0; i < 2; i++) {
            for (int j = 1; j <= noCP2101Devices; j++) {
                gUSBTandMDevices[i][j] = USBTandMDevices[i][j];
            }
        }

        GetidProduct(); //add the third parameter add product string such as USBpule100, or USBscope50

        return noCP2101Devices; //number of usb-serial devices with silab chip

    }

    private void FunctionSpinnerActionPerformed() {

        newFunctionStroke = Float.valueOf(FunctionSpinner.getValue().toString());

        renderer.setSeriesStroke(0, new BasicStroke(newFunctionStroke));
        renderer2.setSeriesStroke(0, new BasicStroke(newFunctionStroke));
        renderer3.setSeriesStroke(0, new BasicStroke(newFunctionStroke));
        renderer4.setSeriesStroke(0, new BasicStroke(newFunctionStroke));

        renderer_FFT.setSeriesStroke(0, new BasicStroke(newFunctionStroke));
        renderer2_FFT.setSeriesStroke(0, new BasicStroke(newFunctionStroke));
        renderer3_FFT.setSeriesStroke(0, new BasicStroke(newFunctionStroke));
        renderer4_FFT.setSeriesStroke(0, new BasicStroke(newFunctionStroke));
    }

    private void GenerateNewConfigFile(String ConfigFilePath) {
        FileWriter firstLine = null;
        FileWriter outputLine = null;
        String Cr = System.getProperty("line.separator");
        String HM1_X = "null";
        String HM1_Y = "null";
        String HM2_X = "null";
        String HM2_Y = "null";
        String VM1_X = "null";
        String VM1_Y = "null";
        String VM2_X = "null";
        String VM2_Y = "null";
        String HM_chartY1 = "null";
        String HM_chartY2 = "null";
        String VM_chartX1 = "null";
        String VM_chartX2 = "null";
        int iFFT_Channel[] = new int[5];
        iFFT_Channel[1] = FFT_Channel[1] ? 1 : 0;
        iFFT_Channel[2] = FFT_Channel[2] ? 1 : 0;
        iFFT_Channel[3] = FFT_Channel[3] ? 1 : 0;
        iFFT_Channel[4] = FFT_Channel[4] ? 1 : 0;


        //remember markers

        switch (NoRangeMarkersOnChart) {
            case 1:
                HM1_X = Integer.toString(HorizontalMarkerMouseClickLocation[0][0]);
                HM1_Y = Integer.toString(HorizontalMarkerMouseClickLocation[0][1]);
                HM_chartY1 = df.format(HorizontalMarkerLocation[0]);
                break;
            case 2:
                HM1_X = Integer.toString(HorizontalMarkerMouseClickLocation[0][0]);
                HM1_Y = Integer.toString(HorizontalMarkerMouseClickLocation[0][1]);
                HM2_X = Integer.toString(HorizontalMarkerMouseClickLocation[1][0]);
                HM2_Y = Integer.toString(HorizontalMarkerMouseClickLocation[1][1]);
                HM_chartY1 = df.format(HorizontalMarkerLocation[0]);
                HM_chartY2 = df.format(HorizontalMarkerLocation[1]);
                break;
            default:
                break;
        }
        switch (NoDomainMarkersOnChart) {//vertical
            case 1:
                VM1_X = Integer.toString(VerticalMarkerMouseClickLocation[0][0]);
                VM1_Y = Integer.toString(VerticalMarkerMouseClickLocation[0][1]);
                VM_chartX1 = df.format(VerticalMarkerLocation[0]);
                break;
            case 2:
                VM1_X = Integer.toString(VerticalMarkerMouseClickLocation[0][0]);
                VM1_Y = Integer.toString(VerticalMarkerMouseClickLocation[0][1]);
                VM2_X = Integer.toString(VerticalMarkerMouseClickLocation[1][0]);
                VM2_Y = Integer.toString(VerticalMarkerMouseClickLocation[1][1]);
                VM_chartX1 = df.format(VerticalMarkerLocation[0]);
                VM_chartX2 = df.format(VerticalMarkerLocation[1]);
                break;
            default:
                break;
        }

        try {
            firstLine = new FileWriter(ConfigFilePath);
            firstLine.write("");//ensures file is empty, so you can write software settings now
            firstLine.close();
            outputLine = new FileWriter(ConfigFilePath, true);//append true as i know for sure the file has just been emptied
            outputLine.write("//" + productID + " Software" + Cr +
                    "//" + Cr +
                    "//" + productID + " Software settings are saved in this file." + Cr +
                    "//" + Cr +
                    "//If you do require to make changes to this file, please do so by only changing text after \"=\" equal sign." + Cr +
                    "//Do not delete lines; write \"default\" for default setting." + Cr +
                    "//End every line with \";\"" + Cr +
                    "//" + Cr +
                    "//When" + productID + " Software attempts to load this file all line of text begining with // will be ignored." + Cr);

            outputLine.write(Cr + Cr + Cr);

            WinExtState = "";
            if (this.getExtendedState() == USBscope50_Main.MAXIMIZED_BOTH) {
                WinExtState = "MAX";
            } else if (this.getExtendedState() == USBscope50_Main.ICONIFIED) {
                WinExtState = "MIN";
            }

            FormLocationX = (int) this.getX();
            FormLocationY = (int) this.getY();
            FormSizeX = this.getWidth();
            FormSizeY = this.getHeight();
            DividerLocation = SplitPane.getDividerLocation();

            outputLine.write("T&MInstrument	= " + productID + ";" + Cr +
                    "Settings 	= GUI;\t\t//GUI" + Cr +
                    "FormSize	= " + WinExtState + FormSizeX + "," + FormSizeY + ";\t//Last available form width;MAX/MIN as first 3 letters will maximize/minimize form" + Cr +
                    "FormLocation	= " + FormLocationX + "," + FormLocationY + ";" + Cr +
                    "SplitPaneDivider= " + DividerLocation + ";\t\t//Split pane divider setting" + Cr +
                    "Font		= default;" + Cr +
                    "TimeBase        = " + intTimeBaseArrayPointer + ";" + Cr +
                    "VoltsBase       = " + intVoltsperDivPointer[1] + ", " + intVoltsperDivPointer[2] + ", " + intVoltsperDivPointer[3] + ", " + intVoltsperDivPointer[4] + ";" + Cr +
                    "Ch GND          = " + gnd_option[1] + ", " + gnd_option[2] + ", " + gnd_option[3] + ", " + gnd_option[4] + ";\t//non zero value - signal grounded" + Cr +
                    "Ch Coupling     = " + Dc_option[1] + ", " + Dc_option[2] + ", " + Dc_option[3] + ", " + Dc_option[4] + ";\t//0=AC coupling; non zero value sets DC coupling" + Cr +
                    "Ch Probe        = " + Probe[1] + ", " + Probe[2] + ", " + Probe[3] + ", " + Probe[4] + ";\t//1-x1 Probe Setting; 2-x10 Probe Setting" + Cr +
                    Cr + "//Axis labels Settings" + Cr +
                    //"X-AxisLabelOn   = " + (MnuShowXAxisLabels.isSelected() ? 1 : 0) + ";" + Cr +
                    "X-AxisLabelOn   = " + "0" + ";" + Cr +
                    "Y-AxisLabelOn   = " + (MnuShowYAxisLabels.isSelected() ? 1 : 0) + ";" + Cr +
                    Cr + "//Trigger Settings" + Cr +
                    "TriggerChannel  = " + TriggerChannel + ";" + Cr +
                    "TriggerMode     = " + triggermode + ";\t\t//0-Free; 1-Normal" + Cr +
                    "TriggerType     = " + currentTrigType + ";\t\t//0-Less Than; 1-Greater Than; 2-Falling Edge; 3-Rising Edge" + Cr +
                    "TriggerLevel    = " + Threshold + ";" + Cr +
                    "Pretrigger      = " + TriggerPositionSlider.getValue() + ";" + Cr +
                    Cr + "//Channel options" + Cr +
                    "INV             = " + INV[1] + ", " + INV[2] + ", " + INV[3] + ", " + INV[4] + ";\t//1-false; -1-signal inverse displayed on the screen" + Cr +
                    "ON              = 1, 1, 1, 1;\t//0-false; any other value true (signal on this channel is displayed on the screen)" + Cr +
                    "COMP            = 0, 0, 0, 0;\t// On start up this will always be false" + Cr +
                    "FFT             = " + iFFT_Channel[1] + ", " + iFFT_Channel[2] + ", " + iFFT_Channel[3] + ", " + iFFT_Channel[4] + ";" + Cr +
                    "Offset          = " + functionOffset[1] / 100 + ", " + functionOffset[2] / 100 + ", " + functionOffset[3] / 100 + ", " + functionOffset[4] / 100 + ";" + Cr +
                    Cr + "//Chart Color Settings" + Cr +
                    "Ch1 Color      = Color(" + ColorCh1.getRed() + "," + ColorCh1.getGreen() + "," + ColorCh1.getBlue() + ");" + Cr +
                    "Ch2 Color      = Color(" + ColorCh2.getRed() + "," + ColorCh2.getGreen() + "," + ColorCh2.getBlue() + ");" + Cr +
                    "Ch3 Color      = Color(" + ColorCh3.getRed() + "," + ColorCh3.getGreen() + "," + ColorCh3.getBlue() + ");" + Cr +
                    "Ch4 Color      = Color(" + ColorCh4.getRed() + "," + ColorCh4.getGreen() + "," + ColorCh4.getBlue() + ");" + Cr +
                    "Background     = Color(" + ChartBackgroundColor.getRed() + "," + ChartBackgroundColor.getGreen() + "," + ChartBackgroundColor.getBlue() + ");" + Cr +
                    "GridLines      = Color(" + DomainGridColor.getRed() + "," + DomainGridColor.getGreen() + "," + DomainGridColor.getBlue() + ");" + Cr +
                    Cr + "//Chart Stroke Settings" + Cr +
                    "FunctionStroke = " + FunctionStroke + ";" + Cr +
                    "RangeStroke    = " + DomainStroke + ";" + Cr +
                    "DomainStroke   = " + RangeStroke + ";" + Cr +
                    Cr + "//FFT" + Cr +
                    "Window = " + lngFFTWindowType + ";" + Cr +
                    "Plot   = " + dblFFTPlotType + ";" + Cr + Cr +
                    "//Menu Marker Options" + Cr +
                    "MenuAddHorizMarker	= " + (MnuAddHorizontalMarker.isSelected() ? 1 : 0) + ";" + Cr +
                    "MenuAddVertiMarker	= " + (MnuAddVerticalMarker.isSelected() ? 1 : 0) + ";" + Cr + Cr +
                    "//Horizontal Markers" + Cr +
                    "HMarker1     = " + HM1_X + ", " + HM1_Y + ";" + Cr +
                    "chartY1      = " + HM_chartY1 + ";" + Cr +
                    "HMarker2     = " + HM2_X + ", " + HM2_Y + ";" + Cr +
                    "chartY2      = " + HM_chartY2 + ";" + Cr +
                    Cr + "//Vertical Markers" + Cr +
                    "VMarker1     = " + VM1_X + ", " + VM1_Y + ";" + Cr +
                    "chartX1      = " + VM_chartX1 + ";" + Cr +
                    "VMarker2     = " + VM2_X + ", " + VM2_Y + ";" + Cr +
                    "chartX2      = " + VM_chartX2 + ";" + Cr);

        } catch (IOException ex) {
            Logger.getLogger(USBscope50_Main.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                outputLine.close();
            } catch (IOException ex) {
                Logger.getLogger(USBscope50_Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void GetGraphValueOfMouseClickedPoint(double mouseX, int mouseY, int dataSetNum) {//dataSetNum 0 to 3 for ch1-4
        /**
         * Sets chartX and chartY. Graph value point where mouse clicked
         */
        //int mouseX = event.getTrigger().getX();
        //int mouseY = event.getTrigger().getY();
        Point2D p = USBscope50_Main.chartPanel.translateScreenToJava2D(new Point((int) mouseX, mouseY));
        //XYPlot tempPlot = (XYPlot) JFchart.getPlot();
        ChartRenderingInfo info = USBscope50_Main.chartPanel.getChartRenderingInfo();
        Rectangle2D dataArea = info.getPlotInfo().getDataArea();

        ValueAxis domainAxis = chartPlot.getDomainAxis();

        RectangleEdge domainAxisEdge = chartPlot.getDomainAxisEdge();
        //ValueAxis rangeAxis = chartPlot.getRangeAxis();//???ANA***
        ValueAxis rangeAxis = chartPlot.getRangeAxisForDataset(dataSetNum);//(dataSetNum);//dataset 1 is for channel 2
        //System.out.println("dataset0 " + rangeAxis.getLowerBound() + " and " + rangeAxis.getUpperBound());
        RectangleEdge rangeAxisEdge = chartPlot.getRangeAxisEdge();
        chartX = domainAxis.java2DToValue(p.getX(), dataArea, domainAxisEdge);
        chartY = rangeAxis.java2DToValue(p.getY(), dataArea, rangeAxisEdge);

    }

    private double GetPointsPerDiv(int ArrayPointer) {
        double points = 100;

        /*if (demoMode){
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

    private String GetPropertyValue(String PropertyFileName, String PortNo) {
        String PropertyValue = "";
        String USBDevicePropertiesLocation;

        USBDevicePropertiesLocation = "/sys/bus/usb/devices/" + PortNo; //returns e.g. 1-3
        File USBDevicePropertyFolder = new File(USBDevicePropertiesLocation);

        if (USBDevicePropertyFolder.exists() && USBDevicePropertyFolder.isDirectory()) {
            File propertyFile = new File(USBDevicePropertiesLocation + "/" + PropertyFileName);
            if (propertyFile.exists() && propertyFile.isFile() && propertyFile.canRead()) {
                BufferedReader productReader = null;
                try {
                    productReader = new BufferedReader(new FileReader(propertyFile));
                    PropertyValue = productReader.readLine(); //expected to read USBscope50 or USBcount50, etc.

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
        return PropertyValue;
    }

    private void GetScopesProperties() {
        int chanct;
        for (chanct = 1; chanct <= numScopesFound; chanct++) {
            //ChannelPresent[chanct] = true;
            if (demoMode) {
                ScopeProductNames[chanct] = productID;//"USBscope50";
                ScopeSerialNumbers[chanct] = "0000";
                ScopeHWRevs[chanct] = -1;
                ScopePortNumbers[chanct] = -1;

            } else {
                ScopeProductNames[chanct] = GetPropertyValue("product", gUSBscope50Devices[0][chanct]); //Done in Scan for scopes
                ScopeSerialNumbers[chanct] = GetPropertyValue("serial", gUSBscope50Devices[0][chanct]);
                ScopeHWRevs[chanct] = Integer.parseInt(GetPropertyValue("bcdDevice", gUSBscope50Devices[0][chanct]));
                ScopePortNumbers[chanct] = Integer.parseInt(gUSBscope50Devices[1][chanct].substring(6));
                IDMsg[chanct] = "\n" + ScopeProductNames[chanct] + "\nS/N: " + ScopeSerialNumbers[chanct] + "\nSerial Port: ttyUSB" + ScopePortNumbers[chanct] + "\nREV: " + ScopeHWRevs[chanct];
            }

        }
    }

    private void GetidProduct() {
        String product = "";
        String USBDevicePropertiesLocation;

        for (int i = 1; i < gUSBTandMDevices[0].length; i++) {
            USBDevicePropertiesLocation = "/sys/bus/usb/devices/" + gUSBTandMDevices[0][i]; //returns e.g. 1-3

            File USBDevicePropertyFolder = new File(USBDevicePropertiesLocation);

            if (USBDevicePropertyFolder.exists() && USBDevicePropertyFolder.isDirectory()) {
                File productFile = new File(USBDevicePropertiesLocation + "/product");
                if (productFile.exists() && productFile.isFile() && productFile.canRead()) {
                    BufferedReader productReader = null;
                    try {
                        productReader = new BufferedReader(new FileReader(productFile));
                        product = productReader.readLine(); //expected to read USBscope50 or USBcount50, etc.

                        gUSBTandMDevices[2][i] = product;
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
        }
    }

    private void Import3000Points_TxtFile() {
        ClearRunFlags();
        JFileChooser fc = new JFileChooser();

        //set up File Chooser as save as dialog
        File defaultDirectory = new File(System.getProperty("user.home"));
        fc.setCurrentDirectory(defaultDirectory);
        fc.setAcceptAllFileFilterUsed(false);
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fc.addChoosableFileFilter(new MyFilterTxt());

        int returnVal = fc.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {

            File GraphPointsFile = fc.getSelectedFile();
            if (!(GraphPointsFile.getName().contains("."))) {
                //probably .txt missing
                GraphPointsFile = new File(fc.getSelectedFile().getPath() + ".txt");
            }

            String TxtFileOk = checkifUSBscope50txtfile(GraphPointsFile); //check if selected file is ok; return error msg or load file

            if (TxtFileOk.length() > 0) {
                //error msg returned
                String OpenMsg = "\nError encountered while opening \n" + GraphPointsFile + "\n\n" + TxtFileOk + "\n\n";
                JOptionPane.showMessageDialog(this, OpenMsg, productID + " Software", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            //open command cancelled by user
        }
    }

    private void InitializeScopes() {

        String Msg = "";
        if (USBFamily_Main.OS.equalsIgnoreCase("Linux")) {
            numIntsFound = EnumUSB_SerialDevices(); //returns number of cp2101 devices attached

        } else if (USBFamily_Main.OS.equalsIgnoreCase("Windows")) {
            if (abort == 10) { //System.load library returns unsatisfied link error
                Msg = "\n" + productID + " Software will terminate now.\n\n" +
                        "Unable to locate " + productID + "Drvr_W32.dll\n\n" +
                        "Please insure that this file is in \n" +
                        "C:\\Program Files\\" + companyID + "\\" + productID + " Java Software\n" +
                        " folder and then try running " + productID + " Software again. \n\n" +
                        companyID;
                JOptionPane.showMessageDialog(this, Msg, "Fatal Error", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
            hDrvr = t.USBscopeDrvr_OpenDrvr();//return 1 on success

            if (hDrvr == 0) {//cp210xMan.dll missing

                Msg = "\n" + productID + " Software will terminate now.\n\n" +
                        "Unable to open cp210xMan.dll\n\n" +
                        "Please insure that this file is in \n" +
                        "C:\\Program Files\\" + companyID + "\\" + productID + " Java Software\n" +
                        "folder and that you have read access to this location.\n\n" +
                        companyID;
                JOptionPane.showMessageDialog(this, Msg, "Fatal Error", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
            String WinOS = System.getProperty("os.name");//Windows 9; Windows 2000; Windows XP;Windows Vista

            int RegKeyPath;
            if (WinOS.indexOf("Window 9") > 0) {
                RegKeyPath = 1;
            } else {
                RegKeyPath = 2;
            }

            numIntsFound = t.USBscope50Drvr_Enumerate(RegKeyPath);

            //check if all instances found return with USBscope50 product name
            String productName;
            cpynumIntsFound = numIntsFound;
            for (int temploop = 0; temploop < numIntsFound; temploop++) {
                productName = t.USBscope50Drvr_GetProductName(temploop + 1);

                if (productName.equalsIgnoreCase("usbscope50")) {
                } else {
                    cpynumIntsFound--;
                }
            }

        }
        if (USBFamily_Main.OS.equalsIgnoreCase("Linux")) {
            arraySize = numIntsFound;
        } else {
            arraySize = cpynumIntsFound;
        }
        //the main  point here is just to determine if the software should run in demo mode
        if (arraySize <= 0) {
            arraySize = MaxScope; //maxscope = 4; don't allow 0 scopes found situation
            numIntsFound = 0;
            demoMode = true;
            if (USBFamily_Main.OS.equalsIgnoreCase("Windows")) {

                //Custom button text
                Object[] options = {"OK", "Quit Software"};
                int response;
                Msg = "\n" + productID + " Software will run in Demo Mode\n" +
                        "as no USBscope50 devices were detected.\n\n";//"as no USB Test and Measurement devices were detected.\n\n";

                if (companyID.equals("PRIST")) {
                    response = JOptionPane.showOptionDialog(this, Msg, productID + " - Demo Mode",
                            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, AKIPLogo_small, options, options[0]);
                } else {
                    response = JOptionPane.showOptionDialog(this, Msg, productID + " - Demo Mode",
                            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, ElanLogo_Small, options, options[0]);
                }
                if (response > 0) {
                    System.exit(0);
                }
            }
        }

        ScopeProductNames = new String[arraySize + 1];
        ScopeSerialNumbers = new String[arraySize + 1];
        for (int loop = 0; loop < arraySize; loop++) {
            ScopeSerialNumbers[loop] = "0000";
        }

        ScopePortNumbers = new int[arraySize + 1];
        ScopeHWRevs = new int[arraySize + 1];
        //ScopeControllerRevs = new int[arraySize+1];
        rismax = new int[arraySize + 1];
        rismin = new int[arraySize + 1];
        triggerDACoffset = new int[arraySize + 1];
        rawchanneloffset = new float[arraySize + 1];
        offsetDACscale = new float[arraySize + 1];
        ChannelOn = new boolean[5];//[arraySize + 1];
        IDMsg = new String[MaxScope];
        IDMsg[1] = "\n\nS/N: 0000" + "\nSerial Port: Not connected" + "\nREV: ";
        IDMsg[2] = "\n\nS/N: 0000" + "\nSerial Port: Not connected" + "\nREV: ";
        IDMsg[3] = "\n\nS/N: 0000" + "\nSerial Port: Not connected" + "\nREV: ";
        IDMsg[4] = "\n\nS/N: 0000" + "\nSerial Port: Not connected" + "\nREV: ";

        //Filter out any scopes
        if (USBFamily_Main.OS.equalsIgnoreCase("Linux")) {
            ScanforScopes_Linux(); //Loads gUSBscope50Devices array with USBscope properties

        } else if (USBFamily_Main.OS.equalsIgnoreCase("Windows") && !demoMode) {
            numScopesFound = numIntsFound;
            ScanforScopes_Windows();//scanfor scopes will elliminate waves, pulses, etc
            numScopesFound = numIntsFound;
            arraySize = numIntsFound;

        }



        // PrintOutArrays(); //For debug purposes only; comment out
        //don't use when in demo mode, you will get out of bound exception as numscopes=4, but global array is not loaded
        //g arrays only contain virtual files properties

        demoMode = (numScopesFound == 0);

        if (demoMode == true) {
            numScopesFound = MaxScope - 1; //Can not call GetScopesProperties when in demoMode as virtual files have not been created and the system will crash
        //numIntsFound is different than numscopes found, as it may contain other than scope usb devices

        } else {
            if (USBFamily_Main.OS.equalsIgnoreCase("Windows")) {
                //get scope properties Serial numbers, com port from registry
                //not true do it here //ports have already been open by this point in windows in open and reset, just after enumerate
            } else if (USBFamily_Main.OS.equalsIgnoreCase("Linux")) {
                GetScopesProperties(); //Getting scope details,

                int intPort;
                int status; //good is 1

                for (int i = 1; i <= numScopesFound; i++) {
                    intPort = ScopePortNumbers[i];//Integer.parseInt(ScopePortNumbers[i].substring(6));
                    status = t.OpenPorts(i, intPort);//Linux specific
                    ChannelPresent[i] = (status != 0);
                    ChannelOn[i] = ChannelPresent[i];
                }
            }



            //set all found scopes to TrigMaster false and float the detect line
            for (int i = 1; i <= numScopesFound; i++) {
                t.USBscope50Drvr_SetTrigMaster(i, 0);//channel, not master (master not set yet)
                t.USBscope50Drvr_SetDetectLine(i, 0, 1);//(channel,master,state (1=float the detect line))
            }

            Msg = "";
            if (!(CheckScopesStackStatus(numScopesFound))) {//check if powered
                Msg = "\nWARNING:\n" + "There may be one or more " + productID + "s in the stack " + "that are not connected to a USB port. " + "\n\nThe stack cannot work like this." + "\nPlease connect all stacked " + productID + "s to USB ports and check" + " the power LED on each is illuminated. \n\n";
            }

            MasterChannel = 1; //choose channel 1 as MASTER

            t.USBscope50Drvr_SetDetectLine(MasterChannel, 1, 0);//(channel,master,state (0=set detect line low on master channel))

            if (CheckScopesStackStatus(numScopesFound - 1)) {//checks all apart MASTER CH if in stack
                //function returns true as default value, so we need to exclude when in demo  mode and when only 1 scope attached
                Msg = Msg + "\nWARNING:\n" + "There may be one or more " + productID + "s that are not " + "in the main stack. " + "\n\nThe stack cannot work like this.\nPlease " + "either stack all connected " + productID + "s or unplug any non-stacked " + "scopes from their USB port. \n\n";
            }

            if (Msg.length() > 1) {
                demoMode = true;
                numScopesFound = 4;
                ChannelPresent[1] = true;
                ChannelPresent[2] = true;
                ChannelPresent[3] = true;
                ChannelPresent[4] = true;
                t.ClosePorts();
                ScopeProductNames = new String[arraySize + 1];
                ScopeSerialNumbers = new String[arraySize + 1];
                ScopePortNumbers = new int[arraySize + 1];
                ScopeHWRevs = new int[arraySize + 1];
                //ScopeControllerRevs = new int[arraySize+1];
                rismax = new int[arraySize + 1];
                rismin = new int[arraySize + 1];
                triggerDACoffset = new int[arraySize + 1];
                rawchanneloffset = new float[arraySize + 1];
                offsetDACscale = new float[arraySize + 1];
                ChannelOn = new boolean[5];//[arraySize + 1];
                ChannelOn[1] = true;
                ChannelOn[2] = true;
                ChannelOn[3] = true;
                ChannelOn[4] = true;

                Object[] options = {"OK", "Quit Software"};
                int response;
                Msg = Msg + "Software will run in Demo Mode.\n";

                if (companyID.equals("PRIST")) {
                    response = JOptionPane.showOptionDialog(this, Msg, productID + " - Demo Mode",
                            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, AKIPLogo_small, options, options[0]);
                } else {
                    response = JOptionPane.showOptionDialog(this, Msg, productID + " - Demo Mode",
                            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, ElanLogo_Small, options, options[0]);
                }
                if (response > 0) {
                    System.exit(0);
                }
            }
        }

        if (!demoMode) {
            if (USBFamily_Main.OS.equalsIgnoreCase("Windows")) {//for linux properties read in GetScopesProperties above
                for (int i = 1; i <= numScopesFound; i++) {
                    ScopeProductNames[i] = t.USBscope50Drvr_GetProductName(i);
                    ScopeHWRevs[i] = t.USBscope50Drvr_GetControllerRev(i);//pass channel values 1-4
                    ScopeSerialNumbers[i] = t.USBscope50Drvr_GetSerialNumber(i);
                    ScopePortNumbers[i] = t.USBscope50Drvr_GetPortNumber(i);
                    IDMsg[i] = "\n" + ScopeProductNames[i] + "\nS/N: " + ScopeSerialNumbers[i] + "\nSerial Port: COM" + ScopePortNumbers[i] + "\nREV: " + ScopeHWRevs[i];
                }
            }

            OpenInitializeScopes();
            t.USBscope50Drvr_SetTrigMaster(MasterChannel, 1);
        //TriggerChannel = MasterChannel;// don't do this! triggerchannel read form config file
        }

        RightSplitPane.removeAll();
        //part 2
        switch (numScopesFound) {
            case 1:
                RightSplitPane.addTab("Ch 1   ", ScrollOptionsCh1);
                MnuCh1.setSelected(true);
                MnuCh2.setSelected(false);
                MnuCh3.setSelected(false);
                MnuCh4.setSelected(false);
                RadioButTrigOnChan1.setEnabled(true);//allow trigger onlty on present scopes
                RadioButTrigOnChan2.setEnabled(false);
                RadioButTrigOnChan3.setEnabled(false);
                RadioButTrigOnChan4.setEnabled(false);
                break;
            case 2:
                RightSplitPane.addTab("Ch 1   ", ScrollOptionsCh1);
                RightSplitPane.addTab("Ch 2   ", ScrollOptionsCh2);
                MnuCh1.setSelected(true);
                MnuCh2.setSelected(true);
                MnuCh3.setSelected(false);
                MnuCh4.setSelected(false);
                RadioButTrigOnChan1.setEnabled(true);
                RadioButTrigOnChan2.setEnabled(true);
                RadioButTrigOnChan3.setEnabled(false);
                RadioButTrigOnChan4.setEnabled(false);
                break;
            case 3:
                RightSplitPane.addTab("Ch 1   ", ScrollOptionsCh1);
                RightSplitPane.addTab("Ch 2   ", ScrollOptionsCh2);
                RightSplitPane.addTab("Ch 3   ", ScrollOptionsCh3);
                MnuCh1.setSelected(true);
                MnuCh2.setSelected(true);
                MnuCh3.setSelected(true);
                MnuCh4.setSelected(false);
                RadioButTrigOnChan1.setEnabled(true);
                RadioButTrigOnChan2.setEnabled(true);
                RadioButTrigOnChan3.setEnabled(true);
                RadioButTrigOnChan4.setEnabled(false);
                break;
            default:
                RightSplitPane.addTab("Ch 1   ", ScrollOptionsCh1);//Demo mode or 4 channels
                RightSplitPane.addTab("Ch 2   ", ScrollOptionsCh2);
                RightSplitPane.addTab("Ch 3   ", ScrollOptionsCh3);
                RightSplitPane.addTab("Ch 4   ", ScrollOptionsCh4);
                MnuCh1.setSelected(true);
                MnuCh2.setSelected(true);
                MnuCh3.setSelected(true);
                MnuCh4.setSelected(true);
                RadioButTrigOnChan1.setEnabled(true);
                RadioButTrigOnChan2.setEnabled(true);
                RadioButTrigOnChan3.setEnabled(true);
                RadioButTrigOnChan4.setEnabled(true);
                break;

        }

        RightSplitPane.addTab("Trigger", ScrollTriggerPanel);
        //RightSplitPane.addTab("Offset", ScrollOffsetPanel);
        RightSplitPane.addTab("Zoom   ", ScrollCustomZoomPane);

        if (demoMode) {
            //try {

            /*for (int i = 0; i < 3000; i++) {
            LoadDataArray.SampleData[1][i] = 0;//+(float)(OffsetSliderCh1.getValue()* YDiv[1] * 5 / 10000);
            LoadDataArray.SampleData[2][i] = 0;//+(float)(OffsetSliderCh2.getValue()* YDiv[2] * 5 / 10000);
            LoadDataArray.SampleData[3][i] = 0;//+(float)(OffsetSliderCh3.getValue()* YDiv[3] * 5 / 10000);
            LoadDataArray.SampleData[4][i] = 0;//+(float)(OffsetSliderCh4.getValue()* YDiv[4] * 5 / 10000);

            }*/

            if (USBFamily_Main.OS.equalsIgnoreCase("Windows")) {
                LoadDemoFunctions(2);
            }
        //Demo_cloneCh1 = (XYSeries) USBscope50_Main.seriesCh1.clone();
        //Demo_cloneCh2 = (XYSeries) USBscope50_Main.seriesCh2.clone();
        //Demo_cloneCh3 = (XYSeries) USBscope50_Main.seriesCh3.clone();
        //Demo_cloneCh4 = (XYSeries) USBscope50_Main.seriesCh4.clone();
        //} catch (CloneNotSupportedException ex) {
        //   Logger.getLogger(USBscope50_Main.class.getName()).log(Level.SEVERE, null, ex);
        //}
        }
    }

    private void LoadDemoFunctions(int noFunctionsToImport) {
        int i;
        File DemoFile;



        PathToDemoFile[1] = System.getenv("ProgramFiles") + "/" + companyID + "/" + productID + " Java Software/CH1_DEMO.txt";
        PathToDemoFile[2] = System.getenv("ProgramFiles") + "/" + companyID + "/" + productID + " Java Software/CH2_DEMO.txt";
        PathToDemoFile[3] = System.getenv("ProgramFiles") + "/" + companyID + "/" + productID + " Java Software/CH3_DEMO.txt";
        PathToDemoFile[4] = System.getenv("ProgramFiles") + "/" + companyID + "/" + productID + " Java Software/CH4_DEMO.txt";

        for (i = 1; i <= noFunctionsToImport; i++) {
            ClearRunFlags();
            DemoFile = new File(PathToDemoFile[i]);
            if (DemoFile.exists() && DemoFile.canRead() && DemoFile.isFile()) {
                String TxtFileOk = checkifUSBscope50txtfile(DemoFile); //check if selected file is ok; return error msg or load file
            }
        }
        OrganizeTabs("Ch 1");//Select Tab Ch1
    }

    private String LoadGraphPoints(File GraphPointsFile) {
        /* Routine called when import text file option selected.
         * Some checks have already been done on the file in checkifUSBscope50txtfile,
         * now open selected text file and go through it line by line.
         * Return empty string on success.*/
        String returnString = "";
        String TxtFileLine = null;
        FileReader fr = null;
        int DataArrayIndex = 0;
        int TxtFileLineTracker = 0;
        String tempTimeBaseSetting;
        int newTimeBaseArrayPointer = 100; //any value greater than 27, length of the array, would do
        int ImportToChannel = 1; //Default to channel 1
        float[] ImportDataArray = new float[maxGraphPointCount];
        boolean EnoughDataPoints = false; //true when 3000 points read, ignore the rest of the data in the text file
        int IgnoreAfterLineNo = 0; //when more than 3000 data points in a txt file, everything after this line is ignored
        String VDIVSetting;
        int newVDIVSetting = 100;

        try {
            fr = new FileReader(GraphPointsFile);
            BufferedReader br = new BufferedReader(fr);
            while ((TxtFileLine = br.readLine()) != null) {
                TxtFileLineTracker++;

                if (TxtFileLine.startsWith("//")) { //line includes a comment-do nothing
                } else if (TxtFileLine.toUpperCase().startsWith("DATA ON CHANNEL")) {

                    try {
                        ImportToChannel = Integer.parseInt(TxtFileLine.substring(TxtFileLine.indexOf(":") + 1).trim());
                        if ((ImportToChannel > 4) || (ImportToChannel < 1)) {
                            returnString = "Error on line " + TxtFileLineTracker + ".\n" + TxtFileLine + "\n\nPlease only use channels 1 to 4.";
                            return returnString;
                        }
                    } catch (Exception exception) {
                        returnString = "Error on line " + TxtFileLineTracker + ".\n" + TxtFileLine + "\n\nPlease only use channels 1 to 4.";
                        return returnString;
                    }
                } else if (TxtFileLine.toUpperCase().startsWith("TIME BASE SETTING")) {
                    for (int i = 0; i < TimeBaseSettings.length; i++) {
                        tempTimeBaseSetting = TimeBaseSettings[i][0];
                        tempTimeBaseSetting = tempTimeBaseSetting.trim();

                        if (TxtFileLine.contains(tempTimeBaseSetting)) {
                            newTimeBaseArrayPointer = i;
                        }
                    }
                    if (newTimeBaseArrayPointer > TimeBaseSettings.length) {//lets see if timebase setting is correct
                        //error not found valid timebase in the txt file
                        returnString = "Error on line " + TxtFileLineTracker + ".\n" + "Please enter valid data for " + productID + " time base setting." +
                                "\n\nAvailable TIME BASE settings :\n" + "2 us/div, 4 us/div, 10 us/div, 20 us/div, 40 us/div, 100 us/div, 200 us/div, 400 us/div," +
                                "\n1 ms/div, 2 ms/div, 4 ms/div, 10 ms/div, 20 ms/div, 40 ms/div," +
                                "\n100 ms/div, 200 ms/div, 400 ms/div, 1 s/div, 2 s/div, 4 s/div";
                        return returnString;
                    }
                } else if (TxtFileLine.toUpperCase().startsWith("V/DIV SETTING")) {

                    VDIVSetting = TxtFileLine.substring(TxtFileLine.indexOf(":") + 1).trim();
                    if (VDIVSetting.length() > 0) {
                        for (newVDIVSetting = 0; newVDIVSetting < VoltsDivSettings.length; newVDIVSetting++) {
                            if (VoltsDivSettings[newVDIVSetting][1].contains(VDIVSetting)) {
                                break;
                            }
                        }
                    }

                } else if (TxtFileLine.toUpperCase().startsWith("FILE PATH & NAME")) { //do nothing
                } else if (TxtFileLine.toUpperCase().startsWith("DATE/TIME STAMP")) { //do nothing
                } else if (TxtFileLine.trim().length() == 0) { //empty line do nothing
                } else {
                    //assume this is data line
                    if (DataArrayIndex < 3000) {
                        try {
                            ImportDataArray[DataArrayIndex] = Float.parseFloat(TxtFileLine);
                        } catch (NumberFormatException e) {
                            returnString = "Error on line " + TxtFileLineTracker + ".\n" + TxtFileLine + "\nExpected to find a numeric value." + "\n\nImport " + productID + " graph data aborted.";
                            return returnString;
                        }
                    } else if ((DataArrayIndex >= 3000) && (!EnoughDataPoints)) {
                        EnoughDataPoints = true;
                        IgnoreAfterLineNo = TxtFileLineTracker;
                    /* enough points read, ignore the rest of the file, but I decided still to display the graph points
                     * as well as display an error msg. I think this is acceptable ?*/
                    }
                    DataArrayIndex++; //data line found
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(USBscope50_Main.class.getName()).log(Level.SEVERE, null, ex);
            returnString = ex.getMessage() + "\nStopped reading text file on line " + TxtFileLineTracker + ".";
        } finally {
            try {
                fr.close();
            } catch (IOException ex) {
                Logger.getLogger(USBscope50_Main.class.getName()).log(Level.SEVERE, null, ex);
                returnString = ex.getMessage() + "\nStopped reading text file on line " + TxtFileLineTracker + ".";
            }
        }
        //lets see if there IS timebase line in the text file; already checked if time base value is correct; see if there at all
        if (newTimeBaseArrayPointer > TimeBaseSettings.length) {
            //error not found valid timebase setting in the txt file
            returnString = "Unable to import graph, time base setting missing.\nPlease add a line stating time base setting." +
                    "\n\ne.g. TIME BASE SETTING : 200 us/div" + "\n\nAvailable TIME BASE settings :\n" +
                    "2 us/div, 4 us/div, 10 us/div, 20 us/div, 40 us/div, 100 us/div, 200 us/div, 400 us/div," +
                    "\n1 ms/div, 2 ms/div, 4 ms/div, 10 ms/div, 20 ms/div, 40 ms/div," + "\n100 ms/div, 200 ms/div, 400 ms/div, 1 s/div, 2 s/div, 4 s/div";
            return returnString;
        }
        if (newVDIVSetting < VoltsDivSettings.length) {
            intVoltsperDivPointer[ImportToChannel] = newVDIVSetting;
            VoltsperDivChangedEvent(intVoltsperDivPointer[ImportToChannel], ImportToChannel);
        }
        if (returnString.length() == 0) { //All OK, display data points on the screen;check GUI is ready & show the function
            ImportedData = true;
            doTabstoImport(ImportToChannel, newTimeBaseArrayPointer);
            //if you do not want to display data points if in txt file there are more than 3000 points just do
            //if dataarrayindex>3000 before changing series data points
            int i;

            if (ImportToChannel == 1) {
                ONButtonCh1.setSelected(true);
                SetButtonState(1, ONButtonCh1, ONButtonCh1.isSelected());//force series on
                SetSeriesONState(1, ONButtonCh1.isSelected());//force series to be visible

                for (i = 0; i < 3000; i++) {
                    LoadDataArray.SampleData[1][i] = (INV[1] * ImportDataArray[i]) + (float) (OffsetSliderCh1.getValue() * YDiv[1] * 5 / 10000);

                }
            }
            if (ImportToChannel == 2) {
                ONButtonCh2.setSelected(true);
                SetButtonState(2, ONButtonCh2, ONButtonCh2.isSelected());//force series on
                SetSeriesONState(2, ONButtonCh2.isSelected());//force series to be visible

                for (i = 0; i < 3000; i++) {
                    LoadDataArray.SampleData[2][i] = (INV[2] * ImportDataArray[i]) + (float) (OffsetSliderCh2.getValue() * YDiv[2] * 5 / 10000);
                }
            }
            if (ImportToChannel == 3) {
                ONButtonCh3.setSelected(true);
                SetButtonState(3, ONButtonCh3, ONButtonCh3.isSelected());//force series on
                SetSeriesONState(3, ONButtonCh3.isSelected());//force series to be visible

                for (i = 0; i < 3000; i++) {
                    LoadDataArray.SampleData[3][i] = (INV[3] * ImportDataArray[i]) + (float) (OffsetSliderCh3.getValue() * YDiv[3] * 5 / 10000);
                }
            }
            if (ImportToChannel == 4) {
                ONButtonCh4.setSelected(true);
                SetButtonState(4, ONButtonCh4, ONButtonCh4.isSelected());//force series on
                SetSeriesONState(4, ONButtonCh4.isSelected());//force series to be visible

                for (i = 0; i < 3000; i++) {
                    LoadDataArray.SampleData[4][i] = (INV[1] * ImportDataArray[i]) + (float) (OffsetSliderCh4.getValue() * YDiv[4] * 5 / 10000);
                }
            }

            if (!JavaRunning) {
                USBscope50_Main.seriesCh1.clear();
                USBscope50_Main.seriesCh2.clear();
                USBscope50_Main.seriesCh3.clear();
                USBscope50_Main.seriesCh4.clear();
                if (demoMode) {
                    t.DisplayDataDemo(false);
                } else {
                    t.DisplayData();
                }
            }

            if (DataArrayIndex > 3000) { //more than 3000 points in the txt file, still display 3000 points & don't abort
                returnString = "Too many data points found in this text file." + "\n\n" + productID + " Software can display on the graph up to 3000 data points." + "\nAll data values after the line " + (IgnoreAfterLineNo - 1) + " was ignored.";
            }
        }
        return returnString;
    }

    private void OffsetSelectActionperformed(int channel, boolean selected) {

        demoOffsetDoNoUpdate = true;
        renderer.setSeriesStroke(0, new BasicStroke(FunctionStroke));
        renderer2.setSeriesStroke(0, new BasicStroke(FunctionStroke));
        renderer3.setSeriesStroke(0, new BasicStroke(FunctionStroke));
        renderer4.setSeriesStroke(0, new BasicStroke(FunctionStroke));

        //SliderOffset.setEnabled(true);
        if (!selected) {
            return;
        }
        // float highlighter = 1;//getHighlightValue();
        //ignoreMouseMove = true;
        /*offsetCh = channel;
        switch (channel) {
        case 1:
        //RadioButOffsetChan1.setSelected(true);
        //SliderOffset.setValue((int) functionOffset[1]);
        //renderer.setSeriesStroke(0, new BasicStroke(FunctionStroke * highlighter));
        break;
        case 2:
        //RadioButOffsetChan2.setSelected(true);
        //SliderOffset.setValue((int) functionOffset[2]);
        //renderer2.setSeriesStroke(0, new BasicStroke(FunctionStroke * highlighter));
        break;
        case 3:
        //RadioButOffsetChan3.setSelected(true);
        //SliderOffset.setValue((int) functionOffset[3]);
        //renderer3.setSeriesStroke(0, new BasicStroke(FunctionStroke * highlighter));
        break;
        case 4:
        //RadioButOffsetChan4.setSelected(true);
        //SliderOffset.setValue((int) functionOffset[4]);
        //renderer4.setSeriesStroke(0, new BasicStroke(FunctionStroke * highlighter));
        break;
        }*/


        chartPanel.requestFocusInWindow();
    }

    private void UpdateVDivLabels(int SettingIndex, int intChannel) {
        //ClearBackgroundlbVDivs();
        switch (intChannel) {//setting index[0->11]
            case 2:
                lbVDivCh2.setText(" Ch2:" + VoltsDivSettings[SettingIndex][Probe[2]].toString());
                break;
            case 3:
                lbVDivCh3.setText(" Ch3:" + VoltsDivSettings[SettingIndex][Probe[3]].toString());
                break;
            case 4:
                lbVDivCh4.setText(" Ch4:" + VoltsDivSettings[SettingIndex][Probe[4]].toString());
                break;
            default:
                lbVDivCh1.setText(" Ch1:" + VoltsDivSettings[SettingIndex][Probe[1]].toString());
                break;
        }
    }

    private float getHighlightValue() {
        float highlighter = 1;

        if (FunctionStroke <= 1) {
            highlighter = 2.5F;
        } else if (FunctionStroke <= 4) {
            highlighter = 2F;
        } else {
            highlighter = 1.5F;
        }
        return highlighter;
    }

    private void OffsetSelectNone() {

        //SliderOffset.setEnabled(false);

        //MnuSelectCh1.setSelected(false);
        //MnuSelectCh2.setSelected(false);
        //MnuSelectCh3.setSelected(false);
        //MnuSelectCh4.setSelected(false);
        renderer.setSeriesStroke(0, new BasicStroke(FunctionStroke));
        renderer2.setSeriesStroke(0, new BasicStroke(FunctionStroke));
        renderer3.setSeriesStroke(0, new BasicStroke(FunctionStroke));
        renderer4.setSeriesStroke(0, new BasicStroke(FunctionStroke));
        offsetCh = 0;
        ignoreMouseMove = false;
    //RadioButOffsetNone.setSelected(true);
    }

    private void RangeSpinnerActionPerformed() {
        if (chkModifyBoth.isSelected()) {
            DomainSpinner.setValue(RangeSpinner.getValue());
        }
        newRangeStroke = Float.valueOf(RangeSpinner.getValue().toString());
        chartPlot.setRangeGridlineStroke(new BasicStroke(newRangeStroke));
        FFTplot.setRangeGridlineStroke(new BasicStroke(newRangeStroke));
    }

    private void ReadConfigFile() {
        File ConfigFile = null;
        FileReader fr = null;
        String TxtFileLine;

        LoadFrameDefaultSettings();//Default settings

        //SetChartDefaultValues();
        //ChartOptionsOKActionPerformed();
        if (USBFamily_Main.OS.equalsIgnoreCase("Windows")) {
            if (USBFamily_Main.Vista) {
                ConfigFile = new File(System.getProperty("user.home") + "/AppData/" + companyID + "/" + productID + " Java Software/" + productID + "Software.config");
            } else {
                ConfigFile = new File(System.getenv("ProgramFiles") + "/" + companyID + "/" + productID + " Java Software/" + productID + "Software.config");
            }
        } else if (USBFamily_Main.OS.equalsIgnoreCase("Linux")) {
            String ConfigFilelocation = System.getProperty("user.home") + "/." + productID + "Software.config";
            ConfigFile = new File(ConfigFilelocation);
        //ConfigFile = new File("/home/ana/USBscope50Software.config");
        }
        if (ConfigFile.exists() && ConfigFile.canRead() && ConfigFile.isFile()) {
            try {
                //read values
                fr = new FileReader(ConfigFile);
                BufferedReader br = new BufferedReader(fr);
                while ((TxtFileLine = br.readLine()) != null) {
                    if (TxtFileLine.length() <= 0 || TxtFileLine.startsWith("//")) { //empty line and comments ignored
                    } else {
                        if (ProcessConfigFileLine(TxtFileLine)) {
                            //abort
                            return;
                        }
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(USBscope50_Main.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    fr.close();
                } catch (IOException ex) {
                    Logger.getLogger(USBscope50_Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            intVoltsperDivPointer[1] = 8;//default is 50mV per division if nothing in config file
            intVoltsperDivPointer[2] = 8;
            intVoltsperDivPointer[3] = 8;
            intVoltsperDivPointer[4] = 8;
        }
    }

    private boolean ProcessConfigFileLine(String TxtFileLine) {
        boolean status = false;//returns false on success
        if (TxtFileLine.contains("FormSize")) {
            status = ReadFormSize(TxtFileLine);
        } else if (TxtFileLine.contains("FormLocation")) {
            status = ReadFormLocation(TxtFileLine);
        } else if (TxtFileLine.contains("SplitPaneDivider")) {
            status = ReadSplitDivider(TxtFileLine);
        } else if (TxtFileLine.contains("Font")) {
            //status = ReadFont(TxtFileLine);
        } else if (TxtFileLine.contains("TimeBase")) {
            status = ReadTimeBase(TxtFileLine);
        } else if (TxtFileLine.contains("VoltsBase")) {
            status = ReadVoltsBase(TxtFileLine);
        } else if (TxtFileLine.contains("Ch GND")) {
            status = ReadGNDSettings(TxtFileLine);
        } else if (TxtFileLine.contains("Ch Coupling")) {
            status = ReadCouplingSettings(TxtFileLine);
        } else if (TxtFileLine.contains("Ch Probe")) {
            status = ReadProbeSettings(TxtFileLine);
        } else if (TxtFileLine.contains("X-AxisLabelOn")) {
            status = ReadAxisSetting(TxtFileLine, 0);
        } else if (TxtFileLine.contains("Y-AxisLabelOn")) {
            status = ReadAxisSetting(TxtFileLine, 1);
        } else if (TxtFileLine.contains("TriggerChannel")) {
            status = ReadTriggerChannel(TxtFileLine);
        } else if (TxtFileLine.contains("TriggerMode")) {
            status = ReadTriggerMode(TxtFileLine);
        } else if (TxtFileLine.contains("TriggerType")) {
            status = ReadTriggerType(TxtFileLine);
        } else if (TxtFileLine.contains("TriggerLevel")) {
            status = ReadTriggerLevel(TxtFileLine);
        } else if (TxtFileLine.contains("Pretrigger")) {
            status = ReadPretrigger(TxtFileLine);
        } else if (TxtFileLine.contains("INV")) {
            status = ReadINVSettings(TxtFileLine);
        } else if (TxtFileLine.contains("ON")) {
            //status = ReadOnSettings;//no need to read this, will always be on on the start up
        } else if (TxtFileLine.contains("COMP")) {
            //status = ReadCOMPSettings;//no need to read this always off
        } else if (TxtFileLine.contains("FFT")) {
            status = ReadFFTSettings(TxtFileLine);
        } else if (TxtFileLine.contains("Offset")) {
            status = ReadOffset(TxtFileLine);
        } else if (TxtFileLine.contains("Ch1 Color")) {
            status = ReadChColor(1, TxtFileLine);
        } else if (TxtFileLine.contains("Ch2 Color")) {
            status = ReadChColor(2, TxtFileLine);
        } else if (TxtFileLine.contains("Ch3 Color")) {
            status = ReadChColor(3, TxtFileLine);
        } else if (TxtFileLine.contains("Ch4 Color")) {
            status = ReadChColor(4, TxtFileLine);
        } else if (TxtFileLine.contains("Background")) {
            status = ReadChColor(5, TxtFileLine);
        } else if (TxtFileLine.contains("GridLines")) {
            status = ReadChColor(6, TxtFileLine);
        } else if (TxtFileLine.contains("FunctionStroke")) {
            status = ReadStroke(1, TxtFileLine);
        } else if (TxtFileLine.contains("RangeStroke")) {
            status = ReadStroke(2, TxtFileLine);
        } else if (TxtFileLine.contains("DomainStroke")) {
            status = ReadStroke(3, TxtFileLine);
        } else if (TxtFileLine.contains("Window")) {
            status = ReadFFTWindow(TxtFileLine);
        } else if (TxtFileLine.contains("Plot")) {
            status = ReadFFTPlotType(TxtFileLine);
        } else if (TxtFileLine.contains("MenuAddHorizMarker")) {
            status = ReadMnuAddMarker(TxtFileLine, 1);//1 for horizontal
        } else if (TxtFileLine.contains("MenuAddVertiMarker")) {
            status = ReadMnuAddMarker(TxtFileLine, 2);//2 for vertical
        } else if (TxtFileLine.contains("HMarker1")) {
            status = ReadMarkersClickLocation(TxtFileLine, 1, 1);//1 for horizontal, 1 for marker 1
        } else if (TxtFileLine.contains("HMarker2")) {
            status = ReadMarkersClickLocation(TxtFileLine, 1, 2);//1 for horizontal, 2 for marker 2
        } else if (TxtFileLine.contains("chartY1")) {
            status = ReadMarkersXYLocation(TxtFileLine, 1, 1);//1 for horizontal(chartY) value, 1 for marker 1
        } else if (TxtFileLine.contains("chartY2")) {
            status = ReadMarkersXYLocation(TxtFileLine, 1, 2);//1 for horizontal(chartY) value, 2 for marker 2
        } else if (TxtFileLine.contains("VMarker1")) {
            status = ReadMarkersClickLocation(TxtFileLine, 2, 1);//2 for vertical, 2 for marker 1
        } else if (TxtFileLine.contains("VMarker2")) {
            status = ReadMarkersClickLocation(TxtFileLine, 2, 2);//2 for vertical, 2 for marker 2
        } else if (TxtFileLine.contains("chartX1")) {
            status = ReadMarkersXYLocation(TxtFileLine, 2, 1);
        } else if (TxtFileLine.contains("chartX2")) {
            status = ReadMarkersXYLocation(TxtFileLine, 2, 2);
        }
        return status;
    }

    private void LoadFrameDefaultSettings() {
        FormSizeX = FormSizeX_default;
        FormSizeY = FormSizeY_default;

        FormLocationX = FormLocationX_default;
        FormLocationY = FormLocationY_default;

        DividerLocation = DividerLocation_default;

        RangeGridColor = DefaultRangeGridColor;
        DomainGridColor = DefaultDomainGridColor;
        RangeStroke = DefaultRangeStroke;
        DomainStroke = DefaultDomainStroke;
        ChartBackgroundColor = ChartBackgroundDefaultColor;
        FunctionStroke = DefaultFunctionStroke;
    }

    private boolean ReadMarkersClickLocation(String TxtFileLine, int HorizontalVertical, int MarkerNum) { //1- Horizontal, 2 for vertical; MarkerNum can only be 1 or 2
        String tempString;
        int equal = TxtFileLine.indexOf("=");
        int semicolumn = TxtFileLine.indexOf(";");
        int comma = TxtFileLine.indexOf(",");
        if ((equal < 0) || (semicolumn < 0 || (equal > semicolumn) || comma < 0)) {
            return true;//abort
        }
        tempString = TxtFileLine.substring(equal + 1, semicolumn).trim();//tempString e.g. "215,215"
        comma = tempString.indexOf(",");
        if (comma > 0) {//found comma
            try {
                String strMarkerLocationX = tempString.substring(0, comma).trim();
                String strMarkerLocationY = tempString.substring(comma + 1, tempString.length()).trim();
                if (strMarkerLocationX.equalsIgnoreCase("null") || strMarkerLocationY.equalsIgnoreCase("null")) {
                } else {
                    if (HorizontalVertical == 1) {
                        tempNoRangeMarkersOnChart++;
                        HorizontalMarkerMouseClickLocation[tempNoRangeMarkersOnChart - 1][0] = Integer.parseInt(strMarkerLocationX);
                        HorizontalMarkerMouseClickLocation[tempNoRangeMarkersOnChart - 1][1] = Integer.parseInt(strMarkerLocationY);
                    } else {
                        tempNoDomainMarkersOnChart++;
                        VerticalMarkerMouseClickLocation[tempNoDomainMarkersOnChart - 1][0] = Integer.parseInt(strMarkerLocationX);
                        VerticalMarkerMouseClickLocation[tempNoDomainMarkersOnChart - 1][1] = Integer.parseInt(strMarkerLocationY);
                    }
                }
            } catch (NumberFormatException ex) {
                System.out.println("Error encountered while reading FormLocation from config file\n" + ex);
                return true;//abort- probably FormLocation = string, notOnlyNumbers;
            }
        }
        return false;

    }

    private boolean ReadFormLocation(String TxtFileLine) {
        String tempString;
        int equal = TxtFileLine.indexOf("=");
        int semicolumn = TxtFileLine.indexOf(";");
        int comma = TxtFileLine.indexOf(",");
        if ((equal < 0) || (semicolumn < 0 || (equal > semicolumn) || comma < 0)) {
            return true;//abort
        }
        tempString = TxtFileLine.substring(equal + 1, semicolumn).trim();//tempString e.g. "215,215"
        comma = tempString.indexOf(",");
        if (comma > 0) {//found comma
            try {
                String strFormLocationX = tempString.substring(0, comma).trim();
                String strFormLocationY = tempString.substring(comma + 1, tempString.length()).trim();
                if (strFormLocationX.equalsIgnoreCase("default")) {
                    FormLocationX = -1;//so we know to set location relative to null(centre of the screen)
                } else {
                    FormLocationX = Integer.parseInt(strFormLocationX);
                }
                if (strFormLocationY.equalsIgnoreCase("default")) {
                    FormLocationX = -1;//so we know to set location relative to null(centre of the screen)
                } else {
                    FormLocationY = Integer.parseInt(strFormLocationY);
                }
            } catch (NumberFormatException ex) {
                System.out.println("Error encountered while reading FormLocation from config file\n" + ex);
                return true;//abort- probably FormLocation = string, notOnlyNumbers;
            }
        }
        return false;
    }

    private boolean ReadFormSize(String TxtFileLine) {

        String tempString;
        int equal = TxtFileLine.indexOf("=");
        int semicolumn = TxtFileLine.indexOf(";");
        int comma = TxtFileLine.indexOf(",");
        WinExtState = "";

        if ((equal < 0) || (semicolumn < 0 || (equal > semicolumn))) {// || comma < 0)) {//decided not to check for comma, so MAX; is allowed
            return true;//abort
        }
        tempString = TxtFileLine.substring(equal + 1, semicolumn).trim();//tempString e.g. "850,453"

        if (tempString.startsWith("MAX")) {
            WinExtState = "MAX";//default settings already loaded in loaddefaultsettings
        } else if (tempString.startsWith("MIN")) {
            WinExtState = "MIN";
        } else {
            comma = tempString.indexOf(",");
            if (comma > 0) {//found comma
                try {
                    String strFormSizeX = tempString.substring(0, comma).trim();
                    String strFormSizeY = tempString.substring(comma + 1, tempString.length()).trim();
                    if (!(strFormSizeX.equalsIgnoreCase("default"))) {
                        FormSizeX = Integer.parseInt(strFormSizeX);
                    }
                    if (!(strFormSizeY.equalsIgnoreCase("default"))) {
                        FormSizeY = Integer.parseInt(strFormSizeY);
                    }

                } catch (NumberFormatException ex) {
                    System.out.println("Error encountered while reading FormSize from config file\n" + ex);
                    return true;//abort- probably FormSize = string, notOnlyNumbers;
                }
            }
        }
        return false;//we are good!

    }

    private boolean ReadSplitDivider(String TxtFileLine) {
        String tempString;
        int equal = TxtFileLine.indexOf("=");
        int semicolumn = TxtFileLine.indexOf(";");
        int comma = TxtFileLine.indexOf(",");//there shouldn't be comma in SplitDivider line

        if ((equal < 0) || (semicolumn < 0 || (equal > semicolumn) || comma >= 0)) {
            return true;//abort
        }
        tempString = TxtFileLine.substring(equal + 1, semicolumn).trim();//tempString e.g. "539"
        try {
            if (!(tempString.equalsIgnoreCase("default"))) {
                DividerLocation = Integer.parseInt(tempString);
            }
        } catch (NumberFormatException ex) {
            System.out.println("Error encountered while reading SplitPaneDivider from config file\n" + ex);
            return true;//abort- probably SplitPaneDivider = string;
        }

        return false;
    }

    private boolean ReadTimeBase(String TxtFileLine) {

        String tempString;
        int equal = TxtFileLine.indexOf("=");
        int semicolumn = TxtFileLine.indexOf(";");
        int comma = TxtFileLine.indexOf(",");//there shouldn't be comma in SplitDivider line

        if ((equal < 0) || (semicolumn < 0 || (equal > semicolumn) || comma >= 0)) {
            return true;//abort
        }

        tempString = TxtFileLine.substring(equal + 1, semicolumn).trim();
        int inttempString = Integer.parseInt(tempString);

        if (inttempString < TimeBaseSettings.length) {//should be 28 as there are 28 time base settings in the array
            intTimeBaseArrayPointer = inttempString;
        } else {
            intTimeBaseArrayPointer = 10;
        }
        return false;
    }

    private boolean ReadVoltsBase(String TxtFileLine) {
        String tempString;
        int equal = TxtFileLine.indexOf("=");
        int semicolumn = TxtFileLine.indexOf(";");
        int comma;// = TxtFileLine.indexOf(",");//there shouldn't be comma in SplitDivider line
        String tempVoltsPointer = "4";//default
        if ((equal < 0) || (semicolumn < 0 || (equal > semicolumn))) {
            return true;//abort
        }
        tempString = TxtFileLine.substring(equal + 1, semicolumn).trim();//10, 8, 4, 4
        comma = tempString.indexOf(",");

        if (comma > 0) {
            tempVoltsPointer = tempString.substring(0, comma).trim();
        } else {
            return true;//abort something wrong in this line
        }

        if ((Integer.parseInt(tempVoltsPointer)) < VoltsDivSettings.length) {
            intVoltsperDivPointer[1] = Integer.parseInt(tempVoltsPointer);//channel 1
        } else {
            return true;
        }

        tempString = tempString.substring(comma + 1).trim();
        comma = tempString.indexOf(",");

        if (comma > 0) {
            tempVoltsPointer = tempString.substring(0, comma).trim();
        } else {
            return true;
        }

        if ((Integer.parseInt(tempVoltsPointer)) < VoltsDivSettings.length) {
            intVoltsperDivPointer[2] = Integer.parseInt(tempVoltsPointer);//channel 2
        } else {
            return true;
        }

        tempString = tempString.substring(comma + 1).trim();
        comma = tempString.indexOf(",");

        if (comma > 0) {
            tempVoltsPointer = tempString.substring(0, comma).trim();
        } else {
            return true;
        }

        if ((Integer.parseInt(tempVoltsPointer)) < VoltsDivSettings.length) {
            intVoltsperDivPointer[3] = Integer.parseInt(tempVoltsPointer);//channel 3
        } else {
            return true;
        }

        tempString = tempString.substring(comma + 1).trim();
        if ((tempString.length() > 0) && (Integer.parseInt(tempVoltsPointer) < VoltsDivSettings.length)) {
            intVoltsperDivPointer[4] = Integer.parseInt(tempVoltsPointer);//channel 4
        } else {
            return true;
        }


        return false;
    }

    private boolean ReadGNDSettings(String TxtFileLine) {
        String tempString;
        int equal = TxtFileLine.indexOf("=");
        int semicolumn = TxtFileLine.indexOf(";");
        int comma;
        String tempGNDSetting = "0";//default false
        if ((equal < 0) || (semicolumn < 0 || (equal > semicolumn))) {
            return true;//abort
        }
        tempString = TxtFileLine.substring(equal + 1, semicolumn).trim();//0, 1, 0, 0
        comma = tempString.indexOf(",");

        if (comma > 0) {
            tempGNDSetting = tempString.substring(0, comma).trim();
        } else {
            return true;//abort something wrong in this line
        }

        if (Integer.parseInt(tempGNDSetting) > 0) {
            gnd_option[1] = 1;//Integer.parseInt(tempGNDSetting);//channel 1
        } else {
            gnd_option[1] = 0;
        }

        tempString = tempString.substring(comma + 1).trim();
        comma = tempString.indexOf(",");

        if (comma > 0) {
            tempGNDSetting = tempString.substring(0, comma).trim();
        } else {
            return true;
        }

        if (Integer.parseInt(tempGNDSetting) > 0) {
            gnd_option[2] = 1;//channel 2
        } else {
            gnd_option[2] = 0;
        }

        tempString = tempString.substring(comma + 1).trim();
        comma = tempString.indexOf(",");

        if (comma > 0) {
            tempGNDSetting = tempString.substring(0, comma).trim();
        } else {
            return true;
        }

        if (Integer.parseInt(tempGNDSetting) > 0) {
            gnd_option[3] = 1;//channel 3
        } else {
            gnd_option[3] = 0;
        }

        tempString = tempString.substring(comma + 1).trim();
        if ((tempString.length() > 0) && (Integer.parseInt(tempString) > 0)) {
            gnd_option[4] = 1;//channel 4
        } else {
            gnd_option[4] = 0;
        }

        return false;
    }

    private boolean ReadCouplingSettings(String TxtFileLine) {
        String tempString;
        int equal = TxtFileLine.indexOf("=");
        int semicolumn = TxtFileLine.indexOf(";");
        int comma;
        String tempCouplingSetting = "0";//default ac coupling
        if ((equal < 0) || (semicolumn < 0 || (equal > semicolumn))) {
            return true;//abort
        }
        tempString = TxtFileLine.substring(equal + 1, semicolumn).trim();//1, 0, 0, 1
        comma = tempString.indexOf(",");

        if (comma > 0) {
            tempCouplingSetting = tempString.substring(0, comma).trim();
        } else {
            return true;//abort something wrong in this line
        }

        if (Integer.parseInt(tempCouplingSetting) > 0) {
            Dc_option[1] = 1;//Integer.parseInt(tempGNDSetting);//channel 1
        } else {
            Dc_option[1] = 0;
        }

        tempString = tempString.substring(comma + 1).trim();
        comma = tempString.indexOf(",");

        if (comma > 0) {
            tempCouplingSetting = tempString.substring(0, comma).trim();
        } else {
            return true;
        }

        if (Integer.parseInt(tempCouplingSetting) > 0) {
            Dc_option[2] = 1;//channel 2
        } else {
            Dc_option[2] = 0;
        }

        tempString = tempString.substring(comma + 1).trim();
        comma = tempString.indexOf(",");

        if (comma > 0) {
            tempCouplingSetting = tempString.substring(0, comma).trim();
        } else {
            return true;
        }

        if (Integer.parseInt(tempCouplingSetting) > 0) {
            Dc_option[3] = 1;//channel 3
        } else {
            Dc_option[3] = 0;
        }

        tempString = tempString.substring(comma + 1).trim();
        if ((tempString.length() > 0) && (Integer.parseInt(tempString) > 0)) {
            Dc_option[4] = 1;//channel 4
        } else {
            Dc_option[4] = 0;
        }

        return false;
    }

    private boolean ReadProbeSettings(String TxtFileLine) {
        String tempString;
        int equal = TxtFileLine.indexOf("=");
        int semicolumn = TxtFileLine.indexOf(";");
        int comma;
        String tempProbeSetting = "1";//default probe x1
        if ((equal < 0) || (semicolumn < 0 || (equal > semicolumn))) {
            return true;//abort
        }
        tempString = TxtFileLine.substring(equal + 1, semicolumn).trim();//1, 2, 1, 2
        comma = tempString.indexOf(",");

        if (comma > 0) {
            tempProbeSetting = tempString.substring(0, comma).trim();
        } else {
            return true;//abort something wrong in this line
        }

        if (Integer.parseInt(tempProbeSetting) == 1) {
            Probe[1] = 1;
        } else if (Integer.parseInt(tempProbeSetting) == 2) {
            Probe[1] = 2;
        } else {
            return true;
        }

        tempString = tempString.substring(comma + 1).trim();
        comma = tempString.indexOf(",");

        if (comma > 0) {
            tempProbeSetting = tempString.substring(0, comma).trim();
        } else {
            return true;
        }

        if (Integer.parseInt(tempProbeSetting) == 1) {
            Probe[2] = 1;
        } else if (Integer.parseInt(tempProbeSetting) == 2) {
            Probe[2] = 2;
        } else {
            return true;
        }

        tempString = tempString.substring(comma + 1).trim();
        comma = tempString.indexOf(",");

        if (comma > 0) {
            tempProbeSetting = tempString.substring(0, comma).trim();
        } else {
            return true;
        }

        if (Integer.parseInt(tempProbeSetting) == 1) {
            Probe[3] = 1;
        } else if (Integer.parseInt(tempProbeSetting) == 2) {
            Probe[3] = 2;
        } else {
            return true;
        }

        tempString = tempString.substring(comma + 1).trim();
        if ((tempString.length() > 0) && (Integer.parseInt(tempString) == 1)) {
            Probe[4] = 1;//channel 4
        } else if ((tempString.length() > 0) && (Integer.parseInt(tempString) == 2)) {
            Probe[4] = 2;
        } else {
            return true;
        }

        return false;
    }

    private boolean ReadTriggerChannel(String TxtFileLine) {
        String tempString;
        int equal = TxtFileLine.indexOf("=");
        int semicolumn = TxtFileLine.indexOf(";");


        if ((equal < 0) || (semicolumn < 0 || (equal > semicolumn))) {
            return true;//abort
        }

        tempString = TxtFileLine.substring(equal + 1, semicolumn).trim();//4

        if ((Integer.parseInt(tempString) > 0) && (Integer.parseInt(tempString) < 5)) {//trigger channel 1 to 4
            TriggerChannel = Integer.parseInt(tempString);
        } else {
            return true;
        }

        return false;
    }

    private boolean ReadTriggerMode(String TxtFileLine) {
        String tempString;
        int equal = TxtFileLine.indexOf("=");
        int semicolumn = TxtFileLine.indexOf(";");


        if ((equal < 0) || (semicolumn < 0 || (equal > semicolumn))) {
            return true;//abort
        }

        tempString = TxtFileLine.substring(equal + 1, semicolumn).trim();//1//values 0 or 1

        if (Integer.parseInt(tempString) == 0) {
            triggermode = 0;
        } else if (Integer.parseInt(tempString) == 1) {
            triggermode = 1;
        } else {
            return true;
        }
        return false;
    }

    private boolean ReadPretrigger(String TxtFileLine) {
        String tempString;
        int equal = TxtFileLine.indexOf("=");
        int semicolumn = TxtFileLine.indexOf(";");


        if ((equal < 0) || (semicolumn < 0 || (equal > semicolumn))) {
            return true;//abort
        }

        tempString = TxtFileLine.substring(equal + 1, semicolumn).trim();

        if ((Integer.parseInt(tempString) >= 0) && (Integer.parseInt(tempString) <= 3072)) {
            initialPretrigger = Integer.parseInt(tempString);
        } else {
            return true;
        }
        return false;
    }

    private boolean ReadTriggerLevel(String TxtFileLine) {
        String tempString;
        int equal = TxtFileLine.indexOf("=");
        int semicolumn = TxtFileLine.indexOf(";");


        if ((equal < 0) || (semicolumn < 0 || (equal > semicolumn))) {
            return true;//abort
        }

        tempString = TxtFileLine.substring(equal + 1, semicolumn).trim();

        if ((Float.parseFloat(tempString) >= -100f) && (Float.parseFloat(tempString) <= 100f)) {
            initialThreshold = Float.parseFloat(tempString);
        } else {
            return true;
        }
        return false;
    }

    private boolean ReadTriggerType(String TxtFileLine) {

        String tempString;
        int equal = TxtFileLine.indexOf("=");
        int semicolumn = TxtFileLine.indexOf(";");


        if ((equal < 0) || (semicolumn < 0 || (equal > semicolumn))) {
            return true;//abort
        }

        tempString = TxtFileLine.substring(equal + 1, semicolumn).trim();//1//values 0 or 1

        if ((Integer.parseInt(tempString) >= 0) && (Integer.parseInt(tempString) < 4)) {//types 0,1,2 or 3
            currentTrigType = Integer.parseInt(tempString);
        } else {
            return true;
        }
        return false;
    }

    private boolean ReadINVSettings(String TxtFileLine) {
        String tempString;
        int equal = TxtFileLine.indexOf("=");
        int semicolumn = TxtFileLine.indexOf(";");
        int comma;
        String tempINVSetting = "1";//default signal not inverted
        if ((equal < 0) || (semicolumn < 0 || (equal > semicolumn))) {
            return true;//abort
        }
        tempString = TxtFileLine.substring(equal + 1, semicolumn).trim();//1, 2, 1, 2
        comma = tempString.indexOf(",");

        if (comma > 0) {
            tempINVSetting = tempString.substring(0, comma).trim();
        } else {
            return true;//abort something wrong in this line
        }

        if (Integer.parseInt(tempINVSetting) == -1) {
            INV[1] = -1;
        }

        tempString = tempString.substring(comma + 1).trim();
        comma = tempString.indexOf(",");

        if (comma > 0) {
            tempINVSetting = tempString.substring(0, comma).trim();
        } else {
            return true;
        }

        if (Integer.parseInt(tempINVSetting) == -1) {
            INV[2] = -1;
        }

        tempString = tempString.substring(comma + 1).trim();
        comma = tempString.indexOf(",");

        if (comma > 0) {
            tempINVSetting = tempString.substring(0, comma).trim();
        } else {
            return true;
        }

        if (Integer.parseInt(tempINVSetting) == -1) {
            INV[3] = -1;
        }

        tempString = tempString.substring(comma + 1).trim();
        if (Integer.parseInt(tempString) == -1) {
            INV[4] = -1;//channel 4
        } else if (Integer.parseInt(tempString) == 1) {
            INV[4] = 1;//channel 4
        } else {
            return true;
        }
        return false;


    }

    private boolean ReadChColor(int channel, String TxtFileLine) {
        String tempString;
        int equal = TxtFileLine.indexOf("=");
        int semicolumn = TxtFileLine.indexOf(";");

        if ((equal < 0) || (semicolumn < 0 || (equal > semicolumn))) {
            return true;//abort
        }

        tempString = TxtFileLine.substring(equal + 1, semicolumn).trim();

        if (tempString.startsWith("Color(")) {
            tempString = tempString.substring(6);
        } else {
            return true;
        }

        String Red;
        String Green;
        String Blue;
        Color ColorRead;
        int comma;

        comma = tempString.indexOf(",");
        if (comma <= 0) {
            return true;//abort
        }
        Red = tempString.substring(0, comma);

        tempString = tempString.substring(comma + 1);
        comma = tempString.indexOf(",");
        if (comma <= 0) {
            return true;//abort
        }
        Green = tempString.substring(0, comma);

        tempString = tempString.substring(comma + 1);
        Blue = tempString.substring(0, tempString.length() - 1);

        //String rgb = Integer.toHexString(ColorCh1.getRGB());
        //rgb = rgb.substring(2,rgb.length());
        //rgb = "0x" + rgb;
        //Integer.decode(rgb).intValue();
        try {
            ColorRead = new Color(Integer.parseInt(Red), Integer.parseInt(Green), Integer.parseInt(Blue));
        } catch (NumberFormatException numberFormatException) {//color value contains chars
            return true;//abort
        }
        switch (channel) {
            case 1:
                newColorCh1 = ColorRead;
                break;
            case 2:
                newColorCh2 = ColorRead;
                break;
            case 3:
                newColorCh3 = ColorRead;
                break;
            case 4:
                newColorCh4 = ColorRead;
                break;
            case 5:
                ChartBackgroundColor = ColorRead;
                break;
            case 6:
                RangeGridColor = ColorRead;
                DomainGridColor = ColorRead;
                break;
            default:
                newColorCh1 = DefaultColorCh1;
                newColorCh2 = DefaultColorCh2;
                newColorCh3 = DefaultColorCh3;
                newColorCh4 = DefaultColorCh4;
                ChartBackgroundColor = ChartBackgroundDefaultColor;
                RangeGridColor = DefaultRangeGridColor;
                DomainGridColor = DefaultDomainGridColor;
                break;
        }
        return false;//returns false on success
    }

    private boolean ReadStroke(int strokeType, String TxtFileLine) {

        String tempString;
        int equal = TxtFileLine.indexOf("=");
        int semicolumn = TxtFileLine.indexOf(";");
        float floattempString;
        //int comma = TxtFileLine.indexOf(",");//there shouldn't be comma in SplitDivider line

        if ((equal < 0) || (semicolumn < 0 || (equal > semicolumn))) {
            return true;//abort
        }

        tempString = TxtFileLine.substring(equal + 1, semicolumn).trim();
        try {
            floattempString = Float.parseFloat(tempString);
        } catch (NumberFormatException numberFormatException) {
            return true;//abort
        }

        switch (strokeType) {
            case 1:
                FunctionStroke = floattempString;
                break;
            case 2:
                RangeStroke = floattempString;
                break;
            case 3:
                DomainStroke = floattempString;
                break;
            default:
                FunctionStroke = DefaultFunctionStroke;
                RangeStroke = DefaultRangeStroke;
                DomainStroke = DefaultDomainStroke;
                break;
        }
        return false;
    }

    private void RecordSignalSamplePeriod() {
        SignalSamplePeriod = Double.parseDouble(TimeBaseSettings[intTimeBaseArrayPointer][5]);
    }

    private void SaveSettingInConfigFile() {
        //generate config file
        File ConfigFile = null;
        String ConfigFilelocation = "";

        try {
            if (USBFamily_Main.OS.equalsIgnoreCase("Windows")) {
                if (USBFamily_Main.Vista) {
                    ConfigFile = new File(System.getProperty("user.home") + "/AppData/" + companyID + "/" + productID + " Java Software");
                    if (ConfigFile.exists() && ConfigFile.isDirectory()) {
                    } else {
                        ConfigFile.mkdirs();
                    }

                    ConfigFile = new File(System.getProperty("user.home") + "/AppData/" + companyID + "/" + productID + " Java Software/" + productID + "Software.config");

                } else {
                    ConfigFile = new File(System.getenv("ProgramFiles") + "/" + companyID + "/" + productID + " Java Software/" + productID + "Software.config");
                }
            } else if (USBFamily_Main.OS.equalsIgnoreCase("Linux")) {
                ConfigFilelocation = System.getProperty("user.home");
                ConfigFile = new File(ConfigFilelocation);
                if (ConfigFile.exists() && ConfigFile.canWrite()) {
                    ConfigFilelocation = System.getProperty("user.home") + "/." + productID + "Software.config";
                    ConfigFile = new File(ConfigFilelocation);
                } else {
                    System.out.println("Permission denied - unable to save software settings.");
                    return;
                }
            }

            boolean configCreated = ConfigFile.createNewFile();
            if (configCreated) { //file created
                GenerateNewConfigFile(ConfigFile.getAbsolutePath());//Load the file
            } else {//file already exists- update the values
                //UpdateConfigFile(ConfigFile.getAbsolutePath());//Attempted to modify text file just by changing the values using RandomAccessFile modifyConfig
                //I found that really whole file is read, modified and then rewritten. So I might as well generate new one
                GenerateNewConfigFile(ConfigFile.getAbsolutePath());//Load the file
            }
        } catch (IOException ex) {
            // System.out.println("Permission denied. Unable to save settings.\nHave you got root permission?");
            Logger.getLogger(USBscope50_Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void SetCOMP(int intChannel, boolean selected) {

        int mode = selected ? 1 : 0;
        ChannelCalSourceOn[intChannel] = selected;//index 1->4
        t.USBscope50Drvr_SetCalSource(intChannel, mode);//porthandles index 1->4
    }

    private void SetChartDefaultValues() {
        newChartBackgroundColor = ChartBackgroundDefaultColor;
        newDomainGridColor = DefaultDomainGridColor;
        newRangeGridColor = DefaultRangeGridColor;
        //newFunctionStroke = DefaultFunctionStroke;
        //newDomainStroke = DefaultDomainStroke;
        //newRangeStroke = DefaultRangeStroke;
        FunctionSpinner.setValue(DefaultFunctionStroke);
        RangeSpinner.setValue(DefaultRangeStroke);
        DomainSpinner.setValue(DefaultDomainStroke);

        chartPlot.setRangeGridlinePaint(newRangeGridColor);
        chartPlot.setDomainGridlinePaint(newDomainGridColor);

        FFTplot.setRangeGridlinePaint(newRangeGridColor);
        FFTplot.setDomainGridlinePaint(newDomainGridColor);

        chartPlot.setRangeGridlineStroke(new BasicStroke(newRangeStroke));
        chartPlot.setDomainGridlineStroke(new BasicStroke(newDomainStroke));

        FFTplot.setRangeGridlineStroke(new BasicStroke(newRangeStroke));
        FFTplot.setDomainGridlineStroke(new BasicStroke(newDomainStroke));

        chartPlot.setBackgroundPaint(newChartBackgroundColor);

        FFTplot.setBackgroundPaint(newChartBackgroundColor);


        renderer.setSeriesStroke(0, new BasicStroke(newFunctionStroke));
        renderer2.setSeriesStroke(0, new BasicStroke(newFunctionStroke));
        renderer3.setSeriesStroke(0, new BasicStroke(newFunctionStroke));
        renderer4.setSeriesStroke(0, new BasicStroke(newFunctionStroke));
        renderer_FFT.setSeriesStroke(0, new BasicStroke(newFunctionStroke));
    }

    private void SetSeriesONState(int intChannel, boolean ONstate) {
        switch (intChannel) {
            case 1:
                renderer.setSeriesVisible(0, ONstate);
                renderer_FFT.setSeriesVisible(0, ONstate);
                axisY[1].setTickLabelsVisible(ONstate && MnuShowYAxisLabels.isSelected());
                axisY_FFT[1].setTickLabelsVisible(ONstate && MnuShowYAxisLabels.isSelected());
                break;
            case 2:
                renderer2.setSeriesVisible(0, ONstate);
                renderer2_FFT.setSeriesVisible(0, ONstate);
                axisY[2].setTickLabelsVisible(ONstate && MnuShowYAxisLabels.isSelected());
                axisY_FFT[2].setTickLabelsVisible(ONstate && MnuShowYAxisLabels.isSelected());
                break;
            case 3:
                renderer3.setSeriesVisible(0, ONstate);
                renderer3_FFT.setSeriesVisible(0, ONstate);
                axisY[3].setTickLabelsVisible(ONstate && MnuShowYAxisLabels.isSelected());
                axisY_FFT[3].setTickLabelsVisible(ONstate && MnuShowYAxisLabels.isSelected());
                break;
            default:
                renderer4.setSeriesVisible(0, ONstate);
                renderer4_FFT.setSeriesVisible(0, ONstate);
                axisY[4].setTickLabelsVisible(ONstate && MnuShowYAxisLabels.isSelected());
                axisY_FFT[4].setTickLabelsVisible(ONstate && MnuShowYAxisLabels.isSelected());
                break;
        }
    }

    private void SetSeriesFFTONState(int intChannel, boolean ONstate) {
        switch (intChannel) {
            case 1:
                renderer_FFT.setSeriesVisible(0, ONstate);
                axisY_FFT[1].setTickLabelsVisible(ONstate && MnuShowYAxisLabels.isSelected());
                FFT_Channel[1] = ONstate;
                break;
            case 2:
                renderer2_FFT.setSeriesVisible(0, ONstate);
                axisY_FFT[2].setTickLabelsVisible(ONstate && MnuShowYAxisLabels.isSelected());
                FFT_Channel[2] = ONstate;
                break;
            case 3:
                renderer3_FFT.setSeriesVisible(0, ONstate);
                axisY_FFT[3].setTickLabelsVisible(ONstate && MnuShowYAxisLabels.isSelected());
                FFT_Channel[3] = ONstate;
                break;
            default:
                renderer4_FFT.setSeriesVisible(0, ONstate);
                axisY_FFT[4].setTickLabelsVisible(ONstate && MnuShowYAxisLabels.isSelected());
                FFT_Channel[4] = ONstate;
                break;
        }
    }

    private boolean WannaCancelZoom() {

        String Msg = "\nBefore applying the changes" +
                "\nthe software will zoom to chart's normal size." +
                "\n\nWould you like to continue?\n\n";
        //JOptionPane optionPane = new JOptionPane();
        //Object msg[] = {"USBscope50 Software",WannaCancelZoomChkBox};
        //optionPane.setMessage(msg);
        //optionPane.setMessageType(JOptionPane.QUESTION_MESSAGE);
        //optionPane.setOptionType(JOptionPane.OK_CANCEL_OPTION);
        //JDialog dialog = optionPane.createDialog(Options.this,"not sure where this goes");
        //dialog.setVisible(true);


        int reply = JOptionPane.showConfirmDialog(this, Msg, productID + " Software", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (reply == JOptionPane.NO_OPTION) {
            return false;
        } else {
            MnuZoomCancelClicked();
        }
        return true;
    }

    private boolean areSeriesVisibile() {
        String strTab;
        boolean channelsPresent = false;//stop running if no channels present
        for (int tab = 0; tab < RightSplitPane.getTabCount(); tab++) {
            strTab = RightSplitPane.getTitleAt(tab);
            if (strTab.contains("Ch 1")) {
                MnuCh1.setSelected(true);
                SetSeriesONState(1, ONButtonCh1.isSelected());
                channelsPresent = true;
                lbVDivCh1.setVisible(true);
            } else if (strTab.contains("Ch 2")) {
                MnuCh2.setSelected(true);
                SetSeriesONState(2, ONButtonCh2.isSelected());
                channelsPresent = true;
                lbVDivCh2.setVisible(true);
            } else if (strTab.contains("Ch 3")) {
                MnuCh3.setSelected(true);
                SetSeriesONState(3, ONButtonCh3.isSelected());
                channelsPresent = true;
                lbVDivCh3.setVisible(true);
            } else if (strTab.contains("Ch 4")) {
                MnuCh4.setSelected(true);
                SetSeriesONState(4, ONButtonCh4.isSelected());
                channelsPresent = true;
                lbVDivCh4.setVisible(true);
            }
        }
        return channelsPresent;
    }

    private void doTabstoImport(int intImportToChannel, int intnewTimeBaseArrayPointer) {
        intTimeBaseArrayPointer = intnewTimeBaseArrayPointer;
        TimeBaseChangedEvent(intTimeBaseArrayPointer);
        OverallZoomRatio = 1;
        boolean booCheckTabTitles = CheckTabTitles("Ch " + intImportToChannel);

        if (!booCheckTabTitles) {//CheckTabTitles says there is no Ch intImportToChannel tab
            switch (intImportToChannel) {
                case 1:
                    RightSplitPane.addTab("Ch 1   ", ScrollOptionsCh1);
                    break;
                case 2:
                    RightSplitPane.addTab("Ch 2   ", ScrollOptionsCh2);
                    break;
                case 3:
                    RightSplitPane.addTab("Ch 3   ", ScrollOptionsCh3);
                    break;
                default:
                    RightSplitPane.addTab("Ch 4   ", ScrollOptionsCh4);
                    break;//default to channel 4, no real reason
            }
            OrganizeTabs("Ch " + intImportToChannel);
        }
        //There should be one now!
        int ImportToChannelTabIndex = CheckTabIndex("Ch " + intImportToChannel);
        if (ImportToChannelTabIndex == -1) {//this should never happen as we have just added tab Ch + intImportToChannel
        } else {
            RightSplitPane.setSelectedIndex(ImportToChannelTabIndex);
        }
    }

    public void OrganizeTabs(String TabInFocus) {
        /*scan through tabs and put them in the following order
         * Ch 1
         * Ch 2
         * Ch 3
         * Ch 4
         * Trigger
         * Zoom
         * add any other tabs to this list
         */

        int noTabs = RightSplitPane.getTabCount();
        boolean[] TabPresent = new boolean[10];//not sure yet how many tabs all together; noTabs would do too!

        int tolltipIndex = 0;
        TabPresent[0] = CheckTabTitles("Ch 1");//CheckTabTitles check it any of the tabs titles contain this string
        TabPresent[1] = CheckTabTitles("Ch 2");
        TabPresent[2] = CheckTabTitles("Ch 3");
        TabPresent[3] = CheckTabTitles("Ch 4");
        TabPresent[4] = CheckTabTitles("Trigger");
        TabPresent[5] = CheckTabTitles("Offset");
        TabPresent[6] = CheckTabTitles("Zoom");
        //TabPresent[7]=CheckTabTitles();
        //TabPresent[8]=CheckTabTitles();
        //TabPresent[9]=CheckTabTitles();

        boolean localJavaRunning = JavaRunning;
        boolean localSingleTrigger = t.SingleTrigger;
        RightSplitPane.removeAll();
        //clear 4 zero point channel annotations

        if (chartPlot.getAnnotations().contains(ZeroPointCh1)) {
            chartPlot.removeAnnotation(ZeroPointCh1);
        }

        if (chartPlot.getAnnotations().contains(ZeroPointCh2)) {
            chartPlot.removeAnnotation(ZeroPointCh2);
        }

        if (chartPlot.getAnnotations().contains(ZeroPointCh3)) {
            chartPlot.removeAnnotation(ZeroPointCh3);
        }

        if (chartPlot.getAnnotations().contains(ZeroPointCh4)) {
            chartPlot.removeAnnotation(ZeroPointCh4);
        }

        if (TabPresent[0]) {
            RightSplitPane.addTab("Ch 1   ", ScrollOptionsCh1);
            RightSplitPane.setToolTipTextAt(tolltipIndex, productID + " channel 1 settings");
            tolltipIndex++;
            MnuCh1.setSelected(true);
        //drawZeroPointChMarkers(0, true, false, false, false);
        } else {
            MnuCh1.setSelected(false);
        }

        if (TabPresent[1]) {
            RightSplitPane.addTab("Ch 2   ", ScrollOptionsCh2);
            RightSplitPane.setToolTipTextAt(tolltipIndex, productID + " channel 2 settings");
            tolltipIndex++;
            MnuCh2.setSelected(true);
        //drawZeroPointChMarkers(0, false, true, false, false);
        } else {
            MnuCh2.setSelected(false);
        }
        if (TabPresent[2]) {
            RightSplitPane.addTab("Ch 3   ", ScrollOptionsCh3);
            RightSplitPane.setToolTipTextAt(tolltipIndex, productID + " channel 3 settings");
            tolltipIndex++;
            MnuCh3.setSelected(true);
        //drawZeroPointChMarkers(0, false, false, true, false);
        } else {
            MnuCh3.setSelected(false);
        }
        if (TabPresent[3]) {
            RightSplitPane.addTab("Ch 4   ", ScrollOptionsCh4);
            RightSplitPane.setToolTipTextAt(tolltipIndex, productID + " channel 4 settings");
            tolltipIndex++;
            MnuCh4.setSelected(true);
        //drawZeroPointChMarkers(0, false, false, false, true);
        } else {
            MnuCh4.setSelected(false);
        }
        if (TabPresent[4]) {
            RightSplitPane.addTab("Trigger", ScrollTriggerPanel);
            RightSplitPane.setToolTipTextAt(tolltipIndex, productID + " trigger controls");
            tolltipIndex++;
        }
        drawZeroPointChMarkers(0, TabPresent[0], TabPresent[1], TabPresent[2], TabPresent[3]);
        /*if (TabPresent[5]) {
        RightSplitPane.addTab("Offset", ScrollOffsetPanel);
        RightSplitPane.setToolTipTextAt(tolltipIndex, "Offset controls");
        tolltipIndex++;
        }*/
        if (TabPresent[6]) {
            RightSplitPane.addTab("Zoom", ScrollCustomZoomPane);
            RightSplitPane.setToolTipTextAt(tolltipIndex, "Graph zoom controls");
            tolltipIndex++;
        }
        if (noTabs != RightSplitPane.getTabCount()) {
            System.out.println("Sorry, I must have made a mistake while rearranging the tabs.\nPlease restart this application.");
        }

        if (TabInFocus.length() > 0) {
            int TabIndex = CheckTabIndex(TabInFocus);
            if (TabIndex > 0) {
                RightSplitPane.setSelectedIndex(TabIndex);
            }
        } else {

            if (RightSplitPane.getTabCount() > 0) {
                RightSplitPane.setSelectedIndex(0);
            }
        }
        t.SingleTrigger = localSingleTrigger;
        if (localJavaRunning) {
            MnuRunGraphClicked(); //JavaRunning=true set within
        }
    }

    private void MakeThisTrigMaster(int intChannel) {
        int tempSliderThresholdValue = SliderThreshold.getValue();
        for (int i = 1; i <= 4; i++) {
            t.USBscope50Drvr_SetTrigMaster(i, 0);
        }
        TriggerChannel = (intChannel);
        t.USBscope50Drvr_SetTrigMaster(TriggerChannel, 1);
        SliderThreshold.setValue(tempSliderThresholdValue + 1);
        SliderThreshold.setValue(tempSliderThresholdValue);
        if (triggermode == 0) {//free            
            ThresholdMarkerOn = false;

        }
    }

    private void MnuRunClicked() {

        t.SingleTrigger = false;
        //ChartZoom(false);
        MnuRunGraphClicked();
    }

    private void MnuSingleClicked() {
        MnuStopClicked();
        t.SingleTrigger = true;
        MnuRunGraphClicked();
    }

    private void MnuTriggerOptionsClicked() {
        boolean TriggerOptionsFormVisible = false; // = TriggerOptionsDialog.isVisible();
        boolean booCheckTabTitles = CheckTabTitles("Trigger");

        if (!(TriggerOptionsFormVisible || booCheckTabTitles)) {
            RightSplitPane.addTab("Trigger", ScrollTriggerPanel);

        /*DECIDED NOT TO DO IT THIS WAY 02.05.2008 ANA
        TriggerDialog.setTitle("Graph Trigger Otions");
        TriggerDialog.setSize(CustomZoomPanelWidth, CustomZoomPanelWidth + 260);
        TriggerDialog.setResizable(false);
        TriggerDialog.setLocationRelativeTo(TriggerOptionsToolBarButton);
        TriggerDialog.setLocation(TriggerDialog.getX() + 150, TriggerDialog.getY() + 200);
        TriggerDialog.setVisible(true);
        RadioButTrigOnChan5.setSelected(true);
        TriggerModeFree1.setSelected(true);
        //TriggerModeRisingEdge1
         * */
        }
        OrganizeTabs("Trigger");//selects trigger tab
        //make tab visible, set divider somewhere

        //System.out.println(SplitPane.getWidth());
        //System.out.println(SplitPane.getDividerLocation());
        //System.out.println(SplitPane.getDividerSize());
        if (SplitPane.getDividerLocation() + SplitPane.getDividerSize() == SplitPane.getWidth()) {
            SplitPane.setDividerLocation(-1); //negative value is a default JSplitPane divider setting
        }
    }

    private void MnuZoomCancelClicked() {
        int[] mult = new int[5];
        int i;

        for (i = 1; i < 5; i++) {
            mult[i] = 1;
            if (Probe[i] == 2) {
                mult[i] = 10;
            }
        }
        RedrawTickUnits(1, true, intVoltsperDivPointer[1], false, 0);
        RedrawTickUnits(2, true, intVoltsperDivPointer[2], false, 0);
        RedrawTickUnits(3, true, intVoltsperDivPointer[3], false, 0);
        RedrawTickUnits(4, true, intVoltsperDivPointer[4], true, 0);
        ChartZoomed = false;


        if (CheckTabTitles("Ch 1")) {
            chartPlot.removeAnnotation(ZeroPointCh1);
        }
        if (CheckTabTitles("Ch 2")) {
            chartPlot.removeAnnotation(ZeroPointCh2);
        }
        if (CheckTabTitles("Ch 3")) {
            chartPlot.removeAnnotation(ZeroPointCh3);
        }
        if (CheckTabTitles("Ch 4")) {
            chartPlot.removeAnnotation(ZeroPointCh4);
        }
        ZeroPointCh1Value = (mult[1] * YDiv[1] * 5) * OffsetSliderCh1.getValue() / 10000;
        ZeroPointCh2Value = (mult[2] * YDiv[1] * 5) * OffsetSliderCh2.getValue() / 10000;
        ZeroPointCh3Value = (mult[3] * YDiv[1] * 5) * OffsetSliderCh3.getValue() / 10000;
        ZeroPointCh4Value = (mult[4] * YDiv[1] * 5) * OffsetSliderCh4.getValue() / 10000;
        SoftwareOffset[1] = OffsetSliderCh1.getValue() * YDiv[1] * 5 / 10000;// * INV[1];
        drawZeroPointChMarkers(0, CheckTabTitles("Ch 1"), CheckTabTitles("Ch 2"), CheckTabTitles("Ch 3"), CheckTabTitles("Ch 4"));


        if (dblFFTPlotType == 0) {
            Range FFTRange = new Range(-60.0, 10.0);//??? THIS NEEDS CHANGING
            axisY_FFT[1].setRange(FFTRange, true, true);
            axisY_FFT[2].setRange(FFTRange, true, true);
            axisY_FFT[3].setRange(FFTRange, true, true);
            axisY_FFT[4].setRange(FFTRange, true, true);
        }

    }

    private void MnuZoomCustomClicked() {
        boolean ZoomCustomFormVisible = false;//CustomZoomDialog.isVisible();
        boolean booCheckTabTitles = CheckTabTitles("Zoom");

        if (!(ZoomCustomFormVisible || booCheckTabTitles)) {
            RightSplitPane.addTab("Zoom", ScrollCustomZoomPane);
        /*NOT DOING IT THIS WAY 02.05.2008 ANA
        CustomZoomDialog.setTitle("Custom Zoom Options");
        CustomZoomDialog.setSize(CustomZoomPanelWidth + 20, CustomZoomPanelWidth + 120);
        CustomZoomDialog.setResizable(false);
        CustomZoomDialog.setLocationRelativeTo(ZoomCancelToolBarButton);
        CustomZoomDialog.setLocation(CustomZoomDialog.getX() + 150, CustomZoomDialog.getY() + 170);
        CustomZoomDialog.setVisible(true);
        ((JSpinner.DefaultEditor) XAxisZoomValue1.getEditor()).getTextField().grabFocus();
         */
        }
        OrganizeTabs("Zoom");//selects zoom tab
    }

    private void MnuZoomInClicked() {
        chartPanel.zoomInDomain(0, 0);
        ChartZoomed = true;
    }

    private void MnuZoomOutClicked() {
        chartPanel.zoomOutDomain(0, 0);
        ChartZoomed = true;
    }

    private void ModifyChartPopupMenu() {

        JPopupMenu chartPopup = chartPanel.getPopupMenu();

        //chartPopup.remove(3);   //removes separator
        chartPopup.remove(2); //removes Save as...

        chartPopup.remove(1); //removes separator

        chartPopup.remove(0); //removes properties

        chartPopup.remove(chartPopup.getComponentCount() - 1); //removed last item auto range

        chartPopup.remove(chartPopup.getComponentCount() - 1); //removed last item separator

        JMenuItem SaveAspopup = new JMenuItem("Save Graph As...");
        SaveAspopup.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveChartAs();
            }
        });
        chartPopup.add(SaveAspopup, 0); //add at the top
        //also add export graph data to popup menu

        JMenuItem ExportGraphDatapopup = new JMenuItem("Export Graph Data");
        ExportGraphDatapopup.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                //save graph points in a txt file
                SaveGraphPointsInTxtFile();
            }
        });
        chartPopup.add(ExportGraphDatapopup, 1);
    }

    private void OKXAxisCustomZoomAction(int ZoomValue) {
        //Zoom X-Axis
        if (ZoomValue < 100) {
            //zoom in
            ZoomValue = 100 - ZoomValue;
            axisX.zoomRange((double) ZoomValue / 200, (double) (100 - (ZoomValue / 2)) / 100);
        } else if (ZoomValue == 100) { //100% do nothing
        } else {
            //zoom out
            axisX.zoomRange(-(double) ((ZoomValue - 100) / 2) / 100, (double) (ZoomValue - ((ZoomValue - 100) / 2)) / 100);
        }

    //chartZoomed = true;
    }

    private void OKYAxisCustomZoomAction(int ZoomValue) {
        //int ZoomValue = Integer.valueOf(YAxisZoomValue1.getValue().toString());
        //Zoom Y-Axis
        if (ZoomValue < 100) {
            //zoom in
            ZoomValue = 100 - ZoomValue;
            for (int i = 1; i <= 4; i++) {
                axisY[i].zoomRange((double) ZoomValue / 200, (double) (100 - (ZoomValue / 2)) / 100);
            }
        } else if (ZoomValue == 100) { //100% do nothing
        } else {
            //zoom out
            for (int i = 1; i <= 4; i++) {
                axisY[i].zoomRange(-(double) ((ZoomValue - 100) / 2) / 100, (double) (ZoomValue - ((ZoomValue - 100) / 2)) / 100);
            }
        }
    }

    private void OpenInitializeScopes() {

        for (int tempChannel = 1; tempChannel <= numScopesFound; tempChannel++) {
            if (tempChannel == MasterChannel) {// 1 is master channel
                t.USBscope50Drvr_InitScope(tempChannel, 1);
            } else {
                t.USBscope50Drvr_InitScope(tempChannel, 0);
            }

            rismax[tempChannel] = t.GetRISmin(tempChannel);
            rismin[tempChannel] = t.GetRISmax(tempChannel);

            triggerDACoffset[tempChannel] = t.GetTriggerDACOffset(tempChannel);
            rawchanneloffset[tempChannel] = t.GetRawChannelOffset(tempChannel);
            offsetDACscale[tempChannel] = t.GetOffsetDACScale(tempChannel);
        }
    }

    private void PopulateTimeBaseSettingsArray() {
        TimeBaseSettings[0][0] = "  4 ns/div";
        TimeBaseSettings[1][0] = " 10 ns/div";
        TimeBaseSettings[2][0] = " 20 ns/div";
        TimeBaseSettings[3][0] = " 40 ns/div";
        TimeBaseSettings[4][0] = "100 ns/div";
        TimeBaseSettings[5][0] = "200 ns/div";
        TimeBaseSettings[6][0] = "400 ns/div";
        TimeBaseSettings[7][0] = "  1 us/div";
        TimeBaseSettings[8][0] = "  2 us/div";
        TimeBaseSettings[9][0] = "  4 us/div";
        TimeBaseSettings[10][0] = " 10 us/div";
        TimeBaseSettings[11][0] = " 20 us/div";
        TimeBaseSettings[12][0] = " 40 us/div";
        TimeBaseSettings[13][0] = "100 us/div";
        TimeBaseSettings[14][0] = "200 us/div";
        TimeBaseSettings[15][0] = "400 us/div";
        TimeBaseSettings[16][0] = "  1 ms/div";
        TimeBaseSettings[17][0] = "  2 ms/div";
        TimeBaseSettings[18][0] = "  4 ms/div";
        TimeBaseSettings[19][0] = " 10 ms/div";
        TimeBaseSettings[20][0] = " 20 ms/div";
        TimeBaseSettings[21][0] = " 40 ms/div";
        TimeBaseSettings[22][0] = "100 ms/div";
        TimeBaseSettings[23][0] = "200 ms/div";
        TimeBaseSettings[24][0] = "400 ms/div";
        TimeBaseSettings[25][0] = "  1  s/div";
        TimeBaseSettings[26][0] = "  2  s/div";
        TimeBaseSettings[27][0] = "  4  s/div";

        //sec per division settings for the graph
        TimeBaseSettings[0][3] = "0.000004";
        TimeBaseSettings[1][3] = "0.000010";
        TimeBaseSettings[2][3] = "0.000020";
        TimeBaseSettings[3][3] = "0.000040";
        TimeBaseSettings[4][3] = "0.000100";
        TimeBaseSettings[5][3] = "0.000200";
        TimeBaseSettings[6][3] = "0.000400";
        TimeBaseSettings[7][3] = "0.001";
        TimeBaseSettings[8][3] = "0.002";
        TimeBaseSettings[9][3] = "0.004";
        TimeBaseSettings[10][3] = "0.010";
        TimeBaseSettings[11][3] = "0.020";
        TimeBaseSettings[12][3] = "0.040";
        TimeBaseSettings[13][3] = "0.1";
        TimeBaseSettings[14][3] = "0.2";
        TimeBaseSettings[15][3] = "0.4";
        TimeBaseSettings[16][3] = "1";
        TimeBaseSettings[17][3] = "2";
        TimeBaseSettings[18][3] = "4";
        TimeBaseSettings[19][3] = "10";
        TimeBaseSettings[20][3] = "20";
        TimeBaseSettings[21][3] = "40";
        TimeBaseSettings[22][3] = "100";
        TimeBaseSettings[23][3] = "200";
        TimeBaseSettings[24][3] = "400";
        TimeBaseSettings[25][3] = "1000";
        TimeBaseSettings[26][3] = "2000";
        TimeBaseSettings[27][3] = "4000";


        TimeBaseSettings[0][1] = "50";
        TimeBaseSettings[1][1] = "50";
        TimeBaseSettings[2][1] = "50";
        TimeBaseSettings[3][1] = "50";
        TimeBaseSettings[4][1] = "50";
        TimeBaseSettings[5][1] = "50";
        TimeBaseSettings[6][1] = "50";
        TimeBaseSettings[7][1] = "50";
        TimeBaseSettings[8][1] = "50";
        TimeBaseSettings[9][1] = "25";
        TimeBaseSettings[10][1] = "10";
        TimeBaseSettings[11][1] = "10";
        TimeBaseSettings[12][1] = "10";
        TimeBaseSettings[13][1] = "10";
        TimeBaseSettings[14][1] = "10";
        TimeBaseSettings[15][1] = "10";
        TimeBaseSettings[16][1] = "10";
        TimeBaseSettings[17][1] = "10";
        TimeBaseSettings[18][1] = "10";
        TimeBaseSettings[19][1] = "10";
        TimeBaseSettings[20][1] = "10";
        TimeBaseSettings[21][1] = "10";
        TimeBaseSettings[22][1] = "10";
        TimeBaseSettings[23][1] = "10";
        TimeBaseSettings[24][1] = "10";
        TimeBaseSettings[25][1] = "10";
        TimeBaseSettings[26][1] = "10";
        TimeBaseSettings[27][1] = "10";


        TimeBaseSettings[0][2] = "1";
        TimeBaseSettings[1][2] = "1";
        TimeBaseSettings[2][2] = "1";
        TimeBaseSettings[3][2] = "1";
        TimeBaseSettings[4][2] = "1";
        TimeBaseSettings[5][2] = "1";
        TimeBaseSettings[6][2] = "1";
        TimeBaseSettings[7][2] = "1";
        TimeBaseSettings[8][2] = "1";
        TimeBaseSettings[9][2] = "1";
        TimeBaseSettings[10][2] = "1";
        TimeBaseSettings[11][2] = "2";
        TimeBaseSettings[12][2] = "4";
        TimeBaseSettings[13][2] = "10";
        TimeBaseSettings[14][2] = "20";
        TimeBaseSettings[15][2] = "40";
        TimeBaseSettings[16][2] = "100";
        TimeBaseSettings[17][2] = "200";
        TimeBaseSettings[18][2] = "400";
        TimeBaseSettings[19][2] = "1000";
        TimeBaseSettings[20][2] = "2000";
        TimeBaseSettings[21][2] = "4000";
        TimeBaseSettings[22][2] = "10000";
        TimeBaseSettings[23][2] = "20000";
        TimeBaseSettings[24][2] = "40000";
        TimeBaseSettings[25][2] = "100000";
        TimeBaseSettings[26][2] = "200000";
        TimeBaseSettings[27][2] = "400000";


        TimeBaseSettings[0][4] = "0";
        TimeBaseSettings[1][4] = "0";
        TimeBaseSettings[2][4] = "0";
        TimeBaseSettings[3][4] = "0";
        TimeBaseSettings[4][4] = "0";
        TimeBaseSettings[5][4] = "0";
        TimeBaseSettings[6][4] = "0";
        TimeBaseSettings[7][4] = "0";
        TimeBaseSettings[8][4] = "0";
        TimeBaseSettings[9][4] = "1";
        TimeBaseSettings[10][4] = "3";
        TimeBaseSettings[11][4] = "7";
        TimeBaseSettings[12][4] = "11";
        TimeBaseSettings[13][4] = "19";
        TimeBaseSettings[14][4] = "23";
        TimeBaseSettings[15][4] = "27";
        TimeBaseSettings[16][4] = "35";
        TimeBaseSettings[17][4] = "39";
        TimeBaseSettings[18][4] = "43";
        TimeBaseSettings[19][4] = "51";
        TimeBaseSettings[20][4] = "55";
        TimeBaseSettings[21][4] = "59";
        TimeBaseSettings[22][4] = "67";
        TimeBaseSettings[23][4] = "71";
        TimeBaseSettings[24][4] = "75";
        TimeBaseSettings[25][4] = "83";
        TimeBaseSettings[26][4] = "87";
        TimeBaseSettings[27][4] = "91";

        //these values used when timebase changed and scope not running;zoom to mimic different time base
        TimeBaseSettings[0][5] = "0.00000000004";//*
        TimeBaseSettings[1][5] = "0.0000000001";//*
        TimeBaseSettings[2][5] = "0.0000000002";//*
        TimeBaseSettings[3][5] = "0.0000000004";//*
        TimeBaseSettings[4][5] = "0.000000001";
        TimeBaseSettings[5][5] = "0.000000002";//*
        TimeBaseSettings[6][5] = "0.000000004";//*
        TimeBaseSettings[7][5] = "0.00000001";//*not true value, but modified for ease of data presentation
        TimeBaseSettings[8][5] = "0.00000002";
        TimeBaseSettings[9][5] = "0.00000004";//4x10-8
        TimeBaseSettings[10][5] = "0.0000001";
        TimeBaseSettings[11][5] = "0.0000002";
        TimeBaseSettings[12][5] = "0.0000004";
        TimeBaseSettings[13][5] = "0.000001";
        TimeBaseSettings[14][5] = "0.000002";
        TimeBaseSettings[15][5] = "0.000004";
        TimeBaseSettings[16][5] = "0.00001";
        TimeBaseSettings[17][5] = "0.00002";
        TimeBaseSettings[18][5] = "0.00004";
        TimeBaseSettings[19][5] = "0.0001";
        TimeBaseSettings[20][5] = "0.0002";
        TimeBaseSettings[21][5] = "0.0004";
        TimeBaseSettings[22][5] = "0.001";
        TimeBaseSettings[23][5] = "0.002";
        TimeBaseSettings[24][5] = "0.004";
        TimeBaseSettings[25][5] = "0.01";
        TimeBaseSettings[26][5] = "0.02";
        TimeBaseSettings[27][5] = "0.04";
    }

    private void PopulateVoltsDivSettingsArray() {

        VoltsDivSettings[0][0] = "0.002";
        VoltsDivSettings[1][0] = "0.005";
        VoltsDivSettings[2][0] = "0.01";
        VoltsDivSettings[3][0] = "0.02";
        VoltsDivSettings[4][0] = "0.05";
        VoltsDivSettings[5][0] = "0.1";
        VoltsDivSettings[6][0] = "0.2";
        VoltsDivSettings[7][0] = "0.5";
        VoltsDivSettings[8][0] = "1";
        VoltsDivSettings[9][0] = "2";
        VoltsDivSettings[10][0] = "5";
        VoltsDivSettings[11][0] = "10";

        VoltsDivSettings[0][1] = "  2 mV/div";
        VoltsDivSettings[1][1] = "  5 mV/div";
        VoltsDivSettings[2][1] = " 10 mV/div";
        VoltsDivSettings[3][1] = " 20 mV/div";
        VoltsDivSettings[4][1] = " 50 mV/div";
        VoltsDivSettings[5][1] = "100 mV/div";
        VoltsDivSettings[6][1] = "200 mV/div";
        VoltsDivSettings[7][1] = "500 mV/div";
        VoltsDivSettings[8][1] = "   1 V/div";
        VoltsDivSettings[9][1] = "   2 V/div";
        VoltsDivSettings[10][1] = "   5 V/div";
        VoltsDivSettings[11][1] = "  10 V/div";

        VoltsDivSettings[0][2] = " 20 mV/div";
        VoltsDivSettings[1][2] = " 50 mV/div";
        VoltsDivSettings[2][2] = "100 mV/div";
        VoltsDivSettings[3][2] = "200 mV/div";
        VoltsDivSettings[4][2] = "500 mV/div";
        VoltsDivSettings[5][2] = "   1 V/div";
        VoltsDivSettings[6][2] = "   2 V/div";
        VoltsDivSettings[7][2] = "   5 V/div";
        VoltsDivSettings[8][2] = "  10 V/div";
        VoltsDivSettings[9][2] = "  20 V/div";
        VoltsDivSettings[10][2] = "  50 V/div";
        VoltsDivSettings[11][2] = "100 V/div";

    }

    private void PrintOutArrays() {
        /* show the arrays data int he debug screen*/
        /* this array not filled on windows, so returns null pointer exception
         * on linux i need 3 parameters (ttyUSB, 1-1:1-0,usbscope50)
         * on windows i need com, usbscope50
        System.out.println("");
        System.out.println("gUSBTandMDevices contents");
        System.out.println("---------------------------");
        for (int j = 0; j < numIntsFound; j++) {
        for (int i = 0; i < 3; i++) {
        System.out.print(gUSBTandMDevices[i][j]);
        System.out.print("  |");
        }
        System.out.println("");
        System.out.println("---------------------------");
        }
         */
        System.out.println("");
        System.out.println("gUSBscope50Devices contents");
        System.out.println("---------------------------");
        //System.out.println(gUSBTandMDevices.length);
        for (int j = 1; j <= numScopesFound; j++) {
            for (int i = 0; i < 2; i++) {
                System.out.print(gUSBscope50Devices[i][j]);
                System.out.print("  |");
            }

            System.out.println("");
            System.out.println("---------------------------");
        }

        System.out.println("");
    }

    private void RedrawTickUnits(int intChannel, boolean VoltsTicks, int intVoltsperDivPointer, boolean TimeTicks, double Ratio) {

        double PointsPerDiv = 100;
        double PointsPerDivMod = 1.0;
        int[] mult = new int[5];
        int i;
        double FFTLabelAdjust = 1.00;
        double oldYDiv = 0;

        for (i = 1; i < 5; i++) {
            mult[i] = 1;
            if (Probe[i] == 2) {
                mult[i] = 10;
            }
        }

        if (VoltsTicks) {
            oldYDiv = YDiv[intChannel];

            YDiv[intChannel] = Double.parseDouble(VoltsDivSettings[intVoltsperDivPointer][0]);//this needs to stay 0
            axisY[intChannel].setRange(-YDiv[intChannel] * 5 * mult[intChannel], YDiv[intChannel] * 5 * mult[intChannel]);
            if (USBscope50_Main.dblFFTPlotType == 1) { //0 is linear
                axisY_FFT[intChannel].setRange(0.0, YDiv[intChannel] * 5);
            }

            axisY[intChannel].setTickUnit(new NumberTickUnit(YDiv[intChannel] * mult[intChannel]), true, true);
            if (USBscope50_Main.dblFFTPlotType == 1) { //0 is linear
                axisY_FFT[intChannel].setTickUnit(new NumberTickUnit(YDiv[intChannel]), true, true);
            }


            if (intChannel == 1) {
                chartPlot.removeAnnotation(ZeroPointCh1);
                if (CheckTabTitles("Ch 2")) {
                    chartPlot.removeAnnotation(ZeroPointCh2);
                }
                if (CheckTabTitles("Ch 3")) {
                    chartPlot.removeAnnotation(ZeroPointCh3);
                }
                if (CheckTabTitles("Ch 4")) {
                    chartPlot.removeAnnotation(ZeroPointCh4);
                }
                ZeroPointCh1Value = (mult[1] * YDiv[1] * 5) * OffsetSliderCh1.getValue() / 10000;
                ZeroPointCh2Value = (mult[2] * YDiv[1] * 5) * OffsetSliderCh2.getValue() / 10000;
                ZeroPointCh3Value = (mult[3] * YDiv[1] * 5) * OffsetSliderCh3.getValue() / 10000;
                ZeroPointCh4Value = (mult[4] * YDiv[1] * 5) * OffsetSliderCh4.getValue() / 10000;
                SoftwareOffset[1] = OffsetSliderCh1.getValue() * YDiv[1] * 5 / 10000;// * INV[1];
                drawZeroPointChMarkers(0, true, CheckTabTitles("Ch 2"), CheckTabTitles("Ch 3"), CheckTabTitles("Ch 4"));
            }

            if (intChannel == 2) {
                chartPlot.removeAnnotation(ZeroPointCh2);
                SoftwareOffset[2] = OffsetSliderCh2.getValue() * YDiv[2] * 5 / 10000;// * INV[2];
                drawZeroPointChMarkers(0, false, CheckTabTitles("Ch 2"), false, false);
            }
            if (intChannel == 3) {
                chartPlot.removeAnnotation(ZeroPointCh3);
                SoftwareOffset[3] = OffsetSliderCh3.getValue() * YDiv[3] * 5 / 10000;// * INV[3];
                drawZeroPointChMarkers(0, false, false, CheckTabTitles("Ch 3"), false);
            }
            if (intChannel == 4) {
                chartPlot.removeAnnotation(ZeroPointCh4);
                SoftwareOffset[4] = OffsetSliderCh4.getValue() * YDiv[4] * 5 / 10000;// * INV[4];
                drawZeroPointChMarkers(0, false, false, false, CheckTabTitles("Ch 4"));
            }
            if (!demoMode) {
                //if (!JavaRunning && FirstStopped && intChannel == 1) {//redraw channel
                if ((FirstStopped || SoftwareLoaded) && intChannel == 1 && !seriesCh1.isEmpty()) {//redraw channel
                    for (i = 0; i < 2999; i++) {
                        seriesCh1.updateByIndex(i, (seriesCh1.getDataItem(i).getYValue() - (oldYDiv * 5 * OffsetSliderCh1.getValue() / 10000) + (YDiv[1] * 5 * OffsetSliderCh1.getValue() / 10000)));
                    }
                }

                //if (!JavaRunning && FirstStopped && intChannel == 2) {//redraw channel
                if ((FirstStopped || SoftwareLoaded) && intChannel == 2 && !seriesCh2.isEmpty()) {//redraw channel
                    for (i = 0; i < 2999; i++) {
                        seriesCh2.updateByIndex(i, (seriesCh2.getDataItem(i).getYValue() - (oldYDiv * 5 * OffsetSliderCh2.getValue() / 10000) + (YDiv[2] * 5 * OffsetSliderCh2.getValue() / 10000)));
                    }
                }

                if ((FirstStopped || SoftwareLoaded) && intChannel == 3 && !seriesCh3.isEmpty()) {//redraw channel
                    for (i = 0; i < 2999; i++) {
                        seriesCh3.updateByIndex(i, (seriesCh3.getDataItem(i).getYValue() - (oldYDiv * 5 * OffsetSliderCh3.getValue() / 10000) + (YDiv[3] * 5 * OffsetSliderCh3.getValue() / 10000)));
                    }
                }

                if ((FirstStopped || SoftwareLoaded) && intChannel == 4 && !seriesCh4.isEmpty()) {//redraw channel
                    for (i = 0; i < 2999; i++) {
                        seriesCh4.updateByIndex(i, (seriesCh4.getDataItem(i).getYValue() - (oldYDiv * 5 * OffsetSliderCh4.getValue() / 10000) + (YDiv[4] * 5 * OffsetSliderCh4.getValue() / 10000)));
                    }
                }
            } else {//IF DEMO MODE
                for (i = 0; i < 3000; i++) {
                    if (intChannel == 1) {
                        LoadDataArray.SampleData[1][i] = (float) (LoadDataArray.SampleData[1][i] - (oldYDiv * 5 * OffsetSliderCh1.getValue() / 10000) + (YDiv[1] * 5 * OffsetSliderCh1.getValue() / 10000));
                    } else if (intChannel == 2) {
                        LoadDataArray.SampleData[2][i] = (float) (LoadDataArray.SampleData[2][i] - (oldYDiv * 5 * OffsetSliderCh2.getValue() / 10000) + (YDiv[2] * 5 * OffsetSliderCh2.getValue() / 10000));
                    } else if (intChannel == 3) {
                        LoadDataArray.SampleData[3][i] = (float) (LoadDataArray.SampleData[3][i] - (oldYDiv * 5 * OffsetSliderCh3.getValue() / 10000) + (YDiv[3] * 5 * OffsetSliderCh3.getValue() / 10000));
                    } else if (intChannel == 4) {
                        LoadDataArray.SampleData[4][i] = (float) (LoadDataArray.SampleData[4][i] - (oldYDiv * 5 * OffsetSliderCh4.getValue() / 10000) + (YDiv[4] * 5 * OffsetSliderCh4.getValue() / 10000));
                    }

                }
                //if (FirstStopped && demoMode && !JavaRunning) {
                if ((FirstStopped && demoMode) || SoftwareLoaded) {
                    for (i = 0; i < 2999; i++) {
                        if (intChannel == 1) {
                            seriesCh1.updateByIndex(i, LoadDataArray.SampleData[1][i]);
                        } else if (intChannel == 2) {
                            seriesCh2.updateByIndex(i, LoadDataArray.SampleData[2][i]);
                        } else if (intChannel == 3) {
                            seriesCh3.updateByIndex(i, LoadDataArray.SampleData[3][i]);
                        } else if (intChannel == 4) {
                            seriesCh4.updateByIndex(i, LoadDataArray.SampleData[4][i]);
                        }

                    }

                }
            }
        }

        VerticalMarkerAdjust = 1.00;
        if (TimeTicks == true) {// && TimeTicks==false) {

            /* on the X axis tick units are not always 100 apart (apart from ris mode and time base settings 4 to 8).
             * When user has got displayed wave
             * and changes the time base, time per division needs to change, and graph followes it
             * using zoom funtion/. Cheat maybe, but it is the only way. Run and capture new data with the new timebase set if you wish.
             */
            double SamplePeriodNow = Double.parseDouble(TimeBaseSettings[intTimeBaseArrayPointer][5]);

            if (SignalSamplePeriod >= 0.000000002 && SignalSamplePeriod < 0.00000002) {
                PointsPerDivMod = 0.00000002 / SignalSamplePeriod;
            }

            if (SignalSamplePeriod >= 0.00000000004 && SignalSamplePeriod < 0.000000001) {//RIS mode
                PointsPerDivMod = 0.000000001 / SignalSamplePeriod;
            }
            PointsPerDiv = (100 * (SamplePeriodNow / SignalSamplePeriod)) / PointsPerDivMod;
            FFTLabelAdjust = (SamplePeriodNow / SignalSamplePeriod);

            VerticalMarkerAdjust = (SamplePeriodNow / SignalSamplePeriod);



            if (!JavaRunning) {
                FFTLabelAdjust = LoadDataArray.FreqStep * FFTLabelAdjust;
                UpdateFFTlabel(FFTLabelAdjust);
                VerticalMarkerAdjust = VerticalMarkerAdjust / PointsPerDivMod;
            }



            if (SignalSamplePeriod > SamplePeriodNow) {
                if (((int) PointsPerDiv) == 40) {
                    axisX.setRange((-maxGraphPointCount / 2) + 20, 20 + ((30 * PointsPerDiv) - (maxGraphPointCount / 2)));
                } else {
                    axisX.setRange((-maxGraphPointCount / 2), ((30 * PointsPerDiv) - (maxGraphPointCount / 2)));

                }
            } else if (SignalSamplePeriod < SamplePeriodNow) {
                axisX.setRange(-(PointsPerDiv * 15), (PointsPerDiv * 15));

            } else {// if =; if at the original sample setting
                PointsPerDiv = GetPointsPerDiv(intTimeBaseArrayPointer);//100;
                //if(intTimeBaseArrayPointer==4){
                //axisX.setRange((-maxGraphPointCount / 2) -20, -20+((30 * PointsPerDiv) - (maxGraphPointCount / 2)));//index between 4 & 8 causes problems

                //}else{

                axisX.setRange(-maxGraphPointCount / 2, ((30 * PointsPerDiv) - (maxGraphPointCount / 2)));//index between 4 & 8 causes problems
                //}
                t.CalculateFreqStep();
            }

            axisX.setTickUnit(new NumberTickUnit(PointsPerDiv), true, true);
            lastPointsPerDiv = PointsPerDiv;
        }
    }

    private void RightArrowSecDivActionPerformed() {
        //One time base for all channels; Right Arrow on every channel calls this routine
        if (ChartZoomed) {
            boolean proceed = WannaCancelZoom();
            if (!proceed) {
                return;
            }
        }

        if (intTimeBaseArrayPointer > 0) {//* for the first 4 values the sampling rate is 1 ns/div; ignore these for the time being
            intTimeBaseArrayPointer--;
            TimeBaseChangedEvent(intTimeBaseArrayPointer);
        }



    }

    private void LeftArrowSecDivActionPerformed() {
        //One time base for all channels; Left Arrow on every channel calls this routine
        if (ChartZoomed) {
            boolean proceed = WannaCancelZoom();
            if (!proceed) {
                return;
            }
        }
        if (intTimeBaseArrayPointer < TimeBaseSettings.length - 1) {
            intTimeBaseArrayPointer++;
            TimeBaseChangedEvent(intTimeBaseArrayPointer);
        }

    }

    private void RightArrowVDivActionPerformed(int intChannel, boolean ApplyToAll) {
        if (ChartZoomed) {
            boolean proceed = WannaCancelZoom();
            if (!proceed) {
                return;
            }
        }
        if (intVoltsperDivPointer[intChannel] > 0) {
            intVoltsperDivPointer[intChannel]--;
            VoltsperDivChangedEvent(intVoltsperDivPointer[intChannel], intChannel);//1 = channel 1
        }

        if (ApplyToAll) {
            int tempVoltsperDivPointer = intVoltsperDivPointer[intChannel];

            lbVDivCh1.setText(" Ch1:" + VoltsDivSettings[tempVoltsperDivPointer][Probe[1]].toString());
            lbVDivCh2.setText(" Ch2:" + VoltsDivSettings[tempVoltsperDivPointer][Probe[2]].toString());
            lbVDivCh3.setText(" Ch3:" + VoltsDivSettings[tempVoltsperDivPointer][Probe[3]].toString());
            lbVDivCh4.setText(" Ch4:" + VoltsDivSettings[tempVoltsperDivPointer][Probe[4]].toString());


            for (int i = 1; i <= 4; i++) {
                intVoltsperDivPointer[i] = tempVoltsperDivPointer;
                VoltsperDivChangedEvent(intVoltsperDivPointer[i], i);//1 = channel 1
            }
            VoltsperDivChangedEvent(tempVoltsperDivPointer, intChannel);//1 = channel 1
        }

    }

    private void LeftArrowVDivActionPerformed(int intChannel, boolean ApplyToAll) {
        if (ChartZoomed) {
            boolean proceed = WannaCancelZoom();
            if (!proceed) {
                return;
            }
        }


        if (intVoltsperDivPointer[intChannel] < VoltsDivSettings.length - 1) {
            intVoltsperDivPointer[intChannel]++;
            VoltsperDivChangedEvent(intVoltsperDivPointer[intChannel], intChannel);
        }

        if (ApplyToAll) {
            int tempVoltsperDivPointer = intVoltsperDivPointer[intChannel];

            lbVDivCh1.setText(" Ch1:" + VoltsDivSettings[tempVoltsperDivPointer][Probe[1]].toString());
            lbVDivCh2.setText(" Ch2:" + VoltsDivSettings[tempVoltsperDivPointer][Probe[2]].toString());
            lbVDivCh3.setText(" Ch3:" + VoltsDivSettings[tempVoltsperDivPointer][Probe[3]].toString());
            lbVDivCh4.setText(" Ch4:" + VoltsDivSettings[tempVoltsperDivPointer][Probe[4]].toString());

            for (int i = 1; i <= 4; i++) {
                intVoltsperDivPointer[i] = tempVoltsperDivPointer;
                VoltsperDivChangedEvent(intVoltsperDivPointer[i], i);//1 = channel 1
            }
            VoltsperDivChangedEvent(tempVoltsperDivPointer, intChannel);//1 = channel 1
        }
    }

    private void SaveChartAs() {
        String Msg = " already exists.\nDo you want to replace it?";
        JFileChooser fc = new JFileChooser();

        //set up File Chooser as save as dialog
        File defaultDirectory = new File(System.getProperty("user.home"));
        fc.setCurrentDirectory(defaultDirectory);
        fc.setAcceptAllFileFilterUsed(false);
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fc.addChoosableFileFilter(new MyFilterPNG());
        fc.addChoosableFileFilter(new MyFilterPDF());
        fc.addChoosableFileFilter(new MyFilterJPG());

        int result = fc.showSaveDialog(this); // Open chooser dialog

        if (result == JFileChooser.CANCEL_OPTION) {
        } else if (result == JFileChooser.APPROVE_OPTION) {
            String CheckedFileName = CheckFileName(fc.getSelectedFile().getName(), fc.getFileFilter().toString());
            File SaveAsFile = new File(fc.getSelectedFile().getParent() + "/" + CheckedFileName);
            if (SaveAsFile.exists()) {
                int response = JOptionPane.showConfirmDialog(null, SaveAsFile.getAbsolutePath() + Msg, "Save As", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (response != JOptionPane.CANCEL_OPTION) {
                    //yes, override
                    SaveGraph(fc.getSelectedFile().getParent(), CheckedFileName, fc.getFileFilter().toString());
                }
            } else {
                SaveGraph(fc.getSelectedFile().getParent(), CheckedFileName, fc.getFileFilter().toString());
            }
        } //end JFileChooser.APPROVE_OPTION

    }

    private void SaveGraph(String FilePath, String FileName, String FileExtensionFilter) {

        String FullFilePath = FilePath + "/" + FileName;
        try {
            if (FileExtensionFilter.toUpperCase().contains("PDF")) {
                saveChartAsPdf(FullFilePath, chartPanel.getChart(), chartPanel.getWidth(), chartPanel.getHeight(), new DefaultFontMapper());
            } else if (FileExtensionFilter.toUpperCase().contains("JPG")) {
                saveChartAsJPG(FullFilePath, chartPanel.getChart(), chartPanel.getWidth(), chartPanel.getHeight());
            } else {
                saveChartAsPNG(FullFilePath, chartPanel.getChart(), chartPanel.getWidth(), chartPanel.getHeight());
            }
        } catch (IOException ex) {
            Logger.getLogger(USBscope50_Main.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "\nFailed to Save Graph.\n\n" + ex.getMessage() + "\n\n", productID + " Software", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void SaveGraphPointsInTxtFile() {
        /* 3000 points used to display the graph are saved in a txt file
         * each value in a new line
         *
         * at the start of the txy file there should be a paragraph with graph settings
         * it should look something like this
         *
         * DISPLAY DATA ON CHANNEL : 1  //where i just look for : and value after it. accept 1-4 only
         * TIME BASE SETTING ms/div: 1  //only time settings which appear on the screen are accepted
         * FILE NAME :
         * DATE/TIME STAMP :
         *
         * any lines starting with // will be ignored and considered as comments
         */
        String CheckedFileName;
        File SaveAsFile;
        int result;

        String Msg = " already exists.\nDo you want to replace it?";
        JFileChooser fc = new JFileChooser();

        //set up File Chooser as save as dialog
        File defaultDirectory = new File(System.getProperty("user.home"));
        fc.setCurrentDirectory(defaultDirectory);
        fc.setAcceptAllFileFilterUsed(false);
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fc.addChoosableFileFilter(new MyFilterTxt());

        for (int loop = 1; loop <= numScopesFound; loop++) {
            fc.setDialogTitle("Save Channel " + (loop) + " data points to file");
            fc.setSelectedFile(new File("Ch" + (loop) + "_"));
            result = fc.showSaveDialog(this); // Open chooser dialog

            if (result == JFileChooser.CANCEL_OPTION) {
            } else if (result == JFileChooser.APPROVE_OPTION) {

                CheckedFileName = CheckFileNameforTxt(fc.getSelectedFile().getName(), fc.getFileFilter().toString());

                SaveAsFile = new File(fc.getSelectedFile().getParent() + "/" + CheckedFileName);
                if (SaveAsFile.exists()) {
                    int response = JOptionPane.showConfirmDialog(null, SaveAsFile.getAbsolutePath() + Msg, "Save As", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (response != JOptionPane.CANCEL_OPTION) {
                        //yes, override
                        //save data
                        Export3000Points_TxtFile(SaveAsFile, (loop));
                    }
                } else {
                    //save data
                    Export3000Points_TxtFile(SaveAsFile, (loop));
                }
            }

            if (FFT_Channel[loop]) {
                fc.setDialogTitle("Save FFT Channel " + (loop) + " FFT data points to file");
                fc.setSelectedFile(new File("Ch" + (loop) + "_FFT_"));
                result = fc.showSaveDialog(this); // Open chooser dialog

                if (result == JFileChooser.CANCEL_OPTION) {
                } else if (result == JFileChooser.APPROVE_OPTION) {

                    CheckedFileName = CheckFileNameforTxt(fc.getSelectedFile().getName(), fc.getFileFilter().toString());

                    SaveAsFile = new File(fc.getSelectedFile().getParent() + "/" + CheckedFileName);
                    if (SaveAsFile.exists()) {
                        int response = JOptionPane.showConfirmDialog(null, SaveAsFile.getAbsolutePath() + Msg, "Save As", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                        if (response != JOptionPane.CANCEL_OPTION) {
                            //yes, override
                            //save data
                            //Export3000Points_TxtFile(SaveAsFile, (loop));
                            Export1024FFTPoints_TxtFile(SaveAsFile, loop);
                        }
                    } else {
                        //save data
                        //Export3000Points_TxtFile(SaveAsFile, (loop));
                        Export1024FFTPoints_TxtFile(SaveAsFile, loop);
                    }

                } //end JFileChooser.APPROVE_OPTION
            }

        }
    }

    private void ScanforScopes_Linux() {
        String Msg = "";
        gUSBscope50Devices = new String[2][numIntsFound + 1];
        numScopesFound = 0;
        for (int i = 1; i <= numIntsFound; i++) {
            if (gUSBTandMDevices[2][i].toLowerCase().equals("usbscope50")) {//this is still usbscope50 even when in AKIP software mode
                numScopesFound++;
                gUSBscope50Devices[0][numScopesFound] = gUSBTandMDevices[0][i];
                gUSBscope50Devices[1][numScopesFound] = gUSBTandMDevices[1][i];
                ScopeProductNames[numScopesFound] = gUSBTandMDevices[2][i];
            }
        }


        if ((numScopesFound == 0) && (numIntsFound > 0)) {
            Msg = "\nThis application can only run with " + productID + "s.\nPlease close this application and " + "replace instrument with " + productID + ". \n\nStarting " + productID + " Software in Demo Mode...\n\n";
            JOptionPane.showMessageDialog(this, Msg, " " + productID + " Software", JOptionPane.ERROR_MESSAGE);
        }

        if (numScopesFound > MaxScope) {
            Msg = "\nThis application can only run with up to " + MaxScope + " " + productID + "s, " + "but " + numScopesFound + " were detected. \nPlease close this application, " + "then change the stack to include only " + MaxScope + " or fewer devices. " + "\n\nStarting " + productID + " Software in Demo Mode...\n\n";

            JOptionPane.showMessageDialog(this, Msg, " " + productID + " Software", JOptionPane.ERROR_MESSAGE);
            numScopesFound = 0;
        }
    }

    private void ScanforScopes_Windows() {
        String Msg = "";
        gUSBscope50Devices = new String[2][numIntsFound + 1];
        //numIntsFound could be more than number of scopes plugged in.
        //get rid of waves, pulses, etc.; cpynumIntsFound is the true number of scopes plugged in
        int j = 0;
        String productName;
        for (int i = 1; i <= numIntsFound; i++) {
            productName = t.USBscope50Drvr_GetProductName(i);
            if (productName.equalsIgnoreCase("usbscope50")) {
                gUSBscope50Devices[0][j] = "COM";// + Integer.toString(t.USBscope50Drv_WinGetComPort(i));
                gUSBscope50Devices[1][j] = productName;//"USBscope50";

                ChannelPresent[i] = (t.USBscope50Drvr_OpenAndReset(i) != 0);//OpenandReset wants ch values 1-4
                ChannelOn[i] = ChannelPresent[i];
                j++;
            } else {
                numIntsFound--;
            }
        }
        if (numIntsFound == 0) {
            Msg = "\nThis application can only run with " + productID + "s.\nPlease close this application and " + "replace instrument with " + productID + ". \n\nStarting " + productID + " Software in Demo Mode...\n\n";
            JOptionPane.showMessageDialog(this, Msg, " " + productID + " Software", JOptionPane.ERROR_MESSAGE);
        }

        if (numIntsFound > MaxScope) {
            Msg = "\nThis application can only run with up to " + MaxScope + " " + productID + "s, " + "but " + numScopesFound + " were detected. \nPlease close this application, " + "then change the stack to include only " + MaxScope + " or fewer devices. " + "\n\nStarting " + productID + " Software in Demo Mode...\n\n";

            JOptionPane.showMessageDialog(this, Msg, " " + productID + " Software", JOptionPane.ERROR_MESSAGE);
            numScopesFound = 0;
        }
    }

    private void SetThreasholdStatus(boolean status) {
        /**
         * Trigger panel gives user option to set trigger threshold when trigger mode is
         * either Greater than trigger threshold or Less than trigger threshold.
         * Enable for these two modes only (status true), disable for all other trigger modes (status false)
         */
        //LbThreshold1.setEnabled(status);
        //TxtThreshold1.setEnabled(status);
        //LbPercentage1.setEnabled(status);
        //SliderThreshold1.setEnabled(status);
        SliderThreshold.setEnabled(status);
        int tempSlValue = SliderThreshold.getValue();
        SliderThreshold.setValue(tempSlValue + 1);
        SliderThreshold.setValue(tempSlValue);

    }

    private void SetTrigType(int trigType) {
        int i;
        for (i = 1; i <= numScopesFound; i++) {
            t.USBscope50Drvr_SetTrigType(i, trigType);
        }
        currentTrigType = trigType;

    }

    private void SetTriggerDelayControlls(boolean enableStatus) {
        //  LbTriggerDelay.setEnabled(enableStatus);
        //  TxtTriggerDelay.setEnabled(enableStatus);
        //   Lbms.setEnabled(enableStatus);
        //  SliderTriggerDelay.setEnabled(enableStatus);
    }

    private void SetTriggerMode(int triggerMode) {
        int i;
        if (JavaRunning) {
            Boolean tempSingleTrigger = t.SingleTrigger;

            t.SingleTrigger = true;
            JavaRunning = false;

            t.USBscope50Drvr_AcquisitionEnd(1);
            t.USBscope50Drvr_AcquisitionEnd(2);
            t.USBscope50Drvr_AcquisitionEnd(3);
            t.USBscope50Drvr_AcquisitionEnd(4);
            for (i = 0; i < 1000; i++) {
            }
            for (i = 1; i <= numScopesFound; i++) {
                t.SetNormTrig(i, triggerMode);
            }

            t.SingleTrigger = tempSingleTrigger;
            MnuRunGraphClicked(); // t.JavaRunning = true set in MnuRunGraphClicked

        } else {

            for (i = 1; i <= numScopesFound; i++) {
                t.SetNormTrig(i, triggerMode);
            }
        }
    }

    public void SetUpStatusTimer() {

        ActionListener StatusTimerListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                abort = t.USBscope50Drv_isAbort();//check abort
                //System.out.println("t.USBscope50Drv_isAbort "+abort);
                if (countStatusTimerLoops > 5) {
                    SoftwareLoaded = true;
                } else {
                    countStatusTimerLoops++;
                }

                if ((abort > 0) && (abort < 5)) {// if it is 1,2,3 or 4

                    MnuStopClicked();
                    System.out.println("Unable to recover from the fault.\nSorry, but I have to abort " + abort);
                    //Error detected on Channel abort
                    TidyUpOnExit();
                    System.exit(0);
                }
                if (JavaRunning) {
                    if (oldStatus == t.statusTracker) {
                        //    System.out.println("Should I have exited here?");
                        //    TidyUpOnExit();
                        //    System.exit(0);//display msg warning about abort
                    }
                }
                oldStatus = t.statusTracker;
                statusButton.setText(t.status);
                if (!JavaRunning && demoMode) {
                    statusButton.setText("stopped");
                }
            }
        };
        StatusTimer = new Timer(750, StatusTimerListener);
        StatusTimer.setRepeats(true);
    }

    public void SetUpNullTimer() {

        ActionListener NullTimerListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int HighestCase = 19;
                switch (intNullTimerCommandID) {//timer called every 1 seconds. Lets take the rading first 3 or 4 times
                    case 0:
                    case 1:
                    case 3:
                    case 4:
                    case 6:
                    case 7:
                    case 9:
                    case 10:
                    case 12:
                    case 13:
                    case 15:
                    case 16:
                    case 18:
                    case 19:
                        MnuSingleClicked();
                        break;
                    case 2:
                        SetVoltageSetting(intNullChannelID, 2, 9);//2 for 3v/div setting (on VB), 9 for 2V/div
                        break;//set voltage setting 2 3V/div
                    case 8:
                        SetVoltageSetting(intNullChannelID, 1, 6);
                        break;
                    case 14:
                        SetVoltageSetting(intNullChannelID, 0, 3);
                        break;
                    case 5:
                        GetAverage(intNullChannelID, 2);//setting 2
                        break;
                    case 11:
                        GetAverage(intNullChannelID, 1);//setting 1
                        break;
                    case 17:
                        GetAverage(intNullChannelID, 0);//setting 0
                        break;
                    default:
                        break;
                }

                if (intNullTimerCommandID < (HighestCase + 1)) {
                    intNullTimerCommandID++;
                    NullTimer.restart();
                } else {
                    String filePath = null;
                    Object[] options = {"OK", "Remove Null"};
                    int response;
                    String Cr = System.getProperty("line.separator");


                    if (USBFamily_Main.OS.equalsIgnoreCase("Windows")) {
                        if (USBFamily_Main.Vista) {
                            filePath = (System.getProperty("user.home") + "\\AppData\\" + companyID + "\\" + productID + " Java Software\\");
                        } else {
                            filePath = (System.getenv("ProgramFiles") + "\\" + companyID + "\\" + productID + " Java Software\\");
                        }
                    } else if (USBFamily_Main.OS.equalsIgnoreCase("Linux")) {
                        filePath = (System.getProperty("user.home") + "/.");
                    }
                    File nullFile = new File(filePath + ScopeSerialNumbers[intNullChannelID] + "_NullOffset.txt");
                    FileWriter outputLine = null;

                    String Msg = ("\nThis completes Null feature for USBscope50 on channel " + intNullChannelID + "\n" +
                            "\nPress OK to save Null Offset Adjustments settings for USBscope50 serial number " + ScopeSerialNumbers[intNullChannelID] + "to the text file \n" +
                            nullFile.getPath() + "\n\n" +
                            "To cancel or remove Null Offset Adjustments settings select Remove Null");

                    if (companyID.equals("PRIST")) {
                        response = JOptionPane.showOptionDialog(USBscope50_Main.this, Msg, productID,
                                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, AKIPLogo_small, options, options[0]);
                    } else {
                        response = JOptionPane.showOptionDialog(USBscope50_Main.this, Msg, productID,
                                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, ElanLogo_Small, options, options[0]);
                    }

                    if (nullFile.exists() && nullFile.canWrite()) {
                        nullFile.delete();
                    }
                    if (response > 0) {//Remove Null: check if file exists and delete it
                        LoadDataArray.OffsetNull[intNullChannelID][0] = 0;
                        LoadDataArray.OffsetNull[intNullChannelID][1] = 0;
                        LoadDataArray.OffsetNull[intNullChannelID][2] = 0;
                    } else {
                        try {
                            nullFile.createNewFile();
                            outputLine = new FileWriter(nullFile, true);//append true as i know for sure the file has just been emptied
                            outputLine.write("//Null feature" + Cr + Cr);
                            outputLine.write("//Please note that the Null Offset Settings saved in this file correspond" + Cr +
                                    "//to USBscope50 with the serial number as per this file name.");
                            outputLine.write(Cr + Cr + "//These settings are read in by the software when the software is loaded, " +
                                    Cr + "//so you should only ever need to click on null button once.");
                            outputLine.write(Cr + Cr + "//Null feature will eliminate small channel offset that is naturally present " +
                                    Cr + "//in various Volts/div ranges.");
                            outputLine.write(Cr + Cr + "//To cancel null offset adjustments either delete this file " +
                                    Cr + "//or click on channel tab Null button and select option Remove Null.");
                            outputLine.write(Cr + Cr + "OffsetNull[0] = " + LoadDataArray.OffsetNull[intNullChannelID][0]);
                            outputLine.write(Cr + Cr + "OffsetNull[1] = " + LoadDataArray.OffsetNull[intNullChannelID][1]);
                            outputLine.write(Cr + Cr + "OffsetNull[2] = " + LoadDataArray.OffsetNull[intNullChannelID][2]);


                        } catch (IOException ex) {
                            Logger.getLogger(USBscope50_Main.class.getName()).log(Level.SEVERE, null, ex);
                        } finally {
                            try {
                                outputLine.close();
                            } catch (IOException ex) {
                                Logger.getLogger(USBscope50_Main.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }

                    }
                    SetVoltageSetting(intNullChannelID, VoltageGain[intNullChannelID], intVoltsperDivPointer[intNullChannelID]);
                    SetButtonState(1, NullButtonCh1, false);
                    SetButtonState(2, NullButtonCh2, false);
                    SetButtonState(3, NullButtonCh3, false);
                    SetButtonState(4, NullButtonCh4, false);
                    intNullChannelID = 0;

                    triggermode = Nulltriggermode;
                    currentTrigType = NullcurrentTrigType;
                    InitialTriggerSettings();

                    if (NullJavaRunning) {
                        MnuRunClicked();
                    }


                //if java running before null pressed then set it running and also if it was not in free mode put it back into triggering mode
                }


            }
        };
        NullTimer = new Timer(500, NullTimerListener);
        NullTimer.setRepeats(false);
    }

    private void SetVoltageSetting(int intNullChannelID, int VoltSettingVB, int VoltSettingJava) {
        //intVoltsperDivPointer[intNullChannelID] = VoltSettingJava;
        VoltsperDivChangedEvent(VoltSettingJava, intNullChannelID);
    }

    private void GetAverage(int intNullChannelID, int VoltSettingVB) {
        double avg = 0;
        double min = 128;
        double max = -128;
        float temp;
        if (demoMode) {
            LoadDataArray.OffsetNull[intNullChannelID][0] = 0;
            LoadDataArray.OffsetNull[intNullChannelID][1] = 0;
            LoadDataArray.OffsetNull[intNullChannelID][2] = 0;
        } else {

            for (int loop = 0; loop < 3000; loop++) {
                temp = LoadDataArray.SampleData[intNullChannelID][loop];
                avg = avg + temp;
                if (avg > max) {
                    max = temp;
                } else if (temp < min) {
                    min = temp;
                }
            }
            avg = avg / SampleDepth;

            LoadDataArray.OffsetNull[intNullChannelID][VoltSettingVB] = (float) avg;

        }
    }

    private void SetButtonState(int channelIndex, JToggleButton JTButton, boolean selected) {
        //change appearance of the JToggle button
        Color ButtonColor;
        switch (channelIndex) {
            case 2:
                ButtonColor = ColorCh2;
                break;
            case 3:
                ButtonColor = ColorCh3;
                break;
            case 4:
                ButtonColor = ColorCh4;
                break;
            default:
                ButtonColor = ColorCh1;
                break;
        }
        if (selected == true) {
            JTButton.putClientProperty("Synthetica.background", ButtonColor);
            JTButton.putClientProperty("Synthetica.background.alpha", 0.30f);
        } else {
            JTButton.putClientProperty("Synthetica.background", Color.GRAY);
            JTButton.putClientProperty("Synthetica.background.alpha", 0.10f);
        }
    }

    private void SetButtonState(int channelIndex, JButton JTButton, boolean selected) {
        //change appearance of the J button
        Color ButtonColor;
        switch (channelIndex) {
            case 2:
                ButtonColor = ColorCh2;
                break;
            case 3:
                ButtonColor = ColorCh3;
                break;
            case 4:
                ButtonColor = ColorCh4;
                break;
            default:
                ButtonColor = ColorCh1;
                break;//channel 1 default
        }
        if (selected == true) {
            JTButton.putClientProperty("Synthetica.background", ButtonColor);
            JTButton.putClientProperty("Synthetica.background.alpha", 0.30f);
        } else {
            JTButton.putClientProperty("Synthetica.background", Color.GRAY);
            JTButton.putClientProperty("Synthetica.background.alpha", 0.10f);
        }
    }

    private void ShowHelp() {
        //Display help set

        int pX = (int) (MnuHelp.getX() + (hb.getSize().getWidth() / 3));
        int pY = (int) (MnuHelp.getY() + (hb.getSize().getHeight() / 3));

        Point p = new Point(pX, pY);

        hb.setLocation(p);
        hb.setDisplayed(true);
    }

    private void SortOutJSpinners() {

        JSpinner.NumberEditor sne = new JSpinner.NumberEditor(XAxisZoomValue, "000");
        XAxisZoomValue.setEditor(sne);
    }

    private void TabShownOnScreen() {
        /* JSplitPane might have only left hand side showing
         * make sure the new added tab custom zoom is displayed on the screen
         */
        //DEAL WITH THIS WHEN JOPTION DIALOGS INTRODUCED AS WELL AS TABS FOR TRIGGER AND ZOOM
        if (this.getWidth() - SplitPane.getDividerLocation() - SplitPane.getDividerSize() - 25 < CustomZoomPanelWidth) {
            //SplitPane.setDividerLocation(-1); //negative value is a default JSplitPane divider setting
        }
    }

    public XYDataset createDataset(int intChannel) {
        //default initial graph velues
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries series = new XYSeries(""); //("USBscope50 on Channel 1");  //label on the bottom of the graph

        for (int i = -(maxGraphPointCount / 2); i < (maxGraphPointCount / 2); i++) {
            series.add(i, intChannel / 10);//AddNewValuetoGraph();
        }

        switch (intChannel) {
            case 1:
                seriesCh1 = series;
                dataset.addSeries(seriesCh1);
                break;
            case 2:
                seriesCh2 = series;
                dataset.addSeries(seriesCh2);
                break;
            case 3:
                seriesCh3 = series;
                dataset.addSeries(seriesCh3);
                break;
            case 4:
                seriesCh4 = series;
                dataset.addSeries(seriesCh4);
                break;
            default:
                dataset.addSeries(series);
                break;//this should never happen. max scopes allowed 4
        }

        return dataset;
    }

    public XYDataset createDataset_FFT(int intChannel) {
        //default initial graph velues
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries series = new XYSeries(""); //("USBscope50 on Channel 1");  //label on the bottom of the graph

        for (int i = -(maxGraphPointCount / 2); i < (maxGraphPointCount / 2); i++) {
            //series.add(i, (i*0.025)-(intChannel*2));//AddNewValuetoGraph();
            series.add(i, intChannel / 10);
        }

        switch (intChannel) {
            case 1:
                seriesCh1_FFT = series;
                dataset.addSeries(seriesCh1_FFT);
                break;
            case 2:
                seriesCh2_FFT = series;
                dataset.addSeries(seriesCh2_FFT);
                break;
            case 3:
                seriesCh3_FFT = series;
                dataset.addSeries(seriesCh3_FFT);
                break;
            case 4:
                seriesCh4_FFT = series;
                dataset.addSeries(seriesCh4_FFT);
                break;
            default:
                dataset.addSeries(series);
                break;//this should never happen. max scopes allowed 4
        }

        return dataset;
    }

    public void MnuRunGraphClicked() {

        ClearRunFlags();
        JavaRunning = true;
        t.run();
        LoadDataArray.SoftwareStopped = false;
        if (demoMode) {
            chartPlot.clearDomainMarkers();
            axisX.setRange((-maxGraphPointCount / 2), (maxGraphPointCount / 2));
            axisX.setTickUnit(new NumberTickUnit(100), true, true);
            updateMarkers(false, true);
            updateZeroMarkers();
            VerticalMarkerAdjust = 1.0;//Do I need to move this one two lines up???
        }


    }

    private void MnuStopClicked() {

        t.SingleTrigger = true;
        JavaRunning = false;

        if (statusButton.getText().contains("initializing")) {
            return;
        } else {
            statusButton.setText("stopped");
        //ChartZoom(true);
        }
        RecordSignalSamplePeriod();

        LoadDataArray.SoftwareStopped = true;
    }

    private void TidyUpOnExit() {

        t.USBscope50Drvr_AcquisitionEnd(1);
        t.USBscope50Drvr_AcquisitionEnd(2);
        t.USBscope50Drvr_AcquisitionEnd(3);
        t.USBscope50Drvr_AcquisitionEnd(4);
        //System.out.println("USBscope50Drvr_AcquisitionEnd");
        JavaRunning = false;
        t.SingleTrigger = false;
        t.Exiting = true;


        if ((abort > 0) && (abort < 5)) {// if it is 1,2,3 or 4
            String Msg = "\nFatal Error\n\nError occurred whilst trying to communicate with " + productID + " on channel " + abort + "." +
                    "\nUnable to recover from the fault.\n\nThe program will terminate.\n\n";
            JOptionPane.showMessageDialog(this, Msg, productID + " - Error", JOptionPane.ERROR_MESSAGE);
        }
        SaveSettingInConfigFile();
        t.ClosePorts();
        if (t.isAlive()) {
            t = null;
        }
    }

    public static void saveChartAsPdf(String FilePath, JFreeChart chart, int width, int height, FontMapper mapper) throws IOException {

        File file = new File(FilePath);
        OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
        writeChartAsPDF(out, chart, width, height, mapper);

        out.close();

    }

    public static void saveChartAsJPG(String FilePath, JFreeChart chart, int width, int height) {
        try {
            ChartUtilities.saveChartAsJPEG(new File(FilePath), 1.0f, chart, width, height);
        } catch (IOException ex) {
            Logger.getLogger(USBscope50_Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void saveChartAsPNG(String FilePath, JFreeChart chart, int width, int height) {
        try {
            ChartUtilities.saveChartAsPNG(new File(FilePath), chart, width, height);
        } catch (IOException ex) {
            Logger.getLogger(USBscope50_Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void TimeBaseChangedEvent(int SettingIndex) {
        //Time base adjust button up or down clicked (well, left or right arrows)

        LbCurrentTimeBaseCh1.setText(TimeBaseSettings[SettingIndex][0]);
        LbCurrentTimeBaseCh2.setText(TimeBaseSettings[SettingIndex][0]);
        LbCurrentTimeBaseCh3.setText(TimeBaseSettings[SettingIndex][0]);
        LbCurrentTimeBaseCh4.setText(TimeBaseSettings[SettingIndex][0]);
        lbTDivAllChs.setText("Time Base: " + TimeBaseSettings[SettingIndex][0]);

        UseRollmode = false;
        UseRISmode = 0;//false
        butRISstatus.setText("");
        if (SettingIndex > 21) {
            UseRollmode = true;
        /*t.USBscope50Drvr_SetRISMode(1, 0);
        t.USBscope50Drvr_SetRISMode(2, 0);
        t.USBscope50Drvr_SetRISMode(3, 0);
        t.USBscope50Drvr_SetRISMode(4, 0);*/
        } else if (SettingIndex < 5) {
            UseRISmode = 1;//true
            butRISstatus.setText("RIS mode");

            t.SetNormTrig(1, intTriggerModeNormal);
            t.SetNormTrig(2, intTriggerModeNormal);
            t.SetNormTrig(3, intTriggerModeNormal);
            t.SetNormTrig(4, intTriggerModeNormal);
            //System.out.println("t.SetNormTrig");
            t.USBscope50Drvr_SetTrigType(1, intFallingEdge);
            t.USBscope50Drvr_SetTrigType(2, intFallingEdge);
            t.USBscope50Drvr_SetTrigType(3, intFallingEdge);
            t.USBscope50Drvr_SetTrigType(4, intFallingEdge);
            //System.out.println("t.USBscope50Drvr_SetTrigType");
            t.USBscope50Drvr_SetRISMode(1, 1);
            t.USBscope50Drvr_SetRISMode(2, 1);
            t.USBscope50Drvr_SetRISMode(3, 1);
            t.USBscope50Drvr_SetRISMode(4, 1);
            //System.out.println("t.USBscope50Drvr_SetRISMode");
            TriggerModeFallingEdge.setSelected(true);

            lbTriggerStatus.setIcon(triggerFalling);
            SliderThreshold.setEnabled(true);
            //TriggerPositionSlider.setEnabled(false);
            triggermode = 1;
            TriggerPositionSlider.setValue(TriggerPositionSlider.getValue() - 1);
        } else {
            /*t.USBscope50Drvr_SetRISMode(1, 0);
            t.USBscope50Drvr_SetRISMode(2, 0);
            t.USBscope50Drvr_SetRISMode(3, 0);
            t.USBscope50Drvr_SetRISMode(4, 0);*/
        }

        UpdateSilabwithTimeSettings(SettingIndex);

        if (JavaRunning && (t.SingleTrigger == false)) {
        } else {
            if (demoMode && ImportedData) {
                ImportedData = false;
            } else {

                RedrawTickUnits(1, false, intVoltsperDivPointer[1], true, 0);//TimeBaseRatio);
            }
        }

        updateMarkers(false, true);

        updateZeroMarkers();

    }

    private void updateZeroMarkers() {
        //xaxis range changed



        if (chartPlot.getAnnotations().contains(ZeroPointCh1)) {
            chartPlot.removeAnnotation(ZeroPointCh1);
        }

        if (chartPlot.getAnnotations().contains(ZeroPointCh2)) {
            chartPlot.removeAnnotation(ZeroPointCh2);
        }

        if (chartPlot.getAnnotations().contains(ZeroPointCh3)) {
            chartPlot.removeAnnotation(ZeroPointCh3);
        }

        if (chartPlot.getAnnotations().contains(ZeroPointCh4)) {
            chartPlot.removeAnnotation(ZeroPointCh4);
        }

        drawZeroPointChMarkers(0, CheckTabTitles("Ch 1"), CheckTabTitles("Ch 2"), CheckTabTitles("Ch 3"), CheckTabTitles("Ch 4"));

    }

    private void UpdateSilabwithTimeSettings(int SettingIndex) {
        int i;
        int AdcClockSetting;
        int DecimationRatio;
        int realDecimationRatio = 0;


        AdcClockSetting = Integer.parseInt(TimeBaseSettings[SettingIndex][1]);
        DecimationRatio = Integer.parseInt(TimeBaseSettings[SettingIndex][2]);
        realDecimationRatio = Integer.parseInt(TimeBaseSettings[SettingIndex][4]);


        t.USBscope50Drvr_AcquisitionEnd(1);
        t.USBscope50Drvr_AcquisitionEnd(2);
        t.USBscope50Drvr_AcquisitionEnd(3);
        t.USBscope50Drvr_AcquisitionEnd(4);
        //System.out.println("t.USBscope50Drvr_AcquisitionEnd");

        if (CurrentAdcClockSetting != AdcClockSetting) {
            CurrentAdcClockSetting = AdcClockSetting;
            for (i = 1; i <= numScopesFound; i++) {
                //*better if channel on rather than numscopes found
                t.USBscope50Drvr_SetBaseAdcClk(i, CurrentAdcClockSetting);
            }
        //System.out.println("t.USBscope50Drvr_SetBaseAdcClk");
        }

        if (CurrentDecimationRatio != DecimationRatio) {
            CurrentDecimationRatio = DecimationRatio;
            for (i = 1; i <= numScopesFound; i++) {
                //*better if channel on rather than numscopes found
                t.SetDecimationRatio(i, CurrentDecimationRatio, realDecimationRatio);
            }
        //System.out.println("t.SetDecimationRatio");
        }

        TimeBaseRatio = (CurrentTimeBase / Double.parseDouble(TimeBaseSettings[SettingIndex][3]));
        CurrentTimeBase = Double.parseDouble(TimeBaseSettings[SettingIndex][3]);
    }

    private void VoltsperDivChangedEvent(int SettingIndex, int intChannel) {

        int tempVoltageGain = VoltageGain[intChannel];
        boolean tempSingleTrigger = t.SingleTrigger;

        UpdateVDivLabels(SettingIndex, intChannel);


        if (SettingIndex < 5) {
            VoltageGain[intChannel] = 0;
        } else if (SettingIndex < 8) {
            VoltageGain[intChannel] = 1;//voltage gain of 1 gives wrong points data readings???
        } else if (SettingIndex >= 8) {
            VoltageGain[intChannel] = 2;
        }

        if (tempVoltageGain != VoltageGain[intChannel]) {

            if (JavaRunning) {
                try {
                    t.USBscope50Drvr_AcquisitionEnd(1);
                    t.USBscope50Drvr_AcquisitionEnd(2);
                    t.USBscope50Drvr_AcquisitionEnd(3);
                    t.USBscope50Drvr_AcquisitionEnd(4);
                    //System.out.println("t.USBscope50Drvr_AcquisitionEnd");
                    JavaRunning = false;
                    t.SingleTrigger = true;
                    Thread.sleep(250);//
                    t.USBscope50Drvr_SetUpFrontEnd(intChannel, VoltageGain[intChannel], Dc_option[intChannel], gnd_option[intChannel], UseRISmode);
                    //System.out.println("t.USBscope50Drvr_SetUpFrontEnd");
                    JavaRunning = true;
                    t.SingleTrigger = tempSingleTrigger;
                } catch (InterruptedException ex) {
                    Logger.getLogger(USBscope50_Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                t.USBscope50Drvr_SetUpFrontEnd(intChannel, VoltageGain[intChannel], Dc_option[intChannel], gnd_option[intChannel], UseRISmode);
            //System.out.println("t.USBscope50Drvr_SetUpFrontEnd");
            }
        }


        RedrawTickUnits(intChannel, true, intVoltsperDivPointer[intChannel], false, 0);

        chartPlot.clearRangeMarkers();
        updateMarkers(true, false);
        if (ThresholdMarkerOn) {
            SliderThreshold.setValue(SliderThreshold.getValue() + 1);
            SliderThreshold.setValue(SliderThreshold.getValue() - 1);
        }

    }

    private void TimeBaseSettingsReset() {
        PopulateTimeBaseSettingsArray();
    //TimeBaseChangedEvent(intTimeBaseArrayPointer); //Default setting is 10us/div 10MHz clock, decimation ration = 1
    }

    private void VoltsDivSettingsReset() {
        PopulateVoltsDivSettingsArray();
    }

    private void TriggerModeFallingEdgeSelected() {
        if (TriggerModeFallingEdge.isSelected()) {// || TriggerModeFallingEdge1.isSelected()) {
            SetThreasholdStatus(true);
            TriggerPositionSlider.setEnabled(true);
            TriggerPositionSlider.setValue(TriggerPositionSlider.getValue() + 1);
            lbTriggerStatus.setIcon(triggerFalling);
        } else {
        }
    }

    private void TriggerModeFreeSelected() {
        if (TriggerModeFree.isSelected()) {// || TriggerModeFree1.isSelected()) {
            SetThreasholdStatus(false);
            //SetTriggerDelayControlls(false);
            //chartPlot.clearRangeMarkers();//clear marker
            ThresholdMarkerOn = false;
            chartPlot.clearRangeMarkers();
            updateMarkers(true, false);

            //slider disabled
            TriggerPositionSlider.setEnabled(false);
            TriggerPositionSlider.setValue(TriggerPositionSlider.getValue() + 1);
            lbTriggerStatus.setIcon(triggerFree);
        } else {
        }
    }

    private void TriggerModeRisingEdgeSelected() {
        if (TriggerModeRisingEdge.isSelected()) {// || TriggerModeRisingEdge1.isSelected()) {
            SetThreasholdStatus(true);
            //SetTriggerDelayControlls(true);
            //SetTriggerDelayControlls(false); //it should be true- not implemented yet
            TriggerPositionSlider.setEnabled(true);
            TriggerPositionSlider.setValue(TriggerPositionSlider.getValue() + 1);
            lbTriggerStatus.setIcon(triggerRising);
        } else {
        }
    }

    private void TriggerModeLessThanSelected() {
        if (TriggerModeLessThan.isSelected()) {// || TriggerModeLessThan1.isSelected()) {
            SetThreasholdStatus(true);
            //SetTriggerDelayControlls(true);
            //SetTriggerDelayControlls(false); //it should be true- not implemented yet
            TriggerPositionSlider.setEnabled(true);
            TriggerPositionSlider.setValue(TriggerPositionSlider.getValue() + 1);
            lbTriggerStatus.setIcon(triggerLessThan);
        } else {
        }
    }

    private void TriggerModeGreaterThanSelected() {
        if (TriggerModeGreaterThan.isSelected()) {// || TriggerModeGreaterThan1.isSelected()) {
            SetThreasholdStatus(true);
            //SetTriggerDelayControlls(true);
            //SetTriggerDelayControlls(false); //it should be true- not implemented yet
            TriggerPositionSlider.setEnabled(true);
            TriggerPositionSlider.setValue(TriggerPositionSlider.getValue() + 1);
            lbTriggerStatus.setIcon(triggerMoreThan);
        } else {
        }
    }

    private void Export1024FFTPoints_TxtFile(File SaveToFile, int intChannel) {

        /* DATA ON CHANNEL   : 1  //where i just look for : and value after it. accept 1-4 only
         * TIME BASE SETTING : 100 us/div  //only time settings which appear on the screen are accepted
         * FILE PATH & NAME  : /home/ana/Desktop/testtxt.txt
         * DATE/TIME STAMP   : 11-02-2008 14:01:54
         */

        float FreqStep = 0;
        XYSeries tempSeries;
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);

        FreqStep = (0.5f / t.GetSamplePeriod((JavaRunning ? 1 : 0), (float) SignalSamplePeriod)) / (2048f / 2f);

        String SpectralWindow = "";
        String PlotType = "";


        if (MnudBVFFTPlot.isSelected()) {
            PlotType = "dB(V)";
        } else {
            PlotType = "Linear";
        }

        if (lngFFTWindowType == 0) {
            SpectralWindow = "Rectangular";
        } else if (lngFFTWindowType == 1) {
            SpectralWindow = "Hanning";
        } else if (lngFFTWindowType == 2) {
            SpectralWindow = "Hamming";
        } else if (lngFFTWindowType == 3) {
            SpectralWindow = "Triangular";
        } else if (lngFFTWindowType == 4) {
            SpectralWindow = "Welch";
        }



        try {

            FileWriter outInitial = new FileWriter(SaveToFile);
            outInitial.write("//" + productID + " Software loaded this file with the 1024 FFT data points." +
                    System.getProperty("line.separator") + "each sample point is at xxx Hz point away from the previous one" +
                    System.getProperty("line.separator") + "//Import option is not awailable for FFT sample points" +
                    System.getProperty("line.separator") + System.getProperty("line.separator") + "FFT FOR CHANNEL   : " +
                    intChannel +
                    System.getProperty("line.separator") + "FREQUENCY STEP(Hz): " + FreqStep +
                    System.getProperty("line.separator") +
                    "V/DIV SETTING     : " + VoltsDivSettings[intVoltsperDivPointer[intChannel]][1] + System.getProperty("line.separator") +
                    "PLOT TYPE         : " + PlotType + System.getProperty("line.separator") +
                    "SPECTRAL WINDOW   : " + SpectralWindow + System.getProperty("line.separator") +
                    "FILE PATH & NAME  : " +
                    SaveToFile + System.getProperty("line.separator") + "DATE/TIME STAMP   : " + sdf.format(cal.getTime()) +
                    System.getProperty("line.separator") + System.getProperty("line.separator"));
            outInitial.close();

            switch (intChannel) {
                case 2:
                    tempSeries = seriesCh2_FFT.createCopy(0, 1024);
                    break;
                case 3:
                    tempSeries = seriesCh3_FFT.createCopy(0, 1024);
                    break;
                case 4:
                    tempSeries = seriesCh4_FFT.createCopy(0, 1024);
                    break;
                default:
                    tempSeries = seriesCh1_FFT.createCopy(0, 1024);
                    break;//channel 1 is default
            }
            FileWriter out = new FileWriter(SaveToFile, true);//(FileToWrite,append)
            for (int i = 0; i < 1024; i++) {
                if (i >= tempSeries.getItemCount()) {
                    out.write(System.getProperty("line.separator") + "NaN");
                } else {
                    out.write(System.getProperty("line.separator") + tempSeries.getY(i).toString());
                }
            }

            out.close();
        } catch (IOException ex) {
            Logger.getLogger(USBscope50_Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CloneNotSupportedException ex) {//series create copy action
            Logger.getLogger(USBscope50_Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void Export3000Points_TxtFile(File SaveToFile, int intChannel) {

        /* DATA ON CHANNEL   : 1  //where i just look for : and value after it. accept 1-4 only
         * TIME BASE SETTING : 100 us/div  //only time settings which appear on the screen are accepted
         * FILE PATH & NAME  : /home/ana/Desktop/testtxt.txt
         * DATE/TIME STAMP   : 11-02-2008 14:01:54
         */

        XYSeries tempSeries;
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);

        try {

            FileWriter outInitial = new FileWriter(SaveToFile);
            outInitial.write("//" + productID + " Software loaded this file with the 3000 graph data points." +
                    System.getProperty("line.separator") + "//100 sample points per division with USBscope50 Java Software." +
                    System.getProperty("line.separator") + "//Use software option Edit -> Import to recreate the graph." +
                    System.getProperty("line.separator") + System.getProperty("line.separator") + "DATA ON CHANNEL   : " +
                    intChannel + System.getProperty("line.separator") + "TIME BASE SETTING : " +
                    TimeBaseSettings[intTimeBaseArrayPointer][0] + System.getProperty("line.separator") +
                    "V/DIV SETTING     : " + VoltsDivSettings[intVoltsperDivPointer[intChannel]][1] + System.getProperty("line.separator") +
                    "FILE PATH & NAME  : " +
                    SaveToFile + System.getProperty("line.separator") + "DATE/TIME STAMP   : " + sdf.format(cal.getTime()) +
                    System.getProperty("line.separator") + System.getProperty("line.separator"));
            outInitial.close();

            switch (intChannel) {
                case 2:
                    tempSeries = seriesCh2.createCopy(0, maxGraphPointCount - 1);
                    break;
                case 3:
                    tempSeries = seriesCh3.createCopy(0, maxGraphPointCount - 1);
                    break;
                case 4:
                    tempSeries = seriesCh4.createCopy(0, maxGraphPointCount - 1);
                    break;
                default:
                    tempSeries = seriesCh1.createCopy(0, maxGraphPointCount - 1);
                    break;//channel 1 is default
            }
            FileWriter out = new FileWriter(SaveToFile, true);//(FileToWrite,append)
            for (int i = 0; i < 3000; i++) {
                if (i >= tempSeries.getItemCount()) {
                    out.write(System.getProperty("line.separator") + "NaN");
                } else {
                    out.write(System.getProperty("line.separator") + tempSeries.getY(i).toString());
                }
            }

            out.close();
        } catch (IOException ex) {
            Logger.getLogger(USBscope50_Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CloneNotSupportedException ex) {//series create copy action
            Logger.getLogger(USBscope50_Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String checkifUSBscope50txtfile(File GraphPointsFile) {
        /* Routine called by import option. Do some checks on the file user wants to import; on success load function and return empty string
         * on error return error string without loading data points on the graph
         */
        if (!GraphPointsFile.exists()) {
            return "File does not exist.";
        }

        if (!GraphPointsFile.isFile()) {
            return "File not a file.";
        }

        if (!(GraphPointsFile.getName().toLowerCase().endsWith(".txt"))) {
            return "Not a text file.";
        }

        if (!GraphPointsFile.canRead()) {
            return "Unable to read the file.";
        }

        return LoadGraphPoints(GraphPointsFile);
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

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        WannaCancelZoomChkBox = new javax.swing.JCheckBox();
        ButGroupTriggerOn = new javax.swing.ButtonGroup();
        ButGroupTriggerMode = new javax.swing.ButtonGroup();
        buttGroupProbeCh1 = new javax.swing.ButtonGroup();
        buttGroupCouplingCh1 = new javax.swing.ButtonGroup();
        ScrollCustomZoomPane = new javax.swing.JScrollPane();
        CustomZoomPanel = new javax.swing.JPanel();
        XAxisOptions = new javax.swing.JPanel();
        CustomZoomXAxisLabel1 = new javax.swing.JLabel();
        XAxisZoomValue = new javax.swing.JSpinner();
        OKXAxisCustomZoom = new javax.swing.JButton();
        CustomZoomXAxisLabel2 = new javax.swing.JLabel();
        YAxisOptions = new javax.swing.JPanel();
        CustomZoomYAxisLabel1 = new javax.swing.JLabel();
        YAxisZoomValue = new javax.swing.JSpinner();
        OKYAxisCustomZoom = new javax.swing.JButton();
        CustomZoomYAxisLabel2 = new javax.swing.JLabel();
        buttonCancelZoom = new javax.swing.JButton();
        ScrollTriggerPanel = new javax.swing.JScrollPane();
        TriggerPanel = new javax.swing.JPanel();
        SliderThreshold = new javax.swing.JSlider();
        SelectTriggerChPanel = new javax.swing.JPanel();
        TriggerOnLabel = new javax.swing.JLabel();
        RadioButTrigOnChan1 = new javax.swing.JRadioButton();
        RadioButTrigOnChan2 = new javax.swing.JRadioButton();
        RadioButTrigOnChan3 = new javax.swing.JRadioButton();
        RadioButTrigOnChan4 = new javax.swing.JRadioButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        TriggerModeFree = new javax.swing.JRadioButton();
        TriggerModeRisingEdge = new javax.swing.JRadioButton();
        TriggerModeFallingEdge = new javax.swing.JRadioButton();
        TriggerModeLessThan = new javax.swing.JRadioButton();
        TriggerModeGreaterThan = new javax.swing.JRadioButton();
        ScrollOptionsCh1 = new javax.swing.JScrollPane();
        OptionsCh1 = new javax.swing.JPanel();
        PanelButtonsCh1 = new javax.swing.JPanel();
        ButtonsCh1 = new javax.swing.JPanel();
        INVButtonCh1 = new javax.swing.JToggleButton();
        IDButtonCh1 = new javax.swing.JButton();
        FFTButtonCh1 = new javax.swing.JToggleButton();
        ONButtonCh1 = new javax.swing.JToggleButton();
        COMPButtonCh1 = new javax.swing.JToggleButton();
        NullButtonCh1 = new javax.swing.JButton();
        ProbeCouplingCh1 = new javax.swing.JPanel();
        OpACCouplingCh1 = new javax.swing.JRadioButton();
        OpDCCouplingCh1 = new javax.swing.JRadioButton();
        OpGNDCh1 = new javax.swing.JRadioButton();
        Opx1ProbeCh1 = new javax.swing.JRadioButton();
        Opx10ProbeCh1 = new javax.swing.JRadioButton();
        ClearOffsetCh1 = new javax.swing.JRadioButton();
        ArrowsPanelLineCh1 = new javax.swing.JPanel();
        ArrowPanelColorCh1 = new javax.swing.JPanel();
        ArrowPanelColorCh1.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED, null, java.awt.Color.orange, java.awt.Color.orange, null), new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED)));
        ArrowPanelCh1 = new javax.swing.JPanel();
        RightArrowVDivCh1 = new javax.swing.JButton();
        LeftArrowVDivCh1 = new javax.swing.JButton();
        LeftArrowSecDivCh1 = new javax.swing.JButton();
        LbCurrentTimeBaseCh1 = new javax.swing.JLabel();
        RightArrowSecDivCh1 = new javax.swing.JButton();
        OffsetSliderCh1 = new javax.swing.JSlider();
        ScrollOptionsCh2 = new javax.swing.JScrollPane();
        OptionsCh2 = new javax.swing.JPanel();
        PanelButtonsCh2 = new javax.swing.JPanel();
        ButtonsCh2 = new javax.swing.JPanel();
        INVButtonCh2 = new javax.swing.JToggleButton();
        IDButtonCh2 = new javax.swing.JButton();
        FFTButtonCh2 = new javax.swing.JToggleButton();
        ONButtonCh2 = new javax.swing.JToggleButton();
        COMPButtonCh2 = new javax.swing.JToggleButton();
        NullButtonCh2 = new javax.swing.JButton();
        ProbeCouplingCh2 = new javax.swing.JPanel();
        OpACCouplingCh2 = new javax.swing.JRadioButton();
        OpDCCouplingCh2 = new javax.swing.JRadioButton();
        OpGNDCh2 = new javax.swing.JRadioButton();
        Opx1ProbeCh2 = new javax.swing.JRadioButton();
        Opx10ProbeCh2 = new javax.swing.JRadioButton();
        ClearOffsetCh2 = new javax.swing.JRadioButton();
        ArrowsPanelLineCh2 = new javax.swing.JPanel();
        ArrowPanelColorCh2 = new javax.swing.JPanel();
        ArrowPanelCh2 = new javax.swing.JPanel();
        RightArrowVDivCh2 = new javax.swing.JButton();
        LeftArrowVDivCh2 = new javax.swing.JButton();
        LeftArrowSecDivCh2 = new javax.swing.JButton();
        LbCurrentTimeBaseCh2 = new javax.swing.JLabel();
        RightArrowSecDivCh2 = new javax.swing.JButton();
        OffsetSliderCh2 = new javax.swing.JSlider();
        ScrollOptionsCh3 = new javax.swing.JScrollPane();
        OptionsCh3 = new javax.swing.JPanel();
        PanelButtonsCh3 = new javax.swing.JPanel();
        ButtonsCh3 = new javax.swing.JPanel();
        INVButtonCh3 = new javax.swing.JToggleButton();
        IDButtonCh3 = new javax.swing.JButton();
        FFTButtonCh3 = new javax.swing.JToggleButton();
        ONButtonCh3 = new javax.swing.JToggleButton();
        COMPButtonCh3 = new javax.swing.JToggleButton();
        NullButtonCh3 = new javax.swing.JButton();
        ProbeCouplingCh3 = new javax.swing.JPanel();
        OpACCouplingCh3 = new javax.swing.JRadioButton();
        OpDCCouplingCh3 = new javax.swing.JRadioButton();
        OpGNDCh3 = new javax.swing.JRadioButton();
        Opx1ProbeCh3 = new javax.swing.JRadioButton();
        Opx10ProbeCh3 = new javax.swing.JRadioButton();
        ClearOffsetCh3 = new javax.swing.JRadioButton();
        ArrowsPanelLineCh3 = new javax.swing.JPanel();
        ArrowPanelColorCh3 = new javax.swing.JPanel();
        ArrowPanelCh3 = new javax.swing.JPanel();
        RightArrowVDivCh3 = new javax.swing.JButton();
        LeftArrowVDivCh3 = new javax.swing.JButton();
        LeftArrowSecDivCh3 = new javax.swing.JButton();
        LbCurrentTimeBaseCh3 = new javax.swing.JLabel();
        RightArrowSecDivCh3 = new javax.swing.JButton();
        OffsetSliderCh3 = new javax.swing.JSlider();
        ScrollOptionsCh4 = new javax.swing.JScrollPane();
        OptionsCh4 = new javax.swing.JPanel();
        PanelButtonsCh4 = new javax.swing.JPanel();
        ButtonsCh4 = new javax.swing.JPanel();
        INVButtonCh4 = new javax.swing.JToggleButton();
        IDButtonCh4 = new javax.swing.JButton();
        FFTButtonCh4 = new javax.swing.JToggleButton();
        ONButtonCh4 = new javax.swing.JToggleButton();
        COMPButtonCh4 = new javax.swing.JToggleButton();
        NullButtonCh4 = new javax.swing.JButton();
        ProbeCouplingCh4 = new javax.swing.JPanel();
        OpACCouplingCh4 = new javax.swing.JRadioButton();
        OpDCCouplingCh4 = new javax.swing.JRadioButton();
        OpGNDCh4 = new javax.swing.JRadioButton();
        Opx1ProbeCh4 = new javax.swing.JRadioButton();
        Opx10ProbeCh4 = new javax.swing.JRadioButton();
        ClearOffsetCh4 = new javax.swing.JRadioButton();
        ArrowsPanelLineCh4 = new javax.swing.JPanel();
        ArrowsPanelColorCh4 = new javax.swing.JPanel();
        ArrowPanelCh4 = new javax.swing.JPanel();
        RightArrowVDivCh4 = new javax.swing.JButton();
        LeftArrowVDivCh4 = new javax.swing.JButton();
        LeftArrowSecDivCh4 = new javax.swing.JButton();
        LbCurrentTimeBaseCh4 = new javax.swing.JLabel();
        RightArrowSecDivCh4 = new javax.swing.JButton();
        OffsetSliderCh4 = new javax.swing.JSlider();
        DisplayLicenseDialog = new javax.swing.JDialog();
        ScrollPaneGPLLicense = new javax.swing.JScrollPane();
        txtGPLLicense = new javax.swing.JTextPane();
        ExitLicenseDialog = new javax.swing.JButton();
        buttGroupCouplingCh2 = new javax.swing.ButtonGroup();
        buttGroupCouplingCh3 = new javax.swing.ButtonGroup();
        buttGroupCouplingCh4 = new javax.swing.ButtonGroup();
        buttGroupProbeCh2 = new javax.swing.ButtonGroup();
        buttGroupProbeCh3 = new javax.swing.ButtonGroup();
        buttGroupProbeCh4 = new javax.swing.ButtonGroup();
        ChartOptionsDialog = new javax.swing.JDialog();
        ChartOptionsTabs = new javax.swing.JTabbedPane();
        BackgroundColorPanel = new javax.swing.JPanel();
        ColorChooser = new javax.swing.JColorChooser();
        ColorChooser.setPreviewPanel(new javax.swing.JPanel());
        ColorChooser.getSelectionModel().addChangeListener(
            new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    newChartBackgroundColor = ColorChooser.getColor();
                    chartPlot.setBackgroundPaint(newChartBackgroundColor);
                    FFTplot.setBackgroundPaint(newChartBackgroundColor);
                }
            }    
        );
        buttChartDefaultValues = new javax.swing.JButton();
        buttChartOptionsCancel = new javax.swing.JButton();
        buttChartOptionsOK = new javax.swing.JButton();
        ChartStrokePanel = new javax.swing.JPanel();
        buttChartDefaultValues2 = new javax.swing.JButton();
        buttChartOptionsCancel2 = new javax.swing.JButton();
        buttChartOptionsOK2 = new javax.swing.JButton();
        GridLinesThicknessPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        RangeSpinner = new javax.swing.JSpinner();
        DomainSpinner = new javax.swing.JSpinner();
        chkModifyBoth = new javax.swing.JCheckBox();
        jPanel4 = new javax.swing.JPanel();
        FunctionSpinner = new javax.swing.JSpinner();
        jLabel3 = new javax.swing.JLabel();
        GridLinesColorPanel = new javax.swing.JPanel();
        ColorChooser1 = new javax.swing.JColorChooser();
        ColorChooser1.setPreviewPanel(new javax.swing.JPanel());
        ColorChooser1.getSelectionModel().addChangeListener(
            new ChangeListener() {

                public void stateChanged(ChangeEvent e) {
                    newRangeGridColor = ColorChooser1.getColor();
                    newDomainGridColor = ColorChooser1.getColor();
                    chartPlot.setDomainGridlinePaint(newDomainGridColor);
                    chartPlot.setRangeGridlinePaint(newRangeGridColor);

                    FFTplot.setDomainGridlinePaint(newDomainGridColor);
                    FFTplot.setRangeGridlinePaint(newRangeGridColor);
                }
            });
            buttChartDefaultValues1 = new javax.swing.JButton();
            buttChartOptionsCancel1 = new javax.swing.JButton();
            buttChartOptionsOK1 = new javax.swing.JButton();
            grpSelectOffsetCh = new javax.swing.ButtonGroup();
            buttGroupOffset = new javax.swing.ButtonGroup();
            ChannelColorsDialog = new javax.swing.JDialog();
            ChannelColourTabs = new javax.swing.JTabbedPane();
            Ch1Color = new javax.swing.JPanel();
            Ch1ColorChooser = new javax.swing.JColorChooser();
            Ch1ColorChooser.setPreviewPanel(new javax.swing.JPanel());
            Ch1ColorChooser.getSelectionModel().addChangeListener(
                new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {	                
                        newColorCh1 = Ch1ColorChooser.getColor();
                        renderer.setSeriesPaint(0, newColorCh1);
                        renderer_FFT.setSeriesPaint(0, newColorCh1);
                    }
                }    
            );
            buttChColorDefaultValues1 = new javax.swing.JButton();
            buttChColorCancel1 = new javax.swing.JButton();
            buttChColorOK1 = new javax.swing.JButton();
            Ch2Color = new javax.swing.JPanel();
            Ch2ColorChooser = new javax.swing.JColorChooser();
            Ch2ColorChooser.setPreviewPanel(new javax.swing.JPanel());
            Ch2ColorChooser.getSelectionModel().addChangeListener(
                new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {	    
                        newColorCh2 = Ch2ColorChooser.getColor();
                        renderer2.setSeriesPaint(0, newColorCh2);
                        renderer2_FFT.setSeriesPaint(0, newColorCh2);
                    }
                }    
            );
            buttChColorDefaultValues2 = new javax.swing.JButton();
            buttChColorCancel2 = new javax.swing.JButton();
            buttChColorOK2 = new javax.swing.JButton();
            Ch3Color = new javax.swing.JPanel();
            Ch3ColorChooser = new javax.swing.JColorChooser();
            Ch3ColorChooser.setPreviewPanel(new javax.swing.JPanel());
            Ch3ColorChooser.getSelectionModel().addChangeListener(
                new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {	    
                        newColorCh3 = Ch3ColorChooser.getColor();
                        renderer3.setSeriesPaint(0, newColorCh3);
                        renderer3_FFT.setSeriesPaint(0, newColorCh3);
                    }
                }    
            );
            buttChColorDefaultValues3 = new javax.swing.JButton();
            buttChColorCancel3 = new javax.swing.JButton();
            buttChColorOK3 = new javax.swing.JButton();
            Ch4Color = new javax.swing.JPanel();
            Ch4ColorChooser = new javax.swing.JColorChooser();
            Ch4ColorChooser.setPreviewPanel(new javax.swing.JPanel());
            Ch4ColorChooser.getSelectionModel().addChangeListener(
                new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {	    
                        newColorCh4 = Ch4ColorChooser.getColor();
                        renderer4.setSeriesPaint(0, newColorCh4);
                        renderer4_FFT.setSeriesPaint(0, newColorCh4);
                    }
                }    
            );
            buttChColorDefaultValues4 = new javax.swing.JButton();
            buttChColorCancel4 = new javax.swing.JButton();
            buttChColorOK4 = new javax.swing.JButton();
            buttonGroupFFTPlotType = new javax.swing.ButtonGroup();
            buttonGroupFFTWindowType = new javax.swing.ButtonGroup();
            ToolBarFile = new javax.swing.JToolBar();
            NewToolBarButton = new javax.swing.JButton();
            OpenToolBarButton = new javax.swing.JButton();
            SaveToolBarButton = new javax.swing.JButton();
            ImportToolBarButton = new javax.swing.JButton();
            ExportToolBarButton = new javax.swing.JButton();
            ToolBarZoom = new javax.swing.JToolBar();
            FFTToolBarButton = new javax.swing.JButton();
            RemoveFFTToolBarButton = new javax.swing.JButton();
            ClearmarkersToolBarButton = new javax.swing.JButton();
            ZoomInToolBarButton = new javax.swing.JButton();
            ZoomOutToolBarButton = new javax.swing.JButton();
            ZoomCancelToolBarButton = new javax.swing.JButton();
            ToolBarHelp = new javax.swing.JToolBar();
            HelpToolBarButton = new javax.swing.JButton();
            RunToolBar = new javax.swing.JToolBar();
            RunToolBarButton = new javax.swing.JButton();
            StopToolBarButton = new javax.swing.JButton();
            SingleToolBarButton = new javax.swing.JButton();
            TriggerOptionsToolBarButton = new javax.swing.JButton();
            StatusToolBar = new javax.swing.JToolBar();
            butSpace = new javax.swing.JButton();
            butProductTitle = new javax.swing.JButton();
            lbTriggerStatus = new javax.swing.JLabel();
            statusButton = new javax.swing.JButton();
            butRISstatus = new javax.swing.JButton();
            SplitPane = new javax.swing.JSplitPane();
            LeftSplitPanel = new javax.swing.JPanel();
            PanelforChart = new javax.swing.JPanel();
            TriggerPositionSlider = new javax.swing.JSlider();
            panelVDiv = new javax.swing.JPanel();
            lbVDivCh1 = new javax.swing.JTextField();
            lbVDivCh2 = new javax.swing.JTextField();
            lbVDivCh3 = new javax.swing.JTextField();
            lbVDivCh4 = new javax.swing.JTextField();
            lbTDivAllChs = new javax.swing.JTextField();
            lbFDivAllChs = new javax.swing.JTextField();
            RightSplitPane = new CloseableTabbedPane();
            //new JTabbedPaneWithCloseIcons();
            MenuBar = new javax.swing.JMenuBar();
            MnuFile = new javax.swing.JMenu();
            MnuSaveAs = new javax.swing.JMenuItem();
            Sep3 = new javax.swing.JSeparator();
            MnuPrint = new javax.swing.JMenuItem();
            Sep4 = new javax.swing.JSeparator();
            MnuExit = new javax.swing.JMenuItem();
            MnuEdit = new javax.swing.JMenu();
            Sep5 = new javax.swing.JSeparator();
            MnuImportGraph = new javax.swing.JMenuItem();
            MnuExport = new javax.swing.JMenuItem();
            MnuView = new javax.swing.JMenu();
            MnuViewonFullScreen = new javax.swing.JMenuItem();
            MnuRestoretoDefaultScreenSize = new javax.swing.JMenuItem();
            Sep6 = new javax.swing.JSeparator();
            MnuGraphOnly = new javax.swing.JMenuItem();
            MnuGraphandOp = new javax.swing.JMenuItem();
            Sep6_1 = new javax.swing.JSeparator();
            MnuChOptions = new javax.swing.JMenu();
            MnuCh1 = new javax.swing.JCheckBoxMenuItem();
            MnuCh2 = new javax.swing.JCheckBoxMenuItem();
            MnuCh3 = new javax.swing.JCheckBoxMenuItem();
            MnuCh4 = new javax.swing.JCheckBoxMenuItem();
            MnuTrigger = new javax.swing.JMenuItem();
            Sep9_0 = new javax.swing.JSeparator();
            MnuZoom = new javax.swing.JMenu();
            MnuZoomIn = new javax.swing.JMenuItem();
            MnuZoomOut = new javax.swing.JMenuItem();
            MnuZoomCustom = new javax.swing.JMenuItem();
            MnuZoomCancel = new javax.swing.JMenuItem();
            Sep7 = new javax.swing.JSeparator();
            MnuDisplayonOneGraph = new javax.swing.JMenuItem();
            MnuSplitGraphs = new javax.swing.JMenuItem();
            MnuRun = new javax.swing.JMenu();
            MnuRunGraph = new javax.swing.JMenuItem();
            MnuStop = new javax.swing.JMenuItem();
            Sep8 = new javax.swing.JSeparator();
            MnuSingle = new javax.swing.JMenuItem();
            MnuTools = new javax.swing.JMenu();
            MnuAddHorizontalMarker = new javax.swing.JCheckBoxMenuItem();
            MnuAddVerticalMarker = new javax.swing.JCheckBoxMenuItem();
            Sep9_1 = new javax.swing.JSeparator();
            jMenu1 = new javax.swing.JMenu();
            OffsetZeroCh1 = new javax.swing.JMenuItem();
            OffsetZeroCh2 = new javax.swing.JMenuItem();
            OffsetZeroCh3 = new javax.swing.JMenuItem();
            OffsetZeroCh4 = new javax.swing.JMenuItem();
            jSeparator6 = new javax.swing.JSeparator();
            MnuMainFFT = new javax.swing.JMenu();
            MnuFFT = new javax.swing.JMenuItem();
            MnuRemoveFFT = new javax.swing.JMenuItem();
            MnuFFTOnly = new javax.swing.JMenuItem();
            jSeparator4 = new javax.swing.JSeparator();
            MnuClearMarkers = new javax.swing.JMenu();
            MnuClearAllMarkers = new javax.swing.JMenuItem();
            Sep9_2 = new javax.swing.JSeparator();
            MnuClearHorizontalMarkers = new javax.swing.JMenuItem();
            MnuClearVerticalMarkers = new javax.swing.JMenuItem();
            MnuSettings = new javax.swing.JMenu();
            MnuChannelSettings = new javax.swing.JMenuItem();
            MnuChartOptions = new javax.swing.JMenuItem();
            Sep10 = new javax.swing.JSeparator();
            MnuFFTOptions = new javax.swing.JMenu();
            MnuFFTPlotType = new javax.swing.JMenu();
            MnudBVFFTPlot = new javax.swing.JRadioButtonMenuItem();
            MnuLinearFFTPlot = new javax.swing.JRadioButtonMenuItem();
            MnuFFTWindowType = new javax.swing.JMenu();
            MnuRectangular = new javax.swing.JRadioButtonMenuItem();
            MnuHanning = new javax.swing.JRadioButtonMenuItem();
            MnuHamming = new javax.swing.JRadioButtonMenuItem();
            MnuTriangular = new javax.swing.JRadioButtonMenuItem();
            MnuWelch = new javax.swing.JRadioButtonMenuItem();
            jSeparator5 = new javax.swing.JSeparator();
            MnuGraph = new javax.swing.JMenu();
            MnuShowYAxisLabels = new javax.swing.JCheckBoxMenuItem();
            MnuHelp = new javax.swing.JMenu();
            MnuHelpF1 = new javax.swing.JMenuItem();
            jSeparator1 = new javax.swing.JSeparator();
            jMenuItem1 = new javax.swing.JMenuItem();
            MnuTutorial = new javax.swing.JMenuItem();
            jSeparator2 = new javax.swing.JSeparator();
            MnuUG = new javax.swing.JMenuItem();
            jSeparator3 = new javax.swing.JSeparator();
            jMenuItem2 = new javax.swing.JMenuItem();
            MnuSupport = new javax.swing.JMenuItem();
            Sep12 = new javax.swing.JSeparator();
            MnuAbout = new javax.swing.JMenuItem();

            WannaCancelZoomChkBox.setText("Don't show this message again");

            ScrollCustomZoomPane.setBorder(null);

            XAxisOptions.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED), "X Axis Custom Zoom"));

            CustomZoomXAxisLabel1.setText("Zoom X-Axis to ");

            SpinnerNumberModel XAxisSpinnerModel = new SpinnerNumberModel(100, //initial value
                0, //min
                400, //max
                1);                //step
            XAxisZoomValue = new JSpinner(XAxisSpinnerModel);

            OKXAxisCustomZoom.setText("OK");
            OKXAxisCustomZoom.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    OKXAxisCustomZoomActionPerformed(evt);
                }
            });
            OKXAxisCustomZoom.addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusGained(java.awt.event.FocusEvent evt) {
                    OKXAxisCustomZoomFocusGained(evt);
                }
            });

            CustomZoomXAxisLabel2.setText("%");

            javax.swing.GroupLayout XAxisOptionsLayout = new javax.swing.GroupLayout(XAxisOptions);
            XAxisOptions.setLayout(XAxisOptionsLayout);
            XAxisOptionsLayout.setHorizontalGroup(
                XAxisOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(XAxisOptionsLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(CustomZoomXAxisLabel1)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(XAxisOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(OKXAxisCustomZoom, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(XAxisZoomValue, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(CustomZoomXAxisLabel2)
                    .addContainerGap(29, Short.MAX_VALUE))
            );
            XAxisOptionsLayout.setVerticalGroup(
                XAxisOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(XAxisOptionsLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(XAxisOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(CustomZoomXAxisLabel1)
                        .addComponent(CustomZoomXAxisLabel2)
                        .addComponent(XAxisZoomValue, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(OKXAxisCustomZoom)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );

            ((JSpinner.DefaultEditor) XAxisZoomValue.getEditor()).getTextField().addKeyListener(new KeyAdapter() {
                public void keyTyped(KeyEvent e) {
                    if (!Character.isDigit(e.getKeyChar()) || (((JSpinner.DefaultEditor) XAxisZoomValue.getEditor()).getTextField().getText().length()>3)) {
                        e.consume();
                    }
                }
            });

            ((JSpinner.DefaultEditor) XAxisZoomValue.getEditor()).getTextField().addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == 10){//enter
                        OKXAxisCustomZoom.grabFocus();
                        EnterPressedJSpinner = true;
                    }else{
                        EnterPressedJSpinner = false;
                    }
                }
            });

            YAxisOptions.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED), "Y Axis Custom Zoom"));

            CustomZoomYAxisLabel1.setText("Zoom Y-Axis to ");

            SpinnerNumberModel YAxisSpinnerModel = new SpinnerNumberModel(100, //initial value
                0, //min
                400, //max
                1);                //step
            YAxisZoomValue = new JSpinner(YAxisSpinnerModel);

            OKYAxisCustomZoom.setText("OK");
            OKYAxisCustomZoom.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    OKYAxisCustomZoomActionPerformed(evt);
                }
            });
            OKYAxisCustomZoom.addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusGained(java.awt.event.FocusEvent evt) {
                    OKYAxisCustomZoomFocusGained(evt);
                }
            });

            CustomZoomYAxisLabel2.setText("%");

            javax.swing.GroupLayout YAxisOptionsLayout = new javax.swing.GroupLayout(YAxisOptions);
            YAxisOptions.setLayout(YAxisOptionsLayout);
            YAxisOptionsLayout.setHorizontalGroup(
                YAxisOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(YAxisOptionsLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(CustomZoomYAxisLabel1)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(YAxisOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(OKYAxisCustomZoom, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(YAxisZoomValue, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(CustomZoomYAxisLabel2)
                    .addContainerGap(29, Short.MAX_VALUE))
            );
            YAxisOptionsLayout.setVerticalGroup(
                YAxisOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(YAxisOptionsLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(YAxisOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(CustomZoomYAxisLabel1)
                        .addComponent(CustomZoomYAxisLabel2)
                        .addComponent(YAxisZoomValue, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(OKYAxisCustomZoom)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );

            ((JSpinner.DefaultEditor) YAxisZoomValue.getEditor()).getTextField().addKeyListener(new KeyAdapter() {
                public void keyTyped(KeyEvent e) {
                    if ((!Character.isDigit(e.getKeyChar()) || (((JSpinner.DefaultEditor) YAxisZoomValue.getEditor()).getTextField().getText().length()>3))) {
                        e.consume();
                    }
                }
            });

            ((JSpinner.DefaultEditor) YAxisZoomValue.getEditor()).getTextField().addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == 10){//enter
                        OKYAxisCustomZoom.grabFocus();
                        EnterPressedJSpinner = true;
                    }else{
                        EnterPressedJSpinner = false;
                    }
                }
            });

            buttonCancelZoom.setIcon(new javax.swing.ImageIcon(getClass().getResource("/usbscope50software/Icons/ZoomCancel16.gif"))); // NOI18N
            buttonCancelZoom.setText("Cancel Zoom");
            buttonCancelZoom.setToolTipText("Cancel zoom");
            buttonCancelZoom.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    buttonCancelZoomActionPerformed(evt);
                }
            });

            javax.swing.GroupLayout CustomZoomPanelLayout = new javax.swing.GroupLayout(CustomZoomPanel);
            CustomZoomPanel.setLayout(CustomZoomPanelLayout);
            CustomZoomPanelLayout.setHorizontalGroup(
                CustomZoomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(CustomZoomPanelLayout.createSequentialGroup()
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(CustomZoomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(XAxisOptions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(YAxisOptions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(buttonCancelZoom))
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
            CustomZoomPanelLayout.setVerticalGroup(
                CustomZoomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(CustomZoomPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(XAxisOptions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(YAxisOptions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(buttonCancelZoom)
                    .addContainerGap(51, Short.MAX_VALUE))
            );

            ScrollCustomZoomPane.setViewportView(CustomZoomPanel);

            ScrollTriggerPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));

            SliderThreshold.setMajorTickSpacing(50);
            SliderThreshold.setMinimum(-100);
            SliderThreshold.setMinorTickSpacing(25);
            SliderThreshold.setOrientation(javax.swing.JSlider.VERTICAL);
            SliderThreshold.setPaintTrack(false);
            SliderThreshold.setToolTipText("Set the trigger threshold");
            SliderThreshold.setValue(0);
            SliderThreshold.setEnabled(false);
            SliderThreshold.addChangeListener(new javax.swing.event.ChangeListener() {
                public void stateChanged(javax.swing.event.ChangeEvent evt) {
                    SliderThresholdStateChanged(evt);
                }
            });
            SliderThreshold.addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusGained(java.awt.event.FocusEvent evt) {
                    SliderThresholdFocusGained(evt);
                }
                public void focusLost(java.awt.event.FocusEvent evt) {
                    SliderThresholdFocusLost(evt);
                }
            });

            SelectTriggerChPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
            SelectTriggerChPanel.setLayout(new javax.swing.BoxLayout(SelectTriggerChPanel, javax.swing.BoxLayout.Y_AXIS));

            TriggerOnLabel.setText("Trigger on:");
            SelectTriggerChPanel.add(TriggerOnLabel);

            ButGroupTriggerOn.add(RadioButTrigOnChan1);
            RadioButTrigOnChan1.setText("Channel 1");
            RadioButTrigOnChan1.setToolTipText("Select trigger channel");
            RadioButTrigOnChan1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    RadioButTrigOnChan1ActionPerformed(evt);
                }
            });
            SelectTriggerChPanel.add(RadioButTrigOnChan1);

            ButGroupTriggerOn.add(RadioButTrigOnChan2);
            RadioButTrigOnChan2.setText("Channel 2");
            RadioButTrigOnChan2.setToolTipText("Select trigger channel");
            RadioButTrigOnChan2.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    RadioButTrigOnChan2ActionPerformed(evt);
                }
            });
            SelectTriggerChPanel.add(RadioButTrigOnChan2);

            ButGroupTriggerOn.add(RadioButTrigOnChan3);
            RadioButTrigOnChan3.setText("Channel 3");
            RadioButTrigOnChan3.setToolTipText("Select trigger channel");
            RadioButTrigOnChan3.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    RadioButTrigOnChan3ActionPerformed(evt);
                }
            });
            SelectTriggerChPanel.add(RadioButTrigOnChan3);

            ButGroupTriggerOn.add(RadioButTrigOnChan4);
            RadioButTrigOnChan4.setText("Channel 4");
            RadioButTrigOnChan4.setToolTipText("Select trigger channel");
            RadioButTrigOnChan4.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    RadioButTrigOnChan4ActionPerformed(evt);
                }
            });
            SelectTriggerChPanel.add(RadioButTrigOnChan4);

            jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
            jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.Y_AXIS));

            jLabel9.setText("Trigger mode:");
            jPanel3.add(jLabel9);

            ButGroupTriggerMode.add(TriggerModeFree);
            TriggerModeFree.setText("Free");
            TriggerModeFree.setToolTipText("Select trigger mode");
            TriggerModeFree.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    TriggerModeFreeActionPerformed(evt);
                }
            });
            jPanel3.add(TriggerModeFree);

            ButGroupTriggerMode.add(TriggerModeRisingEdge);
            TriggerModeRisingEdge.setText("Rising Edge");
            TriggerModeRisingEdge.setToolTipText("Select trigger mode");
            TriggerModeRisingEdge.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    TriggerModeRisingEdgeActionPerformed(evt);
                }
            });
            jPanel3.add(TriggerModeRisingEdge);

            ButGroupTriggerMode.add(TriggerModeFallingEdge);
            TriggerModeFallingEdge.setText("Falling Edge");
            TriggerModeFallingEdge.setToolTipText("Select trigger mode");
            TriggerModeFallingEdge.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    TriggerModeFallingEdgeActionPerformed(evt);
                }
            });
            jPanel3.add(TriggerModeFallingEdge);

            ButGroupTriggerMode.add(TriggerModeLessThan);
            TriggerModeLessThan.setText("Less Than");
            TriggerModeLessThan.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    TriggerModeLessThanActionPerformed(evt);
                }
            });
            jPanel3.add(TriggerModeLessThan);

            ButGroupTriggerMode.add(TriggerModeGreaterThan);
            TriggerModeGreaterThan.setText("Greater Than");
            TriggerModeGreaterThan.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    TriggerModeGreaterThanActionPerformed(evt);
                }
            });
            jPanel3.add(TriggerModeGreaterThan);

            javax.swing.GroupLayout TriggerPanelLayout = new javax.swing.GroupLayout(TriggerPanel);
            TriggerPanel.setLayout(TriggerPanelLayout);
            TriggerPanelLayout.setHorizontalGroup(
                TriggerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(TriggerPanelLayout.createSequentialGroup()
                    .addComponent(SliderThreshold, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(TriggerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(SelectTriggerChPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
            TriggerPanelLayout.setVerticalGroup(
                TriggerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(TriggerPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(TriggerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(SliderThreshold, javax.swing.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE)
                        .addGroup(TriggerPanelLayout.createSequentialGroup()
                            .addComponent(SelectTriggerChPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addContainerGap())
            );

            ScrollTriggerPanel.setViewportView(TriggerPanel);

            ScrollOptionsCh1.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
            ScrollOptionsCh1.setMinimumSize(new java.awt.Dimension(1, 1));

            OptionsCh1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
            OptionsCh1.setRequestFocusEnabled(false);
            OptionsCh1.addComponentListener(new java.awt.event.ComponentAdapter() {
                public void componentShown(java.awt.event.ComponentEvent evt) {
                    OptionsCh1ComponentShown(evt);
                }
            });

            ButtonsCh1.setLayout(new java.awt.GridLayout(2, 4, 5, 5));

            INVButtonCh1.setFont(new java.awt.Font("Lucida Sans", 1, 10)); // NOI18N
            INVButtonCh1.setText("INV");
            INVButtonCh1.setToolTipText("Invert Ch1 wave form");
            INVButtonCh1.setIconTextGap(0);
            INVButtonCh1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    INVButtonCh1ActionPerformed(evt);
                }
            });
            ButtonsCh1.add(INVButtonCh1);

            IDButtonCh1.setFont(new java.awt.Font("Lucida Sans", 1, 10)); // NOI18N
            IDButtonCh1.setText("ID");
            IDButtonCh1.setToolTipText("Pulse scope red LED on Ch1 to identify it");
            IDButtonCh1.setIconTextGap(0);
            IDButtonCh1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    IDButtonCh1ActionPerformed(evt);
                }
            });
            ButtonsCh1.add(IDButtonCh1);

            FFTButtonCh1.setFont(new java.awt.Font("Lucida Sans", 1, 10)); // NOI18N
            FFTButtonCh1.setText("FFT");
            FFTButtonCh1.setToolTipText("Display Ch1 signal on the graph");
            FFTButtonCh1.setIconTextGap(0);
            FFTButtonCh1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    FFTButtonCh1ActionPerformed(evt);
                }
            });
            ButtonsCh1.add(FFTButtonCh1);

            ONButtonCh1.setFont(new java.awt.Font("Lucida Sans", 1, 10)); // NOI18N
            ONButtonCh1.setText("ON");
            ONButtonCh1.setToolTipText("Display Ch1 signal on the graph");
            ONButtonCh1.setIconTextGap(0);
            ONButtonCh1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    ONButtonCh1ActionPerformed(evt);
                }
            });
            ButtonsCh1.add(ONButtonCh1);

            COMPButtonCh1.setFont(new java.awt.Font("Lucida Sans", 1, 10)); // NOI18N
            COMPButtonCh1.setText("COMP");
            COMPButtonCh1.setToolTipText("Ch1 scope probe compensation square wave control");
            COMPButtonCh1.setIconTextGap(0);
            COMPButtonCh1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    COMPButtonCh1ActionPerformed(evt);
                }
            });
            ButtonsCh1.add(COMPButtonCh1);

            NullButtonCh1.setFont(new java.awt.Font("Lucida Sans", 1, 10)); // NOI18N
            NullButtonCh1.setText("Null");
            NullButtonCh1.setToolTipText("Pulse scope red LED on Ch1 to identify it");
            NullButtonCh1.setIconTextGap(0);
            NullButtonCh1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    NullButtonCh1ActionPerformed(evt);
                }
            });
            ButtonsCh1.add(NullButtonCh1);

            javax.swing.GroupLayout PanelButtonsCh1Layout = new javax.swing.GroupLayout(PanelButtonsCh1);
            PanelButtonsCh1.setLayout(PanelButtonsCh1Layout);
            PanelButtonsCh1Layout.setHorizontalGroup(
                PanelButtonsCh1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(PanelButtonsCh1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(ButtonsCh1, javax.swing.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
                    .addContainerGap())
            );
            PanelButtonsCh1Layout.setVerticalGroup(
                PanelButtonsCh1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(PanelButtonsCh1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(ButtonsCh1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );

            ProbeCouplingCh1.setLayout(new java.awt.GridBagLayout());

            buttGroupCouplingCh1.add(OpACCouplingCh1);
            OpACCouplingCh1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
            OpACCouplingCh1.setSelected(true);
            OpACCouplingCh1.setText("AC Coupling");
            OpACCouplingCh1.setToolTipText("Select Ch1 coupling");
            OpACCouplingCh1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    OpACCouplingCh1ActionPerformed(evt);
                }
            });
            ProbeCouplingCh1.add(OpACCouplingCh1, new java.awt.GridBagConstraints());

            buttGroupCouplingCh1.add(OpDCCouplingCh1);
            OpDCCouplingCh1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
            OpDCCouplingCh1.setText("DC Coupling");
            OpDCCouplingCh1.setToolTipText("Select Ch1 coupling");
            OpDCCouplingCh1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    OpDCCouplingCh1ActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 1;
            ProbeCouplingCh1.add(OpDCCouplingCh1, gridBagConstraints);

            buttGroupCouplingCh1.add(OpGNDCh1);
            OpGNDCh1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
            OpGNDCh1.setText("GND");
            OpGNDCh1.setToolTipText("Select Ch1 coupling");
            OpGNDCh1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    OpGNDCh1ActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 2;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            ProbeCouplingCh1.add(OpGNDCh1, gridBagConstraints);

            buttGroupProbeCh1.add(Opx1ProbeCh1);
            Opx1ProbeCh1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
            Opx1ProbeCh1.setSelected(true);
            Opx1ProbeCh1.setText(" x1  Probe");
            Opx1ProbeCh1.setToolTipText("Set this to match Ch1 probe");
            Opx1ProbeCh1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    Opx1ProbeCh1ActionPerformed(evt);
                }
            });
            ProbeCouplingCh1.add(Opx1ProbeCh1, new java.awt.GridBagConstraints());

            buttGroupProbeCh1.add(Opx10ProbeCh1);
            Opx10ProbeCh1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
            Opx10ProbeCh1.setText("x10 Probe");
            Opx10ProbeCh1.setToolTipText("Set this to match Ch1 probe");
            Opx10ProbeCh1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    Opx10ProbeCh1ActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 1;
            ProbeCouplingCh1.add(Opx10ProbeCh1, gridBagConstraints);

            ClearOffsetCh1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
            ClearOffsetCh1.setText("Offset 0");
            ClearOffsetCh1.setToolTipText("Click to clear channel 1 offset");
            ClearOffsetCh1.setMaximumSize(new java.awt.Dimension(81, 25));
            ClearOffsetCh1.setMinimumSize(new java.awt.Dimension(81, 25));
            ClearOffsetCh1.setPreferredSize(new java.awt.Dimension(81, 25));
            ClearOffsetCh1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    ClearOffsetCh1ActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 2;
            ProbeCouplingCh1.add(ClearOffsetCh1, gridBagConstraints);

            ArrowsPanelLineCh1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

            ArrowPanelColorCh1.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED), new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED)));

            RightArrowVDivCh1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/usbscope50software/Icons/ScopeArrowUp.gif"))); // NOI18N
            RightArrowVDivCh1.setToolTipText("Click here to change Ch1 V/Div setting");
            RightArrowVDivCh1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            RightArrowVDivCh1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    RightArrowVDivCh1ActionPerformed(evt);
                }
            });
            RightArrowVDivCh1.addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusLost(java.awt.event.FocusEvent evt) {
                    RightArrowVDivCh1FocusLost(evt);
                }
            });

            LeftArrowVDivCh1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/usbscope50software/Icons/ScopeArrowDown.gif"))); // NOI18N
            LeftArrowVDivCh1.setToolTipText("Click here to change Ch1 V/Div setting");
            LeftArrowVDivCh1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            LeftArrowVDivCh1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    LeftArrowVDivCh1ActionPerformed(evt);
                }
            });
            LeftArrowVDivCh1.addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusLost(java.awt.event.FocusEvent evt) {
                    LeftArrowVDivCh1FocusLost(evt);
                }
            });
            LeftArrowVDivCh1.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyPressed(java.awt.event.KeyEvent evt) {
                    LeftArrowVDivCh1KeyPressed(evt);
                }
            });

            LeftArrowSecDivCh1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/usbscope50software/Icons/ScopeArrowLeft.gif"))); // NOI18N
            LeftArrowSecDivCh1.setToolTipText("Click here to change time base setting");
            LeftArrowSecDivCh1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            LeftArrowSecDivCh1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    LeftArrowSecDivCh1ActionPerformed(evt);
                }
            });

            LbCurrentTimeBaseCh1.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
            LbCurrentTimeBaseCh1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            LbCurrentTimeBaseCh1.setText(" 50 us/div");
            LbCurrentTimeBaseCh1.setToolTipText("Current time base setting");

            RightArrowSecDivCh1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/usbscope50software/Icons/ScopeArrowRight.gif"))); // NOI18N
            RightArrowSecDivCh1.setToolTipText("Click here to change time base setting");
            RightArrowSecDivCh1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            RightArrowSecDivCh1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    RightArrowSecDivCh1ActionPerformed(evt);
                }
            });

            javax.swing.GroupLayout ArrowPanelCh1Layout = new javax.swing.GroupLayout(ArrowPanelCh1);
            ArrowPanelCh1.setLayout(ArrowPanelCh1Layout);
            ArrowPanelCh1Layout.setHorizontalGroup(
                ArrowPanelCh1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(ArrowPanelCh1Layout.createSequentialGroup()
                    .addGap(0, 0, 0)
                    .addComponent(LeftArrowSecDivCh1, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(ArrowPanelCh1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(RightArrowVDivCh1, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(ArrowPanelCh1Layout.createSequentialGroup()
                            .addGroup(ArrowPanelCh1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(LbCurrentTimeBaseCh1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(LeftArrowVDivCh1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(RightArrowSecDivCh1, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGap(0, 0, 0))
            );
            ArrowPanelCh1Layout.setVerticalGroup(
                ArrowPanelCh1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(ArrowPanelCh1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(RightArrowVDivCh1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(ArrowPanelCh1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(RightArrowSecDivCh1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(LeftArrowSecDivCh1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(LbCurrentTimeBaseCh1, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(8, 8, 8)
                    .addComponent(LeftArrowVDivCh1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
            );

            javax.swing.GroupLayout ArrowPanelColorCh1Layout = new javax.swing.GroupLayout(ArrowPanelColorCh1);
            ArrowPanelColorCh1.setLayout(ArrowPanelColorCh1Layout);
            ArrowPanelColorCh1Layout.setHorizontalGroup(
                ArrowPanelColorCh1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(ArrowPanelColorCh1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(ArrowPanelCh1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
            );
            ArrowPanelColorCh1Layout.setVerticalGroup(
                ArrowPanelColorCh1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(ArrowPanelColorCh1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(ArrowPanelCh1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
            );

            javax.swing.GroupLayout ArrowsPanelLineCh1Layout = new javax.swing.GroupLayout(ArrowsPanelLineCh1);
            ArrowsPanelLineCh1.setLayout(ArrowsPanelLineCh1Layout);
            ArrowsPanelLineCh1Layout.setHorizontalGroup(
                ArrowsPanelLineCh1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(ArrowsPanelLineCh1Layout.createSequentialGroup()
                    .addGap(0, 0, 0)
                    .addComponent(ArrowPanelColorCh1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, 0))
            );
            ArrowsPanelLineCh1Layout.setVerticalGroup(
                ArrowsPanelLineCh1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(ArrowsPanelLineCh1Layout.createSequentialGroup()
                    .addGap(0, 0, 0)
                    .addComponent(ArrowPanelColorCh1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, 0))
            );

            OffsetSliderCh1.setMaximum(10000);
            OffsetSliderCh1.setMinimum(-10000);
            OffsetSliderCh1.setOrientation(javax.swing.JSlider.VERTICAL);
            OffsetSliderCh1.setToolTipText("Set Channel 1 Offset");
            OffsetSliderCh1.setValue(0);
            OffsetSliderCh1.setValueIsAdjusting(true);
            OffsetSliderCh1.addChangeListener(new javax.swing.event.ChangeListener() {
                public void stateChanged(javax.swing.event.ChangeEvent evt) {
                    OffsetSliderCh1StateChanged(evt);
                }
            });
            OffsetSliderCh1.addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusGained(java.awt.event.FocusEvent evt) {
                    OffsetSliderCh1FocusGained(evt);
                }
                public void focusLost(java.awt.event.FocusEvent evt) {
                    OffsetSliderCh1FocusLost(evt);
                }
            });

            javax.swing.GroupLayout OptionsCh1Layout = new javax.swing.GroupLayout(OptionsCh1);
            OptionsCh1.setLayout(OptionsCh1Layout);
            OptionsCh1Layout.setHorizontalGroup(
                OptionsCh1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, OptionsCh1Layout.createSequentialGroup()
                    .addGap(0, 0, 0)
                    .addComponent(OffsetSliderCh1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, 0)
                    .addGroup(OptionsCh1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(PanelButtonsCh1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(ArrowsPanelLineCh1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(ProbeCouplingCh1, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(0, 0, 0))
            );
            OptionsCh1Layout.setVerticalGroup(
                OptionsCh1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(OptionsCh1Layout.createSequentialGroup()
                    .addGap(0, 0, 0)
                    .addGroup(OptionsCh1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(OffsetSliderCh1, javax.swing.GroupLayout.DEFAULT_SIZE, 357, Short.MAX_VALUE)
                        .addGroup(OptionsCh1Layout.createSequentialGroup()
                            .addComponent(ArrowsPanelLineCh1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(ProbeCouplingCh1, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(PanelButtonsCh1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGap(0, 0, 0))
            );

            ArrowsPanelLineCh1.setBorder(javax.swing.BorderFactory.createLineBorder(ColorCh1));

            ScrollOptionsCh1.setViewportView(OptionsCh1);

            ScrollOptionsCh2.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
            ScrollOptionsCh2.setMinimumSize(new java.awt.Dimension(1, 1));

            OptionsCh2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
            OptionsCh2.setRequestFocusEnabled(false);
            OptionsCh2.addComponentListener(new java.awt.event.ComponentAdapter() {
                public void componentShown(java.awt.event.ComponentEvent evt) {
                    OptionsCh2ComponentShown(evt);
                }
            });

            ButtonsCh2.setLayout(new java.awt.GridLayout(2, 4, 5, 5));

            INVButtonCh2.setFont(new java.awt.Font("Lucida Sans", 1, 10)); // NOI18N
            INVButtonCh2.setText("INV");
            INVButtonCh2.setToolTipText("Invert Ch2 wave form");
            INVButtonCh2.setIconTextGap(0);
            INVButtonCh2.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    INVButtonCh2ActionPerformed(evt);
                }
            });
            ButtonsCh2.add(INVButtonCh2);

            IDButtonCh2.setFont(new java.awt.Font("Lucida Sans", 1, 10)); // NOI18N
            IDButtonCh2.setText("ID");
            IDButtonCh2.setToolTipText("Pulse scope red LED on Ch2 to identify it");
            IDButtonCh2.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    IDButtonCh2ActionPerformed(evt);
                }
            });
            ButtonsCh2.add(IDButtonCh2);

            FFTButtonCh2.setFont(new java.awt.Font("Lucida Sans", 1, 10)); // NOI18N
            FFTButtonCh2.setText("FFT");
            FFTButtonCh2.setToolTipText("Display Ch1 signal on the graph");
            FFTButtonCh2.setIconTextGap(0);
            FFTButtonCh2.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    FFTButtonCh2ActionPerformed(evt);
                }
            });
            ButtonsCh2.add(FFTButtonCh2);

            ONButtonCh2.setFont(new java.awt.Font("Lucida Sans", 1, 10)); // NOI18N
            ONButtonCh2.setText("ON");
            ONButtonCh2.setToolTipText("Display Ch2 signal on the graph");
            ONButtonCh2.setIconTextGap(0);
            ONButtonCh2.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    ONButtonCh2ActionPerformed(evt);
                }
            });
            ButtonsCh2.add(ONButtonCh2);

            COMPButtonCh2.setFont(new java.awt.Font("Lucida Sans", 1, 10)); // NOI18N
            COMPButtonCh2.setText("COMP");
            COMPButtonCh2.setToolTipText("Ch2 scope probe compensation square wave control");
            COMPButtonCh2.setIconTextGap(0);
            COMPButtonCh2.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    COMPButtonCh2ActionPerformed(evt);
                }
            });
            ButtonsCh2.add(COMPButtonCh2);

            NullButtonCh2.setFont(new java.awt.Font("Lucida Sans", 1, 10)); // NOI18N
            NullButtonCh2.setText("Null");
            NullButtonCh2.setToolTipText("Pulse scope red LED on Ch2 to identify it");
            NullButtonCh2.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    NullButtonCh2ActionPerformed(evt);
                }
            });
            ButtonsCh2.add(NullButtonCh2);

            javax.swing.GroupLayout PanelButtonsCh2Layout = new javax.swing.GroupLayout(PanelButtonsCh2);
            PanelButtonsCh2.setLayout(PanelButtonsCh2Layout);
            PanelButtonsCh2Layout.setHorizontalGroup(
                PanelButtonsCh2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(PanelButtonsCh2Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(ButtonsCh2, javax.swing.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
                    .addContainerGap())
            );
            PanelButtonsCh2Layout.setVerticalGroup(
                PanelButtonsCh2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(PanelButtonsCh2Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(ButtonsCh2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );

            ProbeCouplingCh2.setLayout(new java.awt.GridBagLayout());

            buttGroupCouplingCh2.add(OpACCouplingCh2);
            OpACCouplingCh2.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
            OpACCouplingCh2.setSelected(true);
            OpACCouplingCh2.setText("AC Coupling");
            OpACCouplingCh2.setToolTipText("Select Ch2 coupling");
            OpACCouplingCh2.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    OpACCouplingCh2ActionPerformed(evt);
                }
            });
            ProbeCouplingCh2.add(OpACCouplingCh2, new java.awt.GridBagConstraints());

            buttGroupCouplingCh2.add(OpDCCouplingCh2);
            OpDCCouplingCh2.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
            OpDCCouplingCh2.setText("DC Coupling");
            OpDCCouplingCh2.setToolTipText("Select Ch2 coupling");
            OpDCCouplingCh2.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    OpDCCouplingCh2ActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 1;
            ProbeCouplingCh2.add(OpDCCouplingCh2, gridBagConstraints);

            buttGroupCouplingCh2.add(OpGNDCh2);
            OpGNDCh2.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
            OpGNDCh2.setText("GND");
            OpGNDCh2.setToolTipText("Select Ch2 coupling");
            OpGNDCh2.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    OpGNDCh2ActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 2;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            ProbeCouplingCh2.add(OpGNDCh2, gridBagConstraints);

            buttGroupProbeCh2.add(Opx1ProbeCh2);
            Opx1ProbeCh2.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
            Opx1ProbeCh2.setSelected(true);
            Opx1ProbeCh2.setText(" x1  Probe");
            Opx1ProbeCh2.setToolTipText("Set this to match Ch2 probe");
            Opx1ProbeCh2.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    Opx1ProbeCh2ActionPerformed(evt);
                }
            });
            ProbeCouplingCh2.add(Opx1ProbeCh2, new java.awt.GridBagConstraints());

            buttGroupProbeCh2.add(Opx10ProbeCh2);
            Opx10ProbeCh2.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
            Opx10ProbeCh2.setText("x10 Probe");
            Opx10ProbeCh2.setToolTipText("Set this to match Ch2 probe");
            Opx10ProbeCh2.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    Opx10ProbeCh2ActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 1;
            ProbeCouplingCh2.add(Opx10ProbeCh2, gridBagConstraints);

            ClearOffsetCh2.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
            ClearOffsetCh2.setText("Offset 0");
            ClearOffsetCh2.setToolTipText("Click to clear channel 1 offset");
            ClearOffsetCh2.setMaximumSize(new java.awt.Dimension(81, 25));
            ClearOffsetCh2.setMinimumSize(new java.awt.Dimension(81, 25));
            ClearOffsetCh2.setPreferredSize(new java.awt.Dimension(81, 25));
            ClearOffsetCh2.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    ClearOffsetCh2ActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 2;
            ProbeCouplingCh2.add(ClearOffsetCh2, gridBagConstraints);

            ArrowsPanelLineCh2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

            ArrowPanelColorCh2.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED), new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED)));

            RightArrowVDivCh2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/usbscope50software/Icons/ScopeArrowUp.gif"))); // NOI18N
            RightArrowVDivCh2.setToolTipText("Click here to change Ch2 V/Div setting");
            RightArrowVDivCh2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            RightArrowVDivCh2.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    RightArrowVDivCh2ActionPerformed(evt);
                }
            });
            RightArrowVDivCh2.addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusLost(java.awt.event.FocusEvent evt) {
                    RightArrowVDivCh2FocusLost(evt);
                }
            });

            LeftArrowVDivCh2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/usbscope50software/Icons/ScopeArrowDown.gif"))); // NOI18N
            LeftArrowVDivCh2.setToolTipText("Click here to change Ch2 V/Div setting");
            LeftArrowVDivCh2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            LeftArrowVDivCh2.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    LeftArrowVDivCh2ActionPerformed(evt);
                }
            });
            LeftArrowVDivCh2.addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusLost(java.awt.event.FocusEvent evt) {
                    LeftArrowVDivCh2FocusLost(evt);
                }
            });

            LeftArrowSecDivCh2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/usbscope50software/Icons/ScopeArrowLeft.gif"))); // NOI18N
            LeftArrowSecDivCh2.setToolTipText("Click here to change time base setting");
            LeftArrowSecDivCh2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            LeftArrowSecDivCh2.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    LeftArrowSecDivCh2ActionPerformed(evt);
                }
            });

            LbCurrentTimeBaseCh2.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
            LbCurrentTimeBaseCh2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            LbCurrentTimeBaseCh2.setText(" 50 us/div");
            LbCurrentTimeBaseCh2.setToolTipText("Current time base setting");

            RightArrowSecDivCh2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/usbscope50software/Icons/ScopeArrowRight.gif"))); // NOI18N
            RightArrowSecDivCh2.setToolTipText("Click here to change time base setting");
            RightArrowSecDivCh2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            RightArrowSecDivCh2.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    RightArrowSecDivCh2ActionPerformed(evt);
                }
            });

            javax.swing.GroupLayout ArrowPanelCh2Layout = new javax.swing.GroupLayout(ArrowPanelCh2);
            ArrowPanelCh2.setLayout(ArrowPanelCh2Layout);
            ArrowPanelCh2Layout.setHorizontalGroup(
                ArrowPanelCh2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(ArrowPanelCh2Layout.createSequentialGroup()
                    .addGap(0, 0, 0)
                    .addComponent(LeftArrowSecDivCh2, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(ArrowPanelCh2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(RightArrowVDivCh2, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(ArrowPanelCh2Layout.createSequentialGroup()
                            .addGroup(ArrowPanelCh2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(LbCurrentTimeBaseCh2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(LeftArrowVDivCh2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(RightArrowSecDivCh2, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGap(0, 0, 0))
            );
            ArrowPanelCh2Layout.setVerticalGroup(
                ArrowPanelCh2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(ArrowPanelCh2Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(RightArrowVDivCh2, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(ArrowPanelCh2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(RightArrowSecDivCh2, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(LeftArrowSecDivCh2, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(LbCurrentTimeBaseCh2, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(8, 8, 8)
                    .addComponent(LeftArrowVDivCh2, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
            );

            javax.swing.GroupLayout ArrowPanelColorCh2Layout = new javax.swing.GroupLayout(ArrowPanelColorCh2);
            ArrowPanelColorCh2.setLayout(ArrowPanelColorCh2Layout);
            ArrowPanelColorCh2Layout.setHorizontalGroup(
                ArrowPanelColorCh2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(ArrowPanelColorCh2Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(ArrowPanelCh2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
            );
            ArrowPanelColorCh2Layout.setVerticalGroup(
                ArrowPanelColorCh2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(ArrowPanelColorCh2Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(ArrowPanelCh2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
            );

            javax.swing.GroupLayout ArrowsPanelLineCh2Layout = new javax.swing.GroupLayout(ArrowsPanelLineCh2);
            ArrowsPanelLineCh2.setLayout(ArrowsPanelLineCh2Layout);
            ArrowsPanelLineCh2Layout.setHorizontalGroup(
                ArrowsPanelLineCh2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(ArrowsPanelLineCh2Layout.createSequentialGroup()
                    .addGap(0, 0, 0)
                    .addComponent(ArrowPanelColorCh2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            );
            ArrowsPanelLineCh2Layout.setVerticalGroup(
                ArrowsPanelLineCh2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(ArrowsPanelLineCh2Layout.createSequentialGroup()
                    .addGap(0, 0, 0)
                    .addComponent(ArrowPanelColorCh2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, 0))
            );

            OffsetSliderCh2.setMaximum(10000);
            OffsetSliderCh2.setMinimum(-10000);
            OffsetSliderCh2.setOrientation(javax.swing.JSlider.VERTICAL);
            OffsetSliderCh2.setToolTipText("Set Channel 2 Offset");
            OffsetSliderCh2.setValue(0);
            OffsetSliderCh2.setValueIsAdjusting(true);
            OffsetSliderCh2.addChangeListener(new javax.swing.event.ChangeListener() {
                public void stateChanged(javax.swing.event.ChangeEvent evt) {
                    OffsetSliderCh2StateChanged(evt);
                }
            });
            OffsetSliderCh2.addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusGained(java.awt.event.FocusEvent evt) {
                    OffsetSliderCh2FocusGained(evt);
                }
                public void focusLost(java.awt.event.FocusEvent evt) {
                    OffsetSliderCh2FocusLost(evt);
                }
            });

            javax.swing.GroupLayout OptionsCh2Layout = new javax.swing.GroupLayout(OptionsCh2);
            OptionsCh2.setLayout(OptionsCh2Layout);
            OptionsCh2Layout.setHorizontalGroup(
                OptionsCh2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(OptionsCh2Layout.createSequentialGroup()
                    .addGap(0, 0, 0)
                    .addComponent(OffsetSliderCh2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, 0)
                    .addGroup(OptionsCh2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(ArrowsPanelLineCh2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(ProbeCouplingCh2, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(PanelButtonsCh2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(0, 0, 0))
            );
            OptionsCh2Layout.setVerticalGroup(
                OptionsCh2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(OptionsCh2Layout.createSequentialGroup()
                    .addGap(0, 0, 0)
                    .addGroup(OptionsCh2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(OffsetSliderCh2, javax.swing.GroupLayout.DEFAULT_SIZE, 357, Short.MAX_VALUE)
                        .addGroup(OptionsCh2Layout.createSequentialGroup()
                            .addComponent(ArrowsPanelLineCh2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(ProbeCouplingCh2, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(PanelButtonsCh2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGap(0, 0, 0))
            );

            ArrowsPanelLineCh2.setBorder(javax.swing.BorderFactory.createLineBorder(ColorCh2));

            ScrollOptionsCh2.setViewportView(OptionsCh2);

            ScrollOptionsCh3.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
            ScrollOptionsCh3.setMinimumSize(new java.awt.Dimension(1, 1));

            OptionsCh3.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
            OptionsCh3.setRequestFocusEnabled(false);
            OptionsCh3.addComponentListener(new java.awt.event.ComponentAdapter() {
                public void componentShown(java.awt.event.ComponentEvent evt) {
                    OptionsCh3ComponentShown(evt);
                }
            });

            ButtonsCh3.setLayout(new java.awt.GridLayout(2, 4, 5, 5));

            INVButtonCh3.setFont(new java.awt.Font("Lucida Sans", 1, 10)); // NOI18N
            INVButtonCh3.setText("INV");
            INVButtonCh3.setToolTipText("Invert Ch3 wave form");
            INVButtonCh3.setIconTextGap(0);
            INVButtonCh3.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    INVButtonCh3ActionPerformed(evt);
                }
            });
            ButtonsCh3.add(INVButtonCh3);

            IDButtonCh3.setFont(new java.awt.Font("Lucida Sans", 1, 10)); // NOI18N
            IDButtonCh3.setText("ID");
            IDButtonCh3.setToolTipText("Pulse scope red LED on Ch3 to identify it");
            IDButtonCh3.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    IDButtonCh3ActionPerformed(evt);
                }
            });
            ButtonsCh3.add(IDButtonCh3);

            FFTButtonCh3.setFont(new java.awt.Font("Lucida Sans", 1, 10)); // NOI18N
            FFTButtonCh3.setText("FFT");
            FFTButtonCh3.setToolTipText("Display Ch1 signal on the graph");
            FFTButtonCh3.setIconTextGap(0);
            FFTButtonCh3.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    FFTButtonCh3ActionPerformed(evt);
                }
            });
            ButtonsCh3.add(FFTButtonCh3);

            ONButtonCh3.setFont(new java.awt.Font("Lucida Sans", 1, 10)); // NOI18N
            ONButtonCh3.setText("ON");
            ONButtonCh3.setToolTipText("Display Ch3 signal on the graph");
            ONButtonCh3.setIconTextGap(0);
            ONButtonCh3.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    ONButtonCh3ActionPerformed(evt);
                }
            });
            ButtonsCh3.add(ONButtonCh3);

            COMPButtonCh3.setFont(new java.awt.Font("Lucida Sans", 1, 10)); // NOI18N
            COMPButtonCh3.setText("COMP");
            COMPButtonCh3.setToolTipText("Ch3 scope probe compensation square wave control");
            COMPButtonCh3.setIconTextGap(0);
            COMPButtonCh3.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    COMPButtonCh3ActionPerformed(evt);
                }
            });
            ButtonsCh3.add(COMPButtonCh3);

            NullButtonCh3.setFont(new java.awt.Font("Lucida Sans", 1, 10)); // NOI18N
            NullButtonCh3.setText("Null");
            NullButtonCh3.setToolTipText("Pulse scope red LED on Ch3 to identify it");
            NullButtonCh3.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    NullButtonCh3ActionPerformed(evt);
                }
            });
            ButtonsCh3.add(NullButtonCh3);

            javax.swing.GroupLayout PanelButtonsCh3Layout = new javax.swing.GroupLayout(PanelButtonsCh3);
            PanelButtonsCh3.setLayout(PanelButtonsCh3Layout);
            PanelButtonsCh3Layout.setHorizontalGroup(
                PanelButtonsCh3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelButtonsCh3Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(ButtonsCh3, javax.swing.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
                    .addContainerGap())
            );
            PanelButtonsCh3Layout.setVerticalGroup(
                PanelButtonsCh3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(PanelButtonsCh3Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(ButtonsCh3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
            );

            ProbeCouplingCh3.setLayout(new java.awt.GridBagLayout());

            buttGroupCouplingCh3.add(OpACCouplingCh3);
            OpACCouplingCh3.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
            OpACCouplingCh3.setSelected(true);
            OpACCouplingCh3.setText("AC Coupling");
            OpACCouplingCh3.setToolTipText("Select Ch3 coupling");
            OpACCouplingCh3.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    OpACCouplingCh3ActionPerformed(evt);
                }
            });
            ProbeCouplingCh3.add(OpACCouplingCh3, new java.awt.GridBagConstraints());

            buttGroupCouplingCh3.add(OpDCCouplingCh3);
            OpDCCouplingCh3.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
            OpDCCouplingCh3.setText("DC Coupling");
            OpDCCouplingCh3.setToolTipText("Select Ch3 coupling");
            OpDCCouplingCh3.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    OpDCCouplingCh3ActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 1;
            ProbeCouplingCh3.add(OpDCCouplingCh3, gridBagConstraints);

            buttGroupCouplingCh3.add(OpGNDCh3);
            OpGNDCh3.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
            OpGNDCh3.setText("GND");
            OpGNDCh3.setToolTipText("Select Ch3 coupling");
            OpGNDCh3.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    OpGNDCh3ActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 2;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            ProbeCouplingCh3.add(OpGNDCh3, gridBagConstraints);

            buttGroupProbeCh3.add(Opx1ProbeCh3);
            Opx1ProbeCh3.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
            Opx1ProbeCh3.setSelected(true);
            Opx1ProbeCh3.setText(" x1  Probe");
            Opx1ProbeCh3.setToolTipText("Set this to match Ch3 probe");
            Opx1ProbeCh3.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    Opx1ProbeCh3ActionPerformed(evt);
                }
            });
            ProbeCouplingCh3.add(Opx1ProbeCh3, new java.awt.GridBagConstraints());

            buttGroupProbeCh3.add(Opx10ProbeCh3);
            Opx10ProbeCh3.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
            Opx10ProbeCh3.setText("x10 Probe");
            Opx10ProbeCh3.setToolTipText("Set this to match Ch3 probe");
            Opx10ProbeCh3.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    Opx10ProbeCh3ActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 1;
            ProbeCouplingCh3.add(Opx10ProbeCh3, gridBagConstraints);

            ClearOffsetCh3.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
            ClearOffsetCh3.setText("Offset 0");
            ClearOffsetCh3.setToolTipText("Click to clear channel 1 offset");
            ClearOffsetCh3.setMaximumSize(new java.awt.Dimension(81, 25));
            ClearOffsetCh3.setMinimumSize(new java.awt.Dimension(81, 25));
            ClearOffsetCh3.setPreferredSize(new java.awt.Dimension(81, 25));
            ClearOffsetCh3.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    ClearOffsetCh3ActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 2;
            ProbeCouplingCh3.add(ClearOffsetCh3, gridBagConstraints);

            ArrowsPanelLineCh3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

            ArrowPanelColorCh3.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED), new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED)));

            RightArrowVDivCh3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/usbscope50software/Icons/ScopeArrowUp.gif"))); // NOI18N
            RightArrowVDivCh3.setToolTipText("Click here to change Ch3 V/Div setting");
            RightArrowVDivCh3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            RightArrowVDivCh3.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    RightArrowVDivCh3ActionPerformed(evt);
                }
            });
            RightArrowVDivCh3.addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusLost(java.awt.event.FocusEvent evt) {
                    RightArrowVDivCh3FocusLost(evt);
                }
            });

            LeftArrowVDivCh3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/usbscope50software/Icons/ScopeArrowDown.gif"))); // NOI18N
            LeftArrowVDivCh3.setToolTipText("Click here to change Ch3 V/Div setting");
            LeftArrowVDivCh3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            LeftArrowVDivCh3.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    LeftArrowVDivCh3ActionPerformed(evt);
                }
            });
            LeftArrowVDivCh3.addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusLost(java.awt.event.FocusEvent evt) {
                    LeftArrowVDivCh3FocusLost(evt);
                }
            });

            LeftArrowSecDivCh3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/usbscope50software/Icons/ScopeArrowLeft.gif"))); // NOI18N
            LeftArrowSecDivCh3.setToolTipText("Click here to change time base setting");
            LeftArrowSecDivCh3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            LeftArrowSecDivCh3.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    LeftArrowSecDivCh3ActionPerformed(evt);
                }
            });

            LbCurrentTimeBaseCh3.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
            LbCurrentTimeBaseCh3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            LbCurrentTimeBaseCh3.setText(" 50 us/div");
            LbCurrentTimeBaseCh3.setToolTipText("Current time base setting");

            RightArrowSecDivCh3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/usbscope50software/Icons/ScopeArrowRight.gif"))); // NOI18N
            RightArrowSecDivCh3.setToolTipText("Click here to change time base setting");
            RightArrowSecDivCh3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            RightArrowSecDivCh3.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    RightArrowSecDivCh3ActionPerformed(evt);
                }
            });

            javax.swing.GroupLayout ArrowPanelCh3Layout = new javax.swing.GroupLayout(ArrowPanelCh3);
            ArrowPanelCh3.setLayout(ArrowPanelCh3Layout);
            ArrowPanelCh3Layout.setHorizontalGroup(
                ArrowPanelCh3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(ArrowPanelCh3Layout.createSequentialGroup()
                    .addGap(0, 0, 0)
                    .addComponent(LeftArrowSecDivCh3, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(ArrowPanelCh3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(RightArrowVDivCh3, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(ArrowPanelCh3Layout.createSequentialGroup()
                            .addGroup(ArrowPanelCh3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(LbCurrentTimeBaseCh3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(LeftArrowVDivCh3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(RightArrowSecDivCh3, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGap(0, 0, 0))
            );
            ArrowPanelCh3Layout.setVerticalGroup(
                ArrowPanelCh3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(ArrowPanelCh3Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(RightArrowVDivCh3, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(ArrowPanelCh3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(RightArrowSecDivCh3, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(LeftArrowSecDivCh3, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(LbCurrentTimeBaseCh3, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(8, 8, 8)
                    .addComponent(LeftArrowVDivCh3, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
            );

            javax.swing.GroupLayout ArrowPanelColorCh3Layout = new javax.swing.GroupLayout(ArrowPanelColorCh3);
            ArrowPanelColorCh3.setLayout(ArrowPanelColorCh3Layout);
            ArrowPanelColorCh3Layout.setHorizontalGroup(
                ArrowPanelColorCh3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(ArrowPanelColorCh3Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(ArrowPanelCh3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
            );
            ArrowPanelColorCh3Layout.setVerticalGroup(
                ArrowPanelColorCh3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(ArrowPanelColorCh3Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(ArrowPanelCh3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
            );

            javax.swing.GroupLayout ArrowsPanelLineCh3Layout = new javax.swing.GroupLayout(ArrowsPanelLineCh3);
            ArrowsPanelLineCh3.setLayout(ArrowsPanelLineCh3Layout);
            ArrowsPanelLineCh3Layout.setHorizontalGroup(
                ArrowsPanelLineCh3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(ArrowsPanelLineCh3Layout.createSequentialGroup()
                    .addGap(0, 0, 0)
                    .addComponent(ArrowPanelColorCh3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            );
            ArrowsPanelLineCh3Layout.setVerticalGroup(
                ArrowsPanelLineCh3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(ArrowsPanelLineCh3Layout.createSequentialGroup()
                    .addGap(0, 0, 0)
                    .addComponent(ArrowPanelColorCh3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, 0))
            );

            OffsetSliderCh3.setMaximum(10000);
            OffsetSliderCh3.setMinimum(-10000);
            OffsetSliderCh3.setOrientation(javax.swing.JSlider.VERTICAL);
            OffsetSliderCh3.setToolTipText("Set Channel 3 Offset");
            OffsetSliderCh3.setValue(0);
            OffsetSliderCh3.setValueIsAdjusting(true);
            OffsetSliderCh3.addChangeListener(new javax.swing.event.ChangeListener() {
                public void stateChanged(javax.swing.event.ChangeEvent evt) {
                    OffsetSliderCh3StateChanged(evt);
                }
            });
            OffsetSliderCh3.addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusGained(java.awt.event.FocusEvent evt) {
                    OffsetSliderCh3FocusGained(evt);
                }
                public void focusLost(java.awt.event.FocusEvent evt) {
                    OffsetSliderCh3FocusLost(evt);
                }
            });

            javax.swing.GroupLayout OptionsCh3Layout = new javax.swing.GroupLayout(OptionsCh3);
            OptionsCh3.setLayout(OptionsCh3Layout);
            OptionsCh3Layout.setHorizontalGroup(
                OptionsCh3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(OptionsCh3Layout.createSequentialGroup()
                    .addGap(0, 0, 0)
                    .addComponent(OffsetSliderCh3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, 0)
                    .addGroup(OptionsCh3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(ProbeCouplingCh3, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(ArrowsPanelLineCh3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(PanelButtonsCh3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(0, 0, 0))
            );
            OptionsCh3Layout.setVerticalGroup(
                OptionsCh3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(OptionsCh3Layout.createSequentialGroup()
                    .addGroup(OptionsCh3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(OptionsCh3Layout.createSequentialGroup()
                            .addComponent(ArrowsPanelLineCh3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(ProbeCouplingCh3, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(PanelButtonsCh3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(OffsetSliderCh3, javax.swing.GroupLayout.DEFAULT_SIZE, 357, Short.MAX_VALUE))
                    .addGap(0, 0, 0))
            );

            ArrowsPanelLineCh3.setBorder(javax.swing.BorderFactory.createLineBorder(ColorCh3));

            ScrollOptionsCh3.setViewportView(OptionsCh3);

            ScrollOptionsCh4.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
            ScrollOptionsCh4.setMinimumSize(new java.awt.Dimension(1, 1));

            OptionsCh4.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
            OptionsCh4.setRequestFocusEnabled(false);
            OptionsCh4.addComponentListener(new java.awt.event.ComponentAdapter() {
                public void componentShown(java.awt.event.ComponentEvent evt) {
                    OptionsCh4ComponentShown(evt);
                }
            });

            ButtonsCh4.setLayout(new java.awt.GridLayout(2, 4, 5, 5));

            INVButtonCh4.setFont(new java.awt.Font("Lucida Sans", 1, 10)); // NOI18N
            INVButtonCh4.setText("INV");
            INVButtonCh4.setToolTipText("Invert Ch4 wave form");
            INVButtonCh4.setIconTextGap(0);
            INVButtonCh4.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    INVButtonCh4ActionPerformed(evt);
                }
            });
            ButtonsCh4.add(INVButtonCh4);

            IDButtonCh4.setFont(new java.awt.Font("Lucida Sans", 1, 10)); // NOI18N
            IDButtonCh4.setText("ID");
            IDButtonCh4.setToolTipText("Pulse scope red LED on Ch4 to identify it");
            IDButtonCh4.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    IDButtonCh4ActionPerformed(evt);
                }
            });
            ButtonsCh4.add(IDButtonCh4);

            FFTButtonCh4.setFont(new java.awt.Font("Lucida Sans", 1, 10)); // NOI18N
            FFTButtonCh4.setText("FFT");
            FFTButtonCh4.setToolTipText("Display Ch1 signal on the graph");
            FFTButtonCh4.setIconTextGap(0);
            FFTButtonCh4.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    FFTButtonCh4ActionPerformed(evt);
                }
            });
            ButtonsCh4.add(FFTButtonCh4);

            ONButtonCh4.setFont(new java.awt.Font("Lucida Sans", 1, 10)); // NOI18N
            ONButtonCh4.setText("ON");
            ONButtonCh4.setToolTipText("Display Ch4 signal on the graph");
            ONButtonCh4.setIconTextGap(0);
            ONButtonCh4.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    ONButtonCh4ActionPerformed(evt);
                }
            });
            ButtonsCh4.add(ONButtonCh4);

            COMPButtonCh4.setFont(new java.awt.Font("Lucida Sans", 1, 10)); // NOI18N
            COMPButtonCh4.setText("COMP");
            COMPButtonCh4.setToolTipText("Ch4 scope probe compensation square wave control");
            COMPButtonCh4.setIconTextGap(0);
            COMPButtonCh4.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    COMPButtonCh4ActionPerformed(evt);
                }
            });
            ButtonsCh4.add(COMPButtonCh4);

            NullButtonCh4.setFont(new java.awt.Font("Lucida Sans", 1, 10)); // NOI18N
            NullButtonCh4.setText("Null");
            NullButtonCh4.setToolTipText("Pulse scope red LED on Ch4 to identify it");
            NullButtonCh4.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    NullButtonCh4ActionPerformed(evt);
                }
            });
            ButtonsCh4.add(NullButtonCh4);

            javax.swing.GroupLayout PanelButtonsCh4Layout = new javax.swing.GroupLayout(PanelButtonsCh4);
            PanelButtonsCh4.setLayout(PanelButtonsCh4Layout);
            PanelButtonsCh4Layout.setHorizontalGroup(
                PanelButtonsCh4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(PanelButtonsCh4Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(ButtonsCh4, javax.swing.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
                    .addContainerGap())
            );
            PanelButtonsCh4Layout.setVerticalGroup(
                PanelButtonsCh4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(PanelButtonsCh4Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(ButtonsCh4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
            );

            ProbeCouplingCh4.setLayout(new java.awt.GridBagLayout());

            buttGroupCouplingCh4.add(OpACCouplingCh4);
            OpACCouplingCh4.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
            OpACCouplingCh4.setSelected(true);
            OpACCouplingCh4.setText("AC Coupling");
            OpACCouplingCh4.setToolTipText("Select Ch4 coupling");
            OpACCouplingCh4.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    OpACCouplingCh4ActionPerformed(evt);
                }
            });
            ProbeCouplingCh4.add(OpACCouplingCh4, new java.awt.GridBagConstraints());

            buttGroupCouplingCh4.add(OpDCCouplingCh4);
            OpDCCouplingCh4.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
            OpDCCouplingCh4.setText("DC Coupling");
            OpDCCouplingCh4.setToolTipText("Select Ch4 coupling");
            OpDCCouplingCh4.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    OpDCCouplingCh4ActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 1;
            ProbeCouplingCh4.add(OpDCCouplingCh4, gridBagConstraints);

            buttGroupCouplingCh4.add(OpGNDCh4);
            OpGNDCh4.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
            OpGNDCh4.setText("GND");
            OpGNDCh4.setToolTipText("Select Ch4 coupling");
            OpGNDCh4.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    OpGNDCh4ActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 2;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            ProbeCouplingCh4.add(OpGNDCh4, gridBagConstraints);

            buttGroupProbeCh4.add(Opx1ProbeCh4);
            Opx1ProbeCh4.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
            Opx1ProbeCh4.setSelected(true);
            Opx1ProbeCh4.setText(" x1  Probe");
            Opx1ProbeCh4.setToolTipText("Set this to match Ch4 probe");
            Opx1ProbeCh4.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    Opx1ProbeCh4ActionPerformed(evt);
                }
            });
            ProbeCouplingCh4.add(Opx1ProbeCh4, new java.awt.GridBagConstraints());

            buttGroupProbeCh4.add(Opx10ProbeCh4);
            Opx10ProbeCh4.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
            Opx10ProbeCh4.setText("x10 Probe");
            Opx10ProbeCh4.setToolTipText("Set this to match Ch4 probe");
            Opx10ProbeCh4.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    Opx10ProbeCh4ActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 1;
            ProbeCouplingCh4.add(Opx10ProbeCh4, gridBagConstraints);

            ClearOffsetCh4.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
            ClearOffsetCh4.setText("Offset 0");
            ClearOffsetCh4.setToolTipText("Click to clear channel 1 offset");
            ClearOffsetCh4.setMaximumSize(new java.awt.Dimension(81, 25));
            ClearOffsetCh4.setMinimumSize(new java.awt.Dimension(81, 25));
            ClearOffsetCh4.setPreferredSize(new java.awt.Dimension(81, 25));
            ClearOffsetCh4.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    ClearOffsetCh4ActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 2;
            ProbeCouplingCh4.add(ClearOffsetCh4, gridBagConstraints);

            ArrowsPanelLineCh4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

            ArrowsPanelColorCh4.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED), new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED)));

            RightArrowVDivCh4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/usbscope50software/Icons/ScopeArrowUp.gif"))); // NOI18N
            RightArrowVDivCh4.setToolTipText("Click here to change Ch4 V/Div setting");
            RightArrowVDivCh4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            RightArrowVDivCh4.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    RightArrowVDivCh4ActionPerformed(evt);
                }
            });
            RightArrowVDivCh4.addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusLost(java.awt.event.FocusEvent evt) {
                    RightArrowVDivCh4FocusLost(evt);
                }
            });

            LeftArrowVDivCh4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/usbscope50software/Icons/ScopeArrowDown.gif"))); // NOI18N
            LeftArrowVDivCh4.setToolTipText("Click here to change Ch4 V/Div setting");
            LeftArrowVDivCh4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            LeftArrowVDivCh4.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    LeftArrowVDivCh4ActionPerformed(evt);
                }
            });
            LeftArrowVDivCh4.addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusLost(java.awt.event.FocusEvent evt) {
                    LeftArrowVDivCh4FocusLost(evt);
                }
            });

            LeftArrowSecDivCh4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/usbscope50software/Icons/ScopeArrowLeft.gif"))); // NOI18N
            LeftArrowSecDivCh4.setToolTipText("Click here to change time base setting");
            LeftArrowSecDivCh4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            LeftArrowSecDivCh4.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    LeftArrowSecDivCh4ActionPerformed(evt);
                }
            });

            LbCurrentTimeBaseCh4.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
            LbCurrentTimeBaseCh4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            LbCurrentTimeBaseCh4.setText(" 50 us/div");
            LbCurrentTimeBaseCh4.setToolTipText("Current time base setting");

            RightArrowSecDivCh4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/usbscope50software/Icons/ScopeArrowRight.gif"))); // NOI18N
            RightArrowSecDivCh4.setToolTipText("Click here to change time base setting");
            RightArrowSecDivCh4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            RightArrowSecDivCh4.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    RightArrowSecDivCh4ActionPerformed(evt);
                }
            });

            javax.swing.GroupLayout ArrowPanelCh4Layout = new javax.swing.GroupLayout(ArrowPanelCh4);
            ArrowPanelCh4.setLayout(ArrowPanelCh4Layout);
            ArrowPanelCh4Layout.setHorizontalGroup(
                ArrowPanelCh4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(ArrowPanelCh4Layout.createSequentialGroup()
                    .addGap(0, 0, 0)
                    .addComponent(LeftArrowSecDivCh4, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(ArrowPanelCh4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(RightArrowVDivCh4, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(ArrowPanelCh4Layout.createSequentialGroup()
                            .addGroup(ArrowPanelCh4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(LbCurrentTimeBaseCh4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(LeftArrowVDivCh4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(RightArrowSecDivCh4, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGap(0, 0, 0))
            );
            ArrowPanelCh4Layout.setVerticalGroup(
                ArrowPanelCh4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(ArrowPanelCh4Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(RightArrowVDivCh4, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(ArrowPanelCh4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(RightArrowSecDivCh4, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(LeftArrowSecDivCh4, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(LbCurrentTimeBaseCh4, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(8, 8, 8)
                    .addComponent(LeftArrowVDivCh4, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
            );

            javax.swing.GroupLayout ArrowsPanelColorCh4Layout = new javax.swing.GroupLayout(ArrowsPanelColorCh4);
            ArrowsPanelColorCh4.setLayout(ArrowsPanelColorCh4Layout);
            ArrowsPanelColorCh4Layout.setHorizontalGroup(
                ArrowsPanelColorCh4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(ArrowsPanelColorCh4Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(ArrowPanelCh4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
            );
            ArrowsPanelColorCh4Layout.setVerticalGroup(
                ArrowsPanelColorCh4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(ArrowsPanelColorCh4Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(ArrowPanelCh4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
            );

            javax.swing.GroupLayout ArrowsPanelLineCh4Layout = new javax.swing.GroupLayout(ArrowsPanelLineCh4);
            ArrowsPanelLineCh4.setLayout(ArrowsPanelLineCh4Layout);
            ArrowsPanelLineCh4Layout.setHorizontalGroup(
                ArrowsPanelLineCh4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(ArrowsPanelLineCh4Layout.createSequentialGroup()
                    .addGap(0, 0, 0)
                    .addComponent(ArrowsPanelColorCh4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            );
            ArrowsPanelLineCh4Layout.setVerticalGroup(
                ArrowsPanelLineCh4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(ArrowsPanelLineCh4Layout.createSequentialGroup()
                    .addGap(0, 0, 0)
                    .addComponent(ArrowsPanelColorCh4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, 0))
            );

            OffsetSliderCh4.setMaximum(10000);
            OffsetSliderCh4.setMinimum(-10000);
            OffsetSliderCh4.setOrientation(javax.swing.JSlider.VERTICAL);
            OffsetSliderCh4.setToolTipText("Set Channel 4 Offset");
            OffsetSliderCh4.setValue(0);
            OffsetSliderCh4.setValueIsAdjusting(true);
            OffsetSliderCh4.addChangeListener(new javax.swing.event.ChangeListener() {
                public void stateChanged(javax.swing.event.ChangeEvent evt) {
                    OffsetSliderCh4StateChanged(evt);
                }
            });
            OffsetSliderCh4.addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusGained(java.awt.event.FocusEvent evt) {
                    OffsetSliderCh4FocusGained(evt);
                }
                public void focusLost(java.awt.event.FocusEvent evt) {
                    OffsetSliderCh4FocusLost(evt);
                }
            });

            javax.swing.GroupLayout OptionsCh4Layout = new javax.swing.GroupLayout(OptionsCh4);
            OptionsCh4.setLayout(OptionsCh4Layout);
            OptionsCh4Layout.setHorizontalGroup(
                OptionsCh4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(OptionsCh4Layout.createSequentialGroup()
                    .addGap(0, 0, 0)
                    .addComponent(OffsetSliderCh4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, 0)
                    .addGroup(OptionsCh4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(ProbeCouplingCh4, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)
                        .addComponent(ArrowsPanelLineCh4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(PanelButtonsCh4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(0, 0, 0))
            );
            OptionsCh4Layout.setVerticalGroup(
                OptionsCh4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(OptionsCh4Layout.createSequentialGroup()
                    .addGap(0, 0, 0)
                    .addGroup(OptionsCh4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(OffsetSliderCh4, javax.swing.GroupLayout.DEFAULT_SIZE, 357, Short.MAX_VALUE)
                        .addGroup(OptionsCh4Layout.createSequentialGroup()
                            .addComponent(ArrowsPanelLineCh4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(ProbeCouplingCh4, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(PanelButtonsCh4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGap(0, 0, 0))
            );

            ArrowsPanelLineCh4.setBorder(javax.swing.BorderFactory.createLineBorder(ColorCh4));

            ScrollOptionsCh4.setViewportView(OptionsCh4);

            DisplayLicenseDialog.addComponentListener(new java.awt.event.ComponentAdapter() {
                public void componentResized(java.awt.event.ComponentEvent evt) {
                    DisplayLicenseDialogComponentResized(evt);
                }
            });
            DisplayLicenseDialog.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyPressed(java.awt.event.KeyEvent evt) {
                    DisplayLicenseDialogKeyPressed(evt);
                }
            });

            ScrollPaneGPLLicense.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyPressed(java.awt.event.KeyEvent evt) {
                    ScrollPaneGPLLicenseKeyPressed(evt);
                }
            });

            txtGPLLicense.setEditable(false);
            txtGPLLicense.setText("                    GNU GENERAL PUBLIC LICENSE\n                       Version 3, 29 June 2007\n\n Copyright (C) 2007 Free Software Foundation, Inc. <http://fsf.org/>\n Everyone is permitted to copy and distribute verbatim copies\n of this license document, but changing it is not allowed.\n\n                            Preamble\n\n  The GNU General Public License is a free, copyleft license for\nsoftware and other kinds of works.\n\n  The licenses for most software and other practical works are designed\nto take away your freedom to share and change the works.  By contrast,\nthe GNU General Public License is intended to guarantee your freedom to\nshare and change all versions of a program--to make sure it remains free\nsoftware for all its users.  We, the Free Software Foundation, use the\nGNU General Public License for most of our software; it applies also to\nany other work released this way by its authors.  You can apply it to\nyour programs, too.\n\n  When we speak of free software, we are referring to freedom, not\nprice.  Our General Public Licenses are designed to make sure that you\nhave the freedom to distribute copies of free software (and charge for\nthem if you wish), that you receive source code or can get it if you\nwant it, that you can change the software or use pieces of it in new\nfree programs, and that you know you can do these things.\n\n  To protect your rights, we need to prevent others from denying you\nthese rights or asking you to surrender the rights.  Therefore, you have\ncertain responsibilities if you distribute copies of the software, or if\nyou modify it: responsibilities to respect the freedom of others.\n\n  For example, if you distribute copies of such a program, whether\ngratis or for a fee, you must pass on to the recipients the same\nfreedoms that you received.  You must make sure that they, too, receive\nor can get the source code.  And you must show them these terms so they\nknow their rights.\n\n  Developers that use the GNU GPL protect your rights with two steps:\n(1) assert copyright on the software, and (2) offer you this License\ngiving you legal permission to copy, distribute and/or modify it.\n\n  For the developers' and authors' protection, the GPL clearly explains\nthat there is no warranty for this free software.  For both users' and\nauthors' sake, the GPL requires that modified versions be marked as\nchanged, so that their problems will not be attributed erroneously to\nauthors of previous versions.\n\n  Some devices are designed to deny users access to install or run\nmodified versions of the software inside them, although the manufacturer\ncan do so.  This is fundamentally incompatible with the aim of\nprotecting users' freedom to change the software.  The systematic\npattern of such abuse occurs in the area of products for individuals to\nuse, which is precisely where it is most unacceptable.  Therefore, we\nhave designed this version of the GPL to prohibit the practice for those\nproducts.  If such problems arise substantially in other domains, we\nstand ready to extend this provision to those domains in future versions\nof the GPL, as needed to protect the freedom of users.\n\n  Finally, every program is threatened constantly by software patents.\nStates should not allow patents to restrict development and use of\nsoftware on general-purpose computers, but in those that do, we wish to\navoid the special danger that patents applied to a free program could\nmake it effectively proprietary.  To prevent this, the GPL assures that\npatents cannot be used to render the program non-free.\n\n  The precise terms and conditions for copying, distribution and\nmodification follow.\n\n                       TERMS AND CONDITIONS\n\n  0. Definitions.\n\n  \"This License\" refers to version 3 of the GNU General Public License.\n\n  \"Copyright\" also means copyright-like laws that apply to other kinds of\nworks, such as semiconductor masks.\n\n  \"The Program\" refers to any copyrightable work licensed under this\nLicense.  Each licensee is addressed as \"you\".  \"Licensees\" and\n\"recipients\" may be individuals or organizations.\n\n  To \"modify\" a work means to copy from or adapt all or part of the work\nin a fashion requiring copyright permission, other than the making of an\nexact copy.  The resulting work is called a \"modified version\" of the\nearlier work or a work \"based on\" the earlier work.\n\n  A \"covered work\" means either the unmodified Program or a work based\non the Program.\n\n  To \"propagate\" a work means to do anything with it that, without\npermission, would make you directly or secondarily liable for\ninfringement under applicable copyright law, except executing it on a\ncomputer or modifying a private copy.  Propagation includes copying,\ndistribution (with or without modification), making available to the\npublic, and in some countries other activities as well.\n\n  To \"convey\" a work means any kind of propagation that enables other\nparties to make or receive copies.  Mere interaction with a user through\na computer network, with no transfer of a copy, is not conveying.\n\n  An interactive user interface displays \"Appropriate Legal Notices\"\nto the extent that it includes a convenient and prominently visible\nfeature that (1) displays an appropriate copyright notice, and (2)\ntells the user that there is no warranty for the work (except to the\nextent that warranties are provided), that licensees may convey the\nwork under this License, and how to view a copy of this License.  If\nthe interface presents a list of user commands or options, such as a\nmenu, a prominent item in the list meets this criterion.\n\n  1. Source Code.\n\n  The \"source code\" for a work means the preferred form of the work\nfor making modifications to it.  \"Object code\" means any non-source\nform of a work.\n\n  A \"Standard Interface\" means an interface that either is an official\nstandard defined by a recognized standards body, or, in the case of\ninterfaces specified for a particular programming language, one that\nis widely used among developers working in that language.\n\n  The \"System Libraries\" of an executable work include anything, other\nthan the work as a whole, that (a) is included in the normal form of\npackaging a Major Component, but which is not part of that Major\nComponent, and (b) serves only to enable use of the work with that\nMajor Component, or to implement a Standard Interface for which an\nimplementation is available to the public in source code form.  A\n\"Major Component\", in this context, means a major essential component\n(kernel, window system, and so on) of the specific operating system\n(if any) on which the executable work runs, or a compiler used to\nproduce the work, or an object code interpreter used to run it.\n\n  The \"Corresponding Source\" for a work in object code form means all\nthe source code needed to generate, install, and (for an executable\nwork) run the object code and to modify the work, including scripts to\ncontrol those activities.  However, it does not include the work's\nSystem Libraries, or general-purpose tools or generally available free\nprograms which are used unmodified in performing those activities but\nwhich are not part of the work.  For example, Corresponding Source\nincludes interface definition files associated with source files for\nthe work, and the source code for shared libraries and dynamically\nlinked subprograms that the work is specifically designed to require,\nsuch as by intimate data communication or control flow between those\nsubprograms and other parts of the work.\n\n  The Corresponding Source need not include anything that users\ncan regenerate automatically from other parts of the Corresponding\nSource.\n\n  The Corresponding Source for a work in source code form is that\nsame work.\n\n  2. Basic Permissions.\n\n  All rights granted under this License are granted for the term of\ncopyright on the Program, and are irrevocable provided the stated\nconditions are met.  This License explicitly affirms your unlimited\npermission to run the unmodified Program.  The output from running a\ncovered work is covered by this License only if the output, given its\ncontent, constitutes a covered work.  This License acknowledges your\nrights of fair use or other equivalent, as provided by copyright law.\n\n  You may make, run and propagate covered works that you do not\nconvey, without conditions so long as your license otherwise remains\nin force.  You may convey covered works to others for the sole purpose\nof having them make modifications exclusively for you, or provide you\nwith facilities for running those works, provided that you comply with\nthe terms of this License in conveying all material for which you do\nnot control copyright.  Those thus making or running the covered works\nfor you must do so exclusively on your behalf, under your direction\nand control, on terms that prohibit them from making any copies of\nyour copyrighted material outside their relationship with you.\n\n  Conveying under any other circumstances is permitted solely under\nthe conditions stated below.  Sublicensing is not allowed; section 10\nmakes it unnecessary.\n\n  3. Protecting Users' Legal Rights From Anti-Circumvention Law.\n\n  No covered work shall be deemed part of an effective technological\nmeasure under any applicable law fulfilling obligations under article\n11 of the WIPO copyright treaty adopted on 20 December 1996, or\nsimilar laws prohibiting or restricting circumvention of such\nmeasures.\n\n  When you convey a covered work, you waive any legal power to forbid\ncircumvention of technological measures to the extent such circumvention\nis effected by exercising rights under this License with respect to\nthe covered work, and you disclaim any intention to limit operation or\nmodification of the work as a means of enforcing, against the work's\nusers, your or third parties' legal rights to forbid circumvention of\ntechnological measures.\n\n  4. Conveying Verbatim Copies.\n\n  You may convey verbatim copies of the Program's source code as you\nreceive it, in any medium, provided that you conspicuously and\nappropriately publish on each copy an appropriate copyright notice;\nkeep intact all notices stating that this License and any\nnon-permissive terms added in accord with section 7 apply to the code;\nkeep intact all notices of the absence of any warranty; and give all\nrecipients a copy of this License along with the Program.\n\n  You may charge any price or no price for each copy that you convey,\nand you may offer support or warranty protection for a fee.\n\n  5. Conveying Modified Source Versions.\n\n  You may convey a work based on the Program, or the modifications to\nproduce it from the Program, in the form of source code under the\nterms of section 4, provided that you also meet all of these conditions:\n\n    a) The work must carry prominent notices stating that you modified\n    it, and giving a relevant date.\n\n    b) The work must carry prominent notices stating that it is\n    released under this License and any conditions added under section\n    7.  This requirement modifies the requirement in section 4 to\n    \"keep intact all notices\".\n\n    c) You must license the entire work, as a whole, under this\n    License to anyone who comes into possession of a copy.  This\n    License will therefore apply, along with any applicable section 7\n    additional terms, to the whole of the work, and all its parts,\n    regardless of how they are packaged.  This License gives no\n    permission to license the work in any other way, but it does not\n    invalidate such permission if you have separately received it.\n\n    d) If the work has interactive user interfaces, each must display\n    Appropriate Legal Notices; however, if the Program has interactive\n    interfaces that do not display Appropriate Legal Notices, your\n    work need not make them do so.\n\n  A compilation of a covered work with other separate and independent\nworks, which are not by their nature extensions of the covered work,\nand which are not combined with it such as to form a larger program,\nin or on a volume of a storage or distribution medium, is called an\n\"aggregate\" if the compilation and its resulting copyright are not\nused to limit the access or legal rights of the compilation's users\nbeyond what the individual works permit.  Inclusion of a covered work\nin an aggregate does not cause this License to apply to the other\nparts of the aggregate.\n\n  6. Conveying Non-Source Forms.\n\n  You may convey a covered work in object code form under the terms\nof sections 4 and 5, provided that you also convey the\nmachine-readable Corresponding Source under the terms of this License,\nin one of these ways:\n\n    a) Convey the object code in, or embodied in, a physical product\n    (including a physical distribution medium), accompanied by the\n    Corresponding Source fixed on a durable physical medium\n    customarily used for software interchange.\n\n    b) Convey the object code in, or embodied in, a physical product\n    (including a physical distribution medium), accompanied by a\n    written offer, valid for at least three years and valid for as\n    long as you offer spare parts or customer support for that product\n    model, to give anyone who possesses the object code either (1) a\n    copy of the Corresponding Source for all the software in the\n    product that is covered by this License, on a durable physical\n    medium customarily used for software interchange, for a price no\n    more than your reasonable cost of physically performing this\n    conveying of source, or (2) access to copy the\n    Corresponding Source from a network server at no charge.\n\n    c) Convey individual copies of the object code with a copy of the\n    written offer to provide the Corresponding Source.  This\n    alternative is allowed only occasionally and noncommercially, and\n    only if you received the object code with such an offer, in accord\n    with subsection 6b.\n\n    d) Convey the object code by offering access from a designated\n    place (gratis or for a charge), and offer equivalent access to the\n    Corresponding Source in the same way through the same place at no\n    further charge.  You need not require recipients to copy the\n    Corresponding Source along with the object code.  If the place to\n    copy the object code is a network server, the Corresponding Source\n    may be on a different server (operated by you or a third party)\n    that supports equivalent copying facilities, provided you maintain\n    clear directions next to the object code saying where to find the\n    Corresponding Source.  Regardless of what server hosts the\n    Corresponding Source, you remain obligated to ensure that it is\n    available for as long as needed to satisfy these requirements.\n\n    e) Convey the object code using peer-to-peer transmission, provided\n    you inform other peers where the object code and Corresponding\n    Source of the work are being offered to the general public at no\n    charge under subsection 6d.\n\n  A separable portion of the object code, whose source code is excluded\nfrom the Corresponding Source as a System Library, need not be\nincluded in conveying the object code work.\n\n  A \"User Product\" is either (1) a \"consumer product\", which means any\ntangible personal property which is normally used for personal, family,\nor household purposes, or (2) anything designed or sold for incorporation\ninto a dwelling.  In determining whether a product is a consumer product,\ndoubtful cases shall be resolved in favor of coverage.  For a particular\nproduct received by a particular user, \"normally used\" refers to a\ntypical or common use of that class of product, regardless of the status\nof the particular user or of the way in which the particular user\nactually uses, or expects or is expected to use, the product.  A product\nis a consumer product regardless of whether the product has substantial\ncommercial, industrial or non-consumer uses, unless such uses represent\nthe only significant mode of use of the product.\n\n  \"Installation Information\" for a User Product means any methods,\nprocedures, authorization keys, or other information required to install\nand execute modified versions of a covered work in that User Product from\na modified version of its Corresponding Source.  The information must\nsuffice to ensure that the continued functioning of the modified object\ncode is in no case prevented or interfered with solely because\nmodification has been made.\n\n  If you convey an object code work under this section in, or with, or\nspecifically for use in, a User Product, and the conveying occurs as\npart of a transaction in which the right of possession and use of the\nUser Product is transferred to the recipient in perpetuity or for a\nfixed term (regardless of how the transaction is characterized), the\nCorresponding Source conveyed under this section must be accompanied\nby the Installation Information.  But this requirement does not apply\nif neither you nor any third party retains the ability to install\nmodified object code on the User Product (for example, the work has\nbeen installed in ROM).\n\n  The requirement to provide Installation Information does not include a\nrequirement to continue to provide support service, warranty, or updates\nfor a work that has been modified or installed by the recipient, or for\nthe User Product in which it has been modified or installed.  Access to a\nnetwork may be denied when the modification itself materially and\nadversely affects the operation of the network or violates the rules and\nprotocols for communication across the network.\n\n  Corresponding Source conveyed, and Installation Information provided,\nin accord with this section must be in a format that is publicly\ndocumented (and with an implementation available to the public in\nsource code form), and must require no special password or key for\nunpacking, reading or copying.\n\n  7. Additional Terms.\n\n  \"Additional permissions\" are terms that supplement the terms of this\nLicense by making exceptions from one or more of its conditions.\nAdditional permissions that are applicable to the entire Program shall\nbe treated as though they were included in this License, to the extent\nthat they are valid under applicable law.  If additional permissions\napply only to part of the Program, that part may be used separately\nunder those permissions, but the entire Program remains governed by\nthis License without regard to the additional permissions.\n\n  When you convey a copy of a covered work, you may at your option\nremove any additional permissions from that copy, or from any part of\nit.  (Additional permissions may be written to require their own\nremoval in certain cases when you modify the work.)  You may place\nadditional permissions on material, added by you to a covered work,\nfor which you have or can give appropriate copyright permission.\n\n  Notwithstanding any other provision of this License, for material you\nadd to a covered work, you may (if authorized by the copyright holders of\nthat material) supplement the terms of this License with terms:\n\n    a) Disclaiming warranty or limiting liability differently from the\n    terms of sections 15 and 16 of this License; or\n\n    b) Requiring preservation of specified reasonable legal notices or\n    author attributions in that material or in the Appropriate Legal\n    Notices displayed by works containing it; or\n\n    c) Prohibiting misrepresentation of the origin of that material, or\n    requiring that modified versions of such material be marked in\n    reasonable ways as different from the original version; or\n\n    d) Limiting the use for publicity purposes of names of licensors or\n    authors of the material; or\n\n    e) Declining to grant rights under trademark law for use of some\n    trade names, trademarks, or service marks; or\n\n    f) Requiring indemnification of licensors and authors of that\n    material by anyone who conveys the material (or modified versions of\n    it) with contractual assumptions of liability to the recipient, for\n    any liability that these contractual assumptions directly impose on\n    those licensors and authors.\n\n  All other non-permissive additional terms are considered \"further\nrestrictions\" within the meaning of section 10.  If the Program as you\nreceived it, or any part of it, contains a notice stating that it is\ngoverned by this License along with a term that is a further\nrestriction, you may remove that term.  If a license document contains\na further restriction but permits relicensing or conveying under this\nLicense, you may add to a covered work material governed by the terms\nof that license document, provided that the further restriction does\nnot survive such relicensing or conveying.\n\n  If you add terms to a covered work in accord with this section, you\nmust place, in the relevant source files, a statement of the\nadditional terms that apply to those files, or a notice indicating\nwhere to find the applicable terms.\n\n  Additional terms, permissive or non-permissive, may be stated in the\nform of a separately written license, or stated as exceptions;\nthe above requirements apply either way.\n\n  8. Termination.\n\n  You may not propagate or modify a covered work except as expressly\nprovided under this License.  Any attempt otherwise to propagate or\nmodify it is void, and will automatically terminate your rights under\nthis License (including any patent licenses granted under the third\nparagraph of section 11).\n\n  However, if you cease all violation of this License, then your\nlicense from a particular copyright holder is reinstated (a)\nprovisionally, unless and until the copyright holder explicitly and\nfinally terminates your license, and (b) permanently, if the copyright\nholder fails to notify you of the violation by some reasonable means\nprior to 60 days after the cessation.\n\n  Moreover, your license from a particular copyright holder is\nreinstated permanently if the copyright holder notifies you of the\nviolation by some reasonable means, this is the first time you have\nreceived notice of violation of this License (for any work) from that\ncopyright holder, and you cure the violation prior to 30 days after\nyour receipt of the notice.\n\n  Termination of your rights under this section does not terminate the\nlicenses of parties who have received copies or rights from you under\nthis License.  If your rights have been terminated and not permanently\nreinstated, you do not qualify to receive new licenses for the same\nmaterial under section 10.\n\n  9. Acceptance Not Required for Having Copies.\n\n  You are not required to accept this License in order to receive or\nrun a copy of the Program.  Ancillary propagation of a covered work\noccurring solely as a consequence of using peer-to-peer transmission\nto receive a copy likewise does not require acceptance.  However,\nnothing other than this License grants you permission to propagate or\nmodify any covered work.  These actions infringe copyright if you do\nnot accept this License.  Therefore, by modifying or propagating a\ncovered work, you indicate your acceptance of this License to do so.\n\n  10. Automatic Licensing of Downstream Recipients.\n\n  Each time you convey a covered work, the recipient automatically\nreceives a license from the original licensors, to run, modify and\npropagate that work, subject to this License.  You are not responsible\nfor enforcing compliance by third parties with this License.\n\n  An \"entity transaction\" is a transaction transferring control of an\norganization, or substantially all assets of one, or subdividing an\norganization, or merging organizations.  If propagation of a covered\nwork results from an entity transaction, each party to that\ntransaction who receives a copy of the work also receives whatever\nlicenses to the work the party's predecessor in interest had or could\ngive under the previous paragraph, plus a right to possession of the\nCorresponding Source of the work from the predecessor in interest, if\nthe predecessor has it or can get it with reasonable efforts.\n\n  You may not impose any further restrictions on the exercise of the\nrights granted or affirmed under this License.  For example, you may\nnot impose a license fee, royalty, or other charge for exercise of\nrights granted under this License, and you may not initiate litigation\n(including a cross-claim or counterclaim in a lawsuit) alleging that\nany patent claim is infringed by making, using, selling, offering for\nsale, or importing the Program or any portion of it.\n\n  11. Patents.\n\n  A \"contributor\" is a copyright holder who authorizes use under this\nLicense of the Program or a work on which the Program is based.  The\nwork thus licensed is called the contributor's \"contributor version\".\n\n  A contributor's \"essential patent claims\" are all patent claims\nowned or controlled by the contributor, whether already acquired or\nhereafter acquired, that would be infringed by some manner, permitted\nby this License, of making, using, or selling its contributor version,\nbut do not include claims that would be infringed only as a\nconsequence of further modification of the contributor version.  For\npurposes of this definition, \"control\" includes the right to grant\npatent sublicenses in a manner consistent with the requirements of\nthis License.\n\n  Each contributor grants you a non-exclusive, worldwide, royalty-free\npatent license under the contributor's essential patent claims, to\nmake, use, sell, offer for sale, import and otherwise run, modify and\npropagate the contents of its contributor version.\n\n  In the following three paragraphs, a \"patent license\" is any express\nagreement or commitment, however denominated, not to enforce a patent\n(such as an express permission to practice a patent or covenant not to\nsue for patent infringement).  To \"grant\" such a patent license to a\nparty means to make such an agreement or commitment not to enforce a\npatent against the party.\n\n  If you convey a covered work, knowingly relying on a patent license,\nand the Corresponding Source of the work is not available for anyone\nto copy, free of charge and under the terms of this License, through a\npublicly available network server or other readily accessible means,\nthen you must either (1) cause the Corresponding Source to be so\navailable, or (2) arrange to deprive yourself of the benefit of the\npatent license for this particular work, or (3) arrange, in a manner\nconsistent with the requirements of this License, to extend the patent\nlicense to downstream recipients.  \"Knowingly relying\" means you have\nactual knowledge that, but for the patent license, your conveying the\ncovered work in a country, or your recipient's use of the covered work\nin a country, would infringe one or more identifiable patents in that\ncountry that you have reason to believe are valid.\n\n  If, pursuant to or in connection with a single transaction or\narrangement, you convey, or propagate by procuring conveyance of, a\ncovered work, and grant a patent license to some of the parties\nreceiving the covered work authorizing them to use, propagate, modify\nor convey a specific copy of the covered work, then the patent license\nyou grant is automatically extended to all recipients of the covered\nwork and works based on it.\n\n  A patent license is \"discriminatory\" if it does not include within\nthe scope of its coverage, prohibits the exercise of, or is\nconditioned on the non-exercise of one or more of the rights that are\nspecifically granted under this License.  You may not convey a covered\nwork if you are a party to an arrangement with a third party that is\nin the business of distributing software, under which you make payment\nto the third party based on the extent of your activity of conveying\nthe work, and under which the third party grants, to any of the\nparties who would receive the covered work from you, a discriminatory\npatent license (a) in connection with copies of the covered work\nconveyed by you (or copies made from those copies), or (b) primarily\nfor and in connection with specific products or compilations that\ncontain the covered work, unless you entered into that arrangement,\nor that patent license was granted, prior to 28 March 2007.\n\n  Nothing in this License shall be construed as excluding or limiting\nany implied license or other defenses to infringement that may\notherwise be available to you under applicable patent law.\n\n  12. No Surrender of Others' Freedom.\n\n  If conditions are imposed on you (whether by court order, agreement or\notherwise) that contradict the conditions of this License, they do not\nexcuse you from the conditions of this License.  If you cannot convey a\ncovered work so as to satisfy simultaneously your obligations under this\nLicense and any other pertinent obligations, then as a consequence you may\nnot convey it at all.  For example, if you agree to terms that obligate you\nto collect a royalty for further conveying from those to whom you convey\nthe Program, the only way you could satisfy both those terms and this\nLicense would be to refrain entirely from conveying the Program.\n\n  13. Use with the GNU Affero General Public License.\n\n  Notwithstanding any other provision of this License, you have\npermission to link or combine any covered work with a work licensed\nunder version 3 of the GNU Affero General Public License into a single\ncombined work, and to convey the resulting work.  The terms of this\nLicense will continue to apply to the part which is the covered work,\nbut the special requirements of the GNU Affero General Public License,\nsection 13, concerning interaction through a network will apply to the\ncombination as such.\n\n  14. Revised Versions of this License.\n\n  The Free Software Foundation may publish revised and/or new versions of\nthe GNU General Public License from time to time.  Such new versions will\nbe similar in spirit to the present version, but may differ in detail to\naddress new problems or concerns.\n\n  Each version is given a distinguishing version number.  If the\nProgram specifies that a certain numbered version of the GNU General\nPublic License \"or any later version\" applies to it, you have the\noption of following the terms and conditions either of that numbered\nversion or of any later version published by the Free Software\nFoundation.  If the Program does not specify a version number of the\nGNU General Public License, you may choose any version ever published\nby the Free Software Foundation.\n\n  If the Program specifies that a proxy can decide which future\nversions of the GNU General Public License can be used, that proxy's\npublic statement of acceptance of a version permanently authorizes you\nto choose that version for the Program.\n\n  Later license versions may give you additional or different\npermissions.  However, no additional obligations are imposed on any\nauthor or copyright holder as a result of your choosing to follow a\nlater version.\n\n  15. Disclaimer of Warranty.\n\n  THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY\nAPPLICABLE LAW.  EXCEPT WHEN OTHERWISE STATED IN WRITING THE COPYRIGHT\nHOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM \"AS IS\" WITHOUT WARRANTY\nOF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO,\nTHE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR\nPURPOSE.  THE ENTIRE RISK AS TO THE QUALITY AND PERFORMANCE OF THE PROGRAM\nIS WITH YOU.  SHOULD THE PROGRAM PROVE DEFECTIVE, YOU ASSUME THE COST OF\nALL NECESSARY SERVICING, REPAIR OR CORRECTION.\n\n  16. Limitation of Liability.\n\n  IN NO EVENT UNLESS REQUIRED BY APPLICABLE LAW OR AGREED TO IN WRITING\nWILL ANY COPYRIGHT HOLDER, OR ANY OTHER PARTY WHO MODIFIES AND/OR CONVEYS\nTHE PROGRAM AS PERMITTED ABOVE, BE LIABLE TO YOU FOR DAMAGES, INCLUDING ANY\nGENERAL, SPECIAL, INCIDENTAL OR CONSEQUENTIAL DAMAGES ARISING OUT OF THE\nUSE OR INABILITY TO USE THE PROGRAM (INCLUDING BUT NOT LIMITED TO LOSS OF\nDATA OR DATA BEING RENDERED INACCURATE OR LOSSES SUSTAINED BY YOU OR THIRD\nPARTIES OR A FAILURE OF THE PROGRAM TO OPERATE WITH ANY OTHER PROGRAMS),\nEVEN IF SUCH HOLDER OR OTHER PARTY HAS BEEN ADVISED OF THE POSSIBILITY OF\nSUCH DAMAGES.\n\n  17. Interpretation of Sections 15 and 16.\n\n  If the disclaimer of warranty and limitation of liability provided\nabove cannot be given local legal effect according to their terms,\nreviewing courts shall apply local law that most closely approximates\nan absolute waiver of all civil liability in connection with the\nProgram, unless a warranty or assumption of liability accompanies a\ncopy of the Program in return for a fee.\n\n                     END OF TERMS AND CONDITIONS\n\n            How to Apply These Terms to Your New Programs\n\n  If you develop a new program, and you want it to be of the greatest\npossible use to the public, the best way to achieve this is to make it\nfree software which everyone can redistribute and change under these terms.\n\n  To do so, attach the following notices to the program.  It is safest\nto attach them to the start of each source file to most effectively\nstate the exclusion of warranty; and each file should have at least\nthe \"copyright\" line and a pointer to where the full notice is found.\n\n    <one line to give the program's name and a brief idea of what it does.>\n    Copyright (C) <year>  <name of author>\n\n    This program is free software: you can redistribute it and/or modify\n    it under the terms of the GNU General Public License as published by\n    the Free Software Foundation, either version 3 of the License, or\n    (at your option) any later version.\n\n    This program is distributed in the hope that it will be useful,\n    but WITHOUT ANY WARRANTY; without even the implied warranty of\n    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n    GNU General Public License for more details.\n\n    You should have received a copy of the GNU General Public License\n    along with this program.  If not, see <http://www.gnu.org/licenses/>.\n\nAlso add information on how to contact you by electronic and paper mail.\n\n  If the program does terminal interaction, make it output a short\nnotice like this when it starts in an interactive mode:\n\n    <program>  Copyright (C) <year>  <name of author>\n    This program comes with ABSOLUTELY NO WARRANTY; for details type `show w'.\n    This is free software, and you are welcome to redistribute it\n    under certain conditions; type `show c' for details.\n\nThe hypothetical commands `show w' and `show c' should show the appropriate\nparts of the General Public License.  Of course, your program's commands\nmight be different; for a GUI interface, you would use an \"about box\".\n\n  You should also get your employer (if you work as a programmer) or school,\nif any, to sign a \"copyright disclaimer\" for the program, if necessary.\nFor more information on this, and how to apply and follow the GNU GPL, see\n<http://www.gnu.org/licenses/>.\n\n  The GNU General Public License does not permit incorporating your program\ninto proprietary programs.  If your program is a subroutine library, you\nmay consider it more useful to permit linking proprietary applications with\nthe library.  If this is what you want to do, use the GNU Lesser General\nPublic License instead of this License.  But first, please read\n<http://www.gnu.org/philosophy/why-not-lgpl.html>.\n");
            txtGPLLicense.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyPressed(java.awt.event.KeyEvent evt) {
                    txtGPLLicenseKeyPressed(evt);
                }
            });
            ScrollPaneGPLLicense.setViewportView(txtGPLLicense);

            ExitLicenseDialog.setText("Exit");
            ExitLicenseDialog.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    ExitLicenseDialogActionPerformed(evt);
                }
            });
            ExitLicenseDialog.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyPressed(java.awt.event.KeyEvent evt) {
                    ExitLicenseDialogKeyPressed(evt);
                }
            });

            javax.swing.GroupLayout DisplayLicenseDialogLayout = new javax.swing.GroupLayout(DisplayLicenseDialog.getContentPane());
            DisplayLicenseDialog.getContentPane().setLayout(DisplayLicenseDialogLayout);
            DisplayLicenseDialogLayout.setHorizontalGroup(
                DisplayLicenseDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(DisplayLicenseDialogLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(DisplayLicenseDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(ScrollPaneGPLLicense, javax.swing.GroupLayout.DEFAULT_SIZE, 456, Short.MAX_VALUE)
                        .addComponent(ExitLicenseDialog, javax.swing.GroupLayout.Alignment.TRAILING))
                    .addContainerGap())
            );
            DisplayLicenseDialogLayout.setVerticalGroup(
                DisplayLicenseDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(DisplayLicenseDialogLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(ScrollPaneGPLLicense, javax.swing.GroupLayout.DEFAULT_SIZE, 254, Short.MAX_VALUE)
                    .addGap(0, 0, 0)
                    .addComponent(ExitLicenseDialog)
                    .addGap(0, 0, 0))
            );

            ChartOptionsDialog.addWindowListener(new java.awt.event.WindowAdapter() {
                public void windowDeactivated(java.awt.event.WindowEvent evt) {
                    ChartOptionsDialogWindowDeactivated(evt);
                }
            });

            BackgroundColorPanel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
                public void mouseMoved(java.awt.event.MouseEvent evt) {
                    BackgroundColorPanelMouseMoved(evt);
                }
            });

            buttChartDefaultValues.setText("Set Default Values");
            buttChartDefaultValues.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    buttChartDefaultValuesActionPerformed(evt);
                }
            });

            buttChartOptionsCancel.setText("Cancel");
            buttChartOptionsCancel.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    buttChartOptionsCancelActionPerformed(evt);
                }
            });

            buttChartOptionsOK.setText("OK");
            buttChartOptionsOK.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    buttChartOptionsOKActionPerformed(evt);
                }
            });

            javax.swing.GroupLayout BackgroundColorPanelLayout = new javax.swing.GroupLayout(BackgroundColorPanel);
            BackgroundColorPanel.setLayout(BackgroundColorPanelLayout);
            BackgroundColorPanelLayout.setHorizontalGroup(
                BackgroundColorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(BackgroundColorPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(BackgroundColorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(ColorChooser, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 429, Short.MAX_VALUE)
                        .addGroup(BackgroundColorPanelLayout.createSequentialGroup()
                            .addComponent(buttChartDefaultValues)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 170, Short.MAX_VALUE)
                            .addComponent(buttChartOptionsOK, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(buttChartOptionsCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addContainerGap())
            );
            BackgroundColorPanelLayout.setVerticalGroup(
                BackgroundColorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, BackgroundColorPanelLayout.createSequentialGroup()
                    .addComponent(ColorChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 158, Short.MAX_VALUE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(BackgroundColorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(buttChartDefaultValues)
                        .addComponent(buttChartOptionsCancel)
                        .addComponent(buttChartOptionsOK))
                    .addContainerGap())
            );

            ChartOptionsTabs.addTab("Chart Background Color", BackgroundColorPanel);

            buttChartDefaultValues2.setText("Set Default Values");
            buttChartDefaultValues2.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    buttChartDefaultValues2ActionPerformed(evt);
                }
            });

            buttChartOptionsCancel2.setText("Cancel");
            buttChartOptionsCancel2.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    buttChartOptionsCancel2ActionPerformed(evt);
                }
            });

            buttChartOptionsOK2.setText("OK");
            buttChartOptionsOK2.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    buttChartOptionsOK2ActionPerformed(evt);
                }
            });

            GridLinesThicknessPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Chart Grid Lines Thickness", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, java.awt.Color.gray));

            jLabel1.setText("Set Range Grid Lines Thickness");

            jLabel2.setText("Set Domain Grid Lines Thickness");

            SpinnerNumberModel RangeModel = new SpinnerNumberModel(0.0, //initial value
                0.0, //min
                99.9, //max
                0.1);                //step
            RangeSpinner = new JSpinner(RangeModel);
            RangeSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
                public void stateChanged(javax.swing.event.ChangeEvent evt) {
                    RangeSpinnerStateChanged(evt);
                }
            });

            SpinnerNumberModel DomainModel = new SpinnerNumberModel(0.0, //initial value
                0.0, //min
                99.9, //max
                0.1);                //step
            DomainSpinner = new JSpinner(DomainModel);
            DomainSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
                public void stateChanged(javax.swing.event.ChangeEvent evt) {
                    DomainSpinnerStateChanged(evt);
                }
            });

            chkModifyBoth.setText("Modify both Range and Domain Grid Lines");

            javax.swing.GroupLayout GridLinesThicknessPanelLayout = new javax.swing.GroupLayout(GridLinesThicknessPanel);
            GridLinesThicknessPanel.setLayout(GridLinesThicknessPanelLayout);
            GridLinesThicknessPanelLayout.setHorizontalGroup(
                GridLinesThicknessPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(GridLinesThicknessPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(GridLinesThicknessPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(GridLinesThicknessPanelLayout.createSequentialGroup()
                            .addGroup(GridLinesThicknessPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel1)
                                .addComponent(jLabel2))
                            .addGap(98, 98, 98)
                            .addGroup(GridLinesThicknessPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(DomainSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(RangeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addComponent(chkModifyBoth))
                    .addContainerGap(131, Short.MAX_VALUE))
            );
            GridLinesThicknessPanelLayout.setVerticalGroup(
                GridLinesThicknessPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, GridLinesThicknessPanelLayout.createSequentialGroup()
                    .addGroup(GridLinesThicknessPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(RangeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(GridLinesThicknessPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(DomainSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(0, 0, 0)
                    .addComponent(chkModifyBoth))
            );

            ((JSpinner.DefaultEditor) RangeSpinner.getEditor()).getTextField().addKeyListener(new KeyAdapter() {
                public void keyTyped(KeyEvent e) {
                    if (((JSpinner.DefaultEditor) RangeSpinner.getEditor()).getTextField().getText().length()>3){
                        e.consume();
                        return;
                    }
                    if (!Character.isDigit(e.getKeyChar())) {
                        if (e.getKeyChar()!='.'){
                            e.consume();
                        }
                    }
                }
            });

            ((JSpinner.DefaultEditor) RangeSpinner.getEditor()).getTextField().addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == 10){//enter
                        if (chkModifyBoth.isSelected()){
                            DomainSpinnerActionPerformed();
                        }
                        RangeSpinnerActionPerformed();
                    }
                }
            });
            ((JSpinner.DefaultEditor) DomainSpinner.getEditor()).getTextField().addKeyListener(new KeyAdapter() {
                public void keyTyped(KeyEvent e) {
                    if (((JSpinner.DefaultEditor) DomainSpinner.getEditor()).getTextField().getText().length()>3){
                        e.consume();
                        return;
                    }
                    if (!Character.isDigit(e.getKeyChar())) {
                        if (e.getKeyChar()!='.'){
                            e.consume();
                        }
                    }
                }
            });

            ((JSpinner.DefaultEditor) DomainSpinner.getEditor()).getTextField().addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == 10){//enter
                        if (chkModifyBoth.isSelected()){
                            RangeSpinnerActionPerformed();
                        }
                        DomainSpinnerActionPerformed();
                    }
                }
            });

            jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Function Lines Thickness", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, java.awt.Color.gray));

            SpinnerNumberModel FunctionModel = new SpinnerNumberModel(0.5, //initial value
                0.0, //min
                99.9, //max
                0.1); //step
            FunctionSpinner = new JSpinner(FunctionModel);
            FunctionSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
                public void stateChanged(javax.swing.event.ChangeEvent evt) {
                    FunctionSpinnerStateChanged(evt);
                }
            });

            jLabel3.setText("Set Function Lines Thickness");

            javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
            jPanel4.setLayout(jPanel4Layout);
            jPanel4Layout.setHorizontalGroup(
                jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jLabel3)
                    .addGap(113, 113, 113)
                    .addComponent(FunctionSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(132, Short.MAX_VALUE))
            );
            jPanel4Layout.setVerticalGroup(
                jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addGap(0, 0, 0)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(FunctionSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3)))
            );

            ((JSpinner.DefaultEditor) FunctionSpinner.getEditor()).getTextField().addKeyListener(new KeyAdapter() {
                public void keyTyped(KeyEvent e) {
                    if (((JSpinner.DefaultEditor) FunctionSpinner.getEditor()).getTextField().getText().length()>3){
                        e.consume();
                        return;
                    }
                    if (!Character.isDigit(e.getKeyChar())) {
                        if (e.getKeyChar()!='.'){
                            e.consume();
                        }
                    }
                }
            });

            ((JSpinner.DefaultEditor) FunctionSpinner.getEditor()).getTextField().addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == 10){//enter

                    }
                }
            });

            javax.swing.GroupLayout ChartStrokePanelLayout = new javax.swing.GroupLayout(ChartStrokePanel);
            ChartStrokePanel.setLayout(ChartStrokePanelLayout);
            ChartStrokePanelLayout.setHorizontalGroup(
                ChartStrokePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ChartStrokePanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(ChartStrokePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(GridLinesThicknessPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, ChartStrokePanelLayout.createSequentialGroup()
                            .addComponent(buttChartDefaultValues2)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 170, Short.MAX_VALUE)
                            .addComponent(buttChartOptionsOK2, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(buttChartOptionsCancel2, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addContainerGap())
            );
            ChartStrokePanelLayout.setVerticalGroup(
                ChartStrokePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(ChartStrokePanelLayout.createSequentialGroup()
                    .addGap(0, 0, 0)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, 53, Short.MAX_VALUE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(GridLinesThicknessPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(ChartStrokePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(buttChartDefaultValues2)
                        .addComponent(buttChartOptionsCancel2)
                        .addComponent(buttChartOptionsOK2))
                    .addContainerGap())
            );

            ChartOptionsTabs.addTab("Chart Lines Stroke", ChartStrokePanel);

            GridLinesColorPanel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
                public void mouseMoved(java.awt.event.MouseEvent evt) {
                    GridLinesColorPanelMouseMoved(evt);
                }
            });

            buttChartDefaultValues1.setText("Set Default Values");
            buttChartDefaultValues1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    buttChartDefaultValues1ActionPerformed(evt);
                }
            });

            buttChartOptionsCancel1.setText("Cancel");
            buttChartOptionsCancel1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    buttChartOptionsCancel1ActionPerformed(evt);
                }
            });

            buttChartOptionsOK1.setText("OK");
            buttChartOptionsOK1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    buttChartOptionsOK1ActionPerformed(evt);
                }
            });

            javax.swing.GroupLayout GridLinesColorPanelLayout = new javax.swing.GroupLayout(GridLinesColorPanel);
            GridLinesColorPanel.setLayout(GridLinesColorPanelLayout);
            GridLinesColorPanelLayout.setHorizontalGroup(
                GridLinesColorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(GridLinesColorPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(GridLinesColorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(ColorChooser1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 429, Short.MAX_VALUE)
                        .addGroup(GridLinesColorPanelLayout.createSequentialGroup()
                            .addComponent(buttChartDefaultValues1)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 170, Short.MAX_VALUE)
                            .addComponent(buttChartOptionsOK1, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(buttChartOptionsCancel1, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addContainerGap())
            );
            GridLinesColorPanelLayout.setVerticalGroup(
                GridLinesColorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, GridLinesColorPanelLayout.createSequentialGroup()
                    .addComponent(ColorChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 158, Short.MAX_VALUE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(GridLinesColorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(buttChartDefaultValues1)
                        .addComponent(buttChartOptionsCancel1)
                        .addComponent(buttChartOptionsOK1))
                    .addContainerGap())
            );

            ChartOptionsTabs.addTab("Chart Grid Lines Color", GridLinesColorPanel);

            javax.swing.GroupLayout ChartOptionsDialogLayout = new javax.swing.GroupLayout(ChartOptionsDialog.getContentPane());
            ChartOptionsDialog.getContentPane().setLayout(ChartOptionsDialogLayout);
            ChartOptionsDialogLayout.setHorizontalGroup(
                ChartOptionsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 454, Short.MAX_VALUE)
                .addGroup(ChartOptionsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ChartOptionsTabs, javax.swing.GroupLayout.DEFAULT_SIZE, 454, Short.MAX_VALUE))
            );
            ChartOptionsDialogLayout.setVerticalGroup(
                ChartOptionsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 223, Short.MAX_VALUE)
                .addGroup(ChartOptionsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ChartOptionsTabs, javax.swing.GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE))
            );

            ChannelColorsDialog.addWindowListener(new java.awt.event.WindowAdapter() {
                public void windowDeactivated(java.awt.event.WindowEvent evt) {
                    ChannelColorsDialogWindowDeactivated(evt);
                }
            });

            buttChColorDefaultValues1.setText("Set Default Values");
            buttChColorDefaultValues1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    buttChColorDefaultValues1ActionPerformed(evt);
                }
            });

            buttChColorCancel1.setText("Cancel");
            buttChColorCancel1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    buttChColorCancel1ActionPerformed(evt);
                }
            });

            buttChColorOK1.setText("OK");
            buttChColorOK1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    buttChColorOK1ActionPerformed(evt);
                }
            });

            javax.swing.GroupLayout Ch1ColorLayout = new javax.swing.GroupLayout(Ch1Color);
            Ch1Color.setLayout(Ch1ColorLayout);
            Ch1ColorLayout.setHorizontalGroup(
                Ch1ColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(Ch1ColorLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(Ch1ColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(Ch1ColorChooser, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 429, Short.MAX_VALUE)
                        .addGroup(Ch1ColorLayout.createSequentialGroup()
                            .addComponent(buttChColorDefaultValues1)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 170, Short.MAX_VALUE)
                            .addComponent(buttChColorOK1, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(buttChColorCancel1, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addContainerGap())
            );
            Ch1ColorLayout.setVerticalGroup(
                Ch1ColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Ch1ColorLayout.createSequentialGroup()
                    .addComponent(Ch1ColorChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 158, Short.MAX_VALUE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(Ch1ColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(buttChColorDefaultValues1)
                        .addComponent(buttChColorCancel1)
                        .addComponent(buttChColorOK1))
                    .addContainerGap())
            );

            ChannelColourTabs.addTab("Channel 1 Color", Ch1Color);

            buttChColorDefaultValues2.setText("Set Default Values");
            buttChColorDefaultValues2.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    buttChColorDefaultValues2ActionPerformed(evt);
                }
            });

            buttChColorCancel2.setText("Cancel");
            buttChColorCancel2.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    buttChColorCancel2ActionPerformed(evt);
                }
            });

            buttChColorOK2.setText("OK");
            buttChColorOK2.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    buttChColorOK2ActionPerformed(evt);
                }
            });

            javax.swing.GroupLayout Ch2ColorLayout = new javax.swing.GroupLayout(Ch2Color);
            Ch2Color.setLayout(Ch2ColorLayout);
            Ch2ColorLayout.setHorizontalGroup(
                Ch2ColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(Ch2ColorLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(Ch2ColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(Ch2ColorChooser, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 429, Short.MAX_VALUE)
                        .addGroup(Ch2ColorLayout.createSequentialGroup()
                            .addComponent(buttChColorDefaultValues2)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 170, Short.MAX_VALUE)
                            .addComponent(buttChColorOK2, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(buttChColorCancel2, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addContainerGap())
            );
            Ch2ColorLayout.setVerticalGroup(
                Ch2ColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Ch2ColorLayout.createSequentialGroup()
                    .addComponent(Ch2ColorChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 158, Short.MAX_VALUE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(Ch2ColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(buttChColorDefaultValues2)
                        .addComponent(buttChColorCancel2)
                        .addComponent(buttChColorOK2))
                    .addContainerGap())
            );

            ChannelColourTabs.addTab("Channel 2 Color", Ch2Color);

            buttChColorDefaultValues3.setText("Set Default Values");
            buttChColorDefaultValues3.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    buttChColorDefaultValues3ActionPerformed(evt);
                }
            });

            buttChColorCancel3.setText("Cancel");
            buttChColorCancel3.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    buttChColorCancel3ActionPerformed(evt);
                }
            });

            buttChColorOK3.setText("OK");
            buttChColorOK3.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    buttChColorOK3ActionPerformed(evt);
                }
            });

            javax.swing.GroupLayout Ch3ColorLayout = new javax.swing.GroupLayout(Ch3Color);
            Ch3Color.setLayout(Ch3ColorLayout);
            Ch3ColorLayout.setHorizontalGroup(
                Ch3ColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(Ch3ColorLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(Ch3ColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(Ch3ColorChooser, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 429, Short.MAX_VALUE)
                        .addGroup(Ch3ColorLayout.createSequentialGroup()
                            .addComponent(buttChColorDefaultValues3)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 170, Short.MAX_VALUE)
                            .addComponent(buttChColorOK3, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(buttChColorCancel3, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addContainerGap())
            );
            Ch3ColorLayout.setVerticalGroup(
                Ch3ColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Ch3ColorLayout.createSequentialGroup()
                    .addComponent(Ch3ColorChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 158, Short.MAX_VALUE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(Ch3ColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(buttChColorDefaultValues3)
                        .addComponent(buttChColorCancel3)
                        .addComponent(buttChColorOK3))
                    .addContainerGap())
            );

            ChannelColourTabs.addTab("Channel 3 Color", Ch3Color);

            buttChColorDefaultValues4.setText("Set Default Values");
            buttChColorDefaultValues4.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    buttChColorDefaultValues4ActionPerformed(evt);
                }
            });

            buttChColorCancel4.setText("Cancel");
            buttChColorCancel4.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    buttChColorCancel4ActionPerformed(evt);
                }
            });

            buttChColorOK4.setText("OK");
            buttChColorOK4.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    buttChColorOK4ActionPerformed(evt);
                }
            });

            javax.swing.GroupLayout Ch4ColorLayout = new javax.swing.GroupLayout(Ch4Color);
            Ch4Color.setLayout(Ch4ColorLayout);
            Ch4ColorLayout.setHorizontalGroup(
                Ch4ColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(Ch4ColorLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(Ch4ColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(Ch4ColorChooser, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 429, Short.MAX_VALUE)
                        .addGroup(Ch4ColorLayout.createSequentialGroup()
                            .addComponent(buttChColorDefaultValues4)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 170, Short.MAX_VALUE)
                            .addComponent(buttChColorOK4, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(buttChColorCancel4, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addContainerGap())
            );
            Ch4ColorLayout.setVerticalGroup(
                Ch4ColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Ch4ColorLayout.createSequentialGroup()
                    .addComponent(Ch4ColorChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 158, Short.MAX_VALUE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(Ch4ColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(buttChColorDefaultValues4)
                        .addComponent(buttChColorCancel4)
                        .addComponent(buttChColorOK4))
                    .addContainerGap())
            );

            ChannelColourTabs.addTab("Channel 4 Color", Ch4Color);

            javax.swing.GroupLayout ChannelColorsDialogLayout = new javax.swing.GroupLayout(ChannelColorsDialog.getContentPane());
            ChannelColorsDialog.getContentPane().setLayout(ChannelColorsDialogLayout);
            ChannelColorsDialogLayout.setHorizontalGroup(
                ChannelColorsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 454, Short.MAX_VALUE)
                .addGroup(ChannelColorsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ChannelColourTabs, javax.swing.GroupLayout.DEFAULT_SIZE, 454, Short.MAX_VALUE))
            );
            ChannelColorsDialogLayout.setVerticalGroup(
                ChannelColorsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 223, Short.MAX_VALUE)
                .addGroup(ChannelColorsDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ChannelColourTabs, javax.swing.GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE))
            );

            setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
            setTitle(productID + " Software");
            setPreferredSize(new java.awt.Dimension(1200, 600));
            addWindowListener(new java.awt.event.WindowAdapter() {
                public void windowActivated(java.awt.event.WindowEvent evt) {
                    formWindowActivated(evt);
                }
                public void windowClosed(java.awt.event.WindowEvent evt) {
                    formWindowClosed(evt);
                }
                public void windowClosing(java.awt.event.WindowEvent evt) {
                    formWindowClosing(evt);
                }
                public void windowOpened(java.awt.event.WindowEvent evt) {
                    formWindowOpened(evt);
                }
            });

            ToolBarFile.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
            ToolBarFile.setFloatable(false);
            ToolBarFile.setFocusable(false);

            NewToolBarButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/usbscope50software/Icons/New24.gif"))); // NOI18N
            NewToolBarButton.setToolTipText("New Graph");
            NewToolBarButton.setEnabled(false);
            NewToolBarButton.setFocusable(false);
            NewToolBarButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            NewToolBarButton.setMargin(new java.awt.Insets(5, 2, 5, 2));
            NewToolBarButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
            ToolBarFile.add(NewToolBarButton);

            OpenToolBarButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/usbscope50software/Icons/Open24.gif"))); // NOI18N
            OpenToolBarButton.setToolTipText("Open");
            OpenToolBarButton.setEnabled(false);
            OpenToolBarButton.setFocusable(false);
            OpenToolBarButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            OpenToolBarButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
            OpenToolBarButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    OpenToolBarButtonActionPerformed(evt);
                }
            });
            ToolBarFile.add(OpenToolBarButton);

            SaveToolBarButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/usbscope50software/Icons/Save24.gif"))); // NOI18N
            SaveToolBarButton.setToolTipText("Save Graph");
            SaveToolBarButton.setFocusable(false);
            SaveToolBarButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            SaveToolBarButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
            SaveToolBarButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    SaveToolBarButtonActionPerformed(evt);
                }
            });
            ToolBarFile.add(SaveToolBarButton);

            ImportToolBarButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/usbscope50software/Icons/Import24.gif"))); // NOI18N
            ImportToolBarButton.setToolTipText("Import Graph Data");
            ImportToolBarButton.setFocusable(false);
            ImportToolBarButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            ImportToolBarButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
            ImportToolBarButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    ImportToolBarButtonActionPerformed(evt);
                }
            });
            ToolBarFile.add(ImportToolBarButton);

            ExportToolBarButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/usbscope50software/Icons/Export24.gif"))); // NOI18N
            ExportToolBarButton.setToolTipText("Export Graph Data");
            ExportToolBarButton.setFocusable(false);
            ExportToolBarButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            ExportToolBarButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
            ExportToolBarButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    ExportToolBarButtonActionPerformed(evt);
                }
            });
            ToolBarFile.add(ExportToolBarButton);

            ToolBarZoom.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
            ToolBarZoom.setFloatable(false);
            ToolBarZoom.setFocusable(false);

            FFTToolBarButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/usbscope50software/Icons/FFT.gif"))); // NOI18N
            FFTToolBarButton.setToolTipText("FFT");
            FFTToolBarButton.setFocusable(false);
            FFTToolBarButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            FFTToolBarButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
            FFTToolBarButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    FFTToolBarButtonActionPerformed(evt);
                }
            });
            ToolBarZoom.add(FFTToolBarButton);

            RemoveFFTToolBarButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/usbscope50software/Icons/FFT_crossed.gif"))); // NOI18N
            RemoveFFTToolBarButton.setToolTipText("Cancel FFT");
            RemoveFFTToolBarButton.setFocusable(false);
            RemoveFFTToolBarButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            RemoveFFTToolBarButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
            RemoveFFTToolBarButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    RemoveFFTToolBarButtonActionPerformed(evt);
                }
            });
            ToolBarZoom.add(RemoveFFTToolBarButton);

            ClearmarkersToolBarButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/usbscope50software/Icons/ClearMarkers.gif"))); // NOI18N
            ClearmarkersToolBarButton.setToolTipText("Clear Markers");
            ClearmarkersToolBarButton.setFocusable(false);
            ClearmarkersToolBarButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            ClearmarkersToolBarButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
            ClearmarkersToolBarButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    ClearmarkersToolBarButtonActionPerformed(evt);
                }
            });
            ToolBarZoom.add(ClearmarkersToolBarButton);

            ZoomInToolBarButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/usbscope50software/Icons/ZoomIn24.gif"))); // NOI18N
            ZoomInToolBarButton.setToolTipText("Zoom In");
            ZoomInToolBarButton.setFocusable(false);
            ZoomInToolBarButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            ZoomInToolBarButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
            ZoomInToolBarButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    ZoomInToolBarButtonActionPerformed(evt);
                }
            });
            ToolBarZoom.add(ZoomInToolBarButton);

            ZoomOutToolBarButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/usbscope50software/Icons/ZoomOut24.gif"))); // NOI18N
            ZoomOutToolBarButton.setToolTipText("Zoom Out");
            ZoomOutToolBarButton.setFocusable(false);
            ZoomOutToolBarButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            ZoomOutToolBarButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
            ZoomOutToolBarButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    ZoomOutToolBarButtonActionPerformed(evt);
                }
            });
            ToolBarZoom.add(ZoomOutToolBarButton);

            ZoomCancelToolBarButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/usbscope50software/Icons/ZoomCancel24.gif"))); // NOI18N
            ZoomCancelToolBarButton.setToolTipText("Cancel zoom");
            ZoomCancelToolBarButton.setFocusable(false);
            ZoomCancelToolBarButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            ZoomCancelToolBarButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
            ZoomCancelToolBarButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    ZoomCancelToolBarButtonActionPerformed(evt);
                }
            });
            ToolBarZoom.add(ZoomCancelToolBarButton);

            ToolBarHelp.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
            ToolBarHelp.setFloatable(false);
            ToolBarHelp.setFocusable(false);

            HelpToolBarButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/usbscope50software/Icons/Help24.gif"))); // NOI18N
            HelpToolBarButton.setToolTipText("Help");
            HelpToolBarButton.setFocusable(false);
            HelpToolBarButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            HelpToolBarButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
            HelpToolBarButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    HelpToolBarButtonActionPerformed(evt);
                }
            });
            ToolBarHelp.add(HelpToolBarButton);

            RunToolBar.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
            RunToolBar.setFloatable(false);
            RunToolBar.setRollover(true);
            RunToolBar.setFocusable(false);

            RunToolBarButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/usbscope50software/Icons/Play24.gif"))); // NOI18N
            RunToolBarButton.setToolTipText("Run");
            RunToolBarButton.setFocusable(false);
            RunToolBarButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            RunToolBarButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
            RunToolBarButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    RunToolBarButtonActionPerformed(evt);
                }
            });
            RunToolBar.add(RunToolBarButton);

            StopToolBarButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/usbscope50software/Icons/StopBlue24.gif"))); // NOI18N
            StopToolBarButton.setToolTipText("Stop");
            StopToolBarButton.setFocusable(false);
            StopToolBarButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            StopToolBarButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
            StopToolBarButton.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
                public void mouseDragged(java.awt.event.MouseEvent evt) {
                    StopToolBarButtonMouseDragged(evt);
                }
            });
            StopToolBarButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    StopToolBarButtonActionPerformed(evt);
                }
            });
            RunToolBar.add(StopToolBarButton);

            SingleToolBarButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/usbscope50software/Icons/StepForward24.gif"))); // NOI18N
            SingleToolBarButton.setToolTipText("Trigger Single");
            SingleToolBarButton.setFocusable(false);
            SingleToolBarButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            SingleToolBarButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
            SingleToolBarButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    SingleToolBarButtonActionPerformed(evt);
                }
            });
            RunToolBar.add(SingleToolBarButton);

            TriggerOptionsToolBarButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/usbscope50software/Icons/Preferences24.gif"))); // NOI18N
            TriggerOptionsToolBarButton.setToolTipText("Trigger Options");
            TriggerOptionsToolBarButton.setFocusable(false);
            TriggerOptionsToolBarButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            TriggerOptionsToolBarButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
            TriggerOptionsToolBarButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    TriggerOptionsToolBarButtonActionPerformed(evt);
                }
            });
            RunToolBar.add(TriggerOptionsToolBarButton);

            StatusToolBar.setFloatable(false);
            StatusToolBar.setRollover(true);

            butSpace.setText("    ");
            butSpace.setEnabled(false);
            butSpace.setFocusable(false);
            butSpace.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            butSpace.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
            StatusToolBar.add(butSpace);

            butProductTitle.setText(productID + " status");
            butProductTitle.setFocusable(false);
            butProductTitle.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            butProductTitle.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
            StatusToolBar.add(butProductTitle);

            lbTriggerStatus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/usbscope50software/Icons/free.gif"))); // NOI18N
            StatusToolBar.add(lbTriggerStatus);

            statusButton.setText("initializing " + productID + "s ...");
            statusButton.setFocusable(false);
            statusButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            statusButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
            StatusToolBar.add(statusButton);

            butRISstatus.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
            butRISstatus.setForeground(new java.awt.Color(255, 0, 51));
            butRISstatus.setText(".");
            butRISstatus.setFocusable(false);
            butRISstatus.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            butRISstatus.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
            StatusToolBar.add(butRISstatus);

            SplitPane.setBorder(null);
            SplitPane.setDividerLocation(810);
            SplitPane.setResizeWeight(1.0);
            SplitPane.setLastDividerLocation(810);
            SplitPane.setMinimumSize(new java.awt.Dimension(0, 0));
            SplitPane.setOneTouchExpandable(true);
            SplitPane.setPreferredSize(new java.awt.Dimension(200, 250));
            SplitPane.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
                public void propertyChange(java.beans.PropertyChangeEvent evt) {
                    SplitPanePropertyChange(evt);
                }
            });

            LeftSplitPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

            PanelforChart.addComponentListener(new java.awt.event.ComponentAdapter() {
                public void componentResized(java.awt.event.ComponentEvent evt) {
                    PanelforChartComponentResized(evt);
                }
            });

            javax.swing.GroupLayout PanelforChartLayout = new javax.swing.GroupLayout(PanelforChart);
            PanelforChart.setLayout(PanelforChartLayout);
            PanelforChartLayout.setHorizontalGroup(
                PanelforChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 0, Short.MAX_VALUE)
            );
            PanelforChartLayout.setVerticalGroup(
                PanelforChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 351, Short.MAX_VALUE)
            );

            TriggerPositionSlider.setMaximum(3072);
            TriggerPositionSlider.setToolTipText("Trigger Position");
            TriggerPositionSlider.setValue(0);
            TriggerPositionSlider.setMinimumSize(new java.awt.Dimension(0, 0));
            TriggerPositionSlider.setPreferredSize(new java.awt.Dimension(0, 0));
            TriggerPositionSlider.setValueIsAdjusting(true);
            TriggerPositionSlider.addChangeListener(new javax.swing.event.ChangeListener() {
                public void stateChanged(javax.swing.event.ChangeEvent evt) {
                    TriggerPositionSliderStateChanged(evt);
                }
            });

            panelVDiv.setBorder(javax.swing.BorderFactory.createEtchedBorder());

            lbVDivCh1.setEditable(false);
            lbVDivCh1.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
            lbVDivCh1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
            lbVDivCh1.setText("jTextField1111");
            lbVDivCh1.setToolTipText("Channel 1 V/Div setting");
            lbVDivCh1.setAutoscrolls(false);
            lbVDivCh1.setBorder(null);
            lbVDivCh1.setFocusable(false);
            lbVDivCh1.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    lbVDivCh1MouseClicked(evt);
                }
            });

            lbVDivCh2.setEditable(false);
            lbVDivCh2.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
            lbVDivCh2.setHorizontalAlignment(javax.swing.JTextField.CENTER);
            lbVDivCh2.setText("jTextField2111");
            lbVDivCh2.setToolTipText("Channel 2 V/Div setting");
            lbVDivCh2.setAutoscrolls(false);
            lbVDivCh2.setBorder(null);
            lbVDivCh2.setFocusable(false);
            lbVDivCh2.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    lbVDivCh2MouseClicked(evt);
                }
            });

            lbVDivCh3.setEditable(false);
            lbVDivCh3.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
            lbVDivCh3.setHorizontalAlignment(javax.swing.JTextField.CENTER);
            lbVDivCh3.setText("jTextField3111");
            lbVDivCh3.setToolTipText("Channel 3 V/Div setting");
            lbVDivCh3.setAutoscrolls(false);
            lbVDivCh3.setBorder(null);
            lbVDivCh3.setFocusable(false);
            lbVDivCh3.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    lbVDivCh3MouseClicked(evt);
                }
            });

            lbVDivCh4.setEditable(false);
            lbVDivCh4.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
            lbVDivCh4.setHorizontalAlignment(javax.swing.JTextField.CENTER);
            lbVDivCh4.setText("jTextField4111");
            lbVDivCh4.setToolTipText("Channel 4 V/Div setting");
            lbVDivCh4.setAutoscrolls(false);
            lbVDivCh4.setBorder(null);
            lbVDivCh4.setFocusable(false);
            lbVDivCh4.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    lbVDivCh4MouseClicked(evt);
                }
            });

            lbTDivAllChs.setEditable(false);
            lbTDivAllChs.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
            lbTDivAllChs.setHorizontalAlignment(javax.swing.JTextField.CENTER);
            lbTDivAllChs.setText("Time Base: 100 us/div");
            lbTDivAllChs.setToolTipText("Current time base setting");
            lbTDivAllChs.setAutoscrolls(false);
            lbTDivAllChs.setBorder(null);
            lbTDivAllChs.setFocusable(false);

            lbFDivAllChs.setEditable(false);
            lbFDivAllChs.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
            lbFDivAllChs.setHorizontalAlignment(javax.swing.JTextField.CENTER);
            lbFDivAllChs.setText("FFT: 166 kHz/div");
            lbFDivAllChs.setToolTipText("FFT per/div setting");
            lbFDivAllChs.setAutoscrolls(false);
            lbFDivAllChs.setBorder(null);
            lbFDivAllChs.setFocusable(false);
            lbFDivAllChs.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
                public void propertyChange(java.beans.PropertyChangeEvent evt) {
                    lbFDivAllChsPropertyChange(evt);
                }
            });

            javax.swing.GroupLayout panelVDivLayout = new javax.swing.GroupLayout(panelVDiv);
            panelVDiv.setLayout(panelVDivLayout);
            panelVDivLayout.setHorizontalGroup(
                panelVDivLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelVDivLayout.createSequentialGroup()
                    .addComponent(lbVDivCh1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(lbVDivCh2, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(lbVDivCh3, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(lbVDivCh4, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(lbTDivAllChs, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(lbFDivAllChs, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
            panelVDivLayout.setVerticalGroup(
                panelVDivLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelVDivLayout.createSequentialGroup()
                    .addGap(0, 0, 0)
                    .addGroup(panelVDivLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lbVDivCh1, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lbVDivCh2, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lbVDivCh3, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lbVDivCh4, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lbTDivAllChs, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lbFDivAllChs, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)))
            );

            javax.swing.GroupLayout LeftSplitPanelLayout = new javax.swing.GroupLayout(LeftSplitPanel);
            LeftSplitPanel.setLayout(LeftSplitPanelLayout);
            LeftSplitPanelLayout.setHorizontalGroup(
                LeftSplitPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(TriggerPositionSlider, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(PanelforChart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(panelVDiv, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            );
            LeftSplitPanelLayout.setVerticalGroup(
                LeftSplitPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, LeftSplitPanelLayout.createSequentialGroup()
                    .addComponent(panelVDiv, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, 0)
                    .addComponent(PanelforChart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(TriggerPositionSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
            );

            SplitPane.setLeftComponent(LeftSplitPanel);

            RightSplitPane.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
            RightSplitPane.setTabPlacement(javax.swing.JTabbedPane.RIGHT);
            RightSplitPane.setFocusable(false);
            RightSplitPane.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    RightSplitPaneMouseClicked(evt);
                }
            });
            RightSplitPane.addContainerListener(new java.awt.event.ContainerAdapter() {
                public void componentAdded(java.awt.event.ContainerEvent evt) {
                    RightSplitPaneComponentAdded(evt);
                }
                public void componentRemoved(java.awt.event.ContainerEvent evt) {
                    RightSplitPaneComponentRemoved(evt);
                }
            });
            SplitPane.setRightComponent(RightSplitPane);

            MenuBar.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

            MnuFile.setMnemonic('F');
            MnuFile.setText("File");
            MnuFile.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

            MnuSaveAs.setIcon(new javax.swing.ImageIcon(getClass().getResource("/usbscope50software/Icons/Save16.gif"))); // NOI18N
            MnuSaveAs.setMnemonic('a');
            MnuSaveAs.setText("Save Graph");
            MnuSaveAs.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    MnuSaveAsActionPerformed(evt);
                }
            });
            MnuFile.add(MnuSaveAs);
            MnuFile.add(Sep3);

            MnuPrint.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
            MnuPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/usbscope50software/Icons/Print16.gif"))); // NOI18N
            MnuPrint.setMnemonic('P');
            MnuPrint.setText("Print");
            MnuPrint.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    MnuPrintActionPerformed(evt);
                }
            });
            MnuFile.add(MnuPrint);
            MnuFile.add(Sep4);

            MnuExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_MASK));
            MnuExit.setMnemonic('x');
            MnuExit.setText("Exit");
            MnuExit.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    MnuExitActionPerformed(evt);
                }
            });
            MnuFile.add(MnuExit);

            MenuBar.add(MnuFile);

            MnuEdit.setMnemonic('E');
            MnuEdit.setText("Edit");
            MnuEdit.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
            MnuEdit.add(Sep5);

            MnuImportGraph.setIcon(new javax.swing.ImageIcon(getClass().getResource("/usbscope50software/Icons/Import16.gif"))); // NOI18N
            MnuImportGraph.setText("Import Graph Data");
            MnuImportGraph.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    MnuImportGraphActionPerformed(evt);
                }
            });
            MnuEdit.add(MnuImportGraph);

            MnuExport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/usbscope50software/Icons/Export16.gif"))); // NOI18N
            MnuExport.setText("Export Graph Data");
            MnuExport.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    MnuExportActionPerformed(evt);
                }
            });
            MnuEdit.add(MnuExport);

            MenuBar.add(MnuEdit);

            MnuView.setMnemonic('V');
            MnuView.setText("View");
            MnuView.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

            MnuViewonFullScreen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/usbscope50software/Icons/behavior.ne.resize.mac.gif"))); // NOI18N
            MnuViewonFullScreen.setText("Maximize Screen Size");
            MnuViewonFullScreen.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    MnuViewonFullScreenActionPerformed(evt);
                }
            });
            MnuView.add(MnuViewonFullScreen);

            MnuRestoretoDefaultScreenSize.setIcon(new javax.swing.ImageIcon(getClass().getResource("/usbscope50software/Icons/behavior.sw.resize.mac.gif"))); // NOI18N
            MnuRestoretoDefaultScreenSize.setText("Unmaximize Screen Size");
            MnuRestoretoDefaultScreenSize.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    MnuRestoretoDefaultScreenSizeActionPerformed(evt);
                }
            });
            MnuView.add(MnuRestoretoDefaultScreenSize);
            MnuView.add(Sep6);

            MnuGraphOnly.setText("Show Graph Only");
            MnuGraphOnly.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    MnuGraphOnlyActionPerformed(evt);
                }
            });
            MnuView.add(MnuGraphOnly);

            MnuGraphandOp.setText("Show Graph and Options");
            MnuGraphandOp.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    MnuGraphandOpActionPerformed(evt);
                }
            });
            MnuView.add(MnuGraphandOp);
            MnuView.add(Sep6_1);

            MnuChOptions.setText("Channel Options");

            MnuCh1.setText("Show Ch 1 Options in Tab");
            MnuCh1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    MnuCh1ActionPerformed(evt);
                }
            });
            MnuChOptions.add(MnuCh1);

            MnuCh2.setText("Show Ch 2 Options in Tab");
            MnuCh2.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    MnuCh2ActionPerformed(evt);
                }
            });
            MnuChOptions.add(MnuCh2);

            MnuCh3.setText("Show Ch 3 Options in Tab");
            MnuCh3.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    MnuCh3ActionPerformed(evt);
                }
            });
            MnuChOptions.add(MnuCh3);

            MnuCh4.setText("Show Ch 4 Options in Tab");
            MnuCh4.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    MnuCh4ActionPerformed(evt);
                }
            });
            MnuChOptions.add(MnuCh4);

            MnuView.add(MnuChOptions);

            MnuTrigger.setIcon(new javax.swing.ImageIcon(getClass().getResource("/usbscope50software/Icons/Preferences16.gif"))); // NOI18N
            MnuTrigger.setText("Trigger Options");
            MnuTrigger.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    MnuTriggerActionPerformed(evt);
                }
            });
            MnuView.add(MnuTrigger);
            MnuView.add(Sep9_0);

            MnuZoom.setIcon(new javax.swing.ImageIcon(getClass().getResource("/usbscope50software/Icons/Zoom16.gif"))); // NOI18N
            MnuZoom.setText("Zoom");

            MnuZoomIn.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_PAGE_UP, 0));
            MnuZoomIn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/usbscope50software/Icons/ZoomIn16.gif"))); // NOI18N
            MnuZoomIn.setMnemonic('Z');
            MnuZoomIn.setText("Zoom In");
            MnuZoomIn.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    MnuZoomInActionPerformed(evt);
                }
            });
            MnuZoom.add(MnuZoomIn);

            MnuZoomOut.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_PAGE_DOWN, 0));
            MnuZoomOut.setIcon(new javax.swing.ImageIcon(getClass().getResource("/usbscope50software/Icons/ZoomOut16.gif"))); // NOI18N
            MnuZoomOut.setMnemonic('O');
            MnuZoomOut.setText("Zoom Out");
            MnuZoomOut.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    MnuZoomOutActionPerformed(evt);
                }
            });
            MnuZoom.add(MnuZoomOut);

            MnuZoomCustom.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_MASK));
            MnuZoomCustom.setIcon(new javax.swing.ImageIcon(getClass().getResource("/usbscope50software/Icons/Zoom16Custom.gif"))); // NOI18N
            MnuZoomCustom.setText("Zoom Custom");
            MnuZoomCustom.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    MnuZoomCustomActionPerformed(evt);
                }
            });
            MnuZoom.add(MnuZoomCustom);

            MnuZoomCancel.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_MASK));
            MnuZoomCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/usbscope50software/Icons/ZoomCancel16.gif"))); // NOI18N
            MnuZoomCancel.setText("Cancel Zoom");
            MnuZoomCancel.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    MnuZoomCancelActionPerformed(evt);
                }
            });
            MnuZoom.add(MnuZoomCancel);

            MnuView.add(MnuZoom);
            MnuView.add(Sep7);

            MnuDisplayonOneGraph.setText("Display All on One Graph");
            MnuDisplayonOneGraph.setEnabled(false);
            MnuDisplayonOneGraph.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    MnuDisplayonOneGraphActionPerformed(evt);
                }
            });
            MnuView.add(MnuDisplayonOneGraph);

            MnuSplitGraphs.setText("Split Graphs");
            MnuSplitGraphs.setEnabled(false);
            MnuView.add(MnuSplitGraphs);

            MenuBar.add(MnuView);

            MnuRun.setMnemonic('R');
            MnuRun.setText("Run");
            MnuRun.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

            MnuRunGraph.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, 0));
            MnuRunGraph.setIcon(new javax.swing.ImageIcon(getClass().getResource("/usbscope50software/Icons/Play16.gif"))); // NOI18N
            MnuRunGraph.setMnemonic('R');
            MnuRunGraph.setText("Run");
            MnuRunGraph.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    MnuRunGraphActionPerformed(evt);
                }
            });
            MnuRun.add(MnuRunGraph);

            MnuStop.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, 0));
            MnuStop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/usbscope50software/Icons/StopBlue16.gif"))); // NOI18N
            MnuStop.setMnemonic('S');
            MnuStop.setText("Stop");
            MnuStop.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    MnuStopActionPerformed(evt);
                }
            });
            MnuRun.add(MnuStop);
            MnuRun.add(Sep8);

            MnuSingle.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, 0));
            MnuSingle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/usbscope50software/Icons/StepForward16.gif"))); // NOI18N
            MnuSingle.setText("Trigger Single");
            MnuSingle.setToolTipText("Trigger Single");
            MnuSingle.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    MnuSingleActionPerformed(evt);
                }
            });
            MnuRun.add(MnuSingle);

            MenuBar.add(MnuRun);

            MnuTools.setMnemonic('T');
            MnuTools.setText("Tools");
            MnuTools.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

            MnuAddHorizontalMarker.setSelected(true);
            MnuAddHorizontalMarker.setText("Add Horizontal Marker");
            MnuAddHorizontalMarker.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    MnuAddHorizontalMarkerActionPerformed(evt);
                }
            });
            MnuTools.add(MnuAddHorizontalMarker);

            MnuAddVerticalMarker.setSelected(true);
            MnuAddVerticalMarker.setText("Add Vertical Marker");
            MnuTools.add(MnuAddVerticalMarker);
            MnuTools.add(Sep9_1);

            jMenu1.setText("Clear Offset");

            OffsetZeroCh1.setText("Channel 1");
            OffsetZeroCh1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    OffsetZeroCh1ActionPerformed(evt);
                }
            });
            jMenu1.add(OffsetZeroCh1);

            OffsetZeroCh2.setText("Channel 2");
            OffsetZeroCh2.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    OffsetZeroCh2ActionPerformed(evt);
                }
            });
            jMenu1.add(OffsetZeroCh2);

            OffsetZeroCh3.setText("Channel 3");
            OffsetZeroCh3.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    OffsetZeroCh3ActionPerformed(evt);
                }
            });
            jMenu1.add(OffsetZeroCh3);

            OffsetZeroCh4.setText("Channel 4");
            OffsetZeroCh4.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    OffsetZeroCh4ActionPerformed(evt);
                }
            });
            jMenu1.add(OffsetZeroCh4);

            MnuTools.add(jMenu1);
            MnuTools.add(jSeparator6);

            MnuMainFFT.setText("FFT");

            MnuFFT.setText("FFT");
            MnuFFT.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    MnuFFTActionPerformed(evt);
                }
            });
            MnuMainFFT.add(MnuFFT);

            MnuRemoveFFT.setText("Cancel FFT");
            MnuRemoveFFT.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    MnuRemoveFFTActionPerformed(evt);
                }
            });
            MnuMainFFT.add(MnuRemoveFFT);

            MnuFFTOnly.setText("Show FFT graph only");
            MnuFFTOnly.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    MnuFFTOnlyActionPerformed(evt);
                }
            });
            MnuMainFFT.add(MnuFFTOnly);

            MnuTools.add(MnuMainFFT);
            MnuTools.add(jSeparator4);

            MnuClearMarkers.setText("Clear Markers");

            MnuClearAllMarkers.setText("Clear All Markers");
            MnuClearAllMarkers.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    MnuClearAllMarkersActionPerformed(evt);
                }
            });
            MnuClearMarkers.add(MnuClearAllMarkers);
            MnuClearMarkers.add(Sep9_2);

            MnuClearHorizontalMarkers.setText("Clear Horizontal Markers");
            MnuClearHorizontalMarkers.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    MnuClearHorizontalMarkersActionPerformed(evt);
                }
            });
            MnuClearMarkers.add(MnuClearHorizontalMarkers);

            MnuClearVerticalMarkers.setText("Clear Vertical Markers");
            MnuClearVerticalMarkers.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    MnuClearVerticalMarkersActionPerformed(evt);
                }
            });
            MnuClearMarkers.add(MnuClearVerticalMarkers);

            MnuTools.add(MnuClearMarkers);

            MenuBar.add(MnuTools);

            MnuSettings.setMnemonic('S');
            MnuSettings.setText("Settings");
            MnuSettings.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

            MnuChannelSettings.setText("Channel Colours");
            MnuChannelSettings.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    MnuChannelSettingsActionPerformed(evt);
                }
            });
            MnuSettings.add(MnuChannelSettings);

            MnuChartOptions.setText("Chart Settings");
            MnuChartOptions.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    MnuChartOptionsActionPerformed(evt);
                }
            });
            MnuSettings.add(MnuChartOptions);
            MnuSettings.add(Sep10);

            MnuFFTOptions.setText("FFT Options");

            MnuFFTPlotType.setText("FFT Plot Type");

            buttonGroupFFTPlotType.add(MnudBVFFTPlot);
            MnudBVFFTPlot.setText("dB(V) FFT Plot");
            MnudBVFFTPlot.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    MnudBVFFTPlotActionPerformed(evt);
                }
            });
            MnuFFTPlotType.add(MnudBVFFTPlot);

            buttonGroupFFTPlotType.add(MnuLinearFFTPlot);
            MnuLinearFFTPlot.setText("Linear FFT Plot");
            MnuLinearFFTPlot.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    MnuLinearFFTPlotActionPerformed(evt);
                }
            });
            MnuFFTPlotType.add(MnuLinearFFTPlot);

            MnuFFTOptions.add(MnuFFTPlotType);

            MnuFFTWindowType.setText("FFT Windows Type");

            buttonGroupFFTWindowType.add(MnuRectangular);
            MnuRectangular.setSelected(true);
            MnuRectangular.setText("Rectangular");
            MnuRectangular.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    MnuRectangularActionPerformed(evt);
                }
            });
            MnuFFTWindowType.add(MnuRectangular);

            buttonGroupFFTWindowType.add(MnuHanning);
            MnuHanning.setText("Hanning");
            MnuHanning.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    MnuHanningActionPerformed(evt);
                }
            });
            MnuFFTWindowType.add(MnuHanning);

            buttonGroupFFTWindowType.add(MnuHamming);
            MnuHamming.setText("Hamming");
            MnuHamming.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    MnuHammingActionPerformed(evt);
                }
            });
            MnuFFTWindowType.add(MnuHamming);

            buttonGroupFFTWindowType.add(MnuTriangular);
            MnuTriangular.setText("Triangular");
            MnuTriangular.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    MnuTriangularActionPerformed(evt);
                }
            });
            MnuFFTWindowType.add(MnuTriangular);

            buttonGroupFFTWindowType.add(MnuWelch);
            MnuWelch.setText("Welch");
            MnuWelch.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    MnuWelchActionPerformed(evt);
                }
            });
            MnuFFTWindowType.add(MnuWelch);

            MnuFFTOptions.add(MnuFFTWindowType);

            MnuSettings.add(MnuFFTOptions);
            MnuSettings.add(jSeparator5);

            MnuGraph.setText("Graph");

            MnuShowYAxisLabels.setText("Show Y-Axis Labels");
            MnuShowYAxisLabels.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    MnuShowYAxisLabelsActionPerformed(evt);
                }
            });
            MnuGraph.add(MnuShowYAxisLabels);

            MnuSettings.add(MnuGraph);

            MenuBar.add(MnuSettings);

            MnuHelp.setMnemonic('H');
            MnuHelp.setText("Help");
            MnuHelp.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

            MnuHelpF1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
            MnuHelpF1.setText("Help");
            MnuHelpF1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    MnuHelpF1ActionPerformed(evt);
                }
            });
            MnuHelp.add(MnuHelpF1);
            MnuHelp.add(jSeparator1);

            jMenuItem1.setText("Tutorial Save Graph");
            jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jMenuItem1ActionPerformed(evt);
                }
            });
            MnuHelp.add(jMenuItem1);

            MnuTutorial.setMnemonic('T');
            MnuTutorial.setText("Tutorial Probe COMP");
            MnuTutorial.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    MnuTutorialActionPerformed(evt);
                }
            });
            MnuHelp.add(MnuTutorial);
            MnuHelp.add(jSeparator2);

            MnuUG.setText("USBscope50 User Guide");
            MnuUG.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    MnuUGActionPerformed(evt);
                }
            });
            MnuHelp.add(MnuUG);
            MnuHelp.add(jSeparator3);

            jMenuItem2.setText("Check for Updates");
            jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    jMenuItem2ActionPerformed(evt);
                }
            });
            MnuHelp.add(jMenuItem2);

            MnuSupport.setMnemonic('S');
            MnuSupport.setText("Support");
            MnuSupport.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    MnuSupportActionPerformed(evt);
                }
            });
            MnuHelp.add(MnuSupport);
            MnuHelp.add(Sep12);

            MnuAbout.setMnemonic('A');
            MnuAbout.setText("About");
            MnuAbout.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    MnuAboutActionPerformed(evt);
                }
            });
            MnuAbout.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyPressed(java.awt.event.KeyEvent evt) {
                    MnuAboutKeyPressed(evt);
                }
            });
            MnuHelp.add(MnuAbout);

            MenuBar.add(MnuHelp);

            setJMenuBar(MenuBar);

            javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
            getContentPane().setLayout(layout);
            layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(ToolBarFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, 0)
                    .addComponent(RunToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, 0)
                    .addComponent(ToolBarZoom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(1, 1, 1)
                    .addComponent(ToolBarHelp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, 0)
                    .addComponent(StatusToolBar, javax.swing.GroupLayout.DEFAULT_SIZE, 435, Short.MAX_VALUE))
                .addGroup(layout.createSequentialGroup()
                    .addComponent(SplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 953, Short.MAX_VALUE)
                    .addGap(5, 5, 5))
            );
            layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(ToolBarZoom, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(StatusToolBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(ToolBarHelp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(ToolBarFile, 0, 0, Short.MAX_VALUE)
                        .addComponent(RunToolBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGap(0, 0, 0)
                    .addComponent(SplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 397, Short.MAX_VALUE)
                    .addContainerGap())
            );

            pack();
        }// </editor-fold>//GEN-END:initComponents
    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        String title;

        statusButton.setText("initializing " + productID + "s ...");
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        InitializeScopes();
        setCursor(Cursor.getDefaultCursor());
        ReadNullOffsetSettings();
        InitialCouplingValues();
        InitialTriggerSettings();
        //InitialChSettings();

        MnuShowYAxisLabelsActionPerformed();
        MnudBVFFTPlotActionPerformed();
        MnuLinearFFTPlotActionPerformed();
        if (demoMode) {
            title = productID + " Software - DEMO MODE";
        } else {
            title = productID + " Software";
        }
        this.setTitle(title);
        t.start();

        for (int ch = 1; ch <= 4; ch++) {//set v/div now settings read from the config file
            VoltsperDivChangedEvent(intVoltsperDivPointer[ch], ch);// ch=1 for channel1, ch=2 for channel 2, etc.
        }
        RecordSignalSamplePeriod();//SignalSamplePeriod = ;
        RedrawTickUnits(1, true, intVoltsperDivPointer[1], true, 0);//necessary to display 30 horizontal divisions

        ChColorOKActionPerformed();

        MnuRunClicked();
        TimeBaseChangedEvent(intTimeBaseArrayPointer); //i think it squashes it???it doesn't, but i still don't know why i have put it here it makes ris labels invisible


        //FFT
        FFT_Ch1_4 = FFT_Channel[1] || FFT_Channel[2] || FFT_Channel[3] || FFT_Channel[4];
        FFTFunctionSelected(FFT_Ch1_4);//true or false

        OrganizeTabs("Ch 1");

        SliderThreshold.setValue((int) initialThreshold);
        TriggerPositionSlider.setValue(initialPretrigger);

        OffsetSliderCh1.setValue((int) functionOffset[1]);
        OffsetSliderCh2.setValue((int) functionOffset[2]);
        OffsetSliderCh3.setValue((int) functionOffset[3]);
        OffsetSliderCh4.setValue((int) functionOffset[4]);
        if (demoMode) {
            PlotOffsetDataDemo(1, functionOffset[1]);
            PlotOffsetDataDemo(2, functionOffset[2]);
            PlotOffsetDataDemo(3, functionOffset[3]);
            PlotOffsetDataDemo(4, functionOffset[4]);

            ZeroPointCh1Value = (YDiv[1] * 5) * functionOffset[1] / 10000;
            if (Probe[1] == 2) {
                ZeroPointCh1Value = ZeroPointCh1Value * 10;
            }
            ZeroPointCh1.setY(ZeroPointCh1Value);

            ZeroPointCh2Value = (YDiv[1] * 5) * functionOffset[2] / 10000;
            if (Probe[1] == 2) {//channel 1 yes, this is correct. all annotations are linked to domain of channel 1. JFreeChart restriction
                ZeroPointCh2Value = ZeroPointCh2Value * 10;
            }
            ZeroPointCh2.setY(ZeroPointCh2Value);

            ZeroPointCh3Value = (YDiv[1] * 5) * functionOffset[3] / 10000;
            if (Probe[1] == 2) {//channel 1 yes, this is correct. all annotations are linked to domain of channel 1. JFreeChart restriction
                ZeroPointCh3Value = ZeroPointCh3Value * 10;
            }
            ZeroPointCh3.setY(ZeroPointCh3Value);

            ZeroPointCh4Value = (YDiv[1] * 5) * functionOffset[4] / 10000;
            if (Probe[1] == 2) {//channel 1 yes, this is correct. all annotations are linked to domain of channel 1. JFreeChart restriction
                ZeroPointCh4Value = ZeroPointCh4Value * 10;
            }
            ZeroPointCh4.setY(ZeroPointCh4Value);
        }

        OffsetSelectNone();
        InitialChSettings();
    }//GEN-LAST:event_formWindowOpened

    private void MnuRunGraphActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnuRunGraphActionPerformed
        MnuRunClicked();
    }//GEN-LAST:event_MnuRunGraphActionPerformed

    private void MnuStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnuStopActionPerformed
        MnuStopClicked();
        FirstStopped = true;
    }//GEN-LAST:event_MnuStopActionPerformed

    private void RunToolBarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RunToolBarButtonActionPerformed

        MnuRunClicked();
	}//GEN-LAST:event_RunToolBarButtonActionPerformed

    private void OpenToolBarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OpenToolBarButtonActionPerformed
	}//GEN-LAST:event_OpenToolBarButtonActionPerformed

    private void StopToolBarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_StopToolBarButtonActionPerformed

        MnuStopClicked();
        FirstStopped = true;
    }//GEN-LAST:event_StopToolBarButtonActionPerformed

    private void MnuZoomInActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnuZoomInActionPerformed
        MnuZoomInClicked();
    }//GEN-LAST:event_MnuZoomInActionPerformed

    private void ZoomInToolBarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ZoomInToolBarButtonActionPerformed
        MnuZoomInClicked();
}//GEN-LAST:event_ZoomInToolBarButtonActionPerformed

    private void MnuZoomOutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnuZoomOutActionPerformed
        MnuZoomOutClicked();
    }//GEN-LAST:event_MnuZoomOutActionPerformed

    private void drawZeroPointChMarkers(int numPresentChannels, boolean ZeroOnCh1, boolean ZeroOnCh2, boolean ZeroOnCh3, boolean ZeroOnCh4) {
        int Xlocation = -1470;
        Font font = new Font("SansSerif", Font.PLAIN, 9);


        Xlocation = (int) ((axisX.getUpperBound()) + Math.abs(axisX.getLowerBound()));
        Xlocation = Xlocation / 100;

        Xlocation = (int) (axisX.getLowerBound() + Xlocation);

        if (!demoMode && JavaRunning) {
            if (intTimeBaseArrayPointer == 5) {
                Xlocation = -1497;
            } else if (intTimeBaseArrayPointer == 6) {
                Xlocation = -1493;
            } else if (intTimeBaseArrayPointer == 7) {
                Xlocation = -1485;
            } else if (intTimeBaseArrayPointer == 8) {
                Xlocation = -1470;
            } else if (intTimeBaseArrayPointer == 4) {
                Xlocation = -1470;
            } else if (intTimeBaseArrayPointer == 3) {
                Xlocation = -1468;
            } else if (intTimeBaseArrayPointer == 2) {
                Xlocation = -1473;
            } else if (intTimeBaseArrayPointer == 1) {
                Xlocation = -1476;
            } else if (intTimeBaseArrayPointer == 0) {
                Xlocation = -1478;
            }
        }


        if (numPresentChannels > 3 || ZeroOnCh4) {

            //current =  chartPlot.getDomainAxis(4);
            ZeroPointCh4 = new XYTextAnnotation("∇4", Xlocation, ZeroPointCh4Value);
            //ZeroPointCh4 = new XYTextAnnotation("∇Ch4", 700, 0);
            ZeroPointCh4.setFont(font);
            ZeroPointCh4.setBackgroundPaint(ColorCh4);
            //ZeroPointCh1.setTextAnchor(TextAnchor.HALF_ASCENT_LEFT);
            chartPlot.addAnnotation(ZeroPointCh4);
        }
        if (numPresentChannels > 2 || ZeroOnCh3) {
            //current =  chartPlot.getDomainAxis(3);
            ZeroPointCh3 = new XYTextAnnotation("∇3", Xlocation, ZeroPointCh3Value);
            //ZeroPointCh3 = new XYTextAnnotation("∇Ch3", 300, 0);
            ZeroPointCh3.setFont(font);
            ZeroPointCh3.setBackgroundPaint(ColorCh3);
            //ZeroPointCh1.setTextAnchor(TextAnchor.HALF_ASCENT_LEFT);
            chartPlot.addAnnotation(ZeroPointCh3);
        }


        if (numPresentChannels > 1 || ZeroOnCh2) {
            //current =  chartPlot.getDomainAxis(2);
            ZeroPointCh2 = new XYTextAnnotation("∇2", Xlocation, ZeroPointCh2Value);
            //ZeroPointCh2 = new XYTextAnnotation("∇Ch2", 100, 0);
            ZeroPointCh2.setFont(font);
            ZeroPointCh2.setBackgroundPaint(ColorCh2);
            //ZeroPointCh1.setTextAnchor(TextAnchor.HALF_ASCENT_LEFT);
            chartPlot.addAnnotation(ZeroPointCh2);
        }
        if (numPresentChannels > 0 || ZeroOnCh1) {
            ZeroPointCh1 = new XYTextAnnotation("∇1", Xlocation, ZeroPointCh1Value);
            //ZeroPointCh1 = new XYTextAnnotation("∇Ch1", 0, 0);
            ZeroPointCh1.setFont(font);
            ZeroPointCh1.setBackgroundPaint(ColorCh1);
            //ZeroPointCh1.setTextAnchor(TextAnchor.HALF_ASCENT_LEFT);
            chartPlot.addAnnotation(ZeroPointCh1);
        }


    /*ImageIcon Gnd1 = new ImageIcon(getClass().getResource("/usbscope50software/Images/gnd1.jpg"));
    testImageAnnotation = new XYImageAnnotation(0, 0, Gnd1.getImage());
    chartPlot.addAnnotation(testImageAnnotation);*/

    }
    private void ZoomOutToolBarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ZoomOutToolBarButtonActionPerformed

        MnuZoomOutClicked();
    }//GEN-LAST:event_ZoomOutToolBarButtonActionPerformed

    private void MnuZoomCustomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnuZoomCustomActionPerformed
        MnuZoomCustomClicked();
    }//GEN-LAST:event_MnuZoomCustomActionPerformed

    private void SplitPanePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_SplitPanePropertyChange

        SlitPaneEffect = SplitPane.getWidth() - SplitPane.getDividerLocation() - SplitPane.getDividerSize() - 3 - 70;
    }//GEN-LAST:event_SplitPanePropertyChange

    private void OKXAxisCustomZoomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OKXAxisCustomZoomActionPerformed
        OKXAxisCustomZoomAction(Integer.valueOf(XAxisZoomValue.getValue().toString()));

    // On the custom zoom tab OK button selected to zoom on the graph x-axis
 /*       int ZoomValue = Integer.valueOf(XAxisZoomValue.getValue().toString());
    //Zoom X-Axis
    if (ZoomValue < 100) { //zoom in
    ZoomValue = 100 - ZoomValue;
    axisX.zoomRange((double) ZoomValue / 200, (double) (100 - (ZoomValue / 2)) / 100);
    } else if (ZoomValue == 100) {    //100% do nothing
    } else {              //zoom out
    axisX.zoomRange(-(double) ((ZoomValue - 100) / 2) / 100, (double) (ZoomValue - ((ZoomValue - 100) / 2)) / 100);
    }
     */
    }//GEN-LAST:event_OKXAxisCustomZoomActionPerformed

    private void OKYAxisCustomZoomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OKYAxisCustomZoomActionPerformed
        OKYAxisCustomZoomAction(Integer.valueOf(YAxisZoomValue.getValue().toString()));
    }//GEN-LAST:event_OKYAxisCustomZoomActionPerformed

    private void MnuNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnuNewActionPerformed
        //TidyUpTheGraph();
    }//GEN-LAST:event_MnuNewActionPerformed

    private void MnuViewonFullScreenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnuViewonFullScreenActionPerformed
        this.setExtendedState(this.getExtendedState() | USBscope50_Main.MAXIMIZED_BOTH);
    }//GEN-LAST:event_MnuViewonFullScreenActionPerformed

    private void MnuRestoretoDefaultScreenSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnuRestoretoDefaultScreenSizeActionPerformed
        this.setExtendedState(this.getExtendedState() & USBscope50_Main.NORMAL);
    //SplitPane.setDividerLocation(-1);        //better when this line commented out
    }//GEN-LAST:event_MnuRestoretoDefaultScreenSizeActionPerformed

    private void MnuTriggerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnuTriggerActionPerformed
        MnuTriggerOptionsClicked();
    }//GEN-LAST:event_MnuTriggerActionPerformed

    private void MnuGraphOnlyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnuGraphOnlyActionPerformed
        // simulate OneTouchExpandable left button action preformed;

        SplitPane.setDividerLocation(1.0);
	}//GEN-LAST:event_MnuGraphOnlyActionPerformed

    private void MnuDisplayonOneGraphActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnuDisplayonOneGraphActionPerformed
    }//GEN-LAST:event_MnuDisplayonOneGraphActionPerformed

    private void TriggerModeFreeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TriggerModeFreeActionPerformed
        TriggerModeFreeSelected();
        SetTriggerMode(intTriggerModeFree);
        triggermode = 0;
    }//GEN-LAST:event_TriggerModeFreeActionPerformed

    private void TriggerModeRisingEdgeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TriggerModeRisingEdgeActionPerformed
        TriggerModeRisingEdgeSelected();
        SetTriggerMode(intTriggerModeNormal);
        SetTrigType(intRisingEdge);
        triggermode = 1;
    }//GEN-LAST:event_TriggerModeRisingEdgeActionPerformed

    private void TriggerModeFallingEdgeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TriggerModeFallingEdgeActionPerformed
        TriggerModeFallingEdgeSelected();
        SetTriggerMode(intTriggerModeNormal);
        SetTrigType(intFallingEdge);
        triggermode = 1;
    }//GEN-LAST:event_TriggerModeFallingEdgeActionPerformed

    private void Opx1ProbeCh1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Opx1ProbeCh1ActionPerformed
        ProbeOptionActionPerformed(1, 1);//channel, probe setting
	}//GEN-LAST:event_Opx1ProbeCh1ActionPerformed

    private void TriggerOptionsToolBarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TriggerOptionsToolBarButtonActionPerformed
        MnuTriggerOptionsClicked();
    }//GEN-LAST:event_TriggerOptionsToolBarButtonActionPerformed

    private void RightArrowVDivCh1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RightArrowVDivCh1ActionPerformed
        boolean VDivCtrlMod = (evt.getModifiers() == 18);//change all present channels v/div setting to this one
        RightArrowVDivActionPerformed(1, VDivCtrlMod);//1 stands for channel1
	}//GEN-LAST:event_RightArrowVDivCh1ActionPerformed

    private void MnuExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnuExportActionPerformed
        SaveGraphPointsInTxtFile();
	}//GEN-LAST:event_MnuExportActionPerformed

    public String CheckFileName(
            String FileName, String FileExtensionFilter) {

        String CheckedFileName = "";
        String FileExtension = ".png";

        //.println(FileName + " " + FileName.length());

        if (FileExtensionFilter.toUpperCase().contains("PDF")) {     //pdf filter

            FileExtension = ".pdf";
        } else if (FileExtensionFilter.toUpperCase().contains("JPG")) {//jpeg,jpg filter

            FileExtension = ".jpeg";
            if (FileName.length() > 4 && FileName.contains(".")) {
                if (FileName.substring(FileName.length() - 4, FileName.length()).equalsIgnoreCase(".jpg")) {
                    FileExtension = ".jpg";
                }
            }
        } else {                                                      //png filter

            FileExtension = ".png";
        }

        if (FileName.contains(".")) {
            int loc = FileName.indexOf(".");
            CheckedFileName =
                    FileName.substring(0, loc) + FileExtension;
            return CheckedFileName;
        } else {
            return (FileName + FileExtension);
        }
    }

    private void HelpToolBarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_HelpToolBarButtonActionPerformed
        ShowHelp();
    /*Delta = new XYLineAnnotation(-500.0, YDiv[1]*4.0, 500.0, YDiv[1]*4.0);
    chartPlot.addAnnotation(Delta);
    //Delta=new XYLineAnnotation(-500.0, 1, 500.0, 1);


    //chartPlot.removeAnnotation(Delta);


    XYTextAnnotation deltaText = new XYTextAnnotation("delta time", 0, (YDiv[1]*4.0)+(YDiv[1]/2));
    chartPlot.addAnnotation(deltaText);
     */


    }//GEN-LAST:event_HelpToolBarButtonActionPerformed

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated

        if (!StupidNetbeans) {
            SetUpStatusTimer();
            StatusTimer.start();
            StupidNetbeans = true;
            SetUpNullTimer();
            NoDomainMarkersOnChart = tempNoDomainMarkersOnChart;
            NoRangeMarkersOnChart = tempNoRangeMarkersOnChart;
            updateMarkers(true, true);
        }
    }//GEN-LAST:event_formWindowActivated

    private void MnuClearHorizontalMarkersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnuClearHorizontalMarkersActionPerformed
        ClearRangeMarkers();
    }//GEN-LAST:event_MnuClearHorizontalMarkersActionPerformed

    private void MnuClearVerticalMarkersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnuClearVerticalMarkersActionPerformed
        ClearDomainMarkers();
    }//GEN-LAST:event_MnuClearVerticalMarkersActionPerformed

    private void MnuClearAllMarkersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnuClearAllMarkersActionPerformed
        ClearRangeMarkers();
        ClearDomainMarkers();
    }//GEN-LAST:event_MnuClearAllMarkersActionPerformed

    private void MnuSupportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnuSupportActionPerformed
        String email;

        if (companyID.equals("PRIST")) {
            email = "prist@prist.ru";
        } else {
            email = "sales@elandigitalsystems.com";
        }
        String Msg = ("To contact our tech support team,\nplease write to " + email + "\n\n" +
                "Copyright © 2009 " + companyID + "\n");
        if (companyID.equals("PRIST")) {
            JOptionPane.showMessageDialog(this, Msg, productID + " Software", 1, AKIPLogo_small);
        } else {
            JOptionPane.showMessageDialog(this, Msg, productID + " Software", 1, ElanLogo_Small);
        }
    }//GEN-LAST:event_MnuSupportActionPerformed

    private void MnuAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnuAboutActionPerformed

        String Msg = (productID + " Software\n\n" +
                "Copyright © 2009 " + companyID + "\n" +
                "Release Version " + version + "\n\n" +
                "Software supplied with PC-Cards, Compact Flash cards\n" +
                "or USB device is provided \"as-is\" with no warranty, express or implied,\n" +
                "as to its quality or fitness for a particular purpose.\n" +
                "Software supplier assumes no liability for any direct or indirect losses\n" +
                "arising from use of the supplied code.\n\n" +
                productID + " Software is licensed under the terms of the\n" +
                "GNU General Public License as published by the\n" +
                "Free Software Foundation version 3 of the License.");

        Object[] options = {"View GPL License", "OK"};
        int n;

        if (companyID.equals("PRIST")) {
            n = JOptionPane.showOptionDialog(this, Msg, "About", JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE, AKIPLogo_small, options, options[1]);
        } else {
            n = JOptionPane.showOptionDialog(this, Msg, "About", JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE, ElanLogo_Small, options, options[1]);
        }
        if (n == 0) {//display GPL license

            DisplayLicenseDialog.setTitle("Text version of GPL");
            DisplayLicenseDialog.setSize(505, CustomZoomPanelWidth + 260);
            DisplayLicenseDialog.setResizable(true);
            DisplayLicenseDialog.setLocationRelativeTo(null);
            DisplayLicenseDialog.setVisible(true);
            ExitLicenseDialog.grabFocus();
        }
    }//GEN-LAST:event_MnuAboutActionPerformed

    private void OKXAxisCustomZoomFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_OKXAxisCustomZoomFocusGained
        if (EnterPressedJSpinner) {
            EnterPressedJSpinner = false;
            OKXAxisCustomZoomAction(Integer.valueOf(XAxisZoomValue.getValue().toString()));
        }
    }//GEN-LAST:event_OKXAxisCustomZoomFocusGained

    private void OKYAxisCustomZoomFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_OKYAxisCustomZoomFocusGained
        if (EnterPressedJSpinner) {
            EnterPressedJSpinner = false;
            OKYAxisCustomZoomAction(Integer.valueOf(YAxisZoomValue.getValue().toString()));
        }
    }//GEN-LAST:event_OKYAxisCustomZoomFocusGained

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing

        TidyUpOnExit();
    }//GEN-LAST:event_formWindowClosing

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
    }//GEN-LAST:event_formWindowClosed

    private void MnuHelpF1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnuHelpF1ActionPerformed
        ShowHelp();
    }//GEN-LAST:event_MnuHelpF1ActionPerformed

    private void MnuPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnuPrintActionPerformed
        chartPanel.createChartPrintJob();
    }//GEN-LAST:event_MnuPrintActionPerformed

    private void RightArrowSecDivCh1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RightArrowSecDivCh1ActionPerformed
        RightArrowSecDivActionPerformed();
	}//GEN-LAST:event_RightArrowSecDivCh1ActionPerformed

    private void MnuExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnuExitActionPerformed
        TidyUpOnExit();
        System.exit(0);
    }//GEN-LAST:event_MnuExitActionPerformed

    private void SingleToolBarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SingleToolBarButtonActionPerformed

        MnuSingleClicked();
    }//GEN-LAST:event_SingleToolBarButtonActionPerformed

    private void INVButtonCh1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_INVButtonCh1ActionPerformed
        SetButtonState(1, INVButtonCh1, INVButtonCh1.isSelected());
        INV[1] = INVButtonCh1.isSelected() ? -1 : 1;
        PlotINVData(1, INV[1]);
	}//GEN-LAST:event_INVButtonCh1ActionPerformed

    private void SliderThresholdStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_SliderThresholdStateChanged
        float dbThreshold;
        float SetThresholdTo;//final threshold value after all the adjustemnts

        int x10Adjust = 1;

        if (TriggerModeFree.isSelected()) {
            return;
        }
        if (Probe[1] == 2) {
            x10Adjust = 10;
        }
        Threshold = (float) SliderThreshold.getValue();

        chartPlot.clearRangeMarkers();
        updateMarkers(true, false);

        ThresholdMarkerValue = SliderThreshold.getValue() * 5 * x10Adjust * YDiv[1] / 100.000;
        Marker rangeMarker = new ValueMarker(ThresholdMarkerValue);//SliderThreshold.getValue() * 5 * x10Adjust * YDiv[1] / 100.000);

        rangeMarker.setPaint(Color.DARK_GRAY);
        rangeMarker.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, new float[]{10.0f, 10.0f}, 0.0f));

        chartPlot.addRangeMarker(rangeMarker);
        ThresholdMarkerOn = true;
        dbThreshold = (float) (SliderThreshold.getValue() * (5 * YDiv[TriggerChannel]) / (0.3 * Math.pow(10.0, VoltageGain[TriggerChannel])));

        float offsetTriggerThreshold = (float) ((functionOffset[TriggerChannel] / 100) * (5 * YDiv[TriggerChannel]) / (0.3 * Math.pow(10.0, VoltageGain[TriggerChannel])));

        SetThresholdTo = (float) (dbThreshold - offsetTriggerThreshold);

        if (VoltageGain[TriggerChannel] == 2) {
            if (ChannelCalSourceOn[TriggerChannel]) {
                SetThresholdTo = (float) ((dbThreshold - offsetTriggerThreshold) * 1.0f);//0.85
            } else if (intVoltsperDivPointer[TriggerChannel] == 8) {//1v/div
                SetThresholdTo = (float) ((dbThreshold - offsetTriggerThreshold) * 0.85f);//0.85
            }
        }

        if (VoltageGain[TriggerChannel] == 1) {
            if (ChannelCalSourceOn[TriggerChannel]) {
                SetThresholdTo = (float) ((dbThreshold - offsetTriggerThreshold) * 1.0f);
            } else if (intVoltsperDivPointer[TriggerChannel] == 6) {//200mV/div
                SetThresholdTo = (float) ((dbThreshold - offsetTriggerThreshold) * 0.87f);
            } else if (intVoltsperDivPointer[TriggerChannel] == 5) {//100mV/div
                SetThresholdTo = (float) ((dbThreshold - offsetTriggerThreshold) * 0.8f);
            }
        }

        t.SetTrigThreshold(TriggerChannel, SetThresholdTo, VoltageGain[TriggerChannel], ChannelCalSourceOn[TriggerChannel]);

    //jTextField1.setText(String.valueOf(SetThresholdTo));
	}//GEN-LAST:event_SliderThresholdStateChanged

    private void SliderThresholdFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_SliderThresholdFocusGained
        // TODO add your handling code here:
	}//GEN-LAST:event_SliderThresholdFocusGained

    private void SliderThresholdFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_SliderThresholdFocusLost
        // TODO add your handling code here:
	}//GEN-LAST:event_SliderThresholdFocusLost

    private void LeftArrowSecDivCh1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LeftArrowSecDivCh1ActionPerformed
        LeftArrowSecDivActionPerformed();
	}//GEN-LAST:event_LeftArrowSecDivCh1ActionPerformed

    private void MnuSingleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnuSingleActionPerformed
        MnuSingleClicked();
    }//GEN-LAST:event_MnuSingleActionPerformed

    private void Opx10ProbeCh1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Opx10ProbeCh1ActionPerformed
        ProbeOptionActionPerformed(1, 2);//channel, probe setting 1 for x1, 2 for x10
	}//GEN-LAST:event_Opx10ProbeCh1ActionPerformed

    private void LeftArrowVDivCh1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LeftArrowVDivCh1ActionPerformed
        boolean VDivCtrlMod = (evt.getModifiers() == 18);//change all present channels v/div setting to this one
        LeftArrowVDivActionPerformed(1, VDivCtrlMod);//1 is for channel 1
	}//GEN-LAST:event_LeftArrowVDivCh1ActionPerformed

    private void ZoomCancelToolBarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ZoomCancelToolBarButtonActionPerformed
        MnuZoomCancelClicked();
	}//GEN-LAST:event_ZoomCancelToolBarButtonActionPerformed

    private void MnuZoomCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnuZoomCancelActionPerformed
        MnuZoomCancelClicked();
    }//GEN-LAST:event_MnuZoomCancelActionPerformed

    private void OpACCouplingCh1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OpACCouplingCh1ActionPerformed
        boolean CouplingCtrlMod = (evt.getModifiers() == 18);//change all present channels to match this setting
        CouplingActionPerformed(1, 0, 0, CouplingCtrlMod);//1=channel1,Dc_option[1]=0,gnd_option[1]=0
	}//GEN-LAST:event_OpACCouplingCh1ActionPerformed

    private void StopToolBarButtonMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_StopToolBarButtonMouseDragged
        // TODO add your handling code here:
    }//GEN-LAST:event_StopToolBarButtonMouseDragged

    private void OpDCCouplingCh1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OpDCCouplingCh1ActionPerformed
        boolean CouplingCtrlMod = (evt.getModifiers() == 18);//change all present channels to match this setting
        CouplingActionPerformed(1, 1, 0, CouplingCtrlMod);//1=channel1,Dc_option[1]=1,gnd_option[1]=0
	}//GEN-LAST:event_OpDCCouplingCh1ActionPerformed

    private void OpGNDCh1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OpGNDCh1ActionPerformed
        boolean CouplingCtrlMod = (evt.getModifiers() == 18);//change all present channels to match this setting
        CouplingActionPerformed(1, 0, 1, CouplingCtrlMod);//1=channel1,Dc_option[1]=0,gnd_option[1]=1
	}//GEN-LAST:event_OpGNDCh1ActionPerformed

    private void MnuTutorialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnuTutorialActionPerformed
        DisplayTutorial();
    }//GEN-LAST:event_MnuTutorialActionPerformed

    private void MnuSaveAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnuSaveAsActionPerformed
        SaveChartAs();
    }//GEN-LAST:event_MnuSaveAsActionPerformed

    private void SaveToolBarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SaveToolBarButtonActionPerformed
        SaveChartAs();
    }//GEN-LAST:event_SaveToolBarButtonActionPerformed

    private void ExportToolBarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExportToolBarButtonActionPerformed
        SaveGraphPointsInTxtFile();
    }//GEN-LAST:event_ExportToolBarButtonActionPerformed

    private void MnuImportGraphActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnuImportGraphActionPerformed
        Import3000Points_TxtFile();
    }//GEN-LAST:event_MnuImportGraphActionPerformed

    private void ImportToolBarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ImportToolBarButtonActionPerformed
        Import3000Points_TxtFile();
    }//GEN-LAST:event_ImportToolBarButtonActionPerformed

    private void OptionsCh1ComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_OptionsCh1ComponentShown
	}//GEN-LAST:event_OptionsCh1ComponentShown

    private void RightSplitPaneComponentAdded(java.awt.event.ContainerEvent evt) {//GEN-FIRST:event_RightSplitPaneComponentAdded

        if (!areSeriesVisibile()) {//it will look at the tabs and make sure mnu options show correct settings
            MnuStopClicked();

        }
    }//GEN-LAST:event_RightSplitPaneComponentAdded

    private void TriggerPositionSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_TriggerPositionSliderStateChanged

        //System.out.println("horizontal trigger slider values is " + TriggerPositionSlider.getValue() + " adjusted to " + (TriggerPositionSlider.getValue() + 8));
        if (intTimeBaseArrayPointer == 5) {//sampling rate of 20ns for all of these so adjust values in software
            intPreTrigDepth = (TriggerPositionSlider.getValue() / 10);
        } else if (intTimeBaseArrayPointer == 6) {
            intPreTrigDepth = (TriggerPositionSlider.getValue() / 5);
        } else if (intTimeBaseArrayPointer == 7) {
            intPreTrigDepth = (TriggerPositionSlider.getValue() / 2);
        } else {
            intPreTrigDepth = (TriggerPositionSlider.getValue());//set pretrigger 0 to 0.99
        }


        PreTrigDepth = ((intPreTrigDepth) / 3000.0000F);//TriggerPositionSlider.getMaximum() returns int 3000; we need to pass float to change pretrigger
        //PreTrigDepth = 0.10F;
        if (PreTrigDepth >= 1) {
            PreTrigDepth = 0.99F;
        } else if (PreTrigDepth < 0) {
            PreTrigDepth = 0;
        }

        if (JavaRunning && !t.SingleTrigger) {
            return;
        }

        // if number positive add that many zeros at the beginning
        //if number negative lose that many points from the beginning

        int i;

        try {
            if (trigPosClone_loaded[1] == false) {
                trigPosSeries_cloneCh1 = (XYSeries) seriesCh1.clone();
                trigPosClone_loaded[1] = true;
            }
            if (trigPosClone_loaded[2] == false) {
                trigPosSeries_cloneCh2 = (XYSeries) seriesCh2.clone();
                trigPosClone_loaded[2] = true;
            }
            if (trigPosClone_loaded[3] == false) {
                trigPosSeries_cloneCh3 = (XYSeries) seriesCh3.clone();
                trigPosClone_loaded[3] = true;
            }
            if (trigPosClone_loaded[4] == false) {
                trigPosSeries_cloneCh4 = (XYSeries) seriesCh4.clone();
                trigPosClone_loaded[4] = true;
            }

        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(USBscope50_Main.class.getName()).log(Level.SEVERE, null, ex);
        }


        int trigOffset = (intPreTrigDepth - ScopeReadAtSlider);
        if (trigOffset == 0) {

            if (!seriesCh1.isEmpty()) {
                seriesCh1.clear();
                for (i = 0; i < maxGraphPointCount - 2; i++) {
                    seriesCh1.add(i - 1499, trigPosSeries_cloneCh1.getY(i), false);
                }
                seriesCh1.add(i - 1499, trigPosSeries_cloneCh1.getY(i), true);
            }

            if (!seriesCh2.isEmpty()) {
                seriesCh2.clear();
                for (i = 0; i < maxGraphPointCount - 2; i++) {
                    seriesCh2.add(i - 1499, trigPosSeries_cloneCh2.getY(i), false);
                }
                seriesCh2.add(i - 1499, trigPosSeries_cloneCh2.getY(i), true);
            }

            if (!seriesCh3.isEmpty()) {
                seriesCh3.clear();
                for (i = 0; i < maxGraphPointCount - 2; i++) {
                    seriesCh3.add(i - 1499, trigPosSeries_cloneCh3.getY(i), false);
                }
                seriesCh3.add(i - 1499, trigPosSeries_cloneCh3.getY(i), true);
            }

            if (!seriesCh4.isEmpty()) {
                seriesCh4.clear();
                for (i = 0; i < maxGraphPointCount - 2; i++) {
                    seriesCh4.add(i - 1499, trigPosSeries_cloneCh4.getY(i), false);
                }
                seriesCh4.add(i - 1499, trigPosSeries_cloneCh4.getY(i), true);
            }

        } else if (intPreTrigDepth > ScopeReadAtSlider) {

            if (!seriesCh1.isEmpty()) {
                seriesCh1.clear();
                for (i = 0; i < trigOffset; i++) {
                    seriesCh1.add(i - 1499, null, false);
                }
                for (i = trigOffset; i < maxGraphPointCount - 2; i++) {
                    seriesCh1.add(i - 1499, trigPosSeries_cloneCh1.getY(i - trigOffset), false);
                }
                seriesCh1.add(i - 1499, trigPosSeries_cloneCh1.getY(i - trigOffset), true);
            }

            if (!seriesCh2.isEmpty()) {
                seriesCh2.clear();
                for (i = 0; i < trigOffset; i++) {
                    seriesCh2.add(i - 1499, null, false);
                }
                for (i = trigOffset; i < maxGraphPointCount - 2; i++) {
                    seriesCh2.add(i - 1499, trigPosSeries_cloneCh2.getY(i - trigOffset), false);
                }
                seriesCh2.add(i - 1499, trigPosSeries_cloneCh2.getY(i - trigOffset), true);
            }

            if (!seriesCh3.isEmpty()) {
                seriesCh3.clear();
                for (i = 0; i < trigOffset; i++) {
                    seriesCh3.add(i - 1499, null, false);
                }
                for (i = trigOffset; i < maxGraphPointCount - 2; i++) {
                    seriesCh3.add(i - 1499, trigPosSeries_cloneCh3.getY(i - trigOffset), false);
                }
                seriesCh3.add(i - 1499, trigPosSeries_cloneCh3.getY(i - trigOffset), true);
            }

            if (!seriesCh4.isEmpty()) {
                seriesCh4.clear();
                for (i = 0; i < trigOffset; i++) {
                    seriesCh4.add(i - 1499, null, false);
                }
                for (i = trigOffset; i < maxGraphPointCount - 2; i++) {
                    seriesCh4.add(i - 1499, trigPosSeries_cloneCh4.getY(i - trigOffset), false);
                }
                seriesCh4.add(i - 1499, trigPosSeries_cloneCh4.getY(i - trigOffset), true);
            }


        } else {
            if (!seriesCh1.isEmpty()) {
                seriesCh1.clear();
                for (i = 0; i < maxGraphPointCount - Math.abs(trigOffset) - 2; i++) {//translate is negative
                    seriesCh1.add(i - 1499, trigPosSeries_cloneCh1.getY(i + Math.abs(trigOffset)), false);
                }
                seriesCh1.add(i - 1499, trigPosSeries_cloneCh1.getY(i + Math.abs(trigOffset)), true);
            }



            if (!seriesCh2.isEmpty()) {
                seriesCh2.clear();
                for (i = 0; i < maxGraphPointCount - Math.abs(trigOffset) - 2; i++) {//translate is negative
                    seriesCh2.add(i - 1499, trigPosSeries_cloneCh2.getY(i + Math.abs(trigOffset)), false);
                }
                seriesCh2.add(i - 1499, trigPosSeries_cloneCh2.getY(i + Math.abs(trigOffset)), true);
            }



            if (!seriesCh3.isEmpty()) {
                seriesCh3.clear();
                for (i = 0; i < maxGraphPointCount - Math.abs(trigOffset) - 2; i++) {//translate is negative
                    seriesCh3.add(i - 1499, trigPosSeries_cloneCh3.getY(i + Math.abs(trigOffset)), false);
                }
                seriesCh3.add(i - 1499, trigPosSeries_cloneCh3.getY(i + Math.abs(trigOffset)), true);
            }

            if (!seriesCh4.isEmpty()) {
                seriesCh4.clear();
                for (i = 0; i < maxGraphPointCount - Math.abs(trigOffset) - 2; i++) {//translate is negative
                    seriesCh4.add(i - 1499, trigPosSeries_cloneCh4.getY(i + Math.abs(trigOffset)), false);
                }
                seriesCh4.add(i - 1499, trigPosSeries_cloneCh4.getY(i + Math.abs(trigOffset)), true);
            }
        }

	}//GEN-LAST:event_TriggerPositionSliderStateChanged

    private void MnuAddHorizontalMarkerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnuAddHorizontalMarkerActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_MnuAddHorizontalMarkerActionPerformed

    private void ExitLicenseDialogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExitLicenseDialogActionPerformed
        DisplayLicenseDialog.setVisible(false);
	}//GEN-LAST:event_ExitLicenseDialogActionPerformed

    private void DisplayLicenseDialogComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_DisplayLicenseDialogComponentResized
        //    System.out.println(DisplayLicenseDialog.getWidth());
        //    System.out.println(DisplayLicenseDialog.getHeight());// TODO add your handling code here:
    }//GEN-LAST:event_DisplayLicenseDialogComponentResized

    private void MnuAboutKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_MnuAboutKeyPressed
    }//GEN-LAST:event_MnuAboutKeyPressed

    private void DisplayLicenseDialogKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DisplayLicenseDialogKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_DisplayLicenseDialogKeyPressed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        DisplayDemoTutorial();
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void ExitLicenseDialogKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ExitLicenseDialogKeyPressed
        if (evt.getKeyCode() == 27) {
            DisplayLicenseDialog.setVisible(false);
        }
    }//GEN-LAST:event_ExitLicenseDialogKeyPressed

    private void ScrollPaneGPLLicenseKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ScrollPaneGPLLicenseKeyPressed
        if (evt.getKeyCode() == 27) {
            DisplayLicenseDialog.setVisible(false);
        }
    }//GEN-LAST:event_ScrollPaneGPLLicenseKeyPressed

    private void txtGPLLicenseKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtGPLLicenseKeyPressed
        if (evt.getKeyCode() == 27) {
            DisplayLicenseDialog.setVisible(false);
        }
    }//GEN-LAST:event_txtGPLLicenseKeyPressed

    private void RadioButTrigOnChan2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RadioButTrigOnChan2ActionPerformed
        MakeThisTrigMaster(2);
    }//GEN-LAST:event_RadioButTrigOnChan2ActionPerformed

    private void RadioButTrigOnChan1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RadioButTrigOnChan1ActionPerformed
        MakeThisTrigMaster(1);
    }//GEN-LAST:event_RadioButTrigOnChan1ActionPerformed

    private void RadioButTrigOnChan3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RadioButTrigOnChan3ActionPerformed
        MakeThisTrigMaster(3);
    }//GEN-LAST:event_RadioButTrigOnChan3ActionPerformed

    private void RadioButTrigOnChan4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RadioButTrigOnChan4ActionPerformed
        MakeThisTrigMaster(4);
    }//GEN-LAST:event_RadioButTrigOnChan4ActionPerformed

    private void RightSplitPaneMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_RightSplitPaneMouseClicked
    }//GEN-LAST:event_RightSplitPaneMouseClicked

    private void COMPButtonCh2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_COMPButtonCh2ActionPerformed
        SetButtonState(2, COMPButtonCh2, COMPButtonCh2.isSelected());
        SetCOMP(2, COMPButtonCh2.isSelected());//channel,state
    }//GEN-LAST:event_COMPButtonCh2ActionPerformed

    private void OpACCouplingCh2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OpACCouplingCh2ActionPerformed
        boolean CouplingCtrlMod = (evt.getModifiers() == 18);//change all present channels to match this setting
        CouplingActionPerformed(2, 0, 0, CouplingCtrlMod);//2=channel2,Dc_option[2]=0,gnd_option[2]=0
    }//GEN-LAST:event_OpACCouplingCh2ActionPerformed

    private void OpDCCouplingCh2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OpDCCouplingCh2ActionPerformed
        boolean CouplingCtrlMod = (evt.getModifiers() == 18);//change all present channels to match this setting
        CouplingActionPerformed(2, 1, 0, CouplingCtrlMod);//2=channel2,Dc_option[2]=1,gnd_option[2]=0
    }//GEN-LAST:event_OpDCCouplingCh2ActionPerformed

    private void OpGNDCh2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OpGNDCh2ActionPerformed
        boolean CouplingCtrlMod = (evt.getModifiers() == 18);//change all present channels to match this setting
        CouplingActionPerformed(2, 0, 1, CouplingCtrlMod);//channe2,Dc_option[2]=0,gnd_option[2]=1
    }//GEN-LAST:event_OpGNDCh2ActionPerformed

    private void Opx1ProbeCh2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Opx1ProbeCh2ActionPerformed
        ProbeOptionActionPerformed(2, 1);
    }//GEN-LAST:event_Opx1ProbeCh2ActionPerformed

    private void Opx10ProbeCh2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Opx10ProbeCh2ActionPerformed
        ProbeOptionActionPerformed(2, 2);//channel, probe setting 1 for x1, 2 for x10
    }//GEN-LAST:event_Opx10ProbeCh2ActionPerformed

    private void RightArrowVDivCh2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RightArrowVDivCh2ActionPerformed
        boolean VDivCtrlMod = (evt.getModifiers() == 18);//change all present channels v/div setting to this one
        RightArrowVDivActionPerformed(2, VDivCtrlMod);//2 stands for channel2
    }//GEN-LAST:event_RightArrowVDivCh2ActionPerformed

    private void LeftArrowVDivCh2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LeftArrowVDivCh2ActionPerformed
        boolean VDivCtrlMod = (evt.getModifiers() == 18);//change all present channels v/div setting to this one
        LeftArrowVDivActionPerformed(2, VDivCtrlMod);//2 is for channel 2
    }//GEN-LAST:event_LeftArrowVDivCh2ActionPerformed

    private void LeftArrowSecDivCh2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LeftArrowSecDivCh2ActionPerformed
        LeftArrowSecDivActionPerformed();
    }//GEN-LAST:event_LeftArrowSecDivCh2ActionPerformed

    private void RightArrowSecDivCh2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RightArrowSecDivCh2ActionPerformed
        RightArrowSecDivActionPerformed();
    }//GEN-LAST:event_RightArrowSecDivCh2ActionPerformed

    private void OptionsCh2ComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_OptionsCh2ComponentShown
        // TODO add your handling code here:
    }//GEN-LAST:event_OptionsCh2ComponentShown

    private void COMPButtonCh3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_COMPButtonCh3ActionPerformed
        SetButtonState(3, COMPButtonCh3, COMPButtonCh3.isSelected());
        SetCOMP(3, COMPButtonCh3.isSelected());//channel,state
    }//GEN-LAST:event_COMPButtonCh3ActionPerformed

    private void OpACCouplingCh3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OpACCouplingCh3ActionPerformed
        boolean CouplingCtrlMod = (evt.getModifiers() == 18);//change all present channels to match this setting
        CouplingActionPerformed(3, 0, 0, CouplingCtrlMod);//3=channel3,Dc_option[3]=0,gnd_option[3]=0,ApplyToAll
    }//GEN-LAST:event_OpACCouplingCh3ActionPerformed

    private void OpDCCouplingCh3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OpDCCouplingCh3ActionPerformed
        boolean CouplingCtrlMod = (evt.getModifiers() == 18);//change all present channels to match this setting
        CouplingActionPerformed(3, 1, 0, CouplingCtrlMod);//3=channel3,Dc_option[3]=1,gnd_option[3]=0
    }//GEN-LAST:event_OpDCCouplingCh3ActionPerformed

    private void OpGNDCh3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OpGNDCh3ActionPerformed
        boolean CouplingCtrlMod = (evt.getModifiers() == 18);//change all present channels to match this setting
        CouplingActionPerformed(3, 0, 1, CouplingCtrlMod);//channe3,Dc_option[3]=0,gnd_option[3]=1
    }//GEN-LAST:event_OpGNDCh3ActionPerformed

    private void Opx1ProbeCh3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Opx1ProbeCh3ActionPerformed
        ProbeOptionActionPerformed(3, 1);//channel, probe setting
    }//GEN-LAST:event_Opx1ProbeCh3ActionPerformed

    private void Opx10ProbeCh3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Opx10ProbeCh3ActionPerformed
        ProbeOptionActionPerformed(3, 2);//channel, probe setting
    }//GEN-LAST:event_Opx10ProbeCh3ActionPerformed

    private void RightArrowVDivCh3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RightArrowVDivCh3ActionPerformed
        boolean VDivCtrlMod = (evt.getModifiers() == 18);//change all present channels v/div setting to this one
        RightArrowVDivActionPerformed(3, VDivCtrlMod);//3 stands for channel3
    }//GEN-LAST:event_RightArrowVDivCh3ActionPerformed

    private void LeftArrowVDivCh3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LeftArrowVDivCh3ActionPerformed
        boolean VDivCtrlMod = (evt.getModifiers() == 18);//change all present channels v/div setting to this one
        LeftArrowVDivActionPerformed(3, VDivCtrlMod);//3 is for channel 3
    }//GEN-LAST:event_LeftArrowVDivCh3ActionPerformed

    private void LeftArrowSecDivCh3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LeftArrowSecDivCh3ActionPerformed
        LeftArrowSecDivActionPerformed();
    }//GEN-LAST:event_LeftArrowSecDivCh3ActionPerformed

    private void RightArrowSecDivCh3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RightArrowSecDivCh3ActionPerformed
        RightArrowSecDivActionPerformed();
    }//GEN-LAST:event_RightArrowSecDivCh3ActionPerformed

    private void OptionsCh3ComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_OptionsCh3ComponentShown
        // TODO add your handling code here:
    }//GEN-LAST:event_OptionsCh3ComponentShown

    private void COMPButtonCh4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_COMPButtonCh4ActionPerformed
        SetButtonState(4, COMPButtonCh4, COMPButtonCh4.isSelected());
        SetCOMP(4, COMPButtonCh4.isSelected());//channel,state
    }//GEN-LAST:event_COMPButtonCh4ActionPerformed

    private void OpACCouplingCh4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OpACCouplingCh4ActionPerformed
        boolean CouplingCtrlMod = (evt.getModifiers() == 18);//change all present channels to match this setting
        CouplingActionPerformed(4, 0, 0, CouplingCtrlMod);//4=channel4,Dc_option[4]=0,gnd_option[4]=0
    }//GEN-LAST:event_OpACCouplingCh4ActionPerformed

    private void OpDCCouplingCh4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OpDCCouplingCh4ActionPerformed
        boolean CouplingCtrlMod = (evt.getModifiers() == 18);//change all present channels to match this setting
        CouplingActionPerformed(4, 1, 0, CouplingCtrlMod);//4=channel4,Dc_option[4]=1,gnd_option[4]=0
    }//GEN-LAST:event_OpDCCouplingCh4ActionPerformed

    private void OpGNDCh4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OpGNDCh4ActionPerformed
        boolean CouplingCtrlMod = (evt.getModifiers() == 18);//change all present channels to match this setting
        CouplingActionPerformed(4, 0, 1, CouplingCtrlMod);//channe4,Dc_option[4]=0,gnd_option[4]=1
    }//GEN-LAST:event_OpGNDCh4ActionPerformed

    private void Opx1ProbeCh4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Opx1ProbeCh4ActionPerformed
        ProbeOptionActionPerformed(4, 1);//channel, probe setting
    }//GEN-LAST:event_Opx1ProbeCh4ActionPerformed

    private void Opx10ProbeCh4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Opx10ProbeCh4ActionPerformed
        ProbeOptionActionPerformed(4, 2);//channel, probe setting 1 for x1, 2 for x10
    }//GEN-LAST:event_Opx10ProbeCh4ActionPerformed

    private void RightArrowVDivCh4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RightArrowVDivCh4ActionPerformed
        boolean VDivCtrlMod = (evt.getModifiers() == 18);//change all present channels v/div setting to this one
        RightArrowVDivActionPerformed(4, VDivCtrlMod);//4 stands for channel4
    }//GEN-LAST:event_RightArrowVDivCh4ActionPerformed

    private void LeftArrowVDivCh4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LeftArrowVDivCh4ActionPerformed
        boolean VDivCtrlMod = (evt.getModifiers() == 18);//change all present channels v/div setting to this one
        LeftArrowVDivActionPerformed(4, VDivCtrlMod);//4 is for channel 4
    }//GEN-LAST:event_LeftArrowVDivCh4ActionPerformed

    private void LeftArrowSecDivCh4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LeftArrowSecDivCh4ActionPerformed
        LeftArrowSecDivActionPerformed();
    }//GEN-LAST:event_LeftArrowSecDivCh4ActionPerformed

    private void RightArrowSecDivCh4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RightArrowSecDivCh4ActionPerformed
        RightArrowSecDivActionPerformed();
    }//GEN-LAST:event_RightArrowSecDivCh4ActionPerformed

    private void OptionsCh4ComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_OptionsCh4ComponentShown
        // TODO add your handling code here:
    }//GEN-LAST:event_OptionsCh4ComponentShown

    private void ONButtonCh1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ONButtonCh1ActionPerformed
        SetButtonState(1, ONButtonCh1, ONButtonCh1.isSelected());
        SetSeriesONState(1, ONButtonCh1.isSelected());
	}//GEN-LAST:event_ONButtonCh1ActionPerformed

    private void COMPButtonCh1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_COMPButtonCh1ActionPerformed
        SetButtonState(1, COMPButtonCh1, COMPButtonCh1.isSelected());
        SetCOMP(1, COMPButtonCh1.isSelected());//channel,state
	}//GEN-LAST:event_COMPButtonCh1ActionPerformed

    private void IDButtonCh1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_IDButtonCh1ActionPerformed
        SetButtonState(1, IDButtonCh1, true);
        t.USBscope50Drvr_SetLEDMode(1, 2);//channel 1 fast blinking
        //System.out.println("t.USBscope50Drvr_SetLEDMode");
        JOptionPane.showMessageDialog(this, IDMsg[1], " " + productID + " ID", JOptionPane.INFORMATION_MESSAGE);
        t.USBscope50Drvr_SetLEDMode(1, 1);//channel 1 slow/normal blinking
        //System.out.println("t.USBscope50Drvr_SetLEDMode");
        SetButtonState(1, IDButtonCh1, false);
	}//GEN-LAST:event_IDButtonCh1ActionPerformed

    private void INVButtonCh2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_INVButtonCh2ActionPerformed
        SetButtonState(2, INVButtonCh2, INVButtonCh2.isSelected());
        INV[2] = INVButtonCh2.isSelected() ? -1 : 1;
        PlotINVData(2, -1);
    }//GEN-LAST:event_INVButtonCh2ActionPerformed

    private void ONButtonCh2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ONButtonCh2ActionPerformed
        SetButtonState(2, ONButtonCh2, ONButtonCh2.isSelected());
        SetSeriesONState(2, ONButtonCh2.isSelected());
    }//GEN-LAST:event_ONButtonCh2ActionPerformed

    private void IDButtonCh2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_IDButtonCh2ActionPerformed
        SetButtonState(2, IDButtonCh2, true);
        t.USBscope50Drvr_SetLEDMode(2, 2);//channel 2 fast blinking
        JOptionPane.showMessageDialog(this, IDMsg[2], " " + productID + " ID", JOptionPane.INFORMATION_MESSAGE);
        t.USBscope50Drvr_SetLEDMode(2, 1);//channel 2 slow/normal blinking
        SetButtonState(2, IDButtonCh2, false);
    }//GEN-LAST:event_IDButtonCh2ActionPerformed

    private void INVButtonCh3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_INVButtonCh3ActionPerformed
        SetButtonState(3, INVButtonCh3, INVButtonCh3.isSelected());
        INV[3] = INVButtonCh3.isSelected() ? -1 : 1;
        PlotINVData(3, -1);
    }//GEN-LAST:event_INVButtonCh3ActionPerformed

    private void ONButtonCh3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ONButtonCh3ActionPerformed
        SetButtonState(3, ONButtonCh3, ONButtonCh3.isSelected());
        SetSeriesONState(3, ONButtonCh3.isSelected());
    }//GEN-LAST:event_ONButtonCh3ActionPerformed

    private void IDButtonCh3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_IDButtonCh3ActionPerformed
        SetButtonState(3, IDButtonCh3, true);
        t.USBscope50Drvr_SetLEDMode(3, 2);//channel 3 fast blinking
        JOptionPane.showMessageDialog(this, IDMsg[3], " " + productID + " ID", JOptionPane.INFORMATION_MESSAGE);
        t.USBscope50Drvr_SetLEDMode(3, 1);//channel 3 slow/normal blinking
        SetButtonState(3, IDButtonCh3, false);
    }//GEN-LAST:event_IDButtonCh3ActionPerformed

    private void INVButtonCh4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_INVButtonCh4ActionPerformed
        SetButtonState(4, INVButtonCh4, INVButtonCh4.isSelected());
        INV[4] = INVButtonCh4.isSelected() ? -1 : 1;
        PlotINVData(4, -1);
    }//GEN-LAST:event_INVButtonCh4ActionPerformed

    private void ONButtonCh4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ONButtonCh4ActionPerformed
        SetButtonState(4, ONButtonCh4, ONButtonCh4.isSelected());
        SetSeriesONState(4, ONButtonCh4.isSelected());
    }//GEN-LAST:event_ONButtonCh4ActionPerformed

    private void IDButtonCh4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_IDButtonCh4ActionPerformed
        SetButtonState(4, IDButtonCh4, true);
        t.USBscope50Drvr_SetLEDMode(4, 2);//channel 4 fast blinking
        JOptionPane.showMessageDialog(this, IDMsg[4], " " + productID + " ID", JOptionPane.INFORMATION_MESSAGE);
        t.USBscope50Drvr_SetLEDMode(4, 1);//channel 4 slow/normal blinking
        SetButtonState(4, IDButtonCh4, false);
    }//GEN-LAST:event_IDButtonCh4ActionPerformed

    private void LeftArrowVDivCh1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_LeftArrowVDivCh1KeyPressed
    }//GEN-LAST:event_LeftArrowVDivCh1KeyPressed

    private void MnuCh1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnuCh1ActionPerformed
        boolean selected = MnuCh1.isSelected();
        boolean Ch1TabTitles = CheckTabTitles("Ch 1");
        int TabIndexCh1 = CheckTabIndex("Ch 1");

        if (selected) {
            if (Ch1TabTitles || (TabIndexCh1 >= 0)) {
                //Ooops! something has gone wrong. this should not happen.
                //do nothing and hope it will sort itself out :)
            } else {
                RightSplitPane.addTab("Ch 1   ", ScrollOptionsCh1);
                OrganizeTabs("Ch 1");
            }
        } else {
            if (!Ch1TabTitles || TabIndexCh1 < 0) {
                //Ooops! something has gone wrong. this should not happen.
                //do nothing and hope it will sort itself out :)
            } else {
                RightSplitPane.remove(TabIndexCh1);
            }
        }
        if (SplitPane.getDividerLocation() + SplitPane.getDividerSize() == SplitPane.getWidth()) {
            SplitPane.setDividerLocation(-1); //negative value is a default JSplitPane divider setting
        }
    //chartPlot.clearRangeMarkers();
    //updateMarkers(false, true);
    }//GEN-LAST:event_MnuCh1ActionPerformed

    private void MnuCh2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnuCh2ActionPerformed
        boolean selected = MnuCh2.isSelected();
        boolean Ch2TabTitles = CheckTabTitles("Ch 2");
        int TabIndexCh2 = CheckTabIndex("Ch 2");

        if (selected) {
            if (Ch2TabTitles || (TabIndexCh2 >= 0)) {
                //Ooops! something has gone wrong. this should not happen.
                //do nothing and hope it will sort itself out :)
            } else {
                RightSplitPane.addTab("Ch 2   ", ScrollOptionsCh2);
                OrganizeTabs("Ch 2");
            }
        } else {
            if (!Ch2TabTitles || TabIndexCh2 < 0) {
                //Ooops! something has gone wrong. this should not happen.
                //do nothing and hope it will sort itself out :)
            } else {
                RightSplitPane.remove(TabIndexCh2);
            }
        }
        if (SplitPane.getDividerLocation() + SplitPane.getDividerSize() == SplitPane.getWidth()) {
            SplitPane.setDividerLocation(-1); //negative value is a default JSplitPane divider setting
        }
    //chartPlot.clearRangeMarkers();
    //updateMarkers(true, false);
    }//GEN-LAST:event_MnuCh2ActionPerformed

    private void MnuCh3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnuCh3ActionPerformed
        boolean selected = MnuCh3.isSelected();
        boolean Ch3TabTitles = CheckTabTitles("Ch 3");
        int TabIndexCh3 = CheckTabIndex("Ch 3");

        if (selected) {
            if (Ch3TabTitles || (TabIndexCh3 >= 0)) {
                //Ooops! something has gone wrong. this should not happen.
                //do nothing and hope it will sort itself out :)
            } else {
                RightSplitPane.addTab("Ch 3   ", ScrollOptionsCh3);
                OrganizeTabs("Ch 3");
            }
        } else {
            if (!Ch3TabTitles || TabIndexCh3 < 0) {
                //Ooops! something has gone wrong. this should not happen.
                //do nothing and hope it will sort itself out :)
            } else {
                RightSplitPane.remove(TabIndexCh3);
            }
        }
        if (SplitPane.getDividerLocation() + SplitPane.getDividerSize() == SplitPane.getWidth()) {
            SplitPane.setDividerLocation(-1); //negative value is a default JSplitPane divider setting
        }
    //chartPlot.clearRangeMarkers();
    //updateMarkers(true, false);
    }//GEN-LAST:event_MnuCh3ActionPerformed

    private void MnuCh4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnuCh4ActionPerformed
        boolean selected = MnuCh4.isSelected();
        boolean Ch4TabTitles = CheckTabTitles("Ch 4");
        int TabIndexCh4 = CheckTabIndex("Ch 4");

        if (selected) {
            if (Ch4TabTitles || (TabIndexCh4 >= 0)) {
                //Ooops! something has gone wrong. this should not happen.
                //do nothing and hope it will sort itself out :)
            } else {
                RightSplitPane.addTab("Ch 4   ", ScrollOptionsCh4);
                OrganizeTabs("Ch 4");
            }
        } else {
            if (!Ch4TabTitles || TabIndexCh4 < 0) {
                //Ooops! something has gone wrong. this should not happen.
                //do nothing and hope it will sort itself out :)
            } else {
                RightSplitPane.remove(TabIndexCh4);
            }
        }
        if (SplitPane.getDividerLocation() + SplitPane.getDividerSize() == SplitPane.getWidth()) {
            SplitPane.setDividerLocation(-1); //negative value is a default JSplitPane divider setting
        }
    //chartPlot.clearRangeMarkers();
    //updateMarkers(true, false);
    }//GEN-LAST:event_MnuCh4ActionPerformed

    private void RightSplitPaneComponentRemoved(java.awt.event.ContainerEvent evt) {//GEN-FIRST:event_RightSplitPaneComponentRemoved
        //this will ensure that check box channel menu options are ticked accordingly
        MnuCh1.setSelected(false);
        MnuCh2.setSelected(false);
        MnuCh3.setSelected(false);
        MnuCh4.setSelected(false);
        SetSeriesONState(1, false);
        SetSeriesONState(2, false);
        SetSeriesONState(3, false);
        SetSeriesONState(4, false);
        lbVDivCh1.setVisible(false);
        lbVDivCh2.setVisible(false);
        lbVDivCh3.setVisible(false);
        lbVDivCh4.setVisible(false);

        if (!areSeriesVisibile()) {
            MnuStopClicked();
        }
        OrganizeTabs("");//"" = focus set to the tab index 0
    }//GEN-LAST:event_RightSplitPaneComponentRemoved

private void TriggerModeLessThanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TriggerModeLessThanActionPerformed

    TriggerModeLessThanSelected();
    SetTriggerMode(intTriggerModeNormal);
    SetTrigType(intLessThan);
    triggermode = 1;
}//GEN-LAST:event_TriggerModeLessThanActionPerformed

private void TriggerModeGreaterThanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TriggerModeGreaterThanActionPerformed
    TriggerModeGreaterThanSelected();
    SetTriggerMode(intTriggerModeNormal);
    SetTrigType(intGreaterThan);
    triggermode = 1;
}//GEN-LAST:event_TriggerModeGreaterThanActionPerformed

private void MnuChartOptionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnuChartOptionsActionPerformed

    ChartOptionsDialog.setTitle("Chart Options");
    ChartOptionsDialog.setSize(464, 250);//(464, 314);
    ChartOptionsDialog.setResizable(true);
    ChartOptionsDialog.setLocationRelativeTo(null);

    newChartBackgroundColor = ChartBackgroundColor;
    newDomainGridColor = DomainGridColor;
    newRangeGridColor = RangeGridColor;

    newDomainStroke = DomainStroke;
    newRangeStroke = RangeStroke;

    FunctionSpinner.setValue(FunctionStroke);
    RangeSpinner.setValue(RangeStroke);
    DomainSpinner.setValue(DomainStroke);
    ChartOptionsDialog.setVisible(true);// TODO add your handling code here:

    }//GEN-LAST:event_MnuChartOptionsActionPerformed

private void BackgroundColorPanelMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BackgroundColorPanelMouseMoved
// TODO add your handling code here:
//System.out.println(ChartOptionsDialog.getWidth() + " " + ChartOptionsDialog.getHeight());
}//GEN-LAST:event_BackgroundColorPanelMouseMoved

private void buttChartDefaultValuesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttChartDefaultValuesActionPerformed
    SetChartDefaultValues();
}//GEN-LAST:event_buttChartDefaultValuesActionPerformed

private void buttChartOptionsCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttChartOptionsCancelActionPerformed
    ChartOptionsCancelActionPerformed();
}//GEN-LAST:event_buttChartOptionsCancelActionPerformed

private void buttChartOptionsOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttChartOptionsOKActionPerformed
    ChartOptionsOKActionPerformed();
}//GEN-LAST:event_buttChartOptionsOKActionPerformed

private void buttChartDefaultValues1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttChartDefaultValues1ActionPerformed
    SetChartDefaultValues();
}//GEN-LAST:event_buttChartDefaultValues1ActionPerformed

private void buttChartOptionsCancel1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttChartOptionsCancel1ActionPerformed
    ChartOptionsCancelActionPerformed();
}//GEN-LAST:event_buttChartOptionsCancel1ActionPerformed

private void buttChartOptionsOK1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttChartOptionsOK1ActionPerformed
    ChartOptionsOKActionPerformed();
}//GEN-LAST:event_buttChartOptionsOK1ActionPerformed

private void GridLinesColorPanelMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_GridLinesColorPanelMouseMoved
// TODO add your handling code here:
}//GEN-LAST:event_GridLinesColorPanelMouseMoved

private void buttChartDefaultValues2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttChartDefaultValues2ActionPerformed
    SetChartDefaultValues();
}//GEN-LAST:event_buttChartDefaultValues2ActionPerformed

private void buttChartOptionsCancel2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttChartOptionsCancel2ActionPerformed
    ChartOptionsCancelActionPerformed();
}//GEN-LAST:event_buttChartOptionsCancel2ActionPerformed

private void buttChartOptionsOK2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttChartOptionsOK2ActionPerformed
    ChartOptionsOKActionPerformed();
}//GEN-LAST:event_buttChartOptionsOK2ActionPerformed

private void RangeSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_RangeSpinnerStateChanged
    RangeSpinnerActionPerformed();
}//GEN-LAST:event_RangeSpinnerStateChanged

private void DomainSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_DomainSpinnerStateChanged
    DomainSpinnerActionPerformed();
}//GEN-LAST:event_DomainSpinnerStateChanged

private void ChartOptionsDialogWindowDeactivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_ChartOptionsDialogWindowDeactivated
    ChartOptionsCancelActionPerformed();//when dialog closed by clicking on x don't accept any changes. Only OK will accept changes
}//GEN-LAST:event_ChartOptionsDialogWindowDeactivated

private void FunctionSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_FunctionSpinnerStateChanged
    FunctionSpinnerActionPerformed();
}//GEN-LAST:event_FunctionSpinnerStateChanged

private void RightArrowVDivCh1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_RightArrowVDivCh1FocusLost
// TODO add your handling code here:
    //if (LeftArrowVDivCh1.isFocusOwner()) {
    //still changing V/div
    //}// else {
    //ClearBackgroundlbVDivs();
    //}
}//GEN-LAST:event_RightArrowVDivCh1FocusLost

private void LeftArrowVDivCh1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_LeftArrowVDivCh1FocusLost

    //if (RightArrowVDivCh1.isFocusOwner()) {
    //still changing V/div
    //}// else {
    //ClearBackgroundlbVDivs();
    //}
}//GEN-LAST:event_LeftArrowVDivCh1FocusLost

private void RightArrowVDivCh2FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_RightArrowVDivCh2FocusLost
// TODO add your handling code here:
    //if (LeftArrowVDivCh2.isFocusOwner()) {
    //still changing V/div
    //} else {
    //ClearBackgroundlbVDivs();
    //}
}//GEN-LAST:event_RightArrowVDivCh2FocusLost

private void LeftArrowVDivCh2FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_LeftArrowVDivCh2FocusLost
// TODO add your handling code here:
    //if (RightArrowVDivCh2.isFocusOwner()) {
    //still changing V/div
    //} else {
    //ClearBackgroundlbVDivs();
    //}
}//GEN-LAST:event_LeftArrowVDivCh2FocusLost

private void RightArrowVDivCh3FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_RightArrowVDivCh3FocusLost
// TODO add your handling code here:
    // if (LeftArrowVDivCh3.isFocusOwner()) {
    //still changing V/div
    // } else {
    //ClearBackgroundlbVDivs();
    // }
}//GEN-LAST:event_RightArrowVDivCh3FocusLost

private void LeftArrowVDivCh3FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_LeftArrowVDivCh3FocusLost
// TODO add your handling code here:
    // if (RightArrowVDivCh3.isFocusOwner()) {
    //still changing V/div
    // } else {
    //ClearBackgroundlbVDivs();
    // }
}//GEN-LAST:event_LeftArrowVDivCh3FocusLost

private void RightArrowVDivCh4FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_RightArrowVDivCh4FocusLost
// TODO add your handling code here:
    // if (LeftArrowVDivCh4.isFocusOwner()) {
    //still changing V/div
    // } else {
    //ClearBackgroundlbVDivs();
    // }
}//GEN-LAST:event_RightArrowVDivCh4FocusLost

private void LeftArrowVDivCh4FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_LeftArrowVDivCh4FocusLost
// TODO add your handling code here:
    // if (RightArrowVDivCh4.isFocusOwner()) {
    //still changing V/div
    // } else {
    //ClearBackgroundlbVDivs();
    // }
}//GEN-LAST:event_LeftArrowVDivCh4FocusLost

private void buttChColorDefaultValues1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttChColorDefaultValues1ActionPerformed
    SetChColorDefaultValues();
}//GEN-LAST:event_buttChColorDefaultValues1ActionPerformed

private void buttChColorCancel1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttChColorCancel1ActionPerformed
    ChColorCancelActionPerformed();
}//GEN-LAST:event_buttChColorCancel1ActionPerformed

private void buttChColorOK1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttChColorOK1ActionPerformed
    ChColorOKActionPerformed();
}//GEN-LAST:event_buttChColorOK1ActionPerformed

private void buttChColorDefaultValues2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttChColorDefaultValues2ActionPerformed
    SetChColorDefaultValues();
}//GEN-LAST:event_buttChColorDefaultValues2ActionPerformed

private void buttChColorCancel2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttChColorCancel2ActionPerformed
    ChColorCancelActionPerformed();
}//GEN-LAST:event_buttChColorCancel2ActionPerformed

private void buttChColorOK2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttChColorOK2ActionPerformed
    ChColorOKActionPerformed();
}//GEN-LAST:event_buttChColorOK2ActionPerformed

private void buttChColorDefaultValues3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttChColorDefaultValues3ActionPerformed
    SetChColorDefaultValues();
}//GEN-LAST:event_buttChColorDefaultValues3ActionPerformed

private void buttChColorCancel3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttChColorCancel3ActionPerformed
    ChColorCancelActionPerformed();
}//GEN-LAST:event_buttChColorCancel3ActionPerformed

private void buttChColorOK3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttChColorOK3ActionPerformed
    ChColorOKActionPerformed();
}//GEN-LAST:event_buttChColorOK3ActionPerformed

private void buttChColorDefaultValues4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttChColorDefaultValues4ActionPerformed
    SetChColorDefaultValues();
}//GEN-LAST:event_buttChColorDefaultValues4ActionPerformed

private void buttChColorCancel4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttChColorCancel4ActionPerformed
    ChColorCancelActionPerformed();
}//GEN-LAST:event_buttChColorCancel4ActionPerformed

private void buttChColorOK4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttChColorOK4ActionPerformed
    ChColorOKActionPerformed();
}//GEN-LAST:event_buttChColorOK4ActionPerformed

private void MnuChannelSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnuChannelSettingsActionPerformed

    ChannelColorsDialog.setTitle("Channel Color Options");//GEN-LAST:event_MnuChannelSettingsActionPerformed
        ChannelColorsDialog.setSize(464, 250);//(464, 314);
        ChannelColorsDialog.setResizable(true);
        ChannelColorsDialog.setLocationRelativeTo(null);


        ChannelColorsDialog.setVisible(true);//

    }
private void ChannelColorsDialogWindowDeactivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_ChannelColorsDialogWindowDeactivated
    ChColorCancelActionPerformed();
}//GEN-LAST:event_ChannelColorsDialogWindowDeactivated

private void buttonCancelZoomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelZoomActionPerformed

    MnuZoomCancelClicked();
}//GEN-LAST:event_buttonCancelZoomActionPerformed

private void MnuUGActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnuUGActionPerformed

    String PathToUserGuide = System.getenv("ProgramFiles") + "/" + companyID + "/" + productID + " Java Software/USBscope50ug.pdf";

    //display pdf user guide

    try {
        Desktop desktop = Desktop.getDesktop();
        File pdfFile = new File(PathToUserGuide);
        //System.out.println("pdf found at " + getClass().getResource("/usbscope50software/Images/USBscope50ug.pdf"));
        //System.out.println(pdfFile.getAbsolutePath());
        if (pdfFile.exists() == false) {
            String Message;
            Message = "\nError encountered while trying to open USBscope50ug.pdf.\n\n" +
                    "File USBscope50ug.pdf not found.  \n\n";
            JOptionPane.showMessageDialog(this, Message, "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            desktop.open(pdfFile);
        }
    } catch (IOException ex) {
        Logger.getLogger(USBscope50_Main.class.getName()).log(Level.SEVERE, null, ex);
    }


}//GEN-LAST:event_MnuUGActionPerformed

private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed

    try {
        Runtime.getRuntime().exec("C://Program Files//Elan Digital Systems//USBscope50 Java Software//updater.exe /checknow");
    } catch (IOException ex) {
        ex.printStackTrace();
    }
}//GEN-LAST:event_jMenuItem2ActionPerformed

private void MnuGraphandOpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnuGraphandOpActionPerformed
    SplitPane.setDividerLocation((this.getWidth() / 3) * 2);
}//GEN-LAST:event_MnuGraphandOpActionPerformed

private void FFTToolBarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FFTToolBarButtonActionPerformed
    FFTFunctionSelected(true);
}//GEN-LAST:event_FFTToolBarButtonActionPerformed

private void FFTButtonCh1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FFTButtonCh1ActionPerformed
    SetButtonState(1, FFTButtonCh1, FFTButtonCh1.isSelected());
    SetFFTSubPlot(1, FFTButtonCh1.isSelected());
    SetSeriesFFTONState(1, FFTButtonCh1.isSelected());
}//GEN-LAST:event_FFTButtonCh1ActionPerformed

private void FFTButtonCh2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FFTButtonCh2ActionPerformed
    SetButtonState(2, FFTButtonCh2, FFTButtonCh2.isSelected());
    SetFFTSubPlot(2, FFTButtonCh2.isSelected());
    SetSeriesFFTONState(2, FFTButtonCh2.isSelected());
}//GEN-LAST:event_FFTButtonCh2ActionPerformed

private void FFTButtonCh3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FFTButtonCh3ActionPerformed
    SetButtonState(3, FFTButtonCh3, FFTButtonCh3.isSelected());
    SetFFTSubPlot(3, FFTButtonCh3.isSelected());
    SetSeriesFFTONState(3, FFTButtonCh3.isSelected());
}//GEN-LAST:event_FFTButtonCh3ActionPerformed

private void FFTButtonCh4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FFTButtonCh4ActionPerformed
    SetButtonState(4, FFTButtonCh4, FFTButtonCh4.isSelected());
    SetFFTSubPlot(4, FFTButtonCh4.isSelected());
    SetSeriesFFTONState(4, FFTButtonCh4.isSelected());

}//GEN-LAST:event_FFTButtonCh4ActionPerformed

private void RemoveFFTToolBarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RemoveFFTToolBarButtonActionPerformed
    FFTFunctionSelected(false);
}//GEN-LAST:event_RemoveFFTToolBarButtonActionPerformed

private void MnuFFTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnuFFTActionPerformed
    FFTFunctionSelected(true);
}//GEN-LAST:event_MnuFFTActionPerformed

private void MnuRemoveFFTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnuRemoveFFTActionPerformed
    FFTFunctionSelected(false);
}//GEN-LAST:event_MnuRemoveFFTActionPerformed

private void MnuFFTOnlyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnuFFTOnlyActionPerformed
    if (!(plot.getSubplots().contains(FFTplot))) {
        FFTFunctionSelected(true);
    }
    if (plot.getSubplots().contains(chartPlot)) {
        plot.remove(chartPlot);
    }
}//GEN-LAST:event_MnuFFTOnlyActionPerformed

private void MnudBVFFTPlotActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnudBVFFTPlotActionPerformed
    MnudBVFFTPlotActionPerformed();
}//GEN-LAST:event_MnudBVFFTPlotActionPerformed

private void MnuLinearFFTPlotActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnuLinearFFTPlotActionPerformed
    MnuLinearFFTPlotActionPerformed();
}//GEN-LAST:event_MnuLinearFFTPlotActionPerformed

private void MnuRectangularActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnuRectangularActionPerformed
    lngFFTWindowType = 0;
    UpdateFFTgraph(lngFFTWindowType);
}//GEN-LAST:event_MnuRectangularActionPerformed

private void MnuHanningActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnuHanningActionPerformed
    lngFFTWindowType = 1;
    UpdateFFTgraph(lngFFTWindowType);
}//GEN-LAST:event_MnuHanningActionPerformed

private void MnuHammingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnuHammingActionPerformed
    lngFFTWindowType = 2;
    UpdateFFTgraph(lngFFTWindowType);
}//GEN-LAST:event_MnuHammingActionPerformed

private void MnuTriangularActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnuTriangularActionPerformed
    lngFFTWindowType = 3;
    UpdateFFTgraph(lngFFTWindowType);
}//GEN-LAST:event_MnuTriangularActionPerformed

private void MnuWelchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnuWelchActionPerformed
    lngFFTWindowType = 4;
    UpdateFFTgraph(lngFFTWindowType);
}//GEN-LAST:event_MnuWelchActionPerformed

private void MnuShowYAxisLabelsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnuShowYAxisLabelsActionPerformed
    MnuShowYAxisLabelsActionPerformed();
//here i take the vlaues and generate xy coordintates from it
}//GEN-LAST:event_MnuShowYAxisLabelsActionPerformed

private void lbFDivAllChsPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_lbFDivAllChsPropertyChange
    //validate();//appears I don't need that!
}//GEN-LAST:event_lbFDivAllChsPropertyChange

private void ClearmarkersToolBarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ClearmarkersToolBarButtonActionPerformed
    ClearRangeMarkers();
    ClearDomainMarkers();
}//GEN-LAST:event_ClearmarkersToolBarButtonActionPerformed

private void lbVDivCh1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbVDivCh1MouseClicked
    int TabIndex = CheckTabIndex("Ch 1");
    RightSplitPane.setSelectedIndex(TabIndex);
}//GEN-LAST:event_lbVDivCh1MouseClicked

private void lbVDivCh2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbVDivCh2MouseClicked
    int TabIndex = CheckTabIndex("Ch 2");
    RightSplitPane.setSelectedIndex(TabIndex);
}//GEN-LAST:event_lbVDivCh2MouseClicked

private void lbVDivCh3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbVDivCh3MouseClicked
    int TabIndex = CheckTabIndex("Ch 3");
    RightSplitPane.setSelectedIndex(TabIndex);
}//GEN-LAST:event_lbVDivCh3MouseClicked

private void lbVDivCh4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbVDivCh4MouseClicked
    int TabIndex = CheckTabIndex("Ch 4");
    RightSplitPane.setSelectedIndex(TabIndex);
}//GEN-LAST:event_lbVDivCh4MouseClicked

private void PanelforChartComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_PanelforChartComponentResized
    try {
        //SplitPane.setDividerLocation(DividerLocation);
        //this.setSize(FormSizeX, FormSizeY);
        //SplitPane.setDividerLocation(DividerLocation);
        chartPlot.clearRangeMarkers();
        updateMarkers(true, true);

        if (ThresholdMarkerOn) {
            SliderThreshold.setValue(SliderThreshold.getValue() + 1);
            SliderThreshold.setValue(SliderThreshold.getValue() - 1);
        }
    } catch (Exception ee) {
    }
}//GEN-LAST:event_PanelforChartComponentResized

private void NullButtonCh3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NullButtonCh3ActionPerformed
    SetButtonState(3, NullButtonCh3, true);
    PerformNull(3);
// SetButtonState(3, NullButtonCh3, false);
}//GEN-LAST:event_NullButtonCh3ActionPerformed

private void NullButtonCh1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NullButtonCh1ActionPerformed
    SetButtonState(1, NullButtonCh1, true);
    PerformNull(1);
//SetButtonState(1, NullButtonCh1, false);
}//GEN-LAST:event_NullButtonCh1ActionPerformed

private void NullButtonCh2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NullButtonCh2ActionPerformed
    SetButtonState(2, NullButtonCh2, true);
    PerformNull(2);
//SetButtonState(2, NullButtonCh2, false);
}//GEN-LAST:event_NullButtonCh2ActionPerformed

    private void PerformNull(int channelID) {
        Object[] options = {"OK", "Cancel"};
        int response;

        intNullTimerCommandID = 0;
        intNullChannelID = channelID;
        if (companyID.equals("PRIST")) {
            response = JOptionPane.showOptionDialog(this, NullMsg, productID,
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, AKIPLogo_small, options, options[0]);
        } else {
            response = JOptionPane.showOptionDialog(this, NullMsg, productID,
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, ElanLogo_Small, options, options[0]);
        }
        if (response > 0) {//cancel
            //clear buttons here
            SetButtonState(1, NullButtonCh1, false);
            SetButtonState(2, NullButtonCh2, false);
            SetButtonState(3, NullButtonCh3, false);
            SetButtonState(4, NullButtonCh4, false);
            intNullChannelID = 0;

        } else {
            Nulltriggermode = triggermode;
            NullJavaRunning = JavaRunning;
            NullcurrentTrigType = currentTrigType;

            TriggerModeFreeSelected();
            SetTriggerMode(intTriggerModeFree);
            triggermode = 0;
            LoadDataArray.OffsetNull[channelID][0] = 0;
            LoadDataArray.OffsetNull[channelID][1] = 0;
            LoadDataArray.OffsetNull[channelID][2] = 0;


            if (JavaRunning) {
                MnuSingleClicked();
            }

            NullTimer.start();
        //JOptionPane.showMessageDialog(this, "Null Function for Channel " + channelID + " completed successfully.", " " + productID, JOptionPane.INFORMATION_MESSAGE);
        }
    }

private void NullButtonCh4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NullButtonCh4ActionPerformed
    SetButtonState(4, NullButtonCh4, true);
    PerformNull(4);
//SetButtonState(4, NullButtonCh4, false);
}//GEN-LAST:event_NullButtonCh4ActionPerformed

private void ClearOffsetCh1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ClearOffsetCh1ActionPerformed
    //boolean CtrlMod = (evt.getModifiers() == 18);

    OffsetSliderCh1.setValue(0);
/*if (CtrlMod) {
OffsetSliderCh1.setValue(0);
OffsetSliderCh2.setValue(0);
OffsetSliderCh3.setValue(0);
OffsetSliderCh4.setValue(0);
}*/

}//GEN-LAST:event_ClearOffsetCh1ActionPerformed

private void ClearOffsetCh2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ClearOffsetCh2ActionPerformed
    //boolean CtrlMod = (evt.getModifiers() == 18);
    OffsetSliderCh2.setValue(0);
/*if (CtrlMod) {
OffsetSliderCh1.setValue(0);
OffsetSliderCh2.setValue(0);
OffsetSliderCh3.setValue(0);
OffsetSliderCh4.setValue(0);
}*/
}//GEN-LAST:event_ClearOffsetCh2ActionPerformed

private void ClearOffsetCh3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ClearOffsetCh3ActionPerformed
    //boolean CtrlMod = (evt.getModifiers() == 18);
    OffsetSliderCh3.setValue(0);
/*if (CtrlMod) {
OffsetSliderCh1.setValue(0);
OffsetSliderCh2.setValue(0);
OffsetSliderCh3.setValue(0);
OffsetSliderCh4.setValue(0);
}*/
}//GEN-LAST:event_ClearOffsetCh3ActionPerformed

private void ClearOffsetCh4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ClearOffsetCh4ActionPerformed
    //boolean CtrlMod = (evt.getModifiers() == 18);
    OffsetSliderCh4.setValue(0);
/*if (CtrlMod) {
OffsetSliderCh1.setValue(0);
OffsetSliderCh2.setValue(0);
OffsetSliderCh3.setValue(0);
OffsetSliderCh4.setValue(0);
}*/
}//GEN-LAST:event_ClearOffsetCh4ActionPerformed

private void OffsetSliderCh2StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_OffsetSliderCh2StateChanged

    if (demoMode && OffsetSliderCh2.getValueIsAdjusting() && JavaRunning) {
        return;
    }

    offsetCh = 2;
    //OffsetSelectActionperformed(offsetCh, true);//this highlights the function
    double SliderOffsetValue = OffsetSliderCh2.getValue() - functionOffset[offsetCh];//new-old

    functionOffset[offsetCh] = OffsetSliderCh2.getValue();
    //SliderOffsetValueLb.setText((SliderOffset.getValue() / 100.00) + " %");

    if (demoMode) {
        PlotOffsetDataDemo(offsetCh, SliderOffsetValue);

        USBscope50_Main.seriesCh1.clear();
        USBscope50_Main.seriesCh2.clear();
        USBscope50_Main.seriesCh3.clear();
        USBscope50_Main.seriesCh4.clear();
        if (!JavaRunning) {
            t.DisplayDataDemo(false);
        }
    } else if (!JavaRunning && (SliderOffsetValue != 0)) {
        PlotOffsetData(offsetCh, SliderOffsetValue);
    }//else{//if (JavaRunning && (OffsetSliderCh1.getValue() != 0)){ not sure why this !=0
    SoftwareOffset[2] = OffsetSliderCh2.getValue() * YDiv[2] * 5 / 10000;// * INV[2];Do i want it to go up when slider pulled down? no.
    //}
    if (OffsetSliderCh2.getValue() != 0 && ClearOffsetCh2.isSelected()) {
        ClearOffsetCh2.setSelected(false);
    }

    ZeroPointCh2Value = (YDiv[1] * 5) * OffsetSliderCh2.getValue() / 10000;
    if (Probe[1] == 2) {//channel 1 yes, this is correct. all annotations are linked to domain of channel 1. JFreeChart restriction
        ZeroPointCh2Value = ZeroPointCh2Value * 10;
    }
    ZeroPointCh2.setY(ZeroPointCh2Value);

    chartPlot.clearRangeMarkers();
    updateMarkers(true, false);
    //redraw threshold
    if (ThresholdMarkerOn) {
        SliderThreshold.setValue(SliderThreshold.getValue() + 1);
        SliderThreshold.setValue(SliderThreshold.getValue() - 1);
    }

}//GEN-LAST:event_OffsetSliderCh2StateChanged

private void OffsetSliderCh2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_OffsetSliderCh2FocusGained
    // TODO add your handling code here:
}//GEN-LAST:event_OffsetSliderCh2FocusGained

private void OffsetSliderCh2FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_OffsetSliderCh2FocusLost
    // TODO add your handling code here:
}//GEN-LAST:event_OffsetSliderCh2FocusLost

private void OffsetSliderCh3StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_OffsetSliderCh3StateChanged

    if (demoMode && OffsetSliderCh3.getValueIsAdjusting() && JavaRunning) {
        return;
    }


    offsetCh = 3;
    //OffsetSelectActionperformed(offsetCh, true);//this highlights the function
    double SliderOffsetValue = OffsetSliderCh3.getValue() - functionOffset[offsetCh];//new-old

    functionOffset[offsetCh] = OffsetSliderCh3.getValue();
    //SliderOffsetValueLb.setText((SliderOffset.getValue() / 100.00) + " %");

    if (demoMode) {
        PlotOffsetDataDemo(offsetCh, SliderOffsetValue);

        USBscope50_Main.seriesCh1.clear();
        USBscope50_Main.seriesCh2.clear();
        USBscope50_Main.seriesCh3.clear();
        USBscope50_Main.seriesCh4.clear();
        if (!JavaRunning) {
            t.DisplayDataDemo(false);
        }
    } else if (!JavaRunning && (SliderOffsetValue != 0)) {
        PlotOffsetData(offsetCh, SliderOffsetValue);
    }//else{//if (JavaRunning && (OffsetSliderCh1.getValue() != 0)){ not sure why this !=0
    SoftwareOffset[3] = OffsetSliderCh3.getValue() * YDiv[3] * 5 / 10000;// * INV[3];Do i want it to go up when slider pulled down? no.
    //}
    if (OffsetSliderCh3.getValue() != 0 && ClearOffsetCh3.isSelected()) {
        ClearOffsetCh3.setSelected(false);
    }
    ZeroPointCh3Value = (YDiv[1] * 5) * OffsetSliderCh3.getValue() / 10000;
    if (Probe[1] == 2) {//channel 1 yes, this is correct. all annotations are linked to domain of channel 1. JFreeChart restriction
        ZeroPointCh3Value = ZeroPointCh3Value * 10;
    }
    ZeroPointCh3.setY(ZeroPointCh3Value);

    chartPlot.clearRangeMarkers();
    updateMarkers(true, false);
    //redraw threshold
    if (ThresholdMarkerOn) {
        SliderThreshold.setValue(SliderThreshold.getValue() + 1);
        SliderThreshold.setValue(SliderThreshold.getValue() - 1);
    }
}//GEN-LAST:event_OffsetSliderCh3StateChanged

private void OffsetSliderCh3FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_OffsetSliderCh3FocusGained
    // TODO add your handling code here:
}//GEN-LAST:event_OffsetSliderCh3FocusGained

private void OffsetSliderCh3FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_OffsetSliderCh3FocusLost
    // TODO add your handling code here:
}//GEN-LAST:event_OffsetSliderCh3FocusLost

private void OffsetSliderCh4StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_OffsetSliderCh4StateChanged

    if (demoMode && OffsetSliderCh4.getValueIsAdjusting() && JavaRunning) {
        return;
    }

    offsetCh = 4;
    //OffsetSelectActionperformed(offsetCh, true);//this highlights the function
    double SliderOffsetValue = OffsetSliderCh4.getValue() - functionOffset[offsetCh];//new-old

    functionOffset[offsetCh] = OffsetSliderCh4.getValue();
    //SliderOffsetValueLb.setText((SliderOffset.getValue() / 100.00) + " %");

    if (demoMode) {
        PlotOffsetDataDemo(offsetCh, SliderOffsetValue);

        USBscope50_Main.seriesCh1.clear();
        USBscope50_Main.seriesCh2.clear();
        USBscope50_Main.seriesCh3.clear();
        USBscope50_Main.seriesCh4.clear();
        if (!JavaRunning) {
            t.DisplayDataDemo(false);
        }
    } else if (!JavaRunning && (SliderOffsetValue != 0)) {
        PlotOffsetData(offsetCh, SliderOffsetValue);
    }//else{//if (JavaRunning && (OffsetSliderCh1.getValue() != 0)){ not sure why this !=0
    SoftwareOffset[4] = OffsetSliderCh4.getValue() * YDiv[4] * 5 / 10000;// * INV[4];Do i want it to go up when slider pulled down? no.
    //}
    if (OffsetSliderCh4.getValue() != 0 && ClearOffsetCh4.isSelected()) {
        ClearOffsetCh4.setSelected(false);
    }

    ZeroPointCh4Value = (YDiv[1] * 5) * OffsetSliderCh4.getValue() / 10000;
    if (Probe[1] == 2) {//channel 1 yes, this is correct. all annotations are linked to domain of channel 1. JFreeChart restriction
        ZeroPointCh4Value = ZeroPointCh4Value * 10;
    }

    ZeroPointCh4.setY(ZeroPointCh4Value);

    chartPlot.clearRangeMarkers();
    updateMarkers(true, false);
    //redraw threshold
    if (ThresholdMarkerOn) {
        SliderThreshold.setValue(SliderThreshold.getValue() + 1);
        SliderThreshold.setValue(SliderThreshold.getValue() - 1);
    }
}//GEN-LAST:event_OffsetSliderCh4StateChanged

private void OffsetSliderCh4FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_OffsetSliderCh4FocusGained
    // TODO add your handling code here:
}//GEN-LAST:event_OffsetSliderCh4FocusGained

private void OffsetSliderCh4FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_OffsetSliderCh4FocusLost
    // TODO add your handling code here:
}//GEN-LAST:event_OffsetSliderCh4FocusLost

private void OffsetSliderCh1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_OffsetSliderCh1StateChanged


    if (demoMode && OffsetSliderCh1.getValueIsAdjusting() && JavaRunning) {
        return;
    }

    offsetCh = 1;
    //OffsetSelectActionperformed(offsetCh, true);//this highlights the function
    double SliderOffsetValue = OffsetSliderCh1.getValue() - functionOffset[offsetCh];//new-old

    functionOffset[offsetCh] = OffsetSliderCh1.getValue();
    //SliderOffsetValueLb.setText((SliderOffset.getValue() / 100.00) + " %");

    if (demoMode) {
        PlotOffsetDataDemo(offsetCh, SliderOffsetValue);

        USBscope50_Main.seriesCh1.clear();
        USBscope50_Main.seriesCh2.clear();
        USBscope50_Main.seriesCh3.clear();
        USBscope50_Main.seriesCh4.clear();
        if (!JavaRunning) {
            t.DisplayDataDemo(false);
        }
    } else if (!JavaRunning && (SliderOffsetValue != 0)) {
        PlotOffsetData(offsetCh, SliderOffsetValue);
    }//else{//if (JavaRunning && (OffsetSliderCh1.getValue() != 0)){ not sure why this !=0
    SoftwareOffset[1] = OffsetSliderCh1.getValue() * YDiv[1] * 5 / 10000;// * INV[1];Do i want it to go up when slider pulled down? no.
    //}
    if (OffsetSliderCh1.getValue() != 0) {
        ClearOffsetCh1.setSelected(false);
    }

    ZeroPointCh1Value = (YDiv[1] * 5) * OffsetSliderCh1.getValue() / 10000;

    if (Probe[1] == 2) {
        ZeroPointCh1Value = ZeroPointCh1Value * 10;
    }

    ZeroPointCh1.setY(ZeroPointCh1Value);

    chartPlot.clearRangeMarkers();
    updateMarkers(true, false);
    //redraw threshold
    if (ThresholdMarkerOn) {
        SliderThreshold.setValue(SliderThreshold.getValue() + 1);
        SliderThreshold.setValue(SliderThreshold.getValue() - 1);
    }

}//GEN-LAST:event_OffsetSliderCh1StateChanged

private void OffsetSliderCh1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_OffsetSliderCh1FocusGained
    // TODO add your handling code here:
}//GEN-LAST:event_OffsetSliderCh1FocusGained

private void OffsetSliderCh1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_OffsetSliderCh1FocusLost
    // TODO add your handling code here:
}//GEN-LAST:event_OffsetSliderCh1FocusLost

private void OffsetZeroCh1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OffsetZeroCh1ActionPerformed
    OffsetSliderCh1.setValue(0);
}//GEN-LAST:event_OffsetZeroCh1ActionPerformed

private void OffsetZeroCh2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OffsetZeroCh2ActionPerformed
    OffsetSliderCh2.setValue(0);
}//GEN-LAST:event_OffsetZeroCh2ActionPerformed

private void OffsetZeroCh3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OffsetZeroCh3ActionPerformed
    OffsetSliderCh3.setValue(0);
}//GEN-LAST:event_OffsetZeroCh3ActionPerformed

private void OffsetZeroCh4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OffsetZeroCh4ActionPerformed
    OffsetSliderCh4.setValue(0);
}//GEN-LAST:event_OffsetZeroCh4ActionPerformed

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new USBscope50_Main().setVisible(true);
            }
        });
    }
    private double chartX;                              //Add marker to this point on the graph
    private double chartY;                              //manipulated in chartMouseClicked
    private Color SliderFocusColor = new Color(140, 200, 255);
    private double ZoomInRangeLower = 0.1;          //zoom in - take away 10% on each side of the X-Axis
    private double ZoomInRangeHigher = 0.9;
    private double ZoomOutRangeLower = -0.1;        //zoom out - add 10% on each side of the X-Axis
    private double ZoomOutRangeHigher = 1.1;
    ImageIcon ElanLogo = new ImageIcon(getClass().getResource("/usbscope50software/Images/elan-logo.jpg"));
    //ImageIcon Elan_gif = new ImageIcon(getClass().getResource("/usbscope50software/Images/ElanLogo.gif"));
    //ImageIcon Elan_gif = new ImageIcon(getClass().getResource("/usbscope50software/Images/elan-logo.gif"));
    ImageIcon ElanLogo_Small = new ImageIcon(getClass().getResource("/usbscope50software/Images/elan-logo_small.jpg"));
    ImageIcon AKIPLogo = new ImageIcon(getClass().getResource("/usbscope50software/Images/AKIP-logo.jpg"));
    ImageIcon AKIPLogo_small = new ImageIcon(getClass().getResource("/usbscope50software/Images/AKIP-logo_small.jpg"));
    ImageIcon triggerFalling = new ImageIcon(getClass().getResource("/usbscope50software/Icons/falling.gif"));
    ImageIcon triggerRising = new ImageIcon(getClass().getResource("/usbscope50software/Icons/rising.gif"));
    ImageIcon triggerFree = new ImageIcon(getClass().getResource("/usbscope50software/Icons/free.gif"));
    ImageIcon triggerLessThan = new ImageIcon(getClass().getResource("/usbscope50software/Icons/lessthan.gif"));
    ImageIcon triggerMoreThan = new ImageIcon(getClass().getResource("/usbscope50software/Icons/morethan.gif"));
    private int maxGraphPointCount = 3000;           //number of data instances on the graph
    double x_axisData, y_axisData;                  //when generating function to display on the Graph use these for x,y displayed values
    int TabAtIndex = 0;                            //place CustomZoom panel at the tab position of this value
    String TabTxtCustomZoom = "Zoom";
    //public static boolean chartZoomed = false;
    /**********ATTENTION************/
    int CustomZoomPanelWidth = 239;     //need this value before the custom panel is displayed in the tab, so hard code (otherwise it returns 0)
    /**********ATTENTION************/
    private boolean EnterPressedJSpinner = false;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel ArrowPanelCh1;
    private javax.swing.JPanel ArrowPanelCh2;
    private javax.swing.JPanel ArrowPanelCh3;
    private javax.swing.JPanel ArrowPanelCh4;
    private javax.swing.JPanel ArrowPanelColorCh1;
    private javax.swing.JPanel ArrowPanelColorCh2;
    private javax.swing.JPanel ArrowPanelColorCh3;
    private javax.swing.JPanel ArrowsPanelColorCh4;
    private javax.swing.JPanel ArrowsPanelLineCh1;
    private javax.swing.JPanel ArrowsPanelLineCh2;
    private javax.swing.JPanel ArrowsPanelLineCh3;
    private javax.swing.JPanel ArrowsPanelLineCh4;
    private javax.swing.JPanel BackgroundColorPanel;
    private javax.swing.ButtonGroup ButGroupTriggerMode;
    private javax.swing.ButtonGroup ButGroupTriggerOn;
    private javax.swing.JPanel ButtonsCh1;
    private javax.swing.JPanel ButtonsCh2;
    private javax.swing.JPanel ButtonsCh3;
    private javax.swing.JPanel ButtonsCh4;
    private javax.swing.JToggleButton COMPButtonCh1;
    private javax.swing.JToggleButton COMPButtonCh2;
    private javax.swing.JToggleButton COMPButtonCh3;
    private javax.swing.JToggleButton COMPButtonCh4;
    private javax.swing.JPanel Ch1Color;
    private javax.swing.JColorChooser Ch1ColorChooser;
    private javax.swing.JPanel Ch2Color;
    private javax.swing.JColorChooser Ch2ColorChooser;
    private javax.swing.JPanel Ch3Color;
    private javax.swing.JColorChooser Ch3ColorChooser;
    private javax.swing.JPanel Ch4Color;
    private javax.swing.JColorChooser Ch4ColorChooser;
    private javax.swing.JDialog ChannelColorsDialog;
    private javax.swing.JTabbedPane ChannelColourTabs;
    private javax.swing.JDialog ChartOptionsDialog;
    private javax.swing.JTabbedPane ChartOptionsTabs;
    private javax.swing.JPanel ChartStrokePanel;
    private javax.swing.JRadioButton ClearOffsetCh1;
    private javax.swing.JRadioButton ClearOffsetCh2;
    private javax.swing.JRadioButton ClearOffsetCh3;
    private javax.swing.JRadioButton ClearOffsetCh4;
    private javax.swing.JButton ClearmarkersToolBarButton;
    private javax.swing.JColorChooser ColorChooser;
    private javax.swing.JColorChooser ColorChooser1;
    private javax.swing.JPanel CustomZoomPanel;
    private javax.swing.JLabel CustomZoomXAxisLabel1;
    private javax.swing.JLabel CustomZoomXAxisLabel2;
    private javax.swing.JLabel CustomZoomYAxisLabel1;
    private javax.swing.JLabel CustomZoomYAxisLabel2;
    private javax.swing.JDialog DisplayLicenseDialog;
    private javax.swing.JSpinner DomainSpinner;
    private javax.swing.JButton ExitLicenseDialog;
    private javax.swing.JButton ExportToolBarButton;
    private javax.swing.JToggleButton FFTButtonCh1;
    private javax.swing.JToggleButton FFTButtonCh2;
    private javax.swing.JToggleButton FFTButtonCh3;
    private javax.swing.JToggleButton FFTButtonCh4;
    private javax.swing.JButton FFTToolBarButton;
    private javax.swing.JSpinner FunctionSpinner;
    private javax.swing.JPanel GridLinesColorPanel;
    private javax.swing.JPanel GridLinesThicknessPanel;
    private javax.swing.JButton HelpToolBarButton;
    private javax.swing.JButton IDButtonCh1;
    private javax.swing.JButton IDButtonCh2;
    private javax.swing.JButton IDButtonCh3;
    private javax.swing.JButton IDButtonCh4;
    private javax.swing.JToggleButton INVButtonCh1;
    private javax.swing.JToggleButton INVButtonCh2;
    private javax.swing.JToggleButton INVButtonCh3;
    private javax.swing.JToggleButton INVButtonCh4;
    private javax.swing.JButton ImportToolBarButton;
    private javax.swing.JLabel LbCurrentTimeBaseCh1;
    private javax.swing.JLabel LbCurrentTimeBaseCh2;
    private javax.swing.JLabel LbCurrentTimeBaseCh3;
    private javax.swing.JLabel LbCurrentTimeBaseCh4;
    private javax.swing.JButton LeftArrowSecDivCh1;
    private javax.swing.JButton LeftArrowSecDivCh2;
    private javax.swing.JButton LeftArrowSecDivCh3;
    private javax.swing.JButton LeftArrowSecDivCh4;
    private javax.swing.JButton LeftArrowVDivCh1;
    private javax.swing.JButton LeftArrowVDivCh2;
    private javax.swing.JButton LeftArrowVDivCh3;
    private javax.swing.JButton LeftArrowVDivCh4;
    private javax.swing.JPanel LeftSplitPanel;
    private javax.swing.JMenuBar MenuBar;
    private javax.swing.JMenuItem MnuAbout;
    private javax.swing.JCheckBoxMenuItem MnuAddHorizontalMarker;
    private javax.swing.JCheckBoxMenuItem MnuAddVerticalMarker;
    private javax.swing.JCheckBoxMenuItem MnuCh1;
    private javax.swing.JCheckBoxMenuItem MnuCh2;
    private javax.swing.JCheckBoxMenuItem MnuCh3;
    private javax.swing.JCheckBoxMenuItem MnuCh4;
    private javax.swing.JMenu MnuChOptions;
    private javax.swing.JMenuItem MnuChannelSettings;
    private javax.swing.JMenuItem MnuChartOptions;
    private javax.swing.JMenuItem MnuClearAllMarkers;
    private javax.swing.JMenuItem MnuClearHorizontalMarkers;
    private javax.swing.JMenu MnuClearMarkers;
    private javax.swing.JMenuItem MnuClearVerticalMarkers;
    private javax.swing.JMenuItem MnuDisplayonOneGraph;
    private javax.swing.JMenu MnuEdit;
    private javax.swing.JMenuItem MnuExit;
    private javax.swing.JMenuItem MnuExport;
    private javax.swing.JMenuItem MnuFFT;
    private javax.swing.JMenuItem MnuFFTOnly;
    private javax.swing.JMenu MnuFFTOptions;
    private javax.swing.JMenu MnuFFTPlotType;
    private javax.swing.JMenu MnuFFTWindowType;
    private javax.swing.JMenu MnuFile;
    private javax.swing.JMenu MnuGraph;
    private javax.swing.JMenuItem MnuGraphOnly;
    private javax.swing.JMenuItem MnuGraphandOp;
    private javax.swing.JRadioButtonMenuItem MnuHamming;
    private javax.swing.JRadioButtonMenuItem MnuHanning;
    private javax.swing.JMenu MnuHelp;
    private javax.swing.JMenuItem MnuHelpF1;
    private javax.swing.JMenuItem MnuImportGraph;
    private javax.swing.JRadioButtonMenuItem MnuLinearFFTPlot;
    private javax.swing.JMenu MnuMainFFT;
    private javax.swing.JMenuItem MnuPrint;
    private javax.swing.JRadioButtonMenuItem MnuRectangular;
    private javax.swing.JMenuItem MnuRemoveFFT;
    private javax.swing.JMenuItem MnuRestoretoDefaultScreenSize;
    private javax.swing.JMenu MnuRun;
    private javax.swing.JMenuItem MnuRunGraph;
    private javax.swing.JMenuItem MnuSaveAs;
    private javax.swing.JMenu MnuSettings;
    private javax.swing.JCheckBoxMenuItem MnuShowYAxisLabels;
    private javax.swing.JMenuItem MnuSingle;
    private javax.swing.JMenuItem MnuSplitGraphs;
    private javax.swing.JMenuItem MnuStop;
    private javax.swing.JMenuItem MnuSupport;
    private javax.swing.JMenu MnuTools;
    private javax.swing.JRadioButtonMenuItem MnuTriangular;
    private javax.swing.JMenuItem MnuTrigger;
    private javax.swing.JMenuItem MnuTutorial;
    private javax.swing.JMenuItem MnuUG;
    private javax.swing.JMenu MnuView;
    private javax.swing.JMenuItem MnuViewonFullScreen;
    private javax.swing.JRadioButtonMenuItem MnuWelch;
    private javax.swing.JMenu MnuZoom;
    private javax.swing.JMenuItem MnuZoomCancel;
    private javax.swing.JMenuItem MnuZoomCustom;
    private javax.swing.JMenuItem MnuZoomIn;
    private javax.swing.JMenuItem MnuZoomOut;
    private javax.swing.JRadioButtonMenuItem MnudBVFFTPlot;
    private javax.swing.JButton NewToolBarButton;
    private javax.swing.JButton NullButtonCh1;
    private javax.swing.JButton NullButtonCh2;
    private javax.swing.JButton NullButtonCh3;
    private javax.swing.JButton NullButtonCh4;
    private javax.swing.JButton OKXAxisCustomZoom;
    private javax.swing.JButton OKYAxisCustomZoom;
    private javax.swing.JToggleButton ONButtonCh1;
    private javax.swing.JToggleButton ONButtonCh2;
    private javax.swing.JToggleButton ONButtonCh3;
    private javax.swing.JToggleButton ONButtonCh4;
    private javax.swing.JSlider OffsetSliderCh1;
    private javax.swing.JSlider OffsetSliderCh2;
    private javax.swing.JSlider OffsetSliderCh3;
    private javax.swing.JSlider OffsetSliderCh4;
    private javax.swing.JMenuItem OffsetZeroCh1;
    private javax.swing.JMenuItem OffsetZeroCh2;
    private javax.swing.JMenuItem OffsetZeroCh3;
    private javax.swing.JMenuItem OffsetZeroCh4;
    private javax.swing.JRadioButton OpACCouplingCh1;
    private javax.swing.JRadioButton OpACCouplingCh2;
    private javax.swing.JRadioButton OpACCouplingCh3;
    private javax.swing.JRadioButton OpACCouplingCh4;
    private javax.swing.JRadioButton OpDCCouplingCh1;
    private javax.swing.JRadioButton OpDCCouplingCh2;
    private javax.swing.JRadioButton OpDCCouplingCh3;
    private javax.swing.JRadioButton OpDCCouplingCh4;
    private javax.swing.JRadioButton OpGNDCh1;
    private javax.swing.JRadioButton OpGNDCh2;
    private javax.swing.JRadioButton OpGNDCh3;
    private javax.swing.JRadioButton OpGNDCh4;
    private javax.swing.JButton OpenToolBarButton;
    private javax.swing.JPanel OptionsCh1;
    private javax.swing.JPanel OptionsCh2;
    private javax.swing.JPanel OptionsCh3;
    private javax.swing.JPanel OptionsCh4;
    private javax.swing.JRadioButton Opx10ProbeCh1;
    private javax.swing.JRadioButton Opx10ProbeCh2;
    private javax.swing.JRadioButton Opx10ProbeCh3;
    private javax.swing.JRadioButton Opx10ProbeCh4;
    private javax.swing.JRadioButton Opx1ProbeCh1;
    private javax.swing.JRadioButton Opx1ProbeCh2;
    private javax.swing.JRadioButton Opx1ProbeCh3;
    private javax.swing.JRadioButton Opx1ProbeCh4;
    private javax.swing.JPanel PanelButtonsCh1;
    private javax.swing.JPanel PanelButtonsCh2;
    private javax.swing.JPanel PanelButtonsCh3;
    private javax.swing.JPanel PanelButtonsCh4;
    private javax.swing.JPanel PanelforChart;
    private javax.swing.JPanel ProbeCouplingCh1;
    private javax.swing.JPanel ProbeCouplingCh2;
    private javax.swing.JPanel ProbeCouplingCh3;
    private javax.swing.JPanel ProbeCouplingCh4;
    private javax.swing.JRadioButton RadioButTrigOnChan1;
    private javax.swing.JRadioButton RadioButTrigOnChan2;
    private javax.swing.JRadioButton RadioButTrigOnChan3;
    private javax.swing.JRadioButton RadioButTrigOnChan4;
    private javax.swing.JSpinner RangeSpinner;
    private javax.swing.JButton RemoveFFTToolBarButton;
    private javax.swing.JButton RightArrowSecDivCh1;
    private javax.swing.JButton RightArrowSecDivCh2;
    private javax.swing.JButton RightArrowSecDivCh3;
    private javax.swing.JButton RightArrowSecDivCh4;
    private javax.swing.JButton RightArrowVDivCh1;
    private javax.swing.JButton RightArrowVDivCh2;
    private javax.swing.JButton RightArrowVDivCh3;
    private javax.swing.JButton RightArrowVDivCh4;
    public static javax.swing.JTabbedPane RightSplitPane;
    private javax.swing.JToolBar RunToolBar;
    private javax.swing.JButton RunToolBarButton;
    private javax.swing.JButton SaveToolBarButton;
    private javax.swing.JScrollPane ScrollCustomZoomPane;
    private javax.swing.JScrollPane ScrollOptionsCh1;
    private javax.swing.JScrollPane ScrollOptionsCh2;
    private javax.swing.JScrollPane ScrollOptionsCh3;
    private javax.swing.JScrollPane ScrollOptionsCh4;
    private javax.swing.JScrollPane ScrollPaneGPLLicense;
    private javax.swing.JScrollPane ScrollTriggerPanel;
    private javax.swing.JPanel SelectTriggerChPanel;
    private javax.swing.JSeparator Sep10;
    private javax.swing.JSeparator Sep12;
    private javax.swing.JSeparator Sep3;
    private javax.swing.JSeparator Sep4;
    private javax.swing.JSeparator Sep5;
    private javax.swing.JSeparator Sep6;
    private javax.swing.JSeparator Sep6_1;
    private javax.swing.JSeparator Sep7;
    private javax.swing.JSeparator Sep8;
    private javax.swing.JSeparator Sep9_0;
    private javax.swing.JSeparator Sep9_1;
    private javax.swing.JSeparator Sep9_2;
    private javax.swing.JButton SingleToolBarButton;
    private javax.swing.JSlider SliderThreshold;
    private javax.swing.JSplitPane SplitPane;
    private javax.swing.JToolBar StatusToolBar;
    private javax.swing.JButton StopToolBarButton;
    private javax.swing.JToolBar ToolBarFile;
    private javax.swing.JToolBar ToolBarHelp;
    private javax.swing.JToolBar ToolBarZoom;
    private javax.swing.JRadioButton TriggerModeFallingEdge;
    private javax.swing.JRadioButton TriggerModeFree;
    private javax.swing.JRadioButton TriggerModeGreaterThan;
    private javax.swing.JRadioButton TriggerModeLessThan;
    private javax.swing.JRadioButton TriggerModeRisingEdge;
    private javax.swing.JLabel TriggerOnLabel;
    private javax.swing.JButton TriggerOptionsToolBarButton;
    private javax.swing.JPanel TriggerPanel;
    private javax.swing.JSlider TriggerPositionSlider;
    private javax.swing.JCheckBox WannaCancelZoomChkBox;
    private javax.swing.JPanel XAxisOptions;
    private javax.swing.JSpinner XAxisZoomValue;
    private javax.swing.JPanel YAxisOptions;
    private javax.swing.JSpinner YAxisZoomValue;
    private javax.swing.JButton ZoomCancelToolBarButton;
    private javax.swing.JButton ZoomInToolBarButton;
    private javax.swing.JButton ZoomOutToolBarButton;
    private javax.swing.JButton butProductTitle;
    private javax.swing.JButton butRISstatus;
    private javax.swing.JButton butSpace;
    private javax.swing.JButton buttChColorCancel1;
    private javax.swing.JButton buttChColorCancel2;
    private javax.swing.JButton buttChColorCancel3;
    private javax.swing.JButton buttChColorCancel4;
    private javax.swing.JButton buttChColorDefaultValues1;
    private javax.swing.JButton buttChColorDefaultValues2;
    private javax.swing.JButton buttChColorDefaultValues3;
    private javax.swing.JButton buttChColorDefaultValues4;
    private javax.swing.JButton buttChColorOK1;
    private javax.swing.JButton buttChColorOK2;
    private javax.swing.JButton buttChColorOK3;
    private javax.swing.JButton buttChColorOK4;
    private javax.swing.JButton buttChartDefaultValues;
    private javax.swing.JButton buttChartDefaultValues1;
    private javax.swing.JButton buttChartDefaultValues2;
    private javax.swing.JButton buttChartOptionsCancel;
    private javax.swing.JButton buttChartOptionsCancel1;
    private javax.swing.JButton buttChartOptionsCancel2;
    private javax.swing.JButton buttChartOptionsOK;
    private javax.swing.JButton buttChartOptionsOK1;
    private javax.swing.JButton buttChartOptionsOK2;
    private javax.swing.ButtonGroup buttGroupCouplingCh1;
    private javax.swing.ButtonGroup buttGroupCouplingCh2;
    private javax.swing.ButtonGroup buttGroupCouplingCh3;
    private javax.swing.ButtonGroup buttGroupCouplingCh4;
    private javax.swing.ButtonGroup buttGroupOffset;
    private javax.swing.ButtonGroup buttGroupProbeCh1;
    private javax.swing.ButtonGroup buttGroupProbeCh2;
    private javax.swing.ButtonGroup buttGroupProbeCh3;
    private javax.swing.ButtonGroup buttGroupProbeCh4;
    private javax.swing.JButton buttonCancelZoom;
    private javax.swing.ButtonGroup buttonGroupFFTPlotType;
    private javax.swing.ButtonGroup buttonGroupFFTWindowType;
    private javax.swing.JCheckBox chkModifyBoth;
    private javax.swing.ButtonGroup grpSelectOffsetCh;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    public static javax.swing.JTextField lbFDivAllChs;
    private javax.swing.JTextField lbTDivAllChs;
    private javax.swing.JLabel lbTriggerStatus;
    private javax.swing.JTextField lbVDivCh1;
    private javax.swing.JTextField lbVDivCh2;
    private javax.swing.JTextField lbVDivCh3;
    private javax.swing.JTextField lbVDivCh4;
    private javax.swing.JPanel panelVDiv;
    private javax.swing.JButton statusButton;
    private javax.swing.JTextPane txtGPLLicense;
    // End of variables declaration//GEN-END:variables
}

class MyFilterPDF extends javax.swing.filechooser.FileFilter {

    @Override
    public boolean accept(File file) {
        String filename = file.getName();
        return filename.toLowerCase().endsWith(".pdf") | file.isDirectory();
    }

    @Override
    public String getDescription() {
        return "PDF files (*.pdf)";
    }
}

class MyFilterJPG extends javax.swing.filechooser.FileFilter {

    @Override
    public boolean accept(File file) {
        String filename = file.getName();
        return filename.toLowerCase().endsWith(".jpg") | filename.toLowerCase().endsWith(".jpeg") | file.isDirectory();
    }

    @Override
    public String getDescription() {
        return "JPEG (*.jpg,*.jpeg)";
    }
}

class MyFilterPNG extends javax.swing.filechooser.FileFilter {

    @Override
    public boolean accept(File file) {
        String filename = file.getName();
        return filename.toLowerCase().endsWith(".png") | file.isDirectory();
    }

    @Override
    public String getDescription() {
        return "PNG Bitmap (*.png)";
    }
}

class MyFilterTxt extends javax.swing.filechooser.FileFilter {

    @Override
    public boolean accept(File file) {
        String filename = file.getName();
        return filename.toLowerCase().endsWith(".txt") | file.isDirectory();
    }

    @Override
    public String getDescription() {
        return "text files (*.txt)";
    }
}

class ChartPanelModified extends org.jfree.chart.ChartPanel {

    public ChartPanelModified(JFreeChart chart, boolean useBuffer) {
        super(chart, useBuffer);
    }

    @Override
    public void restoreAutoBounds() {
    }

    @Override
    public void mouseDragged(MouseEvent e) {

        if (USBscope50_Main.chartPanel.getCursor() == java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.N_RESIZE_CURSOR)) {

            USBscope50_Main.chartPlot.clearRangeMarkers();
            UpdateMarkersfromDragged(e.getX(), e.getY(), true, false, 0);// mouse coordinates + horizontal,vertical
        } else if (USBscope50_Main.chartPanel.getCursor() == java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.S_RESIZE_CURSOR)) {
            USBscope50_Main.chartPlot.clearRangeMarkers();
            UpdateMarkersfromDragged(e.getX(), e.getY(), true, false, 1);// mouse coordinates + horizontal,vertical
        } else if (USBscope50_Main.chartPanel.getCursor() == java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.E_RESIZE_CURSOR)) {

            USBscope50_Main.chartPlot.clearDomainMarkers();
            UpdateMarkersfromDragged(e.getX(), e.getY(), false, true, 0);//mouse coordiantes + horizontal,vertical

        } else if (USBscope50_Main.chartPanel.getCursor() == java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.W_RESIZE_CURSOR)) {
            USBscope50_Main.chartPlot.clearDomainMarkers();
            UpdateMarkersfromDragged(e.getX(), e.getY(), false, true, 1);//mouse coordiantes + horizontal,vertical
        } else {
            super.mouseDragged(e);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

        if (USBscope50_Main.chartPanel.getCursor() == java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.N_RESIZE_CURSOR)) {
        } else if (USBscope50_Main.chartPanel.getCursor() == java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.E_RESIZE_CURSOR)) {
        } else if (USBscope50_Main.chartPanel.getCursor() == java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.S_RESIZE_CURSOR)) {
        } else if (USBscope50_Main.chartPanel.getCursor() == java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.W_RESIZE_CURSOR)) {
        } else {
            double XpointsStart = USBscope50_Main.axisX.getUpperBound();

            super.mouseReleased(e);
            double XpointsEnd = USBscope50_Main.axisX.getUpperBound();
            if (XpointsStart != XpointsEnd) {//user zoomed the graph using mouse
                USBscope50_Main.ChartZoomed = true;
            }

        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (USBscope50_Main.chartPanel.getCursor() == java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.N_RESIZE_CURSOR)) {
        } else if (USBscope50_Main.chartPanel.getCursor() == java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.E_RESIZE_CURSOR)) {
        } else if (USBscope50_Main.chartPanel.getCursor() == java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.S_RESIZE_CURSOR)) {
        } else if (USBscope50_Main.chartPanel.getCursor() == java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.W_RESIZE_CURSOR)) {
        } else {
            super.mousePressed(e);
        }
    }

    private void GetGraphValueOfMouseClickedPoint(int HorizontalMousePosition, int VerticalMousePosition, int dataSetNum) {

        Point2D p = USBscope50_Main.chartPanel.translateScreenToJava2D(new Point((int) HorizontalMousePosition, VerticalMousePosition));
        ChartRenderingInfo info = USBscope50_Main.chartPanel.getChartRenderingInfo();
        Rectangle2D dataArea = info.getPlotInfo().getDataArea();
        ValueAxis domainAxis = USBscope50_Main.chartPlot.getDomainAxis();
        RectangleEdge domainAxisEdge = USBscope50_Main.chartPlot.getDomainAxisEdge();
        ValueAxis rangeAxis = USBscope50_Main.chartPlot.getRangeAxisForDataset(dataSetNum);//dataset 1 is for channel 2
        RectangleEdge rangeAxisEdge = USBscope50_Main.chartPlot.getRangeAxisEdge();
        chartXDragging = domainAxis.java2DToValue(p.getX(), dataArea, domainAxisEdge);
        chartYDragging = rangeAxis.java2DToValue(p.getY(), dataArea, rangeAxisEdge);
    }

    private String GetHorizontalmarkerLabel(int MarkerID) {//0 or 1 passed
        String HorizontalMarkerLabel = "";
        int whichVUnit = 0;


        draggedMarker.setPaint(Color.black);
        draggedMarker.setLabelPaint(Color.black);


        if (chartYDragging > (USBscope50_Main.axisY[1].getUpperBound() / 5 * 4)) {
            draggedMarker.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
            draggedMarker.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
            draggedMarker.setLabelOffset(USBscope50_Main.rectOffset_horiz_down);
            if ((MarkerID > 0) && (Math.abs(USBscope50_Main.HorizontalMarkerLocation[1] - USBscope50_Main.HorizontalMarkerLocation[0]) < (USBscope50_Main.axisY[1].getUpperBound() / 5))) {
                //if ((Math.abs(USBscope50_Main.HorizontalMarkerLocation[1] - USBscope50_Main.HorizontalMarkerLocation[0]) < (USBscope50_Main.axisY[1].getUpperBound() / 5))) {
                //draggedMarker.setLabelAnchor(RectangleAnchor.BOTTOM_LEFT);
                //draggedMarker.setLabelTextAnchor(TextAnchor.TOP_LEFT);
                //draggedMarker.setLabelOffset(USBscope50_Main.rectOffset_horiz);
                draggedMarker.setLabelOffset(USBscope50_Main.rectOffset_horiz_down_left);
            }
        } else {
            draggedMarker.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
            draggedMarker.setLabelTextAnchor(TextAnchor.BOTTOM_RIGHT);
            if ((MarkerID > 0) && (Math.abs(USBscope50_Main.HorizontalMarkerLocation[1] - USBscope50_Main.HorizontalMarkerLocation[0]) < (USBscope50_Main.axisY[1].getUpperBound() / 5))) {
                //if ((Math.abs(USBscope50_Main.HorizontalMarkerLocation[1] - USBscope50_Main.HorizontalMarkerLocation[0]) < (USBscope50_Main.axisY[1].getUpperBound() / 5))) {
                //draggedMarker.setLabelAnchor(RectangleAnchor.BOTTOM_LEFT);
                //draggedMarker.setLabelTextAnchor(TextAnchor.TOP_LEFT);
                draggedMarker.setLabelOffset(USBscope50_Main.rectOffset_horiz);
            }
        }

        for (int i = 1; i <= USBscope50_Main.numScopesFound; i++) {
            if ((USBscope50_Main.ChannelOn[i] || USBscope50_Main.demoMode) && USBscope50_Main.CheckTabTitles("Ch " + i)) {
                /*if (USBscope50_Main.intVoltsperDivPointer[i] < 8) {
                whichVUnit = 0;
                } else {
                whichVUnit = 1;
                }*/
                whichVUnit = 1;
                GetGraphValueOfMouseClickedPoint(USBscope50_Main.HorizontalMarkerMouseClickLocation[MarkerID][0], USBscope50_Main.HorizontalMarkerMouseClickLocation[MarkerID][1], i - 1);
                HorizontalMarkerLabel = HorizontalMarkerLabel + "Ch" + i + "=" + USBscope50_Main.df.format(chartYDragging - (USBscope50_Main.functionOffset[i] * 5 * USBscope50_Main.YDiv[i] / 10000)) + "" + USBscope50_Main.VoltageUnits[whichVUnit] + ";";
            }
        }
        return (HorizontalMarkerLabel);
    }

    private String GetVerticalMarkerLabel(int MarkerID) {//0 or 1 variable passed
        int indexTimeUnits;
        String TimeMarker;
        double atTime;
        double adjust = 1;
        String TimeUnit = null;
        double PointsPerDiv = GetPointsPerDiv(USBscope50_Main.intTimeBaseArrayPointer);

        draggedMarker.setPaint(Color.black);

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
        if (USBscope50_Main.demoMode) {

            if (USBscope50_Main.demoMode && !USBscope50_Main.JavaRunning) {
                adjust = adjust * USBscope50_Main.VerticalMarkerAdjust;

                if (USBscope50_Main.lastPointsPerDiv == 40) {//this is as i have moved point by 20 as 0 is in the middle of the x axis and to get div lines i had to move by 20
                    chartXDragging = chartXDragging - 20;
                }

            } else if (USBscope50_Main.intTimeBaseArrayPointer < 5 && USBscope50_Main.demoMode) {
                //adjust = adjust;//Ris mode already adjuster
            } else if (USBscope50_Main.intTimeBaseArrayPointer < 8 && USBscope50_Main.demoMode) {
            } else if (USBscope50_Main.JavaRunning) {
                adjust = adjust / (100 / PointsPerDiv);
            }


        } else {
            if (USBscope50_Main.JavaRunning) {
                adjust = adjust / (100 / PointsPerDiv);
                if (USBscope50_Main.intTimeBaseArrayPointer < 4) {//this is as i have moved point by 20 as 0 is in the middle of the x axis and to get div lines i had to move by 20
                    chartXDragging = chartXDragging - 20;
                }
            } else {
                adjust = adjust * USBscope50_Main.VerticalMarkerAdjust;
            }

        }

        atTime = (chartXDragging + 1500) * 10 / adjust * (Double.parseDouble(USBscope50_Main.TimeBaseSettings[USBscope50_Main.intTimeBaseArrayPointer][3]));


        USBscope50_Main.verticalMarkersAtTime[MarkerID + 1] = atTime;
        USBscope50_Main.verticalMarkersTimeUnitIndex[MarkerID + 1] = indexTimeUnits;

        if ((atTime > 1000) && (indexTimeUnits < USBscope50_Main.TimeUnits.length - 1)) {
            indexTimeUnits++;
            atTime = atTime / 1000;
        }


        if ((chartXDragging + 1500) > ((USBscope50_Main.axisX.getUpperBound() + 1500) * 3 / 4)) {
            draggedMarker.setLabelAnchor(RectangleAnchor.TOP);
            draggedMarker.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
            if (MarkerID > 0 && (Math.abs(USBscope50_Main.VerticalMarkerLocation[1] - USBscope50_Main.VerticalMarkerLocation[0]) < AllowedDistanceBetweenMarkers)) {
                //draggedMarker.setLabelAnchor(RectangleAnchor.BOTTOM);//use offset inste3ad of bottom
                draggedMarker.setLabelTextAnchor(TextAnchor.BOTTOM_RIGHT);
                draggedMarker.setLabelOffset(USBscope50_Main.rectOffset);
            }
        } else {
            draggedMarker.setLabelAnchor(RectangleAnchor.TOP);
            draggedMarker.setLabelTextAnchor(TextAnchor.TOP_LEFT);
            if (MarkerID > 0 && (Math.abs(USBscope50_Main.VerticalMarkerLocation[1] - USBscope50_Main.VerticalMarkerLocation[0]) < AllowedDistanceBetweenMarkers)) {
                //System.out.println("The time difference between two vertical markers is: " + (Math.abs(VerticalMarkerLocation[1] - VerticalMarkerLocation[0])));

                //draggedMarker.setLabelAnchor(RectangleAnchor.BOTTOM);//use offset inste3ad of bottom
                draggedMarker.setLabelTextAnchor(TextAnchor.BOTTOM_LEFT);
                draggedMarker.setLabelOffset(USBscope50_Main.rectOffset);
            }
        }
        TimeMarker = USBscope50_Main.df.format(atTime);//(chartX + 1500)*10*(Double.parseDouble(TimeBaseSettings[intTimeBaseArrayPointer][3])));
        TimeUnit = USBscope50_Main.TimeUnits[indexTimeUnits];
        double dt = (Math.abs(USBscope50_Main.verticalMarkersAtTime[2] - USBscope50_Main.verticalMarkersAtTime[1]));
        int dtUnit = USBscope50_Main.verticalMarkersTimeUnitIndex[MarkerID + 1];
        if (dt > 1000 && dtUnit < 3) {
            dt = dt / 1000;
            dtUnit++;
        }

        if ((MarkerID + 1) == 2) {
            if (USBscope50_Main.DeltaShowing) {
                USBscope50_Main.chartPlot.removeAnnotation(USBscope50_Main.Delta);
                USBscope50_Main.chartPlot.removeAnnotation(USBscope50_Main.DeltaText);
            }

            int mult = 1;
            if (USBscope50_Main.Probe[1] == 2) {
                mult = 10;
            }

            Font font = new Font("SansSerif", Font.PLAIN, 9);
            double smallerX = (USBscope50_Main.VerticalMarkerLocation[0] < USBscope50_Main.VerticalMarkerLocation[1] ? USBscope50_Main.VerticalMarkerLocation[0] : USBscope50_Main.VerticalMarkerLocation[1]);
            USBscope50_Main.Delta = new XYLineAnnotation(USBscope50_Main.VerticalMarkerLocation[0], USBscope50_Main.YDiv[1] * mult * 4.0, USBscope50_Main.VerticalMarkerLocation[1], USBscope50_Main.YDiv[1] * mult * 4.0);
            USBscope50_Main.DeltaText = new XYTextAnnotation("dt=" + USBscope50_Main.df.format(dt) + USBscope50_Main.TimeUnits[dtUnit], Math.abs(USBscope50_Main.VerticalMarkerLocation[0] - USBscope50_Main.VerticalMarkerLocation[1]) / 2 + (smallerX), (USBscope50_Main.YDiv[1] * mult * 4.0) + (USBscope50_Main.YDiv[1] * mult / 3));
            USBscope50_Main.DeltaText.setFont(font);
            USBscope50_Main.chartPlot.addAnnotation(USBscope50_Main.Delta);
            USBscope50_Main.chartPlot.addAnnotation(USBscope50_Main.DeltaText);
            USBscope50_Main.DeltaShowing = true;
        //System.out.println(USBscope50_Main.DeltaText.getFont());
        //System.out.println("3:-dt=" + USBscope50_Main.df.format(dt)+USBscope50_Main.TimeUnits[dtUnit]);
        }


        return (TimeMarker + TimeUnit);

    }

    private double GetPointsPerDiv(int ArrayPointer) {
        double points = 100;

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

    private void UpdateMarkersfromDragged(int HorizontalMousePosition, int VerticalMousePosition, boolean HorizontalMarkerDragged, boolean VerticalMarkerDragged, int MarkerID) {//MarkerID is 0 or 1
        String HorizontalMarkerLabel = "";
        String VerticalMarkerLabel = "";

        if (HorizontalMarkerDragged) {

            if (MarkerID > 0) {// update marker 2 HorizontalMarkerMouseClickLocation and redraw marker 1
                USBscope50_Main.HorizontalMarkerMouseClickLocation[1][0] = HorizontalMousePosition;
                USBscope50_Main.HorizontalMarkerMouseClickLocation[1][1] = VerticalMousePosition;

                //now add the other marker
                GetGraphValueOfMouseClickedPoint(USBscope50_Main.HorizontalMarkerMouseClickLocation[0][0], USBscope50_Main.HorizontalMarkerMouseClickLocation[0][1], 0);

                draggedMarker = new ValueMarker(chartYDragging);
                HorizontalMarkerLabel = GetHorizontalmarkerLabel(0);
                draggedMarker.setLabel(HorizontalMarkerLabel);
                USBscope50_Main.chartPlot.addRangeMarker(draggedMarker);
            } else {  //0 = update marker 1
                USBscope50_Main.HorizontalMarkerMouseClickLocation[0][0] = HorizontalMousePosition;
                USBscope50_Main.HorizontalMarkerMouseClickLocation[0][1] = VerticalMousePosition;

                //now add the other marker
                if (USBscope50_Main.NoRangeMarkersOnChart > 1) {
                    GetGraphValueOfMouseClickedPoint(USBscope50_Main.HorizontalMarkerMouseClickLocation[1][0], USBscope50_Main.HorizontalMarkerMouseClickLocation[1][1], 0);
                    draggedMarker = new ValueMarker(chartYDragging);

                    HorizontalMarkerLabel = GetHorizontalmarkerLabel(1);//send 0 or 1
                    draggedMarker.setLabel(HorizontalMarkerLabel);
                    USBscope50_Main.chartPlot.addRangeMarker(draggedMarker);
                }
            }
            GetGraphValueOfMouseClickedPoint(HorizontalMousePosition, VerticalMousePosition, 0);
            USBscope50_Main.HorizontalMarkerLocation[MarkerID] = chartYDragging;
            draggedMarker = new ValueMarker(chartYDragging);
            HorizontalMarkerLabel = GetHorizontalmarkerLabel(MarkerID);
            draggedMarker.setLabel(HorizontalMarkerLabel);

            USBscope50_Main.chartPlot.addRangeMarker(draggedMarker);

        } else if (VerticalMarkerDragged) {

            GetGraphValueOfMouseClickedPoint(HorizontalMousePosition, VerticalMousePosition, 0);
            USBscope50_Main.VerticalMarkerLocation[MarkerID] = chartXDragging;//indexes 0 and 1 used
            USBscope50_Main.VerticalMarkerMouseClickLocation[MarkerID][0] = HorizontalMousePosition;
            USBscope50_Main.VerticalMarkerMouseClickLocation[MarkerID][1] = VerticalMousePosition;

            draggedMarker = new ValueMarker(chartXDragging);
            VerticalMarkerLabel = GetVerticalMarkerLabel(MarkerID);
            draggedMarker.setLabel("@" + VerticalMarkerLabel);
            USBscope50_Main.chartPlot.addDomainMarker(MarkerID, draggedMarker, Layer.FOREGROUND);

            if (MarkerID > 0 && USBscope50_Main.NoDomainMarkersOnChart > 1) {// update marker 2 VerticalMarkerMouseClickLocation and redraw marker 1
                USBscope50_Main.VerticalMarkerMouseClickLocation[1][0] = HorizontalMousePosition;
                USBscope50_Main.VerticalMarkerMouseClickLocation[1][1] = VerticalMousePosition;
                //now add the other marker
                GetGraphValueOfMouseClickedPoint(USBscope50_Main.VerticalMarkerMouseClickLocation[0][0], USBscope50_Main.VerticalMarkerMouseClickLocation[0][1], 0);
                draggedMarker = new ValueMarker(chartXDragging);

                VerticalMarkerLabel = GetVerticalMarkerLabel(0);
                draggedMarker.setLabel("@" + VerticalMarkerLabel);
                USBscope50_Main.chartPlot.addDomainMarker(draggedMarker);
            } else {  //0 = update marker 1 at index 0, and redraw marker 2
                USBscope50_Main.VerticalMarkerMouseClickLocation[0][0] = HorizontalMousePosition;
                USBscope50_Main.VerticalMarkerMouseClickLocation[0][1] = VerticalMousePosition;
                //now add the other marker

                if (USBscope50_Main.NoDomainMarkersOnChart == 2) {
                    GetGraphValueOfMouseClickedPoint(USBscope50_Main.VerticalMarkerMouseClickLocation[1][0], USBscope50_Main.VerticalMarkerMouseClickLocation[1][1], 0);
                    draggedMarker = new ValueMarker(chartXDragging);

                    VerticalMarkerLabel = GetVerticalMarkerLabel(1);
                    draggedMarker.setLabel("@" + VerticalMarkerLabel);
                    USBscope50_Main.chartPlot.addDomainMarker(draggedMarker);
                }
            }





        }
        if (USBscope50_Main.ThresholdMarkerOn) {
            draggedMarker = new ValueMarker(USBscope50_Main.ThresholdMarkerValue);
            draggedMarker.setPaint(Color.DARK_GRAY);
            draggedMarker.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, new float[]{10.0f, 10.0f}, 0.0f));
            USBscope50_Main.chartPlot.addRangeMarker(draggedMarker);
        }
    }
    private double chartYDragging = 0;
    private double chartXDragging = 0;
    Marker draggedMarker;
    //RectangleInsets rectOffset=new RectangleInsets(30, 0, 0, 0);
    //RectangleInsets rectOffset_horiz = new RectangleInsets(0, 0, 0, 240);
    //RectangleInsets rectOffset_horiz_down = new RectangleInsets(0, 0, 10, 240);
    int AllowedDistanceBetweenMarkers = 450;
}

