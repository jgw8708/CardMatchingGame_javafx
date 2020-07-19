package application;

public class NextCardGameThread extends Thread {
	
	NextController next_Controller;
	
	public NextCardGameThread(NextController controller) {
		super();
		next_Controller =controller;
	}
	
	public void run() {
		while(true) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			next_Controller.checkMatch();
		}
	}
}
