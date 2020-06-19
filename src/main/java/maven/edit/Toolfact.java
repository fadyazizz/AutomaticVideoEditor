package maven.edit;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.ToolFactory;

public class Toolfact {

	public static void main(String[] args) {
		IMediaReader reader = ToolFactory.makeReader("speak.mp4");
		reader.addListener(ToolFactory.makeViewer(true));
		// reader.addListener(ToolFactory.makeWriter("output.mpg", reader));
		 while (reader.readPacket() == null)
		   ;
	}

}
