package hello;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.SmartLifecycle;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import hello.netty.ChatSeverHostPortHolder;
import hello.netty.NettyClient;

@Component
public class NettyConnectInit implements InitializingBean, SmartLifecycle {

	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;

	private boolean isRunning = false;

	private NettyClient client = null;

	@Override
	public void start() {
		isRunning = true;

		client = new NettyClient();

		client.connect(ChatSeverHostPortHolder.DEFAULT_SERVER_HOST, ChatSeverHostPortHolder.DEFUALT_SERVER_PORT, simpMessagingTemplate);

	}

	@Override
	public void stop() {
		isRunning = false;
	}

	@Override
	public boolean isRunning() {
		return isRunning;
	}

	@Override
	public int getPhase() {
		return 0;
	}

	@Override
	public boolean isAutoStartup() {
		return true;
	}

	@Override
	public void stop(Runnable callback) {
		client.shutdown();
		callback.run();
		isRunning = false;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(simpMessagingTemplate, "simpMessagingTemplate can not be null");
	}
}
