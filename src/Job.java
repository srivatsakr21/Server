public class Job extends Thread {
	private String dataSet;
	private ResourceManager rm;
	public Job(String dataSet, ResourceManager rm) {
		this.dataSet=dataSet;
		this.rm=rm;
	} 
	@Override
	public void run() {
		if(this.dataSet==null) return;
		for(int i=0;i<this.dataSet.length();++i) {
			try { Thread.sleep(2500); } catch(InterruptedException iex){}
		}
		this.rm.removeJob(this);
	}
}
