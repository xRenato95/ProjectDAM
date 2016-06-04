package dam.projects.projectdam.objects;

/**
 * Created by Renato on 01/06/2016 : 00:32.
 */
public class Member {
    private int me_id, m_id, event_id, rating;
    private Student student;
    private boolean favorite;

    public Member(int me_id, int m_id, int event_id, int rating, Student student, boolean favorite) {
        this.me_id = me_id;
        this.m_id = m_id;
        this.event_id = event_id;
        this.rating = rating;
        this.student = student;
        this.favorite = favorite;
    }

    public Member(int m_id, int event_id, int rating, Student student, boolean favorite) {
        this.m_id = m_id;
        this.event_id = event_id;
        this.rating = rating;
        this.student = student;
        this.favorite = favorite;
    }

    public int getMe_id() {
        return me_id;
    }

    public int getM_id() {
        return m_id;
    }

    public int getEvent_id() {
        return event_id;
    }

    public int getRating() {
        return rating;
    }

    public Student getStudent() {
        return student;
    }

    public boolean isFavorite() {
        return favorite;
    }

    @Override
    public String toString() {
        return "Member{" +
                "me_id=" + me_id +
                ", m_id=" + m_id +
                ", event_id=" + event_id +
                ", rating=" + rating +
                ", student=" + student +
                ", favorite=" + favorite +
                '}';
    }
}
