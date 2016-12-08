package magia.af.ezpay.interfaces;

import magia.af.ezpay.Parser.LogItem;

/**
 * Created by pc on 11/30/2016.
 */

public interface MessageHandler {
  void handleMessage(LogItem logItem , boolean deleteState, String chatMemberMobile);
}
