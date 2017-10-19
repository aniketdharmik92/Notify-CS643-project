package notify;

import static spark.Spark.post;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.http.HttpServlet;

import com.twilio.twiml.Body;
import com.twilio.twiml.Message;
import com.twilio.twiml.MessagingResponse;

public class ReceiveSms extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {

		post("/receive-sms", (req, res) -> {

			String toMsg = "";

			BufferedWriter bw = null;
			FileWriter fw = null;

			Properties prop = null;
			InputStream input = null;

			Properties prop1 = null;
			InputStream input1 = null;

			try {
				
				prop = new Properties();
				input = new FileInputStream("C:\\Users\\Aniket\\workspace\\notify\\Locate.properties");

				prop1 = new Properties();
				input1 = new FileInputStream("C:\\Users\\Aniket\\workspace\\notify\\TimeTable.properties");

				// load a properties file
				prop.load(input);
				prop1.load(input1);

				String body = req.queryParams("Body");
				String from = req.queryParams("From");

				System.out.println("\nFrom: " + from + "\nMessage: " + body);
				if (body.toLowerCase().startsWith("locate")) {
					System.out.println("In Locate match");

					if (prop.getProperty(body.substring(body.indexOf(" ") + 1, body.indexOf(" ") + 6)) == null) {
						System.out.println("Locate no classes");
						toMsg = "Sorry, for this course, no classes are alotted";
					} else {
						System.out.println("Match found");
						String temp = prop.getProperty(body.substring(body.indexOf(" ") + 1, body.indexOf(" ") + 6));
						System.out.println("temp: "+temp);
						//System.out.println(prop.getProperty(body.substring(body.indexOf(" ") + 1, body.indexOf(" ") + 6)));
						toMsg = temp;
					}

				} else if (body.toLowerCase().startsWith("timetable")) {
					System.out.println("In timetable");
					if (prop1.getProperty(body.substring(body.indexOf(" ") + 1, body.indexOf(" ") + 6)) == null) {
						System.out.println("No class for timetable");
						toMsg = "Sorry, for this course, there are no time table available.";
					} else {
						System.out.println("Class found TimeTable");
						toMsg = prop1.getProperty(body.substring(body.indexOf(" ") + 1, body.indexOf(" ") + 6));
					}
				
				} else {
					toMsg = "Please use LOCATE or TIMETABLE";
				}

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (bw != null)
						bw.close();

					if (fw != null)
						fw.close();

				} catch (IOException ex) {

					ex.printStackTrace();
				}
			}

			System.out.println("After If Else");
			Message sms = new Message.Builder().body(new Body(toMsg)).build();
			MessagingResponse twiml = new MessagingResponse.Builder().message(sms).build();

			return twiml.toXml();
		});
	}
}