/**
 * Re-implement the window to respond the predefined keys
 * 
 * @author kevin
 * 
 */
import java.awt.Container;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JFrame;

public class MyFrame extends JFrame{
	public boolean start;
	public boolean again;
	public boolean recogStart = false;
	public boolean uploadStart  = false;
	public boolean uploaded  = false;
	public int perNum = 0;
	public int picNum = 0;
	
	/**
	 * Constructor to the class
	 */
	public MyFrame(){
		setFocusable(true);
		addKeyListener(new KeyAdapter(){
			public void keyTyped(KeyEvent e){
				char c = e.getKeyChar();
				if(c == 'a'){
					again = true;
					System.out.println("A is pressed");
					picNum =0;
					perNum ++;
				}
				if(c=='s'){
					start =true;
					//perNum++;
					System.out.println("S is pressed");
					picNum = 0;
					perNum = 0;
					again = false;
					uploadStart = false;
					uploaded = false;
					recogStart = false;
				}
				if(c =='r'){
					System.out.println("R is pressed");
					recogStart = !recogStart;
				}
				if(c =='u'){
					System.out.println("U is pressed");
					uploadStart = true;
				}
			}
		});
	}
}