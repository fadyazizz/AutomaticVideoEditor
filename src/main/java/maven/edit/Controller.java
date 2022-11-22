package maven.edit;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.IMediaViewer;
import com.xuggle.mediatool.MediaListenerAdapter;
import com.xuggle.mediatool.ToolFactory;

public class Controller extends MediaListenerAdapter implements ActionListener {
	GUI gn;
	Engine en;
	String MainVidPath;
	Vector<String> sub;
	Vector<Integer> markers;
	String out;
	boolean show=false;
	public Controller() {
		 gn =new GUI();
		//gn.jt.addActionListener(this);
		gn.btMain.addActionListener(this);
		gn.btSub.addActionListener(this);
		gn.btMarkers.addActionListener(this);
		gn.show.addActionListener(this);
		gn.out.addActionListener(this);
		gn.edit.addActionListener(this);
	}
	
public static void main(String[] args) {
	Controller con=new Controller();
	
}

public void actionPerformed(ActionEvent e) {
	if(e.getActionCommand().equals("MainOK!")) {
		String mainVideoPath = gn.MainVid.getText();
		this.MainVidPath= mainVideoPath.equals("Main Vid path (press MainOk to use default)") ? "resources/jp_t.mp4" : mainVideoPath;
	}
	if(e.getActionCommand().equals("SubsOK!")) {
	
		String[] paths;
		String subVideosPaths = this.gn.SubVids.getText();
		this.sub=new Vector<String>(1,1);
		if(subVideosPaths.equals("sub Vids paths separated by a comma (press SubsOk to use default)")) {
			subVideosPaths = "resources/island.mp4,resources/ocean.mp4";
		}
		
				
		paths = subVideosPaths.split(","); 
		
		for(int i=0;i<paths.length;i++) {
			this.sub.add(paths[i]);
		}
		System.out.println(this.sub.size()+" videos are added");
	}
	if(e.getActionCommand().equals("MarkersOK!")) {
		String[] paths;
		paths=this.gn.Markers.getText().split(",");
		this.markers=new Vector<Integer>(1,1);
		for(int i=0;i<paths.length;i++) {
			//time is in microseconds
			this.markers.add(Integer.parseInt(paths[i])*1000000);
		}
		System.out.println(this.markers.size()+" mareker(s) is/are added");

		
	}
	if(e.getActionCommand().equals("show")) {
		this.show=true;
		 //this.gn.show.disable();
		//this.gn.show.setEnabled(false);
		
		
	}
	
	if(e.getActionCommand().equals("OutOk!")) {
		
		this.out=this.gn.output.getText();
		if(this.out.equals("Output Path (press OutOk to use default)")) {
			System.out.println("using default output path");
			this.out = "resources/out.mp4";
		}
	}
	if(e.getActionCommand().equals("Edit")){
		this.gn.edit.disable();
		en=new Engine(this.MainVidPath,this.sub,this.markers, this.out);
		en.Run(this.out);
	}
	
	}





public void show() {
	 IMediaReader reader = ToolFactory.makeReader(this.MainVidPath);
	 IMediaViewer c=ToolFactory.makeViewer(true);

	    reader.addListener(c);
	    this.gn.frame.revalidate();
	    
	    while(reader.readPacket() == null );
	    
}

}
	



