package net.kem.newtquickfix;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.kem.newtquickfix.blocks.QFMessage;
import net.kem.newtquickfix.blocks.QFUtils;
import net.kem.newtquickfix.builders.BuilderUtils;
import net.kem.newtquickfix.gateway.json.JSONGateway;
import net.kem.newtquickfix.gateway.json.JSONQFComponentValidator;
import net.kem.newtquickfix.gateway.json.JSONResponceHolder;
import net.kem.newtquickfix.gateway.json.MultimapTypeAdapterFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.regex.Pattern;

//import net.kem.newtquickfix.v50sp2.components.StandardHeader;
//import net.kem.newtquickfix.v50sp2.components.StandardTrailer;
//import net.kem.newtquickfix.v50sp2.fields.AllocStatus;
//import net.kem.newtquickfix.v50sp2.fields.BeginString;
//import net.kem.newtquickfix.v50sp2.fields.MsgType;
//import net.kem.newtquickfix.v50sp2.fields.SendingTime;
//import net.kem.newtquickfix.v50sp2.fields.TradeDate;
//import net.kem.newtquickfix.v50sp2.messages.AllocationReportAck;

/**
 * Created by Evgeny Kurtser on 12/22/2015 at 12:46 PM.
 * <a href=mailto:EvgenyK@traiana.com>EvgenyK@traiana.com</a>
 */
public class ParseMessages {
	private static final Pattern PATTERN = Pattern.compile("(\\d+)=(.*)");
	private static LiteFixMessageParser messageParser;

	public static void main(String[] args) throws NoSuchMethodException, NoSuchFieldException, IllegalAccessException, IOException, InvocationTargetException, ClassNotFoundException {
//        final CharSequence src = "8=FIX.5.0\u00019=0\u000135=J\u000152=20120325-07:45:05.364\u000170=1234\u000171=0\u000172=1234\u0001626=2\u0001857=1\u000154=2\u000155=\u000148=IBM.TH\u000122=5\u000153=1220\u00016=10.01\u000115=USD\u0001453=2\u0001448=default_test_client\u0001447=D\u0001452=3\u0001448=default_test_eb\u0001447=D\u0001452=1\u0001207=J_EXCHANGE\u000175=20120325\u000173=1\u000137=\u000163=4\u000164=20120325\u000178=2\u000179=default_test_giveup_account\u000180=1210\u0001467=5678\u000181=3\u0001539=1\u0001524=default_test_cb\u0001525=C\u0001538=4\u0001161=Electronic\u0001153=10.01\u0001155=1\u0001156=M\u0001120=USD\u000110251=10251.111111111\u000110252=10252.111111111\u000112=5\u000113=1\u0001479=USD\u000179=GIVEUP ACCOUNT_1\u000180=10\u0001467=1234\u000181=3\u0001539=1\u0001524=default_test_cb\u0001525=C\u0001538=4\u0001161=Electronic\u0001153=10.01\u0001155=1\u0001156=M\u0001120=USD\u000110251=10251.222222222\u000110252=10252.22222222\u000112=5\u000113=1\u0001479=USD\u000160=20120325-07:45:05.364\u000110351=10351.123456789\u000110352=10352.123456789\u000110=0\u0001";

		ParseMessages theRabbit = new ParseMessages();
//		theRabbit.init();
//		theRabbit.testParseMessages();
		theRabbit.testJSONParseMessages();
//        theRabbit.testParseMessage(src);
		theRabbit.shutdown();
	}

	private void init() throws NoSuchMethodException, NoSuchFieldException, IllegalAccessException, IOException, InvocationTargetException {
		// If BuilderUtils.forcedFixVersion is null, then version of LiteFix Message will be picked automatically according to FIX version of the incoming message.
		// Uncomment when all incoming messages processing should be narrowed to specific FIX version definition. Optional setting.
		messageParser = LiteFixMessageParser.of(new DefaultQFComponentValidator() {
			@Override
			// Force parser to use 50SP2 FIX version to parse any incoming messages regardless their real FIX version.
			public String getDefaultFIXVersion() {
				return BuilderUtils.FIXVersion.VER50SP2.getFixVersion();
			}
		});
	}

	private void shutdown() {
		QFUtils.storeMaps();
	}

	public static QFMessage parseMessage(CharSequence src, QFComponentValidator componentValidator) {
		return messageParser.parse(src, componentValidator);
	}

	private void testParseMessages() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("FIXMessages.txt"));//export.txt
		StringBuilder sb = new StringBuilder();
		int count = 0;
		String src;
		while ((src = br.readLine()) != null) {
			try {
				QFMessage msg = messageParser.parse(src);
				msg.toFIXString(sb);
				System.out.println(String.valueOf(count++) + '\t' + sb.toString());
			} catch (UnsupportedOperationException e) {
				e.printStackTrace();
			} catch (Exception e) {
				System.err.println("ERROR IN LINE " + count);
				e.printStackTrace();
				throw e;
			}
		}
		br.close();
	}

	private void testJSONParseMessages() throws IOException, ClassNotFoundException {
		BufferedReader br = new BufferedReader(new FileReader("FIXMessages.txt"));//export.txt
		StringBuilder sb = new StringBuilder();

		GsonBuilder gsonBuilder = new GsonBuilder();
//		gsonBuilder.registerTypeAdapter(Multimap.class, JSONGateway.MultimapSerializer.INSTANCE);
		gsonBuilder.registerTypeAdapterFactory(new MultimapTypeAdapterFactory());
		Gson gson = gsonBuilder.create();

		JSONGateway gw = JSONGateway.getInstance();

		int count = 0;
		String src;
		while ((src = br.readLine()) != null) {
			try {
				// Get JSON response.
				String jsonResponse = gw.parse(src, JSONQFComponentValidator.class.getName());

				// Parse JSON response.
				JSONResponceHolder responceHolder = JSONResponceHolder.fromJSON(jsonResponse, gson);
				if(responceHolder.isMessage()) {
					final JSONQFComponentValidator componentValidator = responceHolder.getComponentValidator();
					final QFMessage message = responceHolder.getMessage();

					// Show JSON response.
//					System.out.println(String.valueOf(count++) + '\t' + sb.toString() + "\n");
//					if(componentValidator.isFailed()) {
//						System.out.println(componentValidator.getFailures().entrySet().stream().count() + " failures");
//					}

					// Validation
					message.validate(componentValidator);
				} else {
					System.err.println("Parsing error: " + responceHolder.getException());
				}
			} catch (Exception e) {
				System.err.println("ERROR IN LINE " + count);
				e.printStackTrace();
				throw e;
			}
		}
		br.close();
	}
}