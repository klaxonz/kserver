package com.klaxon.kserver.module.spider.telegram;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import com.klaxon.kserver.module.spider.mapper.DriveShareMapper;
import com.klaxon.kserver.module.spider.mapper.DriveShareReferTelegramMapper;
import com.klaxon.kserver.module.spider.mapper.ext.DriveShareReferTelegramMapperExt;
import com.klaxon.kserver.module.spider.model.entity.DriveShare;
import com.klaxon.kserver.module.spider.model.entity.DriveShareReferTelegram;
import it.tdlight.Init;
import it.tdlight.Log;
import it.tdlight.Slf4JLogMessageHandler;
import it.tdlight.client.APIToken;
import it.tdlight.client.AuthenticationSupplier;
import it.tdlight.client.SimpleAuthenticationSupplier;
import it.tdlight.client.SimpleTelegramClient;
import it.tdlight.client.SimpleTelegramClientBuilder;
import it.tdlight.client.SimpleTelegramClientFactory;
import it.tdlight.client.TDLibSettings;
import it.tdlight.jni.TdApi;
import it.tdlight.jni.TdApi.MessageContent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Example class for TDLight Java
 * <p>
 * <a href="https://tdlight-team.github.io/tdlight-docs">The documentation of the TDLight functions can be found here</a>
 */
@Slf4j
@Component
public class TelegramSpider {

    @Resource
    private DriveShareMapper driveShareMapper;
    @Resource
    private DriveShareReferTelegramMapper driveShareReferTelegramMapper;
    @Resource
    private DriveShareReferTelegramMapperExt driveShareReferTelegramMapperExt;

    private ExampleApp app;
    private static final String phoneNumber = "12546777229";
    private static final Integer apiId = 3606839;
    private static final String apiHash = "058bab946d8d080c45b687aab06590e1";
    private final List<String> channels = Lists.newArrayList(
            "Aliyun_4K_Movies",
            "XiangxiuNB",
            "shareAliyun",
            "dianying4K",
            "wangpanziyuan2021",
            "alyp_1",
            "Remux_2160P",
            "alyp_4K_Movies",
            "alyp_JLP",
            "Aliyundrive_Share_Channel",
            "Q66Share",
            "ikiviyyp",
            "aliyunys",
            "al_cloud"
    );
    private final List<Long> chatIds = Lists.newArrayList();
    private final List<Pattern> patterns = new ArrayList<>();

    public void run(boolean isIncrement) throws Exception {

        // Initialize TDLight native libraries
        Init.init();

        // Initialize regex pattern
        initPattern();

        // Set the log level
        Log.setLogMessageHandler(1, new Slf4JLogMessageHandler());

        // Create the client factory (You can create more than one client,
        // BUT only a single instance of ClientFactory is allowed globally!
        // You must reuse it if you want to create more than one client!)
        try (SimpleTelegramClientFactory clientFactory = new SimpleTelegramClientFactory()) {

            APIToken apiToken = new APIToken(apiId, apiHash);

            // Configure the client
            TDLibSettings settings = TDLibSettings.create(apiToken);

            // Configure the session directory.
            // After you authenticate into a session, the authentication will be skipped from the next restart!
            // If you want to ensure to match the authentication supplier user/bot with your session user/bot,
            //   you can name your session directory after your user id, for example: "tdlib-session-id12345"
            Path sessionPath = Paths.get("example-tdlight-session");
            settings.setDatabaseDirectoryPath(sessionPath.resolve("data"));
            settings.setDownloadedFilesDirectoryPath(sessionPath.resolve("downloads"));

            // Prepare a new client builder
            SimpleTelegramClientBuilder clientBuilder = clientFactory.builder(settings);

            // Configure the authentication info
            // Replace with AuthenticationSupplier.consoleLogin(), or .user(xxx), or .bot(xxx);
            SimpleAuthenticationSupplier<?> authenticationData = AuthenticationSupplier.user(phoneNumber);
            // This is an example, remove this line to use the real telegram datacenters!
            // settings.setUseTestDatacenter(true);

            // Create and start the client
            try {
                app = new ExampleApp(clientBuilder, authenticationData);

                // Get Chat list
                getChatInfo();

                // Get Chat Message
                getChatMessage(isIncrement);

            } catch (Exception e) {
                log.info("Telegram spider run failed", e);
            }
        }
        log.info("Telegram spider run success");
    }

    private void getChatInfo() throws ExecutionException, InterruptedException, TimeoutException {
        // Get Chats
        TdApi.GetChats getChatsReq = new TdApi.GetChats(new TdApi.ChatListMain(), 2000);
        TdApi.Chats chats = app.getClient().send(getChatsReq).get(1, TimeUnit.MINUTES);
        new TdApi.GetSupergroup();

        // Get Chat
        for (int i = 0; i < chats.chatIds.length; i++) {
            TdApi.GetChat getChatReq = new TdApi.GetChat(chats.chatIds[i]);
            TdApi.Chat chat = app.getClient().send(getChatReq).get(1, TimeUnit.MINUTES);
            if (chat.type instanceof TdApi.ChatTypeSupergroup) {
                long supergroupId = ((TdApi.ChatTypeSupergroup) chat.type).supergroupId;
                boolean isChannel = ((TdApi.ChatTypeSupergroup) chat.type).isChannel;
                if (isChannel) {
                    TdApi.GetSupergroup getSupergroupReq = new TdApi.GetSupergroup(supergroupId);
                    TdApi.Supergroup supergroup = app.getClient().send(getSupergroupReq).get(1, TimeUnit.MINUTES);
                    TdApi.Usernames usernames = supergroup.usernames;
                    if (Objects.nonNull(usernames)) {
                        if (channels.contains(usernames.editableUsername)) {
                            chatIds.add(chat.id);
                        }
                    }
                }
            }
        }
    }

