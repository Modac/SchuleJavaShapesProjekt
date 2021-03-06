import java.io.PrintStream;
import java.net.URL;
import java.io.File;
import java.io.InputStream;
import java.net.URLConnection;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Closeable;

public class DOWNLOADER{
	private static PrintStream o=System.out;
	private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
	private static final int EOF = -1;
	
	public static void main(String[] args){
		main("http://p-productions.myftp.org/oeffentlich/ApacheCommonsIO2.4.zip", "Z:/Java/Downloader/ApacheCom.zip");
	}
	
	
	public static void main(String url, String file){
		try{
			copyURLToFile(new URL(url),new File(file), 10000, 10000);
		}catch(Exception e){
			e.printStackTrace(o);
		}
	}
	
	public static void copyURLToFile(URL source, File destination, int connectionTimeout, int readTimeout) throws IOException {
			URLConnection connection = source.openConnection();
			connection.setConnectTimeout(connectionTimeout);
			connection.setReadTimeout(readTimeout);
			InputStream input = connection.getInputStream();
			copyInputStreamToFile(input, destination);
	}
	
	public static void copyInputStreamToFile(InputStream source, File destination) throws IOException {
		try {
			FileOutputStream output = openOutputStream(destination);
			try {
				copy(source, output);
				output.close(); // don't swallow close Exception if copy completes normally
			} finally {
				closeQuietly(output);
			}
		} finally {
			closeQuietly(source);
		}
	}
	
	public static int copy(InputStream input, OutputStream output) throws IOException {
		long count = copyLarge(input, output);
		if (count > Integer.MAX_VALUE) {
			return -1;
		}
		return (int) count;
	}
	
	public static long copyLarge(InputStream input, OutputStream output) throws IOException {
		return copyLarge(input, output, new byte[DEFAULT_BUFFER_SIZE]);
	}
	
	public static long copyLarge(InputStream input, OutputStream output, byte[] buffer) throws IOException {
		long count = 0;
		int n = 0;
		while (EOF != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}
	
	public static FileOutputStream openOutputStream(File file) throws IOException {
		return openOutputStream(file, false);
	}

	public static FileOutputStream openOutputStream(File file, boolean append) throws IOException {
		if (file.exists()) {
			if (file.isDirectory()) {
				throw new IOException("File '" + file + "' exists but is a directory");
			}
			if (file.canWrite() == false) {
				throw new IOException("File '" + file + "' cannot be written to");
			}
		} else {
			File parent = file.getParentFile();
			if (parent != null) {
				if (!parent.mkdirs() && !parent.isDirectory()) {
					throw new IOException("Directory '" + parent + "' could not be created");
				}
			}
		}
		return new FileOutputStream(file, append);
	}
	
	public static void closeQuietly(OutputStream output) {
		closeQuietly((Closeable)output);
	}

	public static void closeQuietly(InputStream input) {
		closeQuietly((Closeable)input);
	}

	public static void closeQuietly(Closeable closeable) {
		try {
			if (closeable != null) {
				closeable.close();
			}
		} catch (IOException ioe) {
			// ignore
		}
	}
	
	
}