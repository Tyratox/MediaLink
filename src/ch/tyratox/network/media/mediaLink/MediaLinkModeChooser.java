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

public class MediaLinkModeChooser extends FFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6121615997266550396L;
	
	private int mode = -1;

	public MediaLinkModeChooser(FColors colors, Font font, String closeText, boolean customCloseAction) {
		super(colors, font, closeText, customCloseAction);
		FBorders borders = new FBorders(colors);
		
		setBounds(100, 100, 350, 100);
		Container contentPane = getContentPane();
		
		final FButton btnClient = new FButton(colors, borders, font, "Client");
		final FButton btnServer = new FButton(colors, borders, font, "Server");
		btnClient.setBounds(25, 50, 100, 25);
		btnClient.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				btnServer.setEnabled(false);
				btnClient.setEnabled(false);
				mode = 1;
				dispose();
			}
		});
		contentPane.add(btnClient);
		
		btnServer.setBounds(175, 50, 100, 25);
		btnServer.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				btnServer.setEnabled(false);
				btnClient.setEnabled(false);
				mode = 0;
				dispose();
			}
		});
		contentPane.add(btnServer);
		
		FLabel choose = new FLabel(colors, font, "What Mode do you want to use?", true);
		choose.setBounds(67, 25, 200, 25);
		contentPane.add(choose);
	}
	
	public int getMode(){
		return mode;
	}

}
