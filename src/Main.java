import java.net.InetSocketAddress;
import java.net.Socket;
import java.io.IOException;
public class Main {
	public static void main(String args[]) {
/**********************************************************************************************/
		so.prefix=args[0];
		so.sleep=500;
		final int PORT=9000;
		final String HOST="52.10.236.205";
		boolean wait=false;
		Socket loadBalancer=null;
		Packet packet;
		ResourceManager rm=new ResourceManager();
		while(true) {
			try {
				loadBalancer=new Socket();
				loadBalancer.connect(new InetSocketAddress(HOST,PORT));
				so.p("connected to load balancer");
				SocketIO socketIO=new SocketIO(loadBalancer);
				while(true) {
					packet=rm.getResourceUsage();
					Return r=socketIO.send(packet);
					if(r.type==Type.False) {
						so.p("connection to load balancer is lost");
						break;
					}
					r=socketIO.receive();
					if(r.type==Type.False) {
						so.p("connection to load balancer is lost");
						break;
					}
					if(r.packet.type==Type.Wait) {
						wait=true;
						so.p("Stopped");
					} else if(r.packet.type==Type.Ready) {
						wait=false;
						rm.addJob(r.packet);
					}
					try { Thread.sleep(so.sleep); } catch(InterruptedException iex) {}
				}
			} catch (IOException e) {
			} finally {
				if(loadBalancer!=null) try { loadBalancer.close(); } catch(IOException ioex) {}
			}
		}
/**********************************************************************************************/
	}
}