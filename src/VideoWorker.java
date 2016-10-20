package utility;

import javax.swing.SwingWorker;

public class VideoWorker extends SwingWorker<Void, Void> {
	
	private String command;
	private String reward;
	
	public VideoWorker(String rewardType) {
		switch(rewardType) {
		   case "Audio" :
			   command = "ffmpeg -i bgm.mp3 -i big_buck_bunny_1_minute.avi -shortest -c copy audioReward.avi";
			   reward = "audioReward.avi";
		      break; 
		   
		   case "Bonus" :
			   command = "ffmpeg -i big_buck_bunny_1_minute.avi -vf lutrgb=\"r=negval:g=negval:b=negval\" bonus_reward.avi ";
			   reward = "bonus_reward.avi";
		      break;
		}
	}
	
	protected Void doInBackground() throws Exception {
		BashCommand.bashCommand(command);
		return null;
	}   	
	
	protected void done(){
		@SuppressWarnings("unused")
		VideoPlayer video = new VideoPlayer(reward);
	}
	
}
