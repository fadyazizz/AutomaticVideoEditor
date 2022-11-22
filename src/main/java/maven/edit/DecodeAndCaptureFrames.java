// 
// Decompiled by Procyon v0.5.36
// 

package maven.edit;

import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.Utils;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainerFormat;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IVideoResampler;
import com.xuggle.xuggler.SimpleMediaFile;

import java.awt.image.RenderedImage;
import javax.imageio.ImageIO;
import java.io.File;
import com.xuggle.xuggler.Global;
import java.awt.image.BufferedImage;
import com.xuggle.xuggler.IVideoPicture;

public class DecodeAndCaptureFrames
{
    public static final double SECONDS_BETWEEN_FRAMES = 0.05;
    public static final long NANO_SECONDS_BETWEEN_FRAMES;
    private static long mLastPtsWrite;
    static int counter=0;
    
    private static void processFrame(final IVideoPicture picture, final BufferedImage image) {
    	System.out.println("--------I am here0--------");
    	System.out.println("--------------Time Stamp----------");
    	System.out.println(picture.getTimeStamp());
    	System.out.println("--------------pts----------");
    	System.out.println(picture.getPts());
    	
    	try {
            if (DecodeAndCaptureFrames.mLastPtsWrite == Global.NO_PTS) {
            	System.out.println("--------I am here1--------");
            	System.out.println("----------------");
            	System.out.println(picture.getPts());
            	//picture.getTimeStamp();
                DecodeAndCaptureFrames.mLastPtsWrite = picture.getPts() - DecodeAndCaptureFrames.NANO_SECONDS_BETWEEN_FRAMES;
                System.out.println("----------------");
                System.out.println("-------mLastPtsWrite---------: "+mLastPtsWrite);
                return;
            }
            File file=new File("/Users/macuser/Desktop/frames/photo"+(counter++)+".png");
            ImageIO.write(image, "png", file);
            final double seconds = picture.getPts() / (double)Global.DEFAULT_PTS_PER_SECOND;
            System.out.printf("at elapsed time of %6.3f seconds wrote: %s\n", seconds, file);
           // DecodeAndCaptureFrames.mLastPtsWrite += DecodeAndCaptureFrames.NANO_SECONDS_BETWEEN_FRAMES;
//            if (picture.getPts() - DecodeAndCaptureFrames.mLastPtsWrite >= DecodeAndCaptureFrames.NANO_SECONDS_BETWEEN_FRAMES) {
//            	System.out.println("--------I am here2--------");
//            	System.out.println("----------------");
//            	System.out.println(picture.getPts());
//            	File file=new File("/Users/macuser/Desktop/frames/photo"+(counter++)+".png");
//                ImageIO.write(image, "png", file);
//                final double seconds = picture.getPts() / (double)Global.DEFAULT_PTS_PER_SECOND;
//                System.out.printf("at elapsed time of %6.3f seconds wrote: %s\n", seconds, file);
//                DecodeAndCaptureFrames.mLastPtsWrite += DecodeAndCaptureFrames.NANO_SECONDS_BETWEEN_FRAMES;
//            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void main(final String[] args) {
    	//System.setProperty("java.io.tmpdir", "/Users/macuser/Desktop/frames");
    	String[] s= new String[1];
    	s[0]="/Users/macuser/Desktop/speak1.mp4";
    	SimpleMediaFile smf=new SimpleMediaFile();
    	smf.setURL(s[0]);
    	System.out.println("------------video bit rate-------- "+smf.getVideoBitRate());
    	System.out.println("------------video frame rate-------- "+smf.getVideoTimeBase());
        if (s.length <= 0) {
            throw new IllegalArgumentException("must pass in a filename as the first argument");
        }
        final String filename = s[0];
        if (!IVideoResampler.isSupported(IVideoResampler.Feature.FEATURE_COLORSPACECONVERSION)) {
            throw new RuntimeException("you must install the GPL version of Xuggler (with IVideoResampler support) for this demo to work");
        }
        IContainer container = IContainer.make();
        if (container.open(filename, IContainer.Type.READ, null) < 0) {
            throw new IllegalArgumentException("could not open file: " + filename);
        }
        final int numStreams = container.getNumStreams();
        int videoStreamId = -1;
        IStreamCoder videoCoder = null;
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
        IVideoResampler resampler = null;
        if (videoCoder.getPixelType() != IPixelFormat.Type.BGR24) {
            resampler = IVideoResampler.make(videoCoder.getWidth(), videoCoder.getHeight(), IPixelFormat.Type.BGR24, videoCoder.getWidth(), videoCoder.getHeight(), videoCoder.getPixelType());
            if (resampler == null) {
                throw new RuntimeException("could not create color space resampler for: " + filename);
            }
        }
        final IPacket packet = IPacket.make();
        while (container.readNextPacket(packet) >= 0) {
        	System.out.println("---------duration------- "+container.getDuration());
            if (packet.getStreamIndex() == videoStreamId) {
                final IVideoPicture picture = IVideoPicture.make(videoCoder.getPixelType(), videoCoder.getWidth(), videoCoder.getHeight());
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
                        newPic = IVideoPicture.make(resampler.getOutputPixelFormat(), picture.getWidth(), picture.getHeight());
                        if (resampler.resample(newPic, picture) < 0) {
                            throw new RuntimeException("could not resample video from: " + filename);
                        }
                    }
                    if (newPic.getPixelType() != IPixelFormat.Type.BGR24) {
                        throw new RuntimeException("could not decode video as BGR 24 bit data in: " + filename);
                    }
                    final BufferedImage javaImage = Utils.videoPictureToImage(newPic);
                    processFrame(newPic, javaImage);
                }
            }
        }
        if (videoCoder != null) {
            videoCoder.close();
            videoCoder = null;
        }
        if (container != null) {
            container.close();
            container = null;
        }
    }
    
    static {
        NANO_SECONDS_BETWEEN_FRAMES = (long)(Global.DEFAULT_PTS_PER_SECOND * 0.1);
        DecodeAndCaptureFrames.mLastPtsWrite = Global.NO_PTS;
        System.out.println("----------"+Global.DEFAULT_PTS_PER_SECOND+"--------");
    	System.out.println("----------"+Global.NO_PTS+"--------");
    }
}
