/*
 * SSHManager
 * 
 * @author Tim Vinkemeier
 */
package de.timvinkemeier.haptics.ssh;

import java.io.InputStream;

import javax.activity.InvalidActivityException;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

// TODO: Auto-generated Javadoc
/**
 * The Class SSHManager.
 */
public class SSHManager {

	/** The _jsch. */
	private JSch _jsch;

	/** The _user name. */
	private String _userName;

	/** The _connection ip. */
	private String _connectionIP;

	/** The _port. */
	private int _port;

	/** The _password. */
	private String _password;

	/** The _session. */
	private Session _session;

	/** The _timeout. */
	private int _timeout = 1000;

	/** The exec channel. */
	private ChannelExec _execChannel = null;

	/**
	 * Initializes common data.
	 * 
	 * @param userName
	 *            the user name
	 * @param password
	 *            the password
	 * @param connectionIP
	 *            the connection ip
	 * @param knownHostsFileName
	 *            the known hosts file name
	 */
	private void initialize(String userName, String password, String connectionIP, String knownHostsFileName) {
		_jsch = new JSch();

		if (knownHostsFileName != null && !knownHostsFileName.isEmpty()) {
			try {
				_jsch.setKnownHosts(knownHostsFileName);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		_userName = userName;
		_password = password;
		_connectionIP = connectionIP;
	}

	/**
	 * Instantiates a new SSH manager.
	 * 
	 * @param userName
	 *            the user name
	 * @param password
	 *            the password
	 * @param connectionIP
	 *            the connection ip
	 * @param knownHostsFileName
	 *            the known hosts file name
	 */
	public SSHManager(String userName, String password, String connectionIP, String knownHostsFileName) {
		initialize(userName, password, connectionIP, knownHostsFileName);
		_port = 22;
		_timeout = 60000;
	}

	/**
	 * Instantiates a new sSH manager.
	 * 
	 * @param userName
	 *            the user name
	 * @param password
	 *            the password
	 * @param connectionIP
	 *            the connection ip
	 * @param knownHostsFileName
	 *            the known hosts file name
	 * @param connectionPort
	 *            the connection port
	 */
	public SSHManager(String userName, String password, String connectionIP, String knownHostsFileName, int connectionPort) {
		initialize(userName, password, connectionIP, knownHostsFileName);
		_port = connectionPort;
		_timeout = 60000;
	}

	/**
	 * Instantiates a new sSH manager.
	 * 
	 * @param userName
	 *            the user name
	 * @param password
	 *            the password
	 * @param connectionIP
	 *            the connection ip
	 * @param knownHostsFileName
	 *            the known hosts file name
	 * @param connectionPort
	 *            the connection port
	 * @param timeOutMilliseconds
	 *            the time out milliseconds
	 */
	public SSHManager(String userName, String password, String connectionIP, String knownHostsFileName, int connectionPort, int timeOutMilliseconds) {
		initialize(userName, password, connectionIP, knownHostsFileName);
		_port = connectionPort;
		_timeout = timeOutMilliseconds;
	}

	/**
	 * Connect.
	 * 
	 * @return the string
	 */
	public String connect() {
		return connect(true);
	}

	/**
	 * Connect.
	 * 
	 * @param skipHostKeyCheck
	 *            the skip host key check
	 * @return the string
	 */
	public String connect(boolean skipHostKeyCheck) {
		String errorMessage = null;

		try {
			_session = _jsch.getSession(_userName, _connectionIP, _port);
			_session.setPassword(_password);
			if (skipHostKeyCheck) {
				_session.setConfig("StrictHostKeyChecking", "no");
			}
			_session.connect(_timeout);
		} catch (JSchException jschX) {
			errorMessage = jschX.getMessage();
		}

		return errorMessage;
	}

	/**
	 * Send command.
	 *
	 * @param commands the commands
	 * @param envvars the envvars
	 * @return the string
	 * @throws Exception the exception
	 */
	public String sendCommands(String[] commands, String[] envvars) throws Exception {
		if (_session == null || !_session.isConnected()) {
			throw new InvalidActivityException("SSHManager.sendCommand(String) can only be used if a session is connected!");
		}
		StringBuilder outputBuffer = new StringBuilder();
		StringBuilder errorBuffer = new StringBuilder();
		String env = "";
		String output = "";

		try {
			// combine all commands into one
			String command = "";
			for (String s : commands) {
				command += s + ";";
			}
			_execChannel = (ChannelExec) _session.openChannel("exec");

			// combine environment variable declarations
			for (String s : envvars) {
				try {
					env += s + ";";
				} catch (Exception e) {}
			}

			_execChannel.setCommand(env + command);
			_execChannel.connect();
			InputStream commandOutput = _execChannel.getInputStream();
			InputStream errorOutput = _execChannel.getErrStream();
			int readByte = commandOutput.read();

			while (readByte != 0xffffffff) {
				outputBuffer.append((char) readByte);
				readByte = commandOutput.read();
			}

			readByte = errorOutput.read();
			while (readByte != 0xffffffff) {
				errorBuffer.append((char) readByte);
				readByte = errorOutput.read();
			}

			_execChannel.disconnect();
			if (_execChannel.getExitStatus() != 0) {
				output = "<Command resulted in exitcode " + _execChannel.getExitStatus() + ">\n" + errorBuffer.toString();
			} else {
				output = outputBuffer.toString();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return output;
	}

	/**
	 * Close.
	 */
	public void close() {
		_session.disconnect();
	}

}
