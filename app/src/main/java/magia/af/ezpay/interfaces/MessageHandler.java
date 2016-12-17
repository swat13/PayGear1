package magia.af.ezpay.interfaces;

import magia.af.ezpay.Parser.PayLogItem;

/**
 * Created by pc on 11/30/2016.
 */

public interface MessageHandler {
    void handleMessage(PayLogItem payLogItem, boolean deleteState, String chatMemberMobile);

    void handleMessageGp(String body,String chatMemberMobile);
}

