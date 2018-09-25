package hello.netty;

import java.util.Date;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.util.HtmlUtils;

import hello.Greeting;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ClientHandler extends SimpleChannelInboundHandler<ChatBaseMessage> {

	
	private SimpMessagingTemplate simpMessagingTemplate;
	
	
	
	public ClientHandler(SimpMessagingTemplate simpMessagingTemplate) {
		super();
		this.simpMessagingTemplate = simpMessagingTemplate;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ChatBaseMessage msg) throws Exception {
		if(!HeartBeatMessage.class.isInstance(msg)) {
			System.out.println(msg);
			ChatMessage message = (ChatMessage) msg;
			Greeting greeting =  new Greeting(message.getMessage());
			if(simpMessagingTemplate != null) {
				simpMessagingTemplate.convertAndSend("/topic/greetings", greeting);
			}
		}
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		NettyChannelHolder.addChannel(ctx.channel());
		super.channelActive(ctx);
	}  
	
	
}
