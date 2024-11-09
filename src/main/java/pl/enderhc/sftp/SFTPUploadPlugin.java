/*
 * This file is part of "GradleSFTP", licensed under MIT License.
 *
 *  Copyright (c) 2024 EnderHC-PL
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package pl.enderhc.sftp;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import java.io.File;
import java.io.FileInputStream;
import org.gradle.api.DefaultTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.logging.LogLevel;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

@SuppressWarnings("unused")
public class SFTPUploadPlugin implements Plugin<Project> {

    @Override
    public void apply(final Project target) {
        target.getTasks().register("sftpUpload", SFTPUploadTask.class, (task) -> {
            task.getHost().set("your-host.com");
            task.getPort().set(22);
            task.getUsername().set("your-username");
            task.getPassword().set("your-password");
            task.getLocalFile().set(target.file("build/libs/your-library.jar"));
            task.getRemotePath().set("/path/to/remote/your-library.jar");
        });
    }

    public abstract static class SFTPUploadTask extends DefaultTask {

        @Input
        public abstract Property<String> getHost();

        @Input
        public abstract Property<Integer> getPort();

        @Input
        public abstract Property<String> getUsername();

        @Input
        public abstract Property<String> getPassword();

        @Input
        public abstract Property<File> getLocalFile();

        @Input
        public abstract Property<String> getRemotePath();

        @TaskAction
        public void uploadFile() {
            final String host = this.getHost().get();
            final int port = this.getPort().get();
            final String user = this.getUsername().get();
            final String password = this.getPassword().get();
            final File localFile = this.getLocalFile().get();
            final String remotePath = this.getRemotePath().get();

            Session session = null;
            ChannelSftp channelSftp = null;
            try {
                final JSch jSch = new JSch();
                session = jSch.getSession(user, host, port);
                session.setPassword(password);
                session.setConfig("StrictHostKeyChecking", "no");
                session.connect();

                channelSftp = (ChannelSftp) session.openChannel("sftp");
                channelSftp.connect();

                try (final FileInputStream fileInputStream = new FileInputStream(localFile)) {
                    channelSftp.put(fileInputStream, remotePath);
                    this.getLogger().log(LogLevel.INFO, "File uploaded successfully: {0}", localFile.getAbsolutePath());
                }
            } catch (final Exception exception) {
                this.getLogger().log(LogLevel.ERROR, "error while uploading file to SFTP", exception);
            } finally {
                if (channelSftp != null && channelSftp.isConnected()) {
                    channelSftp.disconnect();
                }
                if (session != null && session.isConnected()) {
                    session.disconnect();
                }
            }
        }
    }
}