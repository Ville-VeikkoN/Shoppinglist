package fi.tamk.tiko.shoppinglist;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.*;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.users.FullAccount;
import com.dropbox.core.v2.files.UploadErrorException;
import com.dropbox.core.v2.files.WriteMode;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;

/**
 * Uploads the given file to dropbox.
 *
 * @author Ville-Veikko Nieminen
 * @version 1.8
 * @since 2018-11-20
 */
public class DropboxUploader {
    File jsonFile;

    /**
     * Constructs DropboxUploader.
     *
     * @param jsonFile to upload to Dropbox.
     */
    public DropboxUploader(File jsonFile) {
        this.jsonFile = jsonFile;
    }

    /**
     * Saves JsonFile to Dropbox. Uses access token for now.
     *
     * @throws DbxException The base exception thrown by Dropbox API calls.
     * @throws IOException Signals that an I/O exception of some sort has occurred.
     */
    public void saveToDropbox() throws DbxException, IOException {
        final String ACCESS_TOKEN = "8OTd-qQEBDsAAAAAAAADxIM49ruAOT76kDtKVRPKdP80oAlnlT7ePg66mX2IDkqi";
        DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/java-tutorial").build();
        DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);
        FullAccount account = client.users().getCurrentAccount();
        ListFolderResult result = client.files().listFolder("");
        while (true) {
            for (Metadata metadata : result.getEntries()) {
                System.out.println(metadata.getPathLower());
            }
            if (!result.getHasMore()) {
                break;
            }
            result = client.files().listFolderContinue(result.getCursor());
        }
        try (InputStream in = new FileInputStream("groceries.json")) {
            FileMetadata metadata = client.files().uploadBuilder("/groceries.json").withMode(WriteMode.OVERWRITE)
                .uploadAndFinish(in);
        } catch(Exception ex) {
            System.out.println(ex.getMessage());
        }
        
    }


}