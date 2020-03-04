package sftp;

import java.io.File;


import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;
import org.apache.commons.vfs2.provider.sftp.IdentityInfo;


    public class SftpClientV2 {

        public static void main(String[] args) {
            downloadFile();
        }


        private static boolean downloadFile(){

            String host = "s-1ef43beb34ff4c54b.server.transfer.ap-southeast-2.amazonaws.com";
            String user = "lassi_app";
            String password = "";
            String fileName = "lassi-data-store/cat.jpg";
            String localFilePath = "c:/cygwin64/home/woddle/wrote_lines.txt";

            // without passphrase
            String keyPath = "C:/Data/lassi/lassi_app";
            String passphrase = "skyIsBlue";

            // with passphrase
            // String keyPath = "c:/cygwin64/home/woddle/.ssh/id_dsa_withpass";
            // String passphrase = "super-secrets";

            StandardFileSystemManager sysManager = new StandardFileSystemManager();

            try {
                sysManager.init();

                FileObject localFile = sysManager.resolveFile(localFilePath);

                FileObject remoteFile = sysManager.resolveFile(createConnectionString(host, user, password, keyPath, passphrase, fileName), createDefaultOptions(keyPath, passphrase));

                //Selectors.SELECT_FILES --> A FileSelector that selects only the base file/folder.
                localFile.copyFrom(remoteFile, Selectors.SELECT_FILES);


            } catch (Exception e) {
                System.out.println("Downloading file failed: " + e.toString());
                return false;
            }finally{
                sysManager.close();
            }
            return true;
        }


        public static String createConnectionString(String hostName, String username, String password, String keyPath, String passphrase, String remoteFilePath) {

            if (keyPath != null) {
                return "sftp://" + username + "@" + hostName + "/" + remoteFilePath;
            } else {
                return "sftp://" + username + ":" + password + "@" + hostName + "/" + remoteFilePath;
            }
        }



        private static FileSystemOptions createDefaultOptions(final String keyPath, final String passphrase) throws FileSystemException{

            //create options for sftp
            FileSystemOptions options = new FileSystemOptions();
            //ssh key
            SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(options, "no");
            //set root directory to user home
            SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(options, true);
            //timeout
            SftpFileSystemConfigBuilder.getInstance().setTimeout(options, 10000);

            if (keyPath != null) {
                IdentityInfo identityInfo = null;
                if(passphrase!=null){
                    identityInfo = new IdentityInfo(new File(keyPath), passphrase.getBytes());
                }else{
                    identityInfo =  new IdentityInfo(new File(keyPath));
                }
          SftpFileSystemConfigBuilder.getInstance().setIdentityInfo(options, identityInfo);
            }


            return options;
        }


}