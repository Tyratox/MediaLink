package ch.tyratox.network.media.mediaLink;

import java.awt.Font;
import java.io.IOException;
import java.net.Socket;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JOptionPane;

import com.cjsavage.java.net.discovery.ServiceAnnouncer;
import com.cjsavage.java.net.discovery.ServiceFinder;
import com.cjsavage.java.net.discovery.ServiceInfo;
import com.cjsavage.java.net.discovery.ServiceFinder.Listener;

import ch.tyratox.design.flatUI.java.FColors;
import ch.tyratox.network.deviceDiscovery.DeviceDiscovery;

public class MediaLink {
	
	public MediaLinkServer server;
	public MediaLinkClient client;
	
	public String ip = "localhost";
	public int port = 6728;
	public String file = "song.wav";
	
	public Font font = Fonts.lato(getClass(), 14f);
	
	public MediaLinkServerUI serverUI;
	
	public String SID = "6222544206f5eea957785d62285e8028"; //ch.tyratox.network.media.medialink
	public String serverName = "DropShareServer";
	
	public boolean serverMode = false;
	public boolean clientMode = false;
	
	private ServiceFinder finder;
	private ServiceAnnouncer announcer;
	
	public static void main(String[] args){
		new MediaLink(args);
	}
	public MediaLink(String[] args){
		try{
			//Get IP
			Socket s = new Socket("google.com", 80);
			ip = s.getLocalAddress().getHostAddress();
			s.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		FColors colors = new FColors();
		colors.frameBackground = FColors.orange;
		colors.buttonBackground  = FColors.orange;
		colors.buttonBackgroundClick = FColors.blue;
		colors.buttonBackgroundHover = FColors.orange_dark;
		
		if(args.length <= 0 || args == null || args[0] == null){
			MediaLinkModeChooser chooser = new MediaLinkModeChooser(colors, font, "Close", false);
			chooser.setVisible(true);
			
			while(chooser.getMode() == -1){
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if(chooser.getMode()==0){
				serverMode=true;
			}else if(chooser.getMode()==1){
				clientMode=true;
			}else{
				System.err.println("IMPOSSIBLE");
			}
		}else{
			client = new MediaLinkClient(args[0], port);
		}
		if(serverMode!=clientMode && serverMode==true){
			//Server Mode
			try {
				announcer = DeviceDiscovery.showServiceInNetwork(SID, serverName, port, true);
				server = new MediaLinkServer(port, file, this);
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (UnsupportedAudioFileException e1) {
				e1.printStackTrace();
			} catch (LineUnavailableException e1) {
				e1.printStackTrace();
			}
			serverUI = new MediaLinkServerUI(colors, font, "Close", false, this);
			serverUI.setVisible(true);
			//Add Shutdown hook
			Runtime.getRuntime().addShutdownHook(new Thread() {
			    public void run() {
			    	try{
			    		announcer.stopListening();
			    	}catch(Exception e){
			    		
			    	}
			        server.play = false;
			        try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
			    }
			});
		}else if(serverMode!=clientMode && clientMode==true){
			//Client Mode
			//TODO UI
			Runtime.getRuntime().addShutdownHook(new Thread() {
			    public void run() {
			        try {
						client.close();
						try{
							finder.stopListening();
						}catch(Exception e){
							
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					}
			    }
			});
			finder = DeviceDiscovery.searchServiceInNetwork(SID, mListener);
		}
		
		
	}
	
	private Listener mListener = new Listener() {
	    @Override
	    public void serverFound(ServiceInfo si, int requestId, ServiceFinder finder_) {
	        System.out.println("Found service provider named " + si.getServerName() + " at " + si.getServiceHost() + ":" + si.getServicePort());
	        //FIXME
	        if(JOptionPane.showConfirmDialog(null, "Found a MediaLink Server: " + si.getServiceHost() + ". Do you want to connect?", "Host found!", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE) == JOptionPane.YES_OPTION){
	        	finder.stopListening();
	        	client = new MediaLinkClient(si.getServiceHost(), port);
	        }
	    }

	    @Override
	    public void listenStateChanged(ServiceFinder finder, boolean listening) {
	    }
	};
	
	public static void playAudioStream(AudioInputStream stream) throws LineUnavailableException, IOException{
		AudioFormat format = stream.getFormat();
	    if (format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
	      format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, format
	          .getSampleRate(), format.getSampleSizeInBits() * 2, format
	          .getChannels(), format.getFrameSize() * 2, format.getFrameRate(),
	          true); // big endian
	      stream = AudioSystem.getAudioInputStream(format, stream);
	    }
	    
		SourceDataLine.Info info = new DataLine.Info(SourceDataLine.class, stream.getFormat(), ((int) stream.getFrameLength() * format.getFrameSize()));
		SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
		line.open(stream.getFormat());
		line.start();
		
		int numRead = 0;
	    byte[] buf = new byte[line.getBufferSize()];
	    while ((numRead = stream.read(buf, 0, buf.length)) >= 0) {
	      int offset = 0;
	      while (offset < numRead) {
	        offset += line.write(buf, offset, numRead - offset);
	      }
	    }
	    line.drain();
	    line.stop();
	}

}
