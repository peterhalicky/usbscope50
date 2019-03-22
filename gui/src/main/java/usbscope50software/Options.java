/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package usbscope50software;

import java.awt.Frame;
import javax.swing.JFrame;

/**
 *
 * @author ana.orec-archer
 */
public class Options extends JFrame {
    private static class FrameShower implements Runnable{
        final Frame frame;
        public FrameShower(Frame frame){
            this.frame = frame;
        }
        @Override
        public void run(){
            frame.setVisible(true);
        }
       
    }
}
