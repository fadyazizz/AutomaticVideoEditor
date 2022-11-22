package maven.edit;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.ICodec.ID;

public class Encoder {

	IMediaWriter writer;
	Dimension dimen;
	static long lastAudioPts;
	static Vector<Long> mainAudiopts = new Vector<Long>(1, 1);
	static int mainAudioIndex = 0;
	static int streamIndex;
	static boolean writeOrNotMain = false;
	static boolean writeOrNotBack = true;

	public Encoder(String path) {
		writer = ToolFactory.makeWriter(path);

		System.out.println("---------Will You InterLeave????? " + writer.willForceInterleave());
		writer.setForceInterleave(true);
		System.out.println("---------Will You InterLeave????? " + writer.willForceInterleave());
		writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_MPEG4, DecoderMain.dimen.width, DecoderMain.dimen.height);
		streamIndex = writer.addAudioStream(1, 1, ICodec.ID.CODEC_ID_AAC, 2, DecoderMain.SampleRate);
	}

	public void addBackgroundStream() {
		this.writer.addAudioStream(1, 1, ICodec.ID.CODEC_ID_AAC, 2, DecoderMain.BackGroundSampleRate);
	}

	public void close() {
		this.writer.close();
	}

	public void encode(BufferedImage image, long pts) {
		//System.out.println("--------------I am in Encode-------- " + pts);
		if (pts == 0) {
			this.dimen = new Dimension(image.getWidth(), image.getHeight());
			// System.out.println("--------------I am in If---aaas-----");

		}

		// lastFramePts=pts;
//	if(writeOrNotMain) {
//		return;
//	}
		writer.encodeVideo(0, image, pts, TimeUnit.MICROSECONDS);
//	

	}

	public void EncodeAudio(IAudioSamples sample) {
		if (sample.getPts() == 0) {
			System.out.println("------EncodeAudio----");

			System.out.println("------samplerate----" + sample.getSampleRate());

			System.out.println("------StreamIndex----" + streamIndex);
		}

		// System.out.println("------EncodeAudio1----"+sample.getPts());
		// lastFramePts=sample.getPts();
		// mainAudiopts.add(sample.getPts());

		writer.encodeAudio(1, sample);
	}

	public void EncodeBackGround(IAudioSamples sample) {

		System.out.println("----------------EncoderBackGround---------- " + sample.getPts());
		// sample.setPts(Encoder.mainAudiopts.get(streamIndex++));
//	writeOrNotBack=!writeOrNotBack;
//	if(writeOrNotBack) {
//		return;
//	}

		writer.encodeAudio(1, sample);

	}

}
