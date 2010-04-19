package is.iclt.icenlp.arc.gui;

import is.iclt.icenlp.arc.network.NetworkException;
import is.iclt.icenlp.arc.network.NetworkHandler;
import is.iclt.icenlp.client.network.ClientNetworkHandler;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import javax.swing.*;

public class ARCGuiWindow extends JFrame{
	private JLabel jLabel1;
	private JLabel jLabel2;
	private JLabel jLabel3;
	private JLabel jLabel4;
	private JTextField hostText;
	private JTextArea clientTaggingOutput;
	private JScrollPane jScrollPane1;
	private JTextArea translationTextArea;
	private JScrollPane jScrollPane2;
	private JTextArea apertiumOutputtextArea;
	private JScrollPane jScrollPane3;
	private JPanel contentPane;
	private JButton translateButton;
	private TextField portText;


    public ARCGuiWindow(){
		super();
		initializeComponent();
		this.setVisible(true);
	}

	private void initializeComponent()
	{
		jLabel1 = new JLabel();
		jLabel2 = new JLabel();
		jLabel3 = new JLabel();
		jLabel4 = new JLabel();

		hostText = new JTextField();
        portText = new TextField(6);
		clientTaggingOutput = new JTextArea();
		jScrollPane1 = new JScrollPane();
		translationTextArea = new JTextArea();
		jScrollPane2 = new JScrollPane();
		apertiumOutputtextArea = new JTextArea();
		jScrollPane3 = new JScrollPane();
		contentPane = (JPanel)this.getContentPane();
		portText = new TextField();
		translateButton = new JButton("translate");

		//
		// jLabel1
		//
		jLabel1.setText("Router host:");
        jLabel1.setSize(10,10);
		//
		// jLabel2
		//
		jLabel2.setText("Server output:");
        
		//
		// jLabel3
		//
		jLabel3.setText("Apertium output:");
		//
		// jTextField1
		//

		jLabel4.setText("Port:");

		hostText.setText("localhost");
        portText.setText("2526");

		translateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				translateButton_actionPerformed(e);
			}
		});

		//
		// clientTaggingOutput
		//
		clientTaggingOutput.setText("");
        clientTaggingOutput.setEditable(false);

		//
		// jScrollPane1
		//
		jScrollPane1.setViewportView(clientTaggingOutput);
        clientTaggingOutput.setLineWrap(true);

		//
		// translationTextArea
		//
		translationTextArea.setText("Sláðu inn texta til þess að þýða.");
		//
		// jScrollPane2
		//
		jScrollPane2.setViewportView(translationTextArea);
		//
		// apertiumOutputtextArea
		//
		apertiumOutputtextArea.setText("");
        apertiumOutputtextArea.setEditable(false);
		//
		// jScrollPane3
		//
		jScrollPane3.setViewportView(apertiumOutputtextArea);
		//
		// contentPane
		//
		contentPane.setLayout(null);
		addComponent(contentPane, jLabel1, 5,6,60,18);
		addComponent(contentPane, portText, 230,5,107,22);
		addComponent(contentPane, translateButton, 350,5,107,22);
		addComponent(contentPane, jLabel4, 200,6,60,18);
		addComponent(contentPane, jLabel2, 6,199,70,18);
		addComponent(contentPane, jLabel3, 6,380,86,18);
		addComponent(contentPane, hostText, 73,5,107,22);
		addComponent(contentPane, jScrollPane1, 6,217,542,152);
		addComponent(contentPane, jScrollPane2, 6,32,542,155);
		addComponent(contentPane, jScrollPane3, 6,402,543,157);

		this.setTitle("ARC GUI");
		this.setLocation(new Point(307, 25));
		this.setSize(new Dimension(562, 600));
		this.setResizable(false);
	}

	/** Add Component Without a Layout Manager (Absolute Positioning) */
	private void addComponent(Container container,Component c,int x,int y,int width,int height){
		c.setBounds(x,y,width,height);
		container.add(c);
	}

	private void translateButton_actionPerformed(ActionEvent e){
		String hostText = this.hostText.getText();
		String portText = this.portText.getText();
        String translationText = this.translationTextArea.getText();

        try{
            // Get the tagged output for the string.
            ClientNetworkHandler clientHandler = new ClientNetworkHandler("localhost","1234");
            clientTaggingOutput.setText(clientHandler.tagString(translationText));
            
            // Get the translation for the sentence.
            NetworkHandler handler = new NetworkHandler(hostText, portText);
            apertiumOutputtextArea.setText(handler.translate(translationText));
        }
        catch (NetworkException e1) 
        {
            JOptionPane.showMessageDialog(this, "Unable to connect to router " + hostText + ":" + portText);
        }
        catch (IOException e1) 
        {
            JOptionPane.showMessageDialog(this, "Unable to connect to tag the string.");
        }
	}
}
