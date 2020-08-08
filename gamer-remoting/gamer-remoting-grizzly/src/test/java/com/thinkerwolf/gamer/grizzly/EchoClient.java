package com.thinkerwolf.gamer.grizzly;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.logging.Logger;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.glassfish.grizzly.Connection;
import org.glassfish.grizzly.Grizzly;
import org.glassfish.grizzly.filterchain.FilterChainBuilder;
import org.glassfish.grizzly.filterchain.TransportFilter;
import org.glassfish.grizzly.nio.transport.TCPNIOTransport;
import org.glassfish.grizzly.nio.transport.TCPNIOTransportBuilder;
import org.glassfish.grizzly.utils.StringFilter;

/**
 * The simple client, which sends a message to the echo server and waits for
 * response
 */
public class EchoClient {
	private static final Logger logger = Grizzly.logger(EchoClient.class);

	public static void main(String[] args)
			throws IOException, ExecutionException, InterruptedException, TimeoutException {

		Connection connection = null;

		// Create a FilterChain using FilterChainBuilder
		FilterChainBuilder filterChainBuilder = FilterChainBuilder.stateless();
		// Add TransportFilter, which is responsible
		// for reading and writing data to the connection
		filterChainBuilder.add(new TransportFilter());
		// StringFilter is responsible for Buffer <-> String conversion
		filterChainBuilder.add(new StringFilter(Charset.forName("UTF-8")));
		// ClientFilter is responsible for redirecting server responses to the
		// standard output
		filterChainBuilder.add(new ClientFilter());

		// Create TCP transport
		final TCPNIOTransport transport = TCPNIOTransportBuilder.newInstance().build();
		transport.setProcessor(filterChainBuilder.build());

		try {
			// start the transport
			transport.start();

			// perform async. connect to the server
			Future<Connection> future = transport.connect(EchoServer.HOST, EchoServer.PORT);
			// wait for connect operation to complete
			connection = future.get(10, TimeUnit.SECONDS);

			assert connection != null;

			System.out.println("Ready... (\"q\" to exit)");
			final BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
			do {
				final String userInput = inReader.readLine();
				if (userInput == null || "q".equals(userInput)) {
					break;
				}

				connection.write(userInput);
			} while (true);
		} finally {
			// close the client connection
			if (connection != null) {
				connection.close();
			}

			// stop the transport
			transport.shutdownNow();
		}
	}
}
