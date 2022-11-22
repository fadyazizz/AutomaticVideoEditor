package maven.edit;

import java.util.Vector;

import com.xuggle.xuggler.IAudioSamples;

public class AudioSynchronizer {

	Vector<IAudioSamples> mainSamples;
	Vector<IAudioSamples> backSamples;
	static Vector<IAudioSamples> All = new Vector<IAudioSamples>(1, 1);

	public AudioSynchronizer(Vector<IAudioSamples> mainSamples, Vector<IAudioSamples> backSamples) {

		this.mainSamples = mainSamples;
		this.backSamples = backSamples;

	}

	public void synchronize1() {

	}

	public void SendToEncoder() {
		this.merger();
		for (int i = 0; i < All.size(); i++) {
			Engine.encoder.EncodeAudio(All.elementAt(i));
		}
	}

	public void merger() {

		for (int i = 0; i < backSamples.size(); i++) {
			int Backsample = 0;
			int mainsample = 0;
			int j;
			for (j = 0; j < backSamples.elementAt(i).getNumSamples()
					|| j < mainSamples.elementAt(i).getNumSamples(); j++) {
				Backsample += backSamples.elementAt(i).getSample(j, 0, IAudioSamples.Format.FMT_S16);
				mainsample += mainSamples.elementAt(i).getSample(j, 0, IAudioSamples.Format.FMT_S16);
				System.out.println("---------------Backsample------------------  " + Backsample);
				System.out.println("---------------mainsample------------------  " + mainsample);

			}
			IAudioSamples mergedAudioSamples = IAudioSamples.make(1024, DecoderMain.BackGroundCoder.getChannels());
			mergedAudioSamples.setSample(j, 0, IAudioSamples.Format.FMT_S16, Backsample + mainsample);
			mergedAudioSamples.setPts(mainSamples.elementAt(i).getPts());
			All.add(mergedAudioSamples);
		}
	}

	public void synchonize() {

		System.out.println("---------------Synching------------");
		while (true) {

			if (this.mainSamples.size() == 0) {
				int num = this.backSamples.size();
				for (int i = 0; i < num; i++) {
//				All.add(this.backSamples.remove(0));
					Engine.encoder.EncodeBackGround(this.backSamples.remove(0));

				}
				return;
			}
			if (this.backSamples.size() == 0) {
				int num = this.mainSamples.size();
				for (int i = 0; i < num; i++) {
//				All.add(this.mainSamples.remove(0));
					Engine.encoder.EncodeAudio(this.mainSamples.remove(0));
				}
				return;
			}

			if (this.mainSamples.elementAt(0).getPts() < this.backSamples.elementAt(0).getPts()) {
				int num = this.mainSamples.size();
				for (int i = 0; i < num
						&& this.mainSamples.elementAt(0).getPts() < this.backSamples.elementAt(0).getPts(); i++) {
//				All.add(this.mainSamples.remove(0));
					Engine.encoder.EncodeAudio(this.mainSamples.remove(0));
				}
				continue;

			}
			if (this.mainSamples.elementAt(0).getPts() > this.backSamples.elementAt(0).getPts()) {
				int num = this.backSamples.size();
				for (int i = 0; i < num
						&& this.mainSamples.elementAt(0).getPts() > this.backSamples.elementAt(0).getPts(); i++) {
//				All.add(this.backSamples.remove(0));
					Engine.encoder.EncodeBackGround(this.backSamples.remove(0));
				}
				continue;

			}
			if (this.mainSamples.elementAt(0).getPts() == this.backSamples.elementAt(0).getPts()) {
				IAudioSamples temp = this.mainSamples.remove(0);
				Engine.encoder.EncodeAudio(temp);
				IAudioSamples temp2 = this.backSamples.remove(0);
				temp2.setPts(temp.getPts() + 400000);
				Engine.encoder.EncodeBackGround((temp2));
//			All.add(temp);
//			All.add(temp2);
			}

		}

	}

}
