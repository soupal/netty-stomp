package hello;

import java.util.Date;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

import hello.netty.ChatMessage;
import hello.netty.NettyChannelHolder;
import io.netty.channel.Channel;

@Controller
public class GreetingController {

	@MessageMapping("/hello")
//	@SendTo("/topic/greetings")
	public void greeting(HelloMessage message) throws Exception {
		Thread.sleep(1000); // simulated delay
		Channel channel = NettyChannelHolder.getChannel();
		if (channel != null) {
			
			ChatMessage chatmessage = new ChatMessage();
			chatmessage.setUserId(100000L);
			chatmessage.setUserName("无可奉告");
			chatmessage.setMessage(message.getName());
			chatmessage.setSendTime(new Date());
			
			channel.writeAndFlush(chatmessage);
		}
//		return new Greeting("you :" + HtmlUtils.htmlEscape(message.getName()) + "!");
	}

}
