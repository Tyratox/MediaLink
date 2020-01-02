package ch.tyratox.network.media.mediaLink;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;

public class MediaLinkClient extends Socket{
	
	public MediaLinkClient(final String ip, final int port){
		new Thread(){
			public void run(){
				try{
					connect((new InetSocketAddress(ip, port)));
					InputStream is;
					while((is = getInputStream()) == null){
						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					InputStream bufferedIn = new BufferedInputStream(is);
					AudioInputStream ais = AudioSystem.getAudioInputStream(bufferedIn);
					try {
						MediaLink.playAudioStream(ais);
					} catch (LineUnavailableException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					is.close();
				}catch(Exception e){
					if(e instanceof EOFException){
						System.exit(0);
					}else{
						e.printStackTrace();
					}
				}
			}
		}.start();
	}
	

}
