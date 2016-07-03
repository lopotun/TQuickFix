package net.kem.newtquickfix.gateway.json;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.sun.istack.internal.NotNull;
import net.kem.newtquickfix.blocks.QFMessage;

/**
 * Created by Evgeny Kurtser on 30-Jun-16 at 4:18 PM.
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 */
public class JSONResponceHolder<M extends QFMessage> {
	private static final JsonParser JSON_PARSER = new JsonParser();

	private enum PayloadType{MESSAGE, ERROR}

	private PayloadType payloadType;
	private String className;
	private M message;
	private JSONQFComponentValidator componentValidator;
	private Exception exception;

	private JSONResponceHolder(M message, JSONQFComponentValidator componentValidator) {
		this(message.getClass().asSubclass(QFMessage.class).getName(), message, componentValidator);
	}

	private JSONResponceHolder(String className, M message, JSONQFComponentValidator componentValidator) {
		this.payloadType = PayloadType.MESSAGE;
		this.className = className;
		this.message = message;
		this.componentValidator = componentValidator;
	}

	private JSONResponceHolder(Exception exception) {
		this.payloadType = PayloadType.ERROR;
		this.className = exception.getClass().asSubclass(Exception.class).getName();
		this.exception = exception;
	}

	private JSONResponceHolder(String className, Exception exception) {
		this.payloadType = PayloadType.ERROR;
		this.className = className;
		this.exception = exception;
	}

	public String getClassName() {
		return className;
	}

	public boolean isMessage() {
		return payloadType == PayloadType.MESSAGE;
	}

	public boolean isError() {
		return payloadType == PayloadType.ERROR;
	}



	public M getMessage() {
		return message;
	}

	public JSONQFComponentValidator getComponentValidator() {
		return componentValidator;
	}

	public Exception getException() {
		return exception;
	}



	public static <M extends QFMessage> JSONResponceHolder<M> fromJSON(@NotNull String jsonString, @NotNull Gson gson) throws ClassNotFoundException {
		JsonObject jsonObject = JSON_PARSER.parse(jsonString).getAsJsonObject();

		final JsonPrimitive jPayloadType = jsonObject.getAsJsonPrimitive("payloadType");
		if(jPayloadType != null) {
			final JsonPrimitive jClassName = jsonObject.getAsJsonPrimitive("className");
			final String className = gson.fromJson(jClassName, String.class);
			final PayloadType payloadType = PayloadType.valueOf(jPayloadType.getAsString());
			switch (payloadType) {
				case MESSAGE: {
					final JsonObject jMessage = jsonObject.getAsJsonObject("message");
					final Class<? extends QFMessage> messageClass = Class.forName(className).asSubclass(QFMessage.class);
					final QFMessage msg = gson.fromJson(jMessage, messageClass);

					final JsonObject jComponentValidator = jsonObject.getAsJsonObject("componentValidator");
					JSONQFComponentValidator componentValidator = gson.fromJson(jComponentValidator, JSONQFComponentValidator.class);
					return new JSONResponceHolder(className, msg, componentValidator);
				}
				case ERROR: {
					final JsonObject jException = jsonObject.getAsJsonObject("exception");
					final Class<? extends Exception> exceptionClass = Class.forName(className).asSubclass(Exception.class);
					final Exception exception = gson.fromJson(jException, exceptionClass);
					return new JSONResponceHolder(className, exception);
				}
			}
			throw new JsonParseException("Unrecognizable payload type " + payloadType + " in JSON " + jsonObject.toString());
		}
		throw new JsonParseException("Unrecognizable JSON " + jsonObject.toString());
	}



	public static <M extends QFMessage> String toJSON(@NotNull JSONResponceHolder<M> responceHolder, @NotNull Gson gson) {
		return gson.toJson(responceHolder);
	}

	public static <M extends QFMessage> String toJSON(@NotNull final M message, final JSONQFComponentValidator componentValidator, @NotNull Gson gson) {
		return gson.toJson(new JSONResponceHolder(message, componentValidator));
	}

	public static String toJSON(@NotNull final Exception exception, @NotNull Gson gson) {
		return gson.toJson(new JSONResponceHolder(exception));
	}
}
