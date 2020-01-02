package ch.tyratox.network.media.mediaLink;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class MediaLinkServer extends ServerSocket{
	
	private Socket client;
	private boolean acceptConnection = true;
	
	private MediaLink main;
	private int clientCounter;
	public boolean play = false;
	
	private MediaLinkServer(final int port, InetAddress bindAddr, final String file, MediaLink main_) throws IOException, UnsupportedAudioFileException, LineUnavailableException{
		this.main = main_;
		if(new File(file).exists()!=true){
//			JFileChooser fc = new JFileChooser();
//			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
//			fc.setDialogTitle("Choose song to stream" +  " [." + "wav" + "]");
//			fc.setFileFilter(new FileFilter() {
//				
//				@Override
//				public String getDescription() {
//					 return "WAVE Sound" + " (*." + "wav" + ")";
//				}
//				
//				@Override
//				public boolean accept(File f) {
//					 return f.isDirectory() || f.getName().toLowerCase().endsWith("." + "wav");
//				}
//			});
//			fc.showDialog(null, "OPEN");
			System.exit(0);
		}
		bind(new InetSocketAddress(bindAddr, port), 50);
		if(this.isBound()){
			new Thread(){
				public void run(){
					while(acceptConnection){
						try{
							client = accept();
							if(client != null){
								System.out.println("Got new Client");
								clientCounter++;
								main.serverUI.clientCounter.setText("Clients connected: " + clientCounter);
								new Thread(){
									public void run(){
										try {
											waitForPlaying(client, file);
											return;
										} catch (Exception e) {
											if(e instanceof SocketException){
												return;
											}else{
												e.printStackTrace();
												return;
											}
										}
									}
								}.start();
							}
						}catch(Exception e){
							e.printStackTrace();
							return;
						}
					}
					return;
				}
			}.start();
		}
	}
	public void waitForPlaying(Socket socket, String file) throws IOException, UnsupportedAudioFileException{
		final AudioInputStream stream = AudioSystem.getAudioInputStream(new File(file));
		while(play==false){
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		waitForClosing(socket);
		OutputStream os = socket.getOutputStream();
		if(stream != null){
			System.out.println("Playing sound on: " + socket.getInetAddress().getHostAddress());
            AudioSystem.write(stream, AudioFileFormat.Type.WAVE, os);
        }
		os.close();
		socket.close();
	}
	
	public void waitForClosing(final Socket socket){
		new Thread(){
			public void run(){
				while(play){
					try {
						sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
						return;
					}
				}
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
				return;
			}
		}.start();
	}
	
	public MediaLinkServer(final int port, final String file, MediaLink main) throws IOException, UnsupportedAudioFileException, LineUnavailableException{
		this(port, null, file, main);
	}

}
