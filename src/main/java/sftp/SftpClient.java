package sftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.commons.vfs2.provider.sftp.IdentityInfo;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;

public class SftpClient {

	static String HOST = "s-a8758ab398cb462c8.server.transfer.ap-southeast-2.amazonaws.com";
	static String USER = "javauser";
	static String PRIVATE_KEY_PATH = "C:\\Data\\lassi\\lassi_app";
	static String PRIVATE_KEY_PASSPHRASE = "skyIsBlue";

	public static void main(String args[]) {

		try {

			String connectionString = "sftp://" + USER + "@" + HOST;
			FileSystemOptions opts = createDefaultOptions(PRIVATE_KEY_PATH, PRIVATE_KEY_PASSPHRASE);
			
			String localFile = "C:/Data/lassi/cat1.jpg";
			downloadFile(localFile, "/cat.jpg", connectionString, opts);
			deleteFile("/cat1.jpg", connectionString, opts);
			System.out.println("competed operation");
			StandardFileSystemManager manager = (StandardFileSystemManager) VFS.getManager();
			manager.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static FileSystemOptions createDefaultOptions(String keyPath, String passPhrase) throws FileSystemException, FileNotFoundException, IOException {
		
		System.out.println(keyPath);
		System.out.println(passPhrase);	
		System.out.println(passPhrase.getBytes());


		IdentityInfo identites = new IdentityInfo(new File(keyPath), passPhrase.getBytes());

		FileSystemOptions opts = new FileSystemOptions();
		SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(opts, "no");
		SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(opts, false);
		SftpFileSystemConfigBuilder.getInstance().setIdentityProvider(opts, identites);

		return opts;
	}

	
	
	public static boolean downloadFile(String localFilePath, String remoteFilePath, String connectionString,
			FileSystemOptions opts) throws FileSystemException {

		try {
			StandardFileSystemManager manager = (StandardFileSystemManager) VFS.getManager();

			String completeFilePath = connectionString + remoteFilePath;
			System.out.println(completeFilePath);
			FileObject localFile = manager.resolveFile(localFilePath);
			System.out.println("localfile resoved");
			FileObject remoteFile = manager.resolveFile(completeFilePath, opts);
			System.out.println("remotefile resoved");

			localFile.copyFrom(remoteFile, Selectors.SELECT_FILES);
			System.out.println("localfile copyfrom complete");
			
			remoteFile.close();
			localFile.close();

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;

	}

	public static boolean deleteFile(String remoteFilePath, String connectionString,
			FileSystemOptions opts) throws FileSystemException{

		try {
			StandardFileSystemManager manager = (StandardFileSystemManager) VFS.getManager();

			String completeFilePath = connectionString + remoteFilePath;
			System.out.println(completeFilePath);

			FileObject remoteFile = manager.resolveFile(completeFilePath, opts);
			System.out.println("remotefile resoved");
			remoteFile.delete();


			System.out.println("remotefile delete complete");
			
			remoteFile.close();

			//manager.close();
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}

