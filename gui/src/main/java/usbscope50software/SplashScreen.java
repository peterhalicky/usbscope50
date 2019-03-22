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

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import java.awt.*;

public class SplashScreen extends JWindow {

    BorderLayout borderLayout1 = new BorderLayout();
    JLabel imageLabel = new JLabel();
    JLabel Spacer = new JLabel();
    JPanel southPanel = new JPanel();
    JProgressBar progressBar = new JProgressBar();
    FlowLayout southPanelFlowLayout = new FlowLayout();
    BorderLayout borderLayout2 = new BorderLayout(0, 3);
    ImageIcon imageIcon;
    JTextArea TxtMsg = new JTextArea();
    String SoftwareID = USBscope50_Main.productID + " Java Software ... ";//display this text on the splash screen
 
    public SplashScreen(ImageIcon imageIcon) {
        this.imageIcon = imageIcon;
        try {
            SSInit();   //initialize Splash Screen
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    void SSInit() throws Exception {

        imageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        imageLabel.setIcon(imageIcon);
        imageLabel.setBackground(Color.WHITE);
        progressBar.setForeground(Color.DARK_GRAY);
        progressBar.setBackground(Color.gray);

        //unable to change progress bar colour, maybe I need to take synthetica route!
        //(new Color(148,206,247));//
        //progressBar.setForeground(Color.yellow);//(new Color(148,206,247));
        //progressBar.setBackground(new Color(148,206,247));
        this.getContentPane().setLayout(borderLayout1);

        if (USBscope50_Main.companyID.equals("PRIST")){
            southPanel.setBackground(new Color(170,170,170));//Color.darkGray);//(new Color(255,207,79));//(Color.orange);//(Color.GRAY);
        }else{
            southPanel.setBackground(new Color(170,170,170));//(Color.darkGray);//(new Color(255,207,79));//(Color.orange);//(Color.GRAY);
        }
       
        southPanel.setLayout(borderLayout2);
        southPanel.add(Spacer, BorderLayout.NORTH);
        southPanel.add(TxtMsg, BorderLayout.CENTER);
        southPanel.add(progressBar, BorderLayout.SOUTH);

        this.getContentPane().add(imageLabel, BorderLayout.CENTER);
        this.getContentPane().add(southPanel, BorderLayout.SOUTH);

        if (USBscope50_Main.companyID.equals("PRIST")){
            TxtMsg.setBackground(Color.lightGray);//(new Color(98,188,206));//(Color.gray);//(new Color(167,190,255));//(Color.BLUE);//(new Color(255,244,156));//(Color.white);//(Color.lightGray);
        }else{
            TxtMsg.setBackground(Color.lightGray);//(new Color(98,188,206));//(Color.gray);//(new Color(167,190,255));//(Color.BLUE);//(new Color(255,244,156));//(Color.white);//(Color.lightGray);
        }
        TxtMsg.setFont(new Font("Lucinda", Font.ITALIC + Font.BOLD, 12));
        TxtMsg.setForeground(Color.white);//(Color.BLACK);

        TxtMsg.setText("\n  Loading " + SoftwareID +
                "\n  This should only take couple of seconds ...  \n");
        southPanel.add(TxtMsg);

        this.pack();
    }

    public void setScreenVisible(boolean b) {
        try {
            final boolean boo = b;
            SwingUtilities.invokeAndWait(new Runnable() {

                public void run() {
                    setVisible(boo);
                }
            });
        } catch (InterruptedException ex) {
            Logger.getLogger(SplashScreen.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(SplashScreen.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setProgressMax(int maxProgress) {
        progressBar.setMaximum(maxProgress);
    }

    public void setProgress(int progress) {
        final int theProgress = progress;
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {                
                progressBar.setValue(theProgress);
            }
        });
    }

    public void setProgress(String message, int progress) {
        final int theProgress = progress;
        final String theMessage = message;
        setProgress(progress);
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                progressBar.setValue(theProgress);
                setMessage(theMessage);
            }
        });
    }

    private void setMessage(String message) {
        if (message == null) {
            message = "";
            progressBar.setStringPainted(false);
        } else {
            progressBar.setStringPainted(true);
        }
        progressBar.setString(message);
    }
}
