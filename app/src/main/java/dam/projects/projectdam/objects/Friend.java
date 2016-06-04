package dam.projects.projectdam.objects;

import android.graphics.Bitmap;

/**
 * Created by Renato on 04/05/2016 : 00:38.
 */
public class Friend {
    private int f_id;
    private String name, number, mail, photoURL;
    private boolean invite;
    private Bitmap photo;

    public Friend(String name, String number, String mail, String photoURL, Bitmap photo, boolean isInvite) {
        this.photo = photo;
        this.name = name;
        this.number = number;
        this.mail = mail;
        this.photoURL = photoURL;
        this.invite = isInvite;
    }

    public Friend(int id, String name, String number, String mail, String photoURL, Bitmap photo, boolean isInvite) {
        this.f_id = id;
        this.photo = photo;
        this.name = name;
        this.number = number;
        this.mail = mail;
        this.photoURL = photoURL;
        this.invite = isInvite;
    }

    public int getF_id() {
        return f_id;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public String getMail() {
        return mail;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public boolean isInvite() {
        return invite;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    @Override
    public String toString() {
        return "Friend{" +
                "f_id=" + f_id +
                ", name='" + name + '\'' +
                ", number='" + number + '\'' +
                ", mail='" + mail + '\'' +
                ", photoURL='" + photoURL + '\'' +
                ", invite=" + invite +
                ", photo=" + photo +
                '}';
    }
}
