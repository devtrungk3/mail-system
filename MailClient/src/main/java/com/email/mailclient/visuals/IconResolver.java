package com.email.mailclient.visuals;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Arrays;

public class IconResolver {
    private static final String[] AUDIO_EXTENSIONS = { ".mp3", ".wav", ".flac", ".aac", ".ogg", ".m4a", ".aiff", ".wma", ".amr", ".opus" };
    private static final String[] ARCHIVE_EXTENSIONS = { ".zip", ".rar", ".7z", ".tar", ".gz", ".bz2", ".xz", ".z", ".cab", ".iso" };
    private static final String[] IMAGE_EXTENSIONS = { ".jpg", ".jpeg", ".png", ".gif", ".bmp", ".tiff", ".svg", ".ico", ".webp", ".exif" };
    private static final String[] TEXT_EXTENSIONS = { ".txt", ".doc", ".docx", ".rtf", ".pdf", ".html", ".xml", ".csv", ".json", ".md" };
    private static final String[] VIDEO_EXTENSIONS = { ".mp4", ".avi", ".mkv", ".mov", ".wmv", ".flv", ".m4v", ".3gp", ".webm", ".mpeg" };


    public Node getFolderIcon(String folderName){
        String lowerCaseFolderName = folderName.toLowerCase();
        ImageView imageView;
        try {
            if(lowerCaseFolderName.contains("@")){
                imageView = new ImageView(new Image(getClass().getResourceAsStream("/com/email/mailclient/icons/email.png")));
            } else if (lowerCaseFolderName.contains("inbox")){
                imageView = new ImageView(new Image(getClass().getResourceAsStream("/com/email/mailclient/icons/inbox.png")));
            } else if (lowerCaseFolderName.contains("sent")){
                imageView = new ImageView(new Image(getClass().getResourceAsStream("/com/email/mailclient/icons/sent2.png")));
            } else if (lowerCaseFolderName.contains("spam")){
                imageView = new ImageView(new Image(getClass().getResourceAsStream("/com/email/mailclient/icons/spam.png")));
            } else if (lowerCaseFolderName.contains("starred")){
                imageView = new ImageView(new Image(getClass().getResourceAsStream("/com/email/mailclient/icons/starred.png")));
            } else if (lowerCaseFolderName.contains("trash")){
                imageView = new ImageView(new Image(getClass().getResourceAsStream("/com/email/mailclient/icons/trash.png")));
            } else {
                imageView = new ImageView(new Image(getClass().getResourceAsStream("/com/email/mailclient/icons/folder.png")));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        imageView.setFitWidth(16);
        imageView.setFitHeight(16);
        return imageView;
    }
    public Node getAttachmentIcon(String attachmentName){
        String lowerCaseAttachmentName = attachmentName.toLowerCase();
        ImageView imageView;
        try {
            if(containsExtension(AUDIO_EXTENSIONS, lowerCaseAttachmentName)){
                imageView = new ImageView(new Image(getClass().getResourceAsStream("/com/email/mailclient/icons/audio-file.png")));
            } else if (containsExtension(ARCHIVE_EXTENSIONS, lowerCaseAttachmentName)){
                imageView = new ImageView(new Image(getClass().getResourceAsStream("/com/email/mailclient/icons/archive.png")));
            } else if (lowerCaseAttachmentName.contains("gif")){
                imageView = new ImageView(new Image(getClass().getResourceAsStream("/com/email/mailclient/icons/gif.png")));
            } else if (containsExtension(IMAGE_EXTENSIONS, lowerCaseAttachmentName)){
                imageView = new ImageView(new Image(getClass().getResourceAsStream("/com/email/mailclient/icons/image.png")));
            } else if (containsExtension(TEXT_EXTENSIONS, lowerCaseAttachmentName)){
                imageView = new ImageView(new Image(getClass().getResourceAsStream("/com/email/mailclient/icons/text-file.png")));
            } else if (containsExtension(VIDEO_EXTENSIONS, lowerCaseAttachmentName)){
                imageView = new ImageView(new Image(getClass().getResourceAsStream("/com/email/mailclient/icons/video.png")));
            } else {
                imageView = new ImageView(new Image(getClass().getResourceAsStream("/com/email/mailclient/icons/file.png")));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        imageView.setFitWidth(24);
        imageView.setFitHeight(24);
        return imageView;
    }
    private boolean containsExtension(String[] extensions, String lowerCaseAttachmentName){
        return Arrays.stream(extensions)
                .anyMatch(lowerCaseAttachmentName::contains);
    }
}
