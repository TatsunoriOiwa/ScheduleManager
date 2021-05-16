package qwertzite.schedulemanager.storage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import qwertzite.schedulemanager.Log;
import qwertzite.schedulemanager.storage.ScheduleStorage.Folder;

public class SmGoogleDrive {
	
	private static final String APPLICATION_NAME = "ScheduleManager";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static final String TOKENS_DIRECTORY_PATH = "tokens";
	/**
	 * Global instance of the scopes required by this quickstart. If modifying these
	 * scopes, delete your previously saved tokens/ folder.
	 */
	private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_FILE);
	private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
	
	private static String ROOT_NAME = "schedule";
	
	private static Drive DRIVE;
	private static File ROOT;
	private static EnumMap<Folder, File> FOLDERS = new EnumMap<>(Folder.class);
	
	/**
	 * Creates an authorised Credential object.
	 * 
	 * @param HTTP_TRANSPORT The network HTTP Transport.
	 * @return An authorised Credential object.
	 * @throws IOException If the credentials.json file cannot be found.
	 */
	private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT, boolean createNewCredential) throws IOException {
		// Load client secrets.
		InputStream in = SmGoogleDrive.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
		if (in == null) {
			throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
		}
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

		// Build flow and trigger user authorisation request.
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
				clientSecrets, SCOPES)
						.setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
						.setAccessType("offline").build();
		if (!createNewCredential && flow.loadCredential("user") == null) {
			Log.info("No drive credential found.");
			return null;
		} else {
			if (!createNewCredential) Log.info("Using existng drive credential.");
			else Log.info("Creating new drive credential.");
		}
		LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
		AuthorizationCodeInstalledApp app = new AuthorizationCodeInstalledApp(flow, receiver);
		return app.authorize("user");
//		return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
	}
	
	public static boolean authoriseAndConnect(boolean createNewCredential) {
		Log.info("Authorising Google Drive connection.");
		// Build a new authorised API client service.
		NetHttpTransport HTTP_TRANSPORT;
		try {
			Log.info("Connecting to google drive...");
			HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
			Credential credential = getCredentials(HTTP_TRANSPORT, createNewCredential);
			if (credential == null) { return false; }
			Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
					.setApplicationName(APPLICATION_NAME).build();
			DRIVE = service;
//			System.out.println(DRIVE.about().get().execute().getUser().getEmailAddress());
			Log.info("Google drive authorisation completed.");
			
			return true;
		} catch (GeneralSecurityException | IOException e) {
			Log.warn("Failed to connect to google drive!", e);
			return false;
		}
	}
	
	public static boolean isAuthorised() {
		return DRIVE != null;
	}
	
	public static InputStream loadFile(String name, Folder folder) throws IOException {
		if (DRIVE == null) { return null; }
		FileList files = DRIVE.files().list().setQ(queryString(name, false, getFolder(folder))).execute();
		List<File> filelist = files.getFiles();
		switch (filelist.size()) {
		case 0: {
			Log.info("{} not found in drive.", name);
			return null;
		}
		default:
			Log.warn("Found multiple {}s in the drive.", name);
		case 1: {
			File schedulejson = filelist.get(0);
			String fileId = schedulejson.getId();
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			DRIVE.files().get(fileId).executeMediaAndDownloadTo(outputStream);
			ByteArrayInputStream bais = new ByteArrayInputStream(outputStream.toByteArray());
//			DRIVE.files().watch(schedulejson.getId(), new Channel());
//			DRIVE.files().watch(fileId, new Channel().setType(fileId))
			return bais;
		}
		}
	}
	
	public static void uploadFile(String name, Folder folder, java.io.File local) throws IOException {
		if (DRIVE == null) { return; }
		if (!local.exists()) { return; }
		FileList files = DRIVE.files().list().setQ(queryString(name, false, getFolder(folder))).execute();
		List<File> filelist = files.getFiles();
		switch (filelist.size()) {
		case 0: {
			Log.info("Creating {} in google drive.", name);
			File fileMetadata = new File();
			fileMetadata.setName(name);
			fileMetadata.setParents(Collections.singletonList(getFolder(folder).getId()));
			FileContent mediaContent = new FileContent("text/plain", local);
			DRIVE.files().create(fileMetadata, mediaContent).execute();
		} break;
		default:
			Log.warn("Found multiple {}s in the drive.", name);
		case 1: {
			File prevfile = filelist.get(0);
			File file = new File();
			// File's new content.
			FileContent mediaContent = new FileContent("text/plain", local);
			// Send the request to the API.
			DRIVE.files().update(prevfile.getId(), file, mediaContent).execute();
		} break;
		}
		
	}
	
	/**
	 * get or create folder if not existing.
	 * @param folder
	 * @return null if folder == null
	 * @throws IOException 
	 */
	private static File getFolder(Folder folder) throws IOException {
		File root = getRoot();
		if (folder == null) { return root; }
		if (root == null) { return null; }
		if (FOLDERS.containsKey(folder)) {
			return FOLDERS.get(folder);
		}
		
		FileList files = DRIVE.files().list().setQ(queryString(folder.getName(), true, root)).execute();
		List<File> filelist = files.getFiles();
		File res;
		switch (filelist.size()) {
		case 0:
			Log.info("Creating new root folder in google drive.");
			File folderMetadata = new File();
			folderMetadata.setName(folder.getName());
			folderMetadata.setMimeType("application/vnd.google-apps.folder");
			folderMetadata.setParents(Collections.singletonList(root.getId()));
			res = DRIVE.files().create(folderMetadata).execute();//.setFields("id, name, webContentLink, webViewLink").execute();
			break;
		default:
			Log.info("Found multiple root folders in google drive.");
		case 1:
			res = filelist.get(0);
			break;
		}
		FOLDERS.put(folder, res);
		return res;
	}
	
	private static File getRoot() throws IOException {
		if (ROOT == null) {
			FileList files = DRIVE.files().list().setQ(queryString(ROOT_NAME, true, null)).execute();
			List<File> filelist = files.getFiles();
			switch (filelist.size()) {
			case 0:
				Log.info("Creating new root folder in google drive.");
				File folderMetadata = new File();
				folderMetadata.setName("schedule");
				folderMetadata.setMimeType("application/vnd.google-apps.folder");
				ROOT = DRIVE.files().create(folderMetadata).execute();//.setFields("id, name, webContentLink, webViewLink").execute();
				break;
			default:
				Log.info("Found multiple root folders in google drive.");
			case 1:
				ROOT = filelist.get(0);
				break;
			}
		}
		return ROOT;
	}
	
	/**
	 * 
	 * @param name
	 * @param isFolder
	 * @param parent leave this null to obtain query string for root directory.
	 */
	private static String queryString(String name, boolean isFolder, File parent) {
		// "name = '" + ROOT_NAME + "' and mimeType='application/vnd.google-apps.folder' and trashed = false"
		// "name = '" + folder.getName() + "' and mimeType='application/vnd.google-apps.folder' and trashed = false and '" + root.getId() + "' in parents"
//		String q = String.format("name = '%s' and mimeType='test/plain' and trashed=false and '%s' in parents", name, getRoot().getId());
//		if (folder != null) { q += String.format(" and '%s' in parents", getFolder(folder).getId()); }
		return String.format("name='%s' and mimeType='%s' and trashed=false",
						name,
						isFolder ? "application/vnd.google-apps.folder" : "text/plain") + 
				(parent == null ? "" : String.format(" and '%s' in parents", parent.getId()));
		
		
	}
}
