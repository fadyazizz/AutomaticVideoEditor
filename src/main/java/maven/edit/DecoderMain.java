package maven.edit;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.Vector;

import com.xuggle.ferry.IBuffer;
import com.xuggle.ferry.IBuffer.Type;
import com.xuggle.xuggler.IAudioResampler;
import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.IVideoResampler;

import com.xuggle.xuggler.Utils;

public class DecoderMain {

	public IContainer container;
	IContainer BackGroundContainer;
	public String FilePath;

	IVideoResampler resampler = null;
	IStreamCoder videoCoder;
	int videoStreamId = -1;
	IVideoPicture picture;
	BufferedImage javaImage;
	static long lastPts = 0;
	static Dimension dimen;

	static int AudioStreamID = -1;
	static IStreamCoder AudioCoder;
	static int SampleRate;
	static int BackGroundSampleRate;
	static int BackGroundAudioStreamID = -1;
	static IStreamCoder BackGroundCoder;
	static String BackGroundPath;
	static boolean endReached = false;

	Vector<IAudioSamples> mainSamples = new Vector<IAudioSamples>(1, 1);
	Vector<IAudioSamples> backSamples = new Vector<IAudioSamples>(1, 1);
	Vector<IAudioSamples> All = new Vector<IAudioSamples>(1, 1);

	public DecoderMain(String FilePath) {
		this.FilePath = FilePath;

		if (FilePath == null) {
			throw new IllegalArgumentException("must pass in a filename as the first argument");
		}
		final String filename = FilePath;
		if (!IVideoResampler.isSupported(IVideoResampler.Feature.FEATURE_COLORSPACECONVERSION)) {
			throw new RuntimeException(
					"you must install the GPL version of Xuggler (with IVideoResampler support) for this demo to work");
		}
		this.container = IContainer.make();

		if (this.container.open(filename, IContainer.Type.READ, null) < 0) {
			throw new IllegalArgumentException("could not open file: " + filename);
		}
		final int numStreams = container.getNumStreams();
		System.out.println("--------------numstreams" + numStreams);
		this.videoStreamId = -1;
		this.videoCoder = null;
		for (int i = 0; i < numStreams; ++i) {
			final IStream stream = container.getStream(i);
			final IStreamCoder coder = stream.getStreamCoder();
			if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
				videoStreamId = i;
				videoCoder = coder;

			}
			if (DecoderMain.AudioStreamID == -1 && coder.getCodecType() == ICodec.Type.CODEC_TYPE_AUDIO) {
				AudioCoder = coder;
				AudioStreamID = i;
			}
		}
		if (videoStreamId == -1) {
			throw new RuntimeException("could not find video stream in container: " + filename);
		}
		if (videoCoder.open() < 0) {
			throw new RuntimeException("could not open video decoder for container: " + filename);
		}
		if (AudioStreamID == -1) {
			throw new RuntimeException("could not find Audio stream in container: " + filename);
		}
		if (AudioCoder.open() < 0) {
			throw new RuntimeException("could not open video decoder for container: " + filename);
		}

