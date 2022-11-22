package maven.edit;

import com.xuggle.ferry.IBuffer;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IStream;

public class main {

	public static void main(String[] args) {
		  
		 IContainer container = IContainer.make();  
		 if (container.open("speak.mp4", IContainer.Type.READ, null) <0) 
		  
		   throw new RuntimeException("failed to open");  
		 int numStreams = container.getNumStreams();
		 int counter=0;
		 for(int i=0; i<numStreams;i++) {  
		   IStream stream = container.getStream(i);  
		     System.out.println(++counter+numStreams);
		 }  
		 IPacket packet = IPacket.make();  
		 
		 while(container.readNextPacket(packet) >= 0)  
		 {  
		     IBuffer b=packet.getData();
		   // System.out.println(b);
		 }  
		 container.close();
	}

}
