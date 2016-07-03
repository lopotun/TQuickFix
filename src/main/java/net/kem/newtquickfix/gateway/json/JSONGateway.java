package net.kem.newtquickfix.gateway.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.istack.internal.NotNull;
import net.kem.newtquickfix.LiteFixMessageParser;
import net.kem.newtquickfix.LoggerUtil;
import net.kem.newtquickfix.blocks.QFMessage;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by Evgeny Kurtser on 29-Jun-16 at 10:21 AM.
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 */
public class JSONGateway {
	private static final JSONGateway INSTANCE = new JSONGateway();

	private LiteFixMessageParser parser;
	private Gson gson;
	private boolean initialized;

	private JSONGateway() {
		try {
			parser = LiteFixMessageParser.create();

			GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder
//					.registerTypeAdapter(Multimap.class, MultimapSerializer.INSTANCE)
					.registerTypeAdapterFactory(new MultimapTypeAdapterFactory())
					.setPrettyPrinting();
			gson = gsonBuilder.create();// new Gson();

			initialized = true;
		} catch (NoSuchMethodException | NoSuchFieldException | IOException | InvocationTargetException | IllegalAccessException e) {
			initialized = false;
			LoggerUtil.getLogger().severe("Could not initialize LiteFix Message Parser due to " + e.getMessage());
		}
	}

	public static JSONGateway getInstance() {
		return INSTANCE;
	}

	public String parse(@NotNull CharSequence src) {
		return parse(src, JSONQFComponentValidator.class.getName());
	}

	public String parse(@NotNull CharSequence src, @NotNull CharSequence componentValidatorClassName) {
		String res;
		if(initialized) {
			try {
				final JSONQFComponentValidator componentValidator = createComponentValidator(componentValidatorClassName);
				final QFMessage message = parser.parse(src, componentValidator);
				res = JSONResponceHolder.toJSON(message, componentValidator, gson);
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | UnsupportedOperationException e) {
				res = JSONResponceHolder.toJSON(e, gson);
			}
		} else {
			res = JSONResponceHolder.toJSON(new Exception("Not initialized. See startup log for details."), gson);
		}
		return res;
	}

	private JSONQFComponentValidator createComponentValidator(@NotNull CharSequence componentValidatorClassName) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
		return (JSONQFComponentValidator) Class.forName(componentValidatorClassName.toString()).newInstance();
	}
}