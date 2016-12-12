import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
public class ResourceManager {
	private static final FileSystem fs=FileSystems.getDefault();
	private static final Path pathToCpu=fs.getPath("cpu.txt");
	private static final Path pathToMemory=fs.getPath("memory.txt");
	private ArrayList<Job> jobs;
	public ResourceManager() {
		this.jobs=new ArrayList<>();
	}
	public void addJob(Packet packet) {
		if(packet.dataSet==null||packet.dataSet.length()==0||packet.dataSet.isEmpty()) {
			so.p(".");
			return;
		}
		so.p(packet.dataSet.length());
		Job job=new Job(packet.dataSet,this);
		synchronized(this.jobs) {
			this.jobs.add(job);
			job.start();
		}
	}
	public void removeJob(Job job) {
		synchronized(this.jobs) {
			this.jobs.remove(job);
		}
	}
	public Packet getResourceUsage() {
		Packet packet=new Packet(so.prefix,0,0,0,Type.Server);
		synchronized(this.jobs) {
			packet.jobs=this.jobs.size();
		}
		try {
			Process process=Runtime.getRuntime().exec("./bm");
			try(BufferedReader in=new BufferedReader(new InputStreamReader(Files.newInputStream(pathToCpu)))) {
				float cpu=Float.parseFloat(in.readLine());
				if(this.jobs.size()>0)
					packet.cpu=cpu+cpu*(this.jobs.size()*3.5f);
				else
					packet.cpu=cpu;
			} catch(IOException ioex) {
				ioex.printStackTrace();
			}
			try(BufferedReader in=new BufferedReader(new InputStreamReader(Files.newInputStream(pathToMemory)))) {
				float memory=Float.parseFloat(in.readLine());
				if(this.jobs.size()>0)
					packet.memory=(100-memory)+(this.jobs.size()*2.0f);
				else
					packet.memory=(100-memory);
			} catch(IOException ioex) {
				ioex.printStackTrace();
			}
		} catch(IOException ioex) {
			ioex.printStackTrace();
		} finally {
			return packet;
		}
	}

}