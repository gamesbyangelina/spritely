package org.gba.spritely;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.gba.spritely.concurrent.SpritelyThread;

//Much of this class owes its code to http://docs.oracle.com/javase/tutorial/displayCode.html?code=http://docs.oracle.com/javase/tutorial/uiswing/examples/layout/FlowLayoutDemoProject/src/layout/FlowLayoutDemo.java

public class SpritelyUI extends JFrame implements ChangeListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1355966279718666239L;

	List<JCheckBox> options = new LinkedList<JCheckBox>();
	
	final String RECOLOR_TEXT = "Recolor Output Image";
	final String IMAGE_SEARCH = "Search term for images:";
	final String COLOR_SEARCH = "Search term for colour palettes:";
	final String NUM_IMAGES = "Number of images to retrieve (per source):";
	final String GOOG = "Search Google Images";
	final String WIKI = "Search Wikimedia Commons";
	final String OCA = "Search OpenClipArt";
	final String RANDIMAGE = "Choose Images Randomly";
	final String RANDCOLOR = "Choose Colour Palette Randomly";
	final String OUTPUT_TEXT = "Output path:";
	
	JRadioButton RtoLbutton;
    JRadioButton LtoRbutton;
    FlowLayout experimentLayout = new FlowLayout();
    final String RtoL = "Right to left";
    final String LtoR = "Left to right";
    JButton applyButton = new JButton("Apply component orientation");

	private JTextField numImagesField;
	private JTextField colourSearchField;
	private JCheckBox randColorBox;
	private JTextField imageSearchField;
	private JCheckBox useRecolor;
	private JCheckBox gBox;
	private JCheckBox wBox;
	private JCheckBox oBox;

	private JTextField outputField;
	
	public static Thread workerThread;

	private static JButton goButton;

    public SpritelyUI(String name) {
        super(name);
    }
     
    public void addComponentsToPane(final Container pane) {
        final JPanel compsToExperiment = new JPanel();
        compsToExperiment.setLayout(experimentLayout);
        experimentLayout.setAlignment(FlowLayout.TRAILING);
        JPanel controls = new JPanel();
        controls.setLayout(new FlowLayout());
         
        LtoRbutton = new JRadioButton(LtoR);
        LtoRbutton.setActionCommand(LtoR);
        LtoRbutton.setSelected(true);
        RtoLbutton = new JRadioButton(RtoL);
        RtoLbutton.setActionCommand(RtoL);
        
        //Add controls to set up the component orientation in the experiment layout
        final ButtonGroup group = new ButtonGroup();
        group.add(LtoRbutton);
        group.add(RtoLbutton);
        controls.add(LtoRbutton);
        controls.add(RtoLbutton);
        controls.add(applyButton);
        
        JPanel imageSearchPanel = new JPanel();
        imageSearchPanel.setLayout(new GridLayout(0, 1));
        JLabel jl = new JLabel("Image Search Options");
        imageSearchPanel.add(jl);
        jl.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel imageSearchTerm = new JPanel();
        imageSearchTerm.setLayout(new FlowLayout());
        imageSearchTerm.add(new JLabel(IMAGE_SEARCH));
        imageSearchField = new JTextField("dog", 10);
        imageSearchTerm.add(imageSearchField);
        imageSearchPanel.add(imageSearchTerm);
        JPanel numImagesPanel = new JPanel();
        numImagesPanel.setLayout(new FlowLayout());
        numImagesPanel.add(new JLabel(NUM_IMAGES));
        numImagesField = new JTextField("1", 4);
        numImagesPanel.add(numImagesField);
        imageSearchPanel.add(numImagesPanel);
        jl = new JLabel("Image Sources");
        imageSearchPanel.add(jl);
        jl.setHorizontalAlignment(SwingConstants.CENTER);
        gBox = new JCheckBox(GOOG); gBox.setSelected(true);
        imageSearchPanel.add(gBox);
        wBox = new JCheckBox(WIKI);
//        imageSearchPanel.add(wBox);
        oBox = new JCheckBox(OCA);
        imageSearchPanel.add(oBox);
        jl = new JLabel("Other Options");
        imageSearchPanel.add(jl);
        jl.setHorizontalAlignment(SwingConstants.CENTER);
        imageSearchPanel.add(new JCheckBox(RANDIMAGE));
        
        JPanel colourPalettePanel = new JPanel();
        colourPalettePanel.setLayout(new GridLayout(0, 1));
        jl = new JLabel("Colour Search Options");
        colourPalettePanel.add(jl);
        jl.setHorizontalAlignment(SwingConstants.CENTER);
        useRecolor = new JCheckBox(RECOLOR_TEXT, false);
        useRecolor.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent e) {
				randColorBox.setEnabled(!randColorBox.isEnabled());
				colourSearchField.setEnabled(!colourSearchField.isEnabled());
			}
        });
        
        colourPalettePanel.add(useRecolor);
        JPanel paletteSearchPanel = new JPanel();
        paletteSearchPanel.setLayout(new FlowLayout());
        paletteSearchPanel.add(new JLabel(COLOR_SEARCH));
        colourSearchField = new JTextField("winter", 5);
        colourSearchField.setEnabled(false);
        paletteSearchPanel.add(colourSearchField);
        colourPalettePanel.add(paletteSearchPanel);
        randColorBox = new JCheckBox(RANDCOLOR); randColorBox.setSelected(true);
        randColorBox.setEnabled(false);
        colourPalettePanel.add(randColorBox);
        
        JPanel goPanel = new JPanel();
        goPanel.setLayout(new GridLayout(0, 1));
        JPanel outputPanel = new JPanel();
        outputPanel.setLayout(new FlowLayout());
        outputPanel.add(new JLabel(OUTPUT_TEXT));
        outputField = new JTextField(System.getProperty("user.home")+System.getProperty("file.separator")+"spritely", 10);
        outputPanel.add(outputField);
        goPanel.add(outputPanel);
        
        goButton = new JButton("Go");
        goButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Searching for "+imageSearchField.getText()+", looking for "+numImagesField.getText()+" images.");
				if(useRecolor.isSelected()){
					System.out.println("Recolouring the images with "+colourSearchField.getText()+" palettes.");
				}
				
				Spritely s = new Spritely();
				int numImages = 1;
				try{
					numImages = Integer.parseInt(numImagesField.getText());
				}
				catch(NumberFormatException ex){
					ex.printStackTrace();
				}
				s.setImagesPerSource(numImages);
				s.setQuery(imageSearchField.getText());
				s.setSearchGoogleImages(gBox.isSelected());
				s.setSearchOpenClipart(oBox.isSelected());
				s.setSearchWikimediaCommons(wBox.isSelected());
				s.setOutputPath(outputField.getText());
				s.setSize(32);
				if(useRecolor.isSelected()){
					s.setRecolor(colourSearchField.getText());
				}
				workerThread = new Thread(new SpritelyThread(s));
				workerThread.start();
				
				((JButton)e.getSource()).setEnabled(false);
				((JButton)e.getSource()).setText("Searching!");
			}
		});
        goPanel.add(goButton);
        
        pane.setLayout(new GridLayout(0, 1));
        pane.add(imageSearchPanel);//, BorderLayout.WEST);
        pane.add(colourPalettePanel);//, BorderLayout.EAST);
        pane.add(goPanel);//, BorderLayout.SOUTH);
    }
     
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event dispatch thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        SpritelyUI frame = new SpritelyUI("Spritely v0.0");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //Set up the content pane.
        frame.addComponentsToPane(frame.getContentPane());
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
     
    public static void main(String[] args) {
        /* Use an appropriate Look and Feel */
        try {
            //UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        /* Turn off metal's use of bold fonts */
        UIManager.put("swing.boldMetal", Boolean.FALSE);
        //Schedule a job for the event dispatchi thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

	@Override
	public void stateChanged(ChangeEvent e) {
		JCheckBox obj = (JCheckBox) e.getSource();
		if(obj.getText().equalsIgnoreCase(RECOLOR_TEXT)){
			randColorBox.setEnabled(obj.isEnabled());
			colourSearchField.setEnabled(obj.isEnabled());
		}
	}
	
	public static void notifySearchComplete(){
		goButton.setEnabled(true);
		goButton.setText("Go");
	}
    
}
