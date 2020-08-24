package com.example.demo.line.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.keys.ImagesURL;
import com.example.demo.keys.LineKeys;
import com.example.demo.line.action.entity.LocationAction;
import com.example.demo.line.action.entity.MessageAction;
import com.example.demo.line.action.entity.PostBackAction;
import com.example.demo.line.action.entity.QuickReplyAction;
import com.example.demo.line.message.entity.FlexMessage;
import com.example.demo.line.message.entity.Message;
import com.example.demo.line.message.entity.QuickReply;
import com.example.demo.line.message.entity.QuickReplyMessage;
import com.example.demo.line.message.entity.Reply;
import com.example.demo.line.message.entity.TextMessage;
import com.example.demo.line.util.JsonParserUtil;
import com.example.demo.line.util.SendMessageUtil;

@Service
public class ReplyService implements LineKeys,ImagesURL {

	private static final Logger LOG = LoggerFactory.getLogger(ReplyService.class);

	@Autowired
	private SendMessageUtil sendMessageUtil;
	@Autowired
	private JsonParserUtil jsonParserUtil;

	// show spring init components and other tags at starting server
	{
		LOG.info("init :\t" + this.getClass().getSimpleName());
	}

	// send back one
	// message.length must smaller than 3
	public void sendResponseMessage(String replyToken, String... messages) {

		String uuid = UUID.randomUUID().toString();

		Reply reply = new Reply();

		List<Message> messagesList = new ArrayList<Message>();

		TextMessage textMessage;

		for (String message : messages) {
			textMessage = new TextMessage();
			textMessage.setType("text");
			textMessage.setText(message);
			messagesList.add(textMessage);
		}

		reply.setReplyToken(replyToken);
		reply.setMessages(messagesList);

		String jsonData = jsonParserUtil.jsonToString(reply);

		System.out.println(jsonData);

		boolean isDone = sendMessageUtil.sendReplyMessage(uuid, jsonData);

		if (!isDone) {
			replyFailedHashMap.put(uuid, jsonData);
		}

		System.out.println(isDone == true ? "成功回復" : "回復失敗");

	}

	// one flex
	public void sendResponseMessage_flex(String replyToken, FlexMessage flexMessage) {

		String uuid = UUID.randomUUID().toString();

		Reply reply = new Reply();

		List<Message> messageList = new ArrayList<Message>();

		messageList.add(flexMessage);

		reply.setReplyToken(replyToken);
		reply.setMessages(messageList);

		String jsonData = jsonParserUtil.jsonToString(reply);

		System.out.println(jsonData);

		boolean isDone = sendMessageUtil.sendReplyMessage(uuid, jsonData);

		if (!isDone) {
			replyFailedHashMap.put(uuid, jsonData);
		}

		System.out.println(isDone == true ? "成功回復" : "回復失敗");
	}

	public void sendQuickReply(String replyToken) {
		
		String uuid = UUID.randomUUID().toString();

		List<Message> messageList = new ArrayList<Message>();

		List<QuickReplyAction> actionList = new ArrayList<QuickReplyAction>();
		
		TextMessage textMessage = new TextMessage();
		textMessage.setType("text");
		textMessage.setText("test");
		messageList.add(textMessage);

		actionList.add(new QuickReplyAction("action",new PostBackAction("postback","post test","data=123","TEST")));
		actionList.add(new QuickReplyAction("action",DOGE_URL,new MessageAction("message","doge","testMessage")));
		actionList.add(new QuickReplyAction("action",LOGO_URL,new MessageAction("message","logo","testLogo")));
		actionList.add(new QuickReplyAction("action",new LocationAction("location","Send location")));
		
		QuickReplyMessage quickReplyMessage = new QuickReplyMessage("text","Select one",new QuickReply(actionList));
		messageList.add(quickReplyMessage);
		
		Reply reply = new Reply(replyToken,messageList);

		String jsonData = jsonParserUtil.jsonToString(reply);

		System.out.println(jsonData);

		boolean isDone = sendMessageUtil.sendReplyMessage(uuid, jsonData);

		if (!isDone) {
			pushFailedHashMap.put(uuid, jsonData);
		}

		System.out.println(isDone == true ? "成功發送" : "發送失敗");
	}

}