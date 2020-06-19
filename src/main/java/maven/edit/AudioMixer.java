package maven.edit;

import java.nio.ByteBuffer;
import java.util.Vector;

import com.xuggle.ferry.IBuffer;
import com.xuggle.xuggler.IAudioResampler;
import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.IAudioSamples.Format;

public class AudioMixer {
	Vector<IAudioSamples> main;
	Vector<IAudioSamples> back;
	static Vector<IAudioSamples> All = new Vector<IAudioSamples>(1, 1);

	public AudioMixer(Vector<IAudioSamples> main, Vector<IAudioSamples> back) {
		this.main = main;
		this.back = back;
	}

	public void Resample() {
		int mainRate = this.main.elementAt(0).getSampleRate();
		int backRate = this.back.elementAt(0).getSampleRate();
		System.out.println("mainRate" + mainRate);
		System.out.println("BackRate" + backRate);
		Vector<IAudioSamples> temp = new Vector<IAudioSamples>(1, 1);
		if (mainRate > backRate) {
			System.out.println("main>back");
			IAudioResampler AudioResampler = IAudioResampler.make(2, 2, DecoderMain.BackGroundSampleRate,
					DecoderMain.SampleRate);
			for (int i = 0; i < this.main.size(); i++) {
				IAudioSamples samples = this.main.elementAt(i);
				IAudioSamples outputBuffer = IAudioSamples.make(1024, samples.getChannels());
				AudioResampler.resample(outputBuffer, samples, 1024);
				
				temp.add(outputBuffer);
			}
			this.main = temp;
		}
		if (mainRate < backRate) {
			System.out.println("main<back");
			IAudioResampler AudioResampler = IAudioResampler.make(2, 2, DecoderMain.SampleRate,
					DecoderMain.BackGroundSampleRate);
			for (int i = 0; i < this.back.size(); i++) {
				IAudioSamples samples = this.back.elementAt(i);
				IAudioSamples outputBuffer = IAudioSamples.make(1024, samples.getChannels());
				AudioResampler.resample(outputBuffer, samples, 1024);
				temp.add(outputBuffer);
			}
			this.back = temp;
		}
		
	}

	public void mixer() {
		while (!(this.back.isEmpty() || this.main.isEmpty())) {
			long mainpts = this.main.elementAt(0).getPts();
			long backpts = this.back.elementAt(0).getPts();
			//System.out.println(mainpts + " " + backpts);
			if (mainpts - backpts > 100000 || backpts - mainpts > 100000) {
				//System.out.println("I am here");
				if (mainpts < backpts) {

					All.add(this.main.remove(0));

				} else {
					
					
					
					All.add(this.back.remove(0));
				}
			} else {
				
				//this.mixer(this.main.elementAt(0).getByteBuffer(), this.back.elementAt(0).getByteBuffer());
				this.mixerChannel1(this.main.elementAt(0), back.elementAt(0));
				//this.main.elementAt(0).setPts((this.main.elementAt(0).getPts() + this.back.elementAt(0).getPts()) / 2);
				All.add(this.main.remove(0));
				this.back.remove(0);
				
			}
		}
	
		if (!this.back.isEmpty()) {
			for (int i = 0; i < this.back.size(); i++) {
				All.add(this.back.elementAt(i));
			}
		}
		if (!this.main.isEmpty()) {
			for (int i = 0; i < this.main.size(); i++) {
				All.add(this.main.elementAt(i));
			}
		}
	}
	
	public void mixer(ByteBuffer BufferMain, ByteBuffer BufferBack) {
		//System.out.println(BufferMain+"    "+BufferBack);
		int backsize = BufferBack.limit();

		// BufferMain.array();
		int mainsize = BufferMain.limit();
		int maxSize = Math.max(backsize, mainsize);
		for (int i = 0; i < maxSize; i++) {
			if (i >= backsize) {

				continue;
			}
			if (i >= mainsize) {

				continue;
			}

			int temp = (int) (BufferBack.get(i) + BufferMain.get(i));
			BufferMain.put(i, (byte) (temp));
			BufferBack.put(i, (byte) (temp));

		}

	}
	public void mixerChannel1(IAudioSamples main,IAudioSamples back) {
		long backsize = back.getNumSamples();

		// BufferMain.array();
		long mainsize = main.getNumSamples();
		long minSize = Math.min(backsize, mainsize);
		for(int i=0;i<minSize;i++) {
			int samplemain=main.getSample(i, 0, Format.FMT_S16);
			int sampleback=back.getSample(i, 0, Format.FMT_S16);
			int avg=(sampleback/2+samplemain*3)/2;
			back.setSample(i, 0, Format.FMT_S16, avg);
			main.setSample(i, 0, Format.FMT_S16, avg);
		}
	}

	public void tagroba() {
		int i = 0;
		int counter = 0;
		int counter1 = 0;
		for (i = 0; i < this.main.size(); i++) {
			//System.out.println("mainSamples Rate" + this.main.elementAt(i).getNumSamples());
			if (this.main.elementAt(i).getNumSamples() == 940) {
				counter++;
			}
		}
		//System.out.println("-------------------------------- " + i);
		for (i = 0; i < this.back.size(); i++) {
			System.out.println("BackSamples Rate" + this.back.elementAt(i).getNumSamples());

		}

		System.out.println("-------------------------------- " + i);
		System.out.println("-------------------------------- " + counter);
	}

}