		resampler = null;
		if (videoCoder.getPixelType() != IPixelFormat.Type.BGR24) {
			resampler = IVideoResampler.make(videoCoder.getWidth(), videoCoder.getHeight(), IPixelFormat.Type.BGR24,
					videoCoder.getWidth(), videoCoder.getHeight(), videoCoder.getPixelType());
			if (resampler == null) {
				throw new RuntimeException("could not create color space resampler for: " + filename);
			}
		}
		DecoderMain.dimen = new Dimension(videoCoder.getWidth(), videoCoder.getHeight());
		SampleRate = AudioCoder.getSampleRate();
		//this.GettingInfoAboutBackGround();
	}
	
	
	
	
	
	
	public void GettingInfoAboutBackGround() {
		this.BackGroundContainer = IContainer.make();
		if (this.BackGroundContainer.open(DecoderMain.BackGroundPath, IContainer.Type.READ, null) < 0) {
			throw new IllegalArgumentException("could not open file: ");
		}
		int numOfStreams = this.BackGroundContainer.getNumStreams();
		System.out.println("numOfStreams" + numOfStreams);
		for (int i = 0; i < numOfStreams; i++) {
			IStream Is = this.BackGroundContainer.getStream(i);
			IStreamCoder isc = Is.getStreamCoder();
			if (isc.getCodecType() == ICodec.Type.CODEC_TYPE_AUDIO) {
				DecoderMain.BackGroundAudioStreamID = i;
				DecoderMain.BackGroundCoder = isc;

			}
		}
		if (DecoderMain.BackGroundAudioStreamID == -1) {
			throw new RuntimeException("could not find Audio stream in container: ");
		}
		if (DecoderMain.BackGroundCoder.open() < 0) {
			throw new RuntimeException("could not open video decoder for container: ");
		}

		DecoderMain.BackGroundSampleRate = DecoderMain.BackGroundCoder.getSampleRate();
	}


	public void Decoder() {

		System.out.println("--------in DM----");
		final IPacket packet = IPacket.make();
		while (this.container.readNextPacket(packet) >= 0) {
			if (packet.getStreamIndex() == videoStreamId) {
				picture = IVideoPicture.make(videoCoder.getPixelType(), videoCoder.getWidth(), videoCoder.getHeight());
				int offset = 0;
				while (offset < packet.getSize()) {

					int bytesDecoded = videoCoder.decodeVideo(picture, packet, offset);
					if (bytesDecoded < 0) {

						throw new RuntimeException("got error decoding video in: ");
					}
					offset += bytesDecoded;
					if (!picture.isComplete()) {
						continue;
					}
					IVideoPicture newPic = picture;
					if (newPic.getPts() < lastPts) {
						continue;
					}

					if (resampler != null) {
						newPic = IVideoPicture.make(resampler.getOutputPixelFormat(), picture.getWidth(),
								picture.getHeight());
						if (resampler.resample(newPic, picture) < 0) {
							System.out.println("--------in DM---ggg111-");
							throw new RuntimeException("could not resample video from: ");
						}
					}
					if (newPic.getPixelType() != IPixelFormat.Type.BGR24) {
						System.out.println("--------in DM---ggg111-");
						throw new RuntimeException("could not decode video as BGR 24 bit data in: ");
					}

					javaImage = Utils.videoPictureToImage(newPic);

					if (Engine.Index < Engine.marks.size() && Engine.marks.elementAt(Engine.Index) <= newPic.getPts()) {
						DecoderMain.lastPts = newPic.getPts();
						DecoderSub dsub = new DecoderSub();
						dsub.Decode(Engine.SubVideoPath.elementAt(Engine.Index));
						Engine.Index++;
						continue;
					}
					Engine.encoder.encode(javaImage, this.picture.getPts());

				}
			}
			if (packet.getStreamIndex() == AudioStreamID) {
				IAudioSamples samples = IAudioSamples.make(1024, AudioCoder.getChannels());
				int offset = 0;
				DecoderMain.SampleRate = AudioCoder.getSampleRate();

				while (offset < packet.getSize())

				{
					int bytesDecoded = AudioCoder.decodeAudio(samples, packet, offset);
					if (bytesDecoded < 0)
						throw new RuntimeException("got error decoding audio in: ");
					offset += bytesDecoded;
					if (!samples.isComplete()) {
						continue;
					}
				
					this.mainSamples.add(samples);
					
					//Engine.encoder.EncodeAudio(outputBuffer);
				}
			}
		}
		lastPts = this.mainSamples.elementAt(this.mainSamples.size() - 1).getPts();

	}

	
	public void DecodeBackGround() {
		
		System.out.println("Sampling Rate of MainAudio"+DecoderMain.SampleRate);
		System.out.println("Sampling Rate of BackAudio"+DecoderMain.BackGroundSampleRate);

		final IPacket packet = IPacket.make();

		while (this.BackGroundContainer.readNextPacket(packet) >= 0) {
			// System.out.println("Here in Decoder Background");

			if (packet.getStreamIndex() == DecoderMain.BackGroundAudioStreamID) {
				///////// -----------------------------to do-------------
//				IAudioSamples samples = IAudioSamples.make(1024, 1);
				IAudioSamples samples = IAudioSamples.make(1024, BackGroundCoder.getChannels());
				int offset = 0;
				while (offset < packet.getSize()) {

					int bytesDecoded = DecoderMain.BackGroundCoder.decodeAudio(samples, packet, offset);
					if (bytesDecoded < 0) {
						throw new RuntimeException("got error decoding audio in: ");
					}
					offset = offset + bytesDecoded;
					if (!samples.isComplete()) {
						continue;
					}
				
					if (samples.getPts() >lastPts) {
						System.out.println("---------------Limit exceded------ " + lastPts);
						return;
					}
					
					this.backSamples.add(samples);

				}
			}
		}

	}

	public void SynchronizeDecodedSamples() {
		int backsize = this.backSamples.size();
		int mainsize = this.mainSamples.size();
		int maxSize = Math.max(backsize, mainsize);

		for (int i = 0; i < maxSize; i++) {
			if (i >= backsize) {

				this.All.add(this.mainSamples.elementAt(i));

				continue;
			}

			if (i >= mainsize) {

				this.All.add(this.backSamples.elementAt(i));
				continue;
			}

			ByteBuffer BufferMain = this.mainSamples.elementAt(i).getByteBuffer();
			ByteBuffer BufferBack = this.backSamples.elementAt(i).getByteBuffer();
			mixer(BufferMain, BufferBack);
			// this.mainSamples.elementAt(i).setPts((this.mainSamples.elementAt(i).getPts()+this.backSamples.elementAt(i).getPts())/2);
			All.add(this.backSamples.elementAt(i));

		}
		for (int i = 0; i < this.All.size(); i++) {
			Engine.encoder.EncodeAudio(this.All.elementAt(i));
		}

	}

	public void mixer(ByteBuffer BufferMain, ByteBuffer BufferBack) {
		int backsize = BufferMain.limit();
		int mainsize = BufferBack.limit();
		int maxSize = Math.max(backsize, mainsize);
		for (int i = 0; i < maxSize; i++) {
			if (i >= backsize) {

				continue;
			}
			if (i >= mainsize) {

				continue;
			}

			int temp = (int) (BufferBack.get(i));
			BufferMain.put(i, (byte) (temp));
			BufferBack.put(i, (byte) (temp));

		}

	}

	public void mixer1(ByteBuffer BufferMain, ByteBuffer BufferBack) {

		int backsize = BufferMain.capacity();
		int mainsize = BufferBack.capacity();
		int maxSize = Math.max(backsize, mainsize);

		for (int i = 0; i < maxSize; i++) {
			if (i >= backsize) {

				continue;
			}
			if (i >= mainsize) {

				continue;
			}

			int temp = BufferMain.get(i) + (BufferBack.get(i));

			// System.out.print("temp------------- "+temp);

			BufferMain.put(i, (byte) (temp));
			BufferBack.put(i, (byte) (temp));

		}
		// System.out.println();

	}

	public void GiveToEncoder(Encoder encoder) {
		encoder.encode(javaImage, picture.getPts());
	}

}