    private void initPattern() {
        List<String> rePatterns = Lists.newArrayList(
                "https://www.aliyundrive.com/s/[a-zA-Z0-9]+",
                "https://www.alipan.com/s/[a-zA-Z0-9]+"
        );
        for (String re : rePatterns) {
            Pattern pattern = Pattern.compile(re);
            patterns.add(pattern);
        }
    }

    private void getChatMessage(boolean isIncrement) throws InterruptedException, ExecutionException, TimeoutException {
        for (Long chatId : chatIds) {
            long lastMessageId = 0L;

            Long maxDate = driveShareReferTelegramMapperExt.selectMaxMessageDateByChatId(chatId);
            if (Objects.isNull(maxDate)) {
                maxDate = 0L;
            }

            boolean isFinished = false;
            TdApi.Messages messages;
            do {
                TdApi.GetChatHistory getChatHistory = new TdApi.GetChatHistory(chatId, lastMessageId, 0, 50, false);
                messages = app.getClient().send(getChatHistory).get(1, TimeUnit.MINUTES);

                for (TdApi.Message message : messages.messages) {
                    if (isIncrement && message.date < maxDate.intValue()) {
                        isFinished = true;
                        break;
                    }
                    saveMessage(chatId, message);
                    lastMessageId = message.id;
                }
                Thread.sleep(1000);
            } while (!isFinished && messages.totalCount > 0);
        }
    }

    private void saveMessage(Long chatId, TdApi.Message message) {
        if (Objects.nonNull(message.content)) {
            String text = getMessageText(message);
            if (StringUtils.isNotBlank(text)) {
                for (Pattern pattern : patterns) {
                    Matcher matcher = pattern.matcher(text);
                    while (matcher.find()) {
                        String url = matcher.group();

                        DriveShare driveShare = driveShareMapper.selectOne(new LambdaQueryWrapper<DriveShare>()
                                .eq(DriveShare::getShareUrl, url)
                                .eq(DriveShare::getDriveType, 1));
                        if (Objects.isNull(driveShare)) {
                            driveShare = new DriveShare();
                            driveShare.setDriveType(1);
                            driveShare.setShareUrl(url);
                            driveShareMapper.insert(driveShare);
                        }

                        DriveShareReferTelegram driveShareReferTelegram = driveShareReferTelegramMapper.selectOne(new LambdaQueryWrapper<DriveShareReferTelegram>()
                                .eq(DriveShareReferTelegram::getChatId, chatId)
                                .eq(DriveShareReferTelegram::getMessageId, message.id)
                                .eq(DriveShareReferTelegram::getShareUrl, url)
                                .eq(DriveShareReferTelegram::getDriveType, 1)
                        );
                        if (Objects.isNull(driveShareReferTelegram)) {
                            driveShareReferTelegram = new DriveShareReferTelegram();
                            driveShareReferTelegram.setChatId(chatId);
                            driveShareReferTelegram.setShareId(driveShare.getId());
                            driveShareReferTelegram.setMessageId(message.id);
                            driveShareReferTelegram.setMessageDate(message.date);
                            driveShareReferTelegram.setDriveType(1);
                            driveShareReferTelegram.setShareUrl(url);
                            driveShareReferTelegramMapper.insert(driveShareReferTelegram);
                        }

                        log.info("chat id: {}, message id: {}, url: {}", chatId, message.id, url);
                    }
                }
            }
        }
    }

    private static String getMessageText(TdApi.Message message) {
        String text = null;
        MessageContent content = message.content;
        if (content instanceof TdApi.MessageText) {
            text = ((TdApi.MessageText) content).text.text;
        } else if (content instanceof TdApi.MessagePhoto) {
            text = ((TdApi.MessagePhoto) content).caption.text;
        } else if (content instanceof TdApi.MessageDocument) {
            text = ((TdApi.MessageDocument) content).caption.text;
        } else if (content instanceof  TdApi.MessageAudio) {
            text = ((TdApi.MessageAudio) content).caption.text;
        } else if (content instanceof  TdApi.MessageVideo) {
            text = ((TdApi.MessageVideo) content).caption.text;
        }
        return text;
    }

    public class ExampleApp implements AutoCloseable {

        private final SimpleTelegramClient client;


        public ExampleApp(SimpleTelegramClientBuilder clientBuilder,
                          SimpleAuthenticationSupplier<?> authenticationData) {

            // Add an example update handler that prints every received message
            clientBuilder.addUpdateHandler(TdApi.UpdateNewMessage.class, this::onUpdateNewMessage);

            // Build the client
            this.client = clientBuilder.build(authenticationData);
        }

        @Override
        public void close() throws Exception {
            client.close();
        }

        public SimpleTelegramClient getClient() {
            return client;
        }

        /**
         * Print new messages received via updateNewMessage
         */
        private void onUpdateNewMessage(TdApi.UpdateNewMessage update) {
            TdApi.Message message = update.message;
            saveMessage(message.chatId, message);
        }

    }
}