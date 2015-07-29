package net.kem.tquickfix;

import java.lang.reflect.Method;

/**
 * Created with IntelliJ IDEA.
 * User: EvgenyK
 * Date: 12/18/14
 * Time: 9:56 AM
 * To change this template use File | Settings | File Templates.
 */
public interface MessageMapper {

    Method getMessageByType(String msgType);
}
