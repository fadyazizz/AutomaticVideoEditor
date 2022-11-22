package maven.edit;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.LayoutManager;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.MediaListenerAdapter;
import com.xuggle.mediatool.ToolFactory;

public class GUI extends MediaListenerAdapter {
	JTextField MainVid;
	JTextField SubVids;
	JTextField Markers;
	JTextField output;
	JButton btMain;
	JButton btSub;
	JButton btMarkers;
	JButton show;
	JButton out;
	JButton edit;
	JFrame  frame;
	public GUI() {
		  frame=new JFrame("Input data");
		 frame.setLayout(new FlowLayout());
		 frame.setSize(500, 1000);
		  
			frame.setVisible(true);
			
			MainVid=new JTextField("Main Vid path (press MainOk to use default)");
			MainVid.setPreferredSize(new Dimension(250,20));;
			SubVids=new JTextField("sub Vids paths separated by a comma (press SubsOk to use default)");
			SubVids.setPreferredSize(new Dimension(250,20));
			Markers=new JTextField("timestamps separated by a comma");
			Markers.setPreferredSize(new Dimension(250,20));
			output=new JTextField("Output Path (press OutOk to use default)");
			output.setPreferredSize(new Dimension(250,20));
			// buttons
			btMain=new JButton("MainOK!");
			btSub=new JButton("SubsOK!");
			btMarkers=new JButton("MarkersOK!");
			show=new JButton("show");
			out=new JButton("OutOk!");
			edit=new JButton("Edit");
			frame.add(MainVid);
			frame.add(btMain);
			frame.add(SubVids);
			frame.add(btSub);
			frame.add(Markers);
			frame.add(btMarkers);
			frame.add(output);
			frame.add(out);
			frame.add(edit);
			frame.add(show);
			frame.revalidate();
	
	
	
	}
	
	
	
	}
	
	
	
	
	
	

	
	
	
	
	
	
	
	
	
	
	

