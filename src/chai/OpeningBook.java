package chai;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Random;

import chesspresso.game.Game;
import chesspresso.pgn.PGNReader;
import chesspresso.pgn.PGNSyntaxError;

public class OpeningBook {
	private String urlString;
	
	private Game[] openings;
	
	public OpeningBook(String url) {
		urlString = url;
		openings = new Game[120];
		
		readBook();
	}
	
	public void readBook() {
		URL url = this.getClass().getResource(urlString);
		
		File f;
		try {
			f = new File(url.toURI());
			FileInputStream fis = new FileInputStream(f);
			PGNReader pgnReader = new PGNReader(fis, urlString);
			
			for (int i = 0; i < 120; i++) {
				Game g = pgnReader.parseGame();
				g.gotoStart();
				openings[i] = g;
			}
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PGNSyntaxError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}
	
	public short getOpeningMove(int playerNum) {
		Random r = new Random();
		int randIdx = r.nextInt(121);
		
		// White (0) always goes first. If Black (1) move forward one and get next short move
		if (playerNum == 1) {
			openings[randIdx].goForward();
		}
		
		return openings[randIdx].getNextShortMove();
	}
}
