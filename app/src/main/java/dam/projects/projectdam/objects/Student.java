package dam.projects.projectdam.objects;

/**
 * Created by Renato on 24/04/2016 : 17:19.
 * Class that represents a student in the context of the problem.
 */
public class Student {
    private int db_id;
    private String id,
                   name,
                   mail,
                   photo;
    private String token;

    public Student(int db_id, String id, String name, String mail, String photo) {
        this.db_id = db_id;
        this.id = id;
        this.name = name;
        this.mail = mail;
        this.photo = photo;
    }

    public Student(int db_id, String id, String name, String mail, String photo, String token) {
        this.id = id;
        this.name = name;
        this.mail = mail;
        this.photo = photo;
        this.db_id = db_id;
        this.token = token;
    }

    public int getDb_id() {
        return db_id;
    }

    public String getPhoto() {
        return photo;
    }

    public String getMail() {
        return mail;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    @Override
    public String toString() {
        return "Student{" +
                "db_id=" + db_id +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", mail='" + mail + '\'' +
                ", photo='" + photo + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
