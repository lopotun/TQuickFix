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
import net.kem.newtquickfix.v50sp2.components.Instrument;
import net.kem.newtquickfix.v50sp2.fields.MaturityDate;
import net.kem.newtquickfix.v50sp2.fields.SecurityID;
import net.kem.newtquickfix.v50sp2.fields.SecuritySubType;
import net.kem.newtquickfix.v50sp2.fields.SenderCompID;
import net.kem.newtquickfix.v50sp2.fields.Side;
import net.kem.newtquickfix.v50sp2.fields.Symbol;
import net.kem.newtquickfix.v50sp2.fields.TargetCompID;
import net.kem.newtquickfix.v50sp2.fields.TradeDate;
import net.kem.newtquickfix.v50sp2.messages.AllocationInstruction;
import net.kem.newtquickfix.v50sp2.messages.TradeCaptureReport;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.regex.Pattern;

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
		theRabbit.init();
		theRabbit.testParseMessages();
//		theRabbit.testJSONParseMessages();
//        theRabbit.testParseMessage(src);
//        theRabbit.testCreateMessage();
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
				src = src.trim();
				if(src.length() == 0 || src.startsWith("#")) {
					continue;
				}
				QFMessage msg = messageParser.parse(src);
				msg.toFIXString(sb);
				count++;
//				System.out.println(String.valueOf(count) + '\t' + sb.toString());
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

		// Configure JSON parser.
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapterFactory(new MultimapTypeAdapterFactory());
		Gson gson = gsonBuilder.create();

		// Take the reference to JSONGateway.
		JSONGateway gw = JSONGateway.getInstance();

		int count = 0;
		String src;
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(new FileReader("FIXMessages.txt"));//export.txt
		while ((src = br.readLine()) != null) {
			if(src.length() == 0) {
				continue;
			}
			sb.setLength(0);
			try {
				// Pass to JSONGateway the FIX message along with class name of QFComponentValidator
				// that should be used by the JSONGateway to trace after parsing progress (this class must be present on the JSONGateway side).
				// Get JSON response.
				String jsonResponse = gw.parse(src, JSONQFComponentValidator.class.getName());

				// Parse JSON response.
				JSONResponceHolder responceHolder = JSONResponceHolder.fromJSON(jsonResponse, gson);
				// The response can contain either parsed Message (along with populated QFComponentValidator) or error.
				if(responceHolder.isMessage()) {
					final JSONQFComponentValidator componentValidator = responceHolder.getComponentValidator();
					if(componentValidator.hasFailure()) {
						System.out.println(componentValidator.getFailures().entrySet().stream().count() + " failures");
					}

					final QFMessage message = responceHolder.getMessage();
					// Show JSON response.
					message.toFIXString(sb);
					System.out.println(String.valueOf(count++) + '\t' + sb.toString() + "\n");

					// Validation
					componentValidator.hasFailure(JSONQFComponentValidator.Failures.INVALID_FIELD_VALUE);
					componentValidator.getFailure(JSONQFComponentValidator.Failures.MANDATORY_ELEMENT_MISSING);
					//message.validate(componentValidator);
				} else {
					System.err.println("Parsing severe error: " + responceHolder.getException());
				}
			} catch (Exception e) {
				System.err.println("ERROR IN LINE " + count);
				e.printStackTrace();
				throw e;
			}
		}
		br.close();
	}

	private void testCreateMessage() throws IOException, ClassNotFoundException {
		// Get reference to Component Validator.
		final QFComponentValidator componentValidator = LiteFixMessageParser.getComponentValidator();
		// The same can be done with this code:
//		final QFComponentValidator componentValidator = new DefaultQFComponentValidator();
//		 LiteFixMessageParser.setComponentValidator(componentValidator);

		// Create message.
		AllocationInstruction msgJ = AllocationInstruction.of();
		// Fill it with some fields.
		msgJ.setSide(Side.BUY);
		msgJ.setTradeDate(TradeDate.of()); // This will set current date.
		msgJ.setTradeDate(TradeDate.of(LocalDate.of(2015, 11, 23))); // Alternatively, the date can be set in this way.
		msgJ.setTradeDate(TradeDate.of("20151123")); // Or even in this way in "yyyyMMdd" format.

		// Define and populate some Message Component (for example, "Instrument").
		final Instrument instrument = Instrument.of();
		instrument.setSymbol(Symbol.of("ACME"));
		instrument.setSecurityID(SecurityID.of("1234"));
		instrument.setSecuritySubType(SecuritySubType.of("1B2D"));
		instrument.setMaturityDate(MaturityDate.of());
		// Attach it to the Massage.
		msgJ.setInstrument(instrument);

		// Now, let's validate our Message.
		System.out.println("Our " + msgJ.getMessageType().getValue() + " message is " + (msgJ.validate() ? "valid :-)": "invalid :-("));
		// Well, if you want to get more details about validation failures, you can either
		// implement more sophisticated QFComponentValidator or just take a look at log :-)

		// OK. I want to see the message. NOW!!
		// Here it comes...
		CharSequence asFIX = msgJ.seal();
		System.out.println(asFIX);


		// Let's create another Message.
		TradeCaptureReport msgAE = TradeCaptureReport.of();

		// Let's assume that we want to have in this Message the same Instrument as previous message has.
		// That's simple!
		msgAE.setInstrument(msgJ.getInstrument());
		// Or, even more simple!
		// The LiteFix is aware that 'msgJ' contains the Instrument instance and it is smart enough to extract this instance automatically.
		msgAE.setInstrument(msgJ);
		// (how would you implement it with QuickFix?..)

		System.out.println(msgAE.seal(SenderCompID.of("The Sender"), TargetCompID.of("The Target")));
	}
}