package maven.edit;

import java.awt.image.BufferedImage;

import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.IVideoResampler;
import com.xuggle.xuggler.Utils;

public class DecoderSub {
	
	
	
	public  IContainer container;
	public  String FilePath;

	 IVideoResampler resampler = null;
	 IStreamCoder videoCoder;
	 int videoStreamId = -1;
	 IVideoPicture picture;
	 BufferedImage javaImage;
	
	
	public void Decode(String path) {
		 this.FilePath=path;
		 
		 if (FilePath==null) {
		        throw new IllegalArgumentException("must pass in a filename as the first argument");
		    }
		    final String filename = FilePath;
		    if (!IVideoResampler.isSupported(IVideoResampler.Feature.FEATURE_COLORSPACECONVERSION)) {
		        throw new RuntimeException("you must install the GPL version of Xuggler (with IVideoResampler support) for this demo to work");
		    }
		     container = IContainer.make();
		    if (container.open(filename, IContainer.Type.READ, null) < 0) {
		        throw new IllegalArgumentException("could not open file: " + filename);
		    }
		    final int numStreams = container.getNumStreams();
		     videoStreamId = -1;
		    videoCoder = null;
		    for (int i = 0; i < numStreams; ++i) {
		        final IStream stream = container.getStream(i);
		        final IStreamCoder coder = stream.getStreamCoder();
		        if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
		            videoStreamId = i;
		            videoCoder = coder;
		            break;
		        }
		    }
		    if (videoStreamId == -1) {
		        throw new RuntimeException("could not find video stream in container: " + filename);
		    }
		    if (videoCoder.open() < 0) {
		        throw new RuntimeException("could not open video decoder for container: " + filename);
		    }
		     resampler = null;
		    if (videoCoder.getPixelType() != IPixelFormat.Type.BGR24) {
		        resampler = IVideoResampler.make(DecoderMain.dimen.width, DecoderMain.dimen.height, IPixelFormat.Type.BGR24, videoCoder.getWidth(), videoCoder.getHeight(), videoCoder.getPixelType());
		        if (resampler == null) {
		            throw new RuntimeException("could not create color space resampler for: " + filename);
		        }
		    }
		    
		    IPacket packet = IPacket.make();
		    while (container.readNextPacket(packet) >= 0) {
	            if (packet.getStreamIndex() == videoStreamId) {
	            	//System.out.println("--------------Dimen width"+DecoderMain.dimen.width);
	            	//System.out.println("--------------Dimen height"+DecoderMain.dimen.height);
	               picture = IVideoPicture.make(videoCoder.getPixelType(), DecoderMain.dimen.width, DecoderMain.dimen.height);
	                int offset = 0;
	                while (offset < packet.getSize()) {
	                    final int bytesDecoded = videoCoder.decodeVideo(picture, packet, offset);
	                    if (bytesDecoded < 0) {
	                        throw new RuntimeException("got error decoding video in: " + filename);
	                    }
	                    offset += bytesDecoded;
	                    if (!picture.isComplete()) {
	                        continue;
	                    }
	                    IVideoPicture newPic = picture;
	                    if (resampler != null) {
	                        newPic = IVideoPicture.make(resampler.getOutputPixelFormat(),DecoderMain.dimen.width, DecoderMain.dimen.height);
	                        if (resampler.resample(newPic, picture) < 0) {
	                            throw new RuntimeException("could not resample video from: " + filename);
	                        }
	                    }
	                    if (newPic.getPixelType() != IPixelFormat.Type.BGR24) {
	                        throw new RuntimeException("could not decode video as BGR 24 bit data in: " + filename);
	                    }
	                   
	                      javaImage = Utils.videoPictureToImage(newPic);
	                      
	                    long ptsIncrement=DecoderMain.lastPts+newPic.getPts();
	                    Engine.encoder.encode(javaImage,ptsIncrement);
	                    
	                    
	                }
	            }
	        }
		    
	 
	}
	
	
	
	
	
	
	
	
	
	
	

}
