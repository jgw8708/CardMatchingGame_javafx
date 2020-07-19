package application;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class RootController implements Initializable {
	
	@FXML ImageView card0, card1,card2,card3,card4,card5, card6, card7, card8,
					card9, card10, card11, card12, card13, card14, card15;
	@FXML Label lbl_title;
	
	ImageView[][] image_obj = new ImageView[4][4];
	Card m_Card[][] = new Card[4][4];
	Card m_Shuffle1, m_Shuffle2;
	
	public static final int STATE_READY = 0;
	public static final int STATE_GAME = 1;
	public static final int STATE_END = 2;
	public int m_state = STATE_READY;
	int m_colum =3, m_row = 2;
	
	private Stage primaryStage;
	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}
	
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		image_obj[0][0] = card0;
		image_obj[1][0] = card1;
		image_obj[2][0] = card2;
		
		image_obj[0][1] = card3;
		image_obj[1][1] = card4;
		image_obj[2][1] = card5;

		
		Card_Shuffle(m_colum, m_row);
		OnDraw();
		CardGameThread thread = new CardGameThread(this);
		thread.start();
	}
	
	public void Card_Shuffle(int colum, int row) {
		
		for(int y=0;y<row;y++) {
			for(int x=0; x<colum; x++) {
				m_Card[x][y] = new Card((y*colum+x)/2);
			}
		}
		for (int i =0; i<100; i++) {
			int x = (int)(Math.random()*colum);
			int y = (int)(Math.random()*row);
			m_Shuffle1 = m_Card[x][y];
			
			x = (int)(Math.random()*colum);
			y = (int)(Math.random()*row);
			m_Shuffle2 = m_Card[x][y];
			int temp = m_Shuffle1.m_image;
			m_Shuffle1.m_image = m_Shuffle2.m_image;
			m_Shuffle2.m_image = temp;
		}
		
		m_Shuffle1 = null;
		m_Shuffle2 = null;
	}
	
	public void OnDraw() {
		if(m_state == STATE_READY) {
			All_Card_view();
		} else if (m_state == STATE_GAME) {
			for (int y =0; y<m_row; y++) {
				for(int x=0; x<m_colum ;x++) {
					if(m_Card[x][y].m_state == Card.CARD_CLOSE) {
						image_obj[x][y].setImage(new Image(getClass().getResource("images/card.jpg").toString()));
					} else {
						View_Front_Image(x,y);
					}
				}
			}
		}
	}
	
	public void All_Card_view() {
		for(int y = 0; y < m_row; y++) {
			for(int x = 0; x < m_colum; x++) {
				View_Front_Image(x,y);
			}
		}	
	}
	
	public void View_Front_Image(int x, int y) {
		switch(m_Card[x][y].m_image) {
		case Card.IMG_0: 
			image_obj[x][y].setImage( new Image (getClass().getResource("images/eight.jpg").toString()));
			break;
		case Card.IMG_1:
			image_obj[x][y].setImage( new Image (getClass().getResource("images/her.jpg").toString()));
			break;
		case Card.IMG_2:
			image_obj[x][y].setImage( new Image (getClass().getResource("images/mountain.jpg").toString()));
			break;
		}
	}
	
	public void onMouseClickedStart(MouseEvent event) {
		if(m_state == STATE_READY) {
			m_state = STATE_GAME;
			All_card_close();
			OnDraw();
		} else if(m_state == STATE_END) {
			lbl_title.setText("Game OVER");
			try {
				
				FXMLLoader loader = new FXMLLoader(getClass().getResource("next_stage.fxml"));
				Parent root = loader.load();
				
				Scene scene = new Scene(root);
				primaryStage.setScene(scene);
				primaryStage.show();
					
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void All_card_close() {
		for(int y = 0; y < m_row; y++) {
			for(int x = 0; x < m_colum; x++) {
				m_Card[x][y].m_state = Card.CARD_CLOSE;
			}
		}
	}
	
	public void OnMouseClickedImageView(MouseEvent event) {
		 for (int y=0;y<m_row;y++) {
			 for (int x=0;x<m_colum;x++) {
				 if (image_obj[x][y] == event.getSource()) {
					 if(m_Card[x][y].m_state == Card.CARD_CLOSE) {
						 m_Card[x][y].m_state = Card.CARD_PLAYEROPEN;
						 if(m_Shuffle1 == null) {
							 m_Shuffle1 = m_Card[x][y];
						 }else {
							 m_Shuffle2 = m_Card[x][y];
						 } 
					 OnDraw(); 
					 }
				return;
				} 
			 }
		 }
	}
	
	public void checkMatch() {
		if(m_Shuffle1 == null || m_Shuffle2 == null) 
			return;
		if(m_Shuffle1.m_image == m_Shuffle2.m_image) {
			m_Shuffle1.m_state = Card.CARD_MATCHED;
			m_Shuffle2.m_state = Card.CARD_MATCHED;
			m_Shuffle1 = null;
			m_Shuffle2 = null;
			 for (int y=0;y<m_row;y++) {
				 for (int x=0;x<m_colum;x++) {
					 if(m_Card[x][y].m_state != Card.CARD_MATCHED) {
						 OnDraw();
						 return;
					 }
				 }
			 }
			 m_state = STATE_END;
		} else {

			try {
				Thread.sleep(800);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			m_Shuffle1.m_state = Card.CARD_CLOSE;
			m_Shuffle2.m_state = Card.CARD_CLOSE;
			m_Shuffle1 = null;
			m_Shuffle2 = null;
			
		}
		OnDraw();
	}
	
}
