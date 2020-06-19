package maven.edit;

import java.util.Vector;



public class Engine {

	static Encoder encoder;
	static String MainVideoPath;
	static Vector<String> SubVideoPath;
	static Vector<Integer> marks;
	static String output;
	
	static int Index=0;
	
	
	public Engine(String MainVideoPath,Vector<String> SubVideoPath,Vector<Integer> marks,String output ) {
		//encoder=new Encoder(output);
		Engine.MainVideoPath=MainVideoPath;
		Engine.SubVideoPath=SubVideoPath;
		Engine.marks=marks;
		Engine.output=output;
		
	}
	
	
	public void Run(String Output) {
		DecoderMain dm=new DecoderMain(MainVideoPath);
		encoder=new Encoder(Output);
		dm.Decoder();
		//dm.DecodeBackGround();
		//AudioMixer AMixer=new AudioMixer(dm.mainSamples);
		//AMixer.Resample();
		//AMixer.tagroba();
		//AMixer.mixer();
		for(int i=0;i<dm.mainSamples.size();i++) {
			encoder.EncodeAudio(dm.mainSamples.elementAt(i));
		}
		
		Engine.encoder.close();
		//System.exit(0);
		

	}

	
	
	
	
	
	
}
