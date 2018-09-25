package hello.netty;

import java.util.List;

import com.alibaba.fastjson.JSON;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.util.CharsetUtil;

public class ChatBaseMessageCodec extends ByteToMessageCodec<ChatBaseMessage> {

	@Override
	protected void encode(ChannelHandlerContext ctx, ChatBaseMessage msg, ByteBuf out) throws Exception {
		try {
			// 版本位 8个字节
			out.writeDoubleLE(ChatVersion.VERSION);
			byte[] msgBytes = JSON.toJSONString(msg).getBytes(CharsetUtil.UTF_8);
			// 对象类型Id位 4个字节
			out.writeIntLE(MessageTypeIdRegistry.getMessageId(msg.getClass().getName()));
			// 有效数据的长度 4个字节
			out.writeIntLE(msgBytes.length);
			out.writeBytes(msgBytes);
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		if (in.readableBytes() >= 16) {
			// 设置标签位
			in.markReaderIndex();
			double chatVersion = in.readDoubleLE();
			if (ChatVersion.VERSION != chatVersion) {
				throw new RuntimeException("协议版本不一致,当前版本" + ChatVersion.VERSION + ",接收收据协议版本为 = " + chatVersion);
			}
			int messageTypeId = in.readIntLE();
			Class<?> type = MessageTypeIdRegistry.getType(messageTypeId);
			int length = in.readIntLE();
			int readableLength = in.readableBytes();
			if (readableLength < length) {
				// 恢复readerIndex到标签位置
				in.resetReaderIndex();
				return;
			}
			byte[] dst = new byte[length];
			in.readBytes(dst);
//          此处默认编码也是UTF-8			
//			out.add(JSON.parseObject(new String(dst), type));
			out.add(JSON.parseObject(dst, 0, length, CharsetUtil.UTF_8, type));		
		}
	}

}
