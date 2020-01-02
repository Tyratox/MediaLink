package ch.tyratox.network.media.mediaLink;

import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import ch.tyratox.design.flatUI.java.FBorders;
import ch.tyratox.design.flatUI.java.FButton;
import ch.tyratox.design.flatUI.java.FColors;
import ch.tyratox.design.flatUI.java.FFrame;
import ch.tyratox.design.flatUI.java.FLabel;

public class MediaLinkServerUI extends FFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8840858998405641193L;
	
	private MediaLink main;
	
	public FLabel clientCounter;

	public MediaLinkServerUI(FColors colors, Font font, String closeText, boolean customCloseAction, MediaLink main_) {
		super(colors, font, closeText, customCloseAction);
		
		this.main = main_;
		
		FBorders borders = new FBorders(colors);
		
		setBounds(100, 100, 300, 450);
		
		Container contentPane = getContentPane();
		FButton btnPlay = new FButton(colors, borders, font, "Play");
		btnPlay.setBounds(25, 50, 100, 25);
		btnPlay.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				main.server.play = true;
			}
		});
		contentPane.add(btnPlay);
		
		FButton btnStop = new FButton(colors, borders, font, "Stop");
		btnStop.setBounds(175, 50, 100, 25);
		btnStop.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				main.server.play = false;
				
			}
		});
		contentPane.add(btnStop);
		
		clientCounter = new FLabel(colors, font, "", true);
		clientCounter.setBounds(75, 80, 150, 25);
		contentPane.add(clientCounter);
	}

}
