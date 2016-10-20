package utility;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class DeleteGeneratedReward {

	public static void delete(){
		File audioReward = new File("audioReward.avi");
		File bonus_reward = new File("bonus_reward.avi");

			try {
				Files.deleteIfExists(bonus_reward.toPath());
				Files.deleteIfExists(audioReward.toPath());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	}
}
