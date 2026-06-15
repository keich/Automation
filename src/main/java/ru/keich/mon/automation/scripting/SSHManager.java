package ru.keich.mon.automation.scripting;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/*
 * Copyright 2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class SSHManager {

	public static class SSHSession {
		private Session session;

		public final String RESULT_STREAM_OUT = "out";
		public final String RESULT_EXITSTATUS = "exitstatus";
		public final String RESULT_ERROR = "error";

		public SSHSession(Session session) {
			this.session = session;
		}

		public void disconnect() {
			if (session != null) {
				session.disconnect();
			}
		}

		public Map<String, Object> exec(String cmd) {
			ChannelExec channel = null;
			var out = new HashMap<String, Object>();
			int exitStatus = 0;
			var text = new StringBuffer();
			try {
				channel = (ChannelExec) session.openChannel("exec");
				channel.setCommand(cmd);
				InputStream in = channel.getInputStream();
				channel.connect();
				byte[] tmp = new byte[1024];
				while (true) {
					while (in.available() > 0) {
						int len = in.read(tmp, 0, 1024);
						if (len < 0) break;
						text.append(new String(tmp, 0, len));
					}
					if (channel.isClosed()) {
						if (in.available() > 0) continue;
						exitStatus = channel.getExitStatus();
						break;
					}
					Thread.sleep(100);
				}
			} catch (Exception e) {
				out.put(RESULT_ERROR, e.getMessage());
			} finally {
				if (channel != null) {
					channel.disconnect();
				}
			}
			out.put(RESULT_STREAM_OUT, text.toString());
			out.put(RESULT_EXITSTATUS, exitStatus);
			return out;
		}
	}

	public SSHSession connect(String username, String password, String host, int port) throws JSchException {
		var session = new JSch().getSession(username, host, port);
		session.setPassword(password.getBytes());
		session.setConfig("StrictHostKeyChecking", "no");
		session.connect();
		return new SSHSession(session);
	}

}
